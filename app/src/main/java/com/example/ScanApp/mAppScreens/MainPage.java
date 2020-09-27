package com.example.ScanApp.mAppScreens;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.example.ScanApp.BuildConfig;
import com.example.ScanApp.OpenCvClasses.ImageProcessor;
import com.example.ScanApp.OpenCvClasses.helpers.DocumentMessage;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Adapters.FoldersAdapter;
import com.example.ScanApp.mAppScreens.Adapters.PdfsCardAdapter;
import com.example.ScanApp.mAppScreens.Models.Folder;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.OpenCvClasses.DocumentScannerActivity;
import com.example.ScanApp.mAppScreens.mUtils.mUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import stream.customalert.CustomAlertDialogue;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

import static android.provider.MediaStore.Video.VideoColumns.CATEGORY;

public class MainPage extends AppCompatActivity implements View.OnClickListener {

    //Visual Objects
    ImageView addFolder,scanImage,imageViewClose;
    ConstraintLayout whenCheckedLayout;
    Button buttonScanDocument,buttonScanCard;
    FloatingActionButton fabDelete,fabEdit,fabShare;
    private RecyclerView recyclerView;
    private Set<PdfDocumentsModel> pdfDocumentsModelArrayList;
    public ArrayList<Folder> folderList;
    private FoldersAdapter folderAdapter;
    private TextView folderSelected;


    //Veriables
    Intent intent;
    Bitmap temp;
    File root,defPdfFolder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


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
                    startMainPageTutorial1();
                    //exampleForMainPage()
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

