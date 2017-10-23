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

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeReminderListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.reminder_list)
    RecyclerView reminderRecyclerView;

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
                TimeEntry.COLUMN_NAME_HAS_TIME};

        return new CursorLoader(getContext(), TimeEntry.CONTENT_URL, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        timeReminderAdapter = new TimeReminderAdapter(getContext());
        reminderRecyclerView.setAdapter(timeReminderAdapter);
        timeReminderAdapter.swapCursor(newCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        timeReminderAdapter.swapCursor(null);
    }
}
