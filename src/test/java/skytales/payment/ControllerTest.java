package skytales.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
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
import skytales.common.configuration.SecurityConfig;
import skytales.common.security.JwtAuthenticationFilter;
import skytales.common.security.SessionService;
import skytales.payment.dto.StockChange;
import skytales.payment.model.BookState;
import skytales.payment.service.PaymentService;
import skytales.payment.service.StripeService;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(Controller.class)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private BookState bookState;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SessionService sessionService;

    @InjectMocks
    private ControllerTest controllerTest;

    private String token;
    private UUID bookId;
    private SessionResponse mockSessionResponse;

    @BeforeEach
    void setUp() {
        token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImNhcnRJZCI6IjRhZWY3OWRjLTg1MzUtNDRkZi04ZmNiLTc2OTg5MWQwZjkxZSIsInVzZXJJZCI6IjczZmRlNDY1LWMwOWItNDljZi1iNTgxLThlZDE0NWE4ODdmZSIsImVtYWlsIjoibXlFbWFpbDBAYWJ2LmJnIiwidXNlcm5hbWUiOiJ1c2VybmFtZUZpbGwiLCJzdWIiOiJteUVtYWlsMEBhYnYuYmciLCJpYXQiOjE3NDA2NDc3NjksImV4cCI6MTc0MDczNDE2OX0.mCKdMRLZcGjtOht9G74i0pQFfzPqLGvJ0GKTOzpa258";
        bookId = UUID.fromString("73fded46-c09b-49cf-b581-8ed145a887fe");
        mockSessionResponse = new SessionResponse("user1@example.com", "user1", "123e4567-e89b-12d3-a456-426614174000", "USER", "cart456");
        when(sessionService.getSessionData(any(HttpServletRequest.class))).thenReturn(mockSessionResponse);
    }

    @Test
    void testUpdateBookState() throws Exception {
        StockChange stockChange = new StockChange(bookId, 10);

        Mockito.doNothing().when(paymentService).addBookToState(Mockito.eq(bookId), Mockito.eq(10));

        String stockChangeJson = objectMapper.writeValueAsString(stockChange);

        mockMvc.perform(MockMvcRequestBuilders.post("/payments/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stockChangeJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Book state updated successfully"));
    }

    @Test
    void testGetBookStateResponse() throws Exception {
        Map<UUID, BookState.BookDetails> bookStateMap = Map.of(
                bookId, new BookState.BookDetails(bookId, 10)
        );

        Mockito.when(bookState.getBookStateMap()).thenReturn(bookStateMap);

        String expectedResponse = "Book ID: " + bookId + ", Quantity: 10";

        mockMvc.perform(MockMvcRequestBuilders.get("/payments/check_local_state"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
}