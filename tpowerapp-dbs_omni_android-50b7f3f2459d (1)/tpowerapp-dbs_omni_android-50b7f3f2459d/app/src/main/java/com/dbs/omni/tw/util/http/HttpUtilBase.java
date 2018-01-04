package com.dbs.omni.tw.util.http;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


import com.android.volley.RequestQueue;
import com.dbs.omni.tw.BuildConfig;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.setted.OmniApplication;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.sharedMethods;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * 基礎Class，後續再依模組(錢包、儲值、紅包、信用卡)實作延伸　Http Request。
 * Http Request 有可能一支API 分別用在不同的地方，所以集中於此.
 *
 * Created by oster on 2016/1/8.
 */
public class HttpUtilBase {


    static final int TIMEOUT = 60 * 1000;
    static final int MAX_RETRY_TIMES = -1; // 預設為完全不retry

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public static RequestQueue mQueue;

    static final String url = GlobalConst.UseOfficialServer ? GlobalConst.OFFICIAL_SERVER_URL : GlobalConst.MOCK_SERVER_URL;

    // 手機os版本 ex: android 4.4 iso 9.1
    static String deviceVersion = Build.VERSION.RELEASE;
    // APP版本 ex: 1.3.1 (這邊讀取app的build.gradle所設定的值)
    static String appVersion = BuildConfig.VERSION_NAME;
    //Android ID
    static String deviceUUID = "forInternalTest_deviceID";
    // (push Token)
    static String pushToken ="";

    public static String sessionID = "";
    // 儲值系統token(10分鐘過期)

    static JsonObject addRequestHeader(Context context, String apiName, JsonObject bodyParam) throws JSONException {

        String txDate = DATE_FORMAT.format(new java.util.Date());

        JsonObject tmpHeaderParam = new JsonObject();


        String txSN = randomTxSN();
        tmpHeaderParam.addProperty("txSN", txSN);
        tmpHeaderParam.addProperty("txDate",txDate);
        // API代碼
        tmpHeaderParam.addProperty("txID", apiName.toUpperCase());
        tmpHeaderParam.addProperty("channel", "CCDTM");
        tmpHeaderParam.addProperty("lang", getSystemLocal(context));

        tmpHeaderParam.addProperty("deviceVersion",deviceVersion);
        tmpHeaderParam.addProperty("appVersion", appVersion);
        tmpHeaderParam.addProperty("deviceType", "android");
        tmpHeaderParam.addProperty("deviceBrand", Build.MANUFACTURER);
        tmpHeaderParam.addProperty("deviceModel", Build.MODEL); //TODO 加function

        String tmpUniqueValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(!TextUtils.isEmpty(tmpUniqueValue)){
            deviceUUID = tmpUniqueValue;
        }else{
            deviceUUID = "forInternalTest_uniqueValue";
        }
        //Android ID
        tmpHeaderParam.addProperty("deviceUUID", deviceUUID);

        String userAgent = deviceVersion + appVersion + "android" + Build.MANUFACTURER + Build.MODEL + deviceUUID;
        tmpHeaderParam.addProperty("userAgent", userAgent); //deviceVersion + appVersion + deviceType + deviceBrand + deviceModel +deviceUUID  = userAgent

        tmpHeaderParam.addProperty("pushToken", PreferenceUtil.getPushToken(context));

        //由server產生
        tmpHeaderParam.addProperty("sessionID", sessionID);

        tmpHeaderParam.addProperty("userIP", OmniApplication.sIPAddress);

        if(apiName.equalsIgnoreCase(SettingHttpUtil.uploadPictureApiName)) {
            String stringBody = bodyParam.toString().replace("\\", "");
            tmpHeaderParam.addProperty("signCode", getSignCode(txSN, apiName.toUpperCase(), stringBody));
        } else {
            tmpHeaderParam.addProperty("signCode", getSignCode(txSN, apiName.toUpperCase(), bodyParam.toString()));
        }


        return tmpHeaderParam;
    }

    public static JsonObject getRequestParam(String apiName, JsonObject bodyParam, Context context) throws JSONException {
        JsonObject tmpReqParam = new JsonObject();
        tmpReqParam.add("header", addRequestHeader(context, apiName, bodyParam));
        tmpReqParam.add("body", bodyParam);

        Log.d(apiName + " Request", tmpReqParam.toString());

        return tmpReqParam;
    }

    /**
     * return String[], [0]是beginDate, [1]是endDate
     */
    static String[] getMonthBeginEndDate(MonthOption monthOption){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        // 如果是兩個月，起始時間為上個月1號，結束日期為今天
        if(monthOption == MonthOption.LATEST_2_MONTH){
            String[] dates = new String[2];
            // endDate
            dates[1] = df.format(c.getTime());
            // beginDate
            c.add(Calendar.MONTH, -1);
            c.set(Calendar.DAY_OF_MONTH, 1);
            dates[0] = df.format(c.getTime());
            return dates;
        // 否則不帶日期參數
        }else{
            return null;
        }
    }

    public enum MonthOption {
        LATEST_1_MONTH,
        LATEST_2_MONTH,
        NA
    }


    //cancel queue Request for tag
    public static void cancelQueue(String tag) {
        if (mQueue != null) {
            mQueue.cancelAll(tag);
        }
    }

    public static void safeClose(FileOutputStream fileOutputStream) {
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                Log.e("FileOutputStream Close", "close crash");
            }
        }
    }

    private static String getSystemLocal(Context context) {
        Locale systemLocale = context.getResources().getConfiguration().locale;
        String lang = systemLocale.getLanguage();
        if(lang.equalsIgnoreCase("en_US")) {
            return "en-US";
        } else {
            return "zh-TW";
        }
    }


    private static String getSignCode(String txSN, String txID, String body) {
        String code = "DBS123456789";
        String string = txSN + txID + code + body;

        String sha256String = sharedMethods.SHA256Encrypt(string);

        return sha256String.toUpperCase();
    }

    private static String randomTxSN(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        Calendar calendar = Calendar.getInstance();

        SecureRandom random = new SecureRandom();
        random.setSeed(System.currentTimeMillis());
        String randomNum = null;

        randomNum = String.valueOf(random.nextInt(99999999));

        while(randomNum.length() < 8){
            randomNum = "0" + randomNum;
        }
        return simpleDateFormat.format(calendar.getTime()) + randomNum;
    }

    public static String getApiUrl(String apiName) {
        String apiUrl =  url + String.format("%1$s/%2$s/", apiName.substring(0,5), apiName.substring(5,7)) + apiName;

        return apiUrl;
    }



    //region init api header
    public static void initApiHeader(final Context context) {
        if(OmniApplication.sIPAddress.equals("127.0.0.1")) {
            NetworkUtil.IPAddressHttpUtil ipAddressHttpUtil = new NetworkUtil.IPAddressHttpUtil();
            ipAddressHttpUtil.setOnIPAddressListener(new NetworkUtil.IPAddressHttpUtil.OnIPAddressListener() {
                @Override
                public void OnFinish(String IP) {
//                    cellGetSessionID(context);
                }
            });
            ipAddressHttpUtil.execute();
        } else {
//            cellGetSessionID(context);
        }

    }
//    private static void cellGetSessionID(Context context) {
//        //若已經存在將不執行
//        if(!TextUtils.isEmpty(HttpUtilBase.sessionID) ) {
//            return;
//        }
//
//        // 如果沒有網路連線，顯示提示對話框
//        if (NetworkUtil.isConnected(context)) {
//            try {
//                GeneralHttpUtil.getSessionID(null, context, "");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    //regioned
}
