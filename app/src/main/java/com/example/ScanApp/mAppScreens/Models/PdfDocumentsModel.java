package com.example.ScanApp.mAppScreens.Models;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.CheckBox;

import java.util.Objects;

public class PdfDocumentsModel {
    private Bitmap bitmap;
    private String pdfTitle;
    private String pdfInfo;
    private Boolean isChecked;
    private String name;
    private String filePath;
    private Uri uri;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public PdfDocumentsModel(Bitmap bitmap, String pdfTitle, String pdfInfo, Boolean isChecked, String name, String filePath, Uri uri) {
        this.bitmap = bitmap;
        this.pdfTitle = pdfTitle;
        this.pdfInfo = pdfInfo;
        this.isChecked = isChecked;
        this.name = name;
        this.filePath = filePath;
        this.uri = uri;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PdfDocumentsModel that = (PdfDocumentsModel) o;
        return Objects.equals(bitmap, that.bitmap) &&
                Objects.equals(pdfTitle, that.pdfTitle) &&
                Objects.equals(pdfInfo, that.pdfInfo) &&
                Objects.equals(isChecked, that.isChecked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitmap, pdfTitle, pdfInfo, isChecked);
    }
}
