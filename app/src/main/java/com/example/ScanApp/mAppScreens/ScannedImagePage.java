package com.example.ScanApp.mAppScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ScanApp.OpenCvClasses.DocumentScannerActivity;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Adapters.ScannedImageCardAdapter;
import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ScannedImagePage extends AppCompatActivity implements View.OnClickListener {

    //Veriables
    ScannedImageCardAdapter scannedImageCardAdapter;
    ArrayList<ScannedImageModel> scannedImageModelArrayList;
    Bitmap bmp;
    //Visual Objects
    RecyclerView recyclerViewScannedImages;
    Button buttonDone;
    FloatingActionButton buttonScanAgain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_image_page);
        def();
        clicks();
        exampleForScannedImagePage();
    }

    private void def(){
        recyclerViewScannedImages=findViewById(R.id.recyclerViewScannedImages);
        recyclerViewScannedImages.setHasFixedSize(true);
        recyclerViewScannedImages.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        scannedImageModelArrayList=new ArrayList<>();
        bmp= BitmapFactory.decodeResource(getResources(),R.drawable.tmp);
        buttonDone=findViewById(R.id.buttonDone);
        buttonScanAgain=findViewById(R.id.buttonScanAgain);
    }

    private void clicks(){
        buttonDone.setOnClickListener(this);
        buttonScanAgain.setOnClickListener(this);
    }

    private void exampleForScannedImagePage(){
        ScannedImageModel model = new ScannedImageModel(bmp,"Title","Info");

        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);

        scannedImageCardAdapter = new ScannedImageCardAdapter(this,scannedImageModelArrayList);
        recyclerViewScannedImages.setAdapter(scannedImageCardAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonDone:
                //Seçilen resimleri pdf olarak sırasıyla kayıt edeceğimiz nokta burası.
                Intent intent = new Intent(ScannedImagePage.this,MainPage.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonScanAgain:
                //Eğer kullanıcı taranan resimler sayfasından tekrar bir resim scan etmek isterse buradan yönlendirip scan edip tekrar bu sayfaya resim eklenmiş bir şekilde geri dönüyoruz.
                startActivity(new Intent(ScannedImagePage.this, DocumentScannerActivity.class));
                break;
        }
    }




}