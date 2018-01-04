package com.dbs.omni.tw.util.http.responsebody;

import com.dbs.omni.tw.util.http.mode.SSOData;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siang on 2017/4/11.
 */

public class GeneralResponseBodyUtil {


    /**
     * 取得SSO Key
     */
    public static SSOData getSSOData(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), SSOData.class);
    }

    /**
     * opaque
     */
    public static String getOpaque(JSONObject object){
        String opaque = "";
        try {
            opaque = object.getString("opaque");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return opaque;
    }

    /**
     * phoneNumber
     */
    public static String getPhoneNumber(JSONObject object){
        String phoneNumber = "";
        try {
            phoneNumber = object.getString("phoneNumber");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }

    /**
     * Email
     */
    public static String getEmail(JSONObject object){
        String email = "";
        try {
            email = object.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return email;
    }


    /**
     * content
     */
    public static String getContent(JSONObject object){
        String string = "";
        try {
            string = object.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * pWebURL
     */
    public static String getpWebURL(JSONObject object){
        String string = "";
        try {
            string = object.getString("pWebURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }


    /**
     * 回傳extension
     */
    public static String getExtension(JSONObject object){
        String extension = "";
        try {
            extension = object.getString("extension");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return extension;
    }
}
