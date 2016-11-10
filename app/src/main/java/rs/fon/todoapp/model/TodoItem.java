package rs.fon.todoapp.model;

/**
 * Created by calem on 11/3/2016.
 */

public class TodoItem {
    private String text;
    private String user;

    public TodoItem(String text, String user) {
        this.text = text;
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "text='" + text + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
