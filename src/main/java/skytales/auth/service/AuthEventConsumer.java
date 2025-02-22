package skytales.auth.service;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

@Service
@EnableKafka
public class AuthEventConsumer {

    private final UserService userService;

    public AuthEventConsumer(UserService userService) {
        this.userService = userService;
    }


}
