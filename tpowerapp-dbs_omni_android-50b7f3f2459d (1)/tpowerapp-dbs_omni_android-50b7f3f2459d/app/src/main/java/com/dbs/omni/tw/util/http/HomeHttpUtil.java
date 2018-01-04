package com.dbs.omni.tw.util.http;


import android.app.Activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dbs.omni.tw.util.http.listener.JsonResponseErrorListener;
import com.dbs.omni.tw.util.http.listener.JsonResponseListener;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通用面的API等等…
 *
 */
public class HomeHttpUtil extends HttpUtilBase {


    /**<h5> CCD020602 取得使用者信用卡資訊 </h5>
     * <request>
     *
     * <response>
     *  ccList	List:
     *  	cardName		string		卡片名稱
     *  	ccNO		    string		信用卡正卡卡號
     *  	ccLogo		    string		信用卡的產品代碼(系統用來辨別哪一張卡片，與PWeb串接會用此代碼)
     *  	expDate		    string		信用卡效期 MMYY
     *  	ccFlag		    string		M：正卡, S：附卡
     *  	ccStatus		string		卡片狀態說明
     *  							    1-Pending activation
     *  							    2-Active
     *  							    3-Hot-tagged
     *  							    4-Lost/Stolen
     *  							    5-Expired
     *  							    6-Cancelled/Closed
     *  							    7-Transferred
     *  							    8-Pending initial PIN change
     *  							    9-Pending PIN reset
     *  							    10-Pending re-issue of card
     *  							    11-Tagged for deletion
     *  							    12-Retained
     *  	ccID			string		卡片ID
     *
     *
     * <error>
     **/
    public static void getCreditCarInfoList(ResponseListener responseListener,
                                                     Activity activity) throws JSONException {
        String apiName = "ccd020602";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();


        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD050101 未登入信用卡開卡 </h5>
     * <request>
     *  @param ccNO	string	信用卡卡號16碼
     *  @param expDate	string	信用卡有效期限 MMYY
     *  @param activatePIN	string	開卡密碼YYYMMDD
     *
     * <response>
     *
     * <error>
     * */
    public static void activeCreditCardForPreLogin(String ccNO, String expDate, String activatePIN, ResponseListener responseListener,
                                      Activity activity) throws JSONException {
        String apiName = "ccd050101";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("ccNO", ccNO);
        tmpBodyParam.addProperty("expDate", expDate);
        tmpBodyParam.addProperty("activatePIN", activatePIN);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD050102 信用卡開卡 </h5>
     * <request>
     *  @param ccID	string	卡片ID
     *  @param ccNO	string	信用卡卡號16碼
     *  @param expDate	string	信用卡有效期限 MMYY
     *  @param activatePIN	string	開卡密碼YYYMMDD
     *
     * <response>
     *
     * <error>
     * */
    public static void activeCreditCard(String ccID, String ccNO, String expDate, String activatePIN, ResponseListener responseListener,
                                                   Activity activity) throws JSONException {
        String apiName = "ccd050102";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("ccID", ccID);
        tmpBodyParam.addProperty("ccNO", ccNO);
        tmpBodyParam.addProperty("expDate", expDate);
        tmpBodyParam.addProperty("activatePIN", activatePIN);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }


//    /**<h5> CCD060101 取得紅利點數 </h5>
//     * <request>
//     *
//     * <response>
//     *  rPoints	    string			Reward points 紅利點數(沒有塞0)
//     *  mPoints	    string			Miles points飛行積金(沒有塞0)
//     *  crPoints    string			Cash rebate points 現金點數(沒有塞0)
//     *
//     * <error>
//     **/
//    public static void getBonusPoint(ResponseListener responseListener,
//                                            Activity activity) throws JSONException {
//        String apiName = "ccd060101";
//        String apiUrl = getApiUrl(apiName);
//        if(mQueue == null)
//            mQueue = Volley.newRequestQueue(activity);
//
//        JSONObject tmpBodyParam = new JSONObject();
//
//
//        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
//        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
//        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);
//
//        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
//        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        request.setTag(activity.getClass().getSimpleName());
//        mQueue.add(request);
//    }


    /**
     * <h5> 1.1	CCD040101 AD Image/Function Link 廣告與功能連結 </h5>
     * <request>
     *  @param location string		廣告版位
     *                              1.	首頁
     *                              2.	本期帳單
     *                              3.	繳款
     *                              4.	電子帳單
     *                              5.	消費分期
     *                              6.	分期歷史紀錄
     *                              7.	線上開卡
     *                              8.	加辦信用卡
     *                              9.	通知設定
     *                              10.	登出頁
     *                              11.	倒數計時頁
     *                              12.	App首頁
     *
     *
     *  @param funcArea	string	功能區塊
     *
     * <response>
     *
     * <error>
     * */
    public static void getADLink(ResponseListener responseListener,
                                 Activity activity) throws JSONException {
        String apiName = "ccd040101";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("location", "12"); //APP only 12
        tmpBodyParam.addProperty("funcArea", "");



        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }
}

