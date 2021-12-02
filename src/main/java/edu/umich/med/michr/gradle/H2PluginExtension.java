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

/**
 * This is the Gradle extension that configures the H2 plugin. All configuration options will be in the {@code h2} block
 * of the build.gradle file. This block consists of optional configuration settings to run H2 tasks.
 */
public class H2PluginExtension {
  /**
   * Default H2 TCP port
   */
  public static final int DEFAULT_TCP_PORT = 9092;
  /**
   * Default H2 TCP password
   */
  public static final String DEFAULT_TCP_PASSWORD = "default";
  /**
   * Default H2 Web port
   */
  public static final int DEFAULT_WEB_PORT = 8082;

  private int tcpPort;
  private String tcpPassword;
  private int webPort;

  public H2PluginExtension() {
    tcpPort = DEFAULT_TCP_PORT;
    tcpPassword = DEFAULT_TCP_PASSWORD;
    webPort = DEFAULT_WEB_PORT;
  }

  public int getTcpPort() {
    return tcpPort;
  }

  public void setTcpPort(int tcpPort) {
    this.tcpPort = tcpPort;
  }

  public String getTcpPassword() {
    return tcpPassword;
  }

  public void setTcpPassword(String tcpPassword) {
    this.tcpPassword = tcpPassword;
  }

  public int getWebPort() {
    return webPort;
  }

  public void setWebPort(int webPort) {
    this.webPort = webPort;
  }
}
