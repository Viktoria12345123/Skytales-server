package skytales.common.kafka.state_engine.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import skytales.common.kafka.state_engine.dto.BookMessage;

@Getter
@Setter
public class Message {

    private UpdateType updateType;
    private BookMessage book;

    @JsonCreator
    public Message(
            @JsonProperty("updateType") UpdateType updateType,
            @JsonProperty("book") BookMessage book) {
        this.updateType = updateType;
        this.book = book;
    }

}
