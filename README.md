[![champagnearden - dice-telemetry](https://img.shields.io/static/v1?label=champagnearden&message=dice-telemetry&color=blue&logo=github)](https://github.com/champagnearden/dice-telemetry "Go to GitHub repo")
&emsp;[![stars - dice-telemetry](https://img.shields.io/github/stars/champagnearden/dice-telemetry?style=social)](https://github.com/champagnearden/dice-telemetry)
&emsp;[![forks - dice-telemetry](https://img.shields.io/github/forks/champagnearden/dice-telemetry?style=social)](https://github.com/champagnearden/dice-telemetry)
&emsp;[![issues - dice-telemetry](https://img.shields.io/github/issues/champagnearden/dice-telemetry)](https://github.com/champagnearden/dice-telemetry/issues)
&emsp;[![coffee](https://img.buymeacoffee.com/button-api/?text=Buy%20me%20a%20coffee&emoji=🗿&slug=champagnearden&button_colour=FF5F5F&font_colour=FFFFFF&font_family=Cookie&outline_colour=000000&coffee_colour=ffffff)](https://buymeacoffee.com/champagnearden "Buy me a coffee")

# dice-telemetry
This repository is a simple POC to integrate opentelemetry with log4j

## Prerequisities
Java 8+ (21.0.4)
Gradle
curl

## How to run
Clone this repository and go in `dice-telemetry` folder then run 
```shell
chmod +x run_service.sh
./run_service.sh
```

Wait for the following line to show up:

`2025-06-12T09:31:50.118+01:00  INFO 2946 --- [           main] otel.DiceApplication                     : Started DiceApplication in 1.933 seconds (process running for 8.238)`

Then, in another terminal run:
```shell
curl "localhost:8080/rolldice?player=john"
```
Then a random number appears

Go back to the first terminal and you should see two lines:
1. `2025-06-12T11:59:24.617+01:00  INFO 6199 --- [nio-8080-exec-1] otel.RollController                      : john is rolling the dice: 3`
2. `2025-06-12T11:59:24.622+01:00  WARN 6199 --- [nio-8080-exec-1] otel.Log4jSpanProcessor                  : Operation: rollDiceOperation | Start: 2025-06-12T10:59:24.615859Z | End: 2025-06-12T10:59:24.619277258Z | Duration: 3 ms`
