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
    /*
    * Ovo je konstanta koja sluzi kao Action u Implicitnom Intentu.
    * */
    private static final String notificationIntent = "rs.fon.todo.Notif";

    private String username;
    private TextView nameText;
    private ListView listView;

    /*
    * Ovde su definisani lista todo_a i adapter za tu listu.
    *
    * Lista je obicna ArrayList koja prima Stringove, dakle samo lista stringova.
    *
    * Adapter je klasa koja sluzi kao posrednik izmedju liste i layout-a. Ona za svakog clana
    * liste koja mu je pridruzena pravi layout i postavlja ga u odredjeni view. U nasem slucaju,
    * lista koju cemo mu pridruziti je todoList, layout je layout koji se nalazi u fajlu layout/todo_item.xml,
    * a View u kome ce se prikazati lista TextView-ova je ListView iz activity_todo.xml fajla.
    * */
    private ArrayList<String> todoList;
    private ArrayAdapter<String> todoListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        /*
        * Na ovaj nacin vadimo String iz Extras iz Intenta koji je doveo do naseg Activity.
        *
        * Get intent nam daje Intent, getExtras() vadi extras iz tog intenta, getString vadi String
        * vrednost. Imajte na umu da ova vrednost moze biti null.
        * */
        username = getIntent().getExtras().getString("username");

        /*
        * Ovde vadimo TextView iz layout fajla koji treba da pokaze ono sto je ukucano u LoginActivity.
        * Zatim postavljamo za tekst vrednost koju smo izvadili iz intent-a.
        * */
        nameText = (TextView) findViewById(R.id.todo_name);
        nameText.setText(username);


        /*
        * Inicijalizacija liste i adapter-a. U konstruktoru adapter-a definisemo kontekst, u ovom
        * slucaju Activity, tj. this, Layout jednog itema, u ovom slucaju fajl res/layout/todo_item.xml,
        * tj. R.layout.todo_item i listu stringova, tj. todoList.
        * */
        todoList = new ArrayList<>();
        todoListAdapter = new ArrayAdapter<>(this,R.layout.todo_item,todoList);

        /*
        * Ovde vadimo listView iz layout-a, a zatim postavljamo adapter na taj ListView pozivanjem
        * metode listview.setAdapter koji prima kao parametar objekat klase Adapter.
        * */
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(todoListAdapter);

        /*
        * Kao u LoginActivity, definisemo setOnClickListener za Button.
        * */

        Button todoButton = (Button) findViewById(R.id.todo_enter);
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText todoText = (EditText) findViewById(R.id.todo_text);

                /*
                * Vadimo ono sto je korisnik upisao u EditText polje.
                *
                * Zatim, dodajemo u vrednost u listu i obavestavamo adapter da se
                * lista promenila, kako bi adapter azurirao listView.
                * */
                String todo = todoText.getText().toString();

                todoList.add(todo);
                todoListAdapter.notifyDataSetChanged();

                todoText.setText("");

                /*
                * Ovde pravimo implicitni Intent, koji sluzi kada zelimo da posaljemo intent, ali ne
                * znamo koji ce activity da se pokrene. To radimo tako sto napravimo prazan Intent, a
                * zatim mu pridruzimo Action, koji ce kasnije operativni sistem koristiti kako bi znao
                * sta tacno da pokrene. Videti AndroidManifest.xml fajl za vise detalja o tome sta se desava
                * sa ovim Intent-om.
                *
                * sendBroadcast() metoda salje broadcast, tj. salje implicitni intent operativnom sistemu.
                * Primetite da se ne koristi startActivity, jer ne znamo koji Activity pokrecemo.
                * */
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
