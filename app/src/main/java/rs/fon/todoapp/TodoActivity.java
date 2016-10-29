package rs.fon.todoapp;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TodoActivity extends AppCompatActivity {
    private static final String notificationIntent = "rs.fon.todo.Notif";

    private String username;
    private TextView nameText;
    private ListView listView;

    private ArrayList<String> todoList;
    private ArrayAdapter<String> todoListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        username = getIntent().getStringExtra("username");

        if(username == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
            username = sharedPreferences.getString("username","");
        }

        nameText = (TextView) findViewById(R.id.todo_name);
        nameText.setText(username);

        todoList = new ArrayList<>();

        todoListAdapter = new ArrayAdapter<>(this,R.layout.todo_item,todoList);

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

                Intent intent = new Intent();
                intent.putExtra("text",todo);
                intent.setAction(notificationIntent);
                sendBroadcast(intent);
            }
        });

        Log.d("Lifecycle","onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Lifecycle","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Lifecycle","onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Lifecycle","onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle","onDestroy");
    }
}
