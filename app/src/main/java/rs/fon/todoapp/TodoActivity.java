package rs.fon.todoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import rs.fon.todoapp.database.TodoItemContract;
import rs.fon.todoapp.database.TodoItemDBHelper;
import rs.fon.todoapp.model.TodoItem;

public class TodoActivity extends AppCompatActivity {
    private static final String notificationIntent = "rs.fon.todo.Notif";

    private String username;
    private TextView nameText;
    private ListView listView;

    private ArrayList<String> todoList;
    private ArrayAdapter<String> todoListAdapter = null;

    private TodoItemDBHelper dbHelper = null;

    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        sharedPreferences = getSharedPreferences("todoApp",MODE_PRIVATE);
        username = sharedPreferences.getString("username","");

        nameText = (TextView) findViewById(R.id.todo_name);
        nameText.setText(username);

        todoList = new ArrayList<>();

        todoListAdapter = new ArrayAdapter<>(this, R.layout.todo_item, todoList);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(todoListAdapter);

        Button todoButton = (Button) findViewById(R.id.todo_enter);
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText todoText = (EditText) findViewById(R.id.todo_text);

                String todo = todoText.getText().toString();

                todoList.add(todo);
                todoListAdapter.notifyDataSetChanged();

                TodoItem item = new TodoItem(todo, username);

                WriteToDatabase dbInsert = new WriteToDatabase();
                dbInsert.execute(item);

                Intent intent = new Intent();
                intent.putExtra("text", todo);
                intent.setAction(notificationIntent);
                sendBroadcast(intent);

                todoText.setText("");
            }
        });

        Log.d("Lifecycle", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbHelper = new TodoItemDBHelper(this);

        ReadFromDatabase readFromDatabase = new ReadFromDatabase();
        readFromDatabase.execute(username);

        Log.d("Lifecycle", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        dbHelper.close();
        Log.d("Lifecycle", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Lifecycle", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle", "onDestroy");
    }

    @Override
    public void onBackPressed() {

    }

    public void onLogoutClicked(View view) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove("username");
        edit.apply();

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private class WriteToDatabase extends AsyncTask<TodoItem, Void, Boolean> {

        @Override
        protected Boolean doInBackground(TodoItem... items) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(TodoItemContract.TodoItemEntry.COLUMN_NAME_TEXT, items[0].getText());
            values.put(TodoItemContract.TodoItemEntry.COLUMN_NAME_USER, items[0].getUser());

            long newRowId = db.insert(TodoItemContract.TodoItemEntry.TABLE_NAME, null, values);

            return newRowId > 0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (!aBoolean) {
                Toast.makeText(TodoActivity.this, "Item not inserted", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("dbOperation", "insert success");
            }
        }
    }

    private class ReadFromDatabase extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... items) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            ArrayList<String> todoList = new ArrayList<>();

            String[] projection = {
                    TodoItemContract.TodoItemEntry._ID,
                    TodoItemContract.TodoItemEntry.COLUMN_NAME_TEXT,
            };

            String selection = TodoItemContract.TodoItemEntry.COLUMN_NAME_USER + " = ?";
            String[] selectionArgs = {items[0]};

            Cursor c = db.query(
                    TodoItemContract.TodoItemEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );


            while (c.moveToNext()) {
                String text = c.getString(
                        c.getColumnIndexOrThrow(TodoItemContract.TodoItemEntry.COLUMN_NAME_TEXT)
                );

                todoList.add(text);
            }

            c.close();

            return todoList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);

            if (list != null) {
                todoList.clear();
                todoList.addAll(list);

                todoListAdapter.notifyDataSetChanged();
                Log.d("dbOperation", "read success");
            } else {
                Log.d("dbOperation", "nothing to read");
            }
        }
    }
}
