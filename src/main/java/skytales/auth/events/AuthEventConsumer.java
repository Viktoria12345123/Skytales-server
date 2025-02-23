package skytales.auth.events;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import skytales.auth.service.UserService;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

import java.util.UUID;

@Service
@EnableKafka
public class AuthEventConsumer {

    private final UserService userService;

    public AuthEventConsumer(UserService userService) {
        this.userService = userService;
    }


}
