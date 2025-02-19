package skytales.common.kafka;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class KafkaConsumer {

    @KafkaListener(topics = "test-topic", groupId = "book-sync")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}
