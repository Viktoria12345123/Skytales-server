package skytales.common.kafka.state_engine.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import skytales.cart.dto.BookRequest;
import skytales.common.kafka.state_engine.dto.BookMessage;
import skytales.common.kafka.state_engine.utils.KafkaMessage;
import skytales.common.kafka.state_engine.utils.MessageDeserializer;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MessageDeserializerTest {

    private MessageDeserializer messageDeserializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        messageDeserializer = new MessageDeserializer();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testDeserialize_BookNew() throws Exception {
        BookRequest bookRequest = new BookRequest(UUID.randomUUID(), "Test Book", "Fiction", "Author", "http://example.com/banner.jpg", 2023, new BigDecimal("19.99"), 100);
        KafkaMessage<BookRequest> kafkaMessage = new KafkaMessage<>(bookRequest);
        kafkaMessage.setType("book-new");

        byte[] data = objectMapper.writeValueAsBytes(kafkaMessage);

        KafkaMessage<?> result = messageDeserializer.deserialize("book-new", data);

        assertInstanceOf(BookRequest.class, result.getData());
        BookRequest resultData = (BookRequest) result.getData();
        assertEquals(bookRequest.id(), resultData.id());
        assertEquals(bookRequest.title(), resultData.title());
        assertEquals(bookRequest.genre(), resultData.genre());
        assertEquals(bookRequest.author(), resultData.author());
        assertEquals(bookRequest.coverImageUrl(), resultData.coverImageUrl());
        assertEquals(bookRequest.year(), resultData.year());
        assertEquals(bookRequest.price(), resultData.price());
        assertEquals(bookRequest.quantity(), resultData.quantity());
    }

    @Test
    public void testDeserialize_BookUpdates() throws Exception {
        BookMessage bookMessage = new BookMessage(UUID.randomUUID(), "Test Book", "Fiction", "Author", "http://example.com/banner.jpg", 2023, new BigDecimal("19.99"), 100);
        KafkaMessage<BookMessage> kafkaMessage = new KafkaMessage<>(bookMessage);
        kafkaMessage.setType("book-updates");

        byte[] data = objectMapper.writeValueAsBytes(kafkaMessage);

        KafkaMessage<?> result = messageDeserializer.deserialize("book-updates", data);

        assertInstanceOf(BookMessage.class, result.getData());
        BookMessage resultData = (BookMessage) result.getData();
        assertEquals(bookMessage.id(), resultData.id());
        assertEquals(bookMessage.title(), resultData.title());
        assertEquals(bookMessage.genre(), resultData.genre());
        assertEquals(bookMessage.author(), resultData.author());
        assertEquals(bookMessage.coverImageUrl(), resultData.coverImageUrl());
        assertEquals(bookMessage.year(), resultData.year());
        assertEquals(bookMessage.price(), resultData.price());
        assertEquals(bookMessage.quantity(), resultData.quantity());
    }

    @Test
    public void testDeserialize_InvalidData() {
        byte[] data = "invalid data".getBytes();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            messageDeserializer.deserialize("book-new", data);
        });

        assertEquals("Failed to deserialize message", exception.getMessage());
    }
}
