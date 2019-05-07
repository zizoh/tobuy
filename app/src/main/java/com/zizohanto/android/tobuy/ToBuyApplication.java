package com.zizohanto.android.tobuy;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.zizohanto.android.tobuyList.BuildConfig;
import com.zizohanto.android.tobuyList.R;

import timber.log.Timber;

public class ToBuyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Stetho.initializeWithDefaults(this);
            MobileAds.initialize(this, getString(R.string.ad_id));
        }
    }
}
