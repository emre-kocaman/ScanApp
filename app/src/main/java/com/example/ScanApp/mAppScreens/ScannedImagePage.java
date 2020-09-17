package com.example.ScanApp.mAppScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ScanApp.OpenCvClasses.DocumentScannerActivity;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Adapters.ScannedImageCardAdapter;
import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.mAppScreens.mUtils.mUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.kernel.geom.Line;

import java.io.File;
import java.util.ArrayList;

public class ScannedImagePage extends AppCompatActivity implements View.OnClickListener {

    //Veriables
    ScannedImageCardAdapter scannedImageCardAdapter;
    ArrayList<ScannedImageModel> scannedImageModelArrayList;
    ArrayList<ScannedImageModel> selectedScannedImageList;
    public Boolean checkboxesUnchecked=false;
    File root;

    Bitmap bmp;
    //Visual Objects
    FloatingActionButton floatingActionButtonDelete,floatingActionButtonCombine;
    RecyclerView recyclerViewScannedImages;
    Button buttonDone;
    FloatingActionButton buttonScanAgain;
    ConstraintLayout whenCheckedLayout;
    TextView textView;
    ImageView imageViewClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_image_page);
        def();
        clicks();
        getScannedImagesFromList();
        //exampleForScannedImagePage();
    }

    private void def(){
        recyclerViewScannedImages=findViewById(R.id.recyclerViewScannedImages);
        recyclerViewScannedImages.setHasFixedSize(true);
        recyclerViewScannedImages.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //recyclerViewScannedImages.setLayoutManager(new LinearLayoutManager(this));
        scannedImageModelArrayList=new ArrayList<>();
        bmp= BitmapFactory.decodeResource(getResources(),R.drawable.tmp);
        buttonDone=findViewById(R.id.buttonDone);
        buttonScanAgain=findViewById(R.id.buttonScanAgain);

        whenCheckedLayout=findViewById(R.id.whenCheckedLayout);
        selectedScannedImageList= new ArrayList<>();
        textView=findViewById(R.id.folderSelected);

        imageViewClose=findViewById(R.id.imageViewClose);
        floatingActionButtonDelete=findViewById(R.id.floatingActionButtonDelete);
        floatingActionButtonCombine=findViewById(R.id.floatingActionButtonCombine);

        root = new File(Environment.getExternalStorageDirectory(),"PDF dosyaları");
        if(!root.exists()){
            root.mkdir();
        }
    }

    private void clicks(){
        buttonDone.setOnClickListener(this);
        buttonScanAgain.setOnClickListener(this);
        floatingActionButtonDelete.setOnClickListener(this);
        floatingActionButtonCombine.setOnClickListener(this);
        imageViewClose.setOnClickListener(this);


    }


    private void getScannedImagesFromList(){
        scannedImageCardAdapter = new ScannedImageCardAdapter(StaticVeriables.scannedImageModelList,this);
        recyclerViewScannedImages.setAdapter(scannedImageCardAdapter);
    }

    private void exampleForScannedImagePage(){
        ScannedImageModel model = new ScannedImageModel(bmp,"Title","Info",false);

        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);

        scannedImageCardAdapter = new ScannedImageCardAdapter(scannedImageModelArrayList,ScannedImagePage.this);
        recyclerViewScannedImages.setAdapter(scannedImageCardAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonDone:
                //Seçilen resimleri pdf olarak sırasıyla kayıt edeceğimiz nokta burası.
                StaticVeriables.scannedImageModelList= new ArrayList<>();
                Intent intent = new Intent(ScannedImagePage.this,MainPage.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonScanAgain:
                //Eğer kullanıcı taranan resimler sayfasından tekrar bir resim scan etmek isterse buradan yönlendirip scan edip tekrar bu sayfaya resim eklenmiş bir şekilde geri dönüyoruz.
                StaticVeriables.photoCount=1;
                startActivity(new Intent(ScannedImagePage.this, DocumentScannerActivity.class));
                break;

            case R.id.floatingActionButtonCombine:
                if(selectedScannedImageList.size()!=0){
                    mUtils.createPdfOfImageFromList(root,selectedScannedImageList,this,false);
                    //mUtils.createPdfOfImage(root,selectedScannedImageList.get(0).getBitmap(),this);
                }
                break;

            case R.id.floatingActionButtonDelete:
                //scannedImageCardAdapter.RemoveItems(selectedScannedImageList);
                break;

            case R.id.imageViewClose:
                checkboxesUnchecked=false;
                break;
        }
    }

    public void MakeSelection(View v, int itemCount) {
        if (((CheckBox)v).isChecked()){
            whenCheckedLayout.setVisibility(View.VISIBLE);
            selectedScannedImageList.add(StaticVeriables.scannedImageModelList.get(itemCount));
            textView.setText(selectedScannedImageList.size() + " folder selected");
        }
        else{
            selectedScannedImageList.remove(StaticVeriables.scannedImageModelList.get(itemCount));
            if (selectedScannedImageList.size()==0){
                whenCheckedLayout.setVisibility(View.GONE);
            }
            else{
                textView.setText(selectedScannedImageList.size() + " folder selected");
            }
        }
    }


}