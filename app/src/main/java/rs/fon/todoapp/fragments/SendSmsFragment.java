package rs.fon.todoapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import rs.fon.todoapp.R;
import rs.fon.todoapp.activities.TodoActivity;

public class SendSmsFragment extends Fragment {
    //Konstanta preko koje cemo znati da ubacimo/izvucemo iz argumenata fragmenta.
    private static final String PARAM_BODY = "smsBody";

    private String smsBody;

    private OnFragmentInteractionListener mListener;

    public SendSmsFragment() {
        // Required empty public constructor
    }

    //Metoda koja sluzi da napravimo novi fragment, sa nekim argumentima. Argumenti se cuvaju u bundle objektu
    //koga kasnije postavljamo kao argumente tog fragmenta. Videti TodoActivity, trenutak kada postavljamo
    //fragment, korsitimo ovu metodu, sa parametrom tekst poruke.
    public static SendSmsFragment newInstance(String smsBody) {
        SendSmsFragment fragment = new SendSmsFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_BODY, smsBody);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Kao sto smo iz intent-a u activity vadili neke vrednosti,
        //u onCreate metodi fragmenta vadimo argumente koristeci metodu getArguments()
        if (getArguments() != null) {
            smsBody = getArguments().getString(PARAM_BODY);
        }
    }

    //Pravimo layout, kao i u NewTodoFragment klasi
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_sms, container, false);

        Button button = (Button) v.findViewById(R.id.sms_todo_button);
        final EditText todoBody = (EditText) v.findViewById(R.id.sms_todo_body);
        final EditText todoPhone = (EditText) v.findViewById(R.id.sms_todo_number);

        todoBody.setText(smsBody);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = todoPhone.getText().toString();
                String body = todoBody.getText().toString();

                //region Permission
                /*
                * Na ovaj nacin trazimo dozvolu za koriscenje nekih servisa u API nivou 23 i vise.
                *
                * Prvo proveravamo da li je korisnik ranije odoborio slanje sms-a, ukoliko jeste,
                * mozemo poslati poruku, ukoliko nije, koristeci metodu requestPermissions, trazimo
                * dozvolu korisnika. Nakon toga, poziva se metoda onRequestPermissionsResult u
                * TodoActivity klasi, gde se obradjuje odgovor korisnika.
                *
                * Prvi parametar ove metode je aktivnost na koju se odnosi ovaj fragment, drugi je set
                * dozvola koje trazimo, u ovom slucaju je to samo jedna, i requestCode, koji ce nam posluziti
                * kasnije u obradi odgovora.
                * */
                //endregion
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    sendSMS(phoneNumber, body);
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.SEND_SMS},
                            TodoActivity.MY_PERMISSIONS_REQUEST_SMS);
                }
            }
        });
        return v;
    }

    //Za slanje poruke koristimo SmsManager objekat, kome prosledjujemo broj i tekst poruke.
    //Nakon toga, pozivamo metodu onSmsSent iz TodoActivity klase.
    public void sendSMS(String phoneNumber, String smsBody) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, smsBody, null, null);

        mListener.onSmsSent(smsBody);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT >= 23) {
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
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
        void onSmsSent(String smsBody);
    }
}
