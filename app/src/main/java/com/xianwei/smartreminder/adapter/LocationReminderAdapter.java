package com.xianwei.smartreminder.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.data.ReminderContract.LocationEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xianwei li on 10/26/2017.
 */

public class LocationReminderAdapter extends RecyclerView.Adapter<LocationReminderAdapter.ViewHolder> {
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
    public void onBindViewHolder(LocationReminderAdapter.ViewHolder holder, int position) {
        if (cursor == null || cursor.getCount() == 0) return;
        cursor.moveToPosition(position);
        String task = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_TASK));
        String locationName = cursor.getString(
                cursor.getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LOCATION_NAME));
        holder.task.setText(task);
        holder.location.setText(locationName);
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
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
