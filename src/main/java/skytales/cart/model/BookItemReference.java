package skytales.cart.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BookItemReference {

    @Id
    private UUID bookId;

    private String title;

    private String author;

    private String genre;

    @Column(name = "cover_image_url", length = 1000)
    private String coverImageUrl;

    @Column(nullable = false)
    private Integer year;

    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;
}
