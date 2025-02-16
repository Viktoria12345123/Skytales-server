package skytales.auth.service;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import skytales.auth.model.Role;
import skytales.auth.model.User;
import skytales.auth.repository.UserRepository;
import skytales.auth.dto.LoginRequest;
import skytales.auth.dto.RegisterRequest;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        return new User();
    }

    public User isEmailTaken(String email) {
        return userRepository.findByEmail(email);
    }

    public User register(RegisterRequest registerRequest, BCryptPasswordEncoder bCryptPasswordEncoder) {

        User exists = isEmailTaken(registerRequest.email());
        if (exists != null ) {
            throw  new Error("user already exists!");
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

        return user;

    }

    private String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public User login( LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.email());

        if(user == null) {
            throw  new Error("wrong email");
        }

        if(!user.getPassword().equals(loginRequest.password())) {
            throw  new Error("wrong password");
        }

        return user;
    }

    public User getById(UUID id) {
       return userRepository.findById(id).orElse(null);
    }
}
