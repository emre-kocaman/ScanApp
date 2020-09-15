package com.example.ScanApp.mAppScreens.mUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.MainPage;
import com.example.ScanApp.newImport.DocumentScannerActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;

public class CropingImage extends AppCompatActivity implements View.OnClickListener {

    //Visual Objects
    ImageView imageViewCropped;
    Button saveCropppedImage;
    //Veriables
    Intent intent;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        //getSupportActionBar().show();
        defs();
        clicks();
        pickMatImgAndConvertToBitmap();
        startCrop(mUtils.getImageUriFromBitmap(this,bitmap));

    }

    private void defs(){
        imageViewCropped=findViewById(R.id.imageViewCropped);
         intent = new Intent(CropingImage.this, DocumentScannerActivity.class);
        saveCropppedImage=findViewById(R.id.saveCropppedImage);
    }

    private void clicks(){
        saveCropppedImage.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            com.theartofdev.edmodo.cropper.CropImage.ActivityResult result = com.theartofdev.edmodo.cropper.CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageViewCropped.setImageURI(result.getUri());
                Toast.makeText(this, "Image Update Succesfuly !!!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }


    private void pickMatImgAndConvertToBitmap(){
        if(StaticVeriables.scannedDocument!=null){
            Mat seedsImage = StaticVeriables.scannedDocument.processed;
            Mat tmp = new Mat (seedsImage.rows(),seedsImage.cols(), CvType.CV_8U, new Scalar(4));
            try {
                //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
                Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
                bitmap = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(tmp, bitmap);
            }
            catch (CvException e){
                Log.d("Exception",e.getMessage());}


            bitmap = mUtils.RotateBitmap(bitmap,90);
        }


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveCropppedImage:
                checkIsCard();
                break;
        }
    }

    private void checkIsCard(){
        Log.e("GIRIYORMUcount",String.valueOf(StaticVeriables.photoCount));
        //Alttaki işlemi yapmadan önce fotoğrafı kaydedicez.
        StaticVeriables.photoCount--;
        if (StaticVeriables.photoCount==0){//Tarama bitmiştir
            Log.e("TARAMA DURUMU","TARAMA BİTMİŞTİR");
            StaticVeriables.informationText="";
            StaticVeriables.photoCount=20;
            startActivity(new Intent(this,MainPage.class));
            finish();
        }
        else if (StaticVeriables.photoCount==1){//Kimlik sayfasının arka sayfasını çekmeye git
            Log.e("TARAMA DURUMU","2. TARAMAYA GİTMESİ LAZIM");
            StaticVeriables.informationText="SCAN THE BACK OF YOUR CARD";

            startActivity(intent);
        }
    }
}