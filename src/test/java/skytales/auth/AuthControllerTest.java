package skytales.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SessionService sessionService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private String token;
    private SessionResponse mockSessionResponse;

    @BeforeEach
    void setUp() {
        token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImNhcnRJZCI6IjRhZWY3OWRjLTg1MzUtNDRkZi04ZmNiLTc2OTg5MWQwZjkxZSIsInVzZXJJZCI6IjczZmRlNDY1LWMwOWItNDljZi1iNTgxLThlZDE0NWE4ODdmZSIsImVtYWlsIjoibXlFbWFpbDBAYWJ2LmJnIiwidXNlcm5hbWUiOiJ1c2VybmFtZUZpbGwiLCJzdWIiOiJteUVtYWlsMEBhYnYuYmciLCJpYXQiOjE3NDA2NDc3NjksImV4cCI6MTc0MDczNDE2OX0.mCKdMRLZcGjtOht9G74i0pQFfzPqLGvJ0GKTOzpa258";

        user = new User();
        user.setId(UUID.fromString("73fded46-c09b-49cf-b581-8ed145a887fe"));
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        registerRequest = new RegisterRequest("test@example.com", "12345678", "12345678");
        loginRequest = new LoginRequest("test@example.com", "12345678");

        when(bcryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");
        mockSessionResponse = new SessionResponse("user1@example.com", "user1", "123e4567-e89b-12d3-a456-426614174000", "USER", "cart456");
        when(sessionService.getSessionData(any(HttpServletRequest.class))).thenReturn(mockSessionResponse);
    }

    @Test
    void testRegister_Success() throws Exception {
        RegisterResponse registerResponse = new RegisterResponse("test@example.com", "1", "USER", "jwtToken");

        when(userService.register(any(RegisterRequest.class), any(BCryptPasswordEncoder.class))).thenReturn(user);
        when(userService.generateRegisterResponse(any(User.class))).thenReturn(registerResponse);

        String registerRequestJson = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"email\":\"test@example.com\",\"userId\":\"1\",\"role\":\"USER\",\"jwtToken\":\"jwtToken\"}"));

    }

    @Test
    void testRegister_BadRequest() throws Exception {

        String registerRequestJson = objectMapper.writeValueAsString(new RegisterRequest("testexample.com", "12345678", "12345678"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].defaultMessage").value("Email must be valid!"));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginResponse loginResponse = new LoginResponse("73fded46-c09b-49cf-b581-8ed145a887fe", "sameUser", "USER", "jwtToken");

        when(userService.login(any(LoginRequest.class))).thenReturn(user);
        when(userService.generateLoginResponse(any(User.class))).thenReturn(loginResponse);

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"userId\":\"73fded46-c09b-49cf-b581-8ed145a887fe\",\"role\":\"USER\",\"jwtToken\":\"jwtToken\"}"));
    }

    @Test
    void testLogin_BadRequest() throws Exception {
        String loginRequestJson = objectMapper.writeValueAsString(new LoginRequest("Email", "notValid"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].defaultMessage").value("Email must be valid!"));
    }

    @Test
    void testGetSession_Success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"email\":\"user1@example.com\",\"username\":\"user1\",\"id\":\"123e4567-e89b-12d3-a456-426614174000\",\"role\":\"USER\",\"cartId\":\"cart456\"}"));
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
