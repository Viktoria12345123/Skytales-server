package skytales.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import skytales.auth.dto.LoginRequest;
import skytales.auth.dto.LoginResponse;
import skytales.auth.dto.RegisterRequest;
import skytales.auth.dto.RegisterResponse;
import skytales.auth.model.Role;
import skytales.auth.model.User;
import skytales.auth.repository.UserRepository;
import skytales.auth.service.JwtService;
import skytales.auth.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetById() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void testIsEmailTaken() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userService.isEmailTaken(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testRegister() {
        RegisterRequest registerRequest = new RegisterRequest("test@example.com", "password123", "password123");

        when(userRepository.findByEmail(registerRequest.email())).thenReturn(null);
        when(bCryptPasswordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(UUID.fromString("a3983b36-6094-4eea-bd37-297f8aee3073"));
            return savedUser;
        });

        UserService spyUserService = spy(userService);

        doReturn(UUID.randomUUID()).when(spyUserService).createCartForUser(any(UUID.class));
        doNothing().when(spyUserService).assignCart(any(UUID.class), any(UUID.class));

        User result = spyUserService.register(registerRequest, bCryptPasswordEncoder);

        assertNotNull(result);
        assertEquals(registerRequest.email(), result.getEmail());
        assertNotNull(result.getId());

        verify(spyUserService, times(1)).createCartForUser(result.getId());
        verify(spyUserService, times(1)).assignCart(any(UUID.class), eq(result.getId()));
    }

    @Test
    void testLogin() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        User user = new User();
        user.setEmail(loginRequest.email());
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(user);
        when(bCryptPasswordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(true);

        User result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals(loginRequest.email(), result.getEmail());
    }

    @Test
    void testGenerateLoginResponse() {
        User user = new User();
        user.setId(UUID.fromString("a3983b36-6094-4eea-bd37-297f8aee3073"));
        user.setRole(Role.USER);
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        when(jwtService.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        LoginResponse response = userService.generateLoginResponse(user);

        assertNotNull(response);
        assertEquals(user.getId().toString(), response.userId());
        assertEquals(user.getRole().name(), response.role());
        assertEquals("jwtToken", response.jwtToken());
    }

    @Test
    void testGenerateRegisterResponse() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setId(UUID.randomUUID());
        user.setRole(Role.USER);
        user.setUsername("testuser");

        when(jwtService.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        RegisterResponse response = userService.generateRegisterResponse(user);

        assertNotNull(response);
        assertEquals("test@example.com", response.email());
        assertEquals(user.getId().toString(), response.userId());
        assertEquals(user.getRole().name(), response.role());
        assertEquals("jwtToken", response.jwtToken());
    }

    @Test
    void testCreateToken() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(Role.USER);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setCartId(UUID.randomUUID());

        when(jwtService.generateToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("jwtToken");

        String token = userService.createToken(user);

        assertNotNull(token);
        assertEquals("jwtToken", token);
    }

    @Test
    void testAssignCart() {
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.assignCart(cartId, userId);

        assertEquals(cartId, user.getCartId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateCartForUser() {
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of("cartId", cartId.toString())));

        UUID result = userService.createCartForUser(userId);

        assertNotNull(result);
        assertEquals(cartId, result);
    }
}