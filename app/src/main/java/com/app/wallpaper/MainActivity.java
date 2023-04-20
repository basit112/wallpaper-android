package com.app.wallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.multidex.BuildConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.wallpaper.activity.SearchImageActivity;
import com.app.wallpaper.activity.WallpaperSetActivity;
import com.app.wallpaper.adapter.ImageAdapter;
import com.app.wallpaper.interfaces.ItemClickedListner;
import com.app.wallpaper.utils.SpacesItemDecoration;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ItemClickedListner {
    private RecyclerView mRecyclerView;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ConstraintLayout contentView;

    private MaterialToolbar materialToolbar;
    static final float END_SCALE = 0.7f;
    private ProgressBar mainProgress;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private static final String TAG = "MyActivity";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    private final String PRIVACY_LINK = "http://www.google.com";

    private int numCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.Theme_WallpaperOffline);
        mainProgress = findViewById(R.id.progressbar_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        materialToolbar = findViewById(R.id.material_toolbar);
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        contentView = findViewById(R.id.main_layout);
        MobileAds.initialize(this, initializationStatus -> {
        });
        adView = findViewById(R.id.main_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        //numCount = sharedPreferences.getInt("numCount", 0);
        navigationView.setCheckedItem(R.id.nav_home);
        materialToolbar.setNavigationOnClickListener(view -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });
        animateNavigationDrawer();
        //loadInterstitialAd();

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        ImageAdapter imageAdapter = new ImageAdapter(this,this);
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setAdapter(imageAdapter);
    }
//
//    private void loadInterstitialAd() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        InterstitialAd.load(
//                this,
//                getResources().getString(R.string.interstitials_ads),
//                adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        // The mInterstitialAd reference will be null until
//                        // an ad is loaded.
//
//                        MainActivity.this.mInterstitialAd = interstitialAd;
//                        Log.i(TAG, "onAdLoaded");
//                        //Toast.makeText(MainActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
//                        interstitialAd.setFullScreenContentCallback(
//                                new FullScreenContentCallback() {
//                                    @Override
//                                    public void onAdDismissedFullScreenContent() {
//                                        MainActivity.this.mInterstitialAd = null;
//                                        Log.d("TAG", "The ad was dismissed.");
//                                    }
//
//                                    @Override
//                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                        // Called when fullscreen content failed to show.
//                                        // Make sure to set your reference to null so you don't
//                                        // show it a second time.
//                                        MainActivity.this.mInterstitialAd = null;
//                                        Log.d("TAG", "The ad failed to show.");
//                                    }
//
//                                    @Override
//                                    public void onAdShowedFullScreenContent() {
//                                        // Called when fullscreen content is shown.
//                                        Log.d("TAG", "The ad was shown.");
//                                    }
//                                });
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        // Handle the error
//                        Log.i(TAG, loadAdError.getMessage());
//                        mInterstitialAd = null;
////                        String error = String.format(
////                                        "domain: %s, code: %d, message: %s",
////                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
////                        Toast.makeText(
////                                        MainActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
////                                .show();
//                    }
//                });
//    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home: {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.search: {
                refreshFunction();
                break;
            }
            case R.id.share_app: {
                shareAppFunction();
                break;
            }
            case R.id.rate_us: {
                rateUsFunction();
                break;
            }
            case R.id.privacy_policy: {
                privacyPolicyFunction();
                break;
            }
            case R.id.more_apps: {
                moreApps();
                break;
            }

        }
        return true;
    }

    private void rateUsFunction() {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            finish();
        } catch (ActivityNotFoundException e) {
            //
        }

    }

    private void refreshFunction() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        mainProgress.setVisibility(View.VISIBLE);
        new Handler().postDelayed((Runnable) () -> {
            mainProgress.setVisibility(View.INVISIBLE);
        }, 1000*2);
    }



    private void shareAppFunction() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "WallPix -HD Wallpapers");
            String shareMessage = "\nLet me recommend you this application.This is the best Wallpaper Application Check it out By SD code \n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    private void privacyPolicyFunction() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_LINK));
        startActivity(browserIntent);
    }

    private void moreApps() {

        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            finish();
        } catch (ActivityNotFoundException e) {
            //
        }


    }


    private void animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);
                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public void onItemClicked(int position, String fileName, String filePath) {
//        myEdit.putInt("numCount", ++numCount).apply();
//        if(numCount==3 || numCount>3){
//            myEdit.putInt("numCount", 0).apply();
//            if (mInterstitialAd != null) {
//                mInterstitialAd.show(this);
//                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                    @Override
//                    public void onAdDismissedFullScreenContent() {
//                        super.onAdDismissedFullScreenContent();
                        Intent intent = new Intent(MainActivity.this, WallpaperSetActivity.class);
                        intent.putExtra("imageFilePath", filePath);
                        intent.putExtra("filename",fileName);
                        startActivity(intent);
//                    }
//                });
//            } else {
//                Intent intent = new Intent(MainActivity.this, WallpaperSetActivity.class);
//                intent.putExtra("imageFilePath", filePath);
//                intent.putExtra("filename",fileName);
//                startActivity(intent);
//            }
//        }else {
//            Intent intent = new Intent(MainActivity.this, WallpaperSetActivity.class);
//            intent.putExtra("imageFilePath", filePath);
//            intent.putExtra("filename",fileName);
//            startActivity(intent);
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        //numCount = sharedPreferences.getInt("numCount", 0);
        //loadInterstitialAd();
    }
}

