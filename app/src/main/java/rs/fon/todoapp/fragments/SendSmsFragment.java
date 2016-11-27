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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SendSmsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendSmsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendSmsFragment extends Fragment {
    private static final String PARAM_BODY = "param1";

    private String smsBody;

    private OnFragmentInteractionListener mListener;

    public SendSmsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param smsBody Tekst poruke.
     * @return A new instance of fragment SendSmsFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            smsBody = getArguments().getString(PARAM_BODY);
        }
    }

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

                if(ContextCompat.checkSelfPermission(getActivity(),
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

    public void sendSMS(String phoneNumber, String smsBody) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, smsBody, null, null);

        mListener.onSmsSent();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onSmsSent();
    }
}
