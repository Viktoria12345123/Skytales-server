package skytales.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import skytales.common.kafka.state_engine.UpdateProducer;
import skytales.common.kafka.state_engine.model.UpdateType;
import skytales.library.dto.BookData;
import skytales.library.elasticsearch.service.ElasticSearchService;
import skytales.library.model.Book;
import skytales.library.repository.BookRepository;
import skytales.library.service.BookService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ElasticSearchService elasticSearchService;

    @Mock
    private UpdateProducer updateProducer;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        book = Book.builder()
                .id(bookId)
                .title("Test Book")
                .author("Test Author")
                .coverImageUrl("cover.jpg")
                .bannerImageUrl("banner.jpg")
                .price(new BigDecimal("19.99"))
                .description("A test book")
                .year(2024)
                .genre("Fiction")
                .quantity(10)
                .build();
    }

    @Test
    void getAllBooks_ShouldReturnBookList() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> books = bookService.getAllBooks();

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Test Book", books.getFirst().getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookById(bookId);

        assertNotNull(foundBook);
        assertEquals(bookId, foundBook.getId());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBookById_ShouldReturnNull_WhenBookDoesNotExist() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Book foundBook = bookService.getBookById(bookId);

        assertNull(foundBook);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void createBook_ShouldSaveBookAndSendUpdates() {
        BookData bookData = new BookData("Test Book", "Test Author", "cover.jpg", "https://www.hostinger.co.uk/tutorials/wp-content/uploads/sites/2/2022/07/the-structure-of-a-url.png", "https://www.hostinger.co.uk/tutorials/wp-content/uploads/sites/2/2022/07/the-structure-of-a-url.png", "2007", "224", "233", "Some kind of description is here");
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book createdBook = bookService.createBook(bookData);

        assertNotNull(createdBook);
        assertEquals("Test Book", createdBook.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(elasticSearchService, times(1)).addBookToElasticsearch(any(Book.class));
        verify(updateProducer, times(1)).sendBookUpdate(eq(UpdateType.NEW_BOOK), any(Book.class));
    }
}
