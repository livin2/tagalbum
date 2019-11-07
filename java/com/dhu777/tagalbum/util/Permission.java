package com.dhu777.tagalbum.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 用以检查权限的工具类.
 */
public class Permission {
    public static final int REQUEST_CODE_WRITE = 77;
    public static boolean checkWriteExternal(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            final int write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            final int granted = PackageManager.PERMISSION_GRANTED;
            if (read != granted || write != granted) {
                String[] requestedPermissions = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(context, requestedPermissions, REQUEST_CODE_WRITE);
                return false;
            }
        }
        return true;
    }
}
