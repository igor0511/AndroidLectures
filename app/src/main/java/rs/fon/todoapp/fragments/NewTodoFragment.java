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

/**
 *
 */
public class NewTodoFragment extends Fragment {
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

    public void onAddTodoPressed(String title, String desc) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title, desc);
        }
    }

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

    /**
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title, String desc);
    }
}
