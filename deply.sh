#!/bin/bash

mvn clean package

scp target/onceio-vertx-demo-0.0.1-fat.jar root@onceio.top:webapps/

ssh root@onceio.top 'cd webapps;bash boot.sh'
