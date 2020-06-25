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
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Helper class for testing Gradle
 */
public class GradleFunctionalTestHelper {
  private static final String GRADLE_TASK_PATH_PREFIX = ":";

  /**
   * Asserts that the Gradle task had a failure.
   *
   * @param buildResult The results from the Gradle task
   * @param gradleTask  The Gradle task to test
   */
  public static void assertGradleTaskSuccess(BuildResult buildResult, String gradleTask) {
    if (buildResult == null) {
      throw new IllegalArgumentException("BuildResult must not be null");
    }
    if (gradleTask == null || gradleTask.isBlank()) {
      throw new IllegalArgumentException("GradleTask must not be null or blank");
    }

    BuildTask buildTask = buildResult.task(GRADLE_TASK_PATH_PREFIX + gradleTask);

    assertNotNull(buildTask, String.format("Gradle task %s did not run.", gradleTask));
    assertEquals(SUCCESS, buildTask.getOutcome(), String.format("Gradle task %s was not a success", gradleTask));
  }

  /**
   * Asserts that the Gradle task had a failure.
   *
   * @param buildResult The results from the Gradle task
   * @param gradleTask  The Gradle task to test
   */
  public static void assertGradleTaskFailure(BuildResult buildResult, String gradleTask) {
    if (buildResult == null) {
      throw new IllegalArgumentException("BuildResult must not be null");
    }
    if (gradleTask == null || gradleTask.isBlank()) {
      throw new IllegalArgumentException("GradleTask must not be null or blank");
    }

    BuildTask buildTask = buildResult.task(GRADLE_TASK_PATH_PREFIX + gradleTask);

    assertNotNull(buildTask, String.format("Gradle task %s did not run.", gradleTask));
    assertEquals(FAILED, buildTask.getOutcome(), String.format("Gradle task %s was not a success", gradleTask));
  }

  /**
   * Creates the Gradle build files settings.gradle.kts and build.gradle.kts. These files are configured to test the H2
   * plugin. It returns the build.gradle.kts file so it can be appended to for additional setup.
   *
   * @param directory The directory to put settings.gradle.kts and build.gradle.kts
   * @return Path The path for the build.gradle.kts file
   * @throws IOException Thrown if unable to write to the temporary directory
   */
  public static Path createGradleBuildFiles(Path directory) throws IOException {
    Path settingsFile = directory.resolve("settings.gradle.kts");
    Path buildFile = directory.resolve("build.gradle.kts");

    Files.write(settingsFile, "rootProject.name = \"test-h2-plugin\"".getBytes());
    String buildFileContent = "plugins { id(\"edu.umich.med.michr.h2-plugin\") }";
    Files.write(buildFile, buildFileContent.getBytes());

    return buildFile;
  }

  /**
   * Creates the Gradle build files settings.gradle.kts and build.gradle.kts. These files are configured to test the H2
   * plugin. It appends {@code appendToBuildGradle} to the end of the build.gradle.kts file.
   *
   * @param directory           The directory to put settings.gradle.kts and build.gradle.kts
   * @param appendToBuildGradle The string to appended to the end of build.gradle.kts file
   * @throws IOException Thrown if unable to write to the temporary directory
   */
  public static void createGradleBuildFiles(Path directory, String appendToBuildGradle) throws IOException {
    Path buildGradle = createGradleBuildFiles(directory);
    Files.write(buildGradle, appendToBuildGradle.getBytes(), StandardOpenOption.APPEND);
  }

  /**
   * Setups the GradleRunner for the provided {@code gradleTask} to be run later with {@link GradleRunner#build()} or
   * {@link GradleRunner#buildAndFail()}.
   *
   * @param gradleTask Gradle task to setup for execution
   * @return GradleRunner ready to be executed with {@link GradleRunner#build()} or {@link GradleRunner#buildAndFail()}
   */
  public static GradleRunner setupGradleTask(Path directory, String gradleTask) {
    return GradleRunner.create()
                       .withPluginClasspath()
                       .withDebug(true)
                       .withProjectDir(directory.toFile())
                       .withArguments(gradleTask);
  }
}
