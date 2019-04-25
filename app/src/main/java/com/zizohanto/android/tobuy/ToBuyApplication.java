package com.zizohanto.android.tobuy;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.zizohanto.android.tobuyList.BuildConfig;

import timber.log.Timber;

public class ToBuyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Stetho.initializeWithDefaults(this);
        }
    }
}
