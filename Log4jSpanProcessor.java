package otel;

import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.common.CompletableResultCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

/**
 * A SpanProcessor that logs planner-only spans at TRACE level via Log4j.
 */
public class Log4jSpanProcessor implements SpanProcessor {
  private static final Logger logger = LogManager.getLogger(Log4jSpanProcessor.class);

  @Override
  public void onStart(Context parentContext, ReadWriteSpan span) {
    // No action on span start
  }

  @Override
  public void onEnd(ReadableSpan span) {
    // Only log spans instrumented as "planner"
    String instrumentation = span.getInstrumentationScopeInfo().getName();
    if (!"planner".equals(instrumentation)) {
      return;
    }

    // Extract timing information from SpanData
    SpanData data = span.toSpanData();
    String operationName = data.getName();
    Instant start = Instant.ofEpochSecond(0, data.getStartEpochNanos());
    Instant end = Instant.ofEpochSecond(0, data.getEndEpochNanos());
    long durationMs = (data.getEndEpochNanos() - data.getStartEpochNanos()) / 1_000_000;

    logger.trace("Operation: {} | Start: {} | End: {} | Duration: {} ms",
      operationName, start, end, durationMs);
  }

  @Override
  public boolean isStartRequired() {
    return false;
  }

  @Override
  public boolean isEndRequired() {
    return true;
  }

  @Override
  public CompletableResultCode shutdown() {
    return CompletableResultCode.ofSuccess();
  }

  @Override
  public CompletableResultCode forceFlush() {
    return CompletableResultCode.ofSuccess();
  }
}
