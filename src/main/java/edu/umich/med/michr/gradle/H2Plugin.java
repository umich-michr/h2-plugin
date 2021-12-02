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
import edu.umich.med.michr.gradle.tasks.StopH2Task;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

/**
 * H2 Gradle Plugin for running the H2 database inside of gradle.
 */
public class H2Plugin implements Plugin<Project> {
  private static final String H2_CONFIGURATION_NAME = "h2";

  /**
   * {@inheritDoc}
   */
  @Override
  public void apply(Project project) {
    H2PluginExtension extension = applyExtension(project);
    applyConfiguration(project);
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
   * Applies configurations changes that are require for the H2 plugin. Defining a default H2 library if one was not
   * included by the project.
   *
   * @param project The project to enhance
   */
  void applyConfiguration(Project project) {
    final Configuration config = project.getConfigurations()
                                        .maybeCreate("h2")
                                        .setVisible(false)
                                        .setDescription("The h2 artifacts to be processed for this plugin.");

    config.defaultDependencies(dependencies -> dependencies.add(project.getDependencies()
                                                                       .create("com.h2database:h2:2.0.202")));
  }

  /**
   * Create all of the H2 tasks and add them to the project.
   *
   * @param project The project to enhance
   */
  void applyTasks(Project project, H2PluginExtension extension) {
    project.getTasks().register("startH2", StartH2Task.class, (StartH2Task startH2Task) -> {
      startH2Task.setGroup(H2_CONFIGURATION_NAME);
      startH2Task.setDescription("Starts the H2 database.");
      startH2Task.getTcpPort().set(extension.getTcpPort());
      startH2Task.getTcpPassword().set(extension.getTcpPassword());
      startH2Task.getWebPort().set(extension.getWebPort());
    });

    project.getTasks().register("stopH2", StopH2Task.class, (StopH2Task stopH2Task) -> {
      stopH2Task.setGroup(H2_CONFIGURATION_NAME);
      stopH2Task.setDescription("Stops the H2 database.");
      stopH2Task.getTcpPort().set(extension.getTcpPort());
      stopH2Task.getTcpPassword().set(extension.getTcpPassword());
    });
  }
}
