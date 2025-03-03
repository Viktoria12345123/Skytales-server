package skytales.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.PaymentIntent;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import skytales.common.kafka.state_engine.UpdateProducer;
import skytales.common.security.JwtAuthenticationFilter;
import skytales.common.security.SessionService;
import skytales.payment.dto.BookItem;
import skytales.payment.dto.PaymentRequest;
import skytales.payment.exception.PaymentFailedException;
import skytales.payment.model.Payment;
import skytales.payment.model.PaymentStatus;
import skytales.payment.service.PaymentService;
import skytales.payment.service.StripeService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WebMvcTest(Payments.class)
public class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private StripeService stripeService;

    @MockitoBean
    private UpdateProducer updateProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SessionService sessionService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    private UUID userId;
    private String token;
    private SessionResponse mockSessionResponse;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("73fded46-c09b-49cf-b581-8ed145a887fe");
        token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImNhcnRJZCI6IjRhZWY3OWRjLTg1MzUtNDRkZi04ZmNiLTc2OTg5MWQwZjkxZSIsInVzZXJJZCI6IjczZmRlNDY1LWMwOWItNDljZi1iNTgxLThlZDE0NWE4ODdmZSIsImVtYWlsIjoibXlFbWFpbDBAYWJ2LmJnIiwidXNlcm5hbWUiOiJ1c2VybmFtZUZpbGwiLCJzdWIiOiJteUVtYWlsMEBhYnYuYmciLCJpYXQiOjE3NDA2NDc3NjksImV4cCI6MTc0MDczNDE2OX0.mCKdMRLZcGjtOht9G74i0pQFfzPqLGvJ0GKTOzpa258";
        paymentRequest = new PaymentRequest("pm_card_visa", 1000L, List.of(new BookItem("BookId", "Title1", BigDecimal.valueOf(30))));

        Mockito.doNothing().when(paymentService).createPaymentRecord(Mockito.any(UUID.class), Mockito.anyLong(), Mockito.anyString(), Mockito.any(PaymentStatus.class), Mockito.anyList());
        Mockito.doNothing().when(updateProducer).clearCartForUser(Mockito.anyString());
        Mockito.doNothing().when(paymentService).sufficientQuantity(Mockito.anyList());
        mockSessionResponse = new SessionResponse("user1@example.com", "user1", "123e4567-e89b-12d3-a456-426614174000", "USER", "cart456");
        when(sessionService.getSessionData(any(HttpServletRequest.class))).thenReturn(mockSessionResponse);
    }

    @Test
    void testProcessPayment_Success() throws Exception {
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setId("pi_1GqIC8HYgolSBA35x8q2x8bV");
        paymentIntent.setStatus("succeeded");

        Mockito.when(stripeService.createPaymentIntent(Mockito.any(PaymentRequest.class))).thenReturn(paymentIntent);

        String paymentRequestJson = objectMapper.writeValueAsString(paymentRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentRequestJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

    }

    @Test
    void testProcessPayment_Failed() throws Exception {

        Mockito.when(stripeService.createPaymentIntent(Mockito.any(PaymentRequest.class)))
                .thenThrow(new PaymentFailedException("Payment failed due to insufficient funds."));

        String paymentRequestJson = objectMapper.writeValueAsString(paymentRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentRequestJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isPaymentRequired());
    }

    @Test
    void testHistory() throws Exception {
        List<Payment> payments = List.of(new Payment());

        Mockito.when(paymentService.getAllByOwner(Mockito.any(UUID.class))).thenReturn(payments);

        mockMvc.perform(MockMvcRequestBuilders.get("/payments/{userId}/history", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

}