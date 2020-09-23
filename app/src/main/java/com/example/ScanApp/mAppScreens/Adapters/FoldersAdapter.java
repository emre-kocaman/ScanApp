package com.example.ScanApp.mAppScreens.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ScanApp.R;
import com.example.ScanApp.mAppScreens.Models.Folder;

import java.util.List;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.CardTasarimTutucu> {
    ConstraintLayout whenCheckedLayout;
    Context context;
    List<Folder> folderList;
    TextView folderSelected;
    ImageView imageViewClose;
    public FoldersAdapter(ConstraintLayout whenCheckedLayout, Context context, List<Folder> folderList, TextView folderSelected,ImageView imageViewClose) {
        this.whenCheckedLayout = whenCheckedLayout;
        this.context = context;
        this.folderList = folderList;
        this.folderSelected = folderSelected;
        this.imageViewClose= imageViewClose;
    }

    public void setFolderList(List<Folder> folderList) {
        this.folderList = folderList;
    }

    public class CardTasarimTutucu extends RecyclerView.ViewHolder {
        ImageView imageViewArrow;
        TextView folderName;
        RecyclerView pdfFiles;
        ConstraintLayout expendableLayout,constraintLayout2;
        LinearLayout linearLayout;



        public CardTasarimTutucu(@NonNull View itemView) {
            super(itemView);
            imageViewArrow=itemView.findViewById(R.id.imageViewArrow);
            folderName=itemView.findViewById(R.id.folderName);
            pdfFiles=itemView.findViewById(R.id.pdfFiles);
            expendableLayout=itemView.findViewById(R.id.expendableLayout);
            constraintLayout2=itemView.findViewById(R.id.constraintLayout2);
            linearLayout = itemView.findViewById(R.id.mainLayout);

            imageViewArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Folder folder = folderList.get(getAdapterPosition());
                    folder.setExpanded(!folder.getExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });




        }
    }




    @NonNull
    @Override
    public CardTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.card_main_page_folders,parent,false);
            return new CardTasarimTutucu(v); // CarViewHolder

    }


    @Override
    public void onBindViewHolder(@NonNull CardTasarimTutucu holder, int position) {
        Folder folder = folderList.get(position);

        holder.folderName.setText(folder.getFolderName());

        boolean isExpanded= folderList.get(position).getExpanded();

        holder.imageViewArrow.setImageResource(isExpanded ? R.drawable.ic_baseline_keyboard_arrow_up_24 : R.drawable.ic_baseline_keyboard_arrow_down_24);


        if (isExpanded){
            holder.expendableLayout.setVisibility(View.VISIBLE);
            PdfsCardAdapter pdfAdapter= new PdfsCardAdapter(context,folder.getPdfDocumentsModels(),whenCheckedLayout,folderSelected,imageViewClose);
            holder.pdfFiles.setLayoutManager(new LinearLayoutManager(context));
            holder.pdfFiles.setAdapter(pdfAdapter);
        }
        else{
            holder.expendableLayout.setVisibility(View.GONE);
        }

        if (folder.getFolderName().equals("Default Pdf Folder")){
            Log.e("GIRDIMISADFD","EVET");
            holder.expendableLayout.setVisibility(View.VISIBLE);
            holder.constraintLayout2.setVisibility(View.GONE);
            PdfsCardAdapter pdfAdapter= new PdfsCardAdapter(context,folder.getPdfDocumentsModels(),whenCheckedLayout,folderSelected,imageViewClose);
            holder.pdfFiles.setLayoutManager(new LinearLayoutManager(context));
            holder.pdfFiles.setAdapter(pdfAdapter);
            holder.linearLayout.setPadding(0,0,0,0);
        }



    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

}