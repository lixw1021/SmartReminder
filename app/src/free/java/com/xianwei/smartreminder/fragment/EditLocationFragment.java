package com.xianwei.smartreminder.fragment;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.xianwei.smartreminder.EditActivity;
import com.xianwei.smartreminder.Geofencing;
import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditLocationFragment extends Fragment {

    private static final int FINE_LOCATION_PERMISSION_REQUEST = 10000;
    @BindView(R.id.et_location_task)
    EditText locationTaskEt;
    @BindView(R.id.et_location_name)
    EditText locationNameEt;
    @BindView(R.id.tv_location)
    TextView locationPickerTv;
    @BindView(R.id.et_location_reminder_radius)
    EditText reminderRadiusEt;
    @BindView(R.id.ib_delete)
    ImageButton deleteBtn;
//    @BindView(R.id.adView_edit_location)
//    AdView adView;

    private static final String TAG = EditActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;
    private String placeId;
    private Bundle bundle;
    private int itemId;
    private GeoDataClient geoDataClient;
    private Geofencing geofencing;

    public EditLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_location, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        geoDataClient = Places.getGeoDataClient(getContext(), null);
        geofencing = new Geofencing(getContext());
        bundle = this.getArguments();
        if (bundle != null) {
            itemId = bundle.getInt(EditActivity.EXTRA_ITEM_ID, -1);
            String task = bundle.getString(EditActivity.EXTRA_VOICE_INPUT);
            if (itemId > -1) {
                setupItemInfo(itemId);
            }
            if (task != null) {
                locationTaskEt.setText(task);
            }
        }
        AdView adView = (AdView) view.findViewById(R.id.adView_edit_location) ;
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
        return view;
    }

    private void setupItemInfo(int itemId) {
        Cursor cursor = queryData(itemId);
        cursor.moveToNext();
        String task = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_TASK));
        String locationName = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_NAME));
        int radius = cursor.getInt(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_RADIUS));
        placeId = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_ID));

        deleteBtn.setVisibility(View.VISIBLE);
        locationTaskEt.setText(task);
        locationNameEt.setText(locationName);
        reminderRadiusEt.setText(String.valueOf(radius));
        geoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place place = places.get(0);
                    locationPickerTv.setText(place.getName() + "\n" + place.getAddress());
                    places.release();
                } else {
                    Log.e(TAG, "Place not found.");
                }
            }
        });
    }

    private Cursor queryData(int itemId) {
        Uri uri = Uri.withAppendedPath(LocationEntry.CONTENT_URL, String.valueOf(itemId));
        String[] project = new String[]{
                LocationEntry._ID,
                LocationEntry.COLUMN_NAME_TASK,
                LocationEntry.COLUMN_NAME_LOCATION_NAME,
                LocationEntry.COLUMN_NAME_LOCATION_RADIUS,
                LocationEntry.COLUMN_NAME_LOCATION_ID,
                LocationEntry.COLUMN_NAME_TASK_DONE};
        return getContext().getContentResolver().query(uri, project, null, null, null);
    }

    @OnClick(R.id.tv_location)
    void locationPicker() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(getActivity());
            startActivityForResult(i, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(getContext(), data);
            if (place == null) {
                Toast.makeText(getContext(),
                        R.string.toast_no_place_picked,
                        Toast.LENGTH_LONG).show();
                return;
            }

            String placeName = place.getName().toString();
            String placeAddress = place.getAddress().toString();
            placeId = place.getId();

            locationPickerTv.setText(placeName + "\n" + placeAddress);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveTask();
                break;
            default:
                break;
        }
        return true;
    }

    private void saveTask() {
        String task = locationTaskEt.getText().toString().trim();
        String locationName = locationNameEt.getText().toString().trim();
        String stringRadius = reminderRadiusEt.getText().toString().trim();
        if (TextUtils.isEmpty(task)) {
            toast(getString(R.string.toast_please_add_a_task));
        } else if (TextUtils.isEmpty(locationName)) {
            toast(getString(R.string.toast_pleas_add_place_name));
        } else if (TextUtils.isEmpty(placeId)) {
            toast(getString(R.string.toast_please_pick_a_place));
        } else if (TextUtils.isEmpty(stringRadius)) {
            toast(getString(R.string.toast_please_enter_radius));
        } else if (Integer.parseInt(stringRadius) < 20 || Integer.parseInt(stringRadius) > 10000) {
            toast(getString(R.string.toast_please_enter_a_valid_radius));
        } else if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST);
        } else {
            int radius = Integer.parseInt(stringRadius);
            ContentValues values = new ContentValues();
            values.put(LocationEntry.COLUMN_NAME_TASK, task);
            values.put(LocationEntry.COLUMN_NAME_LOCATION_NAME, locationName);
            values.put(LocationEntry.COLUMN_NAME_LOCATION_RADIUS, radius);
            values.put(LocationEntry.COLUMN_NAME_LOCATION_ID, placeId);
            if (itemId > 0) {
                Uri uri = Uri.withAppendedPath(LocationEntry.CONTENT_URL, String.valueOf(itemId));
                getContext().getContentResolver().update(uri, values, null, null);
                updateGeofence(placeId, itemId);
            } else {
                Uri uri =
                        getContext().getContentResolver().insert(LocationEntry.CONTENT_URL, values);
                if (uri != null) {
                    String insertedRowId = uri.getLastPathSegment();
                    geofencing.buildGeofence(Integer.parseInt(insertedRowId));
                }
            }
            getActivity().finish();
        }
    }

    private void updateGeofence(String placeId, int itemId) {
        List<String> unregisterPlaceIds = new ArrayList<>();
        unregisterPlaceIds.add(placeId);
        geofencing.unRegisterGeofence(unregisterPlaceIds);
        geofencing.buildGeofence(itemId);
    }

    @OnClick(R.id.ib_delete)
    void deleteItem() {
        Uri uri = Uri.withAppendedPath(LocationEntry.CONTENT_URL, String.valueOf(itemId));
        getContext().getContentResolver().delete(uri, null, null);
        getActivity().finish();
    }

    public void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
