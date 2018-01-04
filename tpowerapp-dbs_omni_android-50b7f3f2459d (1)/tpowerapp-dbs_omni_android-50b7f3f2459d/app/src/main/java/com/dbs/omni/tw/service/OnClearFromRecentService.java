package com.dbs.omni.tw.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by siang on 2017/6/22.
 * 用來檢測app 被關閉
 *
 */

public class OnClearFromRecentService extends Service {

    public class LocalBinder extends Binder {
        public OnClearFromRecentService getService() {
              return OnClearFromRecentService.this;
        }
     }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "onBind " + intent);
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("ClearFromRecentService", "Service Destroyed");
//        PreferenceUtil.setIsLogin(this, false);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Log.e("ClearFromRecentService", "END");
        super.onTaskRemoved(rootIntent);
        //Code here
//        stopSelf();
    }
}