package com.schibsted.kafkastreamstalk

import java.lang
import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder, KTable}
import org.apache.kafka.streams.{KafkaStreams, KeyValue, StreamsConfig}

object ArticlesApplication {

  def main(args: Array[String]): Unit = {
    import scala.collection.JavaConverters._

    val config = Map(
      StreamsConfig.APPLICATION_ID_CONFIG -> "ArticlesApp",
      StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:29092",
      StreamsConfig.COMMIT_INTERVAL_MS_CONFIG -> "5000"
    )

    val builder = new KStreamBuilder
    val strings = Serdes.String()
    val json = new JsonNodeSerde

    val articles: KStream[String, JsonNode] = builder.stream(strings, json, "Articles")

    val bbcTitles = articles
      .filter((key, value) => value.get("site").asText == "bbc")
      .mapValues[String](value => value.get("title").asText)

    bbcTitles.to(strings, strings, "BBC-Titles")

    val streams = new KafkaStreams(builder, new StreamsConfig(config.asJava))
    streams.cleanUp()
    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() =>
      streams.close(10, TimeUnit.SECONDS)
    ))
  }

}
