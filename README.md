H2 Gradle Plugin
-----------------
[![Build Status](https://travis-ci.org/umich-michr/h2-gradle-plugin.svg?branch=master)](https://travis-ci.org/umich-michr/h2-gradle-plugin)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=umich-michr_h2-gradle-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=umich-michr_h2-gradle-plugin)

A plugin for [Gradle](https://gradle.org) that allows you to use [H2](https://www.h2database.com) during the build process. 
This allows you to use additional plugins like [Liquibase](https://www.liquibase.org/), [Flyaway](https://flywaydb.org/), etc.
to prepopulate the H2 database before starting your application.

News
----
### July 23, 2020
First public release of the H2 plugin.

Usage
-----
<details>
<summary>Groovy</summary>
Using the plugins DSL:

```groovy
plugins {
  id "edu.umich.med.michr.h2-plugin" version "0.1.1-SNAPSHOT"
}
```

Using legacy plugin application:

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "edu.umich.med.michr:h2-gradle-plugin:0.1.1-SNAPSHOT"
  }
}

apply plugin: "edu.umich.med.michr.h2-plugin"
```

Configure the extension using a DSL block. These are the defaults settings if not configured.
```groovy
h2 {
  tcpPort = 9092
  tcpPassword = "default"
  webPort = 8082
}
```
</details>
<details open>
<summary>Kotlin</summary>
Using the plugins DSL:

```kotlin
plugins {
  id("edu.umich.med.michr.h2-plugin") version "0.1.1-SNAPSHOT"
}
```

Using legacy plugin application:

```kotlin
buildscript {
  repositories {
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("edu.umich.med.michr:h2-gradle-plugin:0.1.1-SNAPSHOT")
  }
}

apply(plugin = "edu.umich.med.michr.h2-plugin")
```

Configure the extension using a DSL block. These are the defaults settings if not configured.
```kotlin
configure<H2PluginExtension> {
  tcpPort = 9092
  tcpPassword = "default"
  webPort = 8082
}
```
</details>