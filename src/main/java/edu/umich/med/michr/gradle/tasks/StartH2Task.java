/*
 * Copyright (c) 2020 The Regents of the University of Michigan - Michigan Institute for Clinical and Health Research.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.umich.med.michr.gradle.tasks;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Gradle task that starts the H2 database.
 * Using JavaExec task because we'd like to make this plugin use a h2 db version independent of other plugins depending on h2.
 * Example use case: Dependency Check Analyze plugin of OWASP is bundled with an older version of H2 and if used together with this plugin
 * without overriding the db version in buildscript block of gradle config there's no way to make use of a specific h2 version
 * for this plugin. That results in dependency check analyze use the same version for which the plugin is incompatible.
 */
public class StartH2Task extends JavaExec {
  private static final Logger LOGGER = Logging.getLogger(StartH2Task.class);

  private final PipedInputStream inputStream = new PipedInputStream();

  public StartH2Task() throws IOException {
    //since the JavaExec task will be run async (otherwise H2 thread dies upon gradle finishing JavaExec task in a separate process)
    //the output of the task (h2 server starting) in standard output has to be captured to figure out if the h2 has started.
    super.setStandardOutput(new PipedOutputStream(inputStream));
  }

  /**
   * This {@link TaskAction} starts the H2 Database
   */
  @Override
  public void exec() {
    LOGGER.debug("Trying to start h2 database.");

    LOGGER.info("Using main args: "+this.getArgs());
    LOGGER.info("Using the classpath: "+this.getClasspath().getAsPath()+" to start h2 db, as collected from h2 config block runtimeDependency param");
    CompletableFuture.runAsync(super::exec).exceptionally(ex -> {
      LOGGER.error("Failed to start h2 database", ex);
      throw new GradleException("Could not start H2 database.", ex);
    });

    printH2DbThreadStdOut();

    LOGGER.debug("H2 started with args {}", this.getArgs());
  }

  private void printH2DbThreadStdOut(){
    try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String line;
      int lineCounter=0;

      while (lineCounter<2) {
        line = reader.readLine();
        if(line.toLowerCase().contains("tcp server") || line.toLowerCase().contains("web console server") ){
          lineCounter++;
        }
        System.out.println(line);
      }
    } catch (IOException e) {
      throw new GradleException("Could not start H2 database.", e);
    }
  }
}
