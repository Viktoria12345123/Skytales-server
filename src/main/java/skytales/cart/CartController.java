package skytales.cart;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skytales.auth.dto.SessionResponse;
import skytales.cart.model.BookItemReference;
import skytales.cart.service.CartService;
import skytales.common.security.SessionService;

import java.util.Collections;
import java.util.Map;
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
        UUID cartId = UUID.fromString(sessionResponse.cartId());

        cartService.addToCart(cartId, UUID.fromString(id));

        System.out.println("Added book to cart");
        return ResponseEntity.ok(cartId);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable String id, HttpServletRequest request) {
        SessionResponse sessionResponse = sessionService.getSessionData(request);
        UUID cartId = UUID.fromString(sessionResponse.cartId());

        cartService.deleteFromCart(cartId, UUID.fromString(id));

        System.out.println("Removed book from cart");
        return ResponseEntity.ok(cartId);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getItems(HttpServletRequest request) {
        SessionResponse sessionResponse = sessionService.getSessionData(request);
        UUID cartId = UUID.fromString(sessionResponse.cartId());

        Set<BookItemReference> items = cartService.getCartItems(cartId);


        System.out.println("Got cart");
        return ResponseEntity.ok(items);
    }

    @PostMapping("/createCart")
    public ResponseEntity<Map<String, String>> createCart(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User ID is required"));
        }


        String cartId = cartService.createCartForUser(userId);
        System.out.println("Sending response");
        return ResponseEntity.ok(Collections.singletonMap("cartId", cartId));
    }


}
