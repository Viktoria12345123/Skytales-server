package skytales.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import skytales.auth.dto.SessionResponse;
import skytales.auth.model.User;
import skytales.auth.service.JwtService;
import skytales.auth.service.UserService;
import skytales.common.configuration.SecurityConfig;
import skytales.common.security.JwtAuthenticationFilter;
import skytales.common.security.SessionService;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SessionService sessionService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;
    private UUID userId;
    private String token;
    private SessionResponse mockSessionResponse;

    @BeforeEach
    void setUp() {

        userId = UUID.fromString("73fded46-c09b-49cf-b581-8ed145a887fe");
        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        mockSessionResponse = new SessionResponse("user1@example.com", "user1", "123e4567-e89b-12d3-a456-426614174000", "USER", "cart456");
        when(sessionService.getSessionData(any(HttpServletRequest.class))).thenReturn(mockSessionResponse);
    }

    @Test
    void testGetUser_Success() throws Exception {
        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"73fded46-c09b-49cf-b581-8ed145a887fe\",\"email\":\"test@example.com\",\"username\":\"testuser\"}"));

    }

    @Test
    void testGetUser_NotFound() throws Exception {
        when(userService.getById(any(UUID.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

}
