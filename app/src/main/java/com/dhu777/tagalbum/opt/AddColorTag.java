package com.dhu777.tagalbum.opt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.palette.graphics.Palette;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.persistent.repository.AsyncRepository;
import com.dhu777.tagalbum.util.ColorTag;
import com.dhu777.tagalbum.util.FileUtil;
import com.dhu777.tagalbum.util.PaletteHelper;

public class AddColorTag extends ForegoundOperation{
    public static final String ACTION_STR = "com.dhu777.tagalbum.action.AddColorTag";

    public static void start(Context context, final AlbumItem[] selected_items) {
        Intent intent = new Intent(context, AddColorTag.class);
        intent.setAction(ACTION_STR);
        intent.putExtra(FILES, selected_items);
        context.startService(intent);
    }

    @Override
    public void execute(Intent workIntent) {
        AsyncRepository repository =(AsyncRepository)AsyncRepository.getInstance(getApplicationContext());
        AlbumItem[] itemsToOpt = getFiles(workIntent);
        int success_count = 0;
        onProgress(success_count, itemsToOpt.length);
        for(AlbumItem item:itemsToOpt){
            boolean result = false;
            Bitmap bitmap = BitmapFactory.decodeFile(item.getPath());
            Palette.Swatch swatch = PaletteHelper.getDominSync(bitmap);
            if(swatch != null){
                String tag = ColorTag.tagOf(swatch);
                result = repository.insertTagForMediaSync(item,tag);
            }
            if (result) {
                success_count++;
                onProgress(success_count, itemsToOpt.length);
            } else {
                ;//todo add failed
            }
        }
        if (success_count == 0)
            onProgress(success_count, itemsToOpt.length);
    }

    @Override
    public String getNotificationTitle() {
        return getString(R.string.add_color_tag);
    }

    @Override
    public int getNotificationSmallIconRes() {
        return R.drawable.ic_launcher_foreground;
    }
}
