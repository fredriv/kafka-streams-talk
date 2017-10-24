#!/bin/bash

docker run --net=host --rm confluentinc/cp-kafka:latest \
  kafka-topics --create --topic Articles --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181
docker run --net=host --rm confluentinc/cp-kafka:latest \
  kafka-topics --create --topic ArticlesReads --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181
docker run --net=host --rm confluentinc/cp-kafka:latest \
  kafka-topics --create --topic ArticlesReadsCounts --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181

cat src/test/resources/articles.txt | \
  ~/opt/kafka/bin/kafka-console-producer.sh --broker-list localhost:29092 --topic Articles --property "parse.key=true" --property "key.separator=:"

cat src/test/resources/articles-reads.txt | \
  ~/opt/kafka/bin/kafka-console-producer.sh --broker-list localhost:29092 --topic ArticlesReads --property "parse.key=true" --property "key.separator=:"
