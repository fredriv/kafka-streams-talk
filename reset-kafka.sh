#!/bin/bash

docker-compose -f docker/kafka-mac.yaml down
docker-compose -f docker/kafka-mac.yaml up
