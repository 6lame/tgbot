package funbot.obj;

public class User {
    private final Long id;
    private String state;

    public User(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
