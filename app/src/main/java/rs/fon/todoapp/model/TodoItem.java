package rs.fon.todoapp.model;

/**
 * Created by calem on 11/3/2016.
 *
 *
 * Klasu TodoItem pravimo sa ciljem grupisanja korisnika i teksta todo_a.
 *
 * Objekti ove klase imaju samo polje tekst i user. Definisani su konstruktor i standardni
 * getter-i i setter-i.
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
