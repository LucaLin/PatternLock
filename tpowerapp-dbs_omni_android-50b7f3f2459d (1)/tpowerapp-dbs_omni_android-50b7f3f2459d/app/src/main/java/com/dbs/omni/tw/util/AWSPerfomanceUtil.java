package com.dbs.omni.tw.util;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dbseteam.geolocation.utils.DBSAConfig;

import static dbseteam.geolocation.utils.DBSAConfig.INTENT_START_GEO_LOCATION;
import static dbseteam.geolocation.utils.PerformanceInfoParams.EVENT_TYPE;
import static dbseteam.geolocation.utils.PerformanceInfoParams.INTENT_POST_PERFORMANCE_INFO;
import static dbseteam.geolocation.utils.PerformanceInfoParams.*;
/**
 * Created by siang on 2017/6/8.
 */

public class AWSPerfomanceUtil {

    private static Calendar startTime, endTime;

    public static void initialise (Context context
//                                           String deviceUDID, //Device id
//                                           String appIdx,  //exmaple.. SG_MB_1.1.3.4_ANDROID
//                                           String isProduction, // False for UAT and TRUE  for proudction
//                                           String isGeofence, //True if applicable else False
//                                           String isDebug, // TRUE for UAT  and FALSE  for production
//                                           String showLocalNotifications // True for UAT and FAlse for Production
    ) {


        Intent intent = new Intent();

        String tmpUniqueValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        intent.putExtra(DBSAConfig.DEVICE_UDID,  tmpUniqueValue);//Device id
        intent.putExtra(DBSAConfig.APPIDX, "CCDTM_2.2.2.2_Android"); //exmaple.. SG_MB_1.1.3.4_ANDROID
        intent.putExtra(DBSAConfig.PRODUCTOIN_BUILD, "false");// False for UAT and TRUE  for proudction
        intent.putExtra(DBSAConfig.GEOFENCE_ENABLED, "fa");//True if applicable else False
        intent.putExtra(DBSAConfig.DEBUG_ENABLED, "true");// TRUE for UAT  and FALSE  for production
        intent.putExtra(DBSAConfig.SHOW_NOTIFICATION, "true");// True for UAT and FAlse for Production
        intent.setAction(INTENT_START_GEO_LOCATION);

        context.sendBroadcast(intent);
    }

    public static void sendlog (Context context) {

        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, 5);
        Intent intent =  new Intent();
        intent.setAction(INTENT_POST_PERFORMANCE_INFO);

        intent.putExtra(EVENT_TYPE, "frmLogin");
        intent.putExtra(EVENT_CATEGORY_ID,"4");
        intent.putExtra(EVENT_START_TIME, formatTime(startTime.getTime()));
        intent.putExtra(EVENT_END_TIME, formatTime(endTime.getTime()));
        intent.putExtra(EVENT_PLAYLOAD, "");
        intent.putExtra(NETWORK_TYPE, "WIFI");
        intent.putExtra(NETWORK_LATENCY, "");
        intent.putExtra(APPSID, "");
        intent.putExtra(APPIDX, "CCDTM_2.2.2.2_Android");

        context.sendBroadcast(intent);
    }

    private static String formatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String string = format.format(date);
        return string;
    }
}
