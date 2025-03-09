package skytales.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import skytales.auth.dto.SessionResponse;
import skytales.auth.service.JwtService;
import skytales.cart.service.CartService;
import skytales.common.configuration.SecurityConfig;
import skytales.common.security.SessionService;
import skytales.payment.Payments;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private String token;
    private SessionResponse mockSessionResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("73fded46-c09b-49cf-b581-8ed145a887fe");
        token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImNhcnRJZCI6IjRhZWY3OWRjLTg1MzUtNDRkZi04ZmNiLTc2OTg5MWQwZjkxZSIsInVzZXJJZCI6IjczZmRlNDY1LWMwOWItNDljZi1iNTgxLThlZDE0NWE4ODdmZSIsImVtYWlsIjoibXlFbWFpbDBAYWJ2LmJnIiwidXNlcm5hbWUiOiJ1c2VybmFtZUZpbGwiLCJzdWIiOiJteUVtYWlsMEBhYnYuYmciLCJpYXQiOjE3NDA2NDc3NjksImV4cCI6MTc0MDczNDE2OX0.mCKdMRLZcGjtOht9G74i0pQFfzPqLGvJ0GKTOzpa258";

        mockSessionResponse = new SessionResponse("user1@example.com", "user1", "123e4567-e89b-12d3-a456-426614174000", "USER", UUID.randomUUID().toString());
        when(sessionService.getSessionData(any(HttpServletRequest.class))).thenReturn(mockSessionResponse);
    }

    @Test
    void testAddToCart() throws Exception {
        doNothing().when(cartService).addToCart(any(UUID.class), any(UUID.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/cart/add/{id}", "4aef79dc-8535-44df-8fcb-769891d0f91e")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveFromCart() throws Exception {
        doNothing().when(cartService).deleteFromCart(any(UUID.class), any(UUID.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/cart/remove/{id}", "4aef79dc-8535-44df-8fcb-769891d0f91e")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetItems() throws Exception {
        when(cartService.getCartItems(any(UUID.class))).thenReturn(Collections.emptySet());

        mockMvc.perform(MockMvcRequestBuilders.get("/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateCart() throws Exception {
        when(cartService.createCartForUser(any(String.class))).thenReturn("cart456");

        Map<String, String> requestBody = Collections.singletonMap("userId", "123e4567-e89b-12d3-a456-426614174000");
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/createCart")
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
