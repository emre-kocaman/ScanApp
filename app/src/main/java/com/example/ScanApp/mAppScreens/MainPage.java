package com.example.ScanApp.mAppScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Adapters.PdfsCardAdapter;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.OpenCvClasses.DocumentScannerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainPage extends AppCompatActivity implements View.OnClickListener {

    //Visual Objects
    ImageView folderImage,scanImage,imageViewClose;
    ConstraintLayout whenCheckedLayout;
    Button buttonScanDocument,buttonScanCard;

    private RecyclerView recyclerView;
    private Set<PdfDocumentsModel> pdfDocumentsModelArrayList;
    private ArrayList<PdfDocumentsModel> checkedPdfList;
    private PdfsCardAdapter pdfsCardAdapter;
    private TextView folderSelected;
    //Veriables
    Intent intent;
    Bitmap temp;
    File root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        temp = BitmapFactory.decodeResource(getResources(),R.drawable.sand_watch);
        defs();
        clicks();
        scanButtonsListener();
        getPdfFolderInfos();


        //exampleForMainPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void defs(){
        folderSelected=findViewById(R.id.folderSelected);
        folderImage = findViewById(R.id.folderImage);
        scanImage = findViewById(R.id.scanImage);
        folderImage.setImageResource(R.drawable.folder);
        scanImage.setImageResource(R.drawable.scan);

        intent=new Intent(MainPage.this, DocumentScannerActivity.class);

        recyclerView=findViewById(R.id.pdfRv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        pdfDocumentsModelArrayList=new HashSet<>();
        checkedPdfList=new ArrayList<>();

        whenCheckedLayout=findViewById(R.id.whenCheckedLayout);
        imageViewClose=findViewById(R.id.imageViewClose);
        StaticVeriables.userWillScanCard=false;
        buttonScanDocument = findViewById(R.id.buttonScanDocument);
        buttonScanCard = findViewById(R.id.buttonScanCard);

        root = new File(Environment.getExternalStorageDirectory(),"PDF folders");
        if(!root.exists()){
            root.mkdir();
        }
    }

    private void clicks(){
        imageViewClose.setOnClickListener(this);
    }

    private void scanButtonsListener(){

        buttonScanDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticVeriables.scannedImageModelList=new ArrayList<>();
                StaticVeriables.userWillScanCard=false;
                StaticVeriables.photoCount=1;
                StaticVeriables.informationText="SCAN YOUR DOCUMENT.";
                startActivity(intent);

            }
        });

        buttonScanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                StaticVeriables.scannedImageModelList=new ArrayList<>();
                StaticVeriables.userWillScanCard=true;
                StaticVeriables.photoCount=2;
                StaticVeriables.informationText="SCAN THE FRONT OF YOUR CARD";
                startActivity(intent);

            }
        });

    }

    private void exampleForMainPage(){
        PdfDocumentsModel pdfDocumentsModel1 = new PdfDocumentsModel(temp,"scanner-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"");

        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);

        pdfsCardAdapter = new PdfsCardAdapter(this
                ,new ArrayList<>(pdfDocumentsModelArrayList)
                ,checkedPdfList
                ,whenCheckedLayout
                ,folderSelected
                ,imageViewClose);
        recyclerView.setAdapter(pdfsCardAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewClose:
                whenCheckedLayout.setVisibility(View.GONE);
                break;
        }
    }


    private void getPdfFolderInfos(){
        Log.d("Files", "Path: " + StaticVeriables.path);
        File pdfFile;
        Date date;
        File directory = new File(StaticVeriables.path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);

        for (int i = 0; i < files.length; i++)
        {
            pdfFile= new File(StaticVeriables.path+"/"+files[i].getName());
            date= new Date(pdfFile.lastModified());
            PdfDocumentsModel pdfFolderInfos = new PdfDocumentsModel(temp
                    ,files[i].getName()
                    ,String.valueOf(date)
                    ,false,pdfFile.getName());
           @SuppressLint("StaticFieldLeak")
           PdfAsyncTask a = new PdfAsyncTask(pdfFolderInfos){
               @Override
               protected void onPostExecute(Void aVoid) {
                   super.onPostExecute(aVoid);
                   if (recyclerView.getAdapter() != null)
                        recyclerView.getAdapter().notifyDataSetChanged();
               }
           };
           a.execute(pdfFile);

           pdfDocumentsModelArrayList.add(pdfFolderInfos);


        }
        setAdapter();
    }

    private void setAdapter(){
        pdfsCardAdapter = new PdfsCardAdapter(this
                ,new ArrayList<>(pdfDocumentsModelArrayList)
                ,checkedPdfList
                ,whenCheckedLayout
                ,folderSelected
                ,imageViewClose);
        recyclerView.setAdapter(pdfsCardAdapter);
    }


}
class PdfAsyncTask extends AsyncTask<File,Void,Void>{

    private PdfDocumentsModel documentsModel;

    public PdfAsyncTask(PdfDocumentsModel documentsModel) {
        this.documentsModel = documentsModel;
    }

    @Override
    protected Void doInBackground(File... files) {
     documentsModel.setBitmap(pdfToBitmap(files[0]));
       return null;
    }

    private  Bitmap pdfToBitmap(File pdfFile) {
        Bitmap bitmapFirstPage = null;
        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));

            PdfRenderer.Page page = renderer.openPage(0);

            int width = 100;
            int height = 100;
            bitmapFirstPage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        //    Log.e("BitmapDeger",bitmapFirstPage.toString());

            page.render(bitmapFirstPage, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // close the page
            page.close();



            // close the renderer
            renderer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmapFirstPage;

    }

}