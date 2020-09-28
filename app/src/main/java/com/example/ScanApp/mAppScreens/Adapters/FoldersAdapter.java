package com.example.ScanApp.mAppScreens.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
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
import com.example.ScanApp.mAppScreens.MainActivity;
import com.example.ScanApp.mAppScreens.Models.Folder;
import com.example.ScanApp.mAppScreens.mUtils.StaticVeriables;
import com.example.ScanApp.mAppScreens.mUtils.mUtils;

import java.io.File;
import java.util.List;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.CardTasarimTutucu> implements View.OnDragListener {
    ConstraintLayout whenCheckedLayout;
    Context context;
    List<Folder> folderList;
    TextView folderSelected;
    ImageView imageViewClose;


    Boolean isFolderNameMovingFrom=true;
    String folderNameMovingFrom="";
    PdfsCardAdapter pdfAdapter;

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

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onDrag(View v, DragEvent event) {
        if (isFolderNameMovingFrom){
            folderNameMovingFrom=String.valueOf(v.getTag());
            isFolderNameMovingFrom=false;
        }
        switch (event.getAction()){
            case DragEvent.ACTION_DRAG_STARTED:
                Log.e("ACTION_DRAG_STARTED",String.valueOf(v.getTag()));
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.e("Back", String.valueOf(v.getTag()));
                v.setBackgroundColor(Color.parseColor("#41F27100"));
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                Log.e("ACTION_DRAG_LOCATION",String.valueOf(v.getTag()));
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                Log.e("ACTION_DRAG_EXITED",String.valueOf(v.getTag()));
                return true;
            case DragEvent.ACTION_DROP:
                mUtils.copyFileOrDirectory(StaticVeriables.movingPdfModel.getFilePath()
                        ,StaticVeriables.path+"/"+v.getTag().toString());
                new File(StaticVeriables.movingPdfModel.getFilePath()).delete();
                if (context instanceof MainActivity) {
                    ((MainActivity)context).folderList.clear();
                    ((MainActivity)context).getPdfFolderInfos();
                }
                StaticVeriables.checkedPdfList.clear();
                whenCheckedLayout.setVisibility(View.GONE);
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                return true;
            default:break;
        }
        return false;
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
            return new CardTasarimTutucu(v);
    }


    @Override
    public void onBindViewHolder(@NonNull CardTasarimTutucu holder, int position) {
        Log.e("Cagirildi",String.valueOf(position));

        Folder folder = folderList.get(position);

        holder.folderName.setText(folder.getFolderName());
        boolean isExpanded;

        isExpanded= folderList.get(position).getExpanded();


        holder.imageViewArrow.setImageResource(isExpanded ? R.drawable.ic_baseline_keyboard_arrow_up_24 : R.drawable.ic_baseline_keyboard_arrow_down_24);

        holder.constraintLayout2.setTag(holder.folderName.getText().toString().trim());

        holder.constraintLayout2.setOnDragListener(this);

        if (isExpanded){
            holder.expendableLayout.setVisibility(View.VISIBLE);
            holder.pdfFiles.setTag("Deneme");
            PdfsCardAdapter pdfAdapter= new PdfsCardAdapter(context,folder.getPdfDocumentsModels(),whenCheckedLayout,folderSelected,imageViewClose);
            holder.pdfFiles.setLayoutManager(new LinearLayoutManager(context));
            holder.pdfFiles.setAdapter(pdfAdapter);
        }
        else{
            holder.expendableLayout.setVisibility(View.GONE);
        }

        /*if (folder.getFolderName().equals("Default Pdf Folder")){
            Log.e("GIRDIMIdefF","EVET");
            holder.expendableLayout.setVisibility(View.VISIBLE);
            holder.constraintLayout2.setVisibility(View.GONE);

            pdfAdapter= new PdfsCardAdapter(context,folder.getPdfDocumentsModels(),whenCheckedLayout,folderSelected,imageViewClose);
            holder.pdfFiles.setLayoutManager(new LinearLayoutManager(context));
            holder.pdfFiles.setAdapter(pdfAdapter);
            holder.linearLayout.setPadding(0,0,0,0);
        }*/



    }


    @Override
    public int getItemCount() {
        return folderList.size();
    }

}
