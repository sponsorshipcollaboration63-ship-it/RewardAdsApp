package com.rewardapp;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private RewardedAd rewardedAd;
    private static final String AD_UNIT_ID = "ca-app-pub-2761605268905056/3704425417";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize AdMob
        MobileAds.initialize(this, initializationStatus -> {});
        
        // Load first ad
        loadRewardedAd();
        
        // Setup WebView
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        
        setContentView(webView);
        
        // Load HTML file
        webView.loadUrl("file:///android_asset/reward.html");
    }
    
    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, AD_UNIT_ID, adRequest, 
            new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(RewardedAd ad) {
                    rewardedAd = ad;
                }
                
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    rewardedAd = null;
                }
            });
    }
    
    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(this, rewardItem -> {
                // User earned reward
                webView.evaluateJavascript("rewardUser(0.01)", null);
                loadRewardedAd(); // Load next ad
            });
        }
    }
    
    // JavaScript Interface
    public class WebAppInterface {
        @JavascriptInterface
        public void showAd() {
            runOnUiThread(() -> showRewardedAd());
        }
    }
}