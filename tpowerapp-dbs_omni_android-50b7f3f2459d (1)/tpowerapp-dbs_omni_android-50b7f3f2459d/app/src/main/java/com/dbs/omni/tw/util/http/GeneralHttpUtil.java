package com.dbs.omni.tw.util.http;


import android.app.Activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dbs.omni.tw.model.log.SpecialEvent;
import com.dbs.omni.tw.util.http.listener.JsonResponseErrorListener;
import com.dbs.omni.tw.util.http.listener.JsonResponseListener;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.typeMapping.SwitchImageType;
import com.dbs.omni.tw.util.http.typeMapping.TermType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 通用面的API等等…
 *
 */
public class GeneralHttpUtil extends HttpUtilBase {


//    /**<h5> CCD000108 取得認證章 </h5>
//     * <request>
//     *
//     * <response>
//     *
//     * <error>
//     **/
//    public static void getSessionID(ResponseListener responseListener,
//                                    Context context) throws JSONException {
//        String apiName = "ccd000108";
//        String apiUrl = getApiUrl(apiName);
//        if(mQueue == null)
//            mQueue = Volley.newRequestQueue(context);
//
//        JSONObject tmpBodyParam = new JSONObject();
//
//        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, context);
//        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
//        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);
//
//        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
//        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        request.setTag(activity.getClass().getSimpleName());
//        mQueue.add(request);
//    }

