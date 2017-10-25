package com.schibsted.kafkastreamstalk.javaexamples;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class HelloKafkaStreams {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "HelloKafkaStreams");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");

        KStreamBuilder builder = new KStreamBuilder();

        Serde<String> strings = Serdes.String();
        JsonNodeSerde json = new JsonNodeSerde();

        KStream<String, JsonNode> articles = builder.stream(strings, json, "Articles");
        articles.print();

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                streams.close(10, TimeUnit.SECONDS))
        );
    }
}
