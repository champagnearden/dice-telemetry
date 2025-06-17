package com.demo.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TracerConfig {

  @Value("${otel.resource.attributes.service.name}")
  private String tracerName;

  @Bean
  public Tracer tracer(OpenTelemetry openTelemetry) {
    // the name/version here shows up in your tracing backend
    return openTelemetry.getTracer("Dice Telemetry", "1.0.0");
  }
}
