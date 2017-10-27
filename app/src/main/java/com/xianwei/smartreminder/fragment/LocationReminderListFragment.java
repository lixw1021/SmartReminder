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
import com.xianwei.smartreminder.adapter.LocationReminderAdapter;
import com.xianwei.smartreminder.data.ReminderContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xianwei li on 10/26/2017.
 */

public class LocationReminderListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.reminder_list)
    RecyclerView reminderRecyclerView;

    private static final int DATABASE_FALSE = 0;
    private static final int DATABASE_TRUE = 1;
    private static final int TIME_LOADER_ID = 101;

    private LocationReminderAdapter locationReminderAdapter;

    public LocationReminderListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(TIME_LOADER_ID, null, this);
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
        String[] projection = new String[]{
                ReminderContract.LocationEntry._ID,
                ReminderContract.LocationEntry.COLUMN_NAME_TASK,
                ReminderContract.LocationEntry.COLUMN_NAME_LOCATION_ID,
                ReminderContract.LocationEntry.COLUMN_NAME_TASK_DONE};

        return new CursorLoader(
                getContext(),
                ReminderContract.LocationEntry.CONTENT_URL,
                projection,
                ReminderContract.LocationEntry.COLUMN_NAME_TASK_DONE + "=?",
                new String[]{String.valueOf(DATABASE_FALSE)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        locationReminderAdapter = new LocationReminderAdapter(getContext());
        reminderRecyclerView.setAdapter(locationReminderAdapter);
        locationReminderAdapter.swapCursor(newCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        locationReminderAdapter.swapCursor(null);
    }
}
