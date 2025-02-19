package skytales.cart.model;


import jakarta.persistence.*;
import lombok.*;
import skytales.auth.model.User;
import skytales.library.model.Book;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "cart_books",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "book_reference_id"))
    private Set<BookItemReference> books;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}