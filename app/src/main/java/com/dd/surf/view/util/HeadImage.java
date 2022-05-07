package com.dd.surf.view.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class HeadImage {

    public static Bitmap makeBitmapSquare(Bitmap oldbitmap, int newWidth){
        Bitmap newbitmap=null;
        if (oldbitmap.getWidth()>oldbitmap.getHeight()){
            newbitmap=Bitmap.createBitmap(oldbitmap,oldbitmap.getWidth()/2-oldbitmap.getHeight()/2,0,oldbitmap.getHeight(),oldbitmap.getHeight());
        }else{
            newbitmap=Bitmap.createBitmap(oldbitmap,0,oldbitmap.getHeight()/2-oldbitmap.getWidth()/2,oldbitmap.getWidth(),oldbitmap.getWidth());
        }
        int width=newbitmap.getWidth();
        float scaleWidth=((float)newWidth)/width;
        Matrix matrix=new Matrix();
        matrix.postScale(scaleWidth,scaleWidth);
        newbitmap= Bitmap.createBitmap(newbitmap,0,0,width,width,matrix,true);
        return newbitmap;
    }

}
