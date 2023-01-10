package entities;

import java.util.List;

public class Chat {
    public Long id;
    public Boolean isPrivate;
    public List<User> members;

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", members=" + members +
                ", isPrivate=" + isPrivate +
                '}';
    }
}
