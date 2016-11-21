package rs.fon.todoapp.database;

import android.provider.BaseColumns;

/**
 * Created by calem on 11/3/2016.
 *
 *
 * Klasa gde smo definisali sve tabele, odgovarajuce kolone i komande za pravljenje sheme baze.
 */

public class TodoItemContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String STRING_TYPE = " VARCHAR(20)";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TodoItemEntry.TABLE_NAME + " (" +
                    TodoItemEntry._ID + " INTEGER PRIMARY KEY," +
                    TodoItemEntry.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    TodoItemEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    TodoItemEntry.COLUMN_NAME_USER + STRING_TYPE + " )";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TodoItemEntry.TABLE_NAME;

    private TodoItemContract() {

    }

    /*
    * TodoItemEntry predstavlja jednu tabelu. U njoj definisemo ime tabele i sve kolone koje sadrzi
    * ta tabela.
    *
    * Ona implementira BaseColumns interfejs, kako bi imala polje _ID, koje sluzi kao identifikator
    * u bazi.
    * */
    public static class TodoItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "todos";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_TITLE = "title";
    }
}