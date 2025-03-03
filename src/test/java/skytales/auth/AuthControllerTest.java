package skytales.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import skytales.auth.dto.*;
import skytales.auth.model.User;
import skytales.auth.service.JwtService;
import skytales.auth.service.UserService;
import skytales.common.configuration.SecurityConfig;
import skytales.common.security.JwtAuthenticationFilter;
import skytales.common.security.SessionService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SessionService sessionService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private String token;
    private SessionResponse mockSessionResponse;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(UUID.fromString("73fded46-c09b-49cf-b581-8ed145a887fe"));
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        registerRequest = new RegisterRequest("test@example.com", "password123", "password123");
        loginRequest = new LoginRequest("test@example.com", "password123");

        when(bcryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");
        mockSessionResponse = new SessionResponse("user1@example.com", "user1", "123e4567-e89b-12d3-a456-426614174000", "USER", "cart456");
        when(sessionService.getSessionData(any(HttpServletRequest.class))).thenReturn(mockSessionResponse);
    }

    @Test
    void testRegister_Success() throws Exception {
        RegisterResponse registerResponse = new RegisterResponse("test@example.com", "1", "USER", "jwtToken");

        when(userService.register(any(RegisterRequest.class), any(BCryptPasswordEncoder.class))).thenReturn(user);
        when(userService.generateRegisterResponse(any(User.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\",\"confirmPassword\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"email\":\"test@example.com\",\"userId\":\"1\",\"role\":\"USER\",\"token\":\"jwtToken\"}"));
    }

    @Test
    void testRegister_BadRequest() throws Exception {
        when(userService.register(any(RegisterRequest.class), any(BCryptPasswordEncoder.class))).thenThrow(new ValidationException("Invalid request"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\",\"confirmPassword\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request"));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginResponse loginResponse = new LoginResponse("test@example.com", "1", "USER", "jwtToken");

        when(userService.login(any(LoginRequest.class))).thenReturn(user);
        when(userService.generateLoginResponse(any(User.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"email\":\"test@example.com\",\"userId\":\"1\",\"role\":\"USER\",\"token\":\"jwtToken\"}"));
    }

    @Test
    void testLogin_BadRequest() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenThrow(new ValidationException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testGetSession_Success() throws Exception {
        SessionResponse sessionResponse = new SessionResponse("test@example.com", "testuser", "1", "USER", "cartId");

        when(sessionService.getSessionData(any(HttpServletRequest.class))).thenReturn(sessionResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/session")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"email\":\"test@example.com\",\"username\":\"testuser\",\"userId\":\"1\",\"role\":\"USER\",\"cartId\":\"cartId\"}"));
    }

    @Test
    void testGetSession_Failure() throws Exception {
        when(sessionService.getSessionData(any(HttpServletRequest.class)))
                .thenThrow(new SessionAuthenticationException("Session not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/session")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Session not found"));
    }
}
