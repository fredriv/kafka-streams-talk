package com.schibsted.kafkastreamstalk

import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object ArticlesApplication {

  def main(args: Array[String]): Unit = {
    import scala.collection.JavaConverters._

    val config = Map(
      StreamsConfig.APPLICATION_ID_CONFIG -> "ArticlesApp",
      StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:29092"
    )

    val builder = new KStreamBuilder
    val articles: KStream[String, JsonNode] = builder.stream(Serdes.String(), new JsonNodeSerde, "Articles")

    articles.filter((key, value) => value.get("site").asText == "bbc").print()
    val bbcTitles = articles
      .filter((key, value) => value.get("site").asText == "bbc")
      .mapValues[String](value => value.get("title").asText)

    bbcTitles.to(Serdes.String(), Serdes.String(), "BBC-Titles")

    val streams = new KafkaStreams(builder, new StreamsConfig(config.asJava))
    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() =>
      streams.close(10, TimeUnit.SECONDS)
    ))
  }

}
