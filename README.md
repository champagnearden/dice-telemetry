[![champagnearden - dice-telemetry](https://img.shields.io/static/v1?label=champagnearden&message=dice-telemetry&color=blue&logo=github)](https://github.com/champagnearden/dice-telemetry "Go to GitHub repo")
&emsp;[![stars - dice-telemetry](https://img.shields.io/github/stars/champagnearden/dice-telemetry?style=social)](https://github.com/champagnearden/dice-telemetry)
&emsp;[![forks - dice-telemetry](https://img.shields.io/github/forks/champagnearden/dice-telemetry?style=social)](https://github.com/champagnearden/dice-telemetry)
&emsp;[![issues - dice-telemetry](https://img.shields.io/github/issues/champagnearden/dice-telemetry)](https://github.com/champagnearden/dice-telemetry/issues)
&emsp;[![coffee](https://img.buymeacoffee.com/button-api/?text=Buy%20me%20a%20coffee&emoji=🗿&slug=champagnearden&button_colour=FF5F5F&font_colour=FFFFFF&font_family=Cookie&outline_colour=000000&coffee_colour=ffffff)](https://buymeacoffee.com/champagnearden "Buy me a coffee")

# dice-telemetry
This repository is a simple POC to integrate opentelemetry with log4j

## Prerequisites
- docker
- python *(optionnal)*

## How to run
### 1. OpenTelemetry Receiver
Clone this repository and go in `dice-telemetry/docker` folder then run:
```shell
docker compose up --build
```
**For MacOs and Windows users**, make sure that docker desktop is launched and correct permission have been granted !

Build can take up to one minute

This will automatically install and build the project

### 2. Generate traces
In a second terminal, go in the root folder of the project

In each case, the scripts will generate one request per second. We will let those run for about a minute to have some information to display in the [Jaeger](https://www.jaegertracing.io/) UI
#### 2.1. If you have python installed (better)
```shell
pip install -r requirements.txt 
python test_service/generate_trafic.py
```
#### 2.2. If you haven't python installed
```shell
while : ; do
  curl "http://localhost:8080/rolldice?player=jack"
  sleep 1
done
```

### 3. See the results
Open a browser and go to [localhost:16686](http://localhost:16686)
In the Service dropdown you should have `DiceTelemetry` or `TaskManager`, select it if not already selected.
click on `Find Traces`

And voil, 

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
