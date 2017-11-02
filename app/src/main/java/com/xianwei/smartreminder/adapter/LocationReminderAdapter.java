package com.xianwei.smartreminder.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.xianwei.smartreminder.EditActivity;
import com.xianwei.smartreminder.Geofencing;
import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xianwei li on 10/26/2017.
 */

public class LocationReminderAdapter extends RecyclerView.Adapter<LocationReminderAdapter.ViewHolder> {
    private static int DATABASE_FALSE = 0;
    private static int DATABASE_TRUE = 1;
    private Context context;
    private Cursor cursor;

    public LocationReminderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocationReminderAdapter.ViewHolder holder, int position) {
        if (cursor == null || cursor.getCount() == 0) return;
        cursor.moveToPosition(position);
        final int id = cursor.getInt(
                cursor.getColumnIndexOrThrow(LocationEntry._ID));
        String task = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_TASK));
        String locationName = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_NAME));
        int taskDone = cursor.getInt(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_TASK_DONE));
        final String placeId = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_ID));

        holder.checkBox.setChecked(taskDone == DATABASE_TRUE);
        holder.itemId = id;
        holder.task.setText(task);
        holder.location.setText(locationName);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    updateItem(id, DATABASE_TRUE);
                    unregisterGeofence(placeId);
                    Toast.makeText(context, "Task finished", Toast.LENGTH_SHORT).show();
                } else if (!holder.checkBox.isChecked()) {
                    updateItem(id, DATABASE_FALSE);
                    registerGeofence(id);
                    Toast.makeText(context, "Task forward to undo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateItem(int id, int hasDone) {
        Uri itemUri = ContentUris.withAppendedId(LocationEntry.CONTENT_URL, id );
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_NAME_TASK_DONE, hasDone);
        context.getContentResolver().update(itemUri, values, null, null );
    }

    private void unregisterGeofence(String placeId) {
        List<String> placeIds = new ArrayList<>();
        placeIds.add(placeId);
        Geofencing geofence = new Geofencing(context);
        geofence.unRegisterGeofence(placeIds);
    }

    private void registerGeofence(int rowId) {
        Geofencing geofence = new Geofencing(context);
        geofence.buildGeofence(rowId);
    }

    @Override
    public int getItemCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        if (cursor != null) {
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_location_reminder_task)
        TextView task;
        @BindView(R.id.tv_location)
        TextView location;
        @BindView(R.id.checkbox_location_reminder)
        CheckBox checkBox;
        int itemId;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditActivity.class);
                    intent.putExtra("itemId", itemId);
                    intent.putExtra("editFragment", "locationEdit");
                    context.startActivity(intent);
                }
            });
        }
    }
}
