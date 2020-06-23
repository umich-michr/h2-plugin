/*
 * Copyright (c) 2020. Joshua Raymond
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
import java.nio.file.Path;
import java.sql.SQLException;

import static edu.umich.med.michr.gradle.GradleFunctionalTestHelper.*;
import static edu.umich.med.michr.gradle.H2PluginExtension.DEFAULT_TCP_PASSWORD;
import static edu.umich.med.michr.gradle.H2PluginExtension.DEFAULT_TCP_PORT;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Stop H2 Task")
class StopH2TaskTest {
  private static final Logger LOGGER = Logging.getLogger(StopH2TaskTest.class);
  private static final String STOP_TASK = "stopH2";
  @TempDir
  static Path temporaryProjectDirectory;

  private void startH2() {
    startH2(DEFAULT_TCP_PORT, DEFAULT_TCP_PASSWORD);
  }

  private void startH2(int port) {
    startH2(port, DEFAULT_TCP_PASSWORD);
  }

  private void startH2(int port, String tcpPassword) {
    String connectionUrl = "tcp://localhost:" + port;

    try {
      Server.createTcpServer("-tcpPort", String.valueOf(port), "-tcpPassword", tcpPassword).start();
    } catch (SQLException throwables) {
      LOGGER.error("H2 Database not stopped.", throwables);
    }
  }

  @Test
  @DisplayName("Stop H2 Database")
  public void h2StopTaskTest() throws IOException {
    // Start the H2 database to be stopped
    startH2();

    Path buildGradle = createGradleBuildFiles(temporaryProjectDirectory);
    GradleRunner gradleRunner = setupGradleTask(temporaryProjectDirectory, STOP_TASK);
    BuildResult buildResult = gradleRunner.build();

    assertGradleTaskSuccess(buildResult, STOP_TASK);
    assertTrue(buildResult.getOutput().contains("Shutting down TCP Server"), "H2 database did not stop.");
  }

  @Test
  @DisplayName("Fails to stop H2 Database when H2 is not running.")
  public void h2StopTaskExceptionTest() throws IOException {
    Path buildGradle = createGradleBuildFiles(temporaryProjectDirectory);
    GradleRunner gradleRunner = setupGradleTask(temporaryProjectDirectory, STOP_TASK);
    BuildResult buildResult = gradleRunner.buildAndFail();

    assertGradleTaskFailure(buildResult, STOP_TASK);
    assertTrue(buildResult.getOutput().contains("Could not stop H2 database."), "H2 database was running");
  }

  @Test
  @DisplayName("Stop H2 Database with user configured TCP port")
  public void h2ExtensionTcpPortTest() throws IOException {
    int port = 8181;
    // Start the H2 database to be stopped
    startH2(port);
    String buildFileDependencies = String.format("h2 { tcpPort = %d }", port);
    createGradleBuildFiles(temporaryProjectDirectory, buildFileDependencies);
    GradleRunner gradleRunner = setupGradleTask(temporaryProjectDirectory, STOP_TASK);
    BuildResult buildResult = gradleRunner.withGradleVersion("6.5").build();

    assertGradleTaskSuccess(buildResult, STOP_TASK);
    assertTrue(buildResult.getOutput().contains("Shutting down TCP Server") &&
               buildResult.getOutput().contains(":8181"), String.format("H2 database did not stop on port %d", port));
  }
}