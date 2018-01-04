package com.dbs.omni.tw.util.http.responsebody;

import com.dbs.omni.tw.util.http.mode.register.LoginData;
import com.dbs.omni.tw.util.http.mode.register.LogoutDate;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siang on 2017/4/11.
 */

public class RegisterResponseBodyUtil {

    /**
     * 取得Login資料
     */
    public static LoginData getLoginDate(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), LoginData.class);
    }


    /**
     * 取得Logout資料
     */
    public static LogoutDate getLogoutDate(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), LogoutDate.class);
    }

    /**
     * 回傳userType
     */
    public static String getUserType(JSONObject object){
        String userType = "";
        try {
            userType = object.getString("userType");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userType;
    }

}
