package com.xianwei.smartreminder.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.xianwei.smartreminder.Geofencing;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;
import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.util.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianwei li on 10/25/2017.
 */

public class ReminderTasks {

    public static final String ACTION_TIME_TASK_DONE = "com.xianwei.action.TIME_TASK_DONE";
    public static final String ACTION_TIME_TASK_POSTPONE = "com.xianwei.action.TIME_TASK_POSTPONE";
    public static final String ACTION_TIME_TASK_REMINDER = "com.xianwei.action.TIME_TASK_REMINDER";
    public static final String ACTION_LOCATION_TASK_REMINDER = "com.xianwei.action.LOCATION_TASK_REMINDER";
    public static final String ACTION_LOCATION_TASK_DONE = "com.xianwei.action.LOCATION_TASK_DONE";

    private static final int DATABASE_TRUE = 1;
    private static final int DATABASE_FALSE = 0;
    private static final long MILLISECOND_FIFTEEN_MINUTE = 15 * 60 * 1000;

    public static void executeTask(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_TIME_TASK_DONE.equals(action)) {
            int id = intent.getIntExtra(NotificationUtils.EXTRA_TASK_ID, -1);
            finishTask(context, id);

        } else if (ACTION_TIME_TASK_POSTPONE.equals(action)) {
            int id = intent.getIntExtra(NotificationUtils.EXTRA_TASK_ID, -1);
            long milliseconds = intent.getLongExtra(NotificationUtils.EXTRA_TASK_MILLISECONDS, 0);
            postponeTask(context, id, milliseconds);

        } else if (ACTION_TIME_TASK_REMINDER.equals(action)) {
            NotificationUtils.timeReminder(context, intent);
            long milliseconds = intent.getLongExtra(NotificationUtils.EXTRA_TASK_MILLISECONDS, 0);
            setupNextNotification(context, milliseconds);

        } else if (ACTION_LOCATION_TASK_REMINDER.equals(action)) {
            NotificationUtils.locationReminder(context, intent);
        } else if (ACTION_LOCATION_TASK_DONE.equals(action)) {
            int id = intent.getIntExtra(NotificationUtils.EXTRA_TASK_ID, -1);
            updateLocationTask(context, id);
        }
    }

    private static void updateLocationTask(Context context, int rowId) {
        Geofencing geofence = new Geofencing(context);
        Uri uri = Uri.withAppendedPath(LocationEntry.CONTENT_URL, String.valueOf(rowId));

        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_NAME_TASK_DONE, DATABASE_TRUE);
        context.getContentResolver().update(uri, values, null, null);
        NotificationUtils.clearAllNotification(context, NotificationUtils.LOCATION_NOTIFICATION_ID);

        String[] project = new String[]{
                LocationEntry._ID,
                LocationEntry.COLUMN_NAME_LOCATION_ID};
        Cursor cursor = context.getContentResolver().query(uri, project, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            String placeId = cursor.getString(
                    cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_ID));
            List<String> placeIds = new ArrayList<>();
            placeIds.add(placeId);
            geofence.unRegisterGeofence(placeIds);
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
                    intent.putExtra(NotificationUtils.EXTRA_TASK_ID, taskId);
                    intent.putExtra(NotificationUtils.EXTRA_TASK_MILLISECONDS, milliseconds);
                    intent.putExtra(NotificationUtils.EXTRA_TASK_TITLE, task);
                    NotificationUtils.timeReminder(context, intent);
                } else if (milliseconds > currentMilliseconds) {
                    String task =
                            cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_TASK));
                    int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TimeEntry._ID));
                    Bundle bundle = new Bundle();
                    bundle.putInt(NotificationUtils.EXTRA_TASK_ID, taskId);
                    bundle.putLong(NotificationUtils.EXTRA_TASK_MILLISECONDS, milliseconds);
                    bundle.putString(NotificationUtils.EXTRA_TASK_TITLE, task);
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
