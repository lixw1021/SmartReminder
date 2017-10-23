package com.xianwei.smartreminder.module;

/**
 * Created by xianwei li on 10/19/2017.
 */

public class TimeReminder {
    private String task;
    private String date;
    private String time;
    private boolean done;

    public TimeReminder(String task, String date, String time, boolean done) {
        this.task = task;
        this.date = date;
        this.time = time;
        this.done = done;
    }

    public String getTask() {
        return task;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isDone() {
        return done;
    }
}
