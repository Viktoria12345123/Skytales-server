package skytales.cart.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import skytales.cart.model.BookItemReference;
import skytales.cart.model.Cart;
import skytales.cart.repository.BookItemReferenceRepository;
import skytales.cart.repository.CartRepository;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

import java.util.List;
import java.util.Set;
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

    public Cart getCartByUserId(UUID id) {
        return cartRepository.findCartByOwner(id)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

    }


    public BookItemReference getByBookId(UUID id) {
        return bookItemReferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

    }


    public void addToCart(UUID userId, UUID bookId) {

        List<Cart> all = cartRepository.findAll();

        Cart cart = getCartByUserId(userId);

        BookItemReference bookItemReference = bookItemReferenceRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if(!cart.getBooks().contains(bookItemReference)) {
            cart.getBooks().add(bookItemReference);
            cartRepository.save(cart);
        }

    }

    public void createCartForUser(UUID userId) {

        Cart cart = Cart.builder()
                .owner(userId)
                .build();

        cartRepository.save(cart);
    }

    public void deleteFromCart(UUID userId, UUID bookId) {
        Cart cart = getCartByUserId(userId);
        BookItemReference bookItemReference = getByBookId(bookId);

        cart.getBooks().remove(bookItemReference);
        cartRepository.save(cart);
    }

    public Set<BookItemReference> getCartItems(UUID userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getBooks();
    }

    public void clearCart(UUID id) {
        Cart cart = getCartByUserId(id);
        cart.getBooks().clear();
        cartRepository.save(cart);

        System.out.println(cart.getBooks());
    }
}
