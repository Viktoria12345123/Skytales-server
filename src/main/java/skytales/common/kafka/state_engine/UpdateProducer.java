package skytales.common.kafka.state_engine;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import skytales.common.kafka.state_engine.dto.BookMessage;
import skytales.common.kafka.state_engine.model.UpdateType;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UpdateProducer {

    private final KafkaTemplate<String, KafkaMessage<?>> kafkaTemplate;

    public UpdateProducer(KafkaTemplate<String, KafkaMessage<?>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookUpdate(UpdateType updateType, Object book) {
        try {
            BookMessage bookMessage = createBookMessageFromBook(book);

            KafkaMessage<BookMessage> request = new KafkaMessage<>(bookMessage);
            request.setType(updateType.toString());

            kafkaTemplate.send("book-updates", request);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void clearCartForUser(String id) {
        KafkaMessage<String> request = new KafkaMessage<>(id);
        kafkaTemplate.send("cart-checkout", request);
    }

    public void sendBatchSyncRequest() {
        KafkaMessage<String> request = new KafkaMessage<>("");
        kafkaTemplate.send("sync-db", request);
    }

    public void sendRedisSyncRequest() {
        KafkaMessage<String> request = new KafkaMessage<>("");
        kafkaTemplate.send("sync-redis-latestInf", request);
    }

    private BookMessage createBookMessageFromBook(Object book) throws NoSuchFieldException, IllegalAccessException {
        Field idField = book.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        UUID id = (UUID) idField.get(book);

        Field titleField = book.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        String title = (String) titleField.get(book);

        Field genreField = book.getClass().getDeclaredField("genre");
        genreField.setAccessible(true);
        String genre = (String) genreField.get(book);

        Field authorField = book.getClass().getDeclaredField("author");
        authorField.setAccessible(true);
        String author = (String) authorField.get(book);

        Field coverImageUrlField = book.getClass().getDeclaredField("coverImageUrl");
        coverImageUrlField.setAccessible(true);
        String coverImageUrl = (String) coverImageUrlField.get(book);

        Field yearField = book.getClass().getDeclaredField("year");
        yearField.setAccessible(true);
        int year = (int) yearField.get(book);

        Field priceField = book.getClass().getDeclaredField("price");
        priceField.setAccessible(true);
        BigDecimal price = (BigDecimal) priceField.get(book);

        Field quantityField = book.getClass().getDeclaredField("quantity");
        quantityField.setAccessible(true);
        int quantity = (int) quantityField.get(book);

        return new BookMessage(id, title, genre, author, coverImageUrl, year, price, quantity);
    }

}