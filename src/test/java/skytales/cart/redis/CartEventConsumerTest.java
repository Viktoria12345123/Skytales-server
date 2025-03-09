package skytales.cart.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import skytales.cart.events.CartEventConsumer;
import skytales.cart.redis.sync.CacheStartupSync;
import skytales.cart.redis.sync.CartBatchSync;
import skytales.cart.service.CartService;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEventConsumerTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartBatchSync cartBatchSync;

    @Mock
    private CacheStartupSync cacheStartupSync;

    @InjectMocks
    private CartEventConsumer cartEventConsumer;

    private KafkaMessage<?> kafkaMessage;

    @BeforeEach
    void setUp() {
        kafkaMessage = new KafkaMessage<>();
    }

    @Test
    void handleCartCheckout() {
        UUID cartId = UUID.randomUUID();
        kafkaMessage.setData(cartId.toString());

        cartEventConsumer.handleCartCheckout(kafkaMessage);

        verify(cartService, times(1)).clearCart(cartId);
    }

    @Test
    void handleCartSync() {
        kafkaMessage.setData("cart1,cart2,cart3");

        cartEventConsumer.handleCartSync(kafkaMessage);

        verify(cartBatchSync, times(1)).syncCartsBatch();
    }

    @Test
    void handleCacheSync() {
        cartEventConsumer.handleCacheSync(kafkaMessage);

        verify(cacheStartupSync, times(1)).syncCache();
    }
}