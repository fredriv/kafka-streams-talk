package com.schibsted.kafkastreamstalk.javaexamples;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ReadsBySiteAndCountry {
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
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "ReadsBySiteAndCountry");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonNodeSerde.class);
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "5000");

        KStreamBuilder builder = new KStreamBuilder();

        Serde<String> strings = Serdes.String();
        Serde<Long> longs = Serdes.Long();

        KTable<String, JsonNode> articles = builder.table("Articles");
        KTable<String, JsonNode> users = builder.table("Users");
        KStream<String, String> articleReads = builder.stream(strings, strings, "ArticleReads");

        KeyValueMapper<String, UserRead, KeyValue<String, Long>> siteCountryMapper =
                (articleId, userRead) -> {
                    String site = userRead.article.get("site").asText();
                    String country = userRead.user.get("country").asText();
                    return KeyValue.pair(site + "-" + country, 1L);
                };

        KTable<String, Long> readsBySiteAndCountry = articleReads
                .join(users, (articleId, user) -> new UserRead(user, articleId, null), strings, strings)
                .map((key, value) -> KeyValue.pair(value.articleId, value.user))
                .join(articles, (user, article) -> new UserRead(user, null, article))
                .map(siteCountryMapper)
                .groupByKey(strings, longs)
                .count();

        readsBySiteAndCountry.toStream().print();

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                streams.close(10, TimeUnit.SECONDS))
        );
    }
}
