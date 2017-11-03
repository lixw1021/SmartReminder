package com.xianwei.smartreminder.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xianwei.TimeRecognition;
import com.xianwei.smartreminder.EditActivity;
import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.data.ReminderContract;
import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.module.DateAndTime;
import com.xianwei.smartreminder.util.TimeUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditTimeFragment extends Fragment {
    @BindView(R.id.et_time_task)
    EditText taskEt;
    @BindView(R.id.tv_date_picker)
    TextView datePickTv;
    @BindView(R.id.tv_time_picker)
    TextView timePickTv;
    @BindView(R.id.ib_date_clean)
    ImageButton dateClearBtn;
    @BindView(R.id.ib_time_clean)
    ImageButton timeCleanBtn;
    @BindView(R.id.ib_delete)
    ImageButton deleteBtn;

    private static final String DATE_KEY = "saved_date";
    private static final String TIME_KEY = "saved_time";
    private static final int DATABASE_TRUE = 1;
    private static final int DATABASE_FALSE = 0;

    private int hasTime = DATABASE_FALSE;

    private int itemId;
    private int pickedYear;
    private int pickedMonth;
    private int pickedDay;
    private int pickedHour;
    private int pickedMinute;

    public EditTimeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_time, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            itemId = bundle.getInt(EditActivity.EXTRA_ITEM_ID, -1);
            String task = bundle.getString(EditActivity.EXTRA_VOICE_INPUT);
            if (itemId > -1) {
                setupItemInfo(itemId);
            }
            if (task != null) {
                taskEt.setText(task);
                TimeRecognition timeRecognition = new TimeRecognition(task);
                pickedYear = timeRecognition.getPickedYear();
                pickedMonth = timeRecognition.getPickedMonth();
                pickedDay = timeRecognition.getPickedDay();
                pickedHour = timeRecognition.getPickedHour();
                pickedMinute = timeRecognition.getPickedMinute();
                hasTime = timeRecognition.hasTime() ? 1 : 0;
                if (timeRecognition.hasDate()) {
                    datePickTv.setText(TimeUtil.dateDisplay(pickedYear, pickedMonth, pickedDay));
                    dateClearBtn.setVisibility(View.VISIBLE);
                }
                if (timeRecognition.hasTime()) {
                    timePickTv.setText(TimeUtil.timeDisplay(pickedHour, pickedMinute));
                    timePickTv.setVisibility(View.VISIBLE);
                    dateClearBtn.setVisibility(View.VISIBLE);
                    timeCleanBtn.setVisibility(View.VISIBLE);
                }
            }
        }
        return view;
    }

    private void setupItemInfo(int itemId) {
        Cursor cursor = queryData(itemId);
        cursor.moveToFirst();
        String task = cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_TASK));
        long millisecond = cursor.getLong(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_MILLISECOND));
        hasTime = cursor.getInt(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_HAS_TIME));
        int taskDone = cursor.getInt(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_TASK_DONE));

        taskEt.setText(task);
        deleteBtn.setVisibility(View.VISIBLE);

        if (millisecond > 0) {
            DateAndTime dateAndTime = TimeUtil.millisecondToDateAndTime(millisecond);
            pickedYear = dateAndTime.getYear();
            pickedMonth = dateAndTime.getMonth();
            pickedDay = dateAndTime.getDay();
            pickedHour = dateAndTime.getHour();
            pickedMinute = dateAndTime.getMinute();

            dateClearBtn.setVisibility(View.VISIBLE);
            timePickTv.setVisibility(View.VISIBLE);
            String dateString = TimeUtil.dateDisplay(pickedYear, pickedMonth, pickedDay);
            String timeString = TimeUtil.timeDisplay(pickedHour, pickedMinute);

            datePickTv.setText(dateString);
            if (hasTime == DATABASE_TRUE) {
                timeCleanBtn.setVisibility(View.VISIBLE);
                timePickTv.setText(timeString);
            }
        }
    }

    private Cursor queryData(int itemId) {
        String[] projection = new String[]{TimeEntry._ID,
                TimeEntry.COLUMN_NAME_TASK,
                TimeEntry.COLUMN_NAME_MILLISECOND,
                TimeEntry.COLUMN_NAME_HAS_TIME,
                TimeEntry.COLUMN_NAME_TASK_DONE};

        Uri uri = Uri.withAppendedPath(TimeEntry.CONTENT_URL, String.valueOf(itemId));
        return getContext().getContentResolver().query(uri, projection, null, null, null, null);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DATE_KEY)) {
                datePickTv.setText(savedInstanceState.getString(DATE_KEY));
                dateClearBtn.setVisibility(View.VISIBLE);
                timePickTv.setVisibility(View.VISIBLE);
            }
            if (savedInstanceState.containsKey(TIME_KEY)) {
                timePickTv.setText(savedInstanceState.getString(TIME_KEY));
                timeCleanBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.tv_date_picker)
    public void datePick() {
        final Calendar currentCalender = Calendar.getInstance();
        int year = currentCalender.get(Calendar.YEAR);
        int month = currentCalender.get(Calendar.MONTH);
        int day = currentCalender.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        pickedYear = year;
                        pickedMonth = monthOfYear;
                        pickedDay = dayOfMonth;
                        datePickTv.setText(TimeUtil.dateDisplay(year, monthOfYear, dayOfMonth));
                        dateClearBtn.setVisibility(View.VISIBLE);
                        timePickTv.setVisibility(View.VISIBLE);
                    }
                },
                year,
                month,
                day);
        datePickerDialog.show();
    }

    @OnClick(R.id.ib_date_clean)
    public void dateClean() {
        if (timePickTv != null) {
            timePickTv.setVisibility(View.GONE);
        }
        if (timeCleanBtn != null) {
            timeCleanBtn.setVisibility(View.GONE);
        }
        hasTime = DATABASE_FALSE;
        dateClearBtn.setVisibility(View.GONE);
        datePickTv.setText(null);
        pickedYear = 0;
        pickedMonth = 0;
        pickedDay = 0;
    }

    @OnClick(R.id.tv_time_picker)
    public void timePick() {
        final Calendar currentCalender = Calendar.getInstance();
        int hour = currentCalender.get(Calendar.HOUR_OF_DAY);
        int minute = currentCalender.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        pickedHour = hourOfDay;
                        pickedMinute = minute;
                        timePickTv.setText(TimeUtil.timeDisplay(hourOfDay, minute));
                        hasTime = DATABASE_TRUE;
                        timeCleanBtn.setVisibility(View.VISIBLE);
                    }
                },
                hour,
                minute,
                false);
        timePickerDialog.show();
    }

    @OnClick(R.id.ib_time_clean)
    public void timeClean() {
        timePickTv.setText(null);
        timeCleanBtn.setVisibility(View.GONE);
        pickedHour = 0;
        pickedMinute = 0;
        hasTime = DATABASE_FALSE;
    }

    @OnClick(R.id.ib_delete)
    public void deleteItem() {
        Uri uri = Uri.withAppendedPath(TimeEntry.CONTENT_URL, String.valueOf(itemId));
        getContext().getContentResolver().delete(uri, null, null);
        getActivity().finish();
        showToast(getString(R.string.toast_reminder_delete));
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

    //save or update data
    private void saveTask() {
        String task = taskEt.getText().toString().trim();

        if (!TextUtils.isEmpty(task)) {
            ContentValues values = addContentValues(task);

            if (itemId > 0) {
                Uri uri = Uri.withAppendedPath(TimeEntry.CONTENT_URL, String.valueOf(itemId));
                getContext().getContentResolver().update(uri, values, null, null);
            } else {
                getContext().getContentResolver().insert(TimeEntry.CONTENT_URL, values);
            }
            getActivity().finish();
            showToast(getString(R.string.toast_reminder_saved));
        } else {
            showToast(getString(R.string.toast_please_add_a_task));
        }
    }

    private ContentValues addContentValues(String task) {
        Date date = new Date(pickedYear - 1900, pickedMonth, pickedDay, pickedHour, pickedMinute);
        long timeInMilliseconds = date.getTime();

        ContentValues values = new ContentValues();
        values.put(TimeEntry.COLUMN_NAME_TASK, task);
        values.put(TimeEntry.COLUMN_NAME_MILLISECOND, timeInMilliseconds);
        values.put(TimeEntry.COLUMN_NAME_HAS_TIME, hasTime);
        values.put(TimeEntry.COLUMN_NAME_TASK_DONE, DATABASE_FALSE);
        return values;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String date = datePickTv.getText().toString();
        String time = timePickTv.getText().toString();
        if (!TextUtils.isEmpty(date)) {
            outState.putString(DATE_KEY, date);
        }
        if (!TextUtils.isEmpty(time)) {
            outState.putString(TIME_KEY, time);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
