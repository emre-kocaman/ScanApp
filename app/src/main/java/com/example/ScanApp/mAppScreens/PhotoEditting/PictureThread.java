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

    private ImageView imageView;
    private Bitmap bitmap;
    public Bitmap temp_bitmap;
    private Canvas canvas;
    private Paint paint;
    private ColorMatrix colorMatrixBrightness,colorMatrixSharpness;
    private ColorMatrixColorFilter colorMatrixColorFilterBrightness,colorMatrixColorFilterSharpness;
    private Handler handler;
    private boolean running=false;


    public PictureThread(ImageView imageViewCropped, Bitmap bitmap) {
        this.imageView=imageViewCropped;
        this.bitmap = bitmap;
        temp_bitmap= Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());
        canvas= new Canvas(temp_bitmap);
        paint = new Paint();
        handler = new Handler();

    }

    public void adjustBrightness(int amount){
        colorMatrixBrightness = new ColorMatrix(new float[]{
                1, 0, 0, 0, amount,
                0, 1f, 0, 0, amount,
                0, 0, 1f, 0, amount,
                0, 0, 0, 1f, 0
        });

        colorMatrixColorFilterBrightness = new ColorMatrixColorFilter(colorMatrixBrightness);
        paint.setColorFilter(colorMatrixColorFilterBrightness);
        running=true;
    }

    public void adjustSharpness(int amount){
        colorMatrixSharpness=new ColorMatrix(new float[]{
                amount, 0, 0, 0, 0,
                0, amount, 0, 0, 0,
                0, 0, amount, 0, 0,
                0, 0, 0, 1, 0
        });
        colorMatrixColorFilterSharpness=new ColorMatrixColorFilter(colorMatrixSharpness);
        paint.setColorFilter(colorMatrixColorFilterSharpness);
        running=true;
    }

    public void adjustBrightnessAndSharpness(int amountBrightness,int amountSharpness){
        colorMatrixBrightness = new ColorMatrix(new float[]{
                amountSharpness+1, 0, 0, 0, amountBrightness,
                0, amountSharpness+1, 0, 0, amountBrightness,
                0, 0, amountSharpness+1, 0, amountBrightness,
                0, 0, 0, 1, 0
/*                0, 0, amountSharpness, 0, amountBrightness,
                0,0 , 0, amountSharpness/2, amountBrightness,
                0, 0,0 , amountSharpness/4, amountBrightness,
                0, 0, 0, 1, 0*/
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
