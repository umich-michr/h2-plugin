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
package edu.umich.med.michr.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static edu.umich.med.michr.gradle.GradleFunctionalTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * H2 Gradle Plugin testing
 */
@DisplayName("H2 Gradle Plugin")
class H2PluginTest {
  private static final String DEPENDENCIES_TASK = "dependencies";
  @TempDir
  static Path temporaryProjectDirectory;

  @Test
  @DisplayName("H2 Database default dependency")
  void testH2DefaultDependency() throws IOException {
    createGradleBuildFiles(temporaryProjectDirectory);
    GradleRunner gradleRunner = setupGradleTask(temporaryProjectDirectory, DEPENDENCIES_TASK);
    BuildResult buildResult = gradleRunner.build();

    assertGradleTaskSuccess(buildResult, DEPENDENCIES_TASK);
    assertTrue(buildResult.getOutput().contains("com.h2database:h2:1.4.200"), "H2 dependency missing");
  }

  @Test
  @DisplayName("H2 Database user configured dependency")
  void testH2UserOverrideDependency() throws IOException {
    String buildFileDependencies = "dependencies {\"h2\"(\"com.h2database:h2:1.4.197\")}";
    createGradleBuildFiles(temporaryProjectDirectory, buildFileDependencies);
    GradleRunner gradleRunner = setupGradleTask(temporaryProjectDirectory, DEPENDENCIES_TASK);
    BuildResult buildResult = gradleRunner.build();

    assertGradleTaskSuccess(buildResult, DEPENDENCIES_TASK);
    assertTrue(buildResult.getOutput().contains("com.h2database:h2:1.4.197"), "H2 dependency missing");
  }
}
