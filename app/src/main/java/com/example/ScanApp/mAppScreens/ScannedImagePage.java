package com.example.ScanApp.mAppScreens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ScanApp.OpenCvClasses.DocumentScannerActivity;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Adapters.ScannedImageCardAdapter;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;
import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;
import com.example.ScanApp.mAppScreens.PhotoEditting.EditImage;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.mAppScreens.mUtils.mUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class ScannedImagePage extends AppCompatActivity implements View.OnClickListener {

    //Veriables
    Intent intent;

    ScannedImageCardAdapter scannedImageCardAdapter;
    ArrayList<ScannedImageModel> scannedImageModelArrayList;
    ArrayList<ScannedImageModel> selectedScannedImageList;
    File root;
    Boolean isPdfCreated=false;
    Boolean isPdfCreating=false;
    private static final int SELECT_PHOTO=100;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Bitmap bmp;
    //Visual Objects
    FloatingActionButton floatingActionButtonDelete,floatingActionButtonCombine;
    RecyclerView recyclerViewScannedImages;
    Button buttonDone;
    FloatingActionButton buttonScanAgain,buttonScanAgainFromGallery;
    ConstraintLayout whenCheckedSp;
    TextView textView;
    ImageView imageViewCloseSp;
    ProgressBar progressBarPdfCreating;

    private InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_image_page);
        def();
        clicks();
        getScannedImagesFromList();
        addListener();
        //exampleForScannedImagePage();
    }


    private void applyThreshold(Mat src) {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);

        // Some other approaches
