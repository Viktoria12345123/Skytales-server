package skytales.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import skytales.cart.dto.BookRequest;
import skytales.cart.model.BookItemReference;
import skytales.cart.repository.BookItemReferenceRepository;
import skytales.cart.service.BookReferenceService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookReferenceServiceTest {

    @Mock
    private BookItemReferenceRepository bookItemReferenceRepository;

    @InjectMocks
    private BookReferenceService bookReferenceService;

    private BookRequest bookRequest;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        bookRequest = new BookRequest(bookId, "Title", "Fantasy", "Author", "http://example.com/cover.jpg", 2000, BigDecimal.valueOf(1), 30);
    }

//    @Test
//    void addBookToState() {
//        bookReferenceService.addBookToState(bookRequest);
//
//        verify(bookItemReferenceRepository, times(1)).save(any(BookItemReference.class));
//    }
//
//    @Test
//    void removeBookFromState() {
//        BookItemReference bookItemReference = new BookItemReference();
//        bookItemReference.setBookId(bookId);
//
//        when(bookItemReferenceRepository.findById(bookId)).thenReturn(Optional.of(bookItemReference));
//
//        bookReferenceService.removeBookFromState(bookRequest);
//
//        verify(bookItemReferenceRepository, times(1)).delete(bookItemReference);
//    }
//
//    @Test
//    void removeBookFromState_BookNotFound() {
//        when(bookItemReferenceRepository.findById(bookId)).thenReturn(Optional.empty());
//
//        bookReferenceService.removeBookFromState(bookRequest);
//
//        verify(bookItemReferenceRepository, never()).delete(any(BookItemReference.class));
//    }
}
