/*
Copyright (c) 2024, Upendra Rajan
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree.
 */
package com.upen.rocecalculator;

import static com.upen.rocecalculator.MainActivity.balanceSheetQuarterlyArray;
import static com.upen.rocecalculator.MainActivity.balanceSheetYearlyArray;
import static com.upen.rocecalculator.MainActivity.cashFlowStmtQuarterlyArray;
import static com.upen.rocecalculator.MainActivity.cashFlowStmtYearlyArray;
import static com.upen.rocecalculator.MainActivity.companyName;
import static com.upen.rocecalculator.MainActivity.dates;
import static com.upen.rocecalculator.MainActivity.datesQ;
import static com.upen.rocecalculator.MainActivity.incomeStmtQuarterlyArray;
import static com.upen.rocecalculator.MainActivity.incomeStmtYearlyArray;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
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
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class FinancialStatementsActivity extends AppCompatActivity {
    public static ClipData myClip;
    private static final DecimalFormat formatter = new DecimalFormat("#,###,###");
    public static ArrayList<HashMap<String, String>> RoceInfoList;

    private static JSONObject json;
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
            Intent intent = getIntent();
            String message = intent.getStringExtra("companyName");
            position = intent.getIntExtra("position", 0);
            getSupportActionBar().setTitle(message);

            SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            ViewPager mViewPager = findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = findViewById(R.id.tabs);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest request = new AdRequest.Builder().build();
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

            View rootView = inflater.inflate(R.layout.fragment_financial_statements, container, false);

            ListAdapter RoceInfoAdapter;
            final ListView listView;


            TextView textView = rootView.findViewById(R.id.section_label);
            textView.setMovementMethod(new ScrollingMovementMethod());

            listView = rootView.findViewById(R.id.finStmtListView);
            //myWebView = (WebViewFin) rootView.findViewById(R.id.webView1);

            if (requireArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                try {
                    RoceInfoList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject();
                    for (int i = 0; i < dates.size(); i++) {
                        HashMap<String, String> RoceInfoHashMap = new HashMap<>();
                        RoceInfoHashMap.put("YA", "Statement date: " + dates.get(i));
                        RoceInfoHashMap.put("EA", createkeyValuePairStringFromJSON(incomeStmtYearlyArray.getJSONObject(i)));
                        RoceInfoHashMap.put("TA", createkeyValuePairStringFromJSON(balanceSheetYearlyArray.getJSONObject(i)));
                        RoceInfoHashMap.put("DA", createkeyValuePairStringFromJSON(cashFlowStmtYearlyArray.getJSONObject(i)));
                        RoceInfoList.add(RoceInfoHashMap);
                    }
                    RoceInfoAdapter = new SimpleAdapter(getContext(), RoceInfoList,
                            R.layout.content_financial_statements, new String[]{"YA", "EA", "TA", "DA"},
                            new int[]{R.id.incDate, R.id.incData, R.id.baldata, R.id.cfData});
                    listView.setVerticalScrollBarEnabled(true);
                    listView.setAdapter(RoceInfoAdapter);
                    Log.d("Position Clicked: ", String.valueOf(position));
                    listView.setSelection(position);
                } catch (final Exception e) {
                    Log.e("Exception in ROCE Calc: ", String.valueOf(e));
                    Toast.makeText(getContext(), "There seems to be an issue in ROCE data. Please reload the app.", Toast.LENGTH_LONG).show();
                }
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                try {
                    RoceInfoList = new ArrayList<>();
                    if (datesQ.size() > 0) {
                        for (int i = 0; i < datesQ.size(); i++) {
                            HashMap<String, String> RoceInfoHashMap = new HashMap<>();
                            RoceInfoHashMap.put("YA", "Statement date: " + datesQ.get(i));
                            RoceInfoHashMap.put("EA", createkeyValuePairStringFromJSON(incomeStmtQuarterlyArray.getJSONObject(i)));
                            RoceInfoHashMap.put("TA", createkeyValuePairStringFromJSON(balanceSheetQuarterlyArray.getJSONObject(i)));
                            RoceInfoHashMap.put("DA", createkeyValuePairStringFromJSON(cashFlowStmtQuarterlyArray.getJSONObject(i)));
                            RoceInfoList.add(RoceInfoHashMap);
                        }

                    } else {
                        HashMap<String, String> RoceInfoHashMap = new HashMap<>();
                        RoceInfoHashMap.put("ZA", "Data not available. Try the manual calculator.");
                        RoceInfoHashMap.put("AA", "Data not available.");
                        RoceInfoHashMap.put("CA", "Data not available.");
                        RoceInfoHashMap.put("FA", "Data not available.");
                        RoceInfoList.add(RoceInfoHashMap);
                        Toast.makeText(getContext(), "No Quarterly data available.", Toast.LENGTH_LONG).show();
                    }

                    RoceInfoAdapter = new SimpleAdapter(getContext(), RoceInfoList,
                            R.layout.content_financial_statements, new String[]{"YA", "EA", "TA", "DA"},
                            new int[]{R.id.incDate, R.id.incData, R.id.baldata, R.id.cfData});
                    listView.setVerticalScrollBarEnabled(true);
                    listView.setAdapter(RoceInfoAdapter);
                    listView.setSelection(position);

                } catch (final Exception e) {
                    Log.e("Exception: ", String.valueOf(e));
                    Toast.makeText(getContext(), "There seems to be an issue in ROCE data. Please reload the app.", Toast.LENGTH_LONG).show();
                }
            } else {

                try {

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

                    String text = companyName + "\n" + ((TextView) v.findViewById(R.id.incDate)).getText().toString() + "\n" + ((TextView) v.findViewById(R.id.incStmtTitle)).getText().toString() + "\n" + ((TextView) v.findViewById(R.id.incData)).getText().toString() + "\n" + ((TextView) v.findViewById(R.id.balTitle)).getText().toString() + "\n" + ((TextView) v.findViewById(R.id.baldata)).getText().toString() + "\n" + ((TextView) v.findViewById(R.id.cfTitle)).getText().toString() + "\n" + ((TextView) v.findViewById(R.id.cfData)).getText().toString();
                    myClip = ClipData.newPlainText("Copied Text", text);
                    clipboard.setPrimaryClip(myClip);
                    Toast.makeText(getContext(), "Copied.", Toast.LENGTH_LONG).show();
                    return true;
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(), ResultsActivity.class);
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

    private static String createkeyValuePairStringFromJSON(JSONObject json) {
        String resultValue = "";
        try {
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                //if json.get(key) is not null then get the value corresponding to the longfmt key and add it to the resultValue
                if (json.get(key) != null && !key.equals("maxAge") && !key.equals("endDate") && json.getJSONObject(key).optString("fmt") != null && json.getJSONObject(key).optString("fmt").length() != 0) {
                    resultValue += convertCamelCaseToHuman(key) + ": " + changeStringColorToBlue(json.getJSONObject(key).optString("fmt")) + "\n\n";
                    //Log.d(key + ": ", String.valueOf(json.getJSONObject(key).optString("fmt")));
                } else if (!key.equals("maxAge") && !key.equals("endDate")) {
                    resultValue += convertCamelCaseToHuman(key) + ": " + "N/A" + "\n\n";
                    //Log.d(key + ": ", "N/A");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultValue;
    }

    private static String convertCamelCaseToHuman(String camelCaseString) {
        String result = "";
        for (int i = 0; i < camelCaseString.length(); i++) {
            char c = camelCaseString.charAt(i);
            if (Character.isUpperCase(c)) {
                result += " " + c;
            } else if (i == 0 && Character.isLowerCase(c)) {
                result += Character.toUpperCase(c);
            } else {
                result += c;
            }
        }
        return result;
    }

    private static String changeStringColorToBlue(String string) {
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, string.length(), 0);
        return spannableString.toString();
    }
}