package skytales.common.kafka.state_engine.dto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record BookMessage(
        UUID id,
        String title,
        String genre,
        String author,
        String bannerImageUrl,
        String coverImageUrl,
        Integer year,
        BigDecimal price,
        String description,
        int quantity
) implements Serializable {
}
