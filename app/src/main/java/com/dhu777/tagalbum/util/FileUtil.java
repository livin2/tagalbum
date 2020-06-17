package com.dhu777.tagalbum.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class FileUtil {
    public static final String TAG = "FileUtil";
    public static boolean isOnRemovableStorage(@NonNull String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            File file = new File(path);
            try {
                if (file.exists() && Environment.isExternalStorageRemovable(file)) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Uri addContentItem(@NonNull ContentResolver contentResolver,String mimeType,String name){
        Uri photoColletion = MediaStore.Images.Media.getContentUri(MediaStore.MEDIA_SCANNER_VOLUME);
        ContentValues newImageDetails = new ContentValues();
        newImageDetails.put(MediaStore.Files.FileColumns.TITLE, name);
        newImageDetails.put(MediaStore.Files.FileColumns.MIME_TYPE, mimeType);
        return contentResolver.insert(photoColletion, newImageDetails);
    }

    public static Long getContentId(@NonNull ContentResolver contentResolver,@NonNull Uri uri){
//        Uri photoUri = MediaStore.Images.Media.getContentUri(MediaStore.MEDIA_SCANNER_VOLUME);
        Cursor returnCursor =
                contentResolver.query(uri,
                        new String[]{BaseColumns._ID},
                        null,
                        null,
                        null);
        returnCursor.moveToFirst();
        Long idReturn = returnCursor.getLong(returnCursor.getColumnIndex(BaseColumns._ID));
        returnCursor.close();
        return idReturn;
    }

    public static void logdConentItem(@NonNull ContentResolver contentResolver,@NonNull Uri uri){
        Cursor returnCursor =
                contentResolver.query(uri,
                        null,
                        null,
                        null,
                        null);
        returnCursor.moveToFirst();
        for (String name:returnCursor.getColumnNames()){
            Log.d(TAG, "queryConentItem:"+name);
        }
        returnCursor.close();
    }

    public static String getPathFromDocumentUri(@NonNull Uri uri){
        return uri.getPath().split(":")[2];
    }

    public static void updateContentItem(@NonNull ContentResolver contentResolver,@NonNull String oldPath,@NonNull Uri newuri){
        Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, getPathFromDocumentUri(newuri));
        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, "");

        boolean successMediaStore = contentResolver.update(
                photoUri, values,
        MediaStore.MediaColumns.DATA + "='" + oldPath + "'", null) == 1;
    }

    public static void updateContentItem(@NonNull ContentResolver contentResolver,@NonNull Uri oldUri,@NonNull Uri newuri){
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, getPathFromDocumentUri(newuri));
        boolean successMediaStore = contentResolver.update(oldUri, values,
                null, null) == 1;
    }

    public static String getMimeType(@NonNull ContentResolver contentResolver,@NonNull Uri uri){
//        try(Cursor cursor = contentResolver
//                .query(uri, null, null, null, null, null)){
//            if (cursor != null && cursor.moveToFirst()) {
//                return cursor.getString(
//                        cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
//            }
//            return "image/*";
//        }
        return contentResolver.getType(uri);
    }

    public static boolean deleteFile(@NonNull String path){
        File fdelete = new File(path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private static byte[] buf = new byte[1024];
    public static void Copy(FileInputStream input, FileOutputStream ouput) throws IOException {
        int len;
        while ((len = input.read(buf)) > 0)
            ouput.write(buf, 0, len);
    }

}
