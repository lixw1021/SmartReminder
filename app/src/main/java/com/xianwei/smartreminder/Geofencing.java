package com.xianwei.smartreminder;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;
import com.xianwei.smartreminder.service.ReminderIntentService;
import com.xianwei.smartreminder.service.ReminderTasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianwei li on 10/27/2017.
 */

public class Geofencing implements ResultCallback {

    public static final String TAG = Geofencing.class.getSimpleName();
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours

    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;
    private GoogleApiClient googleApiClient;
    private Context context;

    public Geofencing(Context context, GoogleApiClient client) {
        this.context = context;
        googleApiClient = client;
        geofencePendingIntent = null;
        geofenceList = new ArrayList<>();
    }

    public void registerAllGeofences() {
        if (googleApiClient == null || !googleApiClient.isConnected() ||
                geofenceList == null || geofenceList.size() == 0) {
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            Log.e(TAG, securityException.getMessage());
        }
    }

    public void unRegisterAllGeofences() {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    googleApiClient,
                    // This is the same pending intent that was used in registerGeofences
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }

    public void updateGeofencesList(Cursor cursor) {
        List<String[]> placeIdAndRadius = getPlaceIdAndRadiusFromCursor(cursor);
        if (placeIdAndRadius == null) return;
        String[] placeIds = placeIdAndRadius.get(0);
        final String[] radius = placeIdAndRadius.get(1);

        geofenceList = new ArrayList<>();
        Places.getGeoDataClient(context, null)
                .getPlaceById(placeIds)
                .addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(Task<PlaceBufferResponse> task) {
                        if (task.isSuccessful()) {
                            PlaceBufferResponse places = task.getResult();
                            for (int i = 0; i < places.getCount(); i++) {
                                Place place = places.get(i);
                                geofenceList.add(geofenceBuild(place, Integer.parseInt(radius[i])));
                            }
                            places.release();
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });
    }

    private Geofence geofenceBuild(Place place, int geofenceRadius) {
        String placeId = place.getId();
        double placeLat = place.getLatLng().latitude;
        double placeLng = place.getLatLng().longitude;

        return new Geofence.Builder()
                .setRequestId(placeId)
                .setExpirationDuration(GEOFENCE_TIMEOUT)
                .setCircularRegion(placeLat, placeLng, geofenceRadius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private List<String[]> getPlaceIdAndRadiusFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return null;
        String[] placeIds = new String[cursor.getCount()];
        String[] placeRadius = new String[cursor.getCount()];
        List<String[]> result = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String placeId = cursor
                    .getString(cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_ID));
            int radius = cursor
                    .getInt(cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_RADIUS));
            placeIds[i] = placeId;
            placeRadius[i] = String.valueOf(radius);
        }
        result.add(placeIds);
        result.add(placeRadius);
        return result;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, ReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_LOCATION_TASK_REMINDER);
        geofencePendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, String.format("Error adding/removing geofence : %s",
                result.getStatus().toString()));
    }
}
