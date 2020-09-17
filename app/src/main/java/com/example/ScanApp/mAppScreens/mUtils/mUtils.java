package com.example.ScanApp.mAppScreens.mUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.ScanApp.mAppScreens.Models.ScannedImageModel;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static Uri getImageUriFromBitmap(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmapFromUri(Uri uri,Bitmap bitmap,Context context) throws IOException {
        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;


    }


    public static void createPdfOfImageFromList(File root, ArrayList<ScannedImageModel> scannedImageModelsList, Context context,String folderName)
    {
        PdfDocument.Page page = null;
            PdfDocument pdfDocument = new PdfDocument();
            for (int i = 0 ; i<scannedImageModelsList.size() ; i++){
                PdfDocument.PageInfo  pi = new PdfDocument.PageInfo.Builder(scannedImageModelsList.get(i).getBitmap().getWidth()
                        ,scannedImageModelsList.get(i).getBitmap().getHeight()
                        ,1).create();
                page = pdfDocument.startPage(pi);
                Canvas canvas = page.getCanvas();
                Bitmap tmp = Bitmap.createScaledBitmap(scannedImageModelsList.get(i).getBitmap()
                        ,pi.getPageWidth()
                        ,pi.getPageHeight(),true);

                canvas.drawBitmap(tmp,0,0,null);

                pdfDocument.finishPage(page);

            }



            //RESMİ PDF OLARAK KAYDETME

            File file = new File(root,folderName+"document.pdf");

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                pdfDocument.writeTo(fileOutputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            pdfDocument.close();
        }




    public static void createPdfOfCard(File root ,List<ScannedImageModel> scannedImageModelsList,String folderName){
        int pageHeight=scannedImageModelsList.get(0).getBitmap().getHeight()+scannedImageModelsList.get(1).getBitmap().getHeight();
        int pageWidth=(Math.max(scannedImageModelsList.get(0).getBitmap().getWidth(), scannedImageModelsList.get(1).getBitmap().getWidth()));


        PdfDocument.Page page = null;
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo  pi = new PdfDocument.PageInfo.Builder(pageWidth
                ,pageHeight
                ,1).create();
        page = pdfDocument.startPage(pi);
        Canvas canvas = page.getCanvas();
        Bitmap on = Bitmap.createScaledBitmap(scannedImageModelsList.get(0).getBitmap()
                ,scannedImageModelsList.get(0).getBitmap().getWidth()
                ,scannedImageModelsList.get(0).getBitmap().getHeight(),true);

        canvas.drawBitmap(on,0,0,null);

        Bitmap arka = Bitmap.createScaledBitmap(scannedImageModelsList.get(1).getBitmap()
                ,scannedImageModelsList.get(1).getBitmap().getWidth()
                ,scannedImageModelsList.get(1).getBitmap().getHeight(),true);

        canvas.drawBitmap(arka,0,scannedImageModelsList.get(0).getBitmap().getHeight(),null);

        pdfDocument.finishPage(page);

        File file = new File(root,folderName+"card.pdf");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            pdfDocument.writeTo(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();
    }


}
