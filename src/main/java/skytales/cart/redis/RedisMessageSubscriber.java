package skytales.cart.redis;

import org.springframework.stereotype.Service;
import skytales.common.kafka.state_engine.UpdateProducer;

@Service
public class RedisMessageSubscriber {

    private final UpdateProducer updateProducer;

    public RedisMessageSubscriber(UpdateProducer updateProducer) {
        this.updateProducer = updateProducer;
    }

    public void onMessage(String message, String channel) {
        System.out.println("Received message: " + message + " from channel: " + channel);

        String cartVersionPart = message.substring("syncRequest:".length());
        String[] cartIds = cartVersionPart.split(",");

//        updateProducer.sendBatchSyncRequest();
    }
}