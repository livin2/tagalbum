package com.dhu777.tagalbum.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.dhu777.tagalbum.R;

public class UiUtil {
    public static void showNotification(Context context,String title,String msg,int iconRes,
                                        int notificationId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context.getApplicationContext());
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                context.getString(R.string.op_channel_id))
                .setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
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

}
