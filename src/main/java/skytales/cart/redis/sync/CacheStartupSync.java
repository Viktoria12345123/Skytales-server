package skytales.cart.redis.sync;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import skytales.cart.model.BookItemReference;
import skytales.cart.model.Cart;
import skytales.cart.redis.RedisService;
import skytales.cart.repository.CartRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@EnableKafka
public class CacheStartupSync {

    private final CartRepository cartRepository;
    private final RedisService redisService;

    @Autowired
    public  CacheStartupSync(CartRepository cartRepository, RedisService redisService) {

        this.cartRepository = cartRepository;
        this.redisService = redisService;
    }

    public void syncCache() {
        System.out.println("Retrieving state from database to update Redis...");

        try {
            List<Cart> allCarts = cartRepository.findAll();

            for (Cart cart : allCarts) {
                String cartKey = "shopping_cart:" + cart.getId();
                Set<BookItemReference> cartBooks = cart.getBooks();

                Set<BookItemReference> cachedCart = redisService.get(cartKey);

                if (cachedCart == null || !cachedCart.equals(cartBooks)) {
                    redisService.set(cartKey, cartBooks);
                }
            }
        } catch (Exception e) {
            System.err.println("Error during batch synchronization: " + e.getMessage());
        }

        System.out.println("Batch synchronization finished.");
    }
}
