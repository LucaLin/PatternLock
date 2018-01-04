package com.dbs.omni.tw.util.http.listener;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.dbs.omni.tw.util.http.HttpUtilBase;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonResponseListener implements Response.Listener<JSONObject>{

    private static final String TAG = "JsonResponseListener";

    private String apiName;

    private ResponseListener resultListener;
    public JsonResponseListener(String apiName, ResponseListener resultListener){
        this.apiName = apiName;
        this.resultListener = resultListener;
    }

    @Override
    public void onResponse(JSONObject response) {
        ResponseResult responseResult = new ResponseResult();
        try {
            responseResult.setApiName(apiName);
            // 取得response物件
//            JSONObject objectResponse = response.getJSONObject("");
            // 取得header
            JSONObject objectHeader = response.getJSONObject("header");
            Log.d(TAG, apiName + " header = " + objectHeader.toString());
            responseResult.setTxSN(objectHeader.getString("txSN"));
            responseResult.setTxID(objectHeader.getString("txID"));
            responseResult.setTxDate(objectHeader.getString("txDate"));
            if(objectHeader.get("sessionID") == null) {
                responseResult.setSessionID("");
            } else {
                responseResult.setSessionID(objectHeader.getString("sessionID"));
            }
            responseResult.setLang(objectHeader.getString("lang"));
            responseResult.setChannel(objectHeader.getString("channel"));
            responseResult.setReturnCode(objectHeader.getString("returnCode"));
            responseResult.setReturnMessage(objectHeader.getString("returnMessage"));
//            if(TextUtils.isEmpty(HttpUtilBase.sessionID) && apiName.equals("ccd000108")) {

            String responseSessionID = objectHeader.getString("sessionID");
            if(responseResult.getReturnCode().equalsIgnoreCase(ResponseResult.RESULT_SUCCESS)) {
                if(TextUtils.isEmpty(HttpUtilBase.sessionID)) {
                    if (TextUtils.isEmpty(responseSessionID) || responseSessionID.equals("null")) {
                        HttpUtilBase.sessionID = "";
                    } else {
                        HttpUtilBase.sessionID = responseResult.getSessionID();
                    }
                } else {
                    if (!(TextUtils.isEmpty(responseSessionID) || responseSessionID.equals("null"))) {
                        HttpUtilBase.sessionID = responseResult.getSessionID();
                    }

                }

            }
//            }

            // 取得body
            JSONObject objectBody = response.getJSONObject("body");
            Log.d(TAG, "body = " + objectBody.toString());
            responseResult.setBody(objectBody);
        } catch (JSONException e) {
            e.printStackTrace();
            responseResult.setReturnCode(ResponseResult.RESULT_JSON_ERROR);
            responseResult.setReturnMessage("資料解析發生錯誤，請稍候再試。");
        }
        if(resultListener != null) {
            resultListener.onResponse(responseResult);
        }
    }
}
