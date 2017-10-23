#!/bin/bash

cat src/test/resources/articles-reads.txt | \
  ~/opt/kafka/bin/kafka-console-producer.sh --broker-list localhost:29092 --topic ArticleReads --property "parse.key=true" --property "key.separator=:"
