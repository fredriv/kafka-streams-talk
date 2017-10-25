package com.schibsted.kafkastreamstalk.javaexamples;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ReadsByCountry {
    static class UserRead {
        public final JsonNode user;
        public final String articleId;
        public final JsonNode article;
        public UserRead(JsonNode user, String articleId, JsonNode article) {
            this.user = user;
            this.articleId = articleId;
            this.article = article;
        }
    }

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "ReadsByCountry");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "5000");

        KStreamBuilder builder = new KStreamBuilder();

        Serde<String> strings = Serdes.String();
        Serde<Long> longs = Serdes.Long();

        JsonNodeSerde json = new JsonNodeSerde();

        KTable<String, JsonNode> users = builder.table(strings, json, "Users");
        KStream<String, String> articleReads = builder.stream(strings, strings, "ArticleReads");

        KTable<String, Long> readsByCountry = articleReads
                .join(users, (articleId, user) -> user, strings, strings)
                .groupBy((key, value) -> value.get("country").asText(), strings, json)
                .count();

        readsByCountry.toStream().print();

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                streams.close(10, TimeUnit.SECONDS))
        );
    }
}
