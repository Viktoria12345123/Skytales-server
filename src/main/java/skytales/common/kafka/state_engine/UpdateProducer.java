package skytales.common.kafka.state_engine;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import skytales.common.kafka.state_engine.dto.BookMessage;
import skytales.common.kafka.state_engine.model.UpdateType;
import skytales.common.kafka.state_engine.utils.KafkaMessage;
import skytales.library.model.Book;

@Service
public class UpdateProducer {

    private final KafkaTemplate<String, KafkaMessage<?>> kafkaTemplate;

    public UpdateProducer(KafkaTemplate<String, KafkaMessage<?>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendBookUpdate(UpdateType updateType , Book book) {

        BookMessage bookMessage = new BookMessage(book.getId(),
                book.getTitle(),
                book.getGenre(),
                book.getAuthor(),
                book.getCoverImageUrl(),
                book.getYear(),
                book.getPrice(),
                book.getQuantity()
        );

        KafkaMessage<BookMessage> request = new KafkaMessage<>( bookMessage );
        request.setType(updateType.toString());

        kafkaTemplate.send("book-updates",  request);
    }

    public void sendCreateCartRequest(String id) {

        KafkaMessage<String> request = new KafkaMessage<>(id);
        kafkaTemplate.send("userCreated", request);
    }

    public void clearCartForUser(String id) {
        KafkaMessage<String> request = new KafkaMessage<>(id);
        kafkaTemplate.send("cart-checkout", request);
    }
}