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
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.h2.engine.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.umich.med.michr.gradle.H2PluginExtension.MAIN_CLASS;
import static edu.umich.med.michr.gradle.H2PluginExtension.RUNTIME_DEPENDENCY;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * H2 Gradle Plugin testing
 */
@DisplayName("H2 Gradle Plugin")
class H2PluginTest {
  private static final String PLUGIN_ID = "edu.umich.med.michr.h2-plugin";
  private static final String BUILD_FILE_NAME = "build.gradle";

  @TempDir
  private static Path temporaryProjectDirectory;

  @BeforeAll
  static void setup() throws IOException {
    final File templateBuildFile = new File(ClassLoader.getSystemResource(BUILD_FILE_NAME).getFile());
    File buildFile = temporaryProjectDirectory.resolve(BUILD_FILE_NAME).toFile();

    Files.copy(templateBuildFile.toPath(), new FileOutputStream(buildFile));
  }

  @Test
  @DisplayName("Plugin sets up tasks with default configuration.")
  void pluginAddsStartH2TaskToProject(){
    Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(PLUGIN_ID);

    TaskCollection<Task> h2Tasks = project.getTasks().matching(t -> Objects.equals(t.getGroup(),
                                                                                   H2Plugin.H2_CONFIGURATION_NAME));
    assertEquals(2,h2Tasks.size(),"The plugin should have registered start/stop tasks under the same group name.");

    StartH2Task startH2Task = (StartH2Task)project.getTasks().getByName("startH2");
    JavaExec stopH2Task = (JavaExec)project.getTasks().getByName("stopH2");
    H2PluginExtension defaultPluginConfig = (H2PluginExtension) project.getExtensions().getByName("h2");

    assertEquals(MAIN_CLASS, defaultPluginConfig.getMainClass().get(), "");
    assertEquals(RUNTIME_DEPENDENCY, defaultPluginConfig.getRuntimeDependency().get(), "");
    assertFalse(defaultPluginConfig.getBrowser().get(),"The browser will not start upon h2 db web server start.");
    assertTrue(defaultPluginConfig.getIfNotExists().get(),"A database will be created when the db is accessed the first time if the db already does not exist.");
    assertTrue(defaultPluginConfig.getTcpAllowOthers().get(),"Any client will be able to remotely connect to db");
    assertTrue(defaultPluginConfig.getWebAllowOthers().get(),"Any client will be able to remotely connect to the web console");
    assertEquals("admin",defaultPluginConfig.getTcpPassword().get(),"The default password to shutdown the db should be set");
    assertEquals("admin",defaultPluginConfig.getWebAdminPassword().get(),"The default web console admin password should be set.");
    assertEquals(Constants.DEFAULT_TCP_PORT, defaultPluginConfig.getTcpPort().get(), "The default port for tcp server should be the same default port specified by h2 lib.");
    assertEquals(Constants.DEFAULT_HTTP_PORT,defaultPluginConfig.getWebPort().get(),"The default port for web console server should be the same default port specified by h2 lib.");
    assertEquals(Arrays.asList("-tcp","-tcpPort","9092","-tcpPassword","admin","-web","-webPort","8082","-webAdminPassword","admin","-ifNotExists","-tcpAllowOthers","-webAllowOthers"), startH2Task.getArgs(), "The main method arguments should be correctly build to be passed to the JavaExec task for running executable h2 jar to start the h2 db");
    assertEquals(Arrays.asList("-tcpShutdown","tcp://localhost:9092","-tcpPassword","admin"),stopH2Task.getArgs(),"The main method arguments should be correctly build to be passed to the JavaExec task for running executable h2 jar to stop the h2 db");
  }

  @Test
  @DisplayName("Run plugin tasks with user specified configuration")
  void testTasks_with_user_configured_db() {
    final String expectedTCPServerStartedMsg = "TCP server running at tcp://(\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b):(\\d+).*only local connections";
    final String expectedWebServerStartedMsg = "Web Console server running at http://(\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b):(\\d+)";

    BuildResult result = GradleRunner.create()
                                     .withPluginClasspath()
                                     .withDebug(true)
                                     .withProjectDir(temporaryProjectDirectory.toFile())
                                     .withArguments("startH2","stopH2")
                                     .build();

    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":startH2")).getOutcome());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":stopH2")).getOutcome());

    Matcher tcpServerOutputMatcher = Pattern.compile(expectedTCPServerStartedMsg).matcher(result.getOutput());
    Matcher webServerOutputMatcher = Pattern.compile(expectedWebServerStartedMsg).matcher(result.getOutput());
     assertTrue(tcpServerOutputMatcher.find(), "TCP server started message should be displayed.");
    assertTrue(webServerOutputMatcher.find(), "Web console started should be displayed.");

    assertEquals(tcpServerOutputMatcher.group(1), webServerOutputMatcher.group(1), "The ip address of the local machine reported for web and tcp servers should be the same.");
    assertEquals("9095", tcpServerOutputMatcher.group(2), "TCP server should be listening to the port specified by the user config in the build file");
    assertEquals("8085", webServerOutputMatcher.group(2), "Web server should be listening to the port specified by the user config in the build file.");
  }
}
