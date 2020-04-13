package com.dhu777.tagalbum.util;

import android.os.Build;
import android.os.Environment;

import java.io.File;

public class FileUtil {
    public static boolean isOnRemovableStorage(String path) {
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
}
