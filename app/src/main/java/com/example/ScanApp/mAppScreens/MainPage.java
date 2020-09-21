package com.example.ScanApp.mAppScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Adapters.FoldersAdapter;
import com.example.ScanApp.mAppScreens.Adapters.PdfsCardAdapter;
import com.example.ScanApp.mAppScreens.Models.Folder;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.OpenCvClasses.DocumentScannerActivity;
import com.example.ScanApp.mAppScreens.mUtils.mUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainPage extends AppCompatActivity implements View.OnClickListener {

    //Visual Objects
    ImageView addFolder,scanImage,imageViewClose;
    ConstraintLayout whenCheckedLayout;
    Button buttonScanDocument,buttonScanCard;

    private RecyclerView recyclerView;
    private Set<PdfDocumentsModel> pdfDocumentsModelArrayList;
    private ArrayList<Folder> folderList;
    private FoldersAdapter folderAdapter;
    private TextView folderSelected;
    //Veriables
    Intent intent;
    Bitmap temp;
    File root;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    temp = BitmapFactory.decodeResource(getResources(),R.drawable.sand_watch);
                    defs();
                    clicks();
                    scanButtonsListener();
                    getPdfFolderInfos();
                    //exampleForMainPage();
                } else {
                    Toast.makeText(this, "You have to give permissions", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

    }

    private void defs(){

        ActivityCompat.requestPermissions(MainPage.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);


        root = new File(Environment.getExternalStorageDirectory(),"PDF folders");
        if(!root.exists()){
            root.mkdir();
        }


        folderSelected=findViewById(R.id.folderSelected);
        addFolder = findViewById(R.id.addFolder);
        scanImage = findViewById(R.id.scanImage);
        addFolder.setImageResource(R.drawable.folder);
        scanImage.setImageResource(R.drawable.scan);

        intent=new Intent(MainPage.this, DocumentScannerActivity.class);

        recyclerView=findViewById(R.id.pdfRv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        folderList = new ArrayList<>();

        whenCheckedLayout=findViewById(R.id.whenCheckedLayout);
        imageViewClose=findViewById(R.id.imageViewClose);
        StaticVeriables.userWillScanCard=false;
        buttonScanDocument = findViewById(R.id.buttonScanDocument);
        buttonScanCard = findViewById(R.id.buttonScanCard);


    }

    private void clicks(){
        imageViewClose.setOnClickListener(this);
        addFolder.setOnClickListener(this);
        scanImage.setOnClickListener(this);
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

        PdfDocumentsModel pdfDocumentsModel1 = new PdfDocumentsModel(temp,"asdfscanner-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        PdfDocumentsModel pdfDocumentsModel2 = new PdfDocumentsModel(temp,"sscanner-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        PdfDocumentsModel pdfDocumentsModel3 = new PdfDocumentsModel(temp,"scdfanner-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        PdfDocumentsModel pdfDocumentsModel4 = new PdfDocumentsModel(temp,"scannger-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        PdfDocumentsModel pdfDocumentsModel5 = new PdfDocumentsModel(temp,"scanner-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        PdfDocumentsModel pdfDocumentsModel6 = new PdfDocumentsModel(temp,"scannerasdf-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");

        pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel2);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel3);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel4);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel5);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel6);


        Folder folder = new Folder("İŞ DOSYALARIM","ASDF", new ArrayList<>(pdfDocumentsModelArrayList));
        List<Folder> folderList = new ArrayList<>();
        folderList.add(folder);
        folderList.add(folder);
        folderList.add(folder);
        folderList.add(folder);


        folderAdapter= new FoldersAdapter(whenCheckedLayout,this,folderList,folderSelected,imageViewClose);
        recyclerView.setAdapter(folderAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewClose:
                whenCheckedLayout.setVisibility(View.GONE);
                StaticVeriables.scannedImageModelList.clear();
                break;
            case R.id.addFolder:
                addFolder();
                break;
            case R.id.scanImage:

                break;
        }
    }


    private void getPdfFolderInfos(){
        //Log.d("Files", "Path: " + StaticVeriables.path);
        File pdfFile;
        Date date;
        File directory = new File(StaticVeriables.path);
        File[] folders = directory.listFiles();
       // Log.d("Files", "Size: "+ files.length);

        if (folders != null && folders.length > 1) {//Oluşturulduğu tarih sırasına göre file listesini sıralıyorum
            Arrays.sort(folders, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return (int) ((object1.lastModified() > object2.lastModified()) ? object1.lastModified(): object2.lastModified());
                }
            });
        }


        for (File file : folders) {
            File[] pdfFileInFolder = file.listFiles();

            if (pdfFileInFolder != null && pdfFileInFolder.length > 1) {//Oluşturulduğu tarih sırasına göre file listesini sıralıyorum
                Arrays.sort(pdfFileInFolder, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                        return (int) ((object1.lastModified() > object2.lastModified()) ? object1.lastModified(): object2.lastModified());
                    }
                });
            }


            pdfDocumentsModelArrayList = new HashSet<>();
            for (File value : pdfFileInFolder) {
                pdfFile = new File(StaticVeriables.path + "/" + file.getName() + "/" + value.getName());
                date = new Date(pdfFile.lastModified());
                PdfDocumentsModel pdfFolderInfos = new PdfDocumentsModel(temp
                        , value.getName()
                        , String.valueOf(date)
                        , false, pdfFile.getName(), pdfFile.getPath());

                @SuppressLint("StaticFieldLeak")
                PdfAsyncTask a = new PdfAsyncTask(pdfFolderInfos) {
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
            Folder folder = new Folder(file.getName(), String.valueOf(Uri.fromFile(file)), new ArrayList<>(pdfDocumentsModelArrayList));
            folderList.add(folder);


        }
        if (recyclerView.getAdapter() == null) {
            setAdapter(folderList);
        } else{
            folderAdapter.setFolderList(folderList);
            folderAdapter.notifyDataSetChanged();
        }
    }

    private void setAdapter(List<Folder> folderList){
        folderAdapter = new FoldersAdapter(whenCheckedLayout,this,folderList,folderSelected,imageViewClose);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(folderAdapter);
    }


    private void addFolder(){
        alertOfAddFolder(this);
    }

    public void alertOfAddFolder(Context context)
    {

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View tasarim = layoutInflater.inflate(R.layout.alert_design,null);

        final EditText editTextFolderName = tasarim.findViewById(R.id.editTextFolderName);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Create Folder");
        alert.setView(tasarim);
        alert.setMessage("\n"+"Write your folder name\n");
        alert.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String folderName = editTextFolderName.getText().toString().trim();
                if (folderName.length()!=0){
                    File newFile;
                    newFile = new File(StaticVeriables.path+"/"+folderName);
                    if(!newFile.exists()){
                        newFile.mkdir();
                        Folder newFolder = new Folder(folderName,newFile.getPath(),new ArrayList<>());
                       // folderList.add(newFolder);
                        Toast.makeText(context, "Folder Created", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(context, String.valueOf(folderList.size()), Toast.LENGTH_SHORT).show();
                       // setAdapter(new ArrayList<>(folderList));
                       folderList.add(newFolder);
                       folderAdapter.setFolderList(folderList);
                       folderAdapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(context, "Folder is already exist", Toast.LENGTH_SHORT).show();

                    }
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


    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
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