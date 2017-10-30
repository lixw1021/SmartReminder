package com.xianwei.smartreminder;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;
import com.xianwei.smartreminder.service.ReminderIntentService;
import com.xianwei.smartreminder.service.ReminderTasks;

import java.util.List;

/**
 * Created by xianwei li on 10/29/2017.
 */

public class NewGeofencing {

    public static final String TAG = NewGeofencing.class.getSimpleName();
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours

    private Geofence geofence;
    private PendingIntent geofencePendingIntent;
    private Context context;
    private GeofencingClient geofencingClient;
    private GeoDataClient geoDataClient;

    public NewGeofencing(Context context) {
        this.context = context;
        geofencePendingIntent = null;
        geoDataClient = Places.getGeoDataClient(context, null);
    }

    public void registerGeofence(int rowId) {
        geofencingClient = LocationServices.getGeofencingClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        String[] project = new String[] {
                LocationEntry._ID,
                LocationEntry.COLUMN_NAME_TASK,
                LocationEntry.COLUMN_NAME_LOCATION_ID,
                LocationEntry.COLUMN_NAME_LOCATION_RADIUS};
        Uri uri = Uri.withAppendedPath(LocationEntry.CONTENT_URL, String.valueOf(rowId));
        Cursor cursor = context.getContentResolver().query(uri, project, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            String placeId = cursor
                    .getString(cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_ID));
            String task = cursor
                    .getString(cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_TASK));
            int radius = cursor
                    .getInt(cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_RADIUS));


            geofencingClient
                    .addGeofences(getGeofencingRequest(), getGeofencePendingIntent(rowId, task));
        }
        if (cursor != null) cursor.close();
    }

    public void unRegisterGeofence(List<String> geofenceIds) {
        geofencingClient = LocationServices.getGeofencingClient(context);
        geofencingClient.removeGeofences(geofenceIds);
    }

    public void buildGeofence(String placeId, final int radius) {
        geoDataClient.getPlaceById(placeId)
                .addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(Task<PlaceBufferResponse> task) {
                        if (task.isSuccessful()) {
                            PlaceBufferResponse places = task.getResult();
                            Place place = places.get(0);
                            geofence = createGeofence(place, radius);
                            places.release();
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });
    }

    private Geofence createGeofence(Place place, int geofenceRadius) {
        Log.i("12345geocreated", "created");
        String placeId = place.getId();
        double placeLat = place.getLatLng().latitude;
        double placeLng = place.getLatLng().longitude;
        return new Geofence.Builder()
                .setRequestId(placeId)
                .setExpirationDuration(GEOFENCE_TIMEOUT)
                .setCircularRegion(placeLat, placeLng, geofenceRadius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(int rowId, String task) {
//        if (geofencePendingIntent != null) {
//            return geofencePendingIntent;
//        }
        Intent intent = new Intent(context, ReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_LOCATION_TASK_REMINDER);
        intent.putExtra("rowId",rowId);
        intent.putExtra("task",task);
        geofencePendingIntent = PendingIntent.getService(
                context,
                rowId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}
