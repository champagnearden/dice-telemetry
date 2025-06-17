package com.demo.config;

import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.opensearch.common.settings.Settings;
import org.opensearch.telemetry.tracing.exporter.OTelSpanExporterFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpensearchStyleExporterConfig {
  @Value("${opensearch.otel.tracer.exporter.class}")
  private String exporterClassName;

  @Bean
  public SpanExporter spanExporter() {
    try {
      // Build a minimal Settings object with just that one value
      Settings settings = Settings.builder()
        .put("opensearch.otel.tracer.exporter.class", exporterClassName)
        .build();

      // Let the OpenSearch factory reflectively instantiate it
      SpanExporter exporter = OTelSpanExporterFactory.create(settings);
      return exporter;

    } catch (Exception e) {
      // If reflection/factory fails, fall back to a no-op or your SafeOtlpExporter
      // (so your app still starts)
      return SpanExporter.composite();
    }
  }
}
