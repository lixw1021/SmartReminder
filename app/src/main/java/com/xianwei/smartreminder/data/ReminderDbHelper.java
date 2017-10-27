package com.xianwei.smartreminder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;
/**
 * Created by xianwei li on 10/20/2017.
 */

public class ReminderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Reminder.db";

    private static final String SQL_CREATE_TIME_ENTRY =
            "CREATE TABLE " + TimeEntry.TABLE_NAME + " (" +
                    TimeEntry._ID + " INTEGER PRIMARY KEY," +
                    TimeEntry.COLUMN_NAME_TASK + " TEXT NOT NULL," +
                    TimeEntry.COLUMN_NAME_MILLISECOND + " INTEGER NOT NULL DEFAULT 0," +
                    TimeEntry.COLUMN_NAME_HAS_TIME + " INTEGER NOT NULL DEFAULT 0," +
                    TimeEntry.COLUMN_NAME_TASK_DONE + " INTEGER NOT NULL DEFAULT 0)";

    private static final String SQL_CREATE_LOCATION_ENTRY =
            "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                    LocationEntry._ID + " INTEGER PRIMARY KEY," +
                    LocationEntry.COLUMN_NAME_TASK + " TEXT NOT NULL," +
                    LocationEntry.COLUMN_NAME_LOCATION_ID + " TEXT NOT NULL, " +
                    LocationEntry.COLUMN_NAME_TASK_DONE + " INTEGER NOT NULL DEFAULT 0)";

    public ReminderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TIME_ENTRY);
        db.execSQL(SQL_CREATE_LOCATION_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // not need for the first version
    }
}
