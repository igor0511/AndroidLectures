package rs.fon.domaci;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import rs.fon.domaci.database.DatabaseContract;
import rs.fon.domaci.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(dbHelper!=null) {
            dbHelper.close();
        }
    }
}
