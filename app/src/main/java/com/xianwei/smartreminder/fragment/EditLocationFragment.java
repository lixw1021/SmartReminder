package com.xianwei.smartreminder.fragment;


import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
    @BindView(R.id.tv_location)
    TextView locationPickerTv;

    private static final String TAG = EditActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;

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
        return view;
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
            String placeID = place.getId();

            locationPickerTv.setText(placeID);
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
        String placeId = locationPickerTv.getText().toString().trim();
        if (TextUtils.isEmpty(task)) {
            toast("Please add a task");
        } else if (TextUtils.isEmpty(placeId)) {
            toast("please add a place");
        } else {
            ContentValues values = new ContentValues();
            values.put(LocationEntry.COLUMN_NAME_TASK, task);
            values.put(LocationEntry.COLUMN_NAME_LOCATION_ID, placeId);
            getContext().getContentResolver().insert(LocationEntry.CONTENT_URL, values);
            getActivity().finish();
        }
    }

    public void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
