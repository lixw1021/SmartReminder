package com.xianwei.smartreminder.fragment;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.xianwei.smartreminder.EditActivity;
import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditLocationFragment extends Fragment {

    @BindView(R.id.et_location_task)
    EditText locationTaskEt;
    @BindView(R.id.et_location_Name)
    EditText locationNameEt;
    @BindView(R.id.tv_location)
    TextView locationPickerTv;
    @BindView(R.id.ib_delete)
    ImageButton deleteBtn;

    private static final String TAG = EditActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;
    private String placeId;
    private Bundle bundle;
    private int itemId;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

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
        mGeoDataClient = Places.getGeoDataClient(getContext(), null);
        bundle = this.getArguments();
        if (bundle != null) {
            itemId = bundle.getInt("itemId");
            setupItemInfo(itemId);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setupItemInfo(int itemId) {
        Cursor cursor = queryData(itemId);
        cursor.moveToNext();
        String task = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_TASK));
        String locationName = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_NAME));
        placeId = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_ID));

        deleteBtn.setVisibility(View.VISIBLE);
        locationTaskEt.setText(task);
        locationNameEt.setText(locationName);
        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete( Task<PlaceBufferResponse> task) {
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
                Toast.makeText(getContext(), "No place picked", Toast.LENGTH_LONG).show();
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
        if (TextUtils.isEmpty(task)) {
            toast("Please add a task");
        } else if (TextUtils.isEmpty(locationName)) {
            toast("Please add a place name");
        } else if (TextUtils.isEmpty(placeId)) {
            toast("Please pick a place");
        } else {
            ContentValues values = new ContentValues();
            values.put(LocationEntry.COLUMN_NAME_TASK, task);
            values.put(LocationEntry.COLUMN_NAME_LOCATION_NAME, locationName);
            values.put(LocationEntry.COLUMN_NAME_LOCATION_ID, placeId);
            if (itemId > 0) {
                Uri uri = Uri.withAppendedPath(LocationEntry.CONTENT_URL, String.valueOf(itemId));
                getContext().getContentResolver().update(uri, values, null, null);
            } else {
                getContext().getContentResolver().insert(LocationEntry.CONTENT_URL, values);
            }
            getActivity().finish();
        }
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
}
