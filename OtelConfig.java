package otel.config;

import otel.Log4jSpanProcessor;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelConfig {

  @Bean
  public SdkTracerProvider sdkTracerProvider() {
    return SdkTracerProvider.builder()
      // wire in your custom Log4j processor
      .addSpanProcessor(new Log4jSpanProcessor())
      // could add other exporters here if desired
      .build();
  }

  @Bean
  public Tracer plannerTracer(SdkTracerProvider provider) {
    // name it “planner” so Log4jSpanProcessor will pick it up
    return provider.get("planner", "1.0.0");
  }
}
