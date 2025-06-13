package com.demo.config;

import com.demo.tracing.SafeOtlpExporter;
import com.demo.tracing.SafeOtlpLogExporter;
import com.demo.tracing.SafeOtlpMetricExporter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricReader;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OtelExporterConfig {

  @Value("${otel.exporter.otlp.traces.endpoint}")
  private String tracesEndpoint;

  @Value("${otel.exporter.otlp.logs.endpoint}")
  private String logsEndpoint;

  @Value("${otel.exporter.otlp.metrics.endpoint}")
  private String metricsEndpoint;

  @Bean
  public OpenTelemetry openTelemetry() {
    // 1) Trace exporter + processor
    var spanExporter = new SafeOtlpExporter(tracesEndpoint);
    var spanProcessor = BatchSpanProcessor.builder(spanExporter)
      .setScheduleDelay(Duration.ofSeconds(1))
      .setMaxExportBatchSize(512)
      .setMaxQueueSize(2048)
      .build();
    SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
      .addSpanProcessor(spanProcessor)
      .build();

    // 2) Log exporter + processor
    var logExporter = new SafeOtlpLogExporter(logsEndpoint);
    var logProcessor = BatchLogRecordProcessor.builder(logExporter)
      .setScheduleDelay(Duration.ofSeconds(1))
      .build();
    SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
      .addLogRecordProcessor(logProcessor)
      .build();

    // 3) Metric exporter + reader
    var metricExporter = new SafeOtlpMetricExporter(metricsEndpoint);
    MetricReader metricReader = PeriodicMetricReader.builder(metricExporter)
      .setInterval(Duration.ofSeconds(10))
      .build();
    SdkMeterProvider meterProvider = SdkMeterProvider.builder()
      .registerMetricReader(metricReader)
      .build();

    // 4) Context propagation (e.g. B3)
    ContextPropagators propagators = ContextPropagators.create(
      B3Propagator.injectingMultiHeaders()
    );

    // 5) Build & register global
    return OpenTelemetrySdk.builder()
      .setTracerProvider(tracerProvider)
      .setLoggerProvider(loggerProvider)
      .setMeterProvider(meterProvider)
      .setPropagators(propagators)
      .buildAndRegisterGlobal();
  }

  @Bean
  public Tracer tracer(OpenTelemetry otel) {
    return otel.getTracer("com.demo.taskmanager", "0.1.0");
  }
}
