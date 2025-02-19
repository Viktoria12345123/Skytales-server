package skytales.cart.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import skytales.common.kafka.state_engine.dto.BookMessage;
import skytales.common.kafka.state_engine.model.UpdateType;

@Getter
@Setter
public class MessageRequest {
        private UpdateType type;
        private BookRequest book;

        @JsonCreator
        public MessageRequest(
                @JsonProperty("type") UpdateType type,
                @JsonProperty("book") BookRequest book) {
            this.type = type;
            this.book = book;
        }
    }



