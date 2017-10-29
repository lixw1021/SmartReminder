package com.xianwei.smartreminder.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.util.NotificationUtils;

/**
 * Created by xianwei li on 10/25/2017.
 */

public class ReminderTasks {

    public static final String ACTION_TIME_TASK_DONE = "task-done";
    public static final String ACTION_TIME_TASK_POSTPONE = "task-postpone";
    public static final String ACTION_TIME_TASK_REMINDER = "time-reminder";
    public static final String ACTION_LOCATION_TASK_REMINDER = "location-reminder";
    private static final String ID_KEY = "id";
    private static final String MILLISECONDS_KEY = "milliseconds";
    private static final String TASK_KEY = "task";
    private static final int DATABASE_TRUE = 1;
    private static final int DATABASE_FALSE = 0;
    private static final long MILLISECOND_FIFTEEN_MINUTE = 15 * 60 * 1000;

    public static void executeTask(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_TIME_TASK_DONE.equals(action)) {
            int id = intent.getIntExtra(ID_KEY, -1);
            finishTask(context, id);

        } else if (ACTION_TIME_TASK_POSTPONE.equals(action)) {
            int id = intent.getIntExtra(ID_KEY, -1);
            long milliseconds = intent.getLongExtra(MILLISECONDS_KEY, 0);
            postponeTask(context, id, milliseconds);

        } else if (ACTION_TIME_TASK_REMINDER.equals(action)) {
            Log.i("12345","time notification push");
            NotificationUtils.timeReminder(context, intent);
            long milliseconds = intent.getLongExtra(MILLISECONDS_KEY, 0);
            setupNextNotification(context, milliseconds);

        } else if (ACTION_LOCATION_TASK_REMINDER.equals(action)) {
            Log.i("12345","location notification push");
            NotificationUtils.locationReminder(context);
        }
    }

    /*
    * push new notifications if there are multiple reminder at this time,
    * and setup new alarm trigger for the next notification
    * */
    private static void setupNextNotification(Context context, long currentMilliseconds) {
        Cursor cursor = queryData(context);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long milliseconds =
                        cursor.getLong(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_MILLISECOND));
                if (milliseconds == currentMilliseconds) {
                    String task =
                            cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_TASK));
                    int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TimeEntry._ID));
                    Intent intent = new Intent();
                    intent.putExtra(ID_KEY, taskId);
                    intent.putExtra(MILLISECONDS_KEY, milliseconds);
                    intent.putExtra(TASK_KEY, task);
                    NotificationUtils.timeReminder(context, intent);
                } else if (milliseconds > currentMilliseconds) {
                    String task =
                            cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_TASK));
                    int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TimeEntry._ID));
                    Bundle bundle = new Bundle();
                    bundle.putInt(ID_KEY, taskId);
                    bundle.putLong(MILLISECONDS_KEY, milliseconds);
                    bundle.putString(TASK_KEY, task);
                    NotificationUtils.setupNotificationService(context, bundle);
                    break;
                }
            }
            cursor.close();
        }
    }

    private static Cursor queryData(Context context) {
        String[] project = new String[]{
                TimeEntry._ID,
                TimeEntry.COLUMN_NAME_TASK,
                TimeEntry.COLUMN_NAME_MILLISECOND};
        String selection = TimeEntry.COLUMN_NAME_TASK_DONE + "=?" + " AND " +
                TimeEntry.COLUMN_NAME_HAS_TIME + "=?";

        return context.getContentResolver().query(
                TimeEntry.CONTENT_URL,
                project,
                selection,
                new String[]{String.valueOf(DATABASE_FALSE), String.valueOf(DATABASE_TRUE)},
                TimeEntry.COLUMN_NAME_MILLISECOND + " ASC");
    }

    private static void finishTask(Context context, int id) {
        if (id <= 0) return;
        Uri uri = Uri.withAppendedPath(TimeEntry.CONTENT_URL, String.valueOf(id));
        ContentValues values = new ContentValues();
        values.put(TimeEntry.COLUMN_NAME_TASK_DONE, DATABASE_TRUE);
        context.getContentResolver().update(uri, values, null, null);
        NotificationUtils.clearAllNotification(context, id);
    }

    private static void postponeTask(Context context, int id, long milliseconds) {
        if (id <= 0 || milliseconds < 0) return;
        Uri uri = Uri.withAppendedPath(TimeEntry.CONTENT_URL, String.valueOf(id));
        ContentValues values = new ContentValues();
        values.put(TimeEntry.COLUMN_NAME_MILLISECOND, milliseconds + MILLISECOND_FIFTEEN_MINUTE);
        context.getContentResolver().update(uri, values, null, null);
        NotificationUtils.clearAllNotification(context, id);
    }
}
