plugins {
  id("org.springframework.boot") version "3.0.6"
  id("io.spring.dependency-management") version "1.1.0"
  id("java")
}

group = "com.demo"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
  mavenCentral()
}

dependencyManagement {
  imports {
    // Align all Instrumentation modules, including logs, traces, propagators, etc.
    mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.16.0")
    mavenBom("io.opentelemetry:opentelemetry-bom:1.51.0")
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  runtimeOnly("com.h2database:h2")

  implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
  implementation("io.opentelemetry:opentelemetry-extension-trace-propagators")
  implementation("io.opentelemetry:opentelemetry-exporter-otlp")
  implementation("io.opentelemetry:opentelemetry-sdk-metrics")
  implementation("io.opentelemetry:opentelemetry-sdk-logs")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
