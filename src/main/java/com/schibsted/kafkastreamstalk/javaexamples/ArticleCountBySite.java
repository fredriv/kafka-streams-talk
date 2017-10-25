package com.schibsted.kafkastreamstalk.javaexamples;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ArticleCountBySite {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "ArticleCountBySite");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonNodeSerde.class);
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "5000");

        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, JsonNode> articles = builder.stream("Articles");

        KGroupedStream<String, JsonNode> grouped = articles
                .groupBy((key, value) -> value.get("site").asText());

        KTable<String, Long> articlesBySite = articles
                .groupBy((key, value) -> value.get("site").asText())
                .count();

        articlesBySite.toStream().print();

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                streams.close(10, TimeUnit.SECONDS))
        );
    }
}
