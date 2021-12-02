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

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.h2.tools.Server;

import javax.inject.Inject;
import java.sql.SQLException;

/**
 * Gradle task that starts the H2 database.
 */
public class StartH2Task extends DefaultTask {
  private static final Logger LOGGER = Logging.getLogger(StartH2Task.class);

  private final Property<Integer> tcpPort;
  private final Property<String> tcpPassword;
  private final Property<Integer> webPort;

  @Inject
  public StartH2Task(ObjectFactory objectFactory) {
    this.tcpPort = objectFactory.property(Integer.class);
    this.tcpPassword = objectFactory.property(String.class);
    this.webPort = objectFactory.property(Integer.class);
  }

  @Input
  public Property<Integer> getTcpPort() {
    return tcpPort;
  }

  @Input
  public Property<String> getTcpPassword() {
    return tcpPassword;
  }

  @Input
  public Property<Integer> getWebPort() {
    return webPort;
  }

  /**
   * This {@link TaskAction} starts the H2 Database
   */
  @TaskAction
  void start() {
    LOGGER.debug("Trying to starting h2 database.");

    try {
      Server.main("-tcp",
                  "-tcpPort",
                  tcpPort.get().toString(),
                  "-tcpPassword",
                  tcpPassword.get(),
                  "-web",
                  "-ifNotExists",
                  "-webPort",
                  webPort.get().toString());
    } catch (SQLException throwables) {
      throw new GradleException("Could not start H2 database.", throwables);
    }

    LOGGER.debug("H2 started on port: {} and web server on port {}", tcpPort.get(), webPort.get());
  }
}
