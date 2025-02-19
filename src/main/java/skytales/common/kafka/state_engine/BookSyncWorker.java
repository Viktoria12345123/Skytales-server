package skytales.common.kafka.state_engine;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import skytales.common.kafka.state_engine.dto.BookMessage;
import skytales.common.kafka.state_engine.model.Message;
import skytales.common.kafka.state_engine.model.UpdateType;

@Service
public class BookSyncWorker {

    private final KafkaTemplate<UpdateType, Message> kafkaTemplate;

    public BookSyncWorker(KafkaTemplate<UpdateType, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processBookUpdate(UpdateType type, BookMessage book) {
        Message message = new Message(type, book);

        switch (type) {
            case NEW_BOOK:
                kafkaTemplate.send("book-new", message);
                break;
            case REMOVE_BOOK:
                kafkaTemplate.send("book-remove", message);
                break;
            case STOCK_CHANGE:
                kafkaTemplate.send("book-stock-update", message);
                break;
            default:
                throw new IllegalArgumentException("Unknown update type");
        }
    }
}
