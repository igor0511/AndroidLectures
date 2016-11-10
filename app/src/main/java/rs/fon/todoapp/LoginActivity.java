package rs.fon.todoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private EditText loginText = null;
    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginText = (EditText) findViewById(R.id.login_name);

        sharedPreferences = getSharedPreferences("todoApp",MODE_PRIVATE);

        String username = sharedPreferences.getString("username","");

        if(!username.isEmpty()) {
            Intent intent = new Intent(this,TodoActivity.class);
            startActivity(intent);
        }

        final Button loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = loginText.getText().toString();

                if(!name.isEmpty()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("username",name);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, TodoActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
