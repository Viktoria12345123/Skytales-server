package skytales.cart.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import skytales.cart.dto.BookRequest;
import skytales.cart.service.BookReferenceService;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

@Service
public class ModelChangeConsumer {

    private final BookReferenceService bookReferenceService;

    public ModelChangeConsumer(BookReferenceService bookReferenceService) {
        this.bookReferenceService = bookReferenceService;
    }

    @KafkaListener(topics = "book-new", groupId = "book-sync")
    public void handleNewBook(KafkaMessage<?> message) {

        BookRequest bookRequest = (BookRequest) message.getData();
        bookReferenceService.addBookToState(bookRequest);
    }

    @KafkaListener(topics = "book-remove", groupId = "book-sync")
    public void handleRemoveBook(KafkaMessage<BookRequest> message) {

        BookRequest bookRequest = (BookRequest) message.getData();
        bookReferenceService.removeBookFromState(bookRequest);
    }

    @KafkaListener(topics = "book-stock-update", groupId = "book-sync")
    public void handleStockUpdate(BookRequest bookRequest) {
        bookReferenceService.updateBookStock(bookRequest);
    }

}
