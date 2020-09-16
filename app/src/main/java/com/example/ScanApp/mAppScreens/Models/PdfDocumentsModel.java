package com.example.ScanApp.mAppScreens.Models;

import android.graphics.Bitmap;

public class PdfDocumentsModel {
    private Bitmap bitmap;
    private String pdfTitle;
    private String pdfInfo;

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

    public PdfDocumentsModel(Bitmap bitmap, String pdfTitle, String pdfInfo) {
        this.bitmap = bitmap;
        this.pdfTitle = pdfTitle;
        this.pdfInfo = pdfInfo;
    }
}
