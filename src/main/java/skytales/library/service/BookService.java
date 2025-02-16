package skytales.library.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skytales.library.dto.BookData;
import skytales.library.elasticsearch.service.ElasticSearchService;
import skytales.library.model.Book;
import skytales.library.repository.BookRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BookService {

   private final BookRepository bookRepository;
    private final ElasticSearchService elasticSearchService;

    @Autowired
    public BookService(BookRepository bookRepository, ElasticSearchService elasticSearchService) {
        this.bookRepository = bookRepository;
        this.elasticSearchService = elasticSearchService;
    }

    public List<Book> getAllBooks() {
        return  bookRepository.findAll();
    }

    public Book getBookById(UUID bookId) {
        return  bookRepository.findById(bookId).orElse(null);
    }

    public Book createBook(BookData data) {

        BigDecimal price = new BigDecimal(data.price());

        Book book = Book.builder()
                .title(data.title())
                .author(data.author())
                .coverImageUrl(data.coverImageUrl())
                .bannerImageUrl(data.bannerImageUrl())
                .price(price)
                .description(data.description())
                .year(Integer.parseInt(data.year()))
                .genre(data.genre())
                .build();

        bookRepository.save(book);
        elasticSearchService.addBookToElasticsearch(book);

        return book;
    }
}
