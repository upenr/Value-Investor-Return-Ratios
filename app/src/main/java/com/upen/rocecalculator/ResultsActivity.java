/*
Copyright (c) 2024, Upendra Rajan
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree.
 */
package com.upen.rocecalculator;

import static com.upen.rocecalculator.MainActivity.companyName;
import static com.upen.rocecalculator.MainActivity.dates;
import static com.upen.rocecalculator.MainActivity.datesQ;
import static com.upen.rocecalculator.MainActivity.ebit;
import static com.upen.rocecalculator.MainActivity.ebitQ;
import static com.upen.rocecalculator.MainActivity.fcf;
import static com.upen.rocecalculator.MainActivity.fcfQ;
import static com.upen.rocecalculator.MainActivity.ibt;
import static com.upen.rocecalculator.MainActivity.ibtQ;
import static com.upen.rocecalculator.MainActivity.ni;
import static com.upen.rocecalculator.MainActivity.niQ;
import static com.upen.rocecalculator.MainActivity.ticker;
import static com.upen.rocecalculator.MainActivity.tocl;
import static com.upen.rocecalculator.MainActivity.toclQ;
import static com.upen.rocecalculator.MainActivity.tota;
import static com.upen.rocecalculator.MainActivity.totaQ;
import static com.upen.rocecalculator.MainActivity.totlb;
import static com.upen.rocecalculator.MainActivity.totlbQ;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResultsActivity extends AppCompatActivity {
    public static ClipData myClip;
    private static final DecimalFormat formatter = new DecimalFormat("#,###,###");
    public static ArrayList<HashMap<String, String>> RoceInfoList;
    private static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(MainActivity.companyName);
            DynamicColors.applyToActivitiesIfAvailable(this.getApplication());

            Intent intent = getIntent();
            position = intent.getIntExtra("position", 0);

            SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            ViewPager mViewPager = findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = findViewById(R.id.tabs);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest request = new AdRequest.Builder().build();
            new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("37D5E34239AD8A6CB84BE19E6F9DB79A"));
            mAdView.loadAd(request);

        } catch (final Exception e) {
            Log.e("App Load Exception: ", String.valueOf(e));
            Toast.makeText(getApplicationContext(), "There may be an issue. Please reload the app or contact the developer from Play Store.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_results, container, false);

            ListAdapter RoceInfoAdapter;
            final ListView listView;


            TextView textView = rootView.findViewById(R.id.section_label);
            // TextView percentileTextView = (TextView) rootView.findViewById(R.id.percentile1);
            textView.setMovementMethod(new ScrollingMovementMethod());

            listView = rootView.findViewById(R.id.listview);
            //myWebView = (WebViewFin) rootView.findViewById(R.id.webView1);

            if (requireArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                try {
                    RoceInfoList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject();
                    for (int i = 0; i < dates.size(); i++) {
                        HashMap<String, String> RoceInfoHashMap = new HashMap<>();
                        RoceInfoHashMap.put("YA", "Statement date: " + dates.get(i));
                        jsonObject.put("CompanyName", MainActivity.companyName);
                        jsonObject.put("Date" + String.valueOf(i), dates.get(i));
                        RoceInfoHashMap.put("EA", "EBIT: " + formatter.format(Double.parseDouble(ebit.get(i))));
                        RoceInfoHashMap.put("TA", "Total Assets: " + formatter.format(Double.parseDouble(tota.get(i))));
                        RoceInfoHashMap.put("DA", "Total Current Liabilities: " + formatter.format(Double.parseDouble(tocl.get(i))));
                        RoceInfoHashMap.put("NA", "Net Income: " + formatter.format(Double.parseDouble(ni.get(i))));
                        RoceInfoHashMap.put("GA", "Free Cash Flow: " + formatter.format(Double.parseDouble(fcf.get(i))));
                        //Log.d("RoceInfoHashMap", RoceInfoHashMap.toString());
                        //checking if it's a number
                        if (ebit.get(i).matches("-?\\d+(\\.\\d+)?") && tota.get(i).matches("-?\\d+(\\.\\d+)?") && tocl.get(i).matches("-?\\d+(\\.\\d+)?")) {
                            double ebit1 = Double.parseDouble(ebit.get(i));
                            //Log.d("Ebit1", String.valueOf(ebit1));
                            if (ebit1 == 0) {
                                ebit1 = Double.parseDouble(ibt.get(1));
                                //Log.d("Ebit was 0", String.valueOf(ebit1));
                                Toast.makeText(getContext(), "EBIT unavailable (maybe the company is a bank?). Using Income Before Taxes instead. Data is approximate.", Toast.LENGTH_SHORT).show();
                            }
                            double tota1 = Double.parseDouble(tota.get(i));
                            //Log.d("Tota1", String.valueOf(tota1));
                            double tocl1 = Double.parseDouble(tocl.get(i));
                            //Log.d("Tocl", String.valueOf(tocl1));
                            double roce = 100 * (ebit1 / (tota1 - tocl1));
                            double fcfroce = 100 * (Double.parseDouble(fcf.get(i)) / (tota1 - tocl1));
                            //Log.d("ROCE", String.valueOf(roce));
                            double ni1 = Double.parseDouble(ni.get(i));
                            //Log.d("NI1", String.valueOf(ni1));
                            double totlb1 = Double.parseDouble(totlb.get(i));
                            //Log.d("TotalB1", String.valueOf(totlb1));
                            double roe = 100 * (ni1 / (tota1 - totlb1));
                            double roa = 100 * (ni1 / (tota1));
                            DecimalFormat f = new DecimalFormat("##.00");
                            RoceInfoHashMap.put("ZA", "ROCE: " + f.format(roce) + "%");
                            jsonObject.put("ROCE" + String.valueOf(i), f.format(roce));
                            RoceInfoHashMap.put("AA", "ROE: " + f.format(roe) + "%");
                            RoceInfoHashMap.put("CA", "ROA: " + f.format(roa) + "%");
                            RoceInfoHashMap.put("FA", "FCFROCE: " + f.format(fcfroce) + "%");
                            RoceInfoHashMap.put("GA", "Free Cash Flow: " + formatter.format(Double.parseDouble(fcf.get(i))));
                            //Log.d("RoceInfoHashMap", RoceInfoHashMap.toString());
                            RoceInfoList.add(RoceInfoHashMap);
                        } else {
                            RoceInfoHashMap.put("ZA", "ROCE: N/A. Try the manual calculator.");
                            jsonObject.put("ROCE" + String.valueOf(i), "N/A");
                            RoceInfoHashMap.put("AA", "ROE: Data not available.");
                            RoceInfoHashMap.put("CA", "ROA: Data not available.");
                            RoceInfoHashMap.put("FA", "FCFROCE: Data not available.");
                            RoceInfoList.add(RoceInfoHashMap);
                        }
                    }
                    //Log.d("JSON", jsonObject.toString()); //push this to Firebase
                    //eDatabase(jsonObject.toString());
                    RoceInfoAdapter = new SimpleAdapter(getContext(), RoceInfoList,
                            R.layout.content_results, new String[]{"YA", "EA", "TA", "DA", "ZA", "AA", "NA", "CA", "FA", "GA"},
                            new int[]{R.id.ta, R.id.ia, R.id.tota, R.id.tocl, R.id.roce, R.id.roe, R.id.ni1, R.id.roa, R.id.fcfroce, R.id.fcf});
                    listView.setVerticalScrollBarEnabled(true);
                    listView.setAdapter(RoceInfoAdapter);
                    listView.setSelection(position);
                    if (RoceInfoList.size() > 3) {
                        //Log.d("ValuetoDatabase:", RoceInfoList.get(0).get("YA") + ", " + RoceInfoList.get(0).get("ZA") + ", " + RoceInfoList.get(1).get("YA") + ", " + RoceInfoList.get(1).get("ZA") + ", " + RoceInfoList.get(2).get("YA") + ", " + RoceInfoList.get(2).get("ZA") + ", " + RoceInfoList.get(3).get("YA") + ", " + RoceInfoList.get(3).get("ZA"));
                        updateDatabase(companyName + ", " + RoceInfoList.get(0).get("YA") + ", " + RoceInfoList.get(0).get("ZA") + ", " + RoceInfoList.get(1).get("YA") + ", " + RoceInfoList.get(1).get("ZA") + ", " + RoceInfoList.get(2).get("YA") + ", " + RoceInfoList.get(2).get("ZA") + ", " + RoceInfoList.get(3).get("YA") + ", " + RoceInfoList.get(3).get("ZA"));
                    } else if (RoceInfoList.size() > 2) {
                        //Log.d("ValuetoDatabase:", RoceInfoList.get(0).get("YA") + ", " + RoceInfoList.get(0).get("ZA") + ", " + RoceInfoList.get(1).get("YA") + ", " + RoceInfoList.get(1).get("ZA") + ", " + RoceInfoList.get(2).get("YA") + ", " + RoceInfoList.get(2).get("ZA"));
                        updateDatabase(companyName + ", " + RoceInfoList.get(0).get("YA") + ", " + RoceInfoList.get(0).get("ZA") + ", " + RoceInfoList.get(1).get("YA") + ", " + RoceInfoList.get(1).get("ZA") + ", " + RoceInfoList.get(2).get("YA") + ", " + RoceInfoList.get(2).get("ZA"));
                    } else if (RoceInfoList.size() > 1) {
                        //Log.d("ValuetoDatabase:", RoceInfoList.get(0).get("YA") + ", " + RoceInfoList.get(0).get("ZA") + ", " + RoceInfoList.get(1).get("YA") + ", " + RoceInfoList.get(1).get("ZA"));
                        updateDatabase(companyName + ", " + RoceInfoList.get(0).get("YA") + ", " + RoceInfoList.get(0).get("ZA") + ", " + RoceInfoList.get(1).get("YA") + ", " + RoceInfoList.get(1).get("ZA"));
                    } else if (RoceInfoList.size() > 0) {
                        //Log.d("ValuetoDatabase:", RoceInfoList.get(0).get("YA") + ", " + RoceInfoList.get(0).get("ZA"));
                        updateDatabase(companyName + ", " + RoceInfoList.get(0).get("YA") + ", " + RoceInfoList.get(0).get("ZA"));
                    } else {
                        updateDatabase(companyName + ", " + "N/A");
                    }

                } catch (final Exception e) {
                    Log.e("Exception in ROCE Calc: ", String.valueOf(e));
                    Toast.makeText(getContext(), "There seems to be an issue in ROCE data. Please reload the app.", Toast.LENGTH_LONG).show();
                }
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                try {
                    RoceInfoList = new ArrayList<>();

                    /*Map<String, String[]> incomeMap = new HashMap<>();
                    Map<String, String[]> balanceMap = new HashMap<>();
                    if (balanceDataQ.equalsIgnoreCase("")) {
                        //doNothing
                        Toast.makeText(getContext(), "Does not exist.", Toast.LENGTH_LONG).show();
                        //resultsView.append("Does not exist.");
                    } else {
                        String[] exps = incomeDataQ.split("\\s+(?=[A-Za-z]+)");
                        for (String exp : exps) {
                            String[] parts = exp.split("\\s+");
                            incomeMap.put(parts[0], Arrays.copyOfRange(parts, 1, parts.length));
                        }
                        *//*Iterator it = incomeMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            Log.e("Key-Values", pair.getKey() + " = " + pair.getValue());
                    }*//*

                        String[] exps1 = balanceDataQ.split("\\s+(?=[A-Za-z]+)");
                        for (String exp1 : exps1) {
                            String[] parts = exp1.split("\\s+");
                            balanceMap.put(parts[0], Arrays.copyOfRange(parts, 1, parts.length));
                            //Log.e("BalanceParts[0]",parts[0]);
                        }

                        String[] revenueDates = incomeMap.get("Revenue");
                        String[] ebit = incomeMap.get("EarningsBeforeInterestandTaxes");
                        String[] ibt = incomeMap.get("incomeBeforeTax");
                        String[] ni = incomeMap.get("netIncome");
                        String[] tota = balanceMap.get("TotalAssets");
                        String[] tocl = balanceMap.get("TotalCurrentLiabilities");
                        String[] totlb = balanceMap.get("TotalLiabilities");*/

                    for (int i = 0; i < datesQ.size(); i++) {
                        HashMap<String, String> RoceInfoHashMap = new HashMap<>();
                        RoceInfoHashMap.put("QA", "Statement date: " + datesQ.get(i));
                        RoceInfoHashMap.put("EA", "EBIT: " + formatter.format(Double.parseDouble(ebitQ.get(i))));
                        RoceInfoHashMap.put("TA", "Total Assets: " + formatter.format(Double.parseDouble(totaQ.get(i))));
                        RoceInfoHashMap.put("DA", "Total Current Liabilities: " + formatter.format(Double.parseDouble(toclQ.get(i))));
                        RoceInfoHashMap.put("NA", "Net Income: " + formatter.format(Double.parseDouble(niQ.get(i))));
                        RoceInfoHashMap.put("GA", "Free Cash Flow: " + formatter.format(Double.parseDouble(fcfQ.get(i))));
                        //checking if it's a number
                        if (ebitQ.get(i).matches("-?\\d+(\\.\\d+)?") && totaQ.get(i).matches("-?\\d+(\\.\\d+)?") && toclQ.get(i).matches("-?\\d+(\\.\\d+)?")) {
                            double ebit1 = Double.parseDouble(ebitQ.get(i));
                            if (ebit1 == 0) {
                                ebit1 = Double.parseDouble(ibtQ.get(1));
                                Toast.makeText(getContext(), "EBIT unavailable (maybe the company is a bank?). Using Income Before Taxes instead. Data is approximate.", Toast.LENGTH_LONG).show();
                            }
                            double tota1 = Double.parseDouble(totaQ.get(i));
                            double tocl1 = Double.parseDouble(toclQ.get(i));
                            double roce = 100 * (ebit1 / (tota1 - tocl1));
                            double fcfrocetemp = 100 * (Double.parseDouble(fcfQ.get(i)) / (tota1 - tocl1));
                            double ni1 = Double.parseDouble(niQ.get(i));
                            double totlb1 = Double.parseDouble(totlbQ.get(i));
                            double roe = 100 * (ni1 / (tota1 - totlb1));
                            double roa = 100 * (ni1 / (tota1));
                            DecimalFormat f = new DecimalFormat("##.00");
                            RoceInfoHashMap.put("ZA", "ROCE: " + f.format(roce) + "%");
                            RoceInfoHashMap.put("AA", "ROE: " + f.format(roe) + "%");
                            RoceInfoHashMap.put("CA", "ROA: " + f.format(roa) + "%");
                            RoceInfoHashMap.put("FA", "FCFROCE: " + f.format(fcfrocetemp) + "%");
                            RoceInfoHashMap.put("GA", "Free Cash Flow: " + formatter.format(Double.parseDouble(fcfQ.get(i))));
                            RoceInfoList.add(RoceInfoHashMap);
                        } else {
                            RoceInfoHashMap.put("ZA", "ROCE: N/A. Try the manual calculator.");
                            RoceInfoHashMap.put("AA", "ROE: Data not available.");
                            RoceInfoHashMap.put("CA", "ROA: Data not available.");
                            RoceInfoHashMap.put("FA", "FCFROCE: Data not available.");
                            RoceInfoList.add(RoceInfoHashMap);
                        }
                        //Log.d("Quarterly RoceInfoHashMap", RoceInfoHashMap.toString());

                    }

                    //listItem = getResources().getStringArray(R.array.array_technology);
                    //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getData());
                    RoceInfoAdapter = new SimpleAdapter(getContext(), RoceInfoList,
                            R.layout.content_results, new String[]{"QA", "EA", "TA", "DA", "ZA", "AA", "NA", "CA", "FA", "GA"},
                            new int[]{R.id.ta, R.id.ia, R.id.tota, R.id.tocl, R.id.roce, R.id.roe, R.id.ni1, R.id.roa, R.id.fcfroce, R.id.fcf});
                    listView.setVerticalScrollBarEnabled(true);
                    listView.setAdapter(RoceInfoAdapter);
                    listView.setSelection(position);

                } catch (final Exception e) {
                    Log.e("Exception: ", String.valueOf(e));
                    Toast.makeText(getContext(), "There seems to be an issue in ROCE data. Please reload the app.", Toast.LENGTH_LONG).show();
                }
            } else {

                try {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("We have no totalCashFromOperatingActivities data on this. Try the manual calculator or contact the app developer to understand the issue.");
                } catch (final Exception e) {
                    Log.e("Exception: ", String.valueOf(e));
                    Toast.makeText(getContext(), "There seems to be an issue in ROCE data. Try the manual calculator or please reload the app.", Toast.LENGTH_LONG).show();
                }
            }
            ClipboardManager clipboard = (ClipboardManager)
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View v, int pos, long id) {

                    String text = companyName + "\n" + ((TextView) v.findViewById(R.id.ta)).getText().toString() + "\n" + ((TextView) v.findViewById(R.id.roce)).getText().toString() + "\n" + ((TextView) v.findViewById(R.id.roe)).getText().toString() + "\n" + ((TextView) v.findViewById(R.id.roa)).getText().toString();
                    myClip = ClipData.newPlainText("Copied Text", text);
                    clipboard.setPrimaryClip(myClip);
                    Toast.makeText(getContext(), "Copied.", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(), FinancialStatementsActivity.class);
                    intent.putExtra("companyName", companyName);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private static void updateDatabase(String companySearches) {
        try {
            Map<String, Object> updateMap = new HashMap();
            Date currentTime = Calendar.getInstance().getTime();
            String dateString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            updateMap.put(dateString + ticker.replaceAll("\\.", "Dot"), companySearches);
            FirebaseFirestore.getInstance().collection("rocecalc")
                    .document("companySearches").update(updateMap);
            //Log.d("Success", "Data added to database.");
        } catch (Exception e) {
            Log.e("Error", "Error adding data to database.");
        }
    }
}