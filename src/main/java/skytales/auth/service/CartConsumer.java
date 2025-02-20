package skytales.auth.service;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

@Service
@EnableKafka
public class CartConsumer {

    private final UserService userService;

    public CartConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = "cartCreated", groupId = "book-sync")
    public void handleUserCreated(KafkaMessage<?> request) {

        String cartId = request.getData().toString().split(" ")[0];
        String userId = request.getData().toString().split(" ")[1];
        userService.setCartToUser(cartId, userId);

    }
}
