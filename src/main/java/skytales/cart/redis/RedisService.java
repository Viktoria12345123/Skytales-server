package skytales.cart.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import skytales.cart.model.BookItemReference;

import java.util.HashSet;
import java.util.Set;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
   private final ObjectMapper objectMapper;


    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper jacksonObjectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();


        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jacksonSerializer);
    }


    public Set<BookItemReference> get(String key) {

        Object data = redisTemplate.opsForValue().get(key);
        if (data == null) return new HashSet<>();

        return objectMapper.convertValue(data, new TypeReference<Set<BookItemReference>>() {});
    }

    public int getTerm(String key) {

        Object data = redisTemplate.opsForValue().get(key);
        if (data == null) return 0;

        return (Integer)data;
    }



    public void set(String key, Set<BookItemReference> value) {
        redisTemplate.opsForValue().set(key, value);
    }



    private String serializeBookItem(BookItemReference bookItemReference) {
        try {
            return objectMapper.writeValueAsString(bookItemReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing BookItemReference", e);
        }
    }

    public void incrBy(String versionKey) {
        redisTemplate.opsForValue().increment(versionKey, 1);
    }
}

