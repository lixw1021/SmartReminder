package com.xianwei.smartreminder.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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

import com.xianwei.smartreminder.R;
import com.xianwei.smartreminder.data.ReminderContract.TimeEntry;
import com.xianwei.smartreminder.util.TimeUtil;

import org.xml.sax.DTDHandler;

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
    EditText timeEt;
    @BindView(R.id.tv_date_picker)
    TextView datePickTv;
    @BindView(R.id.tv_time_picker)
    TextView timePickTv;
    @BindView(R.id.ib_data_clean)
    ImageButton dataClearBtn;
    @BindView(R.id.ib_time_clean)
    ImageButton timeCleanBtn;

    private static String DATE_KEY = "date";
    private static String TIME_KEY = "time";
    private static int DATABASE_TRUE = 1;
    private static int DATABASE_FALSE = 0;
    private boolean hasTime = false;

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
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DATE_KEY)) {
                datePickTv.setText(savedInstanceState.getString(DATE_KEY));
                dataClearBtn.setVisibility(View.VISIBLE);
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
                        datePickTv.setText(TimeUtil.dateFormat(year, monthOfYear, dayOfMonth));
                        dataClearBtn.setVisibility(View.VISIBLE);
                        timePickTv.setVisibility(View.VISIBLE);
                    }
                },
                year,
                month,
                day);
        datePickerDialog.show();
    }

    @OnClick(R.id.ib_data_clean)
    public void dateClean() {
        if (timePickTv != null) {
            timePickTv.setVisibility(View.GONE);
        }
        if (timeCleanBtn != null) {
            timeCleanBtn.setVisibility(View.GONE);
        }
        hasTime = false;
        dataClearBtn.setVisibility(View.GONE);
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
                        timePickTv.setText(TimeUtil.timeFormat(hourOfDay, minute));
                        hasTime = true;
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
        hasTime = false;
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
        //extract information
        String task = timeEt.getText().toString().trim();
        Date date = new Date(pickedYear - 1900, pickedMonth, pickedDay, pickedHour, pickedMinute);
        long timeInMilliseconds = date.getTime();

        if (!TextUtils.isEmpty(task)) {
            // insert data into database
            ContentValues values = new ContentValues();
            values.put(TimeEntry.COLUMN_NAME_TASK, task);
            values.put(TimeEntry.COLUMN_NAME_MILLISECOND, timeInMilliseconds);
            values.put(TimeEntry.COLUMN_NAME_HAS_TIME, hasTime ? DATABASE_TRUE : DATABASE_FALSE);
            values.put(TimeEntry.COLUMN_NAME_TASK_DONE, DATABASE_FALSE);
            getContext().getContentResolver().insert(TimeEntry.CONTENT_URL, values);
            getActivity().finish();
            Toast.makeText(getContext(), "Reminder Saved", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Please add a task", Toast.LENGTH_LONG).show();
        }

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
}
