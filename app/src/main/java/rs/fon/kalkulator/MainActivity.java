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

        bottomDisplay.setText(numberDisplay);
        topDisplay.setText(String.valueOf(resultNumber));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_RESULT, resultNumber);
        savedInstanceState.putString(STATE_CURRENT, currentNumber);
        savedInstanceState.putString(STATE_DISPLAY, numberDisplay);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.advanced_calculator) {
            Toast.makeText(this,"Not yet implemented",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    public void onNumberClicked(View view) {

        if (view instanceof Button) {
            Button button = (Button) view;
            String numberAsString = button.getText().toString();

            currentNumber = currentNumber + numberAsString;
            displayNumbers(numberAsString);
        }
    }

    public void onCEClicked(View view) {
        resultNumber = 0;
        currentNumber = "";
        operation = "";
        numberDisplay = "";

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
        numberDisplay = "";

        currentNumber = "";
        resultNumber = 0;
    }

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
