package skytales.common.kafka.state_engine.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import skytales.common.kafka.state_engine.model.Message;

public class MessageDeserializer implements Deserializer<Message> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Message.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
}

