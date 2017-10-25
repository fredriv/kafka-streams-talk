package com.schibsted.kafkastreamstalk.javaexamples;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class FilterTransformExample {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "FilterTransformExample");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "5000");

        KStreamBuilder builder = new KStreamBuilder();

        Serde<String> strings = Serdes.String();
        JsonNodeSerde json = new JsonNodeSerde();

        KStream<String, JsonNode> articles = builder.stream(strings, json, "Articles");

        KStream<String, String> bbcTitles = articles
                .filter((key, value) -> "bbc".equals(value.get("site").asText()))
                .mapValues(value -> value.get("title").asText());

        bbcTitles.to(strings, strings, "BBC-Titles");

        bbcTitles.print();

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                streams.close(10, TimeUnit.SECONDS))
        );
    }
}
