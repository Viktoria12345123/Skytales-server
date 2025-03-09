package skytales.cart;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skytales.auth.dto.SessionResponse;
import skytales.auth.service.JwtService;
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
    private final JwtService jwtService;

    public CartController(SessionService sessionService, CartService cartService, JwtService jwtService) {
        this.sessionService = sessionService;
        this.cartService = cartService;
        this.jwtService = jwtService;
    }

    @PutMapping("add/{id}")
    public ResponseEntity<?> addToCart(@PathVariable String id, HttpServletRequest request) {

        System.out.println("Reached cart controller");

        SessionResponse sessionResponse = sessionService.getSessionData(request);
        UUID cartId = UUID.fromString(sessionResponse.cartId());

        cartService.addToCart(cartId, UUID.fromString(id));

        return ResponseEntity.ok(cartId);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable String id, HttpServletRequest request) {
        SessionResponse sessionResponse = sessionService.getSessionData(request);
        UUID cartId = UUID.fromString(sessionResponse.cartId());

        cartService.deleteFromCart(cartId, UUID.fromString(id));

        return ResponseEntity.ok(cartId);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getItems(HttpServletRequest request) {
        SessionResponse sessionResponse = sessionService.getSessionData(request);
        UUID cartId = UUID.fromString(sessionResponse.cartId());

        Set<BookItemReference> items = cartService.getCartItems(cartId);

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


//    @GetMapping("/generate")
//    public ResponseEntity<?> generateToken(HttpServletRequest request) {
//
//
//        String[] owners = new String[]{
//                "73fde465-c09b-49cf-b581-8ed145a887fe",
//                "75736572-4964-3100-0000-000000000000",
//                "75736572-4964-3130-0000-000000000000",
//                "75736572-4964-3130-3000-000000000000",
//                "75736572-4964-3131-0000-000000000000",
//                "75736572-4964-3132-0000-000000000000",
//                "75736572-4964-3133-0000-000000000000",
//                "75736572-4964-3134-0000-000000000000",
//                "75736572-4964-3135-0000-000000000000",
//                "75736572-4964-3136-0000-000000000000",
//                "75736572-4964-3137-0000-000000000000",
//                "75736572-4964-3138-0000-000000000000",
//                "75736572-4964-3139-0000-000000000000",
//                "75736572-4964-3200-0000-000000000000",
//                "75736572-4964-3230-0000-000000000000",
//                "75736572-4964-3231-0000-000000000000",
//                "75736572-4964-3232-0000-000000000000",
//                "75736572-4964-3233-0000-000000000000",
//                "75736572-4964-3234-0000-000000000000",
//                "75736572-4964-3235-0000-000000000000",
//                "75736572-4964-3236-0000-000000000000",
//                "75736572-4964-3237-0000-000000000000",
//                "75736572-4964-3238-0000-000000000000",
//                "75736572-4964-3239-0000-000000000000",
//                "75736572-4964-3300-0000-000000000000",
//                "75736572-4964-3330-0000-000000000000",
//                "75736572-4964-3331-0000-000000000000",
//                "75736572-4964-3332-0000-000000000000",
//                "75736572-4964-3333-0000-000000000000",
//                "75736572-4964-3334-0000-000000000000",
//                "75736572-4964-3335-0000-000000000000",
//                "75736572-4964-3336-0000-000000000000",
//                "75736572-4964-3337-0000-000000000000",
//                "75736572-4964-3338-0000-000000000000",
//                "75736572-4964-3339-0000-000000000000",
//                "75736572-4964-3400-0000-000000000000",
//                "75736572-4964-3430-0000-000000000000",
//                "75736572-4964-3431-0000-000000000000",
//                "75736572-4964-3432-0000-000000000000",
//                "75736572-4964-3433-0000-000000000000",
//                "75736572-4964-3434-0000-000000000000",
//                "75736572-4964-3435-0000-000000000000",
//                "75736572-4964-3436-0000-000000000000",
//                "75736572-4964-3437-0000-000000000000",
//                "75736572-4964-3438-0000-000000000000",
//                "75736572-4964-3439-0000-000000000000",
//                "75736572-4964-3500-0000-000000000000",
//                "75736572-4964-3530-0000-000000000000",
//                "75736572-4964-3531-0000-000000000000",
//                "75736572-4964-3532-0000-000000000000",
//                "75736572-4964-3533-0000-000000000000",
//                "75736572-4964-3534-0000-000000000000",
//                "75736572-4964-3535-0000-000000000000",
//                "75736572-4964-3536-0000-000000000000",
//                "75736572-4964-3537-0000-000000000000",
//                "75736572-4964-3538-0000-000000000000",
//                "75736572-4964-3539-0000-000000000000",
//                "75736572-4964-3600-0000-000000000000",
//                "75736572-4964-3630-0000-000000000000",
//                "75736572-4964-3631-0000-000000000000",
//                "75736572-4964-3632-0000-000000000000",
//                "75736572-4964-3633-0000-000000000000",
//                "75736572-4964-3634-0000-000000000000",
//                "75736572-4964-3635-0000-000000000000",
//                "75736572-4964-3636-0000-000000000000",
//                "75736572-4964-3637-0000-000000000000",
//                "75736572-4964-3638-0000-000000000000",
//                "75736572-4964-3639-0000-000000000000",
//                "75736572-4964-3700-0000-000000000000",
//                "75736572-4964-3730-0000-000000000000",
//                "75736572-4964-3731-0000-000000000000",
//                "75736572-4964-3732-0000-000000000000",
//                "75736572-4964-3733-0000-000000000000",
//                "75736572-4964-3734-0000-000000000000",
//                "75736572-4964-3735-0000-000000000000",
//                "75736572-4964-3736-0000-000000000000",
//                "75736572-4964-3737-0000-000000000000",
//                "75736572-4964-3738-0000-000000000000",
//                "75736572-4964-3739-0000-000000000000",
//                "75736572-4964-3800-0000-000000000000",
//                "75736572-4964-3830-0000-000000000000",
//                "75736572-4964-3831-0000-000000000000",
//                "75736572-4964-3832-0000-000000000000",
//                "75736572-4964-3833-0000-000000000000",
//                "75736572-4964-3834-0000-000000000000",
//                "75736572-4964-3835-0000-000000000000",
//                "75736572-4964-3836-0000-000000000000",
//                "75736572-4964-3837-0000-000000000000",
//                "75736572-4964-3838-0000-000000000000",
//                "75736572-4964-3839-0000-000000000000",
//                "75736572-4964-3900-0000-000000000000",
//                "75736572-4964-3930-0000-000000000000",
//                "75736572-4964-3931-0000-000000000000",
//                "75736572-4964-3932-0000-000000000000",
//                "75736572-4964-3933-0000-000000000000",
//                "75736572-4964-3934-0000-000000000000",
//                "75736572-4964-3935-0000-000000000000",
//                "75736572-4964-3936-0000-000000000000",
//                "75736572-4964-3937-0000-000000000000",
//                "75736572-4964-3938-0000-000000000000",
//                "75736572-4964-3939-0000-000000000000",
//                "7a657312-f26d-477e-9d6f-5e6324ac0007",
//                "b1e1be2d-04b2-42f0-a34f-ec02dc619702"
//        };
//
//
//        String[] jwts = new String[owners.length];
//
//        int count = 0;
//        for (String owner : owners) {
//            String cartId = cartService.createCartForUser(owner);
//            String token = jwtService.generateToken(owner, "USER", "myEmail" + count + "@abv.bg",  "usernameFill",cartId);
//            jwts[count] = token;
//            count++;
//        }
//
//        System.out.println(jwts[5]);
//        return ResponseEntity.ok(jwts);
//    }


}
