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

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.h2.tools.Server;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.sql.SQLException;

import static edu.umich.med.michr.gradle.GradleFunctionalTestHelper.*;
import static edu.umich.med.michr.gradle.H2PluginExtension.DEFAULT_TCP_PASSWORD;
import static edu.umich.med.michr.gradle.H2PluginExtension.DEFAULT_TCP_PORT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Start H2 Task")
class StartH2TaskTest {
  private static final Logger LOGGER = Logging.getLogger(StartH2TaskTest.class);
  private static final String START_TASK = "startH2";
  @TempDir
  static Path temporaryProjectDirectory;

  private void shutdownH2() {
    shutdownH2(DEFAULT_TCP_PORT);
  }

  private void shutdownH2(int port) {
    String connectionUrl = "tcp://localhost:" + port;

    try {
      Server.shutdownTcpServer(connectionUrl, DEFAULT_TCP_PASSWORD, false, false);
    } catch (SQLException throwables) {
      LOGGER.error("H2 Database not stopped.", throwables);
    }
  }

  @Test
  @DisplayName("Start H2 Database")
  void testH2StartTask() throws IOException {
    createGradleBuildFiles(temporaryProjectDirectory);
    GradleRunner gradleRunner = setupGradleTask(temporaryProjectDirectory, START_TASK);
    BuildResult buildResult = gradleRunner.build();

    assertGradleTaskSuccess(buildResult, START_TASK);
    assertTrue(buildResult.getOutput().contains("TCP server running"), "H2 database did not start");

    // Cleanup
    shutdownH2();
  }

  @Test
  @DisplayName("Fails to start H2 Database when port is in use")
  void testH2StartTaskException() throws IOException {
    // Open socket on default TCP port to block H2 from starting
    ServerSocket serverSocket = new ServerSocket(DEFAULT_TCP_PORT);
    createGradleBuildFiles(temporaryProjectDirectory);
    GradleRunner gradleRunner = setupGradleTask(temporaryProjectDirectory, START_TASK);
    BuildResult buildResult = gradleRunner.buildAndFail();
    serverSocket.close();

    assertGradleTaskFailure(buildResult, START_TASK);
    assertFalse(buildResult.getOutput().contains("TCP server running"), "H2 database started");

    // Cleanup
    serverSocket.close();
  }

  @Test
  @DisplayName("Start H2 Database with user configured TCP port")
  void testH2ExtensionTcpPort() throws IOException {
    int port = 8181;
    String buildFileDependencies = String.format("h2 { tcpPort = %d }", port);
    createGradleBuildFiles(temporaryProjectDirectory, buildFileDependencies);
    GradleRunner gradleRunner = setupGradleTask(temporaryProjectDirectory, START_TASK);
    BuildResult buildResult = gradleRunner.withGradleVersion("6.5").build();

    assertGradleTaskSuccess(buildResult, START_TASK);
    assertTrue(buildResult.getOutput().contains("TCP server running") && buildResult.getOutput().contains(":8181"),
               String.format("H2 database did not start on port %d", port));

    // Cleanup
    shutdownH2(port);
  }
}