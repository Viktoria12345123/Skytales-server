package skytales.cart.events;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import skytales.cart.redis.sync.CartBatchSync;
import skytales.cart.service.CartService;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

import java.util.UUID;

@Service
@EnableKafka
public class CartEventConsumer {


    private final CartService cartService;
    private final CartBatchSync cartBatchSync;

    public CartEventConsumer(CartService cartService, CartBatchSync cartBatchSync) {
        this.cartService = cartService;
        this.cartBatchSync = cartBatchSync;
    }


    @KafkaListener(topics = "cart-checkout", groupId = "book-sync")
    public void handleCartCheckout(KafkaMessage<?> request) {
        UUID id = UUID.fromString(request.getData().toString());
        cartService.clearCart(id);
    }

    @KafkaListener(topics = "sync-db", groupId = "book-sync")
    public void handleCartSync(KafkaMessage<?> request) {
        System.out.println("Synchronisation process beginning...");
        cartBatchSync.syncCartsBatch();
    }

}
