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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.adapter.LocationReminderAdapter;
import com.xianwei.smartreminder.adapter.TimeReminderAdapter;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;
import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskDoneFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.rv_finished_time_reminder)
    RecyclerView finishedTimeRecyclerView;
    @BindView(R.id.rv_finished_location_reminder)
    RecyclerView finishedLocationRecyclerView;

    private TimeReminderAdapter timeReminderAdapter;
    private LocationReminderAdapter locationReminderAdapter;

    private static final int DATABASE_FALSE = 0;
    private static final int DATABASE_TRUE = 1;
    private static final int FINISHED_TIME_LOADER_ID = 102;
    private static final int FINISHED_LOCATION_LOADER_ID = 103;

    public TaskDoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(FINISHED_TIME_LOADER_ID, null, this);
        getLoaderManager().initLoader(FINISHED_LOCATION_LOADER_ID, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_done, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        finishedTimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        timeReminderAdapter = new TimeReminderAdapter(getContext());
        finishedTimeRecyclerView.setAdapter(timeReminderAdapter);

        finishedLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        locationReminderAdapter = new LocationReminderAdapter(getContext());
        finishedLocationRecyclerView.setAdapter(locationReminderAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FINISHED_TIME_LOADER_ID) {
            return FinishedTimeCursorLoader();
        } else if (id == FINISHED_LOCATION_LOADER_ID) {
            return FinishedLocationCursorLoader();
        }
        return null;
    }

    private Loader<Cursor> FinishedLocationCursorLoader() {
        String[] projection = new String[]{
                LocationEntry._ID,
                LocationEntry.COLUMN_NAME_TASK,
                LocationEntry.COLUMN_NAME_LOCATION_NAME,
                LocationEntry.COLUMN_NAME_LOCATION_RADIUS,
                LocationEntry.COLUMN_NAME_LOCATION_ID,
                LocationEntry.COLUMN_NAME_TASK_DONE};

        return new CursorLoader(
                getContext(),
                LocationEntry.CONTENT_URL,
                projection,
                LocationEntry.COLUMN_NAME_TASK_DONE + "=?",
                new String[]{String.valueOf(DATABASE_TRUE)},
                null);
    }

    private Loader<Cursor> FinishedTimeCursorLoader() {
        String[] projection = new String[]{
                TimeEntry._ID,
                TimeEntry.COLUMN_NAME_TASK,
                TimeEntry.COLUMN_NAME_MILLISECOND,
                TimeEntry.COLUMN_NAME_HAS_TIME,
                TimeEntry.COLUMN_NAME_TASK_DONE};

        return new CursorLoader(
                getContext(),
                TimeEntry.CONTENT_URL,
                projection,
                TimeEntry.COLUMN_NAME_TASK_DONE + "=?",
                new String[]{String.valueOf(DATABASE_TRUE)},
                TimeEntry.COLUMN_NAME_MILLISECOND + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor newCursor) {
        if (newCursor == null || newCursor.getCount() < 0) return;
        if (loader.getId() == FINISHED_TIME_LOADER_ID) {
            timeReminderAdapter.swapCursor(newCursor);
        } else if (loader.getId() == FINISHED_LOCATION_LOADER_ID) {
            locationReminderAdapter.swapCursor(newCursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        timeReminderAdapter.swapCursor(null);
        locationReminderAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_taskdone_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_time_reminder:
                deleteAllTimeReminder();
                break;
            case R.id.delete_all_location_reminder:
                deleteAllLocationReminder();
                break;
            default:
                break;
        }
        return false;
    }

    private void deleteAllTimeReminder() {
        getContext().getContentResolver().delete(
                TimeEntry.CONTENT_URL,
                TimeEntry.COLUMN_NAME_TASK_DONE + "=?",
                new String[]{String.valueOf(DATABASE_TRUE)});
    }

    private void deleteAllLocationReminder() {
        getContext().getContentResolver().delete(
                LocationEntry.CONTENT_URL,
                LocationEntry.COLUMN_NAME_TASK_DONE + "=?",
                new String[]{String.valueOf(DATABASE_TRUE)});
    }
}
