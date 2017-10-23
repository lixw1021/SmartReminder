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
import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.module.TimeReminder;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (cursor == null || cursor.getCount() == 0) return;
        cursor.moveToPosition(position);
        String task = cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_TASK));
        long date = cursor.getLong(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_MILLISECOND));
        int displayTime = cursor.getInt(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_HAS_TIME));

        TimeReminder dateAndTime = TimeUtil.millisecondToDateAndTime(date);
        if (displayTime == DATABASE_TRUE) {
            holder.time.setText(dateAndTime.getTime());
        }

        holder.task.setText(task);
        holder.date.setText(dateAndTime.getDate());
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
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
