package rs.fon.domaci.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by calem on 11/14/2016.
 */

public class DatabaseContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String STRING_TYPE = " VARCHAR(20)";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseEntry.TABLE_NAME + " (" +
                    DatabaseEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseEntry.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    DatabaseEntry.COLUMN_NAME_USER + STRING_TYPE + " )";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseEntry.TABLE_NAME;

    private DatabaseContract() {

    }

    /*
    * TodoItemEntry predstavlja jednu tabelu. U njoj definisemo ime tabele i sve kolone koje sadrzi
    * ta tabela.
    *
    * Ona implementira BaseColumns interfejs, kako bi imala polje _ID, koje sluzi kao identifikator
    * u bazi.
    * */
    public static class DatabaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "tabela";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_USER = "user";
    }
}
