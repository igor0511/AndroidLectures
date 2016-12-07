package rs.fon.imdbfon;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.HashMap;

/*
* Posto ova aplikacija koristi internet, koga mogu da koriste samo privilegovane aplikacije, u
* obavezi smo da u AndroidManifest.xml fajlu oznacimo da ce aplikacije koristiti internet. U suprotnom,
* u trenutku kreiranja objekta HttpUrlConnection, sistem ce baciti exception.
* */
public class MainActivity extends AppCompatActivity {
    private TextView movieTitle = null;
    private TextView moviePlot = null;
    private TextView movieReleased = null;
    private ImageView movieImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Inicijalizacija
        /*
        * Inicijalizacija svih elemenata iz layout-a u koje cemo da unosimo podatke o filmu.
        *
        * */
        //endregion

        movieImage = (ImageView) findViewById(R.id.movie_image);
        moviePlot = (TextView) findViewById(R.id.movie_plot);
        movieTitle = (TextView) findViewById(R.id.movie_title);
        movieReleased = (TextView) findViewById(R.id.movie_released);

        //region Picasso
        /*
        * Picasso je eksterna biblioteka koja sluzi za upravljanje slikama.
        * Ovde je koristimo kako bismo ubacili sliku za koju imamo url u imageView u nasem layout-u.
        * Koristimo staticku metodu with(Context context) kojoj prosledjujemo activity, zatim pozivamo
        * metodu load, kojoj dajemo URL slike, i onda into(), koja postavlja sliku u ImageView.
        *
        *
        * Videti build.gradle (Module:app) fajl
        * */
        //endregion

        Picasso.with(MainActivity.this).load("http://greentreesarborcareinc.com/wp-content/uploads/2014/01/image-placeholder.jpg").into(movieImage);
        movieReleased.setText("");
        moviePlot.setText("");
        movieTitle.setText("");
    }

    //region SearchDugme
    /*
    * Event handler za klik na search dugme.
    * */
    //endregion
    public void searchClicked(View view) {
        EditText searchEditText = (EditText) findViewById(R.id.search_text);
        //Vadimo string iz EditText-a.
        String searchText = searchEditText.getText().toString();

        if (!searchText.isEmpty()) {
            //replace metoda iz String klase sluzi kada ocemo odredjeni karakter da zamenimo nekim drugim
            //u ovom slucaju, posto za pozvianje api-ja kao parametar t prosledjujemo string koji je odvojen
            //znakom +, mi menjamo svaki space sa +, zatim prosledjujemo to AsyncTask-u.
            searchText = searchText.replace(" ","+");
            GetMovie getMovie = new GetMovie();
            getMovie.execute(searchText);
        }
    }

    //region GetMovie
    /*
    * AsyncTask je abstraktna klasa koja sluzi za implementiranje asinhronog nacina rada aplikacije.
    *
    * Metoda doInBackground je metoda koja se izvrsava na novoj niti, koja je odvojena od glavne (UI) niti.
    * Unutar nje mozemo da koristimo internet konektivnost telefona.
    *
    * Metoda onPostExecute se izvrsava na glavnoj niti. U njoj cemo postaviti rezultat doInBackground-a u
    * layout.
    * */
    //endregion
    private class GetMovie extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {

                //region HttpURLConnection
                /*
                * Za povezivanje sa nekim servisom koristimo objekat HttpURLConnection klase, koju mozemo da dobijemo
                * kada pozivamo openConnection() metodu objekta URL klase. Konstruktoru URL klase prosledjujemo String koji prestavlja
                 * url. Kao parametar t unutar url-a stavljamo string koji prosledujemo kada pozivamo execute metodu AsyncTask-a.
                 *
                 * Za vise o OMDB api-ju, posetiti sajt https://www.omdbapi.com/.
                * */
                //endregion
                URL url = new URL("http://www.omdbapi.com/?t=" + strings[0] + "&plot=full&r=json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //region RequestMethod
                /*
                * connection.setRequestMethod() metodi prosledjujemo http metodu koju cemo koristiti za ovaj zahtev.
                * Neke standardne http metode su GET, POST, PUT, DELETE...
                * */
                //endregion
                connection.setRequestMethod("GET");

                //region BufferedReader
                /*
                * BufferedReader klasa je klasa koja sluzi da iz nekog ulaznog niza bitova vadimo stringove koji su odvojeni
                * znakom za novi red.
                * */
                //endregion
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String response = "";

                //region ResponseCode
                /*
                * Reponse code predstavlja kod http odgovora. Neki od najcescih su 200 (OK), 500 (INTERNAL SERVER ERROR),
                * 404 (NOT FOUND)...
                *
                * Ovde postavljamo uslov ako je odgovor sa kodom 200, onda parsiramo json, jer znamo da je server odgovorio
                * potvrdno na nas zahtev.
                * */
                //endregion
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    response = bufferedReader.readLine();
                    Log.d("response", response);

                    //region JSONObject
                    /*
                    * Kao povratnu vrednost vracamo isparsiran JSON objekat, kako bismo na glavnoj niti, u metodi
                    * onPostExecute() mogli da azuriramo layout sa novim podacima.
                    * */
                    //endregion
                    return  new JSONObject(response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonResponse) {
            super.onPostExecute(jsonResponse);
            try {

                //region JsonResponse
                /*
                * U slucaju da JSON objekat ima polje Poster, mi njega vadimo iz json-a i zatim koristeci Picasso,
                * postavljamo sliku u imageView. Ukoliko nema to polje, postavljamo neku placeholder sliku.
                *
                * Zatim, to isto radimo i sa ostalim poljima, samo sto njih upisujemo u TextView-ove koristeci metodu
                * setText(String s);
                *
                * Nakon izvrsenja onPostExecute metode ovaj asinhroni proces se zavrsava, layout je azuriran
                * i sve je prikazano na ekranu.
                * */
                //endregion
                if (jsonResponse.has("Poster")) {
                    String image = jsonResponse.getString("Poster");
                    Picasso.with(MainActivity.this).load(image).into(movieImage);
                } else {
                    Picasso.with(MainActivity.this).load("http://greentreesarborcareinc.com/wp-content/uploads/2014/01/image-placeholder.jpg").into(movieImage);
                }

                if(jsonResponse.has("Title")) {
                    String title = jsonResponse.getString("Title");
                    movieTitle.setText(title);
                }

                if(jsonResponse.has("Released")) {
                    String released = jsonResponse.getString("Released");
                    movieReleased.setText(released);
                }

                if(jsonResponse.has("Plot")) {
                    String plot = jsonResponse.getString("Plot");
                    moviePlot.setText(plot);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
