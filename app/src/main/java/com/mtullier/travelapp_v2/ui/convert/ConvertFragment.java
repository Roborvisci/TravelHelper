package com.mtullier.travelapp_v2.ui.convert;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mtullier.travelapp_v2.R;

import java.text.NumberFormat;
import java.util.Locale;

public class ConvertFragment extends Fragment {
    private double percent = 0.15, currencyAmount = 0.0, textListenerPlaceholder; //doubles for calculations
    private EditText editTextAmount; //user input
    private TextView convertedAmount, tipAmount, tipPercentage, enterTextConvert, convertTotal;
    private Spinner originalSpinner, newSpinner; //currency spinners
    private SeekBar tipSeekBar; //tip percent SeekBar
    private String originalCurrency, newCurrency; //holds values of item from spinners
    private Locale localeEnd, localeStart; //holds locale values for currency symbols

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_convert, container, false);

        //Initializes the locales to be used for currency symbols
        /*localeStart = Locale.US;
        localeEnd = Locale.US;*/

        //Initializes the EditText element and sets its listener
        editTextAmount = view.findViewById(R.id.currencyEditText);
        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    //Used to show the user what they're typing
                    textListenerPlaceholder = Double.parseDouble(s.toString()) / 100.0;
                    enterTextConvert.setText(NumberFormat.getCurrencyInstance(localeStart)
                            .format(textListenerPlaceholder));
                    currencyAmount = Double.parseDouble(s.toString());
                } catch(NumberFormatException e) {
                    enterTextConvert.setText("");
                    currencyAmount = 0.0;
                }


                checkCurrencyType();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Initializes the TextView elements
        convertedAmount = view.findViewById(R.id.convertedCurrency);
        tipAmount = view.findViewById(R.id.convertedTip);
        tipPercentage = view.findViewById(R.id.tipPercentage);
        enterTextConvert = view.findViewById(R.id.textViewEnterConvert);
        convertTotal = view.findViewById(R.id.convertedTotal);

        //Initializes the Spinners
        originalSpinner = view.findViewById(R.id.originalCurrencySpinner);
        newSpinner = view.findViewById(R.id.newCurrencySpinner);

        //Sets the Spinner watchers
        originalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //gets the currently selected item from the original currency spinner
                originalCurrency = originalSpinner.getSelectedItem().toString();

                //finds the currency conversion rate
                checkCurrencyType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        newSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //gets the currently selected item from the new currency spinner
                newCurrency = newSpinner.getSelectedItem().toString();

                //find the currency conversion rate
                checkCurrencyType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Initializes the SeekBar and sets its listener
        tipSeekBar = view.findViewById(R.id.seekBar);
        tipSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //finds the percent value that the SeekBar is at, and calculates the tip amount
                percent = progress / 100.0;
                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Sets the values of the two currency types
        setCurrencies();

        return view;
    }

    //Sets the values of the two currency types
    private void setCurrencies() {
        originalCurrency = originalSpinner.getSelectedItem().toString();
        newCurrency = newSpinner.getSelectedItem().toString();
    }

    //Finds the conversion rate for the currency types
    private void checkCurrencyType() {
            try{
                if(originalCurrency.equals(newCurrency) && originalCurrency.equals("USD")) {
                    currencyAmount = conversion(1.0, Locale.US, Locale.US, 15);
                } else if(originalCurrency.equals(newCurrency) && originalCurrency.equals("EUR")) {
                    currencyAmount = conversion(1.0, Locale.GERMANY, Locale.GERMANY, 10);
                } else if(originalCurrency.equals(newCurrency) && originalCurrency.equals("GBP")) {
                    currencyAmount = conversion(1.0, Locale.UK, Locale.UK, 15);
                } else if(originalCurrency.equals(newCurrency) && originalCurrency.equals("CAD")) {
                    currencyAmount = conversion(1.0, Locale.CANADA, Locale.CANADA, 20);
                } else if(originalCurrency.equals(newCurrency) && originalCurrency.equals("CNY")) {
                    currencyAmount = conversion(1.0, Locale.CHINA, Locale.CHINA, 15);
                } else if(originalCurrency.equals("USD") && newCurrency.equals("EUR")) {
                    currencyAmount = conversion(0.88375, Locale.US, Locale.GERMANY, 10);
                } else if(originalCurrency.equals("EUR") && newCurrency.equals("USD")) {
                    currencyAmount = conversion(1.12565, Locale.GERMANY, Locale.US, 15);
                } else if(originalCurrency.equals("USD") && newCurrency.equals("GBP")) {
                    currencyAmount = conversion(0.797131, Locale.US, Locale.UK, 15);
                } else if(originalCurrency.equals("GBP") && newCurrency.equals("USD")) {
                    currencyAmount = conversion(1.2545, Locale.UK, Locale.US, 15);
                } else if(originalCurrency.equals("USD") && newCurrency.equals("CAD")) {
                    currencyAmount = conversion(1.35875, Locale.US, Locale.CANADA, 20);
                } else if(originalCurrency.equals("CAD") && newCurrency.equals("USD")) {
                    currencyAmount = conversion(0.73597, Locale.CANADA, Locale.US, 15);
                } else if(originalCurrency.equals("USD") && newCurrency.equals("CNY")) {
                    currencyAmount = conversion(7.08287, Locale.US, Locale.CHINA, 15);
                } else if(originalCurrency.equals("CNY") && newCurrency.equals("USD")) {
                    currencyAmount = conversion(0.141186, Locale.CHINA, Locale.US, 15);
                } else if(originalCurrency.equals("EUR") && newCurrency.equals("GBP")) {
                    currencyAmount = conversion(0.897290, Locale.GERMANY, Locale.UK, 15);
                } else if(originalCurrency.equals("GBP") && newCurrency.equals("EUR")) {
                    currencyAmount = conversion(1.11447, Locale.UK, Locale.GERMANY, 10);
                } else if(originalCurrency.equals("EUR") && newCurrency.equals("CAD")) {
                    currencyAmount = conversion(1.52948, Locale.GERMANY, Locale.CANADA, 20);
                } else if(originalCurrency.equals("CAD") && newCurrency.equals("EUR")) {
                    currencyAmount = conversion(0.653818, Locale.CANADA, Locale.GERMANY, 10);
                } else if(originalCurrency.equals("EUR") && newCurrency.equals("CNY")) {
                    currencyAmount = conversion(7.97266, Locale.GERMANY, Locale.CHINA, 15);
                } else if(originalCurrency.equals("CNY") && newCurrency.equals("EUR")) {
                    currencyAmount = conversion(0.125429, Locale.CHINA, Locale.GERMANY, 10);
                } else if(originalCurrency.equals("GBP") && newCurrency.equals("CAD")) {
                    currencyAmount = conversion(1.70450, Locale.UK, Locale.CANADA, 20);
                } else if(originalCurrency.equals("CAD") && newCurrency.equals("GBP")) {
                    currencyAmount = conversion(0.586681, Locale.CANADA, Locale.UK, 15);
                } else if(originalCurrency.equals("GBP") && newCurrency.equals("CNY")) {
                    currencyAmount = conversion(8.88509, Locale.UK, Locale.CHINA, 15);
                } else if(originalCurrency.equals("CNY") && newCurrency.equals("GBP")) {
                    currencyAmount = conversion(0.112548, Locale.CHINA, Locale.UK, 15);
                } else if(originalCurrency.equals("CAD") && newCurrency.equals("CNY")) {
                    currencyAmount = conversion(5.21292, Locale.CANADA, Locale.CHINA, 15);
                } else if(originalCurrency.equals("CNY") && newCurrency.equals("CAD")) {
                    currencyAmount = conversion(0.191831, Locale.CHINA, Locale.CANADA, 20);
                }
            } catch(NumberFormatException e) {
                editTextAmount.setText("0");
            }

            calculate();
    }

    //conversion method to make the if statements more palatable; applies the conversion rate,
    //sets the locales, and sets the progress of the seekbar
    private double conversion(double rate, Locale l1, Locale l2, int seek) {
        localeStart = l1;
        localeEnd = l2;
        tipSeekBar.setProgress(seek);
        return (Double.parseDouble(editTextAmount.getText().toString()) * rate) / 100.0;
    }

    //Finds the converted amount, tip amount, total amount, and tip percentage, and sets the
    // GUI elements appropriately
    private void calculate() {
        //Sets the tip percentage view
        tipPercentage.setText(NumberFormat.getPercentInstance().format(percent));

        //calculates the tip and total amount
        double tip = percent * currencyAmount;
        double total = currencyAmount + tip;

        //Sets the different amounts for each TextView
        enterTextConvert.setText(NumberFormat.getCurrencyInstance(localeStart).format(textListenerPlaceholder));
        convertedAmount.setText(NumberFormat.getCurrencyInstance(localeEnd).format(currencyAmount));
        convertTotal.setText(NumberFormat.getCurrencyInstance(localeEnd).format(total));
        tipAmount.setText(NumberFormat.getCurrencyInstance(localeEnd).format(tip));
    }
}