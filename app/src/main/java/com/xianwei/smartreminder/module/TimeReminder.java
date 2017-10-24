package com.xianwei.smartreminder.module;

/**
 * Created by xianwei li on 10/19/2017.
 */

public class TimeReminder {
    private String task;
    private long millisecond;
    private boolean hasTime;
    private boolean done;

    public TimeReminder(String task, long millisecond, boolean hasTime, boolean done) {
        this.task = task;
        this.millisecond = millisecond;
        this.hasTime = hasTime;
        this.done = done;
    }

    public String getTask() {
        return task;
    }

    public long getMillisecond() {
        return millisecond;
    }

    public boolean isHasTime() {
        return hasTime;
    }

    public boolean isDone() {
        return done;
    }
}
