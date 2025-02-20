package skytales.auth.events;

public class UserCreatedEvent {
    private String userId;

    public UserCreatedEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
