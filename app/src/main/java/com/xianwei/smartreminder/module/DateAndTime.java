package com.xianwei.smartreminder.module;

/**
 * Created by xianwei li on 10/23/2017.
 */

public class DateAndTime {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public DateAndTime(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
