package com.dd.surf.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitMapUtil {
    /**
     * 将本地图片转成Bitmap
     * @param path 已有图片的路径
     */
    public static Bitmap openImage(String path){
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis;
            bis = new BufferedInputStream(new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
