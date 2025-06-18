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
python test_service/generate_trafic.py 5
```
`5` is the timeout in seconds, so here it will send a request every 5 seconds.
#### 2.2. If you haven't python installed
```shell
while : ; do
  curl "http://localhost:8080/rolldice?player=jack"
  sleep 1
done
```

### 3. See the results
#### 3.1. Jaeger
- Open a browser and go to [localhost:16686](http://localhost:16686)
- In the Service dropdown you should have `DiceTelemetry` or `TaskManager`, select it if not already selected.
- Click on `Find Traces`

And voila, all the requests to `rolldice` appears !

#### 3.2. Zipkin
- Open a browser and go to [localhost:9411/zipkin/](http://localhost:9411/zipkin/)
- Click on `RUN QUERY`
- Expand one line bu clicking on the arrow on the left
- To see the details about this request click on `SHOW`
- Explore and see the `dice.result` and `player.name` in the span named `rolldiceoperation`

#### 3.3. ElasticSearch APM *(Kibana)*
- Open a browser and go to [localhost:5601/app/apm](http://localhost:5601/app/apm)
- Click on your service
- Got to the `Transactions` tab
- Scroll down to `GET /rolldice` and click on it
- Click on `rollDiceOperation`
- You should see the player name under `labels.player_name` and the dice result under `numeric_labels.dice_result`

If you have executed the python script, you can see in the tags of each request the player name under `Tags` > `player.name` and the result he/she got under `Tags` > `dice.result`.

If not then the player name is set to jack by default but you can still view the dice result.
