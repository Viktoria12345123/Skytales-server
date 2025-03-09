package skytales.cart.sync;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import skytales.cart.model.BookItemReference;
import skytales.cart.model.Cart;
import skytales.cart.redis.RedisService;
import skytales.cart.redis.sync.CacheStartupSync;
import skytales.cart.repository.CartRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheStartupSyncTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private CacheStartupSync cacheStartupSync;

    private Cart cart;
    private Set<BookItemReference> cartBooks;
    private String cartKey;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(UUID.randomUUID());
        cartBooks = Collections.singleton(new BookItemReference());
        cart.setBooks(cartBooks);
        cartKey = "shopping_cart:" + cart.getId();
    }

    @Test
    void syncCache_Success() {
        when(cartRepository.findAll()).thenReturn(Collections.singletonList(cart));
        when(redisService.get(cartKey)).thenReturn(null);

        cacheStartupSync.syncCache();

        verify(redisService, times(1)).set(cartKey, cartBooks);
    }

    @Test
    void syncCache_CachedCartEqualsCartBooks() {
        when(cartRepository.findAll()).thenReturn(Collections.singletonList(cart));
        when(redisService.get(cartKey)).thenReturn(cartBooks);

        cacheStartupSync.syncCache();

        verify(redisService, never()).set(anyString(), anySet());
    }

    @Test
    void syncCache_CachedCartNotEqualsCartBooks() {
        when(cartRepository.findAll()).thenReturn(Collections.singletonList(cart));
        when(redisService.get(cartKey)).thenReturn(Collections.emptySet());

        cacheStartupSync.syncCache();

        verify(redisService, times(1)).set(cartKey, cartBooks);
    }

    @Test
    void syncCache_Exception() {
        when(cartRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        cacheStartupSync.syncCache();

        verify(redisService, never()).set(anyString(), anySet());
    }
}