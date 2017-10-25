package com.schibsted.kafkastreamstalk.scalaexamples

import java.lang
import java.util.Properties
import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder, KTable}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object ArticleCountApplication {

  def main(args: Array[String]): Unit = {
    val config = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "articlecount-application")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092")
    config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "5000")

    val strings = Serdes.String()
    val json = new JsonNodeSerde

    val builder = new KStreamBuilder
    val articles: KStream[String, JsonNode] = builder.stream(strings, json, "Articles")

    val articlesBySite: KTable[String, lang.Long] = articles
      .groupBy((key: String, value: JsonNode) => value.get("site").asText, strings, json)
      .count()

    articlesBySite.toStream().print()

    val users = builder.table(strings, json, "Users")
    val articleReads = builder.stream(strings, strings, "ArticleReads")

    val readsPerCountry: KStream[String, String] = articleReads
      .leftJoin(users, (articleId: String, user: JsonNode) => user.get("country").asText, strings, strings)

    val counts = readsPerCountry
      .groupBy((key: String, value: String) => value, strings, strings)
      .count()

    //    readsPerCountry
    //      .map[String, String]((key, value) => KeyValue.pair(value, key))
    //      .groupByKey(strings, strings)
    //      .count()

    counts.toStream().print()

    val streams = new KafkaStreams(builder, config)
    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() =>
      streams.close(10, TimeUnit.SECONDS)
    ))
  }

}
