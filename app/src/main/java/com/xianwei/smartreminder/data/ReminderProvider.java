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
import android.util.Log;

import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
/**
 * Created by xianwei li on 10/20/2017.
 */

public class ReminderProvider extends ContentProvider {

    public static final String LOG_TAG = ReminderProvider.class.getSimpleName();
    private static final int REMINDER = 100;
    private static final int REMINDER_ID = 101;
    public ReminderDbHelper reminderDbHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY,
                ReminderContract.PATH_TIME_REMINDER, REMINDER);
        uriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY,
                ReminderContract.PATH_TIME_REMINDER + "/#", REMINDER_ID);
    }

    @Override
    public boolean onCreate() {
        reminderDbHelper = new ReminderDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = reminderDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case REMINDER:
                cursor = db.query(TimeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, TimeEntry.COLUMN_NAME_MILLISECOND + " ASC");
                break;
            default:
                throw new IllegalArgumentException("query is not support for :" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case REMINDER:
                return insertReminder(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for :" + uri);
        }
    }

    private Uri insertReminder(Uri uri, ContentValues values) {
        SQLiteDatabase db = reminderDbHelper.getWritableDatabase();
        long id = db.insert(TimeEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for :" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase db = reminderDbHelper.getWritableDatabase();
        int rowDeleted;
        switch (uriMatcher.match(uri)) {
            case REMINDER_ID:
                selection = TimeEntry._ID + "=?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                rowDeleted = db.delete(TimeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("delete is not supported for :" + uri);
        }
        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
