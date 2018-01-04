package com.dbs.omni.tw.util.http.responsebody;

import com.dbs.omni.tw.util.http.mode.setting.DownloadImageData;
import com.dbs.omni.tw.util.http.mode.setting.AddressDetail;
import com.dbs.omni.tw.util.http.mode.setting.PhoneDetail;
import com.dbs.omni.tw.util.http.mode.setting.UserInfoDetail;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siang on 2017/4/11.
 */

public class SettingResponseBodyUtil {

    /**
     * UserInfoDetail
     */
    public static UserInfoDetail getUserInfoDetail(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), UserInfoDetail.class);
    }

    /**
     * PhoneDetail
     */
    public static PhoneDetail getPhoneDetail(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), PhoneDetail.class);
    }

    /**
     * AddressDetail
     */
    public static AddressDetail getAddressDetail(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), AddressDetail.class);
    }

    /**
     * 回傳userCode
     */
    public static String getUserCode(JSONObject object){
        String string = "";
        try {
            string = object.getString("userCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * getDownloadImage
     */
    public static DownloadImageData getDownloadImage(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), DownloadImageData.class);
    }

    /**
     * 回傳fplStatus
     */
    public static String getFplStatus(JSONObject object){
        String string = "";
        try {
            string = object.getString("fplStatus");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * devUUID
     */
    public static String getDevUUID(JSONObject object){
        String string = "";
        try {
            string = object.getString("devUUID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }
}

