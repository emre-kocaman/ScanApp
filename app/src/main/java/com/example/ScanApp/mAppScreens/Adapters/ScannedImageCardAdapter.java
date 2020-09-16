package com.example.ScanApp.mAppScreens.Adapters;

import android.content.Context;
import android.util.Log;
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
import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;

import java.util.List;

public class ScannedImageCardAdapter extends RecyclerView.Adapter<ScannedImageCardAdapter.CardTasarimTutucu> {

    private Context context;
    private List<ScannedImageModel> scannedImageModelList;

    private List<ScannedImageModel> selectedScannedImages;
    private ConstraintLayout whenCheckedLayout;
    private TextView folderSelected;


    public ScannedImageCardAdapter(Context context
            , List<ScannedImageModel> scannedImageModelList
            ,List<ScannedImageModel> selectedScannedImages
            ,ConstraintLayout whenCheckedLayout
            ,TextView folderSelected) {

        this.context = context;
        this.scannedImageModelList = scannedImageModelList;
        this.selectedScannedImages = selectedScannedImages;
        this.whenCheckedLayout = whenCheckedLayout;
        this.folderSelected = folderSelected;

    }



    public class CardTasarimTutucu extends RecyclerView.ViewHolder {

        private ImageView scannedImage;
        private CheckBox checkboxScannedImage;
        public CardTasarimTutucu(@NonNull View itemView) {
            super(itemView);

            scannedImage=itemView.findViewById(R.id.scannedImage);
            checkboxScannedImage=itemView.findViewById(R.id.checkboxScannedImage);
        }
    }



    @NonNull
    @Override
    public CardTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.card_scanned_image_element,parent,false);

        return new CardTasarimTutucu(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardTasarimTutucu holder, int position) {
        ScannedImageModel scannedImageModel = scannedImageModelList.get(position);
        holder.scannedImage.setImageBitmap(scannedImageModel.getBitmap());



        holder.checkboxScannedImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    selectedScannedImages.add(scannedImageModel);
                    whenCheckedLayout.setVisibility(View.VISIBLE);
                    folderSelected.setText(String.valueOf(selectedScannedImages.size()+" folder selected"));
                    Log.e("PDFSIZE",String.valueOf(StaticVeriables.pdfList.size()));
                }
                else{
                    selectedScannedImages.remove(scannedImageModel);
                    if(selectedScannedImages.size()==0){
                        whenCheckedLayout.setVisibility(View.GONE);
                    }
                    else{
                        folderSelected.setText(String.valueOf(selectedScannedImages.size()+" folder selected"));
                    }

                    Log.e("PDFSIZE",String.valueOf(StaticVeriables.pdfList.size()));

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return scannedImageModelList.size();
    }
}
