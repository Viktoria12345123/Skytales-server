package skytales.library;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import skytales.library.dto.BookData;
import skytales.library.service.BookService;
import skytales.library.model.Book;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final ElasticsearchClient elasticsearchClient;

    public BookController(BookService bookService, @Qualifier("elasticsearchClient") ElasticsearchClient elasticsearchClient) {
        this.bookService = bookService;
        this.elasticsearchClient = elasticsearchClient;
    }

    @GetMapping()
    public ResponseEntity<Object> getBooks() {

        List<Book> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.ACCEPTED);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createBook(@RequestBody @Valid BookData data, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {

            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String message = error.getDefaultMessage();
                errors.put(fieldName, message);
            });

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Book book = bookService.createBook(data);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String query) throws IOException {

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("library")
                .query(q -> q
                        .multiMatch(m -> m
                                .query(query)
                                .fields("title^3", "author^2", "description")
                                .fuzziness("AUTO")
                        ))
        );

        SearchResponse<Book> searchResponse = elasticsearchClient.search(searchRequest, Book.class);

        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Object> getBook(@PathVariable String bookId) {

       Book book = bookService.getBookById(UUID.fromString(bookId));

       if( book == null ) {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }

       return new ResponseEntity<>(book, HttpStatus.OK);
    }


}
