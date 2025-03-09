package skytales.common.kafka.state_engine.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;

import skytales.common.kafka.state_engine.dto.BookMessage;


public class MessageDeserializer implements Deserializer<KafkaMessage<?>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public KafkaMessage<?> deserialize(String topic, byte[] data) {
        try {
            KafkaMessage<?> kafkaMessage = objectMapper.readValue(data, KafkaMessage.class);
            Class<?> dataClass = getClassForType(topic);

            Object dataObject = objectMapper.readValue(objectMapper.writeValueAsBytes(kafkaMessage.getData()), dataClass);
            kafkaMessage.setData(dataObject);

            return kafkaMessage;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }


    private Class<?> getClassForType(String type) {
        return switch (type) {
            case "book-new", "book-stock-update", "book-remove", "book-updates" -> BookMessage.class;
            default -> String.class;
        };
    }
}





