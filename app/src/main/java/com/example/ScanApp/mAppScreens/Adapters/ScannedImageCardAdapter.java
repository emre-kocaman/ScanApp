package com.example.ScanApp.mAppScreens.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;
import com.example.ScanApp.mAppScreens.ScannedImagePage;

import java.util.ArrayList;
import java.util.List;

public class ScannedImageCardAdapter extends RecyclerView.Adapter<ScannedCardHolder> {


    List<ScannedImageModel> list = new ArrayList<>();

   public View focusItem;

    public ScannedImageCardAdapter(List<ScannedImageModel> list) {
        this.list = list;
    }



    @NonNull
    @Override
    public ScannedCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)  {
        View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.card_scanned_image_element,parent,false);
        return new ScannedCardHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ScannedCardHolder holder, int position) {
        holder.bind(list.get(position));
        if (position == 0){
            focusItem = holder.checkBox;
        }

    }

      @Override
      public int getItemViewType(int position) {
          return position;
      }

      @Override
      public int getItemCount() {
          return list.size();
      }

  }
class ScannedCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView imageView;
    public CheckBox checkBox;

    public ScannedCardHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.scannedImage);
        checkBox = itemView.findViewById(R.id.checkboxScannedImage);
        checkBox.setOnClickListener(this);

    }


    public void bind(ScannedImageModel model){
        imageView.setImageBitmap(model.getBitmap());
        checkBox.setChecked(model.getChecked());
    }

    @Override
    public void onClick(View v) {
        ((ScannedImagePage) v.getContext()).MakeSelection(v,getAdapterPosition());
    }
}
