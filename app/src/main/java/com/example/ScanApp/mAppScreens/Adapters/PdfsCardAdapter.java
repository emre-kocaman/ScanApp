package com.example.ScanApp.mAppScreens.Adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;
import com.example.ScanApp.mAppScreens.ScannedImagePage;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;

import org.w3c.dom.Text;

import java.util.List;

public class PdfsCardAdapter extends RecyclerView.Adapter<PdfsCardAdapter.CardTasarimTutucu> {
    private Context context;
    private List<PdfDocumentsModel> pdfDocumentsList;

    private List<PdfDocumentsModel> checkedPdfList;
    private ConstraintLayout whenCheckedLayout;
    private TextView folderSelected;

    public PdfsCardAdapter(Context context
            , List<PdfDocumentsModel> pdfDocumentsList
            ,List<PdfDocumentsModel> checkedPdfList
            ,ConstraintLayout whenCheckedLayout
            ,TextView folderSelected) {
        this.context = context;
        this.pdfDocumentsList = pdfDocumentsList;
        this.checkedPdfList=checkedPdfList;
        this.whenCheckedLayout=whenCheckedLayout;
        this.folderSelected=folderSelected;
    }

    public class CardTasarimTutucu extends RecyclerView.ViewHolder {

        private ImageView pdfImage;
        private TextView textViewPdfTitle,textViewPdfInfo;
        private CheckBox checkBoxPdf;

        public CardTasarimTutucu(@NonNull View itemView) {
            super(itemView);

            pdfImage = itemView.findViewById(R.id.pdfImage);
            textViewPdfTitle = itemView.findViewById(R.id.textViewPdfTitle);
            textViewPdfInfo = itemView.findViewById(R.id.textViewPdfInfo);
            checkBoxPdf = itemView.findViewById(R.id.checkBoxPdf);
        }
    }


    @NonNull
    @Override
    public CardTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_main_page_pdf_element,parent,false);
        return new CardTasarimTutucu(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardTasarimTutucu holder, int position) {
        PdfDocumentsModel pdf = pdfDocumentsList.get(position);
        holder.pdfImage.setImageBitmap(pdf.getBitmap());
        holder.textViewPdfInfo.setText(pdf.getPdfInfo());
        holder.textViewPdfTitle.setText(pdf.getPdfTitle());


        holder.checkBoxPdf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    checkedPdfList.add(pdf);
                    whenCheckedLayout.setVisibility(View.VISIBLE);
                    folderSelected.setText(String.valueOf(checkedPdfList.size()+" folder selected"));
                    Log.e("PDFSIZE",String.valueOf(StaticVeriables.pdfList.size()));
                }
                else{
                    checkedPdfList.remove(pdf);
                    if(checkedPdfList.size()==0){
                        whenCheckedLayout.setVisibility(View.GONE);
                    }
                    else{
                        folderSelected.setText(String.valueOf(checkedPdfList.size()+" folder selected"));
                    }

                    Log.e("PDFSIZE",String.valueOf(StaticVeriables.pdfList.size()));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfDocumentsList.size();
    }
}
