package com.dbs.omni.tw.util.http.responsebody;

import com.dbs.omni.tw.util.http.mode.home.CreditCardData;
import com.dbs.omni.tw.util.http.mode.home.UnBilledDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by siang on 2017/4/11.
 */

public class HomeResponseBodyUtil {

    /**
     * ccList
     */
    public static ArrayList<CreditCardData> getCreditCardList(JSONObject object){
        ArrayList<CreditCardData> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("ccList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<CreditCardData>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * unbillTXDetails
     */
    public static ArrayList<UnBilledDetail> getUnbillTXDetails(JSONObject object){
        ArrayList<UnBilledDetail> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("unbillTXDetail");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<UnBilledDetail>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * ADLinkURL
     */
    public static String getADLinkURL(JSONObject object){
        String opaque = "";
        try {
            opaque = object.getString("ADLinkURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return opaque;
    }
}
