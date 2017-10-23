package com.schibsted.kafkastreamstalk

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object ArticlePrintApplication {

  def main(args: Array[String]): Unit = {
    val config = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "articleprint-application")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092")

    val builder = new KStreamBuilder
    val articles: KStream[String, String] = builder.stream(Serdes.String(), Serdes.String(), "ArticlesReads")

    articles.print()

    val streams = new KafkaStreams(builder, config)
    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() =>
      streams.close(10, TimeUnit.SECONDS)
    ))
  }

}
