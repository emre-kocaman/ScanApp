package com.example.ScanApp.mAppScreens.Models;

import android.graphics.Bitmap;

public class ScannedImageModel {

    private Bitmap bitmap;
    private String title,info;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ScannedImageModel(Bitmap bitmap, String title, String info) {
        this.bitmap = bitmap;
        this.title = title;
        this.info = info;
    }
}
