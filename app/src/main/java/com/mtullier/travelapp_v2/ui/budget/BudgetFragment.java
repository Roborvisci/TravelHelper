package com.mtullier.travelapp_v2.ui.budget;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mtullier.travelapp_v2.R;

import java.text.NumberFormat;

public class BudgetFragment extends Fragment {
    private static final String BUDGET = "budget"; //SharedPreferences name

    private SharedPreferences budgetPref; //users budget amount is stored
    private BudgetDatabaseHelper db; //gets entry data for the budget
    private EditText editTextType, editTextAmount, editTextDate, editTextBudget; //user inputs
    private TextView spentTextView, leftTextView, amountTextView, enterAmountView; //program outputs

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        //SharedPreferences for getting the budget amount set by the user
        budgetPref = this.requireActivity().getSharedPreferences(BUDGET, Context.MODE_PRIVATE);

        //DatabaseHelper
        db = new BudgetDatabaseHelper(getActivity());

        //Initialize the EditText elements
        editTextType = view.findViewById(R.id.editTextType);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextBudget = view.findViewById(R.id.editTextBudget);

        //Sets the TextWatcher to format the Amount
        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Used to format the AMOUNT properly, to reduce confusion from the user
                try {
                    double textListenerPlaceHolder = Double.parseDouble(s.toString()) / 100.0;
                    enterAmountView.setText(NumberFormat.getCurrencyInstance()
                            .format(textListenerPlaceHolder));
                } catch(NumberFormatException e) {
                    enterAmountView.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Initialize the TextView elements
        spentTextView = view.findViewById(R.id.spentTextView);
        leftTextView = view.findViewById(R.id.leftTextView);
        amountTextView = view.findViewById(R.id.budgetTextView);
        enterAmountView = view.findViewById(R.id.textViewEnterAmount);

        //Function buttons
        Button addDateBtn = view.findViewById(R.id.buttonAddData);
        Button retrieveDataBtn = view.findViewById(R.id.buttonRetrieveData);
        Button resetDataBtn = view.findViewById(R.id.buttonResetData);
        Button setBudgetBtn = view.findViewById(R.id.buttonSetBudget);

        //Set onClickListeners for the buttons
        addDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();

                //hides the keyboard after the button is tapped
                ((InputMethodManager) requireActivity().getSystemService
                        (Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                        (v.getWindowToken(), 0);
            }
        });
        retrieveDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveData();
            }
        });
        resetDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetData();
            }
        });
        setBudgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gets budget amount from user and assigns a key
                String budget = editTextBudget.getText().toString();
                String key = "1";

                //sets the SharedPreference value "budget"
                setBudget(key, budget);
                //resets the EditText for the budget
                editTextBudget.setText("");

                //sets the appropriate values to the TextViews
                setBudgetTexts();

                //hides the keyboard after the button is tapped
                ((InputMethodManager) requireActivity().getSystemService
                        (Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                        (v.getWindowToken(), 0);
            }
        });

        //Set the budget bar to its appropriate value
        setBudgetTexts();

        return view;
    }

    //used to add the budget entry to the SQLite table
    private void addData() {
        //Ensures the user enters all info necessary for the table entry
        if(editTextType.getText().toString().equals("")) Toast.makeText(this.getActivity(),
                "Please add a type of purchase", Toast.LENGTH_LONG).show();
        else if(editTextAmount.getText().toString().equals("")) Toast.makeText(this.getActivity(),
                "Please add the amount you spent on the purchase", Toast.LENGTH_LONG).show();
        else if(editTextDate.getText().toString().equals("")) Toast.makeText(this.getActivity(),
                "Please add the date you made the purchase", Toast.LENGTH_LONG).show();
        //Checks to make sure data was inserted successfully, true = success
        else {
            //Converts the input from the Amount EditText so that it displays correctly
            //when grabbed from the database
            double numberConvertPlaceholder = Double.parseDouble
                    (editTextAmount.getText().toString()) / 100.0;

            boolean isInserted = db.insertData(editTextType.getText().toString(),
                    numberConvertPlaceholder,
                    editTextDate.getText().toString());

            //Resets the EditTexts so that the user can make another entry
            editTextType.setText("");
            editTextAmount.setText("");
            editTextDate.setText("");

            //Lets the user know if their entry was inserted successfully
            if(isInserted) Toast.makeText(this.getActivity(),
                    "Data was inserted successfully", Toast.LENGTH_LONG).show();
            else Toast.makeText(this.getActivity(),
                    "Data not inserted, please try again", Toast.LENGTH_LONG).show();
        }

        //Set the budget bar to its appropriate value
        setBudgetTexts();
    }

    //used to retrieve data from the SQLite table
    private void retrieveData() {
        //used to iterate over the table
        Cursor res = db.retrieveData();

        //checks if the table has entries
        if(res.getCount() == 0) showMessage("Error", "No data found! Try adding a budget entry");
        else {
            //used to build the strings for each entry
            StringBuilder builder = new StringBuilder();

            //iterates over the table and builds the entry
            while(res.moveToNext()) {
                builder.append("Type: ").append(res.getString(1)).append("\n");
                builder.append("Amount: $").append(res.getString(2)).append("\n");
                builder.append("Date: ").append(res.getString(3)).append("\n\n");
            }

            //shows all entries found during the iteration
            showMessage("Budget Entries", builder.toString());
        }
    }

    //used to reset the SQLite table to empty
    private void resetData() {
        //deletes the contents of the table
        db.deleteData();

        //reset the budget amount
        SharedPreferences.Editor preferencesEditor = budgetPref.edit();
        preferencesEditor.remove("1");
        preferencesEditor.apply();

        //Resets the EditTexts so that the user can make another entry
        editTextType.setText("");
        editTextAmount.setText("");
        editTextDate.setText("");

        //Set the budget bar to its appropriate value
        setBudgetTexts();
    }

    //used to set the budget in SharedPreferences
    private void setBudget(String tag, String budget) {
        SharedPreferences.Editor preferencesEditor = budgetPref.edit();
        preferencesEditor.putString(tag, budget);
        preferencesEditor.apply();
    }

    //sets the budget bar to appropriate values
    private void setBudgetTexts() {
        //tries to set budget amount, catches exception if an amount hasn't been defined yet
        double budgetAmount;
        try {
            budgetAmount = Double.parseDouble(budgetPref.getString("1", "budget"));
        } catch(NumberFormatException e) {
            budgetAmount = 0.0;
        }

        //gets the sum of the AMOUNT column
        double budgetSpent = db.getTotalSpent();
        //calculates the amount left from the user's set budget
        double budgetLeft = budgetAmount - budgetSpent;

        //sets the TextView elements accordingly
        spentTextView.setText(NumberFormat.getCurrencyInstance().format(budgetSpent));
        amountTextView.setText(NumberFormat.getCurrencyInstance().format(budgetAmount));
        leftTextView.setText(NumberFormat.getCurrencyInstance().format(budgetLeft));
    }

    //builds the AlertDialog for showing the entries
    private void showMessage(String title, String message) {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this.requireActivity());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}