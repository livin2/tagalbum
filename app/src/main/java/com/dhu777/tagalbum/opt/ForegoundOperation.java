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

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;

public abstract class ForegoundOperation extends IntentService {
    private static final String TAG = "ForegoundOperation";
    public static final String FILES = "com.dhu777.tagalbum.extra.FILES";
    private static final int NOTIFICATION_ID = 2777;

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
        stopForeground(true);
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
                .setContentTitle(getNotificationTitle());
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
        if (progress >= 0)
            notifBuilder.setProgress(totalNumber, progress, false);
        else
            notifBuilder.setProgress(0, 0, true);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null)
            manager.notify(NOTIFICATION_ID, notifBuilder.build());
    }
}
