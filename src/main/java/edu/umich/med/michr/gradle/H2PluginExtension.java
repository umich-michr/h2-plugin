package edu.umich.med.michr.gradle;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.provider.Property;
import org.h2.engine.Constants;

public abstract class H2PluginExtension {
  static final String MAIN_CLASS="org.h2.tools.Server";
  static final String RUNTIME_DEPENDENCY="com.h2database:h2:2.0.202";
  private static final String DEFAULT_TCP_PASSWORD= "admin";
  private static final String DEFAULT_WEB_ADMIN_PASSWORD= "admin";

  public abstract Property<String> getMainClass();
  public abstract Property<String> getRuntimeDependency();

  public abstract Property<Integer> getTcpPort();
  public abstract Property<Integer> getWebPort();

  public abstract Property<String> getTcpPassword();
  public abstract Property<String> getWebAdminPassword();

  public abstract Property<Boolean> getIfNotExists();
  public abstract Property<Boolean> getTcpAllowOthers();
  public abstract Property<Boolean> getWebAllowOthers();
  public abstract Property<Boolean> getBrowser();

  @SuppressWarnings("java:S5993")
  public H2PluginExtension() {
    this.getMainClass().convention(MAIN_CLASS);
    this.getRuntimeDependency().convention(RUNTIME_DEPENDENCY);
    this.getTcpPort().convention(Constants.DEFAULT_TCP_PORT);
    this.getWebPort().convention(Constants.DEFAULT_HTTP_PORT);
    this.getTcpPassword().convention(DEFAULT_TCP_PASSWORD);
    this.getWebAdminPassword().convention(DEFAULT_WEB_ADMIN_PASSWORD);
    this.getIfNotExists().convention(true);
    this.getTcpAllowOthers().convention(true);
    this.getWebAllowOthers().convention(true);
    this.getBrowser().convention(false);
  }

  /**
   *
   * @param project The gradle project that applies this plugin
   * @return The gradle build dependency config that specifies the h2 jar file in the form of a maven artifact.
   */
  public Configuration buildClassPathConfig(Project project){
    final Dependency h2Dependency = project.getDependencies().create(getRuntimeDependency().get());
    return project.getConfigurations().detachedConfiguration(h2Dependency);
  }

  /**
   * Builds the parameters that will be passed to the main method of H2 db executable jar file to start the db.
   * @return Main method arguments as a string for H2 db server
   */
  public String buildH2StartMainArgs(){
    String cmdArgs = String.format("-tcp -tcpPort %d -tcpPassword %s -web -webPort %d -webAdminPassword %s",
                  getTcpPort().get(), getTcpPassword().get(), getWebPort().get(), getWebAdminPassword().get());
    StringBuilder commandArgsBuilder = new StringBuilder(cmdArgs);

    if(getIfNotExists().get().equals(Boolean.TRUE)){
      commandArgsBuilder.append(" -ifNotExists");
    }else{
      commandArgsBuilder.append(" -ifExists");
    }
    if(getTcpAllowOthers().get().equals(Boolean.TRUE)){
      commandArgsBuilder.append(" -tcpAllowOthers");
    }
    if(getWebAllowOthers().get().equals(Boolean.TRUE)){
      commandArgsBuilder.append(" -webAllowOthers");
    }
    if(getBrowser().get().equals(Boolean.TRUE)){
      commandArgsBuilder.append(" -browser");
    }

    return commandArgsBuilder.toString();
  }

  /**
   * Builds the parameters that will be passed to the main method of H2 db executable jar file to stop the db.
   * @return Main method arguments as a string for H2 db server
   */
  public String getH2StopMainArgs() {
    return String.format("-tcpShutdown tcp://localhost:%d -tcpPassword %s",getTcpPort().get(),getTcpPassword().get());
  }
}
