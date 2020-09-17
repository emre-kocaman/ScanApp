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
import com.example.ScanApp.mAppScreens.MainPage;
import com.example.ScanApp.mAppScreens.Models.PdfDocumentsModel;
import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;
import com.example.ScanApp.mAppScreens.ScannedImagePage;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ScannedImageCardAdapter extends RecyclerView.Adapter<ScannedImageCardAdapter.CardTasarimTutucu>  {

    List<ScannedImageModel> list = new ArrayList<>();
    ScannedImagePage activity;

    public ScannedImageCardAdapter(List<ScannedImageModel> list, ScannedImagePage activity) {
        this.list = list;
        this.activity = activity;

    }

    public void RemoveItems(ArrayList<ScannedImageModel> selectedScannedImageList) {
        for (int i = 0 ; i<selectedScannedImageList.size();i++){
            list.remove(selectedScannedImageList.get(i));
            notifyDataSetChanged();
        }

    }


    public class CardTasarimTutucu extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        CheckBox checkBox;

        public CardTasarimTutucu(@NonNull View itemView, ScannedImagePage activity) {
            super(itemView);

            imageView=itemView.findViewById(R.id.scannedImage);
            checkBox = itemView.findViewById(R.id.checkboxScannedImage);
            checkBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            activity.MakeSelection(v,getAdapterPosition());
        }
    }



    @NonNull
    @Override
    public CardTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType)  {

        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.card_scanned_image_element,parent,false);

        return new CardTasarimTutucu(v,activity);
    }


    @Override
    public void onBindViewHolder(@NonNull CardTasarimTutucu holder, int position) {
        holder.imageView.setImageBitmap(list.get(position).getBitmap());


    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
