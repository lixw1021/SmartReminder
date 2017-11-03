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
import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.module.DateAndTime;
import com.xianwei.smartreminder.util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xianwei li on 10/20/2017.
 */

public class TimeReminderAdapter extends RecyclerView.Adapter<TimeReminderAdapter.ViewHolder> {
    private static int DATABASE_FALSE = 0;
    private static int DATABASE_TRUE = 1;

    private Context context;
    private Cursor cursor;

    public TimeReminderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (cursor == null || cursor.getCount() == 0) return;
        cursor.moveToPosition(position);
        final int id = cursor.getInt(cursor.getColumnIndex(TimeEntry._ID));
        String task = cursor.getString(cursor.getColumnIndex(TimeEntry.COLUMN_NAME_TASK));
        long millisecond = cursor.getLong(cursor.getColumnIndex(TimeEntry.COLUMN_NAME_MILLISECOND));
        int hasTime = cursor.getInt(cursor.getColumnIndex(TimeEntry.COLUMN_NAME_HAS_TIME));
        int taskDone = cursor.getInt(cursor.getColumnIndex(TimeEntry.COLUMN_NAME_TASK_DONE));

        if (millisecond > 0) {
            DateAndTime dateAndTime = TimeUtil.millisecondToDateAndTime(millisecond);
            holder.date.setText(TimeUtil.dateDisplay(
                    dateAndTime.getYear(), dateAndTime.getMonth(), dateAndTime.getDay()));
            if (hasTime == DATABASE_TRUE) {
                holder.time.setText(TimeUtil.timeDisplay(
                        dateAndTime.getHour(), dateAndTime.getMinute()));
            } else {
                holder.time.setText(null);
            }
        } else {
            holder.date.setText(null);
            holder.time.setText(null);
        }

        holder.checkBox.setChecked(taskDone == DATABASE_TRUE);
        holder.itemId = id;
        holder.task.setText(task);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    Uri itemUri = ContentUris.withAppendedId(TimeEntry.CONTENT_URL, id);
                    updateItem(itemUri, DATABASE_TRUE);
                    Toast.makeText(context,
                            context.getString(R.string.toast_task_finished),
                            Toast.LENGTH_SHORT).show();
                } else if (!holder.checkBox.isChecked()) {
                    Uri itemUri = ContentUris.withAppendedId(TimeEntry.CONTENT_URL, id);
                    updateItem(itemUri, DATABASE_FALSE);
                    Toast.makeText(context,
                            context.getString(R.string.toast_task_forward_to_undo),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateItem(Uri itemUri, int taskDone) {
        ContentValues values = new ContentValues();
        values.put(TimeEntry.COLUMN_NAME_TASK_DONE, taskDone);
        context.getContentResolver().update(itemUri, values, null, null);
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
        @BindView(R.id.tv_time_reminder_title)
        TextView task;
        @BindView(R.id.tv_task_time)
        TextView time;
        @BindView(R.id.tv_task_date)
        TextView date;
        @BindView(R.id.checkbox_time_reminder)
        CheckBox checkBox;
        int itemId;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.setIsRecyclable(false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditActivity.class);
                    intent.putExtra(EditActivity.EXTRA_ITEM_ID, itemId);
                    intent.putExtra(
                            EditActivity.EXTRA_EDIT_FRAGMENT,
                            EditActivity.EXTRA_TIME_EDIT);
                    context.startActivity(intent);
                }
            });
        }
    }
}
