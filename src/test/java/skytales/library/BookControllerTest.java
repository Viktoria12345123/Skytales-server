package skytales.library;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import skytales.auth.dto.SessionResponse;
import skytales.auth.service.JwtService;
import skytales.common.configuration.SecurityConfig;
import skytales.common.security.JwtAuthenticationFilter;
import skytales.common.security.SessionService;
import skytales.library.dto.BookData;
import skytales.library.model.Book;
import skytales.library.service.BookService;
import skytales.payment.Payments;
import skytales.payment.dto.BookItem;
import skytales.payment.dto.PaymentRequest;
import skytales.payment.model.PaymentStatus;
import skytales.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private ElasticsearchClient elasticsearchClient;

    @InjectMocks
    private BookController bookController;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SessionService sessionService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    private Book book;
    private UUID bookId;
    private UUID userId;
    private String token;
    private SessionResponse mockSessionResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("73fded46-c09b-49cf-b581-8ed145a887fe");
        token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImNhcnRJZCI6IjRhZWY3OWRjLTg1MzUtNDRkZi04ZmNiLTc2OTg5MWQwZjkxZSIsInVzZXJJZCI6IjczZmRlNDY1LWMwOWItNDljZi1iNTgxLThlZDE0NWE4ODdmZSIsImVtYWlsIjoibXlFbWFpbDBAYWJ2LmJnIiwidXNlcm5hbWUiOiJ1c2VybmFtZUZpbGwiLCJzdWIiOiJteUVtYWlsMEBhYnYuYmciLCJpYXQiOjE3NDA2NDc3NjksImV4cCI6MTc0MDczNDE2OX0.mCKdMRLZcGjtOht9G74i0pQFfzPqLGvJ0GKTOzpa258";

        bookId = UUID.randomUUID();
        book = Book.builder()
                .id(bookId)
                .title("Test Book")
                .author("Test Author")
                .coverImageUrl("https://www.hostinger.co.uk/tutorials/wp-content/uploads/sites/2/2022/07/the-structure-of-a-url.png")
                .bannerImageUrl("https://www.hostinger.co.uk/tutorials/wp-content/uploads/sites/2/2022/07/the-structure-of-a-url.png")
                .price(new BigDecimal("19.99"))
                .description("A test book")
                .year(2024)
                .genre("Fiction")
                .quantity(10)
                .build();

        mockSessionResponse = new SessionResponse("user1@example.com", "user1", "123e4567-e89b-12d3-a456-426614174000", "USER", "cart456");
        when(sessionService.getSessionData(any(HttpServletRequest.class))).thenReturn(mockSessionResponse);
    }


    @Test
    void getBooks_ShouldReturnBooksList() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void getBook_ShouldReturnBook_WhenExists() throws Exception {
        when(bookService.getBookById(bookId)).thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/{bookId}", bookId.toString())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBook_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        when(bookService.getBookById(bookId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/{bookId}", bookId.toString())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBook_ShouldReturnCreatedBook() throws Exception {
        BookData bookData = new BookData("Test Book", "Test Author", "AuthorGuy", "https://www.hostinger.co.uk/tutorials/wp-content/uploads/sites/2/2022/07/the-structure-of-a-url.png", "https://www.hostinger.co.uk/tutorials/wp-content/uploads/sites/2/2022/07/the-structure-of-a-url.png", "2024", "24", "32", "10");
        when(bookService.createBook(any(BookData.class))).thenReturn(book);

        mockMvc.perform(post("/books/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(bookData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

//    @Test
//    void searchBooks_ShouldReturnBooks_WhenQueryMatches() throws Exception {
//        when(elasticsearchClient.search(any(), eq(Book.class))).thenReturn(null); // Mock ElasticsearchClient behavior
//        mockMvc.perform(MockMvcRequestBuilders.get("/books/search").param("query", "Test"))
//                .andExpect(status().isInternalServerError());
//    }


}
