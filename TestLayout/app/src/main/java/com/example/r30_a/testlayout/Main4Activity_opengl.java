package com.example.r30_a.testlayout;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Main4Activity_opengl extends AppCompatActivity {
    sampleGL sampleGL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4_opengl);


        //先檢查手機是否支援openGL ES2.0
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        if(supportsEs2){
            //如果有支援的話，就用sampleGL的物件當成app畫面
            sampleGL = new sampleGL(this);
            sampleGL.setEGLContextClientVersion(2);
            sampleGL.setRenderer(sampleGL);
            setContentView(sampleGL);
        }else{
            setContentView(R.layout.activity_main4_opengl);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        sampleGL.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sampleGL.onResume();
    }

}
