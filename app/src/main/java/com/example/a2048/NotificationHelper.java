package com.example.a2048;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static final int PERMISSION_REQUEST_CODE = 1312;

    public static final String CHANNEL_ID_Achievement = "channel1";
    public static final String CHANNEL_NAME_Achievement = "成就";
    public static final int CHANNEL_LEVEL_Achievement = NotificationManager.IMPORTANCE_HIGH ;

    public static final String CHANNEL_ID_NightSpeech = "channel2";
    public static final String CHANNEL_NAME_NightSpeech = "夜语";
    public static final int CHANNEL_LEVEL_NightSpeech = NotificationManager.IMPORTANCE_DEFAULT;

    public static final String CHANNEL_ID_Letter = "channel3";
    public static final String CHANNEL_NAME_Letter = "来信";
    public static final int CHANNEL_LEVEL_Letter = NotificationManager.IMPORTANCE_HIGH;

    public static final String CHANNEL_ID_Message = "channel4";
    public static final String CHANNEL_NAME_Message = "消息";
    public static final int CHANNEL_LEVEL_Message = NotificationManager.IMPORTANCE_LOW;


    public static void sendNotification(Context context, String title, String text, String ID) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        String channelId = createNotificationChannel(context, ID);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions((MainActivity)context, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            return;
        }
        notificationManager.notify(100, notification.build());
    }

    @SuppressLint("ObsoleteSdkInt")
    private static String createNotificationChannel(Context context, String channelID, String channelNAME, int level) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            switch (channelID) {
                case CHANNEL_ID_Achievement:
                    channel.enableLights(true); //开启指示灯
                    channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC); //设置锁屏展示
                case CHANNEL_ID_NightSpeech:
                    channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE); //设置锁屏展示
                case CHANNEL_ID_Letter:
                    channel.enableLights(true); //开启指示灯
                    channel.enableVibration(true); //开启震动
                    channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE); //设置锁屏展示
                case CHANNEL_ID_Message:
                    channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC); //设置锁屏展示
                default:
            }
            manager.createNotificationChannel(channel);
            return channelID;
        } else {
            return null;
        }
    }
    private static String createNotificationChannel(Context context, String channelID) {
        switch (channelID) {
            case CHANNEL_ID_Achievement:
                return createNotificationChannel(context, channelID, CHANNEL_NAME_Achievement, CHANNEL_LEVEL_Achievement);
            case CHANNEL_ID_NightSpeech:
                return createNotificationChannel(context, channelID, CHANNEL_NAME_NightSpeech, CHANNEL_LEVEL_NightSpeech);
            case CHANNEL_ID_Letter:
                return createNotificationChannel(context, channelID, CHANNEL_NAME_Letter, CHANNEL_LEVEL_Letter);
            case CHANNEL_ID_Message:
                return createNotificationChannel(context, channelID, CHANNEL_NAME_Message, CHANNEL_LEVEL_Message);
            default:
                return createNotificationChannel(context, channelID, "未知", NotificationManager.IMPORTANCE_LOW);
        }
    }
}
