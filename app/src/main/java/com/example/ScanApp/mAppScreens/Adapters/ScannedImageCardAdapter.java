package com.example.ScanApp.mAppScreens.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;

import java.util.List;

public class ScannedImageCardAdapter extends RecyclerView.Adapter<ScannedImageCardAdapter.CardTasarimTutucu> {

    private Context context;
    private List<ScannedImageModel> scannedImageModelList;


    public ScannedImageCardAdapter(Context context, List<ScannedImageModel> scannedImageModelList) {
        this.context = context;
        this.scannedImageModelList = scannedImageModelList;
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


    }

    @Override
    public int getItemCount() {
        return scannedImageModelList.size();
    }
}
