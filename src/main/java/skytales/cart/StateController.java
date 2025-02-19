//package skytales.cart;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import skytales.cart.dto.AddBookRequest;
//import skytales.cart.service.BookItemReferenceService;
//
//@RestController
//@RequestMapping("/state")
//public class StateController {
//
//
//    private final BookItemReferenceService bookItemReferenceService;
//
//    public StateController(BookItemReferenceService bookItemReferenceService) {
//        this.bookItemReferenceService = bookItemReferenceService;
//    }
//
//    @PostMapping("/update")
//    public ResponseEntity<?> updateBookState(@RequestBody AddBookRequest addBookRequest) {
//
//        bookItemReferenceService.addBookToState(addBookRequest);
//        return ResponseEntity.ok("Book state updated successfully");
//    }
//}
