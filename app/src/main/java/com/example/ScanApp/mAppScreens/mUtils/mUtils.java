package com.example.ScanApp.mAppScreens.mUtils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class mUtils {

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public static void matToBitmap(Bitmap bmp, Mat tmp, Mat src){
        try {
            //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);

            Imgproc.cvtColor(src, tmp, Imgproc.COLOR_GRAY2RGBA, 4);

            bmp = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, bmp);
        }

        catch (CvException e){
            Log.d("Exception",e.getMessage());}


    }

}
