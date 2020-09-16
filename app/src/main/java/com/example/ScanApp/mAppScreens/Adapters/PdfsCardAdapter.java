package com.example.ScanApp.mAppScreens.Adapters;

import android.content.Context;
import android.media.Image;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;

import java.util.List;

public class PdfsCardAdapter extends RecyclerView.Adapter<PdfsCardAdapter.CardTasarimTutucu> {
    private Context context;
    private List<PdfDocumentsModel> pdfDocumentsList;

    public PdfsCardAdapter(Context context, List<PdfDocumentsModel> pdfDocumentsList) {
        this.context = context;
        this.pdfDocumentsList = pdfDocumentsList;
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
    }

    @Override
    public int getItemCount() {
        return pdfDocumentsList.size();
    }
}
