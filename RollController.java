package otel;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
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

  // inject the OT tracer
  private final Tracer tracer;

  public RollController(Tracer tracer) {
    this.tracer = tracer;
  }

  @GetMapping("/rolldice")
  public String index(@RequestParam("player") Optional<String> player) {
    // start a "planner" span around your dice‐roll logic
    Span span = tracer.spanBuilder("rollDiceOperation")
      .startSpan();
    try {
      int result = ThreadLocalRandom.current().nextInt(1, 7);

      span.setAttribute("dice.result", result);
      if (player.isPresent()) {
        logger.info("{} is rolling the dice: {}", player.get(), result);
      } else {
        logger.info("Anonymous player is rolling the dice: {}", result);
      }

      return Integer.toString(result);
    } finally {
      span.end();  // triggers Log4jSpanProcessor.onEnd()
    }
  }
}
