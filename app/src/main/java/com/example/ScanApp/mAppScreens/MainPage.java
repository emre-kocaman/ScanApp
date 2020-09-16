package com.example.ScanApp.mAppScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Adapters.PdfsCardAdapter;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.OpenCvClasses.DocumentScannerActivity;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity {

    //Visual Objects
    ImageView folderImage,scanImage;
    SegmentedButtonGroup segmentedButtonGroup;
    ConstraintLayout whenCheckedLayout;


    private RecyclerView recyclerView;
    private ArrayList<PdfDocumentsModel> pdfDocumentsModelArrayList;
    private ArrayList<PdfDocumentsModel> checkedPdfList;
    private PdfsCardAdapter pdfsCardAdapter;
    private TextView folderSelected;
    //Veriables
    Intent intent;
    Bitmap temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        temp = BitmapFactory.decodeResource(getResources(),R.drawable.tmp);
        defs();
        segmentGroupListener();
        exampleForMainPage();
    }


    private void defs(){
        folderSelected=findViewById(R.id.folderSelected);
        folderImage = findViewById(R.id.folderImage);
        scanImage = findViewById(R.id.scanImage);
        folderImage.setImageResource(R.drawable.folder);
        scanImage.setImageResource(R.drawable.scan);

        segmentedButtonGroup = findViewById(R.id.segmentedButtonGroup);
        intent=new Intent(MainPage.this, DocumentScannerActivity.class);

        recyclerView=findViewById(R.id.pdfRv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        pdfDocumentsModelArrayList=new ArrayList<>();
        checkedPdfList=new ArrayList<>();

        whenCheckedLayout=findViewById(R.id.whenCheckedLayout);

    }

    private void segmentGroupListener(){
        segmentedButtonGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    StaticVeriables.photoCount=2;
                    StaticVeriables.informationText="SCAN THE FRONT OF YOUR CARD";
                }
                else{
                    StaticVeriables.photoCount=1;
                    StaticVeriables.informationText="SCAN YOUR DOCUMENT.";

                }
                startActivity(intent);
            }
        });
    }

    private void exampleForMainPage(){
        PdfDocumentsModel pdfDocumentsModel1 = new PdfDocumentsModel(temp,"scanner-app-wireframe","1.9 MB - 13 Eylül, 16:14");

        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);

        pdfsCardAdapter = new PdfsCardAdapter(this,pdfDocumentsModelArrayList,checkedPdfList,whenCheckedLayout,folderSelected);
        recyclerView.setAdapter(pdfsCardAdapter);
    }

}