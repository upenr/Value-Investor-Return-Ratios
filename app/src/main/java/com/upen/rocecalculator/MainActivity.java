/*
Copyright (c) 2024, Upendra Rajan
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree.
 */
package com.upen.rocecalculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText plainText;
    Button queryButton;
    ProgressBar progressBar;
    String netStatus;
    TextView netStatusView, progressUpdateView;
    public static String ticker;
    public static String companyName, companyNameReturn;
    public boolean urlExists = false;
    public static List<String> ebit;
    public static List<String> ibt;
    public static List<String> ni;
    public static List<String> tota;
    public static List<String> tocl;
    public static List<String> totlb;
    public static List<String> dates;
    public static List<String> datesQ;
    public static List<String> ebitQ;
    public static List<String> niQ;
    public static List<String> totaQ;
    public static List<String> toclQ;
    public static List<String> ibtQ;
    public static List<String> totlbQ;
    public static List<String> cfoa;
    public static List<String> capex;
    public static List<String> fcf;
    public static List<String> cfoaQ;
    public static List<String> capexQ;
    public static List<String> fcfQ;
    public static String incomeStmtYearlyURL, incomeStmtQuarterlyURL, balanceSheetYearlyURL, balanceSheetQuarterlyURL, cashFlowStmtYearlyURL, cashFlowStmtQuarterlyURL, companyNameURL;
    public static JSONObject incomeStmtYearlyJson, incomeStmtQuarterlyJson, companyNameDataJson, balanceSheetYearlyJson, balanceSheetQuarterlyJson, cashFlowStmtYearlyJson, cashFlowStmtQuarterlyJson;
    public static JSONArray incomeStmtYearlyArray, incomeStmtQuarterlyArray, balanceSheetYearlyArray, balanceSheetQuarterlyArray, cashFlowStmtYearlyArray, cashFlowStmtQuarterlyArray;
    public static JSONObject incomeStmtYearlyData, incomeStmtQuarterlyData, balanceSheetYearlyData, balanceSheetQuarterlyData, cashFlowStmtYearlyData, cashFlowStmtQuarterlyData;
    public static String incomeStmtYearly, incomeStmtQuarterly, balanceSheetYearly, balanceSheetQuarterly, cashFlowStmtYearly, cashFlowStmtQuarterly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
            ExtendedFloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(view -> {
                Intent findTick = new Intent(MainActivity.this, WebViewFin.class);
                Bundle b = new Bundle();
                b.putString("url", "https://finance.yahoo.com");
                findTick.putExtras(b);
                startActivity(findTick);
            });
            netStatus = chkStatus();
            plainText = findViewById(R.id.plainText);
            //plainText.setText("AAPL");
            progressBar = findViewById(R.id.progressBar);
            netStatusView = findViewById(R.id.conntextview);
            progressUpdateView = findViewById(R.id.progressUpdate);
            netStatusView.setText(netStatus);
            progressBar.setVisibility(View.GONE);
            queryButton = findViewById(R.id.button1);
            progressUpdateView.setTypeface(progressUpdateView.getTypeface(), Typeface.BOLD);
            progressUpdateView.setText("Syntax: Ticker or ticker.stock-exchange\n\nExample tickers: GOOG, BARC.L, INFY.NS, 6758.T, 0293.HK, AIR.PA, CBA.AX, ATD-B.TO");
            queryButton.setEnabled(!netStatus.equalsIgnoreCase("You are not connected to the Internet."));

            final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            queryButton.setOnClickListener(v -> {
                //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                try {
                    inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ticker = plainText.getText().toString().replace(" ", "");
                    //Log.d("Ticker: ", ticker);
                    incomeStmtYearlyURL = "https://query2.finance.yahoo.com/v11/finance/quoteSummary/" + ticker + "?modules=incomeStatementHistory";
                    incomeStmtQuarterlyURL = "https://query2.finance.yahoo.com/v11/finance/quoteSummary/" + ticker + "?modules=incomeStatementHistoryQuarterly";
                    balanceSheetYearlyURL = "https://query2.finance.yahoo.com/v11/finance/quoteSummary/" + ticker + "?modules=balanceSheetHistory";
                    balanceSheetQuarterlyURL = "https://query2.finance.yahoo.com/v11/finance/quoteSummary/" + ticker + "?modules=balanceSheetHistoryQuarterly";
                    cashFlowStmtYearlyURL = "https://query2.finance.yahoo.com/v11/finance/quoteSummary/" + ticker + "?modules=cashflowStatementHistory";
                    cashFlowStmtQuarterlyURL = "https://query2.finance.yahoo.com/v11/finance/quoteSummary/" + ticker + "?modules=cashflowStatementHistoryQuarterly";
                    companyNameURL = "https://query2.finance.yahoo.com/v11/finance/quoteSummary/" + ticker + "?modules=quoteType";
                } catch (Exception e) {
                    Log.e("OnClick Exception: ", String.valueOf(e));
                    Toast.makeText(getApplicationContext(), "There may be an issue.", Toast.LENGTH_LONG).show();
                }
                //String regexp = "^[a-zA-Z0-9_]{1,15}$"; //your regexp here
                String regexp = "^[a-zA-Z\\d_.-]{1,12}$";
                if (ticker.matches(regexp)) {
                    urlExists = true;
                    new GetFinData(getApplicationContext()).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Enter valid stock symbol.", Toast.LENGTH_LONG).show();
                }
            });

        } catch (final Exception e) {
            Log.e("Exception: ", String.valueOf(e));
            Toast.makeText(getApplicationContext(), "There may be an issue.", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Calculate ROCE, ROE and ROA with this Android app (#ReturnsFinder): https://play.google.com/store/apps/details?id=com.upen.rocecalculator";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareBody);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        } else if (id == R.id.action_info) {
            Intent donAct = new Intent(this, InformationActivity.class);
            startActivity(donAct);
            return true;
        } else if (id == R.id.refresh) {
            Intent intent = getIntent();
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
        } else if (id == R.id.exit) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.home) {
        } else if (id == R.id.results) {
            if (companyName != null && !companyName.isEmpty()) {
                Intent resultsAct = new Intent(this, ResultsActivity.class);
                startActivity(resultsAct);
            } else {
                Toast.makeText(getApplicationContext(), "Previous result unavailable. Click Analyze to view results. Then, come back here again.", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.ticker) {
            Intent findTick = new Intent(this, WebViewFin.class);
            Bundle b = new Bundle();
            b.putString("url", "https://finance.yahoo.com");
            findTick.putExtras(b);
            startActivity(findTick);

        } else if (id == R.id.manualCalc) {
            Intent manCalc = new Intent(this, manualCalcActivity.class);
            startActivity(manCalc);

        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Calculate Return on Capital Employed (ROCE), Return on Equity (ROE) and Return on Assets (ROA) of a company. Get the app #StockReturns: https://play.google.com/store/apps/details?id=com.upen.rocecalculator";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareBody);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    class GetFinData extends AsyncTask<Void, Void, String> {
        Context context;

        private GetFinData(Context context) {
            this.context = context.getApplicationContext();
        }

        protected void onPreExecute() {
            try {
                progressBar.setVisibility(View.VISIBLE);
                progressUpdateView.setText("Getting financial data.");

            } catch (final Exception e) {
                Log.e("Exception: ", String.valueOf(e));
                Toast.makeText(getApplicationContext(), "There may be an app issue.", Toast.LENGTH_LONG).show();
            }
        }

        protected String doInBackground(Void... urls) {
            try {
                incomeFetch();
                companyNameReturn = balanceFetch();
                cashflowFetch();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return companyNameReturn;
        }

        protected void onPostExecute(String response) {
            try {

                if (response == null) {
                    Toast.makeText(getApplicationContext(), "There may be an issue with the input provided.", Toast.LENGTH_LONG).show();
                    progressUpdateView.setTextColor(Color.parseColor("#4DB6AC"));
                    progressUpdateView.setText("Done.");
                    progressUpdateView.setText("");
                    progressBar.setVisibility(View.GONE);
                } else if (response.equalsIgnoreCase("")) {
                    progressUpdateView.setTextColor(Color.parseColor("#4DB6AC"));
                    //Log.e("RESPONSE YOU GOT IT", response);
                    progressUpdateView.setText("Does not exist.");
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressUpdateView.setTextColor(Color.parseColor("#4DB6AC"));
                    progressUpdateView.setText("Done.");
                    progressUpdateView.setText("");
                    progressBar.setVisibility(View.GONE);

                    Intent resAct = new Intent(MainActivity.this, ResultsActivity.class);
                    startActivity(resAct);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "There may be an app issue.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String chkStatus() {
        String status = "";
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                status = "  Yahoo Finance has stopped offering free API access.\n  This app will be temporarily unavailable. Please check back later.";
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to mobile data
                }
            } else {
                status = "  Yahoo Finance has stopped offering free API access.\n  This app will be temporarily unavailable. Please check back later.";
            }
        } catch (NullPointerException e) {
            Log.e("Exception: ", String.valueOf(e));
            Toast.makeText(getApplicationContext(), "There was an issue getting the network state. Please reload the app.", Toast.LENGTH_LONG).show();
        } catch (final Exception e) {
            Log.e("Exception: ", String.valueOf(e));
            Toast.makeText(getApplicationContext(), "There was an issue getting Internet connectivity. Please reload the app.", Toast.LENGTH_LONG).show();
        }
        return status;
    }

    private static void incomeFetch() throws IOException {
        /*String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        Document doc = Jsoup.connect(url).userAgent(userAgent1).get();*/
        //Code for Yearly Data
        ebit = new ArrayList<>();
        ibt = new ArrayList<>();
        ni = new ArrayList<>();
        dates = new ArrayList<>();
        datesQ = new ArrayList<>();
        ebitQ = new ArrayList<>();
        niQ = new ArrayList<>();
        ibtQ = new ArrayList<>();

        try {
            incomeStmtYearly = Jsoup.connect(incomeStmtYearlyURL).ignoreContentType(true).execute().body();
            incomeStmtYearlyJson = new JSONObject(incomeStmtYearly);
            //Log.d("Income Statement Yearly Data: ", incomeStmtYearly);
            incomeStmtYearlyArray = incomeStmtYearlyJson.getJSONObject("quoteSummary")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("incomeStatementHistory")
                    .getJSONArray("incomeStatementHistory");

            for (int m = 0; m < incomeStmtYearlyArray.length(); m++) {
                incomeStmtYearlyData = incomeStmtYearlyArray.getJSONObject(m);
                dates.add(incomeStmtYearlyData.getJSONObject("endDate").getString("fmt"));
                ebit.add(incomeStmtYearlyData.getJSONObject("ebit").getString("longFmt").replaceAll("[^a-zA-Z\\d /-]", ""));
                ibt.add(incomeStmtYearlyData.getJSONObject("incomeBeforeTax").getString("longFmt").replaceAll("[^a-zA-Z\\d /-]", ""));
                ni.add(incomeStmtYearlyData.getJSONObject("netIncome").getString("longFmt").replaceAll("[^a-zA-Z\\d /-]", ""));
            }
            //Log.d("Yearly Data: ", dates + String.valueOf(ebit) + ibt + ni);
        } catch (JSONException e) {
            Log.e("err-incomestmt-yearly", "err", e);
        }

        //Code for Quarterly Data
        try {
            incomeStmtQuarterly = Jsoup.connect(incomeStmtQuarterlyURL).ignoreContentType(true).execute().body();
            incomeStmtQuarterlyJson = new JSONObject(incomeStmtQuarterly);
            //Log.d("Quarterly Data: ", incomeStmtQuarterly);
            incomeStmtQuarterlyArray = incomeStmtQuarterlyJson.getJSONObject("quoteSummary")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("incomeStatementHistoryQuarterly")
                    .getJSONArray("incomeStatementHistory");

            for (int n = 0; n < incomeStmtQuarterlyArray.length(); n++) {
                incomeStmtQuarterlyData = incomeStmtQuarterlyArray.getJSONObject(n);
                datesQ.add(incomeStmtQuarterlyData.getJSONObject("endDate").getString("fmt"));
                ebitQ.add(incomeStmtQuarterlyData.getJSONObject("ebit").getString("longFmt").replaceAll("[^a-zA-Z\\d /-]", ""));
                ibtQ.add(incomeStmtQuarterlyData.getJSONObject("incomeBeforeTax").getString("longFmt").replaceAll("[^a-zA-Z\\d /-]", ""));
                niQ.add(incomeStmtQuarterlyData.getJSONObject("netIncome").getString("longFmt").replaceAll("[^a-zA-Z\\d /-]", ""));
            }
            //Log.d("Quarterly Data: ", String.valueOf(datesQ) + String.valueOf(ebitQ) + String.valueOf(ibtQ) + String.valueOf(niQ));
        } catch (JSONException e) {
            Log.e("err-incomestmt-quarterly", "err", e);
        }
    }

    private static String balanceFetch() throws IOException {
        /*String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        Document doc = Jsoup.connect(url).userAgent(userAgent1).get();
        Element h1text = doc.select("h1[class=D(ib) Fz(18px)]").first();
        companyName = h1text.text();
        //Log.d("Company Name", companyName);*/
        //Code for getting Company Name
        tota = new ArrayList<>();
        tocl = new ArrayList<>();
        totlb = new ArrayList<>();
        totaQ = new ArrayList<>();
        toclQ = new ArrayList<>();
        totlbQ = new ArrayList<>();
        try {
            String companyNameData = Jsoup.connect(companyNameURL).ignoreContentType(true).execute().body();
            companyNameDataJson = new JSONObject(companyNameData);
            //Log.d("Company Name Data: ", companyNameData);
            JSONObject result = companyNameDataJson.getJSONObject("quoteSummary")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("quoteType");
            //Log.d("Result", result.toString());
            companyName = result.getString("longName");
            //Log.d("Company Name: ", companyName);
        } catch (JSONException e) {
            Log.e("err-company-name", "err", e);
        }

        //Code for Yearly Balance Sheet Data
        try {

            balanceSheetYearly = Jsoup.connect(balanceSheetYearlyURL).ignoreContentType(true).execute().body();
            balanceSheetYearlyJson = new JSONObject(balanceSheetYearly);
            //Log.d("Balance Sheet Yearly Data: ", balanceSheetYearly);
            balanceSheetYearlyArray = balanceSheetYearlyJson.getJSONObject("quoteSummary")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("balanceSheetHistory")
                    .getJSONArray("balanceSheetStatements");

            for (int m = 0; m < balanceSheetYearlyArray.length(); m++) {
                balanceSheetYearlyData = balanceSheetYearlyArray.getJSONObject(m);
                tota.add(balanceSheetYearlyData.getJSONObject("totalAssets").getString("longFmt").replaceAll("[^a-zA-Z0-9 /-]", ""));
                tocl.add(balanceSheetYearlyData.getJSONObject("totalCurrentLiabilities").getString("longFmt").replaceAll("[^a-zA-Z0-9 /-]", ""));
                totlb.add(balanceSheetYearlyData.getJSONObject("totalLiab").getString("longFmt").replaceAll("[^a-zA-Z0-9 /-]", ""));
            }
            //Log.d("Yearly Balance Sheet Data: ", String.valueOf(tota) + " TOCL: " + String.valueOf(tocl) + String.valueOf(totlb));
        } catch (JSONException e) {
            Log.e("err-balance-sheet-yearly", "err", e);
        }

        //Code for Quarterly Balance Sheet Data
        try {
            balanceSheetQuarterly = Jsoup.connect(balanceSheetQuarterlyURL).ignoreContentType(true).execute().body();
            balanceSheetQuarterlyJson = new JSONObject(balanceSheetQuarterly);
            //Log.d("Balance Sheet Quarterly Data: ", balanceSheetQuarterly);
            balanceSheetQuarterlyArray = balanceSheetQuarterlyJson.getJSONObject("quoteSummary")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("balanceSheetHistoryQuarterly")
                    .getJSONArray("balanceSheetStatements");

            for (int n = 0; n < balanceSheetQuarterlyArray.length(); n++) {
                balanceSheetQuarterlyData = balanceSheetQuarterlyArray.getJSONObject(n);
                totaQ.add(balanceSheetQuarterlyData.getJSONObject("totalAssets").getString("longFmt").replaceAll("[^a-zA-Z0-9 /-]", ""));
                toclQ.add(balanceSheetQuarterlyData.getJSONObject("totalCurrentLiabilities").getString("longFmt").replaceAll("[^a-zA-Z0-9 /-]", ""));
                totlbQ.add(balanceSheetQuarterlyData.getJSONObject("totalLiab").getString("longFmt").replaceAll("[^a-zA-Z0-9 /-]", ""));
            }
            //Log.d("Balance Sheet Quarterly Data: ", String.valueOf(totaQ) + String.valueOf(toclQ) + String.valueOf(totlbQ));
        } catch (JSONException e) {
            Log.e("err-balance-sheet-quarterly", "err", e);
        }
        return companyName;
    }

    //This commented code below extracts every value in the HTML, CSS table. I'm currently not using this.
        /*private static Map<String, List<String>> fetchAll(String url) throws IOException {
        String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        Document doc = Jsoup.connect(url).userAgent(userAgent1).get();
        Map<String, List<String>> data = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<String> currentLiabilities = new ArrayList<>();

        Elements columns = doc.getElementsByAttributeStarting("class");
        Log.d("Column", String.valueOf(columns));
        for (Element column : columns) {
            String value = column.text();
            String[] parts = value.split(" ");
            if (parts.length == 2) {
                dates.add(parts[0]);
                currentLiabilities.add(parts[1]);
            }
        }
        Elements rows = doc.select("div[data-test=fin-row] span");
        for (Element row : rows) {
            Log.d("Row", String.valueOf(row));
            String value = row.text();
            String[] parts = value.split(" ");
            if (parts.length == 2) {
                dates.add(parts[0]);
                currentLiabilities.add(parts[1]);
            }
        }

        Elements dateElements = doc.select("div.Ta.c.Py.6px.Bxz.bb.BdB.Bdc.seperatorColor.Miw.120px.Miw.100px.pnclg.D.ib.Fw.b span");
        Log.d("NewDoc: ", dateElements.text());

        //data.put("dates", dates);
        data.put("CurrentLiabilities", currentLiabilities);
        return data;
    }*/
    private static List<String> cashflowFetch() throws IOException {
        /*String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        Document doc = Jsoup.connect(url).userAgent(userAgent1).get();*/
        /*List<String> cashFlowValues = new ArrayList<>();
        Element freeCashFlow = doc.select("[title=Free Cash Flow]").first();
        Elements freeCashFlowData = freeCashFlow.parent().siblingElements();
        for (Element e : freeCashFlowData) {
            //Log.d("freeCashFlowData", e.text());
            cashFlowValues.add(e.text());
        }*/
        //Code for Yearly Cash Flow Statement Data
        cfoa = new ArrayList<>();
        capex = new ArrayList<>();
        fcf = new ArrayList<>();
        cfoaQ = new ArrayList<>();
        capexQ = new ArrayList<>();
        fcfQ = new ArrayList<>();
        try {
            cashFlowStmtYearly = Jsoup.connect(cashFlowStmtYearlyURL).ignoreContentType(true).execute().body();
            cashFlowStmtYearlyJson = new JSONObject(cashFlowStmtYearly);
            //Log.d("Cash Flow Statement Yearly Data: ", cashFlowStmtYearly);
            cashFlowStmtYearlyArray = cashFlowStmtYearlyJson.getJSONObject("quoteSummary")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("cashflowStatementHistory")
                    .getJSONArray("cashflowStatements");

            for (int m = 0; m < cashFlowStmtYearlyArray.length(); m++) {
                cashFlowStmtYearlyData = cashFlowStmtYearlyArray.getJSONObject(m);
                cfoa.add(cashFlowStmtYearlyData.getJSONObject("totalCashFromOperatingActivities").getString("longFmt").replaceAll("[^a-zA-Z0-9 /-]", ""));
                capex.add(cashFlowStmtYearlyData.getJSONObject("capitalExpenditures").getString("longFmt").replaceAll("[^a-zA-Z0-9 /-]", ""));
            }
            //Log.d("CFOAD", String.valueOf(cfoa));
            for (int i = 0; i < cfoa.size(); i++) {
                double cfoaValue = Double.parseDouble(cfoa.get(i).replaceAll("[^a-zA-Z0-9 /-]", ""));
                double capexValue = Double.parseDouble(capex.get(i).replaceAll("[^a-zA-Z0-9 /-]", ""));
                double result = cfoaValue + capexValue;
                fcf.add(String.valueOf(result));
            }

            //Log.d("Cash Flow Statement Yearly Data: ", String.valueOf(cfoa) + String.valueOf(capex) + String.valueOf(fcf)); //This is the data I want to extract

        } catch (JSONException e) {
            Log.e("cash-flow-stmt-yearly", "err", e);
        }

        //Code for Quarterly Cash Flow Statement Data
        try {
            cashFlowStmtQuarterly = Jsoup.connect(cashFlowStmtQuarterlyURL).ignoreContentType(true).execute().body();
            cashFlowStmtQuarterlyJson = new JSONObject(cashFlowStmtQuarterly);
            //Log.d("Cash Flow Statement Yearly Data: ", cashFlowStmtQuarterly);
            cashFlowStmtQuarterlyArray = cashFlowStmtQuarterlyJson.getJSONObject("quoteSummary")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("cashflowStatementHistoryQuarterly")
                    .getJSONArray("cashflowStatements");

            for (int m = 0; m < cashFlowStmtQuarterlyArray.length(); m++) {
                cashFlowStmtQuarterlyData = cashFlowStmtQuarterlyArray.getJSONObject(m);
                cfoaQ.add(cashFlowStmtQuarterlyData.getJSONObject("totalCashFromOperatingActivities").getString("longFmt"));
                capexQ.add(cashFlowStmtQuarterlyData.getJSONObject("capitalExpenditures").getString("longFmt"));
            }

            for (int i = 0; i < cfoa.size(); i++) {
                Double cfoaValue = Double.parseDouble(cfoaQ.get(i).replaceAll("[^a-zA-Z0-9 /-]", ""));
                Double capexValue = Double.parseDouble(capexQ.get(i).replaceAll("[^a-zA-Z0-9 /-]", ""));
                Double result = cfoaValue + capexValue;
                fcfQ.add(String.valueOf(result));
            }
            //Log.d("Cash Flow Statement Yearly Data: ", String.valueOf(cfoaQ) + String.valueOf(capexQ) + String.valueOf(fcfQ)); //This is the data I want to extract

        } catch (JSONException e) {
            Log.e("cash-flow-stmt-yearly", "err", e);
        }

        return fcf;
    }
}