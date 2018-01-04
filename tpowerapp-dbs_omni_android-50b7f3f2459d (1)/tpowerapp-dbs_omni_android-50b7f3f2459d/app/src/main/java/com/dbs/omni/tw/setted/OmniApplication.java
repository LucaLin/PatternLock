package com.dbs.omni.tw.setted;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.dbs.omni.tw.model.weather.WeatherInfo;
import com.dbs.omni.tw.util.http.mode.bill.BillOverview;
import com.dbs.omni.tw.util.http.mode.register.LoginData;

/**
 * Created by siang on 2017/4/24.
 */

public class OmniApplication extends MultiDexApplication {

    public static Location sCurrentLocation;
    public static WeatherInfo sWallpaperInfo;
    public static LoginData sLoginData;
    public static BillOverview sBillOverview = new BillOverview(); // 帳單總覽
    public static String sPCode;
    public static String sRCode;
    public static String sUCode;

    public static String sIPAddress = "127.0.0.1";


    public interface OnLocationListerent {
        void onLocationChanged(Location location);
    }
    public static OnLocationListerent onLocationListerent;

    public static void setOnLocationListerent(OnLocationListerent listerent) {
        onLocationListerent = listerent;
    }

    private static boolean mGetService = false;     //是否已開啟定位服務
    private static String mProvider = null;
    private static LocationManager mLocationManager;
//    private static WeatherHttpUtil mWeatherTask;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        //測試service來檢查app被關閉
//        Intent intent = new Intent(this, OnClearFromRecentService.class);
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        startService(intent);
        // Bind to the service
//        bindService(new Intent(mContext, OnClearFromRecentService.class),
//                mConnection, Context.BIND_AUTO_CREATE);
//        locationServiceInitial();

    }

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//       @Override
//       public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.d("APP", "onServiceConnected");
//       }
//
//       @Override
//       public void onServiceDisconnected(ComponentName name) {
//          Log.d("APP", "onServiceDisconnected");
//           PreferenceUtil.setIsLogin(mContext, false);
//       }
//   };
//region Get GPS and  天氣

    public static void removeGPS() {
        if(mLocationManager != null && mLocationListener != null) {
            mGetService = false;
            mLocationManager.removeUpdates(mLocationListener);
            mLocationManager = null;
        }
    }


    public static void locationServiceInitial() {
        if(mGetService) {
            return ;
        }
        mGetService = true; //確認開啟定位服務


        LocationManager status = (LocationManager) (mContext.getSystemService(Context.LOCATION_SERVICE));
        if(!(status.isProviderEnabled(LocationManager.GPS_PROVIDER)|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))
        {
//            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
            return ;
        }

        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE); //取得系統定位服務
         /*做法一,由程式判斷用GPS_provider
           if (lms.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
               location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);  //使用GPS定位座標
         }
         else if ( lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
         { location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //使用GPS定位座標
         }
         else {}*/

        // 做法二,由Criteria物件判斷提供最準確的資訊
        Criteria criteria = new Criteria();  //資訊提供者選取標準
        mProvider = mLocationManager.getBestProvider(criteria, true);    //選擇精準度最高的提供者
        if(mProvider != null) {
            mLocationManager.requestLocationUpdates(mProvider, 5000, 0, mLocationListener);
            sCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }

    }

    private static LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            sCurrentLocation = location;

            if(onLocationListerent != null ) {
                onLocationListerent.onLocationChanged(sCurrentLocation);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public static Location getsCurrentLocation() {

        if (mLocationManager != null && mGetService ) {
            sCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            locationServiceInitial();
        }

        return sCurrentLocation;
    }
//endregion

}
