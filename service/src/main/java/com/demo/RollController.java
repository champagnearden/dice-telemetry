package com.demo;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RollController {
  private static final Logger logger = LoggerFactory.getLogger(RollController.class);
  private final Tracer tracer;

  public RollController(Tracer tracer) {
    this.tracer = tracer;
  }

  @GetMapping("/rolldice")
  public String index(@RequestParam("player") Optional<String> player) {
    // 1) Start a span
    Span span = tracer.spanBuilder("rollDiceOperation")
      .setSpanKind(SpanKind.SERVER)
      .startSpan();

    // 2) Put it into the current Context
    try (Scope scope = span.makeCurrent()) {
      int result = ThreadLocalRandom.current().nextInt(1, 7);
      span.setAttribute("dice.result", result);

      if (player.isPresent()) {
        String name = player.get();
        logger.info("{} is rolling the dice: {}", name, result);
        span.setAttribute("player.name", name);
      } else {
        logger.info("Anonymous player is rolling the dice: {}", result);
        span.setAttribute("player.name", "Anonymous");
      }

      return Integer.toString(result);
    } finally {
      span.end();
    }
  }
}

