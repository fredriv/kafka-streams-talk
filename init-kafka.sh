#!/bin/bash

docker-compose -f docker/kafka-mac.yaml down
docker-compose -f docker/kafka-mac.yaml up -d

echo Waiting 10 seconds for brokers to come up

for (( i=1; i<=10; i++ ))
do
  sleep 1
  echo .
done

~/opt/kafka/bin/kafka-topics.sh --create --topic ArticlesReads --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181
~/opt/kafka/bin/kafka-topics.sh --create --topic ArticlesReadsCounts --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181
