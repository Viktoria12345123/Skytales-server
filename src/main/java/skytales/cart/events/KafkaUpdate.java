package skytales.cart.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import skytales.cart.dto.BookRequest;
import skytales.cart.dto.MessageRequest;
import skytales.cart.service.BookReferenceService;
import skytales.common.kafka.state_engine.model.Message;

@Service
public class KafkaUpdate {

    private final BookReferenceService bookReferenceService;

    public KafkaUpdate(BookReferenceService bookReferenceService) {
        this.bookReferenceService = bookReferenceService;
    }

    @KafkaListener(topics = "book-new", groupId = "book-sync")
    public void handleNewBook(Message message) {

        BookRequest bookRequest = new BookRequest(
                message.getBook().id(),
                message.getBook().title(),
                message.getBook().author(),
                message.getBook().genre(),
                message.getBook().coverImageUrl(),
                message.getBook().year(),
                message.getBook().price(),
                message.getBook().quantity()
        );


        bookReferenceService.addBookToState(bookRequest);
    }

    @KafkaListener(topics = "book-remove", groupId = "book-sync")
    public void handleRemoveBook(Message message) {

        BookRequest bookRequest = new BookRequest(
                message.getBook().id(),
                message.getBook().title(),
                message.getBook().author(),
                message.getBook().genre(),
                message.getBook().coverImageUrl(),
                message.getBook().year(),
                message.getBook().price(),
                message.getBook().quantity()
        );

        bookReferenceService.removeBookFromState(bookRequest);
    }

    @KafkaListener(topics = "book-stock-update", groupId = "book-sync")
    public void handleStockUpdate(BookRequest bookRequest) {
        bookReferenceService.updateBookStock(bookRequest);
    }

}
