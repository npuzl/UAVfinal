package com.dji.uavapp.base;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import com.dji.uavapp.util.OnDJIUSBAttachedReceiver;
import com.secneo.sdk.Helper;

import static com.dji.uavapp.constant.Constant.ACCESSORY_ATTACHED;

public class MApplication extends Application {
    private DJIApplication djiApplication;


    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
        if (djiApplication == null) {
            djiApplication = new DJIApplication();
            djiApplication.setContext(this);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        djiApplication.onCreate();
        BroadcastReceiver br = new OnDJIUSBAttachedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACCESSORY_ATTACHED);
        registerReceiver(br, filter);
    }
}