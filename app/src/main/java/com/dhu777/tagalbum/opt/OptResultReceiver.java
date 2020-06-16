package com.dhu777.tagalbum.opt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.util.UiUtil;

public class OptResultReceiver extends BroadcastReceiver {
    private static final String TAG = "OptResultReceiver";
    private static final int NOTIFICATION_ID = 2772;
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(ForegoundOperation.Constants.EXTRA_STATUS,
                ForegoundOperation.Constants.STATUS_FAIL);
        String extra_title = intent.getStringExtra(ForegoundOperation.Constants.EXTRA_DATA_TITLE);
        int extra_icon = intent.getIntExtra(ForegoundOperation.Constants.EXTRA_DATA_ICON,
                R.drawable.ic_launcher_foreground);
        String extra_msg = intent.getStringExtra(ForegoundOperation.Constants.EXTRA_DATA_MSG);
        Log.d(TAG, "onReceive: "+extra_msg);
        UiUtil.showNotification(context,extra_title,extra_msg,extra_icon,NOTIFICATION_ID);
//        switch (status){
//            case ForegoundOperation.Constants.STATUS_SUCCESS:
//                break;
//            case ForegoundOperation.Constants.STATUS_FAIL:
//                break;
//        }
    }


}
