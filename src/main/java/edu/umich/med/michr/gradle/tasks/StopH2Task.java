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
 * Gradle task that stops the H2 database.
 */
public class StopH2Task extends DefaultTask {
  private static final Logger LOGGER = Logging.getLogger(StopH2Task.class);

  private final Property<Integer> tcpPort;
  private final Property<String> tcpPassword;

  @Inject
  public StopH2Task(ObjectFactory objectFactory) {
    this.tcpPort = objectFactory.property(Integer.class);
    this.tcpPassword = objectFactory.property(String.class);
  }

  @Input
  public Property<Integer> getTcpPort() {
    return tcpPort;
  }

  @Input
  public Property<String> getTcpPassword() {
    return tcpPassword;
  }

  /**
   * This {@link TaskAction} stops the H2 Database
   */
  @TaskAction
  void stop() {
    LOGGER.debug("Stopping h2 database on port: {}", tcpPort.get());

    try {
      Server.main("-tcpShutdown", "tcp://localhost:" + tcpPort.get().toString(), "-tcpPassword", tcpPassword.get());
    } catch (SQLException throwables) {
      throw new GradleException("Could not stop H2 database.", throwables);
    }

    LOGGER.debug("Stopped H2 database.");
  }
}
