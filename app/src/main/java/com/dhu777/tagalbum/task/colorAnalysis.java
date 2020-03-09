package com.dhu777.tagalbum.task;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.palette.graphics.Palette;

import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.persistent.repository.AsyncRepository;
import com.dhu777.tagalbum.data.persistent.repository.TagRepository;
import com.dhu777.tagalbum.util.ColorTag;
import com.dhu777.tagalbum.util.PaletteHelper;

import java.util.List;

public class colorAnalysis extends AsyncTask<List<AlbumBucket>,Void,Void> {
    AsyncRepository repository;
    public colorAnalysis(Application application) {
        repository = (AsyncRepository)AsyncRepository.getInstance(application);
    }


    @SafeVarargs
    @Override
    protected final Void doInBackground(List<AlbumBucket>... lists) {
        for(List<AlbumBucket> list:lists)
            for (AlbumBucket albums:list){
                for (AlbumItem item:albums.getAlbumItems()){
                    Bitmap bitmap = BitmapFactory.decodeFile(item.getPath());
                    Palette.Swatch swatch =PaletteHelper.getDominSync(bitmap);
                    if(swatch != null){
                        String tag = ColorTag.tagOf(swatch);
                        repository.insertTagForMediaSync(item,tag);
                    }
                }
            }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
