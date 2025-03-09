package skytales.cart.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import skytales.cart.dto.BookRequest;
import skytales.cart.events.ModelChangeConsumer;
import skytales.cart.service.BookReferenceService;
import skytales.common.kafka.state_engine.dto.BookMessage;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelChangeConsumerTest {

    @Mock
    private BookReferenceService bookReferenceService;

    @InjectMocks
    private ModelChangeConsumer modelChangeConsumer;

    private KafkaMessage<BookRequest> kafkaMessage;
    private BookRequest bookRequest;
    private BookMessage bookMessage;

    @BeforeEach
    void setUp() {
        bookRequest = new BookRequest(UUID.randomUUID(), "Title", "Fantasy", "Author", "http://example.com/cover.jpg", 2000, BigDecimal.valueOf(1), 30);
        bookMessage= new BookMessage(UUID.randomUUID(), "Title", "Fantasy", "Author", "http://example.com/cover.jpg", 2000, BigDecimal.valueOf(1), 30);
        kafkaMessage = new KafkaMessage<>();
        kafkaMessage.setData(bookRequest);
    }

//    @Test
//    void handleNewBook() {
//        modelChangeConsumer.handleNewBook(kafkaMessage);
//
//        verify(bookReferenceService, times(1)).addBookToState(bookRequest);
//    }
//
//    @Test
//    void handleRemoveBook() {
//        modelChangeConsumer.handleRemoveBook(kafkaMessage);
//
//        verify(bookReferenceService, times(1)).removeBookFromState(bookRequest);
//    }
//
//    @Test
//    void handleStockUpdate() {
//        modelChangeConsumer.handleStockUpdate(bookMessage);
//        verify(bookReferenceService, times(1)).updateBookStock(bookMessage);
//    }
}
