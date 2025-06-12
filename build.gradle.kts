plugins {
  id("java")
  id("org.springframework.boot") version "3.0.6"
  id("io.spring.dependency-management") version "1.1.0"
}

sourceSets {
  main {
    java.setSrcDirs(setOf("."))
    resources {
      srcDir(".")
      include("log4j2.xml")
    }
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation ("org.springframework.boot:spring-boot-starter-web")

  // OpenTelemetry API & SDK
  implementation ("io.opentelemetry:opentelemetry-api:1.19.0")
  implementation ("io.opentelemetry:opentelemetry-sdk-trace:1.19.0")

  // Log4J2 (and SLF4J binding so your app logs via Log4J2)
  implementation ("org.apache.logging.log4j:log4j-api:2.20.0")
  implementation ("org.apache.logging.log4j:log4j-core:2.20.0")
  implementation ("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
}

tasks.compileJava {
  dependsOn(tasks.processResources)
}

