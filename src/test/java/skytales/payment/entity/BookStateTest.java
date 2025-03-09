package skytales.payment.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import skytales.payment.model.BookState;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookStateTest {

    private BookState bookState;

    @BeforeEach
    void setUp() {
        bookState = new BookState();
    }

    @Test
    void testAddBook() {
        UUID bookId = UUID.randomUUID();
        bookState.addBook(bookId, 10);

        BookState.BookDetails bookDetails = bookState.getById(bookId);

        assertNotNull(bookDetails);
        assertEquals(bookId, bookDetails.getId());
        assertEquals(10, bookDetails.getQuantity());
    }

    @Test
    void testGetById() {
        UUID bookId = UUID.randomUUID();
        bookState.addBook(bookId, 15);

        BookState.BookDetails bookDetails = bookState.getById(bookId);

        assertNotNull(bookDetails);
        assertEquals(bookId, bookDetails.getId());
        assertEquals(15, bookDetails.getQuantity());
    }

    @Test
    void testSetBookQuantity() {
        UUID bookId = UUID.randomUUID();
        bookState.addBook(bookId, 20);

        bookState.setBook(bookId, 30);

        BookState.BookDetails bookDetails = bookState.getById(bookId);

        assertNotNull(bookDetails);
        assertEquals(30, bookDetails.getQuantity());
    }

    @Test
    void testAddBookWhenBookExists() {
        UUID bookId = UUID.randomUUID();
        bookState.addBook(bookId, 25);

        bookState.setBook(bookId, 50);

        BookState.BookDetails bookDetails = bookState.getById(bookId);

        assertNotNull(bookDetails);
        assertEquals(50, bookDetails.getQuantity());
    }

    @Test
    void testGetByIdWhenBookDoesNotExist() {
        UUID bookId = UUID.randomUUID();
        BookState.BookDetails bookDetails = bookState.getById(bookId);

        assertNull(bookDetails);
    }
}
