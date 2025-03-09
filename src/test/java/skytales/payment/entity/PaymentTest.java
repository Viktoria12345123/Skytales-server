package skytales.payment.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import skytales.payment.model.Payment;
import skytales.payment.model.PaymentStatus;

import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;


class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(UUID.randomUUID())
                .user(UUID.randomUUID())
                .amount(100.0)
                .paymentIntentId("intentId")
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(new java.util.Date())
                .bookTitles(Arrays.asList("Book 1", "Book 2"))
                .build();
    }

    @Test
    void testPaymentCreation() {
        assertNotNull(payment.getId());
        assertEquals(100.0, payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getPaymentStatus());
        assertTrue(payment.getBookTitles().contains("Book 1"));
    }

    @Test
    void testSetPaymentStatus() {
        payment.setPaymentStatus(PaymentStatus.SUCCEEDED);
        assertEquals(PaymentStatus.SUCCEEDED, payment.getPaymentStatus());
    }

    @Test
    void testBookTitles() {
        payment.setBookTitles(List.of("Book 3"));
        assertTrue(payment.getBookTitles().contains("Book 3"));
    }
}
