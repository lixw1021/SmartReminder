package com.xianwei.smartreminder.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.xianwei.smartreminder.Geofencing;
import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.adapter.LocationReminderAdapter;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xianwei li on 10/26/2017.
 */

public class LocationReminderListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(R.id.reminder_list)
    RecyclerView reminderRecyclerView;

    private static final String TAG = LocationReminderListFragment.class.getSimpleName();
    private static final int DATABASE_FALSE = 0;
    private static final int DATABASE_TRUE = 1;
    private static final int TIME_LOADER_ID = 101;

    private LocationReminderAdapter locationReminderAdapter;
    private GoogleApiClient googleClient;
    private Geofencing geofencing;

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
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        locationReminderAdapter = new LocationReminderAdapter(getContext());
        reminderRecyclerView.setAdapter(locationReminderAdapter);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                new String[]{String.valueOf(DATABASE_FALSE)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        locationReminderAdapter.swapCursor(newCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        locationReminderAdapter.swapCursor(null);
    }


}
