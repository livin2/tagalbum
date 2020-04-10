package com.dhu777.tagalbum.task;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.palette.graphics.Palette;

import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.persistent.repository.AsyncRepository;
import com.dhu777.tagalbum.util.ColorTag;
import com.dhu777.tagalbum.util.PaletteHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class colorAnalysisFuture {
    @FunctionalInterface
    interface Callback {
        public void run();
    }

    AsyncRepository repository;
    public colorAnalysisFuture(Application application) {
        repository = (AsyncRepository)AsyncRepository.getInstance(application);
    }

    protected final void exec(List<AlbumBucket>... lists){
        CompletableFuture<Void> future = CompletableFuture.runAsync(() ->{
            for(List<AlbumBucket> list:lists)
                for (AlbumBucket albums:list){
                    for (AlbumItem item:albums.getAlbumItems()){
                        Bitmap bitmap = BitmapFactory.decodeFile(item.getPath());
                        Palette.Swatch swatch = PaletteHelper.getDominSync(bitmap);
                        if(swatch != null){
                            String tag = ColorTag.tagOf(swatch);
                            repository.insertTagForMediaSync(item,tag);
                        }
                    }
                }
        });
        future.join();
    }
}
