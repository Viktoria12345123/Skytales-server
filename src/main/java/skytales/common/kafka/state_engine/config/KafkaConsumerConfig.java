//package skytales.common.kafka.state_engine.config;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//import skytales.common.kafka.state_engine.utils.KafkaMessage;
//import skytales.common.kafka.state_engine.utils.MessageDeserializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaConsumerConfig {
//
//    @Bean
//    public ConsumerFactory<String, KafkaMessage<?>> consumerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "cart-service");
//        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(configProps);
//    }
//
//    @Bean
//    public ConcurrentMessageListenerContainer<String, KafkaMessage<?>> messageListenerContainer() {
//        ConcurrentMessageListenerContainer<String, KafkaMessage<?>> container =
//                new ConcurrentMessageListenerContainer<>(consumerFactory(), new KafkaMessageListener());
//        return container;
//    }
//}
//
////@KafkaListener(topics = "cart-topic", groupId = "cart-service")
////public void handleCartRequest(KafkaMessage<SendCartRequest> kafkaMessage) {
////    SendCartRequest cartRequest = kafkaMessage.getData();
////    // Process cart request...
////}
//
