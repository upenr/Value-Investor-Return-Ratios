/*
Copyright (c) 2024, Upendra Rajan
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree.
 */
package com.upen.rocecalculator;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DecimalFormat;

public class manualCalcActivity extends AppCompatActivity {

    EditText ebit1, totalAssets, totalCL, netInc, totaLib, freeCashFlow;
    Button calculate;
    TextView roceResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_calc);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            calculate = (Button) findViewById(R.id.calculate);
            calculate.setEnabled(true);
            ebit1 = (EditText) findViewById(R.id.ebit1);
            totalAssets = (EditText) findViewById(R.id.totalAssets);
            totalCL = (EditText) findViewById(R.id.totalCL);
            netInc = (EditText) findViewById(R.id.netInc);
            totaLib = (EditText) findViewById(R.id.totalLib);
            roceResultView = (TextView) findViewById(R.id.roceResultView);
            freeCashFlow = (EditText) findViewById(R.id.freeCashFlow);

            final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            calculate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //if(ebit1.getText().toString().matches("-?\\d+(\\.\\d+)?")&&totalAssets.getText().toString().matches("-?\\d+(\\.\\d+)?")&&totalCL.getText().toString().matches("-?\\d+(\\.\\d+)?")){
                    String regexp = "^[0-9.]{1,13}$";
                    if (ebit1.getText().toString().matches(regexp) && totalAssets.getText().toString().matches(regexp) && totalCL.getText().toString().matches(regexp) && netInc.getText().toString().matches(regexp) && totaLib.getText().toString().matches(regexp) && freeCashFlow.getText().toString().matches(regexp)) {
                        double ebit = Double.parseDouble(ebit1.getText().toString());
                        double tota1 = Double.parseDouble(totalAssets.getText().toString());
                        double tocl1 = Double.parseDouble(totalCL.getText().toString());
                        double netInc1 = Double.parseDouble(netInc.getText().toString());
                        double totalLib1 = Double.parseDouble(totaLib.getText().toString());
                        double roce = 100 * (ebit / (tota1 - tocl1));
                        double roe = 100 * (netInc1 / (tota1 - totalLib1));
                        double roa = 100 * (netInc1 / (tota1));
                        double fcfroce = 100 * (Double.parseDouble(freeCashFlow.getText().toString()) / (tota1 - tocl1));
                        DecimalFormat f = new DecimalFormat("00.00");
                        roceResultView.setTypeface(roceResultView.getTypeface(), Typeface.BOLD);
                        roceResultView.setText("ROCE: " + f.format(roce) + "%" + "\n\n" + "ROE: " + f.format(roe) + "%" + "\n\n" + "ROA: " + f.format(roa) + "%" + "\n\n" + "FCFROCE: " + f.format(fcfroce) + "%");
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter valid data.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        } catch (NullPointerException e) {
            Log.e("Exception: ", e.getMessage());
            Toast.makeText(getApplicationContext(), "There may be an issue. Please reload the app.", Toast.LENGTH_LONG).show();
        } catch (final Exception e) {
            Log.e("Exception: ", e.getMessage());
            Toast.makeText(getApplicationContext(), "There may be an issue. Please reload the app.", Toast.LENGTH_LONG).show();
        }
    }

}
