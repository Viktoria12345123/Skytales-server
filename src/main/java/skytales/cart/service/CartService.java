package skytales.cart.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import skytales.cart.dto.SendCartRequest;
import skytales.cart.model.BookItemReference;
import skytales.cart.model.Cart;
import skytales.cart.repository.BookItemReferenceRepository;
import skytales.cart.repository.CartRepository;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

import java.util.UUID;

@Service
public class CartService {


    private final CartRepository cartRepository;
    private final BookItemReferenceRepository bookItemReferenceRepository;
    private final KafkaTemplate<String, KafkaMessage<String>> kafkaTemplate;

    public CartService(CartRepository cartRepository, BookItemReferenceRepository bookItemReferenceRepository, KafkaTemplate<String, KafkaMessage<String>> kafkaTemplate) {
        this.cartRepository = cartRepository;
        this.bookItemReferenceRepository = bookItemReferenceRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Cart getCartById (UUID id) {
        return  cartRepository.findById(id).orElse(null);
    }

    public void addToCart (UUID bookId, UUID cartId) {

       BookItemReference book = bookItemReferenceRepository.getReferenceById(bookId);


    }

    public void createCartForUser(UUID userId) {

        Cart cart = Cart.builder()
                .owner(userId)
                .build();

        cartRepository.save(cart);
//
//        KafkaMessage<String> request = new KafkaMessage<>(cart.getId().toString() + " " + userId);
//        kafkaTemplate.send("cart_created", request);
    }
}
