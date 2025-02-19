package skytales.common.kafka.state_engine;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import skytales.common.kafka.state_engine.model.Message;


@Service
@EnableKafka
public class BookSyncConsumer {

    private final BookSyncWorker bookSyncWorker;

    public BookSyncConsumer(BookSyncWorker bookSyncWorker) {
        this.bookSyncWorker = bookSyncWorker;
    }

    @KafkaListener(topics = "book-updates", groupId = "book-sync")
    public void consumeBookUpdate(Message message) {

        bookSyncWorker.processBookUpdate(message.getUpdateType(), message.getBook());
        System.out.println("Ama kakvo stava tuka sega");
    }
}
