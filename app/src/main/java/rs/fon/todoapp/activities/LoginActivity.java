package rs.fon.todoapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import rs.fon.todoapp.R;

public class LoginActivity extends AppCompatActivity {
    private EditText loginText = null;

    private SharedPreferences sharedPreferences = null;

    /*
    * Kao i uvek, onCreate metoda je jedina metoda koja je obavezna u svakom Activity-ju. Ona
    * je prva metoda zivotnog ciklusa i metoda koja se uvek prva izvrsava. U njoj inicijalizujemo sva
    * polja iz klase.
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginText = (EditText) findViewById(R.id.login_name);

        final Button loginButton = (Button) findViewById(R.id.login_button);

        sharedPreferences = getSharedPreferences("todoApp",MODE_PRIVATE);

        /*
        * SetOnClickListener metod iz klase Button postavalja eventListener na taj button. U njoj se
        * definise sta ce se desiti kada korisnik klikne na dugme. On prima parametar OnClickListener
        * interfejsa, koga cemo mi napraviti unutar parametra.
        * */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                * Na ovaj nacin vadimo tekst iz naseg input polja.
                * */
                String name = loginText.getText().toString();

                /*
                * Ovde vrsimo validaciju polja. Dakle, samo ukoliko polje nije prazno, tada se izvrsava
                * prelazak na drugi Activity
                * */

                if(!name.isEmpty()) {

                    /*
                    * Ukoliko zelimo da upisujemo vrednosti u sharedPreferences objekat, koristimo
                    * objekat klase SharedPreferences.Editor. Pozivanjem metode edit() iz sharedPreferences
                    * objekta, dobijamo Editor, kojim kasnije vrsimo upisivanje podataka.
                    * */
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    /*
                    * Ukoliko zelimo da upisemo vrednost u sharedPreferences, koristimo odgovarajucu
                    * put metodu editora. U ovom slucaju, posto upisujemo String, koristmo
                    * putString() metodu, kojoj prosledjujemo ime i vrednost Stringa.
                    *
                    * Nakon upisivanja, obavezno pozivamo editor.apply() kojim se podtvrdjuje upisivanje
                    * u sharedPreferences. To je operacija slicna commit operaciji u upravljanju
                    * transakcija u bazama podataka.
                    * */
                    editor.putString("username",name);
                    editor.apply();

                    /*
                    * Intent je klasa koja sluzi za komuniciranje izmedju Activity-ja. U ovom slucaju,
                    * pravimo takozvani eksplicitni Intent, koji sluzi kada zelimo da iz jednog activity-ja
                    * predjemo u neki activity. Pravimo ga preko konstruktora klase Intent koji prima
                    * dva parametra, Kontekst iz koga ide i klasu Activity-ja u koju ide.
                    *
                    * U ovom slucaju, jer se trenutno nalazimo u okviru OnClickListener interfejsa,
                    * koristimo LoginActivity.this, a da se nalazimo u okviru LoginActivity klase,
                    * koristili bismo samo this.
                    * */
                    Intent intent = new Intent(LoginActivity.this, TodoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    /*
                    * Da bismo mogli da prenosimo vrednosti iz jednog activity-ja u drugi, koristimo
                    * Bundle klasu, koja se pakuje u Extras tog Intenta. Intent klasa ima metodu
                    * putExtra koja prima dva parametra, ime varijable i vrednost. Ime varijable je bitno
                    * kako bismo znali koju vrednost vadimo iz intent-a u drugom activity.
                    * */
                    //intent.putExtra("username",name);


                    /*
                    * startActivity metoda klase Activity se koristi za pokretanje drugog Activity-ja.
                    * Prima kao parametar objekat klase Intent.
                    * */
                    startActivity(intent);

                    finish();
                }
            }
        });

        Log.d("Lifecycle","onCreate");
    }

    /*
    * U nastavku su redefinisane sve metode zivotnog ciklusa Activity-ja. U svakoj metodi se koristi
    * log sa tag-om Lifecycle. Ovaj kod sluzi samo za proveru kako sistem i u kom trenutku poziva koju
    * metodu zivotnog ciklusa, kada prelazi iz jednog Activity-ja u drugi.
    *
    * Kada pokrenete aplikaciju, bilo preko emulatora ili preko uredjaja, u donjem delu Android Studio
    * okruzenja imate Android Monitor. Tu filtrirajte vrednost na Lifecycle i videcete kako se pozivaju
    * metode zivotnog ciklusa.
    * */

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