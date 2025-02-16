package skytales.library.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import com.fasterxml.jackson.annotation.JsonProperty;

public record BookData(
        @NotNull(message = "Title is mandatory")
        @NotEmpty(message = "Title cannot be empty")
        @JsonProperty("title") String title,

        @NotNull(message = "Genre is mandatory")
        @NotEmpty(message = "Genre cannot be empty")
        @JsonProperty("genre") String genre,

        @NotNull(message = "Author is mandatory")
        @NotEmpty(message = "Author cannot be empty") String author,

        @URL(message = "Banner image URL must be a valid URL") String bannerImageUrl,

        @URL(message = "Cover image URL must be a valid URL") String coverImageUrl,

        @NotNull(message = "Year is mandatory") String year,

        @NotNull(message = "Price is mandatory") String price,

        @Size(max = 10000, message = "Description cannot exceed 10000 characters") String description
) {}
