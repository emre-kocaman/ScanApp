package com.example.ScanApp.mAppScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.CirclePromptFocal;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class ScannedImagePage extends AppCompatActivity implements View.OnClickListener {

    //Veriables
    ScannedImageCardAdapter scannedImageCardAdapter;
    ArrayList<ScannedImageModel> scannedImageModelArrayList;
    ArrayList<ScannedImageModel> selectedScannedImageList;
    File root;
    Boolean isPdfCreated=false;

    Bitmap bmp;
    //Visual Objects
    View viewItem;
    View icon ;
    FloatingActionButton floatingActionButtonDelete,floatingActionButtonCombine;
    RecyclerView recyclerViewScannedImages;
    Button buttonDone;
    FloatingActionButton buttonScanAgain;
    ConstraintLayout whenCheckedSp;
    TextView textView;
    ImageView imageViewCloseSp;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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
        //recyclerViewScannedImages.setLayoutManager(new LinearLayoutManager(this));
        scannedImageModelArrayList=new ArrayList<>();
        bmp= BitmapFactory.decodeResource(getResources(),R.drawable.tmp);
        buttonDone=findViewById(R.id.buttonDone);
        buttonScanAgain=findViewById(R.id.buttonScanAgain);

        whenCheckedSp=findViewById(R.id.whenCheckedSp);
        selectedScannedImageList= new ArrayList<>();
        textView=findViewById(R.id.folderSelected);

        imageViewCloseSp=findViewById(R.id.imageViewCloseSp);
        floatingActionButtonDelete=findViewById(R.id.floatingActionButtonDelete);
        floatingActionButtonCombine=findViewById(R.id.floatingActionButtonCombine);

        root = new File(Environment.getExternalStorageDirectory(),"PDF folders/Default Pdf Folder");
        if(!root.exists()){
            root.mkdir();
        }

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void clicks(){
        buttonDone.setOnClickListener(this);
        buttonScanAgain.setOnClickListener(this);
        floatingActionButtonDelete.setOnClickListener(this);
        floatingActionButtonCombine.setOnClickListener(this);
        imageViewCloseSp.setOnClickListener(this);



    }


    private void getScannedImagesFromList(){

        scannedImageCardAdapter = new ScannedImageCardAdapter(StaticVeriables.scannedImageModelList);
        recyclerViewScannedImages.setHasFixedSize(true);
        recyclerViewScannedImages.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerViewScannedImages.setAdapter(scannedImageCardAdapter);
        startMainPageTutorial1();
        new Handler().postDelayed(this::startMainPageTutorial1,1000);
    }

    private void exampleForScannedImagePage(){
        ScannedImageModel model = new ScannedImageModel(bmp,"Title","Info",false);

        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);
        scannedImageModelArrayList.add(model);

        scannedImageCardAdapter = new ScannedImageCardAdapter(scannedImageModelArrayList);
        recyclerViewScannedImages.setAdapter(scannedImageCardAdapter);
    }

    private void goToMainPage(){
        if (isPdfCreated){//Eğer kullanıcı zaten pdf combine ettiyse done buttonuna basınca bir daha combine etmesine gerek kalmadan direk ana sayfaya git
            //Seçilen resimleri pdf olarak sırasıyla kayıt edeceğimiz nokta burası.
            StaticVeriables.scannedImageModelList= new ArrayList<>();
            Intent intent = new Intent(ScannedImagePage.this,MainPage.class);
            startActivity(intent);
            finish();
        }
        else{
            showWhenPressDone(this);

//            mUtils.createPdfOfImageFromList(root,StaticVeriables.scannedImageModelList,this,);
//            whenCheckedSp.setVisibility(View.GONE);

        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonDone:
                goToMainPage();
                break;
            case R.id.buttonScanAgain:
                //Eğer kullanıcı taranan resimler sayfasından tekrar bir resim scan etmek isterse buradan yönlendirip scan edip tekrar bu sayfaya resim eklenmiş bir şekilde geri dönüyoruz.
                StaticVeriables.photoCount=1;
                startActivity(new Intent(ScannedImagePage.this, DocumentScannerActivity.class));
                break;

            case R.id.floatingActionButtonCombine:
                if(selectedScannedImageList.size()!=0){
                    showAlert(this);
                }
                else{
                    Toast.makeText(this, "You must chose image/images!!!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.floatingActionButtonDelete:
                RemoveItems(selectedScannedImageList);
                break;

            case R.id.imageViewCloseSp:
                whenCheckedSp.setVisibility(View.GONE);
                break;
        }
    }

    public void RemoveItems(ArrayList<ScannedImageModel> selectedScannedImageList) {
        Log.e("SELECTEDSIZE",String.valueOf(selectedScannedImageList.size()));
        for (int i = 0 ; i<selectedScannedImageList.size();i++){
            StaticVeriables.scannedImageModelList.remove(selectedScannedImageList.get(i));

        }
        scannedImageCardAdapter.notifyDataSetChanged();
        whenCheckedSp.setVisibility(View.GONE);
        selectedScannedImageList.clear();


    }

    public void MakeSelection(View v, int itemCount) {
        if (((CheckBox)v).isChecked()){
            whenCheckedSp.setVisibility(View.VISIBLE);
            selectedScannedImageList.add(StaticVeriables.scannedImageModelList.get(itemCount));
            textView.setText(selectedScannedImageList.size() + " folder selected");
        }
        else{
            selectedScannedImageList.remove(StaticVeriables.scannedImageModelList.get(itemCount));
            if (selectedScannedImageList.size()==0){
                whenCheckedSp.setVisibility(View.GONE);
            }
            else{
                textView.setText(selectedScannedImageList.size() + " folder selected");
            }
        }
    }


    public void showWhenPressDone(Context context)
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
                String fileName = editTextFolderName.getText().toString().trim();
                if (fileName.length()!=0){
                    mUtils.createPdfOfImageFromList(root,StaticVeriables.scannedImageModelList,context,fileName);
                    whenCheckedSp.setVisibility(View.GONE);
                    StaticVeriables.scannedImageModelList= new ArrayList<>();
                    Intent intent = new Intent(ScannedImagePage.this,MainPage.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(context, "Name cant be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.create().show();
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
                String fileName = editTextFolderName.getText().toString().trim();
                if (fileName.length()!=0){
                    Log.e("SIZEOFSCANNED",String.valueOf(StaticVeriables.scannedImageModelList.size()));
                    mUtils.createPdfOfImageFromList(root,selectedScannedImageList,context,fileName);
                    whenCheckedSp.setVisibility(View.GONE);
                    isPdfCreated=true;
                }
                else{
                    Toast.makeText(context, "Name cant be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.create().show();
    }

    public void startMainPageTutorial1() {
        //sharedPreferences.getBoolean("isFirstTimeScannedImagePage",true)
        if (recyclerViewScannedImages.getAdapter() != null && sharedPreferences.getBoolean("isFirstTimeScannedImagePage",true)){
            View targetItem = ((ScannedImageCardAdapter) recyclerViewScannedImages.getAdapter()).focusItem;

            new MaterialTapTargetPrompt.Builder(ScannedImagePage.this)
                    .setTarget(targetItem)
                    .setCaptureTouchEventOnFocal(true)
                    .setBackButtonDismissEnabled(true)
                    .setBackgroundColour(Color.parseColor("#FA8A00"))
                    .setPrimaryText("Scanned Images")
                    .setSecondaryText(R.string.ScannedImage1)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                    {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                        {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state==MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                            {
                                editor=sharedPreferences.edit().putBoolean("isFirstTimeScannedImagePage",false);
                                editor.apply();
                                whenCheckedSp.setVisibility(View.VISIBLE);
                                startMainPageTutorial2();
                            }
                        }
                    })
                    .show();
        }



    }



    public void startMainPageTutorial2() {

            new MaterialTapTargetPrompt.Builder(ScannedImagePage.this)
                    .setTarget(floatingActionButtonCombine)
                    .setCaptureTouchEventOnFocal(true)
                    .setBackButtonDismissEnabled(true)
                    .setBackgroundColour(Color.parseColor("#FA8A00"))
                    .setPromptFocal(new RectanglePromptFocal())
                    .setPrimaryText("Convert scanned images to pdf")
                    .setSecondaryText(R.string.ScannedImage2)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                    {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                        {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state==MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                            {
                                whenCheckedSp.setVisibility(View.GONE);
                                startMainPageTutorial3();
                            }
                        }
                    })
                    .show();


    }

    public void startMainPageTutorial3() {

        new MaterialTapTargetPrompt.Builder(ScannedImagePage.this)
                .setTarget(buttonScanAgain)
                .setCaptureTouchEventOnFocal(true)
                .setBackButtonDismissEnabled(true)
                .setBackgroundColour(Color.parseColor("#FA8A00"))
                .setPromptFocal(new RectanglePromptFocal())
                .setPrimaryText("Scan again")
                .setSecondaryText(R.string.ScannedImage3)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state==MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                        {
                            startMainPageTutorial4();

                        }
                    }
                })
                .show();


    }

    public void startMainPageTutorial4() {

        new MaterialTapTargetPrompt.Builder(ScannedImagePage.this)
                .setTarget(buttonDone)
                .setCaptureTouchEventOnFocal(true)
                .setBackButtonDismissEnabled(true)
                .setBackgroundColour(Color.parseColor("#FA8A00"))
                .setPromptFocal(new RectanglePromptFocal())
                .setPrimaryText("Go to Main page")
                .setSecondaryText(R.string.ScannedImage4)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state==MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                        {


                        }
                    }
                })
                .show();


    }


}