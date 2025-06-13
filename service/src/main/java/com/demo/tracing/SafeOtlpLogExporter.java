package com.demo.tracing;

import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Wraps the OTLP HTTP LogRecordExporter so failures are logged cleanly.
 */
public class SafeOtlpLogExporter implements LogRecordExporter {
  private static final Logger logger = LoggerFactory.getLogger(SafeOtlpLogExporter.class);
  private final LogRecordExporter delegate;

  public SafeOtlpLogExporter(String endpoint) {
    this.delegate = OtlpHttpLogRecordExporter.builder()
      .setEndpoint(endpoint)
      .build();
  }

  @Override
  public CompletableResultCode export(Collection<LogRecordData> logs) {
    try {
      return delegate.export(logs);
    } catch (Exception e) {
      logger.error("Unable to export {} logs to OTLP endpoint: {}", logs.size(), e.getMessage());
      return CompletableResultCode.ofFailure();
    }
  }

  @Override
  public CompletableResultCode flush() {
    try {
      return delegate.flush();
    } catch (Exception e) {
      logger.warn("Error flushing OTLP logs exporter: {}", e.getMessage());
      return CompletableResultCode.ofFailure();
    }
  }

  @Override
  public CompletableResultCode shutdown() {
    try {
      return delegate.shutdown();
    } catch (Exception e) {
      logger.warn("Error shutting down OTLP logs exporter: {}", e.getMessage());
      return CompletableResultCode.ofFailure();
    }
  }
}
