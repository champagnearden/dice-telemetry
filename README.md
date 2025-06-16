[![champagnearden - dice-telemetry](https://img.shields.io/static/v1?label=champagnearden&message=dice-telemetry&color=blue&logo=github)](https://github.com/champagnearden/dice-telemetry "Go to GitHub repo")
&emsp;[![stars - dice-telemetry](https://img.shields.io/github/stars/champagnearden/dice-telemetry?style=social)](https://github.com/champagnearden/dice-telemetry)
&emsp;[![forks - dice-telemetry](https://img.shields.io/github/forks/champagnearden/dice-telemetry?style=social)](https://github.com/champagnearden/dice-telemetry)
&emsp;[![issues - dice-telemetry](https://img.shields.io/github/issues/champagnearden/dice-telemetry)](https://github.com/champagnearden/dice-telemetry/issues)
&emsp;[![coffee](https://img.buymeacoffee.com/button-api/?text=Buy%20me%20a%20coffee&emoji=🗿&slug=champagnearden&button_colour=FF5F5F&font_colour=FFFFFF&font_family=Cookie&outline_colour=000000&coffee_colour=ffffff)](https://buymeacoffee.com/champagnearden "Buy me a coffee")

# dice-telemetry
This repository is a simple POC to integrate opentelemetry with log4j

## Prerequisites
### 1. Service
- Java 8+ (21.0.4)
- Gradle
- sha512sum

### 2. OTLP HTTP Receiver (API)
- python + pip ($\ge$3) 

### 3. Testing
- curl (or wget)

## How to run
### 1. OpenTelemetry Receiver
Clone this repository and go in `dice-telemetry` folder then run:
```shell
chmod +x run_api.sh
./run_api.sh
```
This script will automatically install the required python dependencies and run the *OTLP HTTP receiver* on port **4000**
It will also create and/or tail three files under `Otel_API/data/`.
These are the data sent by services on the different routes:

| Route | File |
| ----- | ---- |
| `/v1/logs` | `logs.jsonl` |
| `/v1/metrics` | `metrics.jsonl` |
| `/v1/traces` | `traces.jsonl` |

### 2. Example service
In a second terminal, run the following command:
```shell
chmod +x run_service.sh
./run_service.sh
```
This will generate a hash of the `service` folder and compare it to the one stored in `service.sha512`.
If the hashes are different, it means that a change has been made in the **JAVA** files, so the script will launch a new gradle build (`gradle assemble`).
*The script won't consider any change in the `build.gradle.kts` file.*

Wait for the following line to show up:

`2025-06-12T09:31:50.118+01:00  INFO 2946 --- [main] otel.DiceApplication : Started DiceApplication in 1.933 seconds (process running for 8.238)`

### 3. Test it
Then, in a third terminal, run:
```shell
curl "localhost:8080/rolldice?player=bob"
```
A random number appears

Go back to the second terminal (service) and you should see two lines:
1. `2025-06-12T11:59:24.617+01:00  INFO 6199 --- [nio-8080-exec-1] otel.RollController : bob is rolling the dice: 3`

And in the first terminal (OTLP HTTP Receiver) you should see something like:
- `2025-06-16 10:51:18,377 Received 1 spans`
  `127.0.0.1 - - [16/Jun/2025 10:51:18] "POST /v1/traces HTTP/1.1" 200 -`
- `2025-06-16 10:51:18,473 Received 1 log records`
  `127.0.0.1 - - [16/Jun/2025 10:51:18] "POST /v1/logs HTTP/1.1" 200 -`
- `2025-06-16 10:51:19,401 Received 3 metric points`
  `127.0.0.1 - - [16/Jun/2025 10:51:19] "POST /v1/metrics HTTP/1.1" 200 -`

These lines can be in a different order, but the most important hing is to see all the three of them.

You can now explore the `Otel_API/data` folder and see all the logs metrics and traces snt by he service !
