package rs.fon.todoapp.activities;

import android.Manifest;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import rs.fon.todoapp.R;
import rs.fon.todoapp.database.TodoItemContract;
import rs.fon.todoapp.database.TodoItemDBHelper;
import rs.fon.todoapp.fragments.NewTodoFragment;
import rs.fon.todoapp.fragments.SendSmsFragment;
import rs.fon.todoapp.model.TodoItem;
import rs.fon.todoapp.views.notifications.NewTodoNotification;

public class TodoActivity extends AppCompatActivity implements NewTodoFragment.OnFragmentInteractionListener, SendSmsFragment.OnFragmentInteractionListener {
    //region Description
    /*
    * Ovo je konstanta koja sluzi kao Action u Implicitnom Intentu.
    * */
    //endregion
    private static final String notificationIntent = "rs.fon.todo.Notif";
    public static final int MY_PERMISSIONS_REQUEST_SMS = 1;

    private String username;
    private TextView nameText;
    private ListView listView;

    private String tempText;

    //region Description
    /*

    * Ovde su definisani lista todo_a i adapter za tu listu.
    *
    * Lista je obicna ArrayList koja prima Stringove, dakle samo lista stringova.
    *
    * Adapter je klasa koja sluzi kao posrednik izmedju liste i layout-a. Ona za svakog clana
    * liste koja mu je pridruzena pravi layout i postavlja ga u odredjeni view. U nasem slucaju,
    * lista koju cemo mu pridruziti je todoList, layout je layout koji se nalazi u fajlu layout/todo_item.xml,
    * a View u kome ce se prikazati lista TextView-ova je ListView iz activity_todo.xml fajla.
    *
    * */
    //endregion
    private ArrayList<String> todoList;
    private ArrayAdapter<String> todoListAdapter = null;

    private TodoItemDBHelper dbHelper = null;

    private SharedPreferences sharedPreferences = null;

