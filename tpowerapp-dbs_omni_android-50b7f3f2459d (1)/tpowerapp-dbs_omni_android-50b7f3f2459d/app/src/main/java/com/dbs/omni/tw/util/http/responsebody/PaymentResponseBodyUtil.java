package com.dbs.omni.tw.util.http.responsebody;

import com.dbs.omni.tw.util.http.mode.payment.ConvenientStoreBarcodeData;
import com.dbs.omni.tw.util.http.mode.payment.DBSAccountData;
import com.dbs.omni.tw.util.http.mode.payment.DBSPaymentData;
import com.dbs.omni.tw.util.http.mode.payment.OtherBankData;
import com.dbs.omni.tw.util.http.mode.payment.OtherBankPaymentData;
import com.dbs.omni.tw.util.http.mode.payment.PreLoginPaymentData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sherman-thinkpower on 2017/6/21.
 */

public class PaymentResponseBodyUtil {
    /**
     * ConvenientStoreBarcodeData
     */
    public static ConvenientStoreBarcodeData GetConvenientStoreBarcode(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), ConvenientStoreBarcodeData.class);
    }

    /**
     * getDBSAccount
     */
    public static ArrayList<DBSAccountData> getDBSAccount(JSONObject object){
        ArrayList<DBSAccountData> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("acctList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<DBSAccountData>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * getOtherBankList
     */
    public static ArrayList<OtherBankData> getOtherBankList(JSONObject object){
        ArrayList<OtherBankData> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("bankList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<OtherBankData>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * getDBSPaymentData
     */
    public static DBSPaymentData getDBSPaymentData(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), DBSPaymentData.class);
    }

    /**
     * getOtherBankPaymentData
     */
    public static OtherBankPaymentData getOtherBankPaymentData(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), OtherBankPaymentData.class);
    }

    /**
     * PreLoginPaymentData
     */
    public static PreLoginPaymentData getPreLoginPaymentData(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), PreLoginPaymentData.class);
    }
}

