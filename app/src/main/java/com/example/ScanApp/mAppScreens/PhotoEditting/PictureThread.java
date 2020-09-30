package com.example.ScanApp.mAppScreens.PhotoEditting;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.ColorRes;

import java.util.logging.LogRecord;

public class PictureThread extends Thread{

    public  ImageView imageView;
    public Bitmap bitmap;
    public Bitmap temp_bitmap;
    public Canvas canvas;
    public Paint paint;
    public ColorMatrix colorMatrixBrightness,colorMatrixSharpness;
    public ColorMatrixColorFilter colorMatrixColorFilterBrightness,colorMatrixColorFilterSharpness;
    public Handler handler;
    public boolean running=false;


    public PictureThread(ImageView imageViewCropped, Bitmap bitmap) {
        this.imageView=imageViewCropped;
        this.bitmap = bitmap;
        temp_bitmap= bitmap.copy(bitmap.getConfig(),true);
        canvas= new Canvas(temp_bitmap);
        paint = new Paint();
        handler = new Handler();

    }

    public void adjustBrightnessAndSharpness(int amountBrightness,int amountSharpness){
        colorMatrixBrightness = new ColorMatrix(new float[]{
                amountSharpness+1, 0, 0, 0, amountBrightness,
                0, amountSharpness+1, 0, 0, amountBrightness,
                0, 0, amountSharpness+1, 0, amountBrightness,
                0, 0, 0, 1, 0

        });

        colorMatrixColorFilterBrightness = new ColorMatrixColorFilter(colorMatrixBrightness);
        paint.setColorFilter(colorMatrixColorFilterBrightness);
        running=true;
    }

    @Override
    public void run() {
        while (true){
            if (running){
                canvas.drawBitmap(bitmap,0,0,paint);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(temp_bitmap);

                        running=false;
                    }
                });
            }
        }
    }
}
