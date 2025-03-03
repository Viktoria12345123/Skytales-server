package skytales.cart.events;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import skytales.cart.redis.sync.CacheStartupSync;
import skytales.cart.redis.sync.CartBatchSync;
import skytales.cart.service.CartService;
import skytales.common.kafka.state_engine.utils.KafkaMessage;
import java.util.UUID;

@Service
@EnableKafka
public class CartEventConsumer {


    private final CartService cartService;
    private final CartBatchSync cartBatchSync;
    private final CacheStartupSync cacheStartupSync;

    public CartEventConsumer(CartService cartService, CartBatchSync cartBatchSync, CacheStartupSync cacheStartupSync) {
        this.cartService = cartService;
        this.cartBatchSync = cartBatchSync;
        this.cacheStartupSync = cacheStartupSync;
    }


    @KafkaListener(topics = "cart-checkout", groupId = "book-sync")
    public void handleCartCheckout(KafkaMessage<?> request) {
        UUID id = UUID.fromString(request.getData().toString());
        cartService.clearCart(id);
    }

    @KafkaListener(topics = "sync-db", groupId = "book-sync")
    public void handleCartSync(KafkaMessage<?> request) {

        String cartIdString = String.valueOf(request);
        String[] cartIds = cartIdString.split(",");
        cartBatchSync.syncCartsBatch();
    }


    @KafkaListener(topics ="sync-redis-latestInf", groupId = "book-sync")
    public void handleCacheSync(KafkaMessage<?> request) {
        cacheStartupSync.syncCache();
    }
}
