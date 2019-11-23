package com.karim.com.io.Brodcast;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.karim.com.io.MainActivity;
import com.karim.com.io.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.karim.com.io.App.CHANNEL_1_ID;

public class MyReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if(alarmSound == null){
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if(alarmSound == null){
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        notificationManager = NotificationManagerCompat.from(context);
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.icon_lunch)
                .setContentTitle("hello content")
                .setContentText("text")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .build();

        notificationManager.notify(1, notification);
    }

}
