/*
Copyright (c) 2024, Upendra Rajan
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree.
 */
package com.upen.rocecalculator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewFin extends AppCompatActivity {
    LinearLayout layout2;
    WebView myWebView;
    String netStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            netStatus = chkStatus();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Find your company in search below:");
            myWebView = (WebView) findViewById(R.id.webView1);
            Bundle bundle = getIntent().getExtras();
            String url = bundle.getString("url");
            //listView.setVisibility(View.GONE);
            //myWebView.setVisibility(View.VISIBLE);

            //myWebView.setWebChromeClient(new WebChromeClient());
            if (netStatus.equalsIgnoreCase("You are not connected to the Internet.")) {
                Toast.makeText(getApplicationContext(), "Please connect to the Internet and try again.", Toast.LENGTH_LONG).show();
            } else {
                myWebView.loadUrl(url);
                myWebView.setWebViewClient(new WebViewClient() {
                    public void onLoadResource(android.webkit.WebView view, String url) {
                        super.onLoadResource(view, url);
                    }
                });
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.getSettings().setLoadWithOverviewMode(true);
                myWebView.getSettings().setUseWideViewPort(true);
                myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                myWebView.getSettings().setSupportMultipleWindows(true);
                //myWebView.getSettings().setSupportZoom(true);
                myWebView.getSettings().setBuiltInZoomControls(true);
                myWebView.getSettings().setUseWideViewPort(true);
                //Toast.makeText(getApplicationContext(), "Hit the search button on your device keyboard to start the search.", Toast.LENGTH_LONG).show();
            }


        } catch (final Exception e) {
            Log.e("Exception: ", e.getMessage());
            Toast.makeText(getApplicationContext(), "There seems to be a network issue. Please check connection or contact the admin.", Toast.LENGTH_LONG).show();
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

}
