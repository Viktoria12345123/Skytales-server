package skytales.common.kafka.state_engine.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import skytales.common.kafka.state_engine.dto.BookMessage;
import skytales.common.kafka.state_engine.utils.KafkaMessage;
import skytales.common.kafka.state_engine.utils.MessageSerializer;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MessageSerializerTest {

    private MessageSerializer messageSerializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        messageSerializer = new MessageSerializer();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSerialize_BookMessage() throws Exception {
        BookMessage bookMessage = new BookMessage(
                UUID.randomUUID(),
                "Test Book",
                "Fiction",
                "Author",
                "http://example.com/banner.jpg",
                2023,
                new BigDecimal("19.99"),
                100
        );
        KafkaMessage<BookMessage> kafkaMessage = new KafkaMessage<>(bookMessage);
        kafkaMessage.setType("book-new");

        byte[] result = messageSerializer.serialize("book-new", kafkaMessage);

        byte[] expected = objectMapper.writeValueAsBytes(kafkaMessage);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testSerialize_InvalidData() {
        KafkaMessage<?> kafkaMessage = new KafkaMessage<>(new Object());
        kafkaMessage.setType("invalid");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            messageSerializer.serialize("invalid", kafkaMessage);
        });

        assertEquals("Failed to serialize message", exception.getMessage());
    }
}
