package com.schibsted.kafkastreamstalk

import java.lang
import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder, KTable, Windows}

object ArticleCountApplication {

  def main(args: Array[String]): Unit = {
    val config = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "articlecount-application")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092")
    config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "5000")

    val builder = new KStreamBuilder
    val articles: KStream[String, String] = builder.stream(Serdes.String(), Serdes.String(), "ArticlesReads")

    val counts: KTable[String, lang.Long] = articles
      .groupBy((key, value) => value)
      .count( "ArticlesReadsCounts")

    counts.to(Serdes.String(), Serdes.Long(), "ArticlesReadsCounts")

    val streams = new KafkaStreams(builder, config)
    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() =>
      streams.close(10, TimeUnit.SECONDS)
    ))
  }

}
