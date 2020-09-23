package com.example.ScanApp.mAppScreens.mUtils;


import android.os.Environment;

import com.example.ScanApp.OpenCvClasses.ScannedDocument;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;
import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;

import java.util.ArrayList;
import java.util.List;

public class StaticVeriables {

    public static String path = Environment.getExternalStorageDirectory().toString()+"/PDF folders";

    public static int photoCount = 20;
    public static String informationText = "";
    public static ScannedDocument scannedDocument=null;

    public static List<ScannedImageModel> scannedImageModelList = new ArrayList<>();
    public static  List<PdfDocumentsModel> checkedPdfList=new ArrayList<>();
    public static boolean userWillScanCard=false;

    public static PdfDocumentsModel movingPdfModel=null;


}
