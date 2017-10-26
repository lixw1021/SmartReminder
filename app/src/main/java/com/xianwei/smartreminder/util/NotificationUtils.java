package com.xianwei.smartreminder.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;

import com.xianwei.smartreminder.MainActivity;
import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.service.ReminderTasks;
import com.xianwei.smartreminder.service.ReminderIntentService;

/**
 * Created by xianwei li on 10/25/2017.
 */

public class NotificationUtils {

    private static final int TIME_REMINDER_NOTIFICATION_ID = 1000;
    private static final int NOTIFICATION_SERVICE_ID = 1001;

    private static final int ACTION_TASK_DONE_ID = 2123;


    public static void clearAllNotification(Context context) {
        NotificationManager manger =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manger.cancelAll();
    }


    public static void timeReminder(Context context, String task) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_time)
                .setContentTitle(task)
                .setContentText("content text")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(taskDone(context))
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(TIME_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent (Context context) {
        Intent launchActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                TIME_REMINDER_NOTIFICATION_ID,
                launchActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Action taskDone(Context context) {
        Intent taskDoneIntent = new Intent(context, ReminderIntentService.class);
        taskDoneIntent.setAction(ReminderTasks.ACTION_TASK_DONE);
        PendingIntent taskDonePendingIntent = PendingIntent.getService(
                context,
                ACTION_TASK_DONE_ID,
                taskDoneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Action taskDoneAction = new Action(R.drawable.ic_time, "Task Finish", taskDonePendingIntent );
        return taskDoneAction;
    }


    public static void setupNotificationService(Context context, long milliseconds, String task) {
        Intent intent = new Intent(context, ReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_TASK_REMINDER);
        intent.putExtra("task", task);
        PendingIntent servicePendingIntent = PendingIntent.getService(
                context,
                NOTIFICATION_SERVICE_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manger = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manger.set(AlarmManager.RTC_WAKEUP, milliseconds, servicePendingIntent);
    }
}
