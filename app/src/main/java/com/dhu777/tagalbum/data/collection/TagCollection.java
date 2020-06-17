package com.dhu777.tagalbum.data.collection;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.loader.content.CursorLoader;

import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.persistent.entity.TagView;
import com.dhu777.tagalbum.data.persistent.repository.SyncRepository;

import java.util.List;
import java.util.concurrent.Future;

public class TagCollection implements AlbumBucketBuilder{
    private static final String TAG = "TagCollection";
    private AlbumBucket albumdata;
    private List<String> taglist;
    private Context context;

    public TagCollection(@NonNull Context context,@NonNull String title,@NonNull List<String> taglist) {
        this.taglist = taglist;
        this.context = context;
        this.albumdata = new AlbumBucket().setTitle(title);
    }

    private static final String selectionPre = BaseColumns._ID + " IN ";
    private static final String[] projection = new String[]{
            BaseColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.DATE_TAKEN};

    private String makePlaceholders(int len) {
        StringBuilder sb = new StringBuilder(len * 2);
        sb.append("?");
        for (int i = 1; i < len; i++)
            sb.append(",?");
        return sb.toString();
    }

    @Override
    public AlbumBucket getAlbumBucketSync() {
        List<TagView> tagViews = SyncRepository.getInstance(context).getTagByTagList(taglist);
        if(tagViews.size()<=0)
            return new AlbumBucket();

        try(Cursor cursor = queryCursor(tagViews)){
            if(cursor!=null && cursor.moveToFirst()){
                fetchCursor(cursor);
            }
        }

        return albumdata;
    }

    private Cursor queryCursor(List<TagView> tagViews){
        String selection = selectionPre + "(" + makePlaceholders(tagViews.size()) + ")";
        String[] selectionArgs = new String[tagViews.size()];
        for (int i = 0; i < tagViews.size(); i++)
            selectionArgs[i] = String.valueOf(tagViews.get(i).getMediaId());
        return context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_ADDED);
    }

    private void fetchCursor(Cursor cursor){
        final int pathColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        final int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE);
        final int nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
        final int idColumn = cursor.getColumnIndex(BaseColumns._ID);
        final int dateTakenColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);

        do {
            AlbumItem albumItem = AlbumItem.getInstance(cursor.getString(mimeTypeColumn));
            if(albumItem != null){
                long id = cursor.getLong(idColumn);
                albumItem.setId(id);
                albumItem.setName(cursor.getString(nameColumn));
                albumItem.setPath(cursor.getString(pathColumn))
                        .setDateTaken(cursor.getLong(dateTakenColumn))
                        .setUri(ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id));

                albumdata.getAlbumItems().add(albumItem);
            }
        }while (cursor.moveToNext());
    }
}
