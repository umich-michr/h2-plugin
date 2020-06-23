plugins {
  `java-gradle-plugin`
  id("com.gradle.plugin-publish") version "0.12.0"
  `maven-publish`
  jacoco
  id("pl.droidsonroids.jacoco.testkit") version "1.0.7"
  id("org.sonarqube") version "2.8"
}

group = "edu.umich.med.michr"
version = "0.1-SNAPSHOT"
description = "A Gradle plugin for running the H2 database."

repositories {
  jcenter()
}

dependencies {
  implementation("com.h2database:h2:1.4.200")
  testImplementation(gradleTestKit())
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
  withJavadocJar()
  withSourcesJar()
}

tasks.compileJava {
  options.compilerArgs = listOf("-Xlint:all")
}

tasks.compileTestJava {
  sourceCompatibility = "11"
  targetCompatibility = "11"
  options.compilerArgs = listOf("-Xlint:all")
}

tasks.javadoc {
  if (JavaVersion.current().isJava9Compatible) {
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
  }
}

tasks.test {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)

  reports {
    csv.isEnabled = false
    html.isEnabled = true
    xml.isEnabled = true
  }
}

tasks.jacocoTestCoverageVerification {
  violationRules {
    rule {
      limit {
        minimum = "0.9".toBigDecimal()
      }
    }
  }
}

sonarqube {
  properties {
    property("sonar.projectKey", "umich-michr_h2-gradle-plugin")
  }
}

tasks.check {
  dependsOn(tasks.jacocoTestCoverageVerification)
}

// Configure the java-gradle-plugin. Note that the ID must match it's Gradle Plugin Portal id.
gradlePlugin {
  plugins {
    create("h2Plugin") {
      id = "edu.umich.med.michr.h2-plugin"
      displayName = "Gradle H2 Plugin"
      description = project.description
      implementationClass = "edu.umich.med.michr.gradle.H2Plugin"
    }
  }
}

// Configuration for publishing to the Gradle plugins portal
pluginBundle {
  website = "https://github.com/umich-michr/h2-gradle-plugin"
  vcsUrl = "https://github.com/umich-michr/h2-gradle-plugin.git"
  description = project.description
  tags = listOf("gradle", "h2", "database")
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      pom {
        name.set("Gradle H2 Plugin")
        description.set(project.description)
        url.set("https://github.com/umich-michr/h2-gradle-plugin")
        packaging = "jar"
        licenses {
          license {
            name.set("The MIT License")
            url.set("http://www.opensource.org/licenses/mit-license.php")
          }
        }
        developers {
          developer {
            id.set("raymojos")
            name.set("Joshua Raymond")
            email.set("raymojos@med.umich.edu")
          }
        }
        scm {
          connection.set("scm:git:https://github.com/umich-michr/h2-gradle-plugin.git")
          developerConnection.set("scm:git:https://github.com/umich-michr/h2-gradle-plugin.git")
          url.set("https://github.com/umich-michr/h2-gradle-plugin")
        }
      }
    }
  }
}