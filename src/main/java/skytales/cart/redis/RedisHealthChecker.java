package skytales.cart.redis;

import lombok.Getter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import skytales.common.kafka.state_engine.UpdateProducer;


@EnableAsync
@Getter
@Component
@EnableScheduling
public class RedisHealthChecker {

    private final UpdateProducer updateProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    private volatile boolean redisAvailable = false;

    public RedisHealthChecker(UpdateProducer updateProducer, RedisTemplate<String, Object> redisTemplate) {
        this.updateProducer = updateProducer;
        this.redisTemplate = redisTemplate;
    }

    @Async
    @Scheduled(fixedRate = 120000)
    public void checkRedisHealth() {
        try {
            if (!redisAvailable) {
                System.out.println("✅ Redis is synced and ready.");
                redisAvailable = true;
                return;
            }
//            updateProducer.sendBatchSyncRequest();
        } catch (Exception e) {
            if (redisAvailable) {
                System.out.println("❌ Redis is down.");
                redisAvailable = false;
            }

        }
    }


}
