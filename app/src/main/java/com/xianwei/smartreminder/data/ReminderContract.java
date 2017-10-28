package com.xianwei.smartreminder.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by xianwei li on 10/20/2017.
 */

public final class ReminderContract {

    public static final String CONTENT_AUTHORITY = "com.xianwei.smartreminder";
    public static final Uri BASE_CONTENT_RUL = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TIME_REMINDER = "time";
    public static final String PATH_LOCATION_REMINDER = "location";

    public static class TimeEntry implements BaseColumns {

        public static final Uri CONTENT_URL =
                Uri.withAppendedPath(BASE_CONTENT_RUL, PATH_TIME_REMINDER);

        public static final String TABLE_NAME = "time_reminder";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_MILLISECOND = "millisecond";
        public static final String COLUMN_NAME_TASK_DONE = "task_done";
        public static final String COLUMN_NAME_HAS_TIME = "has_time";
    }

    public static class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URL =
                Uri.withAppendedPath(BASE_CONTENT_RUL, PATH_LOCATION_REMINDER);

        public static final String TABLE_NAME = "Location_reminder";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_LOCATION_NAME = "location_name";
        public static final String COLUMN_NAME_LOCATION_RADIUS = "location_radius";
        public static final String COLUMN_NAME_LOCATION_ID = "Location_id";
        public static final String COLUMN_NAME_TASK_DONE = "task_done";
    }
}
