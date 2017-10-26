package com.xianwei.smartreminder.service;

import android.content.Context;
import android.content.Intent;

import com.xianwei.smartreminder.util.NotificationUtils;

/**
 * Created by xianwei li on 10/25/2017.
 */

public class ReminderTasks {

    public static final String ACTION_TASK_DONE = "task-done";
    public static final String ACTION_TASK_REMINDER = "task-reminder";

    public static void executeTask(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_TASK_DONE.equals(action)) {
            updateTask(context);
        } else if (ACTION_TASK_REMINDER.equals(action)) {
            String task = intent.getStringExtra("task");
            NotificationUtils.timeReminder(context, task);
            setupNextNotification(context);
        }
    }

    private static void setupNextNotification(Context context) {

    }

    private static void updateTask(Context context) {
        NotificationUtils.clearAllNotification(context);
    }
}
