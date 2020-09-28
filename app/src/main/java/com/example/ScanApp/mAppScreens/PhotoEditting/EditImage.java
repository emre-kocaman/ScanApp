package com.example.ScanApp.mAppScreens.PhotoEditting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.MainActivity;
import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;
import com.example.ScanApp.mAppScreens.ScannedImagePage;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.mAppScreens.mUtils.mUtils;
import com.example.ScanApp.OpenCvClasses.DocumentScannerActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class EditImage extends AppCompatActivity implements View.OnClickListener {

    //Visual Objects
    ImageView imageViewCropped;
    Button saveCropppedImage;
    SeekBar seekBarConstrat,seekBarBrightness,seekBarSharpness;
    //Veriables
    Intent intent;
    Bitmap bitmap=null,original=null;
    Uri uri=null;
    PictureThread thread;
    File root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        //getSupportActionBar().show();
        defs();
        clicks();
        pickMatImgAndConvertToBitmap();
        startCrop(mUtils.getImageUriFromBitmap(this,bitmap));
        seekBarsListener();

        Log.e("CREATEPRGBRGHT",String.valueOf(seekBarBrightness.getProgress()));
        Log.e("CREATEPRGSHRPN",String.valueOf(seekBarConstrat.getProgress()));

    }

    private void defs(){

        seekBarConstrat=findViewById(R.id.seeekBarContrast);
        seekBarBrightness=findViewById(R.id.seekBarBrightness);
        seekBarSharpness=findViewById(R.id.seekBarSharpness);

        imageViewCropped=findViewById(R.id.imageViewCropped);
        intent = new Intent(EditImage.this, DocumentScannerActivity.class);
        saveCropppedImage=findViewById(R.id.saveCropppedImage);


        root = new File(Environment.getExternalStorageDirectory(), "PDF folders/Default Pdf Folder");
        if(!root.exists()){
            root.mkdir();
        }
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
                uri=result.getUri();
                try {
                    bitmap=mUtils.getBitmapFromUri(uri,bitmap,this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    imageViewCropped.setImageBitmap(mUtils.getBitmapFromUri(uri,bitmap,this));
                    thread = new PictureThread(imageViewCropped,bitmap);
                    thread.start();
                    original = bitmap.copy(bitmap.getConfig(),true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageViewCropped.setImageURI(result.getUri());
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
        if(getIntent().getBooleanExtra("isGallery",false))
        {
            bitmap=StaticVeriables.getScannedFromGallery;
            bitmap = mUtils.RotateBitmap(bitmap,90);
        }
        else{
            Log.e("HANGISINEGIRDI","KAMERA");

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



    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveCropppedImage:
                saveImageToList();
                checkIsCard();
                break;
        }
    }

    private void checkIsCard(){
        StaticVeriables.photoCount--;
        if (StaticVeriables.photoCount==0 && !StaticVeriables.userWillScanCard){
            //Kullanıcı döküman scan etmiştir ve tarama bitmiştir taranan resimlerin olduğu sayfaya gönder.
            StaticVeriables.informationText="";
            StaticVeriables.photoCount=20;
            Intent intent = new Intent(this, ScannedImagePage.class);
            startActivity(intent);
            finish();
        }
        else if(StaticVeriables.photoCount==0 && StaticVeriables.userWillScanCard){
            //Kullanıcı kard scan etmiştir ve tarama bitmiştir pdf oluşturup direk ana sayfaya gönder
            showAlert(this);

        }
        else if (StaticVeriables.photoCount==1){//Kimlik sayfasının arka sayfasını çekmeye git
            StaticVeriables.informationText="SCAN THE BACK OF YOUR CARD";
            startActivity(intent);
        }
    }

    private void saveImageToList(){
        ScannedImageModel scannedImageModel = new ScannedImageModel(bitmap,StaticVeriables.photoCount+". image","",false);
        StaticVeriables.scannedImageModelList.add(scannedImageModel);
    }

    private void seekBarsListener(){
        //Brightness seek bar
        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                thread.adjustBrightnessAndSharpness(seekBar.getProgress(),seekBarConstrat.getProgress());
                bitmap=thread.temp_bitmap;
                original = bitmap.copy(bitmap.getConfig(),true);
                Log.e("LISTENERPRGBRGHT",String.valueOf(seekBarBrightness.getProgress()));
                Log.e("LISTENERPRGSHRPN",String.valueOf(seekBarConstrat.getProgress()));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //sharpness seekbar
        seekBarConstrat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                thread.adjustBrightnessAndSharpness(seekBarBrightness.getProgress(),seekBar.getProgress());
                bitmap=thread.temp_bitmap;
                original = bitmap.copy(bitmap.getConfig(),true);

                Log.e("LISTENERPRGBRGHT",String.valueOf(seekBarBrightness.getProgress()));
                Log.e("LISTENERPRGSHRPN",String.valueOf(seekBarConstrat.getProgress()));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarSharpness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress){
                    case 0:
                        bitmap= original.copy(original.getConfig(),true);
                        imageViewCropped.setImageBitmap(bitmap);

                        break;
                    case 1:
                        loadBitmapSharp2();
                        imageViewCropped.setImageBitmap(bitmap);
                        break;
                    case 2:
                        loadBitmapSharp1();
                        imageViewCropped.setImageBitmap(bitmap);
                        break;
                    case 3:
                        loadBitmapSharp();
                        imageViewCropped.setImageBitmap(bitmap);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    public void showAlert(Context context)
    {

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View tasarim = layoutInflater.inflate(R.layout.alert_design,null);

        final EditText editTextFolderName = tasarim.findViewById(R.id.editTextFolderName);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("File Name");
        alert.setView(tasarim);
        alert.setMessage("\n"+"Write your pdf file name\n");
        alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String folderName = editTextFolderName.getText().toString().trim();
                if (folderName.length()!=0){
                    mUtils.createPdfOfCard(root,StaticVeriables.scannedImageModelList,folderName);
                    //mUtils.createPdfOfImageFromList(root,StaticVeriables.scannedImageModelList,context,folderName);
                    Intent intent = new Intent(EditImage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(context, "Name cant be empty", Toast.LENGTH_SHORT).show();
                    StaticVeriables.photoCount++;
                }


            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StaticVeriables.photoCount++;
            }
        });

        alert.create().show();
    }

    public Bitmap doSharpen(Bitmap original, float[] radius, Context context) {

        for (float sayi:radius
        ) {
            Log.e("DIZI ELEMANLARI",String.valueOf(sayi));
        }
        //original = temp_bitmap.copy(temp_bitmap.getConfig(),false);
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(radius);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);
        rs.destroy();

        return bitmap;

    }


    // low
    private  void loadBitmapSharp() {
        float[] sharp = { -0.60f, -0.60f, -0.60f,
                -0.60f, 5.81f, -0.60f,
                -0.60f, -0.60f, -0.60f };
//you call the method above and just paste the bitmap you want to apply it and the float of above
        bitmap = doSharpen(original, sharp,this);
    }

    // medium
    private  void loadBitmapSharp1() {
        float[] sharp = { 0.0f, -1.0f, 0.0f,
                -1.0f, 5.0f, -1.0f,
                0.0f, -1.0f, 0.0f

        };
//you call the method above and just paste the bitmap you want to apply it and the float of above
        bitmap = doSharpen(original, sharp,this);
    }

    // high
    private  void loadBitmapSharp2() {
        float[] sharp = { -0.15f, -0.15f, -0.15f,
                -0.15f, 2.2f, -0.15f,
                -0.15f, -0.15f, -0.15f
        };
        //you call the method above and just paste the bitmap you want to apply it and the float of above
        bitmap = doSharpen(original, sharp,this);
    }

}