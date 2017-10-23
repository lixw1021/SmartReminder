package com.xianwei.smartreminder.util;

import android.text.TextUtils;
import android.util.Log;

import com.xianwei.smartreminder.module.TimeReminder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xianwei li on 10/19/2017.
 */

public class TimeUtil {

    public static String timeFormat(int hourOfDay, int minutes) {
        String minuteString;
        if (minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = String.valueOf(minutes);
        }
        Log.i("1234millisecond time", String.valueOf((hourOfDay*60 + minutes)*60*1000));
        if (hourOfDay > 12) {
            return (hourOfDay - 12) + ":" + minuteString + " PM";
        } else if (hourOfDay == 12){
            return hourOfDay + ":" + minuteString + " PM";
        } else {
            return hourOfDay + ":" + minuteString + " AM";
        }
    }

    public static String dateFormat(int year, int month, int day) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        Date date = new Date(year - 1900, month, day);
        long timeInMilliseconds = date.getTime();
        Log.i("1234millisecond date", String.valueOf(timeInMilliseconds));
        return simpleDateFormat.format(date);
    }

    public static TimeReminder millisecondToDateAndTime(long milliSeconds) {
        if (milliSeconds < 0) return  new TimeReminder(null, null, null, false);;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String date = dateFormat(year, month, day);
        String time = timeFormat(hour, minute);

        return new TimeReminder(null, date, time, false);
    }
}
