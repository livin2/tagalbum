package com.dhu777.tagalbum.opt;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;

import java.util.Locale;
import java.util.concurrent.BlockingDeque;

public abstract class ForegoundOperation extends IntentService {
    private static final String TAG = "ForegoundOperation";
    public static final String FILES = "com.dhu777.tagalbum.OPT_FILES";
    public static final String TARGETS = "com.dhu777.tagalbum.OPT_TARGETS";
    public static final int NOTIFICATION_ID = 2777;

    public static final class Constants {
        public static final String BROADCAST_ACTION = "com.dhu777.tagalbum.OPT_FINISHED";
        public static final String EXTRA_STATUS = "com.dhu777.tagalbum.OPT_STATUS";
        public static final int STATUS_SUCCESS = 0;
        public static final int STATUS_FAIL = 1;
        public static final String EXTRA_DATA_MSG = "com.dhu777.tagalbum.OPT_RESULT";
        public static final String EXTRA_DATA_TITLE = "com.dhu777.tagalbum.OPT_TITLE";
        public static final String EXTRA_DATA_ICON = "com.dhu777.tagalbum.OPT_ICON";

    }

    public abstract void execute(Intent workIntent);
    public abstract String getNotificationTitle();
    public abstract int getNotificationSmallIconRes();

    public ForegoundOperation() {
        super("ForegoundOperation");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Notification notification = getNotificationBuilder()
                .setProgress(1, 0, false)
                .build();
        startForeground(NOTIFICATION_ID, notification);
        execute(intent);
        // todo  sendDoneBroadcast();
        stopForeground(true); //duplicated
    }

    public static AlbumItem[] getFiles(Intent workIntent) {
        Parcelable[] parcelables = workIntent.getParcelableArrayExtra(FILES);
        AlbumItem[] files = new AlbumItem[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            files[i] = (AlbumItem) parcelables[i];
            Log.d("TAG", "getFiles: "+files[i].getPath());
        }
        return files;
    }

    private NotificationCompat.Builder mBuilder;
    private NotificationCompat.Builder getNotificationBuilder() {
        if(mBuilder!=null)
            return mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(getApplicationContext());
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                getString(R.string.op_channel_id))
                .setContentTitle(getNotificationTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        builder.setSmallIcon(getNotificationSmallIconRes());
        mBuilder = builder;
        return builder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(
                context.getString(R.string.op_channel_id),
                context.getString(R.string.op_channel_name),
                NotificationManager.IMPORTANCE_LOW);
        mChannel.setDescription(context.getString(R.string.op_channel_description));
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    public void onProgress(final int progress, final int totalNumber) {
        NotificationCompat.Builder notifBuilder = getNotificationBuilder();
        if (progress >= 0){
            notifBuilder.setContentText(String.format("%d / %d", progress,totalNumber));
            notifBuilder.setProgress(totalNumber, progress, false);
        }
        else
            notifBuilder.setProgress(0, 0, true);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null)
            manager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

    public void onSuccess(int success_count){
        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_ACTION);
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.opt_finished)).append(success_count);
        intent.putExtra(Constants.EXTRA_DATA_MSG,sb.toString());
        intent.putExtra(Constants.EXTRA_DATA_TITLE,getNotificationTitle());
        intent.putExtra(Constants.EXTRA_DATA_ICON,getNotificationSmallIconRes());
        intent.putExtra(Constants.EXTRA_STATUS,Constants.STATUS_SUCCESS);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d(TAG, "onSuccess: ");
    }

    public void onFail(String msg){
        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_ACTION);
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.opt_fail)).append(msg);
        intent.putExtra(Constants.EXTRA_DATA_MSG,sb.toString());
        intent.putExtra(Constants.EXTRA_DATA_TITLE,getNotificationTitle());
        intent.putExtra(Constants.EXTRA_DATA_ICON,getNotificationSmallIconRes());
        intent.putExtra(Constants.EXTRA_STATUS,Constants.STATUS_FAIL);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
