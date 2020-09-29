package com.example.ScanApp.mAppScreens.Models;

import android.graphics.Bitmap;


public class ScannedImageModel  {

    private Bitmap bitmap;
    private String title,info;
    private Boolean isChecked;

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

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

    public ScannedImageModel(Bitmap bitmap, String title, String info, Boolean isChecked) {
        this.bitmap = bitmap.copy(bitmap.getConfig(),true);
        this.title = title;
        this.info = info;
        this.isChecked = isChecked;
    }

}
