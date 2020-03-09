package com.dhu777.tagalbum.data.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import androidx.loader.content.CursorLoader;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

public class MediaRetrieverTest {
    private static final String TAG = "MediaRetrieverTest";
    private static final String VOLUME_EXTERNAL = "external";
    private static final String selection = MediaStore.Files.FileColumns.MEDIA_TYPE
            + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
    private static final String[] projection = new String[]{
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Video.VideoColumns.DATE_TAKEN,
            BaseColumns._ID};

    @Test
    public void loadAlbums() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        if(context == null)
            return;
        Uri uri = MediaStore.Files.getContentUri(VOLUME_EXTERNAL);
        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED);
        final Cursor cursor = cursorLoader.loadInBackground();
        if (cursor == null) {
            Log.d(TAG, "loadAlbums: cursor==null");
            return;
        }
        if(cursor.moveToFirst()){
            String path;
            long dateTaken, id;
            final int pathColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            final int idColumn = cursor.getColumnIndex(BaseColumns._ID);

            path = cursor.getString(pathColumn);
            Log.d(TAG, "Asyncload run: path"+path);

            String description = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DESCRIPTION));
            String picasa_id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.PICASA_ID));
            double latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE));
            int datetaken = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
            int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            int mini_thumb_magic = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC));
            String bucket_id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
            String bucket_display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));

            Log.d(TAG, "loadAlbums: description"+description);
            Log.d(TAG, "loadAlbums: picasa_id"+picasa_id);
            Log.d(TAG, "loadAlbums: latitude"+latitude);
            Log.d(TAG, "loadAlbums: longitude"+longitude);
            Log.d(TAG, "loadAlbums: datetaken"+datetaken);
            Log.d(TAG, "loadAlbums: orientation"+orientation);
            Log.d(TAG, "loadAlbums: mini_thumb_magic"+mini_thumb_magic);
            Log.d(TAG, "loadAlbums: bucket_id"+bucket_id);
            Log.d(TAG, "loadAlbums: bucket_display_name"+bucket_display_name);
//                    do {
//                        path = cursor.getString(pathColumn);
//                        Log.d(TAG, "Asyncload run: "+path);
//                    }while (cursor.moveToNext());
        }
        cursor.close();
    }
}