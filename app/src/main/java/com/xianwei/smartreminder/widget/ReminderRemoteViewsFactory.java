package com.xianwei.smartreminder.widget;

import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.module.DateAndTime;
import com.xianwei.smartreminder.util.TimeUtil;

/**
 * Created by xianwei li on 11/2/2017.
 */

class ReminderRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final int DATABASE_FALSE = 0;
    private static final int DATABASE_TRUE = 1;
    private Context context;
    private Cursor cursor;

    public ReminderRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        cursor = getCursorFromDatabase(context);
    }

    private Cursor getCursorFromDatabase(Context context) {
        if (cursor != null) cursor.close();
        String[] projection = new String[]{
                TimeEntry._ID,
                TimeEntry.COLUMN_NAME_TASK,
                TimeEntry.COLUMN_NAME_MILLISECOND,
                TimeEntry.COLUMN_NAME_HAS_TIME,
                TimeEntry.COLUMN_NAME_TASK_DONE};

        return context.getContentResolver().query(
                TimeEntry.CONTENT_URL,
                projection,
                TimeEntry.COLUMN_NAME_TASK_DONE + "=?",
                new String[]{String.valueOf(DATABASE_FALSE)},
                TimeEntry.COLUMN_NAME_MILLISECOND + " ASC");
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (cursor == null || cursor.getCount() == 0) return 0;
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        cursor.moveToPosition(position);
        final int id = cursor.getInt(cursor.getColumnIndex(TimeEntry._ID));
        String task = cursor.getString(cursor.getColumnIndex(TimeEntry.COLUMN_NAME_TASK));
        long millisecond = cursor.getLong(cursor.getColumnIndex(TimeEntry.COLUMN_NAME_MILLISECOND));
        int hasTime = cursor.getInt(cursor.getColumnIndex(TimeEntry.COLUMN_NAME_HAS_TIME));

        RemoteViews reminderItemView = new RemoteViews(context.getPackageName(), R.layout.item_widget);
        reminderItemView.setTextViewText(R.id.tv_widget_item_title, task);
        if (millisecond > 0) {
            DateAndTime dateAndTime = TimeUtil.millisecondToDateAndTime(millisecond);

            reminderItemView.setTextViewText(R.id.tv_widget_item_date, TimeUtil.dateDisplay(
                    dateAndTime.getYear(), dateAndTime.getMonth(), dateAndTime.getDay()));

            if (hasTime == DATABASE_TRUE) {
                reminderItemView.setTextViewText(R.id.tv_widget_item_time, TimeUtil.timeDisplay(
                        dateAndTime.getHour(), dateAndTime.getMinute()));
            }
        }

        return reminderItemView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
