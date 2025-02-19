package skytales.common.kafka.state_engine.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import skytales.common.kafka.state_engine.model.Message;

public class MessageSerializer implements Serializer<Message> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Message message) {
        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }
}
