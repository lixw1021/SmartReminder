package com.xianwei.smartreminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;
import com.xianwei.smartreminder.widget.TimeReminderWidgetProvider;

/**
 * Created by xianwei li on 10/20/2017.
 */

public class ReminderProvider extends ContentProvider {

    private static final String LOG_TAG = ReminderProvider.class.getSimpleName();
    private static final String QUERY_NOT_SUPPORT = "query is not support for :";
    private static final String INSERT_NOT_SUPPORT = "insert is not support for :";
    private static final String DELETE_NOT_SUPPORT = "delete is not support for :";
    private static final String UPDATE_NOT_SUPPORT = "update is not support for :";
    private static final int TIME_REMINDER = 100;
    private static final int TIME_REMINDER_ID = 101;
    private static final int LOCATION_REMINDER = 102;
    private static final int LOCATION_REMINDER_ID = 103;
    private ReminderDbHelper reminderDbHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY,
                ReminderContract.PATH_TIME_REMINDER, TIME_REMINDER);
        uriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY,
                ReminderContract.PATH_TIME_REMINDER + "/#", TIME_REMINDER_ID);
        uriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY,
                ReminderContract.PATH_LOCATION_REMINDER, LOCATION_REMINDER);
        uriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY,
                ReminderContract.PATH_LOCATION_REMINDER + "/#", LOCATION_REMINDER_ID);
    }

    @Override
    public boolean onCreate() {
        reminderDbHelper = new ReminderDbHelper(getContext());
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = reminderDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case TIME_REMINDER_ID:
                selection = TimeEntry._ID + "=?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                cursor = db.query(TimeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TIME_REMINDER:
                cursor = db.query(TimeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LOCATION_REMINDER_ID:
                selection = LocationEntry._ID + "=?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                cursor = db.query(LocationEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LOCATION_REMINDER:
                cursor = db.query(LocationEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(QUERY_NOT_SUPPORT + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case TIME_REMINDER:
                Uri newUri =  insertReminder(uri, values, TimeEntry.TABLE_NAME);
                TimeReminderWidgetProvider.sendRefreshBroadcast(getContext());
                return newUri;
            case LOCATION_REMINDER:
                return insertReminder(uri, values, LocationEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException(INSERT_NOT_SUPPORT + uri);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private Uri insertReminder(Uri uri, ContentValues values, String tableName) {
        SQLiteDatabase db = reminderDbHelper.getWritableDatabase();
        long id = db.insert(tableName, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for :" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase db = reminderDbHelper.getWritableDatabase();
        int rowDeleted;
        switch (uriMatcher.match(uri)) {
            case TIME_REMINDER_ID:
                selection = TimeEntry._ID + "=?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                rowDeleted = db.delete(TimeEntry.TABLE_NAME, selection, selectionArgs);
                TimeReminderWidgetProvider.sendRefreshBroadcast(getContext());
                break;
            case TIME_REMINDER:
                rowDeleted = db.delete(TimeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION_REMINDER_ID:
                selection = LocationEntry._ID + "=?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                rowDeleted = db.delete(LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION_REMINDER:
                rowDeleted = db.delete(LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(DELETE_NOT_SUPPORT + uri);
        }
        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case TIME_REMINDER_ID:
                selection = TimeEntry._ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return updateTimeReminder(uri, values, selection, selectionArgs);
            case LOCATION_REMINDER_ID:
                selection = LocationEntry._ID + "=?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return updateLocationReminder(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(UPDATE_NOT_SUPPORT + uri);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private int updateTimeReminder(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(TimeEntry.COLUMN_NAME_TASK)) {
            String task = values.getAsString(TimeEntry.COLUMN_NAME_TASK);
            if (task == null) {
                throw new IllegalArgumentException(UPDATE_NOT_SUPPORT +uri);
            }
        }
        SQLiteDatabase db = reminderDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(TimeEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        TimeReminderWidgetProvider.sendRefreshBroadcast(getContext());
        return rowsUpdated;
    }

    @SuppressWarnings("ConstantConditions")
    private int updateLocationReminder(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(LocationEntry.COLUMN_NAME_TASK)) {
            String task = values.getAsString(LocationEntry.COLUMN_NAME_TASK);
            if (TextUtils.isEmpty(task)) {
                throw new IllegalArgumentException(UPDATE_NOT_SUPPORT +uri);
            }
        }  else if (values.containsKey(LocationEntry.COLUMN_NAME_LOCATION_NAME)) {
            String locationName = values.getAsString(LocationEntry.COLUMN_NAME_LOCATION_NAME);
            if (TextUtils.isEmpty(locationName)) {
                throw new IllegalArgumentException(UPDATE_NOT_SUPPORT +uri);
            }
        } else if (values.containsKey(LocationEntry.COLUMN_NAME_LOCATION_ID)) {
            String locationId = values.getAsString(LocationEntry.COLUMN_NAME_LOCATION_ID);
            if (TextUtils.isEmpty(locationId)) {
                throw new IllegalArgumentException(UPDATE_NOT_SUPPORT +uri);
            }
        }
        SQLiteDatabase db = reminderDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(LocationEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