//        Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 15);
//        Imgproc.threshold(src, src, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        Imgproc.GaussianBlur(src, src, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        org.opencv.android.Utils.matToBitmap(src, bmp);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("GIRDI","RESULT");
        Log.e("GIRDICOD",String.valueOf(requestCode) + " "+String.valueOf(resultCode));
        if (requestCode==SELECT_PHOTO && resultCode==RESULT_OK){

            Uri selectImage = data.getData();
            try {
                bmp = mUtils.getBitmapFromUri(selectImage,bmp,this);
                Mat madt = new Mat(200,300, CvType.CV_8U);
                Utils.bitmapToMat(bmp,madt);
                applyThreshold(madt);
                StaticVeriables.getScannedFromGallery=bmp.copy(bmp.getConfig(),true);
                bmp.recycle();
                madt.release();
                Intent gallery1 = new Intent(ScannedImagePage.this, EditImage.class);
                gallery1.putExtra("isGallery",true);
                startActivity(gallery1);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void def(){

        MobileAds.initialize(ScannedImagePage.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        //ca-app-pub-5087773943034547/3797409122 Ana Id
        //ca-app-pub-3940256099942544/1033173712 test Id
        interstitialAd= new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        intent = new Intent(ScannedImagePage.this, MainActivity.class);
        recyclerViewScannedImages=findViewById(R.id.recyclerViewScannedImages);
        //recyclerViewScannedImages.setLayoutManager(new LinearLayoutManager(this));
        scannedImageModelArrayList=new ArrayList<>();
        bmp= BitmapFactory.decodeResource(getResources(),R.drawable.tmp);
        buttonDone=findViewById(R.id.buttonDone);
        buttonScanAgain=findViewById(R.id.buttonScanAgain);
        buttonScanAgainFromGallery=findViewById(R.id.buttonScanAgainFromGallery);

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

        progressBarPdfCreating=findViewById(R.id.progressBarPdfCreating);
        progressBarPdfCreating.setVisibility(View.GONE);





    }

    private void clicks(){
        buttonDone.setOnClickListener(this);
        buttonScanAgain.setOnClickListener(this);
        buttonScanAgainFromGallery.setOnClickListener(this);
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
        Log.e("HANGIBOOL",isPdfCreated.toString());
        if (isPdfCreating){
            Toast.makeText(this, "Pdf oluşturuluyor lütfen bekleyiniz.", Toast.LENGTH_SHORT).show();
        }
        else{
            if (isPdfCreated){//Eğer kullanıcı zaten pdf combine ettiyse done buttonuna basınca bir daha combine etmesine gerek kalmadan direk ana sayfaya git
                //Seçilen resimleri pdf olarak sırasıyla kayıt edeceğimiz nokta burası.
                //ca-app-pub-5087773943034547/3797409122 Ana Id
                //ca-app-pub-3940256099942544/1033173712 test Id
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
                else{
                    Log.e("REKLAM","REKLAM HAZIR DEGİL.");
                    StaticVeriables.scannedImageModelList= new ArrayList<>();
                    startActivity(intent);
                    finish();
                }

            }
            else{
                showWhenPressDone(this);

            }
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
            case R.id.buttonScanAgainFromGallery:
                StaticVeriables.userWillScanCard=false;
                StaticVeriables.willScanFromGallery=true;
                StaticVeriables.photoCount=1;
                StaticVeriables.informationText="SCAN FROM GALLERY.";

                Intent pickFromGallery= new Intent(Intent.ACTION_PICK);
                pickFromGallery.setType("image/*");
                startActivityForResult(pickFromGallery,SELECT_PHOTO);
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


                    new MyTask(ScannedImagePage.this).execute(fileName,"done");
                    whenCheckedSp.setVisibility(View.GONE);


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
                        isPdfCreating=true;
                        isPdfCreated=true;
                    new MyTask(ScannedImagePage.this).execute(fileName,"combine");
                        whenCheckedSp.setVisibility(View.GONE);
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

    public static class MyTask extends AsyncTask<String, Void, Boolean> {
        private WeakReference<ScannedImagePage> weakReference;

        MyTask(ScannedImagePage scannedImagePage){
            weakReference = new WeakReference<>(scannedImagePage);
        }


        @Override
        protected void onPreExecute() {
            ScannedImagePage activity = weakReference.get();
            if (activity==null || activity.isFinishing()){
                return;
            }

            activity.progressBarPdfCreating.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... string) {

            ScannedImagePage activity = weakReference.get();
            if (activity==null || activity.isFinishing()){
                return null;
            }

            if (string[1].equals("combine")){
                mUtils.createPdfOfImageFromList(activity.root,activity.selectedScannedImageList,activity,string[0]);
                return true;
            }
            else if(string[1].equals("done")) {
                mUtils.createPdfOfImageFromList(activity.root,StaticVeriables.scannedImageModelList,activity,string[0]);
                return false;
            }
            else{
                return null;
            }



        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            ScannedImagePage activity = weakReference.get();
            if (activity==null || activity.isFinishing()){
                return;
            }
                if (aVoid){
                    activity.progressBarPdfCreating.setVisibility(View.GONE);
                    activity.isPdfCreating=false;
                    Toast.makeText(activity, "Pdf dönüştürme işlemi tamamlandı.Done buttonuna basarak ana sayfaya gidebilirsiniz.", Toast.LENGTH_SHORT).show();
                }
                else{
                    //ca-app-pub-5087773943034547/3797409122 Ana Id
                    //ca-app-pub-3940256099942544/1033173712 test Id
                    activity.progressBarPdfCreating.setVisibility(View.GONE);
                    if (activity.interstitialAd.isLoaded()){
                        activity.interstitialAd.show();
                    }
                    else{
                        StaticVeriables.scannedImageModelList=new ArrayList<>();
                        activity.startActivity(activity.intent);
                        activity.finish();
                    }
                }



        }
    }

    private void addListener(){
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e("REKLAM","onAdClosed");
                StaticVeriables.scannedImageModelList= new ArrayList<>();
                startActivity(intent);
                finish();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("REKLAM","onAdFailedToLoad");

            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.e("REKLAM","onAdLeftApplication");

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.e("REKLAM","onAdOpened");

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e("REKLAM","onAdLoaded");

            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.e("REKLAM","onAdClicked");

            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.e("REKLAM","onAdImpression");

            }
        });
    }

    public void startMainPageTutorial1() {
        //sharedPreferences.getBoolean("isFirstTimeScannedImagePage",true)
        //recyclerViewScannedImages.getAdapter() != null && sharedPreferences.getBoolean("isFirstTimeScannedImagePage",true)
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
                .setPrimaryText("Scan again from camera")
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
                .setTarget(buttonScanAgainFromGallery)
                .setCaptureTouchEventOnFocal(true)
                .setBackButtonDismissEnabled(true)
                .setBackgroundColour(Color.parseColor("#FA8A00"))
                .setPromptFocal(new RectanglePromptFocal())
                .setPrimaryText("Scan image from gallery")
                .setSecondaryText(R.string.ScannedImage5)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state==MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                        {

                            startMainPageTutorial5();
                        }
                    }
                })
                .show();





    }


    public void startMainPageTutorial5() {
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
