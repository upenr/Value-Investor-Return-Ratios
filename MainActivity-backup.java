package com.upen.rocecalculator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLHandshakeException;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText plainText;
    Button queryButton;
    ProgressBar progressBar;
    String netStatus;
    TextView netStatusView, progressUpdateView;
    public static String ticker;
    public static String incomeStmt = "";
    public static String balanceSheet = "";
    public static String companyName = "";
    public static String cashFlowStmt = "";
    public static String incomeDataQ = "";
    public static String incomeData = "";
    public static String balanceData = "";
    public static String cashFlowData = "";
    public static String cashFlowDataQ = "";
    public static String balanceDataQ = "";
    public boolean urlExists = false;
    public static URL u, u1, u2;

    public static String pageContent = "";
    private static final String PAGE_URL = "https://finance.yahoo.com/quote/AAPL/financials?p=AAPL&guccounter=2";
    private static final String DATA_URL = "https://query1.finance.yahoo.com/ws/fundamentals-timeseries/v1/finance/timeseries/AAPL?lang=en-US&region=US&symbol=AAPL&padTimeSeries=true&type=quarterlyEBIT&merge=false&period1=493590046&period2=1674660504&corsDomain=finance.yahoo.com";

    private static final String REGEX_YAHOO_PAGE_EBIT = "^.*ttm</span>.*?<span>(.*?)</span>.*?<span>(.*?)</span>.*?<span>(.*?)</span>.*?<span>(.*?)</span>.*?EBIT</span></div><div.*?<span>(.*?)</span>.*?<span>(.*?)</span>.*?<span>(.*?)</span>.*?<span>(.*?)</span>.*?<span>(.*?)</span>.*$";
    private static final Pattern PATTERN_YAHOO_PAGE_REGEX = Pattern.compile(REGEX_YAHOO_PAGE_EBIT, Pattern.DOTALL);

    private static final Gson GSON = new Gson();

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(new Locale("en", "US"));

    public static URL pageUrl;
    public static HttpURLConnection connection;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent findTick = new Intent(MainActivity.this,WebViewFin.class);
                    Bundle b = new Bundle();
                    b.putString("url", "https://finance.yahoo.com");
                    findTick.putExtras(b);
                    startActivity(findTick);
                }
            });
            netStatus = chkStatus();
            plainText = (EditText) findViewById(R.id.plainText);
            plainText.setText("AAPL");
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            netStatusView = (TextView) findViewById(R.id.conntextview);
            progressUpdateView = (TextView) findViewById(R.id.progressUpdate);
            netStatusView.setTextColor(Color.parseColor("#4DB6AC"));
            netStatusView.setText(netStatus);
            progressBar.setVisibility(View.GONE);
            queryButton = (Button) findViewById(R.id.button1);
            progressUpdateView.setText("Syntax: Ticker or Ticker(DOT)Stock-exchange\n\nExample tickers: GOOG, BARC.L, INFY.NS, 6758.T, 0293.HK, AIR.PA, CBA.AX, ATD-B.TO");
            if (netStatus.equalsIgnoreCase("You are not connected to the Internet.")) {
                queryButton.setEnabled(false);
            } else {
                queryButton.setEnabled(true);
            }

            final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            queryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ticker = plainText.getText().toString();
                    try {
                        u = new URL("https://finance.yahoo.com/quote/" + ticker + "/financials?p=" + ticker + "&guccounter=2");
                        u1 = new URL("https://finance.yahoo.com/quote/" + ticker + "/balance-sheet?p=" + ticker + "&guccounter=2");
                        u2 = new URL("https://finance.yahoo.com/quote/" + ticker + "/cash-flow?p=" + ticker + "&guccounter=2");
                    } catch (MalformedURLException e) {
                        Log.e("Exception: ", e.getMessage());
                        Toast.makeText(getApplicationContext(), "There may be an issue. Please reload the app.", Toast.LENGTH_LONG).show();
                    }
                    //String regexp = "^[a-zA-Z0-9_]{1,15}$"; //your regexp here
                    String regexp = "^[a-zA-Z0-9_.-]{1,12}$";
                    if (ticker.matches(regexp)) {
                        urlExists = true;
                        new MainActivity.GetFinData(getApplicationContext()).execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter valid stock symbol.", Toast.LENGTH_LONG).show();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            if (incomeStmt != null && !incomeStmt.isEmpty()) {
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
            String shareBody = "Calculate Return on Capital Employed (ROCE), Return on Equity (ROE) and Return on Assets (ROA) of a company. Get the app #ValueFinder: https://play.google.com/store/apps/details?id=com.upen.rocecalculator";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareBody);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class GetFinData extends AsyncTask<Void, Void, String> {
        Context context;
        final int SUCCESS = 0;
        final int FAILURE = SUCCESS + 1;

        private GetFinData(Context context) {
            this.context = context.getApplicationContext();
        }

        protected void onPreExecute() {
            try {
                progressBar.setVisibility(View.VISIBLE);
                progressUpdateView.setText("Getting financial data.");

            } catch (final Exception e) {
                Log.e("Exception: ", e.getMessage());
                Toast.makeText(getApplicationContext(), "There may be an issue. Please reload the app.", Toast.LENGTH_LONG).show();
            }
        }

        protected String doInBackground(Void... urls) {
            try {
                //incomeStmt = getIncomeStmtYear(u.toString());
                //balanceSheet = getBalanceSheetYear(u1.toString());
                //new code
                //List<String> values = fetch("https://finance.yahoo.com/quote/AAPL/financials?p=AAPL&guccounter=2");
                Map<String, List<String>> values = fetch("https://finance.yahoo.com/quote/AAPL/financials?p=AAPL&guccounter=2");
                //pageContent = String.valueOf(fetch(PAGE_URL));
                Log.d("Page Content: ", String.valueOf(values));
                //new code ends
            } catch (SSLHandshakeException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

           /* try{
                HttpURLConnection huc =  (HttpURLConnection)  u.openConnection ();
                huc.setRequestMethod ("GET");  //OR  huc.setRequestMethod ("HEAD");
                huc.connect();
                int code = huc.getResponseCode() ;
                Log.e("URL code", Integer.toString(code));
                if (code !=404) {
                    urlExists = true;
                } else {
                    urlExists = false;
                }

            }catch (final Exception e) {
                Log.e("Exception: ", e.getMessage());
                Toast.makeText(getApplicationContext(), "There may be an issue with the symbol.", Toast.LENGTH_LONG).show();
            }

            if (urlExists){
                try {
                    //sample URL: https://finance.yahoo.com/quote/AAPL/financials?p=AAPL
                    incomeStmt=readStringFromURL(u.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                incomeStmt = "404";

            }*/
            return pageContent;
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
                Toast.makeText(getApplicationContext(), "There may be an issue. Please reload the app.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String chkStatus() {
        String status = "";
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                status = "You are connected with a WiFi network.";
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to mobile data
                }
            } else {
                status = "You are not connected to the Internet.";
            }
        } catch (NullPointerException e) {
            Log.e("Exception: ", e.getMessage());
            Toast.makeText(getApplicationContext(), "There was an issue getting the network state. Please reload the app.", Toast.LENGTH_LONG).show();
        } catch (final Exception e) {
            Log.e("Exception: ", e.getMessage());
            Toast.makeText(getApplicationContext(), "There was an issue getting Internet connectivity. Please reload the app.", Toast.LENGTH_LONG).show();
        }
        return status;
    }

    private static JSONArray getIncomeTable(JSONObject json) throws JSONException {
        JSONArray table = (JSONArray) json.getJSONObject("context")
                .getJSONObject("dispatcher")
                .getJSONObject("stores")
                .getJSONObject("QuoteSummaryStore")
                .getJSONObject("incomeStatementHistory") //Just replace this with incomeStatementHistoryQuarterly to get quarterly data
                .getJSONArray("incomeStatementHistory");
        return table;
    }

    private static JSONArray getIncomeTableQ(JSONObject json) throws JSONException {
        JSONArray table = (JSONArray) json.getJSONObject("context")
                .getJSONObject("dispatcher")
                .getJSONObject("stores")
                .getJSONObject("QuoteSummaryStore")
                .getJSONObject("incomeStatementHistoryQuarterly")
                .getJSONArray("incomeStatementHistory");
        return table;
    }

    private static JSONArray getBalanceTable(JSONObject json) throws JSONException {
        JSONArray table = (JSONArray) json.getJSONObject("context")
                .getJSONObject("dispatcher")
                .getJSONObject("stores")
                .getJSONObject("QuoteSummaryStore")
                .getJSONObject("balanceSheetHistory") //Just replace this with balanceSheetHistoryQuarterly to get quarterly data
                .getJSONArray("balanceSheetStatements");
        return table;
    }

    private static JSONArray getBalanceTableQ(JSONObject json) throws JSONException {
        JSONArray table = (JSONArray) json.getJSONObject("context")
                .getJSONObject("dispatcher")
                .getJSONObject("stores")
                .getJSONObject("QuoteSummaryStore")
                .getJSONObject("balanceSheetHistoryQuarterly")
                .getJSONArray("balanceSheetStatements");
        return table;
    }
    private static JSONArray getCashFlowTable(JSONObject json) throws JSONException {
        JSONArray table = (JSONArray) json.getJSONObject("context")
                .getJSONObject("dispatcher")
                .getJSONObject("stores")
                .getJSONObject("QuoteSummaryStore")
                .getJSONObject("cashflowStatementHistory")
                .getJSONArray("cashflowStatements");
        return table;
    }

    private static JSONArray getCashFlowTableQ(JSONObject json) throws JSONException {
        JSONArray table = (JSONArray) json.getJSONObject("context")
                .getJSONObject("dispatcher")
                .getJSONObject("stores")
                .getJSONObject("QuoteSummaryStore")
                .getJSONObject("cashflowStatementHistoryQuarterly")
                .getJSONArray("cashflowStatements");
        return table;
    }

    private static String[] getRow(JSONArray table, String name) throws JSONException {
        String[] values = new String[table.length()];
        for (int i = 0; i < table.length(); i++) {
            JSONObject jo = table.getJSONObject(i).getJSONObject(name);
            values[i] = jo.has("longFmt") ? jo.get("longFmt").toString() : "-";
        }
        return values;
    }

    private static String[] getDates(JSONArray table) throws JSONException {
        String[] values = new String[table.length()];
        for (int i = 0; i < table.length(); i++) {
            values[i] = table.getJSONObject(i).getJSONObject("endDate")
                    .get("fmt").toString();
        }
        return values;
    }

    public static Map<String, Map<String, String>> getTableNames() {
        final Map<String, String> revenue = new LinkedHashMap<String, String>() {
            {
                put("TotalRevenue", "totalRevenue");
            }

            {
                put("CostofRevenue", "costOfRevenue");
            }

            {
                put("GrossProfit", "grossProfit");
            }
        };
        final Map<String, String> incomeCO = new LinkedHashMap<String, String>() {
            {
                put("EarningsBeforeInterestandTaxes", "ebit");
            }

            {
                put("incomeBeforeTax", "incomeBeforeTax");
            }

            {
                put("netIncome", "netIncome");
            }

            {
                put("operatingIncome", "operatingIncome");
            }
        };
        Map<String, Map<String, String>> allTableNames = new LinkedHashMap<String, Map<String, String>>() {
            {
                put("Revenue", revenue);
            }

            {
                put("incomeCO", incomeCO);
            }
        };
        return allTableNames;
    }

    public static Map<String, Map<String, String>> getBalanceTableNames() {
        final Map<String, String> balance = new LinkedHashMap<String, String>() {
            {
                put("TotalAssets", "totalAssets");
            }

            {
                put("Total Current Liabilities", "totalCurrentLiabilities");
            }

            {
                put("Total Liabilities", "totalLiab");
            }

            {
                put("netTangibleAssets", "netTangibleAssets");
            }

            {
                put("totalStockholderEquity", "totalStockholderEquity");
            }
        };

        Map<String, Map<String, String>> allTableNames = new LinkedHashMap<String, Map<String, String>>() {
            {
                put("Balance", balance);
            }
        };
        return allTableNames;
    }

    public static Map<String, Map<String, String>> getCashFlowTableNames() {
        final Map<String, String> cashFlow = new LinkedHashMap<String, String>() {
            {
                put("trailingFreeCashFlow", "trailingFreeCashFlow");
            }
        };

        Map<String, Map<String, String>> allTableNames = new LinkedHashMap<String, Map<String, String>>() {
            {
                put("cashFlow", cashFlow);
            }
        };
        return allTableNames;
    }

    public static String getIncomeStmtYear(String requestURL) throws IOException {
        String result = "";
        String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        try {
            Document doc1 = Jsoup.connect(requestURL).userAgent(userAgent1).get();
            Elements scriptTags = doc1.getElementsByTag("script");
            String re = "root\\.App\\.main\\s*\\=\\s*(.*?);\\s*\\}\\(this\\)\\)\\s*;";

            for (Element script : scriptTags) {
                Pattern pattern = Pattern.compile(re, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(script.html());
                //Log.e ("SCRIPT", script.html());

                if (matcher.find()) {
                    String data = matcher.group(1);
                    //Log.e("incomestmtdata", data);

                    JSONObject jo = new JSONObject(data);
                    JSONArray table = getIncomeTable(jo);
                    JSONArray tableQ = getIncomeTableQ(jo);
                    Map<String, Map<String, String>> tableNames = getTableNames();

                    //Map<String, Map<String, String>> tableNames = getTableNames();
                    String[] dates = getDates(table);
                    String[] datesQ = getDates(tableQ);
                    List<String> tableData = new ArrayList<>();

                    for (Map.Entry<String, Map<String, String>> tableEntry : tableNames.entrySet()) {
                        tableData.add(tableEntry.getKey());
                        tableData.addAll(Arrays.asList(dates));

                        for (Map.Entry<String, String> row1 : tableEntry.getValue().entrySet()) {
                            String[] tableRow1 = getRow(table, row1.getValue());
                            tableData.add(row1.getKey());
                            for (String column : tableRow1) {
                                tableData.add(column);
                            }
                        }
                    }
                    incomeData = TextUtils.join(" ", tableData);
                    incomeData = incomeData.replaceAll("[^a-zA-Z0-9 /-]", "");
                    incomeData = incomeData.trim().replaceAll("(?<=[A-Za-z])\\s+(?=[A-Za-z])", "");
                    //Log.e("incomeDATA", incomeData);

                    List<String> tableDataQ = new ArrayList<>();

                    for (Map.Entry<String, Map<String, String>> tableEntry : tableNames.entrySet()) {
                        tableDataQ.add(tableEntry.getKey());
                        tableDataQ.addAll(Arrays.asList(datesQ));

                        for (Map.Entry<String, String> row1 : tableEntry.getValue().entrySet()) {
                            String[] tableRow1 = getRow(tableQ, row1.getValue());
                            tableDataQ.add(row1.getKey());
                            for (String column : tableRow1) {
                                tableDataQ.add(column);
                            }
                        }
                    }
                    incomeDataQ = TextUtils.join(" ", tableDataQ);
                    incomeDataQ = incomeDataQ.replaceAll("[^a-zA-Z0-9 /-]", "");
                    incomeDataQ = incomeDataQ.trim().replaceAll("(?<=[A-Za-z])\\s+(?=[A-Za-z])", "");
                    //Log.e("incomeDATAQ", incomeDataQ);
                }
            }
        } catch (Exception e) {
            //Log.e("err", "err", e);
        }

        return incomeData;
    }

    public static String getBalanceSheetYear(String requestURL) throws IOException {
        String result = "";
        String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        try {
            Document doc1 = Jsoup.connect(requestURL).userAgent(userAgent1).get();
            Elements scriptTags = doc1.getElementsByTag("script");
            String re = "root\\.App\\.main\\s*\\=\\s*(.*?);\\s*\\}\\(this\\)\\)\\s*;";
            Element title = doc1.select("title").first();
            companyName = title.text().replaceAll(".*\\||\\-.*", "");
            companyName = companyName.substring(0, companyName.length() - 15);
            //Log.e("COMPANY NAME", companyName);

            for (Element script : scriptTags) {
                Pattern pattern = Pattern.compile(re, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(script.html());

                if (matcher.find()) {
                    String data = matcher.group(1);
                    //Log.e("balancesheetdata", data);

                    JSONObject jo = new JSONObject(data);
                    JSONArray table = getBalanceTable(jo);
                    JSONArray tableQ = getBalanceTableQ(jo);
                    Map<String, Map<String, String>> tableNames = getBalanceTableNames();

                    /*String totalAssetsKey = tableNames.get("Balance").get("TotalAssets");
                    String[] totalAssetsValues = getRow(table, totalAssetsKey);
                    String value = totalAssetsValues[0];
                    Log.e("totalAssetsValues0", value);

                    String totalAssetsKeyQ = tableNames.get("Balance").get("TotalAssets");
                    String[] totalAssetsValuesQ = getRow(tableQ, totalAssetsKeyQ);
                    String valueQ = totalAssetsValuesQ[0];
                    Log.e("totalAssetsValuesQ0", valueQ);*/

                    //Map<String, Map<String, String>> tableNames = getTableNames();
                    String[] dates = getDates(table);
                    String[] datesQ = getDates(tableQ);
                    List<String> tableData = new ArrayList<>();

                    for (Map.Entry<String, Map<String, String>> tableEntry : tableNames.entrySet()) {
                        tableData.add(tableEntry.getKey());
                        tableData.addAll(Arrays.asList(dates));

                        for (Map.Entry<String, String> row1 : tableEntry.getValue().entrySet()) {
                            String[] tableRow1 = getRow(table, row1.getValue());
                            tableData.add(row1.getKey());
                            for (String column : tableRow1) {
                                tableData.add(column);
                            }
                        }
                    }
                    balanceData = TextUtils.join(" ", tableData);
                    balanceData = balanceData.replaceAll("[^a-zA-Z0-9 /-]", "");
                    balanceData = balanceData.trim().replaceAll("(?<=[A-Za-z])\\s+(?=[A-Za-z])", "");
                    //Log.e("balanceDATA", balanceData);

                    List<String> tableDataQ = new ArrayList<>();

                    for (Map.Entry<String, Map<String, String>> tableEntry : tableNames.entrySet()) {
                        tableDataQ.add(tableEntry.getKey());
                        tableDataQ.addAll(Arrays.asList(datesQ));

                        for (Map.Entry<String, String> row1 : tableEntry.getValue().entrySet()) {
                            String[] tableRow1 = getRow(tableQ, row1.getValue());
                            tableDataQ.add(row1.getKey());
                            for (String column : tableRow1) {
                                tableDataQ.add(column);
                            }
                        }
                    }
                    balanceDataQ = TextUtils.join(" ", tableDataQ);
                    balanceDataQ = balanceDataQ.replaceAll("[^a-zA-Z0-9 /-]", "");
                    balanceDataQ = balanceDataQ.trim().replaceAll("(?<=[A-Za-z])\\s+(?=[A-Za-z])", "");
                    //Log.e("balanceDATAQ", balanceDataQ);
                }
            }
        } catch (Exception e) {
            //Log.e("err", "err", e);
        }
        return balanceData;
    }

    public static String getCashFlowYear(String requestURL) throws IOException {
        String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        try {
            Document doc1 = Jsoup.connect(requestURL).userAgent(userAgent1).get();
            Elements scriptTags = doc1.getElementsByTag("script");
            String re = "root\\.App\\.main\\s*\\=\\s*(.*?);\\s*\\}\\(this\\)\\)\\s*;";

            for (Element script : scriptTags) {
                Pattern pattern = Pattern.compile(re, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(script.html());

                if (matcher.find()) {
                    String data = matcher.group(1);
                    //Log.e("CashFlowData", data);

                    JSONObject jo = new JSONObject(data);
                    JSONArray table = getCashFlowTable(jo);
                    JSONArray tableQ = getCashFlowTableQ(jo);
                    Map<String, Map<String, String>> tableNames = getCashFlowTableNames();

                    String[] dates = getDates(table);
                    String[] datesQ = getDates(tableQ); //works
                    List<String> tableData = new ArrayList<>();

                    for (Map.Entry<String, Map<String, String>> tableEntry : tableNames.entrySet()) {
                        tableData.add(tableEntry.getKey());
                        tableData.addAll(Arrays.asList(dates));

                        for (Map.Entry<String, String> row1 : tableEntry.getValue().entrySet()) {
                            String[] tableRow1 = getRow(table, row1.getValue());
                            tableData.add(row1.getKey());
                            for (String column : tableRow1) {
                                tableData.add(column);
                            }
                        }
                    }
                    cashFlowData = TextUtils.join(" ", tableData);
                    cashFlowData = cashFlowData.replaceAll("[^a-zA-Z0-9 /-]", "");
                    cashFlowData = cashFlowData.trim().replaceAll("(?<=[A-Za-z])\\s+(?=[A-Za-z])", "");
                    //Log.e("cashFlowData", cashFlowData);

                    List<String> tableDataQ = new ArrayList<>();

                    for (Map.Entry<String, Map<String, String>> tableEntry : tableNames.entrySet()) {
                        tableDataQ.add(tableEntry.getKey());
                        tableDataQ.addAll(Arrays.asList(datesQ));

                        for (Map.Entry<String, String> row1 : tableEntry.getValue().entrySet()) {
                            String[] tableRow1 = getRow(tableQ, row1.getValue());
                            tableDataQ.add(row1.getKey());
                            for (String column : tableRow1) {
                                tableDataQ.add(column);
                            }
                        }
                    }
                    cashFlowDataQ = TextUtils.join(" ", tableDataQ);
                    cashFlowDataQ = cashFlowDataQ.replaceAll("[^a-zA-Z0-9 /-]", "");
                    cashFlowDataQ = cashFlowDataQ.trim().replaceAll("(?<=[A-Za-z])\\s+(?=[A-Za-z])", "");
                    //Log.e("balanceDATAQ", balanceDataQ);
                }
            }
        } catch (Exception e) {
            Log.e("err-cashflowstmt", "err", e);
        }
        return cashFlowData;
    }

    private static Map<String, List<String>> fetch(String url) throws IOException {
        String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        Map<String, List<String>> values = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<String> EBIT = new ArrayList<>();
        Document doc = Jsoup.connect(url).userAgent(userAgent1).get();
        Elements rows = doc.select("tr[data-test=fin-row]");
        for (Element row : rows) {
            String label = row.select("td[class=C($primaryColor) Fz(s) Fw(500) Ta(start) Pend(10px)]").text();
            if (label.equals("EBIT")) {
                Elements cols = row.select("td[data-test=fin-col]");
                for (Element col : cols) {
                    String value = col.select("span").text();
                    EBIT.add(value);
                }
            }
        }
        Elements dateCols = doc.select("div[class=Ta(c) Py(6px) Bxz(bb) BdB Bdc($seperatorColor) Miw(120px) Miw(100px)--pnclg D(ib) Fw(b)]");
        for (Element dateCol : dateCols) {
            String date = dateCol.select("span").text();
            dates.add(date);
        }
        values.put("EBIT", EBIT);
        values.put("dates", dates);
        return values;
    }
/*    private static List<String> fetch(String url) throws IOException {
        String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        List<String> values = new ArrayList<>();
        Document doc = Jsoup.connect(url).userAgent(userAgent1).get();
        Elements divTags = doc.select("div[data-test=fin-col]");
        for (Element divTag : divTags) {
            String value = divTag.select("span").text();
            values.add(value);
        }
        return values;
    }*/
/*    private static List<String> fetch(String url) throws MalformedURLException, IOException, UnsupportedEncodingException {
        String userAgent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.43";
        List<String> values = new ArrayList<>();
        try {
            Document doc1 = Jsoup.connect(url).userAgent(userAgent1).get();
            Elements divTags = doc1.getElementsByTag("div");
            String re = "^<div class=\\\"Ta\\(c\\) Py\\(6px\\) Bxz\\(bb\\) BdB Bdc\\(\\$seperatorColor\\) Miw\\(120px\\) Miw\\(100px\\)\\-\\-pnclg D\\(tbc\\)\\\" data-test=\\\"fin-col\\\">.*</div>$";

            for (Element div : divTags) {
                Log.d("Div data: ", div.html());
                Pattern pattern = Pattern.compile(re, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(div.html());

                if (matcher.find()) {
                    String data = div.select("span").text();
                    values.add(data);
                }
            }
        } catch (Exception e) {
            Log.e("err-new", "err", e);
        }
        return values;
    }*/

}