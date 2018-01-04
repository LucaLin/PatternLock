package com.dbs.omni.tw.util.http;


import android.app.Activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dbs.omni.tw.util.http.listener.JsonResponseErrorListener;
import com.dbs.omni.tw.util.http.listener.JsonResponseListener;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.RegisterData;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通用面的API等等…
 *
 */
public class RegisterHttpUtil extends HttpUtilBase {


    /**<h5> CCD010101 線上註冊-身分判別 </h5>
     * <request>
     *  @param  nID         string      National ID 身分證號/居留證號
     * <response>
     *  userType    String      1.ANZ 用戶+卡友     2.卡友
     *  extension   String      手機號碼(加密)，Client端不需解密，呼叫發送OTP時，request需帶入此欄位
     *
     * <error>
     **/
    public static void identityDifferentiate(String nID, ResponseListener responseListener,
                                       Activity activity) throws JSONException {
        String apiName = "ccd010101";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("nID", nID);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }


    /**<h5> CCD010102 線上註冊-ANZ用戶使用原有代號驗證 </h5>
     * <request>
     *  @param userCode	    string		使用者代號(6~15碼英數字)
     *  @param bDate		string		Date of birth生日 yyyymmdd
     *  @param  nID         string      National ID 身分證號/居留證號
     * <response>
     *
     * <error>
     **/
    public static void verifyUserCodeForANZ(String userCode, String bDate, String nID, ResponseListener responseListener,
                                             Activity activity) throws JSONException {
        String apiName = "ccd010102";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("userCode", userCode);
        tmpBodyParam.addProperty("bDate", bDate);
        tmpBodyParam.addProperty("nID", nID);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010103 線上註冊-信用卡及用戶資料驗證 </h5>
     * <request>
     *  @param bDate		string		Date of birth生日 yyyymmdd
     *  @param ccNO		    string		Card NO 信用卡卡號 16碼數字
     *  @param expDate		string		信用卡效期 mmyy
     *  @param nID         string      National ID 身分證號/居留證號
     * <response>
     *
     * <error>
     **/
    public static void verifyUserData(String bDate, String ccNO, String expDate, String nID, ResponseListener responseListener,
                                             Activity activity) throws JSONException {
        String apiName = "ccd010103";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("bDate", bDate);
        tmpBodyParam.addProperty("ccNO", ccNO);
        tmpBodyParam.addProperty("expDate", expDate);
        tmpBodyParam.addProperty("nID", nID);


        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010104 線上註冊-驗證帳號 </h5>
     * <request>
     *  @param userCode	    string		使用者代號(6~15碼英數字)
     *
     * <response>
     *
     * <error>
     **/
    public static void verifyUserCode(String userCode, ResponseListener responseListener,
                                      Activity activity) throws JSONException {
        String apiName = "ccd010104";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("userCode", userCode);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010106 線上註冊-建立CCDS會員 </h5>
     * 需要OTP
     * <request>
     *  @param nID				string		National ID 身分證號/居留證號
//     *  @param tcVersionSEQ	string		條款table序號
     *  @param userCode		    string		使用者帳號(6~15碼英數字)
     *  @param pCode			string		加密後密碼
     *  @param rKey			string		Random key for pCode
     *  @param nickname		string		暱稱(中英文字，限定為10字，不可設定特殊符號或空白)
     *  @param bDate	    string	Date of birth生日 yyyymmdd
     *  @param ccNO	        string	Card NO 信用卡卡號 16碼數字
     *  @param expDate	    string	信用卡效期 mmyy
     *  @param anzUserCode	string	ANZ使用者代號(6~15碼英數字)
     *
     *
     *
     *
     * <response>
     *
     * <error>
     **/
    public static  String createUserApiName = "ccd010106"; //ForOTP

    public static void createUserMember(RegisterData registerData, ResponseListener responseListener,
                                        Activity activity) throws JSONException {
        String apiName = createUserApiName;
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("nID", registerData.getnID());
        tmpBodyParam.addProperty("userCode", registerData.getUserCode());
        tmpBodyParam.addProperty("pCode", registerData.getpCode());
        tmpBodyParam.addProperty("rKey", registerData.getrKey());
        tmpBodyParam.addProperty("nickname", registerData.getNickname());
        tmpBodyParam.addProperty("bDate", registerData.getbDate());
        tmpBodyParam.addProperty("ccNO", registerData.getCcNO());
        tmpBodyParam.addProperty("expDate", registerData.getExpDate());
        tmpBodyParam.addProperty("anzUserCode", registerData.getAnzUserCode());

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010301 登入 </h5>
    * <request>
    * @param userCode        string	使用者登入帳號
    *  @param pCode        string	使用者登入密碼(加密)
    * @param rKey            string	Random key for pCode
    * @param fplFlag      string  指紋登入(Y/N)
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
    *
    * dcFlag;               //是否變更裝置(for CCDTM)
    *                          //1：未變更裝置
    *                          //2：初次使用APP登入(跳出提醒約定裝置)
    *                          //3：裝置變更(跳出提醒約定裝置)
    * fpcFlag;		        //是否強制更換密碼(Y/N，預設為N)
    * rmsFlag;		        //是否提醒設定email(Y/N，預設為N)
    ***/
    public static void loginUser(String userCode, String pCode, String rKey, String fplFlag, ResponseListener responseListener,
                                 Activity activity) throws JSONException {
        String apiName = "ccd010301";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        tmpBodyParam.addProperty("userCode", userCode);
        tmpBodyParam.addProperty("pCode", pCode);
        tmpBodyParam.addProperty("rKey", rKey);
        tmpBodyParam.addProperty("fplFlag", fplFlag);


        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010701 登出 </h5>
     * <request>
     *  @param logoutType	string	1. only CCDS logout
     *                              2. CCDS+SSO logout
     *
     * <response>
     *  loginTime	string	登入時間YYYYMMDDHHMMSS
     *  logoutTime	string	登出時間YYYYMMDDHHMMSS
     *  stayTime	string	停留時間HR:MM
     *
     * <error>
     **/
    public static void logoutUser(String logoutType, ResponseListener responseListener,
                              Activity activity) throws JSONException {
        String apiName = "ccd010701";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("logoutType", logoutType);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }


}

