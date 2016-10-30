package rs.fon.kalkulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    /*
    * static final polje predstavlja konstantu u Java programskom jeziku.
    *
    * Prve dve konstante oznacavaju dve operacije koje cemo koristiti, sabiranje i oduzimanje.
    * Ove dve operacije su oznacene kao konstante iz razloga sto se u daljem kodu mozemo pozivati na
    * njih, a ne pamtiti vrednost. Tako mozemo izbeci eventualne greske u kucanju operacija.
    *
    * Druge dve konstante oznacavaju stringove koji sluze za pamcenje stanja nase aplikacije. Naime,
    * ukoliko, iz bilo kog razloga, nasa aktivnost bude unistena, tako da mora ponovo da se pozove onCreate
    * metoda, sva polja unutar ove klase ce biti ponovo inicijalizovana, sto ce dovesti do gazenja stanja
    * ove aktivnosti (stanje je prethodni rezultat, broj koji smo kucali i istorija operacija). Da bismo
    * sprecili gubljenje stanja, koristimo onSaveInstanceState metodu, u kojoj Bundle objektu dodeljujemo
    * promenljive stanja sa imenima ovih konstanti.
    *
    * Nakon toga sledi deklaracija svih polja koje cemo koristiti u nasoj aktivnosti.
    *   - operation je trenutna operacija
    *   - currentNumber je trenutni broj kao String
    *   - resultNumber je rezultat operacije
    *   - numberDisplay je string koji predstavlja istoriju operacija koja pise u bottomDisplay
    *
    * bottomDisplay i topDisplay su TextView-ovi iz activity_main layout fajla.
    * */
    private static final String OPERATION_ADD = "addition";
    private static final String OPERATION_SUB = "subtract";
    private static final String STATE_RESULT = "RESULT";
    private static final String STATE_CURRENT = "CURRENT";
    private static final String STATE_DISPLAY = "DISPLAY";

    private String operation;
    private String currentNumber;
    private int resultNumber;
    private String numberDisplay;

    private TextView bottomDisplay = null;
    private TextView topDisplay = null;

    /*
    *
    * onCreate je prvi i jedini obavezni callback zivotnog ciklusa aktivnosti. U njemu se vrsi deklaracija
    * layout i toolbar, kao i inicijalizacija svih polja ove klase. Takodje, unutar njega, mi vadimo,
    * ukoliko postoje, sve vrednosti iz prethodnog stanja. To vrsimo preko parametra onCreate metode savedInstanceState.
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            currentNumber = savedInstanceState.getString(STATE_CURRENT);
            resultNumber = savedInstanceState.getInt(STATE_RESULT);
            numberDisplay = savedInstanceState.getString(STATE_DISPLAY);
        } else {
            currentNumber = "";
            resultNumber = 0;
            numberDisplay = "";
        }

        operation = "";

        bottomDisplay = (TextView) findViewById(R.id.bottom_display);
        topDisplay = (TextView) findViewById(R.id.top_display);

        bottomDisplay.setText("");
        topDisplay.setText("");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_CURRENT,currentNumber);
        savedInstanceState.putInt(STATE_RESULT,resultNumber);
        savedInstanceState.putString(STATE_DISPLAY,numberDisplay);

        super.onSaveInstanceState(savedInstanceState);
    }


    /*
    * Nakon kreiranja toolbara, poziva se ova metoda, u kojoj se poziva inflate metoda sa menijem koji
    * se nalazi u res/drawable/menu.
    *
    * Ovaj postupak postavlja sve ono sto se nalazi u tom fajlu na glavni toolbar
    *
    * */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
    *
    * Ova metoda definise sta se desava kada korisnik klikne na neku opciju unutar menija.
    * Kao sto vidite, preko id-ja item elementa iz meni fajla mozemo da odredimo koju je tacno
    * opciju korisnik odabrao.
    *
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.advanced_calculator) {
            /*
            * makeTest staticka metoda iz Toast klase pravi takozvanu Toast poruku, tekstom
            * "Not yet implemeted", pogledati sta je Toast poruka pokretanjem aplikacije i klikom na
            * opciju iz menija.
            * */

            Toast.makeText(this,"Not yet implemented",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * onAddClicked i onSubtractClicked su metode koje se pozivaju kada korisnik pritisne dugme za
    * sabiranje ili dugme za oduzimanje. Metode su jako slicne.
    *
    * Prvo postavljamo operaciju na operaciju koja je izabrana, zatim pozivamo convertToNumber() metodu,
    * koja pretvara currentNumber iz string-a u int. Zatim se izvrsava executeOperation metoda, koja
    * izvrsava operaciju. Zatim se reinicijalizuje trenutni broj i prikazuje se operacija na ekranu.
    * */

    public void onAddClicked(View view) {
        if (operation.isEmpty()) {
            operation = OPERATION_ADD;
        }

        int res = convertToNumber();
        executeOperation(operation, res);

        operation = OPERATION_ADD;

        currentNumber = "";

        displayNumbers("+");
    }

    public void onSubtractClicked(View view) {
        if (operation.isEmpty()) {
            operation = OPERATION_SUB;
        }

        int res = convertToNumber();
        executeOperation(operation, res);

        operation = OPERATION_SUB;

        currentNumber = "";

        displayNumbers("-");
    }

    /*
    * Ovde se iz Button-a koji predstavlja dugme na kome se nalazi broj vadi vrednost njegovog stringa,
    * tj. broj koji se nalazi na tom dugmetu, zatim se trenutnom broj pridruzuje ta cifra, nakon cega
    * se ta cifra prikazuje na ekranu.
    *
    * */

    public void onNumberClicked(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            String numberAsString = button.getText().toString();

            currentNumber = currentNumber + numberAsString;
            displayNumbers(numberAsString);
        }
    }

    /*
    * Event za dugme koje vraca sve na prvobitno stanje.
    * */

    public void onCEClicked(View view) {
        resultNumber = 0;
        currentNumber = "";
        operation = "";
        numberDisplay = "";

        bottomDisplay.setText("");
        topDisplay.setText("");
    }

    /*
    * Event za dugme jednako. Izvrsava se operacija slicno kao u eventovima za operacije, samo sto se
    * nakon toga sa gornjeg TextView rezultat prebacuje na donji i reinicijalizuju se sva ostala polja.
    *
    * */

    public void onEqualsClicked(View view) {
        int res = convertToNumber();

        executeOperation(operation, res);
        currentNumber = "";

        bottomDisplay.setText(String.valueOf(resultNumber));
        topDisplay.setText("0");
        operation = "";
        numberDisplay = "";

        currentNumber = "";
        resultNumber = 0;
    }

    /*
    * Metoda za prikaz na ekranu.
    * */
    private void displayNumbers(String character) {
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        String[] operations = {"-","+"};

        for (String number : numbers) {
            if (number.equals(character)) {
                numberDisplay += character;
                bottomDisplay.setText(numberDisplay);
                return;
            }
        }

        /*
        * Ovde je ukljucen slucaj kada korisnik ukuca dve operacije za redom. Metoda brise prethodnu
        * i stavlja novu operaciju.
        * */

        if (numberDisplay.length() > 0) {
            if (Arrays.asList(operations).contains(numberDisplay.substring(numberDisplay.length() -1))) {
                numberDisplay = numberDisplay.substring(0,numberDisplay.length() - 1);
                numberDisplay += character;
            } else {
                numberDisplay += character;
            }
        } else {
            numberDisplay += character;
        }

        bottomDisplay.setText(numberDisplay);
    }

    private int convertToNumber() {
        try {
            return Integer.parseInt(currentNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /*
    * Metoda koja izvrsava operaciju, tako sto proverava koja je operacija u pitanju, a zatim
    * je izvrsi sa parametrom number i pridruzuje rezultat resultNumber polju. Rezultat upisuje u
    * gornji TextView.
    * */

    private void executeOperation(String operation, int number) {
        switch (operation) {
            case OPERATION_ADD:
                resultNumber += number;
                break;
            case OPERATION_SUB:
                if (resultNumber == 0) {
                    resultNumber = number;
                } else {
                    resultNumber -= number;
                }
                break;
            default:
                break;
        }

        topDisplay.setText(String.valueOf(resultNumber));
    }
}
