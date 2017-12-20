package com.example.r30_a.testlayout.cellBean;

import android.app.Application;
import android.content.Context;

/**
 * Created by R30-A on 2017/12/14.
 */

public class TestApplication extends Application {
private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){return context;}
}
