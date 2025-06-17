package com.demo.tracing;

import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class SafeOtlpExporter implements SpanExporter {
  private static final Logger logger = LoggerFactory.getLogger(SafeOtlpExporter.class);
  private final SpanExporter delegate;

  public SafeOtlpExporter(String endpoint) {
    this.delegate = OtlpHttpSpanExporter.builder()
      .setEndpoint(endpoint)
      .build();
  }

  @Override
  public CompletableResultCode export(Collection<SpanData> spans) {
    try {
      return delegate.export(spans);
    } catch (Exception e) {
      logger.error("Unable to export {} spans to OTLP endpoint: {}", spans.size(), e.getMessage());
      return CompletableResultCode.ofFailure();
    }
  }

  @Override
  public CompletableResultCode flush() {
    try {
      return delegate.flush();
    } catch (Exception e) {
      logger.warn("Error flushing OTLP exporter: {}", e.getMessage());
      return CompletableResultCode.ofFailure();
    }
  }

  @Override
  public CompletableResultCode shutdown() {
    try {
      return delegate.shutdown();
    } catch (Exception e) {
      logger.warn("Error shutting down OTLP exporter: {}", e.getMessage());
      return CompletableResultCode.ofFailure();
    }
  }
}