    /**<h5> CCD000102 發送OTP/重新發送OTP </h5>
    * <request>
     * @param isLogin  (loginFlag	string		Y:已登入 N:未登入)
    * @param txID          string      需要驗證OTP的交易代碼(eg. CCD010203)
    * @param extension     String      （可選）手機號碼（加密）未登入此欄位為必填
     * @param  email        String     （可選）信箱
     *
    * <response>
    * opaque        string      驗證OTP用 (前端需顯示前四碼在OTP前)
    * phoneNumber   string      行動電話
    *
    * <error>
    **/
    public static void sendOTPRequest(boolean isLogin, String txID, String extension, String email,  ResponseListener responseListener,
                                    Activity activity) throws JSONException {
        String apiName = "ccd000102";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        if (isLogin) {
            tmpBodyParam.addProperty("loginFlag", "Y");
        } else {
            tmpBodyParam.addProperty("loginFlag", "N");
        }

        tmpBodyParam.addProperty("txID",txID);
        tmpBodyParam.addProperty("extension",extension);
        tmpBodyParam.addProperty("email",email);


        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD000103 驗證OTP </h5>
     * <request>
     * @param isLogin  (loginFlag	string		Y:已登入 N:未登入)
     * @param txID		    string		需要驗證OTP的交易代碼(eg. CCD010203)
     * @param otp			string		簡訊動態密碼
     * @param opaque		string		驗證OTP用
     *
     * <response>
     *
     * <error>
     **/
    public static void sendVerifyOTP(boolean isLogin, String txID, String otp, String opaque, ResponseListener responseListener,
                                      Activity activity) throws JSONException {
        String apiName = "ccd000103";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        if (isLogin) {
            tmpBodyParam.addProperty("loginFlag", "Y");
        } else {
            tmpBodyParam.addProperty("loginFlag", "N");
        }

        tmpBodyParam.addProperty("txID",txID);
        tmpBodyParam.addProperty("otp",otp);
        tmpBodyParam.addProperty("opaque",opaque);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD000104 取得條款序號 </h5>
     * <request>
     * @param termType	string		條款類型
     *                      A01:註冊
     *                      A02:使用者帳號查詢
     *                      A03:重設密碼
     *                      A04:eBill(已登入)
     *                      A05:eBill(未登入)
     *                      A06:各項交易分期
     *                      A07:單筆帳單分期
     *                      A08:自動分期設定
     * @param isLogin	    (loginFlag	string		Y:已登入 N:未登入)
     *
     * <response>
     * tcVersionSEQ string      條款table序號
     * <error>
     **/
    public static void getTerms(TermType termType, boolean isLogin, ResponseListener responseListener,
                                Activity activity) throws JSONException {
        String apiName = "ccd000104";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        tmpBodyParam.addProperty("termType",termType.code);

        if (isLogin) {
            tmpBodyParam.addProperty("loginFlag", "Y");

        } else {
            tmpBodyParam.addProperty("loginFlag", "N");
        }

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD000105 取得注意事項 </h5>
     * <request>
     * @param noteType		string		注意事項查詢對應碼：
     * 							P01:繳款  繳款
     * 							P02:繳款  星展銀行帳戶繳款
     * 							P03:繳款  其他銀行帳戶繳款
     * 							P04:繳款  便利商店繳款
     * 							P05:繳款  未登入的其他銀行帳戶繳款
     * 							L01:登入  線上立即註冊Step1
     * 							L02:登入  線上立即註冊Step2
     * 							L03:登入  線上立即註冊Step3
     * 							C01:共用  簡訊動態密碼
     * 							L04:登入  變更密碼
     * 							L05:登入  重設密碼
     * 							L06:登入  重設帳號
     * 							S01:個人化服務 更新個人資料
     * 							M01:消費分期  整筆帳單分期
     * 							M02:消費分期  各項交易分期
     * 							M03:消費分期  自動分期設定
     * @param isLogin		(loginFlag	string		Y:已登入 N:未登入)
     *
     *
     * <response>
     * conStart		    string		前言
     * conEnd			string		結語
     * noteList：		List
     * 	content string	注意事項內容
     *
     * <error>
     **/
    public static void getNotice(String noteType, boolean isLogin, ResponseListener responseListener,
                                       Activity activity) throws JSONException {
        String apiName = "ccd000105";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        tmpBodyParam.addProperty("noteType",noteType);

        if (isLogin) {
            tmpBodyParam.addProperty("loginFlag", "Y");
        } else {
            tmpBodyParam.addProperty("loginFlag", "N");
        }

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD000109 系統維護公告 </h5>
     * <request>
     * loginFlag                        Y:已登入 ; N:未登入
     * <response>
     *
     *  content             string      公告內容
     *
     * <error>
     **/
    public static void getSystemBulletin(ResponseListener responseListener,
                                         Activity activity) throws JSONException {
        String apiName = "ccd000109";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        tmpBodyParam.addProperty("loginFlag","N");

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD000110 系統設定查詢 </h5>
     * <request>
     * @param paraName		string		參數名稱
     * @param isLogin		(loginFlag	string		Y:已登入 N:未登入)
     *
     * <response>
     *  paraList		List :
     * 	    paraName	string		參數名稱
     * 	    paraValue	string		參數值
     *
     * <error>
     **/
    public static void getSystmSetting(String paraName, boolean isLogin, ResponseListener responseListener,
                                    Activity activity) throws JSONException {
        String apiName = "ccd000110";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        tmpBodyParam.addProperty("paraName",paraName);

        if (isLogin) {
            tmpBodyParam.addProperty("loginFlag", "Y");
        } else {
            tmpBodyParam.addProperty("loginFlag", "N");
        }


        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD000111取得功能控制列表 </h5>
     * <request>
     * @param isLogin		(loginFlag	string		Y:已登入 N:未登入)
     *
     * <response>
     *  funcList		List :
     * 	    funcCode	string	功能代碼
     * 	    funcName	string	功能名稱
     * 	    open		string	功能開啟	E-開啟	D-暫停
     * 	    funcType 	string	功能類型	0-主選單	1-次選單
     * 	    funcParent	string	功能主選單 (FUNC_TYPE=1才有)
     * 	    funcLink	string	功能連結
     * 	    funcSort	string	選單排序
     *
     *
     * <error>
     **/

    public static void getFunctionList(boolean isLogin, ResponseListener responseListener,
                                       Activity activity) throws JSONException {
        String apiName = "ccd000111";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        if (isLogin) {
            tmpBodyParam.addProperty("loginFlag", "Y");
        } else {
            tmpBodyParam.addProperty("loginFlag", "N");
        }

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }




    /**
     * <h5> CCD000201  特殊事件紀錄 </h5>
     * <request>
     * specialEventList : [{
     *      eventType,	                    String	type	App自訂事件類型
     *      eventNote                        String	note	App自訂內容
     * }]
     *
     * <response>
     *
     * <error></error>
     * */
    public static void uploadSpecialEvents(ArrayList<SpecialEvent> specialEvents, ResponseListener responseListener,
                                           Activity activity) throws JSONException {
        String apiName = "ccd000201";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        JsonArray specialEventList = new JsonArray();

        if(specialEvents.size() > 0)
        {

            for (SpecialEvent specialEvent: specialEvents) {
                JsonObject object = new JsonObject();
                object.addProperty("eventType", specialEvent.getEventType());
                object.addProperty("eventNote", specialEvent.getEventNote());
                specialEventList.add(object);
            }

        }

        tmpBodyParam.add("specialEventList", specialEventList);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD000202 取得APP版本/強制更版資訊 </h5>
     * <request>
     * @param isLogin		(loginFlag	string		Y:已登入 N:未登入)
     *
     * <response>
     *  appDesc            String    版本說明
     *  appVersion         String    最新版號
     *  forceAppVersion    String    強制更新版本號
     *  appUrl             String    下載網址
     *
     * error:
     * </pre>
     * */
    public static void checkAppUpdate(boolean isLogin, ResponseListener responseListener,
                                           Activity activity) throws JSONException {
        String apiName = "ccd000202";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        if (isLogin) {
            tmpBodyParam.addProperty("loginFlag", "Y");
        } else {
            tmpBodyParam.addProperty("loginFlag", "N");
        }

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD010201 取得TransitID </h5>
     * <request>
     *
     * <response>
     *  transitID           String    SSO跨通路代碼
     *  userCode            String    使用者代碼
     *
     * <error>
     * */
    public static void getTransitID(ResponseListener responseListener,
                                      Activity activity) throws JSONException {
        String apiName = "ccd010201";
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
     * <h5> CCD010202 驗證TransitID </h5>
     * <request>
     *  @param transitID           String    SSO跨通路代碼
     *  @param userCode            String    使用者代碼
     *
     *
     * <response>
     *  channelsAllowed		string 		0 - Both CCDS & RIB
     *  								1 - RIB Only (保留)
     *  								2 - CCDS Only
     *  userType			String 		0 - Both CCDS & RIB
     *  								1 - RIB Only (保留)
     *  								2 - CCDS Only
     *  ccList              List :
     *         cardName		    string	卡片名稱
     *         ccNO			    string	信用卡卡號
     *         ccBrand			string	信用卡品牌
     *         ccLogo			string	信用卡的產品代碼(系統用來辨別哪一張卡片，與PWeb串接會用此代碼)
     *         ccDesc			string	卡片介紹
     *         isVirtualCard	string	是否為虛擬卡
     *         expDate			string	信用卡效期 MMYY
     *         ownerName		string	持有人名稱(本名)
     *         ccFlag			string	M：正卡, S：附卡
     *         ccStatus		    string	卡片狀態說明
     *         						1-Pending activation
     *         						2-Active
     *         						3-Hot-tagged
     *         						4-Lost/Stolen
     *         						5-Expired
     *         						6-Cancelled/Closed
     *         						7-Transferred
     *         						8-Pending initial PIN change
     *         						9-Pending PIN reset
     *         						10-Pending re-issue of card
     *         						11-Tagged for deletion
     *         						12-Retained
     *         ccID			    string	卡片ID
     *
     * <error>
     * */
    public static void verifyTransitID(String transitID, String userCode, ResponseListener responseListener,
                                    Activity activity) throws JSONException {
        String apiName = "ccd010202";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("transitID", transitID);
        tmpBodyParam.addProperty("userCode", userCode);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }


    /**
     * <h5> CCD010203 註冊SSO IB用戶 </h5>
     * <request>
     *  @param  acctNO      String      帳戶號碼
     * <response>
     *
     * <error>
     * */
    public static void registerUserForSSOIB(String acctNO, ResponseListener responseListener,
                                 Activity activity) throws JSONException {
        String apiName = "ccd010302";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        tmpBodyParam.addProperty("acctNO", acctNO);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }


    /**
     * <h5> CCD010302 取得SSO公鑰 </h5>
     * <request>
     *
     * <response>
     *  pKey	string	SSO相關功能使用
     *  rKey	string	SSO相關功能使用
     *
     * <error>
     * */
    public static void getSSOKey(ResponseListener responseListener,
                                    Activity activity) throws JSONException {
        String apiName = "ccd010302";
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
     * <h5> CCD010702 驗證是否有效 </h5>
     * <request>
     *
     * <response>
     *
     * <error>
     * */
    public static void checkSessionID(ResponseListener responseListener,
                                 Activity activity) throws JSONException {
        String apiName = "ccd010702";
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
     * <h5> CCD040102 Image Switch 背景圖片 </h5>
     * <request>
     *  @param  switchPara	string	輪播版位
     *                              S01-Web登入頁
     *                              S02-App登入頁
     *                              S03-App登入輸入帳密頁
     *                              S04-App登入記住帳號頁
     *  localHour	        string	Client端目前的時段(小時) 00~23
     *
     *
     * <response>
     *
     * <error>
     * */
    public static void getSwitchImage(SwitchImageType switchPara, ResponseListener responseListener,
                                      Activity activity) throws JSONException {
        String apiName = "ccd040102";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        Calendar calendar = Calendar.getInstance();

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("switchPara", switchPara.code);
//        tmpBodyParam.addProperty("switchPara", "S01");
        tmpBodyParam.addProperty("localHour", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }


    /**
     * <h5> CCD040411 變更裝置 </h5>
     * <request>

     *
     * <response>
     *
     * <error>
     * */
    public static void changeUserDevice(ResponseListener responseListener, Activity activity) throws JSONException {
        String apiName = "ccd040411";
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
}

