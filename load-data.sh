#!/bin/bash

createTopic() {
  docker run --net=host --rm confluentinc/cp-kafka:latest \
    kafka-topics --create --topic $1 --partitions 3 --replication-factor 1 --if-not-exists --zookeeper localhost:32181
}

toKafkaTopic() {
  ~/opt/kafka/bin/kafka-console-producer.sh --broker-list localhost:29092 --topic $1 --property "parse.key=true" --property "key.separator=:"
}

createTopic Articles
createTopic BBC-Titles
createTopic Users
createTopic ArticleReads

cat src/test/resources/articles.txt | toKafkaTopic Articles
cat src/test/resources/users.txt | toKafkaTopic Users
cat src/test/resources/article-reads.txt | toKafkaTopic ArticleReads
