package com.schibsted.kafkastreamstalk

import java.io.ByteArrayInputStream
import java.util

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class JsonNodeSerde extends Serde[JsonNode] {

  private val mapper = new ObjectMapper()

  override val deserializer = new Deserializer[JsonNode] {
    override def deserialize(topic: String, data: Array[Byte]): JsonNode = mapper.readTree(data)
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
    override def close(): Unit = {}
  }

  override val serializer = new Serializer[JsonNode] {
    override def serialize(topic: String, data: JsonNode): Array[Byte] = mapper.writeValueAsBytes(data)
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
    override def close(): Unit = {}
  }

  override def configure(configs: util.Map[String, _], isKey: Boolean) = {}
  override def close() = {}
}
