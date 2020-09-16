package com.example.ScanApp.mAppScreens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.OpenCvClasses.DocumentScannerActivity;

public class MainPage extends AppCompatActivity {

    //Visual Objects
    ImageView folderImage,scanImage,imageViewBmp;
    SegmentedButtonGroup segmentedButtonGroup;

    //Veriables
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        defs();
        segmentGroupListener();
    }


    private void defs(){
        folderImage = findViewById(R.id.folderImage);
        scanImage = findViewById(R.id.scanImage);
        imageViewBmp=findViewById(R.id.imageViewBmp);
        folderImage.setImageResource(R.drawable.folder);
        scanImage.setImageResource(R.drawable.scan);

        segmentedButtonGroup = findViewById(R.id.segmentedButtonGroup);
        intent=new Intent(MainPage.this, DocumentScannerActivity.class);



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


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("LIFECYCLE","RESTART");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("LIFECYCLE","RESULT");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("LIFECYCLE","START");
    }
}