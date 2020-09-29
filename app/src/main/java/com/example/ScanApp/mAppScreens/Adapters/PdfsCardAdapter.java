package com.example.ScanApp.mAppScreens.Adapters;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;
import com.example.ScanApp.mAppScreens.ScannedImagePage;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;

public class PdfsCardAdapter extends RecyclerView.Adapter<PdfsCardAdapter.CardTasarimTutucu>{
    private Context context;
    private List<PdfDocumentsModel> pdfDocumentsList;

    private ConstraintLayout whenCheckedLayout;
    private TextView folderSelected;
    private ImageView close;
    private static final String ETIKET = "YAZI";

    public PdfsCardAdapter(Context context
            , List<PdfDocumentsModel> pdfDocumentsList
            ,ConstraintLayout whenCheckedLayout
            ,TextView folderSelected,ImageView close) {
        this.context = context;
        this.pdfDocumentsList = pdfDocumentsList;
        this.whenCheckedLayout=whenCheckedLayout;
        this.folderSelected=folderSelected;
        this.close=close;
    }
    public class CardTasarimTutucu extends RecyclerView.ViewHolder {

        private ImageView pdfImage;
        private TextView textViewPdfTitle,textViewPdfInfo;
        private CheckBox checkBoxPdf;
        private ConstraintLayout movableLayout,cardMainLayout;

        public CardTasarimTutucu(@NonNull View itemView) {
            super(itemView);

            pdfImage = itemView.findViewById(R.id.pdfImage);
            textViewPdfTitle = itemView.findViewById(R.id.textViewPdfTitle);
            textViewPdfInfo = itemView.findViewById(R.id.textViewPdfInfo);
            checkBoxPdf = itemView.findViewById(R.id.checkBoxPdf);
            movableLayout=itemView.findViewById(R.id.movableLayout);
            cardMainLayout=itemView.findViewById(R.id.cardMainLayout);
        }
    }


    @NonNull
    @Override
    public CardTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_main_page_pdf_element,parent,false);
        Log.e("giriyormu", "giriyor");
        return new CardTasarimTutucu(v);
    }



    @Override
    public void onBindViewHolder(@NonNull CardTasarimTutucu holder, int position) {
        Log.e("OKABASILDI","DENEME");
        PdfDocumentsModel pdf = pdfDocumentsList.get(position);
        Glide.with(context).load(pdf.getBitmap()).centerCrop().into(holder.pdfImage);
        //holder.pdfImage.setImageBitmap(pdf.getBitmap());
        holder.textViewPdfInfo.setText(pdf.getPdfInfo());
        holder.textViewPdfTitle.setText(pdf.getPdfTitle());
        Log.e("cagrildimi","cagrildi");
        holder.checkBoxPdf.setChecked(pdf.getChecked());
        holder.checkBoxPdf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    StaticVeriables.checkedPdfList.add(pdf);
                    whenCheckedLayout.setVisibility(View.VISIBLE);
                    folderSelected.setText(String.valueOf(StaticVeriables.checkedPdfList.size()+" folder selected"));
                    pdf.setChecked(true);
                }
                else{
                    pdf.setChecked(false);
                    StaticVeriables.checkedPdfList.remove(pdf);
                    if(StaticVeriables.checkedPdfList.size()==0){
                        whenCheckedLayout.setVisibility(View.GONE);
                    }
                    else{
                        folderSelected.setText(String.valueOf(StaticVeriables.checkedPdfList.size()+" folder selected"));
                    }

                }
            }
        });
        holder.movableLayout.setTag(ETIKET);
        holder.movableLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onLongClick(View v) {

                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

                v.startDrag(null,shadowBuilder,v,0);

                StaticVeriables.movingPdfModel=pdf;

                //v.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        holder.pdfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IMAGE E TIKLANIYOR MU","EVET");
                openPdf(pdf.getFilePath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfDocumentsList.size();
    }


    private void openPdf(String filePath){
        File pdfFile = new File(filePath);
        if(pdfFile.exists())
        {
            Uri path = Uri.fromFile(pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try
            {
                context.startActivity(pdfIntent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void removeItem(PdfDocumentsModel pdfDocumentsModel){
        pdfDocumentsList.remove(pdfDocumentsModel);
        notifyDataSetChanged();
    }

}
