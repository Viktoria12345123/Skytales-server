package skytales.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BookRequest(
        UUID id,
        String title,
        String author,
        String genre,
        String coverImageUrl,
        Integer year,
        BigDecimal price,
        Integer quantity
) {
}
