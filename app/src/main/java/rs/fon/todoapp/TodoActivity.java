package rs.fon.todoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.BoolRes;
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

    private TodoItemDBHelper dbHelper = null;

    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);


        /*
        * Inicijalizacija SharedPreferences objekta se vrsi na isti nacin kao i u LoginActivity.
        * Obavezno je da ime fajla bude isto.
        * */

        sharedPreferences = getSharedPreferences("todoApp",MODE_PRIVATE);
        username = sharedPreferences.getString("username","");
        /*
        * Na ovaj nacin vadimo String iz Extras iz Intenta koji je doveo do naseg Activity.
        *
        * Get intent nam daje Intent, getExtras() vadi extras iz tog intenta, getString vadi String
        * vrednost. Imajte na umu da ova vrednost moze biti null.
        * */
        //username = getIntent().getExtras().getString("username");

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

        todoListAdapter = new ArrayAdapter<>(this, R.layout.todo_item, todoList);

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

                /*
                * Ovde pravimo novi TodoItem, kome pridruzujemo tekst todo_a i username trenutnog
                * korisnika.
                *
                * Zatim upisujemo u bazu, pozvianjem metode execute() AsyncTaska. Metodi execute()
                * prosledjujemo objekat tog todo_a, jer njega zelimo da upisemo u bazu.
                * */

                TodoItem item = new TodoItem(todo, username);

                WriteToDatabase dbInsert = new WriteToDatabase();
                dbInsert.execute(item);

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
                intent.putExtra("text", todo);
                intent.setAction(notificationIntent);
                sendBroadcast(intent);

                todoText.setText("");
            }
        });

        Log.d("Lifecycle","onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*
        * Ovde se vrsi konekcija sa bazom.
        * Pravimo objekat TodoItemDBHelper klase, koji cemo kasnije da koristimo za povezivanje na
        * bazu.
        *
        * Zatim citamo iz baze pokretanjem AsyncTask-a sa username-om kao parametrom, koga cemo kasnije
        * da koristimo u where uslovu select operacije.
        * */

        dbHelper = new TodoItemDBHelper(this);

        ReadFromDatabase readFromDatabase = new ReadFromDatabase();
        readFromDatabase.execute(username);

        Log.d("Lifecycle", "onStart");
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

        dbHelper.close();
        Log.d("Lifecycle", "onStop");
    }

    /*
    * Redefinisana metoda onBackPressed definise sta ce se desiti kada korisnik klikne na dugme
    * za vracanje unazad na svom uredjaju. U ovom slucaju, nista se nece desiti. Po default-u,
    * aplikacija unistava activity i prelazi na prethodni activity.
    * */

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Lifecycle","onRestart");
    }

    /*
    * Ovo je onClickListener koji se izvrsava kada korisnik klikne na dugme za logout.
    *
    * Na isti nacin kao i upisivanje, koriscenjem Editor-a, podaci se brisu iz sharedPreferences. Samo
    * sto se u slucaju brisanja podataka koristi metoda remove(), kojoj se prosledjuje ime vrednosti
    * koje zelimo da izbrisemo.
    *
    * Naravno, obavezno je pozivanje apply() metode.
    *
    * Nakon toga, prelazimo preko Intent-a u LoginActivity.
    * */
    public void onLogoutClicked(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.apply();

        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /*
    * Ovo je jos jedan primer implicitnog intent-a.
    *
    * U ovom slucaju, kada korisnik klikne na bilo koji TextView iz ListView-a, salje se e-mail sa
    * tekstom todo_a.
    *
    * Pravimo Intent sa akcijom Intent.ACTION_SEND, koja sluzi kao naznaka svim aplikacijama koje mogu
    * da salju neki tekst da se aktiviraju. Sa setType metodom mi podesavamo tacan tip poruke koju saljemo
    * message/rfc882 oznacava email, pa ce se aktivirati samo aplikacije koje mogu da posalju mail.
    * Nakon toga dodajemo par extras-a, da bismo specificirali subject i tekst poruke.
    *
    * Zatim se pokrece activity, tako sto se napravi chooser, tj. popup preko koga korisnik moze da bira
    * aplikaciju preko koje ce da posalje mail. Nakon toga, pokrece se izabrani activity i korisnik salje
    * mail.
    * */
    public void onSendEmail(View view) {
        TextView textView = (TextView) view;
        String text = textView.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc882");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    /*
    * AsyncTask je apstraktna klasa koja koristi za poslove koje zelimo da obavimo u pozadini, dok
    * korisnik koristi nasu aplikaciju.
    *
    * Uglavnom se koristi za neke asinhrone poslove kao sto su, citanje iz baze, upisivanje u bazu,
    * pozivanje web servisa, i slicno.
    *
    * U ovom slucaju, mi pravimo nasu podklasu koja nasledjuje AsyncTask. Zagradama <> prosledjujemo
    * tip ulaznog objekta, medju objekta i rezultujuceg objekta. To znaci da cemo u metodi execute da
    * prosledimo objekte tipa TodoItem, u ovom slucaju i da ocekujemo da na kraju u metodi
    * onPostExecute bude Boolean.
    * */

    private class WriteToDatabase extends AsyncTask<TodoItem, Void, Boolean> {

        /*
        * Nakon kreiranja objekta klase WriteToDatabase i pokretanje execute() metode, izvrsava se
        * doInBackground. U njoj mi radimo vecinu posla koje zelimo da uradimo unutar ovog AsyncTask-a.
        *
        * Radimo konekciju sa bazom, upisujemo u bazu, proveravamo da li je stvarno upisana ta vrednost
        * i na kraju vracamo boolean.
        *
        * Parametar doInBackground je niz TodoItem objekata neodredjene duzine. Kao rezultat, unutar
        * doInBackground metode mozemo da koristimo items kao niz.
        * */

        @Override
        protected Boolean doInBackground(TodoItem... items) {
            /*
            * Ovim otvaramo konekciju sa bazom i to bazom u koju moze da se upisuju vrednosti.
            * */
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            /*
            * ContentValues objekat predstavlja VALUES kljucnu rec unutar INSERT operacije u SQL-u.
            *
            * Pravimo uredjene parove kolona, vrednost, koje cemo kasnije da unesemo u bazu.
            *
            * items[0] je objekat klase TodoItem, pa ima getere i setere za sva polja. Time mi vadimo
            * vrednost iz tog objekta.
            * */

            ContentValues values = new ContentValues();
            values.put(TodoItemContract.TodoItemEntry.COLUMN_NAME_TEXT, items[0].getText());
            values.put(TodoItemContract.TodoItemEntry.COLUMN_NAME_USER, items[0].getUser());

            /*
            * db.insert je metoda koja se koristi za upisivanje u bazu. Njoj prosledjujemo ime tabele,
            * da li zelimo da unesemo prazan red i koje vrednosti upisujemo. Ona vraca id novog reda.
            * */

            long newRowId = db.insert(TodoItemContract.TodoItemEntry.TABLE_NAME, null, values);

            return newRowId > 0;
        }

        /*
        * OnPostExecute se poziva nakon zavrsetka doInBackground metode. Tu proveravamo da li je unet
        * red u bazu.
        * */
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

    /*
    * Ova klasa je jako slicna klasi WriteToDatabase, samo sto ona ovde prima String kao ulazni objekat
    * i ArrayList<String> kao rezultat.
    * */

    private class ReadFromDatabase extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... items) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            /*
            * Pravimo listu koju cemo da vratimo
            * */

            ArrayList<String> todoList = new ArrayList<>();

            /*
            * Ovde odredjujemo kolone koje zelimo da izvadimo iz baze.
            *
            * Obavezno se pravi kao niz Stringova
            * */

            String[] projection = {
                    TodoItemContract.TodoItemEntry.COLUMN_NAME_TEXT,
            };

            /*
            * Ovo je WHERE uslov SELECT operacije. Dajemo ime kolone za WHERE.
            *
            * ? predstavlja argument where uslova, koga kasnije deklarisemo u selectionArgs.
            *
            * Prilikom izvrsenja select operacije, svaki ? ce biti zamenjen sa vrednoscu iz selectionArgs
            * niza. Ukoliko ima recimo dva znaka upitnika, onda i selectionArgs mora da ima dva clana.
            * Prvi ? ce biti zamenjen sa prvim clanom niza, drugi ? sa drugim.
            * */

            String selection = TodoItemContract.TodoItemEntry.COLUMN_NAME_USER + " = ?";
            String[] selectionArgs = {items[0]};


            /*
            * db.query je metoda koja predstavlja SELECT operaciju. Njoj se prosledjuju parametri:
            * ime tabele, kolone koje zelimo da prikazemo, where uslov, argumenti za where uslov,
            * group by uslov, having uslov i order by.
            *
            * Metoda vraca objekat klase Cursor, koja sluzi kao pokazatelj na red u tabeli.
            * */

            Cursor c = db.query(
                    TodoItemContract.TodoItemEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            /*
            * sa c.moveToNext() prelazimo na sledeci red u tabeli. Zato stavljamo to u while, dok postoji
            * red u tabeli, zelimo da izvlacimo tekst kolonu.
            *
            * Za izvlacenje string-a iz kolone, koristimo getString kojoj prosledjujemo indeks kolone koja nam
            * treba. U ovom slucaju, koristimo c.getColumnIndexOrThrow, koja nam vraca indeks zeljene kolone.
            *
            * Nakon vadjenja teksta iz reda, dodajemo ga u rezultujucu listu.
            * */
            while (c.moveToNext()) {
                String text = c.getString(
                        c.getColumnIndexOrThrow(TodoItemContract.TodoItemEntry.COLUMN_NAME_TEXT)
                );

                todoList.add(text);
            }

            /*
            * Nakon obrade tabele, zatvaramo cursor objekat i vracamo rezultujucu listu.
            * */
            c.close();

            return todoList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);

            /*
            * Ukoliko je procitana lista razlicita od null, svaki element prikazujemo na ekranu,
            * kao jedan todo.
            * */

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
