package skytales.library.model;

import jakarta.persistence.*;
import lombok.*;
import skytales.auth.model.User;
import skytales.cart.model.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String author;

    @Column(name = "banner_image_url")
    private String bannerImageUrl;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(length = 10000)
    private String description;

    @ManyToOne
    private User creator;

    @ManyToMany(mappedBy = "books")
    private List<Cart> carts;


}
