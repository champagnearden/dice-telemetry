package com.demo.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Configuration
public class RestTemplateConfig {
  private static final TextMapSetter<HttpRequest> setter =
    (request, key, value) -> request.getHeaders().set(key, value);

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().add((request, body, execution) -> {
      // Grab the span you put into the Context in your controller
      Span span = Span.current();
      Context context = Context.current().with(span);

      // Inject the exact context carrying your span
      TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
      propagator.inject(context, request, setter);

      // proceed with the call
      return execution.execute(request, body);
    });
    return restTemplate;
  }
}
