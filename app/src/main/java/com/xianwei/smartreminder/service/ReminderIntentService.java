package com.xianwei.smartreminder.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by xianwei li on 10/25/2017.
 */

public class ReminderIntentService extends IntentService {

    public ReminderIntentService() {
        super("ReminderIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ReminderTasks.executeTask(this, intent);
    }
}
