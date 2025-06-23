package com.demo;

import io.opentelemetry.context.Scope;
import io.opentelemetry.context.Context;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.extension.annotations.WithSpan;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RollController {
  private static final Logger logger = LoggerFactory.getLogger(RollController.class);
  private final Tracer tracer;
  private static final ThreadLocal<Span> PARENT_SPAN = new ThreadLocal<>();


  public RollController(Tracer tracer) {
    this.tracer = tracer;
  }

  @WithSpan(value = "rollDiceOperation", kind = SpanKind.SERVER)
  @GetMapping("/rollDice")
  public String index(@RequestParam("player") Optional<String> player) {
    // 1) Start a span
    Span span = Span.current();
    span.setAttribute("custom.method", "rollDice");
    span.setAttribute("custom.dice.maximum", "6");

    // 2) Put it into the current Context
    int result = ThreadLocalRandom.current().nextInt(1, 7);
    span.setAttribute("custom.dice.result", result);

    String name = player.orElse("Anonymous");
    span.setAttribute("custom.player.name", name);
    logger.info("{} is rolling the dice: {}", name, result);
    return Integer.toString(result);
  }

  @GetMapping("/rollDice20")
  public String rollDice20(@RequestParam("player") Optional<String> player) {
    String result = "0";
    Span parent = tracer.spanBuilder("rollDice20Operation")
      .setSpanKind(SpanKind.SERVER)
      .startSpan();
    PARENT_SPAN.set(parent);

    // push the parent span into current context:
    try (Scope scope = parent.makeCurrent()) {
      parent.setAttribute("custom.method", "rollDice20");
      parent.setAttribute("custom.dice.maximum", 20);

      result = indexCustom("20", player);

      parent.setAttribute("custom.dice.result", result);
      parent.setAttribute("custom.player.name", player.orElse("Anonymous"));
      logger.info("{} is rolling the dice 20: {}", player.orElse("Anonymous"), result);
      return result;
    } finally {
      parent.end();
      PARENT_SPAN.remove();
    }
  }

  @GetMapping("/rollDiceCustom")
  public String indexCustom(@RequestParam("maximum") String maximum, @RequestParam("player") Optional<String> player) {
    Span parent = PARENT_SPAN.get();
    if (parent == null) {
      // no parent in this thread ⇒ this was called directly over HTTP
      parent = tracer.spanBuilder("rollCustomDiceFallbackParent")
        .setSpanKind(SpanKind.SERVER)
        .startSpan();
    }

    // 2) Build the child span explicitly using the parent’s Context
    Context parentCtx = Context.current().with(parent);
    Span span = tracer.spanBuilder("rollCustomDiceOperation")
      .setParent(parentCtx)
      .setSpanKind(SpanKind.INTERNAL)
      .startSpan();

    try (Scope spanScope = span.makeCurrent()) {

      span.setAttribute("custom.method", "rollDiceCustom");
      span.setAttribute("custom.dice.maximum", maximum);
      int max = Integer.parseInt(maximum) + 1;

      // 2) Put it into the current Context
      int result = ThreadLocalRandom.current().nextInt(1, max);
      span.setAttribute("custom.dice.result", result);

      String name = player.orElse("Anonymous");
      span.setAttribute("custom.player.name", name);
      logger.info("{} is rolling the dice: {}", name, result);

      // Wait for a time up to one second (1000 ms)
      Random random = new Random();
      int number = random.nextInt(max) % 1000;
      try {
        TimeUnit.MICROSECONDS.sleep(number);
      } catch (InterruptedException e) {
        logger.error("An error occured while waiting: {}", e.getMessage());
        Thread.currentThread().interrupt();
      }
      span.setAttribute("custom.waitTime", number);
      logger.info("Waited for {} ms", number);

      return Integer.toString(result);
    } finally {
      span.end();
      // if we created a fallback parent, end it too
      if (PARENT_SPAN.get() == null) {
        parent.end();
      }
    }
  }
}

