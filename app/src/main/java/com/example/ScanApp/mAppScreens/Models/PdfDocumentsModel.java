package com.example.ScanApp.mAppScreens.Models;

import android.graphics.Bitmap;
import android.widget.CheckBox;

public class PdfDocumentsModel {
    private Bitmap bitmap;
    private String pdfTitle;
    private String pdfInfo;
    private Boolean isChecked;

    public PdfDocumentsModel(Bitmap bitmap, String pdfTitle, String pdfInfo, Boolean isChecked) {
        this.bitmap = bitmap;
        this.pdfTitle = pdfTitle;
        this.pdfInfo = pdfInfo;
        this.isChecked = isChecked;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPdfTitle() {
        return pdfTitle;
    }

    public void setPdfTitle(String pdfTitle) {
        this.pdfTitle = pdfTitle;
    }

    public String getPdfInfo() {
        return pdfInfo;
    }

    public void setPdfInfo(String pdfInfo) {
        this.pdfInfo = pdfInfo;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}
