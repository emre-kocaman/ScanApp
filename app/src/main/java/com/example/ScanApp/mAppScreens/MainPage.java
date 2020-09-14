package com.example.ScanApp.mAppScreens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.example.ScanApp.OpenNoteScannerActivity;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.mAppScreens.mUtils.mUtils;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainPage extends AppCompatActivity {

    //Visual Objects
    ImageView folderImage,scanImage,imageViewTaken;
    SegmentedButtonGroup segmentedButtonGroup;

    //Veriables

    Intent intent;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        defs();
        pickMatImg();
        segmentGroupListener();
    }


    private void defs(){
        folderImage = findViewById(R.id.folderImage);
        scanImage = findViewById(R.id.scanImage);

        folderImage.setImageResource(R.drawable.folder);
        scanImage.setImageResource(R.drawable.scan);

        imageViewTaken=findViewById(R.id.imageViewTaken);

        segmentedButtonGroup = findViewById(R.id.segmentedButtonGroup);
        intent=new Intent(MainPage.this, OpenNoteScannerActivity.class);



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

    private void pickMatImg(){
        if(StaticVeriables.scannedDocument!=null){
            Mat seedsImage = StaticVeriables.scannedDocument.processed;
            Mat tmp = new Mat (seedsImage.rows(),seedsImage.cols(), CvType.CV_8U, new Scalar(4));
            try {
                //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
                Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
                bitmap = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(tmp, bitmap);
            }
            catch (CvException e){Log.d("Exception",e.getMessage());}


            bitmap = mUtils.RotateBitmap(bitmap,90);
            //imageViewTaken.getLayoutParams().height=seedsImage.height();
            //imageViewTaken.getLayoutParams().width=seedsImage.width();
            imageViewTaken.setImageBitmap(bitmap);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        StaticVeriables.photoCount--;
        if (StaticVeriables.photoCount==0){//Tarama bitmiştir
            Log.e("TARAMA DURUMU","TARAMA BİTMİŞTİR");
            StaticVeriables.informationText="";
            StaticVeriables.photoCount=20;
        }
        else if (StaticVeriables.photoCount==1){//Kimlik sayfasının arka sayfasını çekmeye git
            Log.e("TARAMA DURUMU","2. TARAMAYA GİTMESİ LAZIM");
            StaticVeriables.informationText="SCAN THE BACK OF YOUR CARD";

            startActivity(intent);
        }
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