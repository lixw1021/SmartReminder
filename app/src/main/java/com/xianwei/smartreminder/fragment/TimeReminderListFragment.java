package com.xianwei.smartreminder.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.adapter.TimeReminderAdapter;
import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.util.NotificationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeReminderListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.reminder_list)
    RecyclerView reminderRecyclerView;

    private static int DATABASE_FALSE = 0;
    private static int DATABASE_TRUE = 1;

    private TimeReminderAdapter timeReminderAdapter;

    public TimeReminderListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        reminderRecyclerView.setLayoutManager(manager);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{TimeEntry._ID,
                TimeEntry.COLUMN_NAME_TASK,
                TimeEntry.COLUMN_NAME_MILLISECOND,
                TimeEntry.COLUMN_NAME_HAS_TIME,
                TimeEntry.COLUMN_NAME_TASK_DONE};

        return new CursorLoader(getContext(),
                TimeEntry.CONTENT_URL,
                projection,
                TimeEntry.COLUMN_NAME_TASK_DONE + "=?",
                new String[]{String.valueOf(DATABASE_FALSE)},
                TimeEntry.COLUMN_NAME_MILLISECOND + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        timeReminderAdapter = new TimeReminderAdapter(getContext());
        reminderRecyclerView.setAdapter(timeReminderAdapter);
        timeReminderAdapter.swapCursor(newCursor);
        setAlarm(newCursor);
    }

    //set alarm to trigger notification service
    private void setAlarm(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        long currentMilliseconds = System.currentTimeMillis();
        long milliseconds;
        while (cursor.moveToNext()) {
            int hasTime = cursor.
                    getInt(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_HAS_TIME));
            milliseconds = cursor.
                    getLong(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_MILLISECOND));
            if (hasTime == DATABASE_TRUE && milliseconds >= currentMilliseconds) {
                String task = cursor.
                        getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_TASK));
                NotificationUtils.setupNotificationService(getContext(), milliseconds, task);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        timeReminderAdapter.swapCursor(null);
    }
}
