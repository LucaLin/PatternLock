package com.dbs.omni.tw.util.http.responsebody;

import com.dbs.omni.tw.util.http.mode.bill.BillOverview;
import com.dbs.omni.tw.util.http.mode.bill.EBillFileData;
import com.dbs.omni.tw.util.http.mode.bill.PreLoginBillData;
import com.dbs.omni.tw.util.http.mode.bill.UnBillOverview;
import com.dbs.omni.tw.util.http.mode.bill.BilledDetailList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by siang on 2017/4/11.
 */

public class BillResponseBodyUtil {

    /**
     * BillOverview
     */
    public static BillOverview getBilledOverview(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), BillOverview.class);
    }
    /**
     * 取得UnBilledOvervie
     */
    public static UnBillOverview getUnBilledOverview(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), UnBillOverview.class);
    }

    /**
     * BillDetailList
     */
    public static BilledDetailList getBilledDetailList(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), BilledDetailList.class);
    }

    /**
     * EBillFileDataList
     */
    public static ArrayList<EBillFileData> getEBillFileDataList(JSONObject object){
        ArrayList<EBillFileData> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("eBillList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<EBillFileData>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * eBillFile
     */
    public static String getEBillFile(JSONObject object){
        String string = "";
        try {
            string = object.getString("eBillFile");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * BillOverview
     */
    public static PreLoginBillData getPreLoginBillData(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), PreLoginBillData.class);
    }
}

