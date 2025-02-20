package skytales.cart.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import skytales.auth.model.User;
import skytales.library.model.Book;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToMany
    @JoinTable(
            name = "cart_books",
            joinColumns = @JoinColumn(name = "cart_id", columnDefinition = "CHAR(36)"),
            inverseJoinColumns = @JoinColumn(name = "book_reference_id"))
    private Set<BookItemReference> books;

    @NotNull
    private UUID owner;
}