    public void defs(){
        StaticVeriables.checkedPdfList=new ArrayList<>();

        /*ActivityCompat.requestPermissions(MainPage.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);*/


        root = new File(Environment.getExternalStorageDirectory(),"PDF folders");
        if(!root.exists()){
            root.mkdir();
        }

        defPdfFolder = new File(root,"Default Pdf Folder");
        if (!defPdfFolder.exists()){
            Log.e("GIRDIMI","EVET");
            defPdfFolder.mkdir();
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

        fabDelete=findViewById(R.id.fabDelete);
        fabEdit=findViewById(R.id.fabEdit);
        fabShare=findViewById(R.id.fabShare);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

    }

    private void clicks(){
        imageViewClose.setOnClickListener(this);
        addFolder.setOnClickListener(this);
        scanImage.setOnClickListener(this);
        fabEdit.setOnClickListener(this);
        fabShare.setOnClickListener(this);
        fabDelete.setOnClickListener(this);
}

    private void scanButtonsListener(){

        buttonScanDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> other = new ArrayList<>();
                other.add("Cancel");

                ArrayList<String> des = new ArrayList<>();
                des.add("Select image from gallery");
                des.add("Take picture from camera");


                CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(MainPage.this)
                        .setStyle(CustomAlertDialogue.Style.SELECTOR)
                        .setOthers(other)
                        .setDestructive(des)
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                switch (i){
                                    case 0:
                                        StaticVeriables.scannedImageModelList=new ArrayList<>();
                                        StaticVeriables.userWillScanCard=false;
                                        StaticVeriables.willScanFromGallery=true;
                                        StaticVeriables.photoCount=1;
                                        StaticVeriables.informationText="SCAN FROM GALLERY.";
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        StaticVeriables.scannedImageModelList=new ArrayList<>();
                                        StaticVeriables.userWillScanCard=false;
                                        StaticVeriables.willScanFromGallery=false;
                                        StaticVeriables.photoCount=1;
                                        StaticVeriables.informationText="SCAN YOUR DOCUMENT.";
                                        startActivity(intent);
                                        break;
                                    case 2:
                                        CustomAlertDialogue.getInstance().dismiss();
                                        break;
                                }
                            }
                        })
                        .setDecorView(getWindow().getDecorView())
                        .build();
                alert.show();
                Vibrator vibe = (Vibrator) MainPage.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(30);


            }
        });

        buttonScanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticVeriables.scannedImageModelList=new ArrayList<>();
                StaticVeriables.userWillScanCard=true;
                StaticVeriables.willScanFromGallery=false;
                StaticVeriables.photoCount=2;
                StaticVeriables.informationText="SCAN THE FRONT OF YOUR CARD";
                startActivity(intent);

            }
        });

    }

    private void exampleForMainPage(){

        //PdfDocumentsModel pdfDocumentsModel1 = new PdfDocumentsModel(temp,"asdfscanner-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        //PdfDocumentsModel pdfDocumentsModel2 = new PdfDocumentsModel(temp,"sscanner-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        //PdfDocumentsModel pdfDocumentsModel3 = new PdfDocumentsModel(temp,"scdfanner-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        //PdfDocumentsModel pdfDocumentsModel4 = new PdfDocumentsModel(temp,"scannger-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        //PdfDocumentsModel pdfDocumentsModel5 = new PdfDocumentsModel(temp,"scanner-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");
        //PdfDocumentsModel pdfDocumentsModel6 = new PdfDocumentsModel(temp,"scannerasdf-app-wireframe","1.9 MB - 13 Eylül, 16:14",false,"","");

        /*pdfDocumentsModelArrayList.add(pdfDocumentsModel1);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel2);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel3);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel4);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel5);
        pdfDocumentsModelArrayList.add(pdfDocumentsModel6);*/


        /*Folder folder = new Folder("İŞ DOSYALARIM","ASDF", new ArrayList<>(pdfDocumentsModelArrayList));
        List<Folder> folderList = new ArrayList<>();
        folderList.add(folder);
        folderList.add(folder);
        folderList.add(folder);
        folderList.add(folder);


        folderAdapter= new FoldersAdapter(whenCheckedLayout,this,folderList,folderSelected,imageViewClose);
        recyclerView.setAdapter(folderAdapter);*/

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
            case R.id.fabDelete:
                deleteSelectedItems();
                break;
            case R.id.fabEdit:
                editPdf();
                break;
            case R.id.fabShare:
                sharePdf(uriList());
                break;
            case R.id.scanImage:
                folderAdapter.notifyDataSetChanged();
                break;
        }
    }



    private void sharePdf(ArrayList<Uri> paths) {

        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, paths);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("application/pdf");
            startActivity(Intent.createChooser(intent, "Share Your Pdf"));
        } catch (Exception e) {
            Log.e("HATA",e.toString());
        }

    }

    private ArrayList<Uri> uriList(){
        ArrayList<Uri> pathsList=new ArrayList<>();
        for (int i = 0 ; i<StaticVeriables.checkedPdfList.size();i++){
          pathsList.add(StaticVeriables.checkedPdfList.get(i).getUri());
        }
        return pathsList;
    }

    public void getPdfFolderInfos(){
        //Log.d("Files", "Path: " + StaticVeriables.path);
        File pdfFile;
        Date date;
        File directory = new File(StaticVeriables.path);
        Log.e("FOLDERNAMESDF",directory.getName());
        File[] folders = directory.listFiles();
        Log.e("FOLDERNAMESDF",String.valueOf(folders.length));

        // Log.d("Files", "Size: "+ files.length);

        if (folders != null && folders.length > 1) {//En son düzenlendiği tarihe göre sıralanıyor
            Arrays.sort(folders, new Comparator() {
                public int compare(Object o1, Object o2) {

                    if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                        return -1;
                    } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }

            });
        }

        for (File file : folders) {
            File[] pdfFileInFolder = file.listFiles();
            if (pdfFileInFolder != null && pdfFileInFolder.length > 1) {//Oluşturulduğu tarih sırasına göre file listesini sıralıyorum
                Arrays.sort(pdfFileInFolder, new Comparator() {
                    public int compare(Object o1, Object o2) {

                        if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                            return -1;
                        } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
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
                        , false, pdfFile.getName(), pdfFile.getPath(),Uri.fromFile(pdfFile));

                @SuppressLint("StaticFieldLeak")
                PdfAsyncTask a = new PdfAsyncTask(pdfFolderInfos) {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (recyclerView.getAdapter() != null){
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }
                };
                a.execute(pdfFile);

                pdfDocumentsModelArrayList.add(pdfFolderInfos);
            }
            Folder folder = new Folder(file.getName(), String.valueOf(Uri.fromFile(file)), new ArrayList<>(pdfDocumentsModelArrayList));
            Log.e("FOLDERNAME",folder.getFolderName());
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


    private void editPdf() {
        if (StaticVeriables.checkedPdfList.size()==1){
            alertDialogEdit(StaticVeriables.checkedPdfList.get(0).getFilePath(),StaticVeriables.checkedPdfList.get(0).getName());
        }
        else{
            Toast.makeText(this, "Please select only 1 file to edit", Toast.LENGTH_SHORT).show();
        }

    }


    private void alertDialogEdit(String path,String filename) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View tasarim = layoutInflater.inflate(R.layout.alert_design,null);

        final EditText editName = tasarim.findViewById(R.id.editTextFolderName);
        editName.setHint("Edit file name");
        //editName.setText(fileName);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Edit File Name");
        alert.setView(tasarim);
        alert.setMessage("\n"+"Edit your file name\n");
        alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            editFileName(editName.getText().toString().trim(),path,filename);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.create().show();
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

    private void editFileName(String newFileName,String path,String fileName) {
        File fileItself = new File(path);
        File dir = new File(Objects.requireNonNull(fileItself.getParent()));
        File from = new File(dir,fileName);
        File to = new File(dir,newFileName+".pdf");
        boolean deleted = from.renameTo(to);
        Log.e("DEGISTIMI",String.valueOf(deleted));
        if (deleted){
            Toast.makeText(this, "Başarılı", Toast.LENGTH_SHORT).show();
            StaticVeriables.checkedPdfList.clear();
            Log.e("DIZIUZUNLUGU",String.valueOf(StaticVeriables.checkedPdfList.size()));
            folderList=new ArrayList<>();
            getPdfFolderInfos();
            whenCheckedLayout.setVisibility(View.GONE);
        }
        else{
            Toast.makeText(this, "İşlem başarısız", Toast.LENGTH_SHORT).show();
        }
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

    public void startMainPageTutorial1() {

//
        if (sharedPreferences.getBoolean("isFirstTimeMainPage",true)){
            new MaterialTapTargetPrompt.Builder(MainPage.this)
                    .setTarget(buttonScanCard)
                    .setCaptureTouchEventOnFocal(true)
                    .setBackButtonDismissEnabled(true)
                    .setBackgroundColour(Color.parseColor("#FA8A00"))
                    .setPromptFocal(new RectanglePromptFocal())
                    .setPrimaryText("Card Scanning")
                    .setSecondaryText(R.string.mainPageInfo1)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                    {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                        {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state==MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                            {
                                editor=sharedPreferences.edit().putBoolean("isFirstTimeMainPage",false);
                                editor.apply();
                                startMainPageTutorial2();
                            }
                        }
                    })
                    .show();
        }

    }
    private void startMainPageTutorial2() {
        new MaterialTapTargetPrompt.Builder(this)
                .setTarget(buttonScanDocument)
                .setBackgroundColour(Color.parseColor("#FA8A00"))
                .setCaptureTouchEventOnFocal(true)
                .setBackButtonDismissEnabled(true)
                .setPromptFocal(new RectanglePromptFocal())
                .setPrimaryText("Document Scanning")
                .setSecondaryText(R.string.mainPageInfo2)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                        {
                            startMainPageTutorial3();
                        }
                    }
                })
                .show();

    }
    private void startMainPageTutorial3() {
        new MaterialTapTargetPrompt.Builder(this)
                .setTarget(addFolder)
                .setBackgroundColour(Color.parseColor("#FA8A00"))
                .setCaptureTouchEventOnFocal(true)
                .setBackButtonDismissEnabled(true)
                .setPromptFocal(new RectanglePromptFocal())
                .setPrimaryText("Add Folder")
                .setSecondaryText(R.string.mainPageInfo3)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {

                    }
                })
                .show();



    }


    private void deleteSelectedItems(){

        for (int i = 0; i <StaticVeriables.checkedPdfList.size() ; i++) {
            new File(StaticVeriables.checkedPdfList.get(i).getFilePath()).delete();
        }
        StaticVeriables.checkedPdfList.clear();
        Log.e("DIZIUZUNLUGU",String.valueOf(StaticVeriables.checkedPdfList.size()));
        folderList=new ArrayList<>();
        getPdfFolderInfos();
        whenCheckedLayout.setVisibility(View.GONE);
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