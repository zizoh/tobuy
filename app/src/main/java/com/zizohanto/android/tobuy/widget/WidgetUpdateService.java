package com.zizohanto.android.tobuy.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import timber.log.Timber;

public class WidgetUpdateService extends Service {

    public WidgetUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TobuyListWidgetProvider.updateWidget(this);
        Timber.d("widget update service started");
        return super.onStartCommand(intent, flags, startId);
    }
}
