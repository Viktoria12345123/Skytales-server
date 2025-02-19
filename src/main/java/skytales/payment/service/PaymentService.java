package skytales.payment.service;

import org.springframework.stereotype.Service;
import skytales.payment.model.BookState;

import java.util.UUID;

@Service
public class PaymentService {

    private final BookState bookState;

    public PaymentService(BookState bookState) {
        this.bookState = bookState;
    }

    public void addBookToState(UUID id, int quantity) {
        bookState.addBook(id, quantity);
    }
}
