package rs.fon.kalkulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String OPERATION_ADD = "addition";
    private static final String OPERATION_SUB = "subtract";

    private String operation;
    private String currentNumber;
    private int resultNumber = 0;

    private TextView bottomDisplay = null;
    private TextView topDisplay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        operation = "";
        currentNumber = "";

        bottomDisplay = (TextView) findViewById(R.id.bottom_display);
        topDisplay = (TextView) findViewById(R.id.top_display);

        bottomDisplay.setText("");
        topDisplay.setText("");
    }

    public void onAddClicked(View view) {
        if(operation.isEmpty()) {
            operation = OPERATION_ADD;
        }

        int res = convertToNumber();
        executeOperation(operation,res);

        operation = OPERATION_ADD;

        currentNumber = "";

        Button button = (Button) view;

        bottomDisplay.append(button.getText());
    }

    public void onSubtractClicked(View view) {
        if(operation.isEmpty()) {
            operation = OPERATION_SUB;
        }

        int res = convertToNumber();
        executeOperation(operation,res);

        operation = OPERATION_SUB;

        currentNumber = "";

        Button button = (Button) view;

        bottomDisplay.append(button.getText());
    }

    public void onNumberClicked(View view) {
        if(view instanceof Button) {
            Button button = (Button) view;
            String numberAsString = button.getText().toString();

            currentNumber = currentNumber + numberAsString;

            bottomDisplay.append(numberAsString);
        }
    }

    public void onCEClicked(View view) {
        resultNumber = 0;
        currentNumber = "";
        operation = "";

        bottomDisplay.setText("");
        topDisplay.setText("");
    }

    public void onEqualsClicked(View view) {
        int res = convertToNumber();

        executeOperation(operation, res);
        currentNumber = "";

        bottomDisplay.setText(String.valueOf(resultNumber));
        topDisplay.setText("0");
        operation = "";

        currentNumber = resultNumber + "";
    }

    private int convertToNumber() {
        try {
            return Integer.parseInt(currentNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void executeOperation(String operation, int number) {
        switch (operation) {
            case OPERATION_ADD:
                resultNumber += number;
                break;
            case OPERATION_SUB:
                if(resultNumber == 0) {
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
