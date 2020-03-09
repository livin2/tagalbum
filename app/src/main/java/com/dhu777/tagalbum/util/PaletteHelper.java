package com.dhu777.tagalbum.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

public class PaletteHelper {
    private static final String TAG = "PaletteHelper";

    public static Palette.Swatch getDominSync(Bitmap bitmap){
        if (bitmap == null) return null;
        Palette.Swatch s =  Palette.from(bitmap).generate().getDominantSwatch();
        return s;
    }

    public interface getDominCallback{
        void onLoaded(@NonNull Palette.Swatch swatch);
    }
    public static void getDomin(Bitmap bitmap, final getDominCallback callback){
        if (bitmap == null) return;
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
               if(palette!=null){
                   Palette.Swatch s = palette.getDominantSwatch();
                   if(s!=null) callback.onLoaded(s);
               }
            }
        });
    }

    public static void setPaletteColor(Bitmap bitmap, final View view){
        if (bitmap == null) {
            return;
        }
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                int size = palette.getSwatches().size();
                Palette.Swatch s = palette.getDominantSwatch();//独特的一种
                Palette.Swatch s1 = palette.getVibrantSwatch();       //获取到充满活力的这种色调
                Palette.Swatch s2 = palette.getDarkVibrantSwatch();    //获取充满活力的黑
                Palette.Swatch s3 = palette.getLightVibrantSwatch();   //获取充满活力的亮
                Palette.Swatch s4 = palette.getMutedSwatch();           //获取柔和的色调
                Palette.Swatch s5 = palette.getDarkMutedSwatch();      //获取柔和的黑
                Palette.Swatch s6 = palette.getLightMutedSwatch();    //获取柔和的亮
                if (s != null && view!=null) {
                    view.setBackgroundColor(s.getRgb());
                }

                Log.d(TAG, "onGenerated: size:"+size);
                Log.d(TAG, "s:"+s);
                Log.d(TAG, "s:"+s.getHsl()[0]);
                Log.d(TAG, "s1:"+s1);
                Log.d(TAG, "s2:"+s2);
                Log.d(TAG, "s3:"+s3);
                Log.d(TAG, "s4:"+s4);
                Log.d(TAG, "s5:"+s5);
                Log.d(TAG, "s6:"+s6);
            }
        });
    }
}
