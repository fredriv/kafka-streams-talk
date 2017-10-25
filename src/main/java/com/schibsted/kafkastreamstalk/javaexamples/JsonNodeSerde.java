package com.schibsted.kafkastreamstalk.javaexamples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class JsonNodeSerde implements Serde<JsonNode> {

    private ObjectMapper mapper = new ObjectMapper();

    private Serializer<JsonNode> serializer = new JsonNodeSerializer();
    private Deserializer<JsonNode> deserializer = new JsonNodeDeserializer();

    @Override
    public Serializer<JsonNode> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<JsonNode> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    class JsonNodeSerializer implements Serializer<JsonNode> {
        @Override
        public byte[] serialize(String topic, JsonNode data) {
            try {
                return mapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                // TODO logging
                return null;
            }
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            /* Do nothing */
        }

        @Override
        public void close() {
            /* Do nothing */
        }
    }

    class JsonNodeDeserializer implements Deserializer<JsonNode> {
        @Override
        public JsonNode deserialize(String topic, byte[] data) {
            if (data == null) {
                return null;
            }
            try {
                return mapper.readTree(data);
            } catch (IOException e) {
                // TODO logging
                return null;
            }
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            /* Do nothing */
        }

        @Override
        public void close() {
            /* Do nothing */
        }
    }
}

