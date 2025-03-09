package skytales.cart.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import skytales.cart.model.BookItemReference;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    private RedisService redisService;

    private BookItemReference bookItemReference;
    private Set<BookItemReference> bookItemReferences;
    private String key;
    private String versionKey;

    @BeforeEach
    void setUp() {
        bookItemReference = new BookItemReference();
        bookItemReferences = new HashSet<>(Collections.singletonList(bookItemReference));
        key = "testKey";
        versionKey = "testVersionKey";

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void get() {
        when(valueOperations.get(key)).thenReturn(bookItemReferences);

        Set<BookItemReference> result = redisService.get(key);

        assertEquals(bookItemReferences, result);
    }

    @Test
    void set() {
        redisService.set(key, bookItemReferences);

        verify(valueOperations, times(1)).set(key, bookItemReferences);
        verify(redisTemplate, times(1)).expire(key, redisService.TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Test
    void getTerm() {
        when(zSetOperations.score(RedisService.CARTS_ZSET_KEY, versionKey)).thenReturn(1.0);

        int term = redisService.getTerm(versionKey);

        assertEquals(1, term);
    }

    @Test
    void incrBy() {
        when(zSetOperations.score(RedisService.CARTS_ZSET_KEY, versionKey)).thenReturn(null);

        redisService.incrBy(versionKey);

        verify(zSetOperations, times(1)).add(RedisService.CARTS_ZSET_KEY, versionKey, 0);
        verify(zSetOperations, times(1)).incrementScore(RedisService.CARTS_ZSET_KEY, versionKey, 1);
    }

    @Test
    void reset() {
        redisService.reset(versionKey);

        verify(zSetOperations, times(1)).add(RedisService.CARTS_ZSET_KEY, versionKey, 0);
    }

    @Test
    void delete() {
        redisService.delete(key);

        verify(redisTemplate, times(1)).delete(key);
    }
}