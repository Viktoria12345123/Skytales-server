package skytales.cart;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skytales.auth.dto.SessionResponse;
import skytales.cart.model.BookItemReference;
import skytales.cart.service.CartService;
import skytales.common.security.SessionService;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {


    private final SessionService sessionService;
    private final CartService cartService;

    public CartController(SessionService sessionService, CartService cartService) {
        this.sessionService = sessionService;
        this.cartService = cartService;
    }

    @PutMapping("add/{id}")
    public ResponseEntity<?> addToCart(@PathVariable String id, HttpServletRequest request) {

        SessionResponse sessionResponse = sessionService.getSessionData(request);
        UUID userId = UUID.fromString(sessionResponse.id());

        cartService.addToCart(userId, UUID.fromString(id));
        System.out.println("Added book to cart");
        return ResponseEntity.ok(userId);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable String id, HttpServletRequest request) {
        SessionResponse sessionResponse = sessionService.getSessionData(request);
        UUID userId = UUID.fromString(sessionResponse.id());

        cartService.deleteFromCart(userId, UUID.fromString(id));

        System.out.println("Removed book from cart");
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getItems(HttpServletRequest request) {
        SessionResponse sessionResponse = sessionService.getSessionData(request);
        UUID userId = UUID.fromString(sessionResponse.id());

        Set<BookItemReference> items = cartService.getCartItems(userId);


        System.out.println("Got cart");
        return ResponseEntity.ok(items);
    }


}
