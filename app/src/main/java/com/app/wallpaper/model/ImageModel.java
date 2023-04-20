package com.app.wallpaper.model;


import android.graphics.Bitmap;

public class ImageModel {

    private Bitmap mImage;
    private String mTitle;
    private String mFilePath;

    public ImageModel(Bitmap mImage, String mTitle, String mFilePath) {
        this.mImage = mImage;
        this.mTitle = mTitle;
        this.mFilePath = mFilePath;
    }

    public ImageModel() {}

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }
}