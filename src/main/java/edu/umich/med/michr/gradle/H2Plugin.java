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
package edu.umich.med.michr.gradle;

import edu.umich.med.michr.gradle.tasks.StartH2Task;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;

import javax.annotation.Nonnull;

/**
 * H2 Gradle Plugin for running the H2 database inside gradle.
 */
public class H2Plugin implements Plugin<Project> {
  static final String H2_CONFIGURATION_NAME = "h2";

  /**
   * {@inheritDoc}
   */
  @Override
  public void apply(@Nonnull Project project) {
    H2PluginExtension extension = applyExtension(project);
    applyTasks(project, extension);
  }

  /**
   * Apply the H2 plugin extension for the DSL for Gradle.
   *
   * @param project The project to enhance
   * @return {@link H2PluginExtension} Gradle's DSL extension for {@link H2Plugin}
   */
  H2PluginExtension applyExtension(Project project) {
    return project.getExtensions().create(H2_CONFIGURATION_NAME, H2PluginExtension.class);
  }

  /**
   * Create all the H2 tasks and add them to the project.
   *
   * @param project The project using this plugin
   */
  void applyTasks(Project project, H2PluginExtension extension) {
    project.getTasks().register("startH2", StartH2Task.class, (StartH2Task startH2Task) -> {
      startH2Task.setGroup(H2_CONFIGURATION_NAME);
      startH2Task.setDescription("Starts the H2 database.");
      startH2Task.getMainClass().set(extension.getMainClass());
      startH2Task.setArgsString(extension.buildH2StartMainArgs());
      startH2Task.setClasspath(extension.buildClassPathConfig(project));
    });

    project.getTasks().register("stopH2", JavaExec.class, (JavaExec stopH2Task) -> {
      stopH2Task.setGroup(H2_CONFIGURATION_NAME);
      stopH2Task.setDescription("Stops the H2 database.");
      stopH2Task.getMainClass().set(extension.getMainClass());
      stopH2Task.setArgsString(extension.getH2StopMainArgs());
      stopH2Task.setClasspath(extension.buildClassPathConfig(project));
    });
  }
}
