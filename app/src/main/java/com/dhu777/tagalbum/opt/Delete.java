package com.dhu777.tagalbum.opt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;


import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.persistent.repository.SyncRepository;
import com.dhu777.tagalbum.data.persistent.repository.TagRepository;

public class Delete extends  ForegoundOperation {
    public static final String ACTION_STR = "com.dhu777.tagalbum.action.Delete";
    public static void start(Context context, final AlbumItem[] selected_items){
        Intent intent = new Intent(context, Delete.class);
        intent.setAction(ACTION_STR);
        intent.putExtra(FILES, selected_items);
        context.startService(intent);
    }

    private String curFileName = "";

    @Override
    public void execute(Intent workIntent) {
//        AsyncRepository repository =(AsyncRepository)AsyncRepository.getInstance(getApplicationContext());
        TagRepository repository = SyncRepository.getInstance(getApplication());
        AlbumItem[] itemsToDel = getFiles(workIntent);
        int success_count = 0;
        onProgress(success_count, itemsToDel.length);
        for(AlbumItem item:itemsToDel){
            curFileName = item.getName();
            if(!repository.deleteMedia(item)){
                onFail(getString(R.string.fail_del_tag));
                continue;
            }
            getContentResolver().delete(item.getUri(),null,null);
            success_count++;
            onProgress(success_count, itemsToDel.length);
        }
        curFileName = "";
        onSuccess(success_count);
    }

    @Override
    public String getNotificationTitle() {
        return getString(R.string.delete)+" "+curFileName;
    }

    @Override
    public int getNotificationSmallIconRes() {
        return R.drawable.ic_launcher_foreground;
    }

}
