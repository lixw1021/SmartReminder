package com.xianwei.smartreminder.util;

import android.text.TextUtils;
import android.util.Log;

import com.xianwei.smartreminder.module.DateAndTime;
import com.xianwei.smartreminder.module.TimeReminder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xianwei li on 10/19/2017.
 */

public class TimeUtil {

    public static String timeDisplay(int hourOfDay, int minutes) {
        String minuteString;
        if (minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = String.valueOf(minutes);
        }

        if (hourOfDay > 12) {
            return (hourOfDay - 12) + ":" + minuteString + " PM";
        } else if (hourOfDay == 12){
            return hourOfDay + ":" + minuteString + " PM";
        } else {
            return hourOfDay + ":" + minuteString + " AM";
        }
    }

    public static String dateDisplay(int year, int month, int day) {
        if (year == 0) return null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        Date date = new Date(year - 1900, month, day);

        return simpleDateFormat.format(date);
    }

    public static DateAndTime millisecondToDateAndTime(long milliSeconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new DateAndTime(year, month, day, hour, minute);
    }
}
