package skytales.common.kafka.state_engine.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import skytales.cart.dto.BookRequest;
import skytales.common.kafka.state_engine.dto.BookMessage;
import skytales.common.kafka.state_engine.model.Message;

public class MessageDeserializer implements Deserializer<KafkaMessage<?>> {

//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public Message deserialize(String topic, byte[] data) {
//        try {
//            return objectMapper.readValue(data, Message.class);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to deserialize message", e);
//        }
//    }

//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public KafkaMessage<?> deserialize(String topic, byte[] data) {
//        try {
//            return objectMapper.readValue(data, KafkaMessage.class);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to deserialize message", e);
//        }

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public KafkaMessage<?> deserialize(String topic, byte[] data) {
            try {
                KafkaMessage<?> kafkaMessage = objectMapper.readValue(data, KafkaMessage.class);
                Class<?> dataClass = getClassForType(topic);

                if (dataClass != null) {

                    Object dataObject = objectMapper.readValue(objectMapper.writeValueAsBytes(kafkaMessage.getData()), dataClass);
                    kafkaMessage.setData(dataObject);

                } else {
                    throw new RuntimeException("Unknown data type for KafkaMessage");
                }

                return kafkaMessage;
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize message", e);
            }
        }


        private Class<?> getClassForType(String type) {
            return switch (type) {
                case "book-new", "book-stock-update", "book-remove" -> BookRequest.class;
                case "book-updates" -> BookMessage.class;
                default -> String.class;
            };
        }
    }





