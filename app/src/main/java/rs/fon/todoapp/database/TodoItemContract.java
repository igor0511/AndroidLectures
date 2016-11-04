package rs.fon.todoapp.database;

import android.provider.BaseColumns;

/**
 * Created by calem on 11/3/2016.
 */

public class TodoItemContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String STRING_TYPE = " VARCHAR(20)";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TodoItemContract.TodoItemEntry.TABLE_NAME + " (" +
                    TodoItemContract.TodoItemEntry._ID + " INTEGER PRIMARY KEY," +
                    TodoItemContract.TodoItemEntry.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    TodoItemContract.TodoItemEntry.COLUMN_NAME_USER + STRING_TYPE + " )";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TodoItemEntry.TABLE_NAME;

    private TodoItemContract() {

    }

    public static class TodoItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "todos";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_USER = "user";
    }
}
