package com.xianwei.smartreminder.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by xianwei li on 11/2/2017.
 */

public class TimeReminderWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ReminderRemoteViewsFactory(this.getApplicationContext());
    }
}
