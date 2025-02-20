package skytales.cart;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skytales.auth.dto.SessionResponse;
import skytales.common.security.SessionService;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {


    private final SessionService sessionService;

    public CartController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> addToCart(@PathVariable UUID id, HttpServletRequest request) {

        SessionResponse sessionResponse = sessionService.getSessionData(request);
        UUID userId = UUID.fromString(sessionResponse.id());

        return ResponseEntity.ok(userId);
    }


}
