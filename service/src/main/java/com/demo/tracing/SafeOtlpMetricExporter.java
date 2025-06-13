package com.demo.tracing;

import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.InstrumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Wraps the OTLP-HTTP MetricExporter to log on failure instead of throwing.
 */
public class SafeOtlpMetricExporter implements MetricExporter {
  private static final Logger logger = LoggerFactory.getLogger(SafeOtlpMetricExporter.class);
  private final MetricExporter delegate;

  public SafeOtlpMetricExporter(String endpoint) {
    this.delegate = OtlpHttpMetricExporter.builder()
      .setEndpoint(endpoint)
      .build();
  }

  @Override
  public CompletableResultCode export(Collection<MetricData> metrics) {
    try {
      return delegate.export(metrics);
    } catch (Exception e) {
      logger.error("Unable to export {} metrics to OTLP endpoint: {}", metrics.size(), e.getMessage());
      return CompletableResultCode.ofFailure();
    }
  }

  @Override
  public CompletableResultCode flush() {
    try {
      return delegate.flush();
    } catch (Exception e) {
      logger.warn("Error flushing OTLP metric exporter: {}", e.getMessage());
      return CompletableResultCode.ofFailure();
    }
  }

  @Override
  public CompletableResultCode shutdown() {
    try {
      return delegate.shutdown();
    } catch (Exception e) {
      logger.warn("Error shutting down OTLP metric exporter: {}", e.getMessage());
      return CompletableResultCode.ofFailure();
    }
  }

  @Override
  public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
    // delegate to the underlying exporter’s choice
    return delegate.getAggregationTemporality(instrumentType);
  }
}
