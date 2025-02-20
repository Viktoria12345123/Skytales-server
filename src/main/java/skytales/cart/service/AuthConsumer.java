package skytales.cart.service;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

import java.util.UUID;

@Service
@EnableKafka
public class AuthConsumer {


    private final CartService cartService;

    public AuthConsumer(CartService cartService) {
        this.cartService = cartService;
    }

    @KafkaListener(topics = "userCreated", groupId = "book-sync")
    public void handleUserCreated(KafkaMessage<?> request) {

        UUID id = UUID.fromString(request.getData().toString());
        cartService.createCartForUser(id);
    }
}
