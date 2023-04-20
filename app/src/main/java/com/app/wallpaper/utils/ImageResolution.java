package com.app.wallpaper.utils;

import android.graphics.Bitmap;

public class ImageResolution {
    private int width;
    private int height;
    private float resolution;

    public ImageResolution(int width, int height, float resolution) {
        this.width = width;
        this.height = height;
        this.resolution = resolution;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getResolution() {
        return resolution;
    }

    public void setResolution(float resolution) {
        this.resolution = resolution;
    }
}

class ImageUtils {

    public static ImageResolution getImageResolution(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float resolution = bitmap.getDensity();

        return new ImageResolution(width, height, resolution);
    }
}