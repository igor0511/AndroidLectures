package rs.fon.todoapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import rs.fon.todoapp.R;

//region Fragment
/**
 * Fragment predstavlja komponentu koja moze da se iskoristi u vise Activity-ja, na vise mesta u istom
 * Activity-ju koja moze ali ne mora da ima layout vezan za nju. U ovom slucaju, mi fragment koristimo
 * kako bismo prikazali formu za ubacivanje novog todo_a.
 *
 * Unutar fragmenta definisemo interfejs koji predstavlja jedini nacin komunikacije izmedju Activity-ja
 * i Fragmenta.
 *
 */
//endregion
public class NewTodoFragment extends Fragment {
    //region mListener
    /*
    * mListener predstavlja objekat gde cemo cuvati referencu na nas activity.
    *
    * TodoActivity klasa impelementira interfejs ove klase, tako da cemo taj activity kastovati
    * kao objekat ovog interfejsa, sto znaci da cemo dobiti samo metode koje su definisane unutar
    * tog interfejsa, a koje su override-ovane unutar TodoActivity klase.
    * */
    //endregion
    private OnFragmentInteractionListener mListener;

    private Button addNewTodoButton = null;
    private EditText titleEditText = null;
    private EditText descEditText = null;

    public NewTodoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
    * onCreateView metoda predstavalja vazan deo zivotnog ciklusa fragmenta, gde se definise layout
    * tog fragmenta i svi elementi koji se nalaze unutar njega i gde se hvataju svi dogadjaji, kao na
    * primer klik na dugme.
    *
    * Ova metoda vraca View objekat, a kao parametar prima inflater, koga koristimo da od layout
    * fajla napravimo view. Na novi View koga smo kreirali mozemo da kacimo onClickListener-e, kao i da
    * vrsimo obradu teksta u okviru EditText polja.
    *
    * Kada smo ceo layout podesili, vracamo ga kao povratnu vrednost ove metode
    * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_todo, container, false);

        addNewTodoButton = (Button) v.findViewById(R.id.new_todo_button);
        titleEditText = (EditText) v.findViewById(R.id.new_todo_title);
        descEditText = (EditText) v.findViewById(R.id.new_todo_text);

        addNewTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                String desc = descEditText.getText().toString();

                Log.d("Title", title);
                Log.d("Title", desc);

                onAddTodoPressed(title, desc);
            }
        });

        return v;
    }

    //region onAddTodoPressed
    /*
    * Metoda koja poziva metodu iz interfejsa i time ostvaruje interakciju fragmenta sa activity-jem.
    * */
    //endregion
    public void onAddTodoPressed(String title, String desc) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title, desc);
        }
    }

    //region onAttach
    /*
    * onAttach je vazna metoda zivotnog ciklusa fragmenta, koja se poziva kada se fragment veze za
    * activity. Kao parametar dobijamo context, koji u ovom slucaju prestavalja nas activity, pa njega
    * kastujemo u interfejs i pridruzujemo mListener polju.
    *
    * Ponovljena metoda onAttach(Activity activity) prestavlja metodu koja vazi samo API nivoe 23 i nize.
    * Ova metoda se smatra zastarelom u API nivou 23 i kasnije.
    * */
    //endregion
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT >= 23)
            if (context instanceof OnFragmentInteractionListener) {
                System.out.println("context" + context);
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            if (activity instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) activity;
            } else {
                throw new RuntimeException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title, String desc);
    }
}
