package skytales.common.kafka.state_engine.utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KafkaMessage<T> {
    private String type;
    private Object data;

    public KafkaMessage(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public KafkaMessage(T data) {
        this.data = data;
    }
}

