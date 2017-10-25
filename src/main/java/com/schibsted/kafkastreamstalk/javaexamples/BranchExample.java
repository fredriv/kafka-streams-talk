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

public class BranchExample {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "FilterTransformExample");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonNodeSerde.class);
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "5000");

        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, JsonNode> articles = builder.stream("Articles");

        KStream<String, JsonNode>[] articlesBySite = articles.branch(
                (key, value) -> "bbc".equals(value.get("site").asText()),
                (key, value) -> "cnn".equals(value.get("site").asText()),
                (key, value) -> "foxnews".equals(value.get("site").asText()),
                (key, value) -> true
        );

        articlesBySite[0].to("BBC-Articles");
        articlesBySite[1].to("CNN-Articles");
        articlesBySite[2].to("FoxNews-Articles");
        articlesBySite[3].to("Unknown-Articles");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                streams.close(10, TimeUnit.SECONDS))
        );
    }
}