    private NewTodoFragment fragment = null;
    private SendSmsFragment smsFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);


        //region Description
        /*
        * Inicijalizacija SharedPreferences objekta i vadjenje username polja, kako bismo
        * utvrdili da li se korisnik prethodno ulogovao, ukoliko nije, saljemo ga na
        * LoginActivity, kako bi popunio svoje podatke.
        * */
        //endregion

        sharedPreferences = getSharedPreferences("todoApp",MODE_PRIVATE);
        username = sharedPreferences.getString("username","");

        if(username.isEmpty()) {
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
        }
        //region Description
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
        //endregion
        nameText = (TextView) findViewById(R.id.todo_name);
        nameText.setText(username);


        //region Description
        /*
        * Inicijalizacija liste i adapter-a. U konstruktoru adapter-a definisemo kontekst, u ovom
        * slucaju Activity, tj. this, Layout jednog itema, u ovom slucaju fajl res/layout/todo_item.xml,
        * tj. R.layout.todo_item i listu stringova, tj. todoList.
        * */
        //endregion
        todoList = new ArrayList<>();

        todoListAdapter = new ArrayAdapter<>(this, R.layout.todo_item, todoList);

        //region Description
        /*
        * Ovde vadimo listView iz layout-a, a zatim postavljamo adapter na taj ListView pozivanjem
        * metode listview.setAdapter koji prima kao parametar objekat klase Adapter.
        * */
        //endregion
        listView = (ListView) findViewById(R.id.list_view);

        //region ContextMenu
        /*
        * Context Menu predstavlja meni koji se pojavljuje kada uradimo dugi klik na neki element.
        * Mi ovde zelimo da se taj meni pojavi kada mi kliknemo na neki od todo_ova iz liste, pa mi
        * metodi registerforContextMenu() prosledjujemo listView, gde se nalazi lista.
        * */
        //endregion
        registerForContextMenu(listView);
        listView.setAdapter(todoListAdapter);

        //region FAB
        /*
        * FloatingActionButton predstavlja cirkularno dugme koje dolazi u sklopu MaterialDesign standarda.
        * na njega postavljamo onclick Listener koji treba da postavi fragment za dodavanje novog todo_a.
        * */
        //endregion
        FloatingActionButton todoButton = (FloatingActionButton) findViewById(R.id.todo_enter);
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //region Fragment
                /*
                * Za upravljanje fragmentima koristimo objekat FragementManager klase koga dobijamo
                * pozivom getFragmentManager() metode iz Activity-ja. Kako bismo pokrenuli promenu fragmenta,
                * koristimo beginTransaction() metodu, zatim biramo kako zelimo da promenimo fragment.
                * Mozemo odabrati add, remove ili replace metode, kojima prosledjujemo element iz layout-a
                * gde zelimo da stavimo taj fragment, i instancu klase Fragment.
                * Da bi se transakcija konacno izvrsila, koristimo commit() metodu.
                * */
                //endregion
                fragment = new NewTodoFragment();
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container,fragment)
                        .commit();
            }
        });

        Log.d("Lifecycle","onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        //region Description
        /*
        * Ovde se vrsi konekcija sa bazom.
        * Pravimo objekat TodoItemDBHelper klase, koji cemo kasnije da koristimo za povezivanje na
        * bazu.
        *
        * Zatim citamo iz baze pokretanjem AsyncTask-a sa username-om kao parametrom, koga cemo kasnije
        * da koristimo u where uslovu select operacije.
        * */
        //endregion

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

    //region ContextMenu
    /*
    * Ova metoda je callback metoda koja se poziva u trenutku kada treba da se napravi context menu.
    * Ukoliko se context menu pravi unutar naseg listView-a, mi njemu dodeljujemo menu koji se nalazi u
    * res/menu/todo_menu fajlu.
    * */
    //endregion
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list_view) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.todo_menu, menu);
        }
    }

    //region ContextItemSelected
    /*
    * Ova metoda se poziva kada korisnik klikne na jedan todo iz liste.
    * getMenuInfo iz MenuItem klase vraca informacije o tome sto smo kliknuli, od kojih nam je najbitnija
    * pozicija todo_a koga smo kliknuli u listi. Pozicija predstavlja indeks tog todo_a unutar liste, tako da
    * koristeci poziciju tog item-a, mozemo da izvucemo iz liste todo_ova odgovarajuci string koji zelimo da posaljemo
    * kao sms ili email.
    *
    * Kako bismo odredili koju je opciju (sms ili email) odabrao korisnik, koristimo id tog item-a. Id-jeve smo
    * definisali u res/menu/todo_menu fajlu
    * */
    //endregion
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String selectedTodo = todoList.get(info.position);

        switch (item.getItemId()) {
            case R.id.send_sms_id:
                onSendSMS(selectedTodo);
                return true;
            case R.id.send_email_id:
                onSendEmail(selectedTodo);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //region Description
    /*
    * Redefinisana metoda onBackPressed definise sta ce se desiti kada korisnik klikne na dugme
    * za vracanje unazad na svom uredjaju. U ovom slucaju, nista se nece desiti. Po default-u,
    * aplikacija unistava activity i prelazi na prethodni activity.
    * */
    //endregion

    @Override
    public void onBackPressed() {
        //region OnBackPressed
        /*
        * Kada korisnik klikne na back dugme na svom uredjaju, mi proveravamo da li je neki od fragmenata
        * aktivan, ukoliko jeste, njega skidamo, ukoliko nije, izlazimo iz aplikacije.
        * */
        //endregion
        if(fragment != null && fragment.isVisible()) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        } else if(smsFragment != null && smsFragment.isVisible()) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(smsFragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Lifecycle","onRestart");
    }

    //region Description
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
    //endregion
    public void onLogoutClicked(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.apply();

        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //region Description
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
    //endregion
    public void onSendEmail(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc882");
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    //region onsendsms
    /*
    * Ova metoda sluzi samo za postavljanje fragmenta u layout.
    * */
    //endregion
    public void onSendSMS(String text) {
        smsFragment = SendSmsFragment.newInstance(text);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container,smsFragment)
                .commit();
    }

    //region RequestPermissionCallback
    /*
    * Ova metoda se poziva kada korisnik odgovori na dijalog koji se pojavi kada aplikacija zatrazi neku dozvolu ili set dozvola.
    * Prvi parametar je requestcode koji predstavlja requestCode koji smo prosledili kada smo trazili dozvolu, kako bismo u ovoj metodi
    * mogli da razlikujemo razlicite dozvole koje smo trazili.
    *
    * Nakon sto zakljucimo da je requestCode vezan za dozvolu za slanje sms-a, ukoliko je dozvoljeno slanje sms-a,
    * skidamo prethodni fragment i ponovo ga kacimo, samo sto sada, korisik ce moci da posalje sms bez ponovnog trazenja
    * dozvole.
    * */
    //endregion
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(tempText != null && !tempText.isEmpty()) {
                        if(smsFragment != null && smsFragment.isVisible()) {
                            getFragmentManager().beginTransaction().remove(smsFragment).commit();
                        }

                        onSendSMS(tempText);
                    }
                }
            }
        }
    }

    //region Interakcija sa fragmentom
    /*
    * Kada korisnik klikne na dugme na NewTodoFragment-u, izvrsava se ova metoda, koja ubacuje novi
    * todo u listu i upisuje u bazu.
    *
    * Na kraju metode, skida se fragment za ubacivanje novog todo_a.
    *
    * Ova metoda je override-ovana metoda interfejsa iz NewTodoFragment klase.
    * */
    //endregion
    @Override
    public void onFragmentInteraction(String title, String desc) {
        TodoItem todoItem = new TodoItem(title,desc,username);

        todoList.add(todoItem.getText());
        todoListAdapter.notifyDataSetChanged();

        WriteToDatabase writeToDatabase = new WriteToDatabase();
        writeToDatabase.execute(todoItem);

        if(fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    //region Interakcija sa smsFragmentom
    /*
    * Ovo je metoda koja se poziva kada korisnik klikne na dugme za slanje sms-a na SmsFragment-u.
    *
    * Ukoliko postoji smsFragment, skidamo ga, zatim korisnika obavestavamo da je poslata poruka i pravimo notifikaciju,
    * koriscenjem implicitnog intenta sa nasom akcijom, videti AndroidManifest.xml fajl za vise o tome gde i kako se hvata
    * ovaj Intent.
    * */
    //endregion
    @Override
    public void onSmsSent(String smsBody) {
        if(smsFragment != null && smsFragment.isVisible()) {
            getFragmentManager().beginTransaction().remove(smsFragment).commit();
        }

        Toast.makeText(this,"Sms sent",Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        i.setAction("rs.fon.todo.Notif");
        sendBroadcast(i);

    }

    //region Description
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
    //endregion

    private class WriteToDatabase extends AsyncTask<TodoItem, Void, Boolean> {

        //region Description
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
        //endregion

        @Override
        protected Boolean doInBackground(TodoItem... items) {
            //region Description
            /*
            * Ovim otvaramo konekciju sa bazom i to bazom u koju moze da se upisuju vrednosti.
            * */
            //endregion
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //region Description
            /*
            * ContentValues objekat predstavlja VALUES kljucnu rec unutar INSERT operacije u SQL-u.
            *
            * Pravimo uredjene parove kolona, vrednost, koje cemo kasnije da unesemo u bazu.
            *
            * items[0] je objekat klase TodoItem, pa ima getere i setere za sva polja. Time mi vadimo
            * vrednost iz tog objekta.
            * */
            //endregion

            ContentValues values = new ContentValues();
            values.put(TodoItemContract.TodoItemEntry.COLUMN_NAME_TEXT, items[0].getText());
            values.put(TodoItemContract.TodoItemEntry.COLUMN_NAME_USER, items[0].getUser());
            values.put(TodoItemContract.TodoItemEntry.COLUMN_NAME_TITLE, items[0].getTitle());

            //region Description
            /*
            * db.insert je metoda koja se koristi za upisivanje u bazu. Njoj prosledjujemo ime tabele,
            * da li zelimo da unesemo prazan red i koje vrednosti upisujemo. Ona vraca id novog reda.
            * */
            //endregion

            long newRowId = db.insert(TodoItemContract.TodoItemEntry.TABLE_NAME, null, values);

            return newRowId > 0;
        }

        //region Description
        /*
        * OnPostExecute se poziva nakon zavrsetka doInBackground metode. Tu proveravamo da li je unet
        * red u bazu.
        * */
        //endregion
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

    //region Description
    /*
    * Ova klasa je jako slicna klasi WriteToDatabase, samo sto ona ovde prima String kao ulazni objekat
    * i ArrayList<String> kao rezultat.
    * */
    //endregion

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
