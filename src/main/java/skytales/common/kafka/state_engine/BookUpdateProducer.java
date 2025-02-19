package skytales.common.kafka.state_engine;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import skytales.common.kafka.state_engine.dto.BookMessage;
import skytales.common.kafka.state_engine.model.Message;
import skytales.common.kafka.state_engine.model.UpdateType;
import skytales.library.model.Book;

@Service
public class BookUpdateProducer {

    private final KafkaTemplate<String, Message> kafkaTemplate;

    public BookUpdateProducer(KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookUpdate(UpdateType updateType , Book book) {

        BookMessage bookMessage = new BookMessage(book.getId(),
                book.getTitle(),
                book.getGenre(),
                book.getAuthor(),
                book.getBannerImageUrl(),
                book.getCoverImageUrl(),
                book.getYear(),
                book.getPrice(),
                book.getDescription(),
                book.getQuantity()
        );

        Message message = new Message(updateType, bookMessage);
        kafkaTemplate.send("book-updates", message);
    }
}