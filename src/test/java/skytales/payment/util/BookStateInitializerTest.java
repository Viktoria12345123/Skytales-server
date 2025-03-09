package skytales.payment.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import skytales.payment.model.BookState;
import skytales.payment.util.BookStateInitializer.BookDetailsDto;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookStateInitializerTest {

    @Mock
    private BookState bookState;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookStateInitializer bookStateInitializer;

    private BookDetailsDto[] bookDetailsDtos;

    @BeforeEach
    void setUp() {
        bookDetailsDtos = new BookDetailsDto[]{
                new BookDetailsDto(UUID.randomUUID(), 10),
                new BookDetailsDto(UUID.randomUUID(), 5)
        };
    }

    @Test
    void onApplicationEvent() {
        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);
        bookStateInitializer.onApplicationEvent(event);

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(BookDetailsDto[].class));
    }

    @Test
    void fetchBooksOnStartup_Success() {
        ResponseEntity<BookDetailsDto[]> responseEntity = new ResponseEntity<>(bookDetailsDtos, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(BookDetailsDto[].class))).thenReturn(responseEntity);

        bookStateInitializer.fetchBooksOnStartup();

        for (BookDetailsDto book : bookDetailsDtos) {
            verify(bookState, times(1)).setBook(book.id(), book.quantity());
        }
    }

    @Test
    void fetchBooksOnStartup_Failure() {
        when(restTemplate.getForEntity(anyString(), eq(BookDetailsDto[].class))).thenThrow(new RuntimeException("Failed to fetch books"));

        bookStateInitializer.fetchBooksOnStartup();

        verify(bookState, never()).setBook(any(UUID.class), anyInt());
    }
}
