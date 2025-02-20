package skytales.auth.service;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import skytales.auth.dto.*;
import skytales.auth.events.UserCreatedEvent;
import skytales.auth.model.Role;
import skytales.auth.model.User;
import skytales.auth.repository.UserRepository;
import skytales.common.kafka.state_engine.UpdateProducer;
import skytales.common.kafka.state_engine.utils.KafkaMessage;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final KafkaTemplate<String, KafkaMessage<String>> kafkaTemplate;
    private final UpdateProducer updateProducer;

    public UserService(UserRepository userRepository, JwtService jwtService, KafkaTemplate<String, KafkaMessage<String>> kafkaTemplate, UpdateProducer updateProducer) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.kafkaTemplate = kafkaTemplate;
        this.updateProducer = updateProducer;
    }


    public User getById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public User isEmailTaken(String email) {
        return userRepository.findByEmail(email);
    }

    public User register(RegisterRequest registerRequest, BCryptPasswordEncoder bCryptPasswordEncoder) {

        User exists = isEmailTaken(registerRequest.email());
        if (exists != null) {
            throw new Error("user already exists!");
        }

        User user = User.builder()
                .role(Role.USER)
                .status("regular")
                .description("No description to show yet...")
                .username(generateRandomUsername())
                .profilePicture("https://icon-library.com/images/user-icon-jpg/user-icon-jpg-28.jpg")
                .email(registerRequest.email())
                .password(bCryptPasswordEncoder.encode(registerRequest.password()))
                .build();

        userRepository.save(user);
        updateProducer.sendCreateCartRequest(user.getId().toString());

        return user;

    }

    public User login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.email());
        if (user == null) {
            throw new Error("wrong email");
        }

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new Error("wrong password");
        }

        return user;
    }


    private String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public LoginResponse generateLoginResponse(User user) {

        String jwtToken = createToken(user);

        return new LoginResponse(
                user.getEmail(),
                user.getId().toString(),
                user.getRole().name(),
                jwtToken
        );
    }

    public RegisterResponse generateRegisterResponse(User user) {

        String jwtToken = createToken(user);

        return new RegisterResponse(
                user.getEmail(),
                user.getId().toString(),
                user.getRole().name(),
                jwtToken
        );
    }

    public String createToken(User user) {

        return jwtService.generateToken(
                user.getId().toString(),
                user.getRole().name(),
                user.getEmail()
        );
    }

    public void setCartToUser(String cartId, String userId) {

        User user = getById(UUID.fromString(userId));
        user.setCartId(UUID.fromString(cartId));
        userRepository.save(user);

    }
}
