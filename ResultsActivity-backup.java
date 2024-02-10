package com.upen.rocecalculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.upen.rocecalculator.MainActivity.balanceDataQ;
import static com.upen.rocecalculator.MainActivity.balanceSheet;
import static com.upen.rocecalculator.MainActivity.companyName;
import static com.upen.rocecalculator.MainActivity.incomeDataQ;
import static com.upen.rocecalculator.MainActivity.incomeStmt;

public class ResultsActivity extends AppCompatActivity {

    TextView resultsView;
    ListView listView;
    public static ClipData myClip;
    public static TextView roceTextView, roeTextView, taTextView, roaTextView;
    String[] listItem;
    ListAdapter RoceInfoAdapter;
    int positionClicked;
    String title;
    private static DecimalFormat formatter = new DecimalFormat("#,###,###");
    public static ArrayList<HashMap<String, String>> RoceInfoList, incmStmtList, balSheetList, cashFlowList;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static ArrayList<HashMap<String, Object>> personalityList, personality1List, consprefList, conspref1List;
    public static int consPrefSubLength[] = new int[8];
    public static int personality1SubLength[] = new int[5];
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(MainActivity.companyName);

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        } catch (NullPointerException e) {
            Log.e("Exception: ", e.getMessage());
            Toast.makeText(getApplicationContext(), "There may be an issue. Please reload the app or contact the developer from Play Store.", Toast.LENGTH_LONG).show();
        } catch (NumberFormatException e) {
            Log.e("Exception: ", e.getMessage());
            Toast.makeText(getApplicationContext(), "There may be an issue. Please reload the app or contact the developer from Play Store.", Toast.LENGTH_LONG).show();
        } catch (final Exception e) {
            Log.e("Exception: ", e.getMessage());
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
            ListAdapter needsAdapter;

            ArrayList<HashMap<String, Object>> needsList;
            ListAdapter RoceInfoAdapter, incmAdapter, balanceAdapter, cashFlowAdapter;
            final ListView listView, listView2;


            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            // TextView percentileTextView = (TextView) rootView.findViewById(R.id.percentile1);
            textView.setMovementMethod(new ScrollingMovementMethod());

            listView = (ListView) rootView.findViewById(R.id.listview);
            //myWebView = (WebViewFin) rootView.findViewById(R.id.webView1);

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                try {
                    RoceInfoList = new ArrayList<>();

                    Map<String, String[]> incomeMap = new HashMap<>();
                    Map<String, String[]> balanceMap = new HashMap<>();
                    if (incomeStmt.equalsIgnoreCase("")) {
                        //doNothing
                        Toast.makeText(getContext(), "Does not exist.", Toast.LENGTH_LONG).show();
                        //resultsView.append("Does not exist.");
                    } else {
                        String[] exps = incomeStmt.split("\\s+(?=[A-Za-z]+)");
                        for (String exp : exps) {
                            String[] parts = exp.split("\\s+");
                            incomeMap.put(parts[0], Arrays.copyOfRange(parts, 1, parts.length));
                        }
                        /*Iterator it = incomeMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            Log.e("Key-Values", pair.getKey() + " = " + pair.getValue());
                    }*/

                        String[] exps1 = balanceSheet.split("\\s+(?=[A-Za-z]+)");
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
                        String[] totlb = balanceMap.get("TotalLiabilities");

                        for (int i = 0; i < revenueDates.length; i++) {
                            HashMap<String, String> RoceInfoHashMap = new HashMap<>();
                            RoceInfoHashMap.put("TA", "Yearly ending " + revenueDates[i]);
                            RoceInfoHashMap.put("IA", "EBIT: " + formatter.format(Double.parseDouble(ebit[i])));
                            RoceInfoHashMap.put("XA", "Total Assets: " + formatter.format(Double.parseDouble(tota[i])));
                            RoceInfoHashMap.put("YA", "Total Current Liabilities: " + formatter.format(Double.parseDouble(tocl[i])));
                            RoceInfoHashMap.put("BA", "Net Income: " + formatter.format(Double.parseDouble(ni[i])));
                            //checking if it's a number
                            if (ebit[i].matches("-?\\d+(\\.\\d+)?") && tota[i].matches("-?\\d+(\\.\\d+)?") && tocl[i].matches("-?\\d+(\\.\\d+)?")) {
                                double ebit1 = Double.parseDouble(ebit[i]);
                                if (ebit1 == 0) {
                                    ebit1 = Double.parseDouble(ibt[1]);
                                    Toast.makeText(getContext(), "EBIT unavailable (maybe the company is a bank?). Using Income Before Taxes instead. Data is approximate.", Toast.LENGTH_SHORT).show();
                                }
                                double tota1 = Double.parseDouble(tota[i]);
                                double tocl1 = Double.parseDouble(tocl[i]);
                                double roce = 100 * (ebit1 / (tota1 - tocl1));
                                double ni1 = Double.parseDouble(ni[i]);
                                double totlb1 = Double.parseDouble(totlb[i]);
                                double roe = 100 * (ni1 / (tota1 - totlb1));
                                double roa = 100 * (ni1 / (tota1));
                                DecimalFormat f = new DecimalFormat("##.000");
                                RoceInfoHashMap.put("ZA", "ROCE: " + f.format(roce) + "%");
                                RoceInfoHashMap.put("AA", "ROE: " + f.format(roe) + "%");
                                RoceInfoHashMap.put("CA", "ROA: " + f.format(roa) + "%");
                                RoceInfoList.add(RoceInfoHashMap);
                            } else {
                                RoceInfoHashMap.put("ZA", "ROCE: N/A. Try the manual calculator.");
                                RoceInfoHashMap.put("AA", "ROE: Data not available.");
                                RoceInfoHashMap.put("CA", "ROA: Data not available.");
                                RoceInfoList.add(RoceInfoHashMap);
                            }

                        }
                        // iterate each name, and then print out each string in a given array
            /*for (Map.Entry<String, String[]> entry : map.entrySet()) {
               for (String val : entry.getValue()) {
                    // System.out.println(entry.getKey() + ": " + val);
                    Log.e("THREE", entry.getKey() + ": " + val);
                    //resultsView.append(entry.getKey() + ": " + val + "\n");
                    if (entry.getKey().equalsIgnoreCase("Revenue")){
                        HashMap<String, String> RoceInfoHashMap = new HashMap<>();
                        RoceInfoHashMap.put("TA", "Yearly ending "+val);
                        RoceInfoHashMap.put("IA", "Test");
                        RoceInfoList.add(RoceInfoHashMap);
                    }
                }
            }*/
                    }

                    //listItem = getResources().getStringArray(R.array.array_technology);
                    //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getData());
                    RoceInfoAdapter = new SimpleAdapter(getContext(), RoceInfoList,
                            R.layout.content_results, new String[]{"TA", "IA", "XA", "YA", "ZA", "AA", "BA", "CA"},
                            new int[]{R.id.ta, R.id.ia, R.id.tota, R.id.tocl, R.id.roce, R.id.roe, R.id.ni1, R.id.roa});
                    listView.setVerticalScrollBarEnabled(true);
                    listView.setAdapter(RoceInfoAdapter);

                } catch (final Exception e) {
                    Log.e("Exception: ", e.getMessage());
                    Toast.makeText(getContext(), "There seems to be an issue in ROCE data. Please reload the app.", Toast.LENGTH_LONG).show();
                }
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                try {
                    RoceInfoList = new ArrayList<>();

                    Map<String, String[]> incomeMap = new HashMap<>();
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
                        /*Iterator it = incomeMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            Log.e("Key-Values", pair.getKey() + " = " + pair.getValue());
                    }*/

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
                        String[] totlb = balanceMap.get("TotalLiabilities");

                        for (int i = 0; i < revenueDates.length; i++) {
                            HashMap<String, String> RoceInfoHashMap = new HashMap<>();
                            RoceInfoHashMap.put("TA", "Quarterly ending " + revenueDates[i]);
                            RoceInfoHashMap.put("IA", "EBIT: " + formatter.format(Double.parseDouble(ebit[i])));
                            RoceInfoHashMap.put("XA", "Total Assets: " + formatter.format(Double.parseDouble(tota[i])));
                            RoceInfoHashMap.put("YA", "Total Current Liabilities: " + formatter.format(Double.parseDouble(tocl[i])));
                            RoceInfoHashMap.put("BA", "Net Income: " + formatter.format(Double.parseDouble(ni[i])));
                            //checking if it's a number
                            if (ebit[i].matches("-?\\d+(\\.\\d+)?") && tota[i].matches("-?\\d+(\\.\\d+)?") && tocl[i].matches("-?\\d+(\\.\\d+)?")) {
                                double ebit1 = Double.parseDouble(ebit[i]);
                                if (ebit1 == 0) {
                                    ebit1 = Double.parseDouble(ibt[1]);
                                    Toast.makeText(getContext(), "EBIT unavailable (maybe the company is a bank?). Using Income Before Taxes instead. Data is approximate.", Toast.LENGTH_LONG).show();
                                }
                                double tota1 = Double.parseDouble(tota[i]);
                                double tocl1 = Double.parseDouble(tocl[i]);
                                double roce = 100 * (ebit1 / (tota1 - tocl1));
                                double ni1 = Double.parseDouble(ni[i]);
                                double totlb1 = Double.parseDouble(totlb[i]);
                                double roe = 100 * (ni1 / (tota1 - totlb1));
                                double roa = 100 * (ni1 / (tota1));
                                DecimalFormat f = new DecimalFormat("##.000");
                                RoceInfoHashMap.put("ZA", "ROCE: " + f.format(roce) + "%");
                                RoceInfoHashMap.put("AA", "ROE: " + f.format(roe) + "%");
                                RoceInfoHashMap.put("CA", "ROA: " + f.format(roa) + "%");
                                RoceInfoList.add(RoceInfoHashMap);
                            } else {
                                RoceInfoHashMap.put("ZA", "ROCE: N/A. Try the manual calculator.");
                                RoceInfoHashMap.put("AA", "ROE: Data not available.");
                                RoceInfoHashMap.put("CA", "ROA: Data not available.");
                                RoceInfoList.add(RoceInfoHashMap);
                            }

                        }
                    }

                    //listItem = getResources().getStringArray(R.array.array_technology);
                    //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getData());
                    RoceInfoAdapter = new SimpleAdapter(getContext(), RoceInfoList,
                            R.layout.content_results, new String[]{"TA", "IA", "XA", "YA", "ZA", "AA", "BA", "CA"},
                            new int[]{R.id.ta, R.id.ia, R.id.tota, R.id.tocl, R.id.roce, R.id.roe, R.id.ni1, R.id.roa});
                    listView.setVerticalScrollBarEnabled(true);
                    listView.setAdapter(RoceInfoAdapter);

                } catch (final Exception e) {
                    Log.e("Exception: ", e.getMessage());
                    Toast.makeText(getContext(), "There seems to be an issue in ROCE data. Please reload the app.", Toast.LENGTH_LONG).show();
                }
            } else {

                try {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("We have no data on this. Try the manual calculator or contact the app developer to understand the issue.");
                } catch (final Exception e) {
                    Log.e("Exception: ", e.getMessage());
                    Toast.makeText(getContext(), "There seems to be an issue in ROCE data. Try the manual calculator or please reload the app.", Toast.LENGTH_LONG).show();
                }
            }
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);

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

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
            // Show 7 total pages.
            return 2;
        }
    }

}