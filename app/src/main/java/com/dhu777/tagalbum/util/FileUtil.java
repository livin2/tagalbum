package com.dhu777.tagalbum.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

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
    public static String getMimeType(@NonNull ContentResolver contentResolver,@NonNull Uri uri){
        try(Cursor cursor = contentResolver
                .query(uri, null, null, null, null, null)){
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(
                        cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
            }
            return "image/*";
        }
    }
    private static byte[] buf = new byte[1024];
    public static void Copy(FileInputStream input, FileOutputStream ouput) throws IOException {
        int len;
        while ((len = input.read(buf)) > 0)
            ouput.write(buf, 0, len);
    }
}
