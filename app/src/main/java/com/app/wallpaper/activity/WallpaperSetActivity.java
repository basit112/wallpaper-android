package com.app.wallpaper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.wallpaper.MainActivity;
import com.app.wallpaper.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WallpaperSetActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private View viewDetail;
    private LinearLayout linearLayout;
    private TextView TitleName;
    private String filePath;
    private String filename;
    private ImageView wallPaperImageView;
    private ImageView infoButton;
    private ImageView downloadButton;
    private ImageView setWallpaperButton;
    private AdView adView;

    private InterstitialAd mInterstitialAd;
    private static final String TAG = "WallpaperActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_set);

        viewDetail = findViewById(R.id.view_detail);
        linearLayout = findViewById(R.id.LL_credit);
        linearLayout.setVisibility(View.GONE);
        TitleName = findViewById(R.id.credit_photo_name);
        wallPaperImageView = findViewById(R.id.wallpaper_full_display);
        progressBar = findViewById(R.id.progressbar);
        infoButton = findViewById(R.id.info_button);
        downloadButton = findViewById(R.id.download_button);
        setWallpaperButton = findViewById(R.id.set_wallpaper_button);
        MobileAds.initialize(this, initializationStatus -> {
        });

        loadInterstitialAd();
        adView = findViewById(R.id.wallpaper_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        infoButton.setOnClickListener(v -> {
            infoFunction();
        });
        downloadButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Download Picture")
                    .setMessage("Want to download this picture?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        downloadFunction();
                        // Continue with delete operation
                    }).setNegativeButton("No",(dialogInterface, i) -> {
                        dialogInterface.cancel();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            //downloadImage(this,wallPaperImageView,filePath);

        });

        setWallpaperButton.setOnClickListener(v -> {
            setWallpaperFunction();

        });
        Bundle bundle = getIntent().getExtras();
        filePath = bundle.getString("imageFilePath");
        filename = bundle.getString("filename");


        if (filePath != null) {
            String filePath = getIntent().getStringExtra("imageFilePath");
            setImageINView(filePath);
            progressBar.setVisibility(View.INVISIBLE);
            // linearLayout.setVisibility(View.VISIBLE);
        } else {
            wallPaperImageView.setImageDrawable(getResources().getDrawable(R.drawable.placeholder_image));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setWallpaperFunction() {
        new AlertDialog.Builder(this)
                .setTitle("Set wallpaper")
                .setMessage("You want to set wallpaper?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, which) -> {
                    setThisWallpaper();
                    progressBar.setVisibility(View.VISIBLE);
                    // Continue with delete operation
                }).setNegativeButton("No",(dialogInterface, i) -> {
                    dialogInterface.cancel();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setThisWallpaper() {
        wallPaperImageView.buildDrawingCache();
        Bitmap bitmap = wallPaperImageView.getDrawingCache();
        WallpaperManager wallManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallManager.setBitmap(bitmap);
            Toast.makeText(WallpaperSetActivity.this, "Wallpaper Set Successfully!!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        } catch (IOException e) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(WallpaperSetActivity.this, "Setting WallPaper Failed!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFunction() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }
        wallPaperImageView.buildDrawingCache();
        Bitmap bmp = wallPaperImageView.getDrawingCache();
        File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //context.getExternalFilesDir(null);

        File file = new File(storageLoc, filename + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Toast.makeText(this, "Image downloaded successfully!!", Toast.LENGTH_SHORT).show();


            scanFile(this, Uri.fromFile(file));

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Image Not Saved!!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Image Not Saved!!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private static void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }
//    public static void downloadImage(Context context, ImageView imageView, String fileName) {
//        // Check for write external storage permission
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Request permission if not granted
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            return;
//        }
//
//        // Get the bitmap from the ImageView
//        imageView.setDrawingCacheEnabled(true);
//        imageView.buildDrawingCache();
//        Bitmap bitmap = imageView.getDrawingCache();
//
//        // Save the bitmap to a file in external storage
//        File directory = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "WallPapers");
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//        File file = new File(directory, fileName);
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//            Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show();
//
//            // Use DownloadManager to scan the file and make it available in the gallery
//            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//            downloadManager.addCompletedDownload(file.getName(), file.getName(), true, "image/*", file.getAbsolutePath(), file.length(), true);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void infoFunction() {
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void setImageINView(String name) {
        TitleName.setText(name);
        AssetManager assetManager = this.getAssets();
        try {
//            InputStream inputStream = getAssets().open(filePath);
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            mImageView.setImageBitmap(bitmap);
//            inputStream.close();
            if (name.endsWith(".jpg") || name.endsWith(".png")) {
                InputStream inputStream = assetManager.open(name);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                wallPaperImageView.setImageBitmap(bitmap);
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                getResources().getString(R.string.interstitials_ads),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.

                        WallpaperSetActivity.this.mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        //Toast.makeText(MainActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        WallpaperSetActivity.this.mInterstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        WallpaperSetActivity.this.mInterstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
//                        String error = String.format(
//                                        "domain: %s, code: %d, message: %s",
//                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
//                        Toast.makeText(
//                                        MainActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
//                                .show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    finish();
                }
            });
        } else {
            super.onBackPressed();
        }
    }
}