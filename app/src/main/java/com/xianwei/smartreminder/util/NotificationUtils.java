package com.xianwei.smartreminder.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;

import com.xianwei.smartreminder.MainActivity;
import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.service.ReminderIntentService;
import com.xianwei.smartreminder.service.ReminderTasks;

/**
 * Created by xianwei li on 10/25/2017.
 */
/*
* in order to push multiple notifications, use task id as pendingIntent id
* */
public class NotificationUtils {

    public static final String EXTRA_TASK_ID = "com.xianwei.extra.TASK_ID";
    public static final String EXTRA_TASK_MILLISECONDS = "com.xianwei.extra.TASK_MILLISECONDS";
    public static final String EXTRA_TASK_TITLE = "com.xianwei.extra.TASK_TITLE";
    private static final String TIME_NOTIFICATION_CHANNEL_ID = "time_channel";
    private static final String LOCATION_NOTIFICATION_CHANNEL_ID = "location_channel";

    //make sure it won't equal to time reminder notification id
    public static final int LOCATION_NOTIFICATION_ID = -100;
    private static final int NOTIFICATION_SERVICE_PENDING_INTENT_ID = 1001;
    private static final int CONTENT_PENDING_INTENT_ID = 1001;

    public static void clearAllNotification(Context context, int NotificationId) {
        NotificationManager manger =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manger.cancel(NotificationId);
    }

    public static void locationReminder(Context context, Intent intent) {
        String task = intent.getStringExtra(EXTRA_TASK_TITLE);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(
                    LOCATION_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.location_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            manager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context,LOCATION_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location)
                .setContentText(task)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(locationTaskDone(context, intent))
                .setAutoCancel(true);

        manager.notify(LOCATION_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static Action locationTaskDone(Context context, Intent intent) {
        int taskId = intent.getIntExtra(EXTRA_TASK_ID, 0);
        Intent locationTaskDoneIntent = new Intent(context, ReminderIntentService.class);
        locationTaskDoneIntent.setAction(ReminderTasks.ACTION_LOCATION_TASK_DONE);
        locationTaskDoneIntent.putExtras(intent);
        PendingIntent taskDonePendingIntent = PendingIntent.getService(
                context,
                taskId,
                locationTaskDoneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new Action(
                R.drawable.ic_location,
                context.getString(R.string.notification_task_finish),
                taskDonePendingIntent);
    }

    public static void timeReminder(Context context, Intent intent) {
        String task = intent.getStringExtra(EXTRA_TASK_TITLE);
        int taskId = intent.getIntExtra(EXTRA_TASK_ID, 0);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(
                    TIME_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.time_notification_channel_nbame),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            manager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, TIME_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_time)
                .setContentText(task)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(taskDone(context, intent))
                .addAction(taskPostpone(context, intent))
                .setAutoCancel(true);

        manager.notify(taskId, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context) {
        Intent launchActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                CONTENT_PENDING_INTENT_ID,
                launchActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Action taskDone(Context context, Intent intent) {
        int taskId = intent.getIntExtra(EXTRA_TASK_ID, 0);
        Intent taskDoneIntent = new Intent(context, ReminderIntentService.class);
        taskDoneIntent.setAction(ReminderTasks.ACTION_TIME_TASK_DONE);
        taskDoneIntent.putExtras(intent);
        PendingIntent taskDonePendingIntent = PendingIntent.getService(
                context,
                taskId,
                taskDoneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new Action(
                R.drawable.ic_time,
                context.getString(R.string.notification_task_finish),
                taskDonePendingIntent);
    }

    private static Action taskPostpone(Context context, Intent intent) {
        int taskId = intent.getIntExtra(EXTRA_TASK_ID, 0);
        Intent taskPostponeIntent = new Intent(context, ReminderIntentService.class);
        taskPostponeIntent.setAction(ReminderTasks.ACTION_TIME_TASK_POSTPONE);
        taskPostponeIntent.putExtras(intent);
        PendingIntent taskPostponePendingIntent = PendingIntent.getService(
                context,
                taskId,
                taskPostponeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new Action(
                R.drawable.ic_time,
                context.getString(R.string.notification_postphone_15_min),
                taskPostponePendingIntent);
    }


    public static void setupNotificationService(Context context, Bundle bundle) {
        long milliseconds = bundle.getLong(EXTRA_TASK_MILLISECONDS);
        Intent intent = new Intent(context, ReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_TIME_TASK_REMINDER);
        intent.putExtras(bundle);
        PendingIntent servicePendingIntent = PendingIntent.getService(
                context,
                NOTIFICATION_SERVICE_PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manger = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manger.set(AlarmManager.RTC_WAKEUP, milliseconds, servicePendingIntent);
    }
}
