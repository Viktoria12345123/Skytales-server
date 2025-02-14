package skytales.auth;

import com.bookstore.demo.entity.cart.CartService;
import com.bookstore.demo.web.dto.request.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    public final CartService cartService;

    public UserService(UserRepository userRepository, CartService cartService) {
        this.userRepository = userRepository;
        this.cartService = cartService;
    }


    public User login(String username, String password) {
        return new User();
    }

    public User isEmailTaken(String email) {
        return userRepository.findByEmail(email);
    }


    public User register(RegisterRequest registerRequest, BCryptPasswordEncoder bCryptPasswordEncoder) {

        User user = User.builder()
                .role(Role.USER)
                .status("regular")
                .description("No description to show yet...")
                .username(generateRandomUsername())
                .email(registerRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(registerRequest.getPassword()))
                .build();

        userRepository.save(user);

        user.setCart(cartService.createDefaultUserCart(user));

        return user;

    }

    public User getSession(String username, String password) {
        return new User();
    }

    private String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8); // Example: user_123e4567
    }


}
