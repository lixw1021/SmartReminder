package com.xianwei.smartreminder.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    private static final String ID_KEY = "id";
    private static final String MILLISECONDS_KEY = "milliseconds";
    private static final String TASK_KEY = "task";

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
        String task = intent.getStringExtra("task");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_location)
                .setContentText(task)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(locationTaskDone(context, intent))
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(LOCATION_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static Action locationTaskDone(Context context, Intent intent) {
        int taskId = intent.getIntExtra(ID_KEY, 0);
        Intent locationTaskDoneIntent = new Intent(context, ReminderIntentService.class);
        locationTaskDoneIntent.setAction(ReminderTasks.ACTION_LOCATION_TASK_DONE);
        locationTaskDoneIntent.putExtras(intent);
        PendingIntent taskDonePendingIntent = PendingIntent.getService(
                context,
                taskId,
                locationTaskDoneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new Action(R.drawable.ic_location, "Task Finish", taskDonePendingIntent);
    }

    public static void timeReminder(Context context, Intent intent) {
        String task = intent.getStringExtra(TASK_KEY);
        int taskId = intent.getIntExtra(ID_KEY, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_time)
                .setContentText(task)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(taskDone(context, intent))
                .addAction(taskPostpone(context, intent))
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
        int taskId = intent.getIntExtra(ID_KEY, 0);
        Intent taskDoneIntent = new Intent(context, ReminderIntentService.class);
        taskDoneIntent.setAction(ReminderTasks.ACTION_TIME_TASK_DONE);
        taskDoneIntent.putExtras(intent);
        PendingIntent taskDonePendingIntent = PendingIntent.getService(
                context,
                taskId,
                taskDoneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new Action(R.drawable.ic_time, "Task Finish", taskDonePendingIntent);
    }

    private static Action taskPostpone(Context context, Intent intent) {
        int taskId = intent.getIntExtra(ID_KEY, 0);
        Intent taskPostponeIntent = new Intent(context, ReminderIntentService.class);
        taskPostponeIntent.setAction(ReminderTasks.ACTION_TIME_TASK_POSTPONE);
        taskPostponeIntent.putExtras(intent);
        PendingIntent taskPostponePendingIntent = PendingIntent.getService(
                context,
                taskId,
                taskPostponeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new Action(R.drawable.ic_time, "PostPone 15 min", taskPostponePendingIntent);
    }


    public static void setupNotificationService(Context context, Bundle bundle) {
        long milliseconds = bundle.getLong(MILLISECONDS_KEY);
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
