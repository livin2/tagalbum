package com.dhu777.tagalbum.opt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.util.FileUtil;

import java.io.File;

public class Delete extends  ForegoundOperation {
    public static final String ACTION_STR = "com.dhu777.tagalbum.action.DELETE";
    public static void start(Context context, final AlbumItem[] selected_items){
        Intent intent = new Intent(context, Delete.class);
        intent.setAction(ACTION_STR);
        intent.putExtra(FILES, selected_items);
        context.startService(intent);
    }
    @Override
    public void execute(Intent workIntent) {
        AlbumItem[] itemsToDel = getFiles(workIntent);
        int success_count = 0;
        onProgress(success_count, itemsToDel.length);
        for(AlbumItem item:itemsToDel){

            //todo for debug
//            try{
//                Thread.sleep(1000);
//            }catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            boolean result = false;
            if (FileUtil.isOnRemovableStorage(item.getPath())) {
                //todo Delete item from RemovableStorage
            }else
                result = deleteFile(item.getPath());
            if (result) {
                success_count++;
                onProgress(success_count, itemsToDel.length);
            } else {
                ;//todo delete failed
            }
        }

        if (success_count == 0) {
            onProgress(success_count, itemsToDel.length);
        }
    }
    public boolean deleteFile(String path) {
        File file = new File(path);
        if(file.isDirectory())
            return false;
        //todo if del a folder
        return file.delete();
    }

    @Override
    public String getNotificationTitle() {
        return getString(R.string.delete);
    }

    @Override
    public int getNotificationSmallIconRes() {
        return R.drawable.ic_launcher_foreground;
    }

}
