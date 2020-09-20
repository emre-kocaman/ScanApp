package com.example.ScanApp.mAppScreens.Models;

import java.util.List;

public class Folder {
    String folderName;
    String folderUri;
    private Boolean isExpanded;
    private List<PdfDocumentsModel> pdfDocumentsModels;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderUri() {
        return folderUri;
    }

    public void setFolderUri(String folderUri) {
        this.folderUri = folderUri;
    }

    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public List<PdfDocumentsModel> getPdfDocumentsModels() {
        return pdfDocumentsModels;
    }

    public void setPdfDocumentsModels(List<PdfDocumentsModel> pdfDocumentsModels) {
        this.pdfDocumentsModels = pdfDocumentsModels;
    }

    public Folder(String folderName, String folderUri, List<PdfDocumentsModel> pdfDocumentsModels) {
        this.folderName = folderName;
        this.folderUri = folderUri;
        this.pdfDocumentsModels = pdfDocumentsModels;
        isExpanded=false;
    }
}
