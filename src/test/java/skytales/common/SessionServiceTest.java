package skytales.common;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import skytales.auth.dto.SessionResponse;
import skytales.common.security.SessionService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {
    @InjectMocks
    private SessionService sessionService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testGetSessionData_Success() {
        when(request.getAttribute("userId")).thenReturn("123");
        when(request.getAttribute("username")).thenReturn("testUser");
        when(request.getAttribute("email")).thenReturn("test@example.com");
        when(request.getAttribute("role")).thenReturn("USER");
        when(request.getAttribute("cartId")).thenReturn("cart123");

        SessionResponse response = sessionService.getSessionData(request);

        assertEquals("123", response.id());
        assertEquals("testUser", response.username());
        assertEquals("test@example.com", response.email());
        assertEquals("USER", response.role());
        assertEquals("cart123", response.cartId());
    }

    @Test
    public void testGetSessionData_IncompleteSessionData() {
        when(request.getAttribute("userId")).thenReturn(null);
        when(request.getAttribute("username")).thenReturn("testUser");
        when(request.getAttribute("email")).thenReturn("test@example.com");
        when(request.getAttribute("role")).thenReturn("USER");

        Exception exception = assertThrows(SessionAuthenticationException.class, () -> {
            sessionService.getSessionData(request);
        });

        assertEquals("Session data is incomplete.", exception.getMessage());
    }
}
