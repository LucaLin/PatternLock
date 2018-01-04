package com.dbs.omni.tw.util.http;


import android.app.Activity;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.http.listener.JsonResponseErrorListener;
import com.dbs.omni.tw.util.http.listener.JsonResponseListener;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 通用面的API等等…
 *
 */
public class SettingHttpUtil extends HttpUtilBase {

    private static final String TAG = "SettingHttpUtil";

    /**<h5> CCD010501忘記使用者帳號-身分驗證 </h5>
     * <request>
     *  @param nID		string	National ID (證號)
     *  @param bDate	string	Date of birth生日 YYYYMMDD
     *  @param ccNO	string	Card NO 信用卡卡號 16碼數字
     *  @param expDate	string	信用卡效期 MMYY
     *
     * <response>
     *
     * <error>
     **/
    public static void identityVerifyOfForgetUserCode(String nID, String bDate, String ccNO, String expDate, ResponseListener responseListener,
                                                      Activity activity) throws JSONException {
        String apiName = "ccd010501";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("nID", nID);
        tmpBodyParam.addProperty("bDate", bDate);
        tmpBodyParam.addProperty("ccNO", ccNO);
        tmpBodyParam.addProperty("expDate", expDate);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010502忘記使用者帳號-查詢使用者帳號 </h5>
     * <request>
     *  @param  nID			string	National ID (證號)
     *  @param  bDate	    string	Date of birth生日 YYYYMMDD
     *  @param  ccNO	    string	Card NO 信用卡卡號 16碼數字
     *  @param  expDate	    string	信用卡效期 MMYY
     *
     * <response>
     *  userCode		string	使用者登入帳號
     * <error>
     **/
    public static  String forgetUserCodeApiName = "ccd010502";

    public static void forgetUserCode(String nID, String bDate, String ccNO, String expDate, ResponseListener responseListener,
                                      Activity activity) throws JSONException {
        String apiName = forgetUserCodeApiName;
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("nID", nID);
        tmpBodyParam.addProperty("bDate", bDate);
        tmpBodyParam.addProperty("ccNO", ccNO);
        tmpBodyParam.addProperty("expDate", expDate);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010601 忘記密碼-身份驗證 </h5>
     * <request>
     *  @param nID		string	National ID (證號)
     *  @param bDate	string	Date of birth生日 YYYYMMDD
     *  @param ccNO	    string	Card NO 信用卡卡號 16碼數字
     *  @param expDate	string	信用卡效期 MMYY
     *  @param userCode	string	使用者登入帳號
     *
     * <response>
     *
     * <error>
     **/
    public static void identityVerifyOfForgetMima(String nID, String bDate, String ccNO, String expDate, String userCode, ResponseListener responseListener,
                                                  Activity activity) throws JSONException {
        String apiName = "ccd010601";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("nID", nID);
        tmpBodyParam.addProperty("bDate", bDate);
        tmpBodyParam.addProperty("ccNO", ccNO);
        tmpBodyParam.addProperty("expDate", expDate);
        tmpBodyParam.addProperty("userCode", userCode);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010602 忘記密碼-重設密碼 </h5>
     * <request>
     *  @param userCode		string	使用者登入帳號
     *  @param newPCode	    string	修改後新密碼(加密)
     *  @param rKey			string	Random key for newPCode
     *  @param nID		    string	National ID (證號)
     *  @param bDate	    string	Date of birth生日 YYYYMMDD
     *  @param ccNO	        string	Card NO 信用卡卡號 16碼數字
     *  @param expDate	    string	信用卡效期 MMYY
     *
     * <response>
     *
     * <error>
     **/
    public static  String forgetMimaApiName = "ccd010602";
    public static void reSettingMimaOfForgetMima(String userCode, String newPCode, String rKey, String nID, String bDate, String ccNO, String expDate, ResponseListener responseListener,
                                                 Activity activity) throws JSONException {
        String apiName = forgetMimaApiName;
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("userCode", userCode);
        tmpBodyParam.addProperty("newPCode", newPCode);
        tmpBodyParam.addProperty("rKey", rKey);
        tmpBodyParam.addProperty("nID", nID);
        tmpBodyParam.addProperty("bDate", bDate);
        tmpBodyParam.addProperty("ccNO", ccNO);
        tmpBodyParam.addProperty("expDate", expDate);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010603 驗證變更帳號 </h5>
     * <request>
     *  @param newUserCode	string	新使用者登入帳號 (6~15碼英數字)
     *  @param newEncUserCode	string	新使用者登入帳號 (加密)
     *  @param rKey	string	Random key for newEncUserCode
     *
     * <response>
     *
     * <error>
     **/
    public static void verifyOfChangeUserCode(String newUserCode, String newEncUserCode, String rKey, ResponseListener responseListener,
                                              Activity activity) throws JSONException {
        String apiName = "ccd010603";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("newUserCode", newUserCode);
        tmpBodyParam.addProperty("newEncUserCode", newEncUserCode);
        tmpBodyParam.addProperty("rKey", rKey);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010604 變更使用者帳號 </h5>
     * <request>
     *  @param newUserCode	string	新使用者登入帳號 (6~15碼英數字)
     *  @param newEncUserCode string  加密的新使用者登入帳號
     *  @param rKey string
     * <response>
     *
     * <error>
     **/
    public static  String changeUserCodeApiName = "ccd010604"; //ForOTP

    public static void changeUserCode(String newUserCode, String newEncUserCode, String rKey, ResponseListener responseListener,
                                      Activity activity) throws JSONException {
        String apiName = "ccd010604";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("newUserCode", newUserCode);
        tmpBodyParam.addProperty("newEncUserCode", newEncUserCode);
        tmpBodyParam.addProperty("rKey", rKey);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010605 驗證使用者密碼 </h5>
     * <request>
     *  @param oldPCode	string	加密後舊密碼
     *  @param newPCode	string	加密後新密碼
     *  @param rKey	string	Random key for newEncUserCode
     *
     * <response>
     *
     * <error>
     **/
    public static void verifyOfChangeMima(String oldPCode, String newPCode, String rKey, ResponseListener responseListener,
                                          Activity activity) throws JSONException {
        String apiName = "ccd010605";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("oldPCode", oldPCode);
        tmpBodyParam.addProperty("newPCode", newPCode);
        tmpBodyParam.addProperty("rKey", rKey);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD010606 變更使用者密碼 </h5>
     * <request>
     *  @param oldPCode	string	加密後舊密碼
     *  @param newPCode	string	加密後新密碼
     *  @param rKey	string	Random key for newEncUserCode
     *
     * <response>
     *
     * <error>
     **/
    public static  String ChangeMimaApiName = "ccd010606"; //ForOTP

    public static void changeMima(String oldPCode, String newPCode, String rKey, ResponseListener responseListener,
                                  Activity activity) throws JSONException {
        String apiName = "ccd010606";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("oldPCode", oldPCode);
        tmpBodyParam.addProperty("newPCode", newPCode);
        tmpBodyParam.addProperty("rKey", rKey);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD020601 取得使用者資訊 </h5>
     * <request>
     *
     * <response>
     *  nameDetl :
     *      firstName	            string	名稱
     *      middleName	            string	名稱
     *      lastName	            string	名稱
     *      fullName	            string	名稱
     *      salutation	            string	稱謂
     *  emailDetl :
     *      email		            string	電子信箱
     *      blockCode	            string	客戶身分權限代碼 (待V+提供)
     *  phoneDetl :
     *      phoneID	                string	行動電話識別碼
     *      phoneCtryCode	        string	國碼
     *      phoneNumber	            string	行動電話
     *      updatedPhoneCtryCode	string	延緩更新國碼
     *      updatedPhoneNumber	    string	延緩更新行動電話
     *  addressDetl :
     *      addressID	            string	住址識別碼
     *      zipCode	                string	郵遞區號
     *      address	                string	地址
     *      updatedZipCode	        string	延緩更新郵遞區號
     *      updatedAddress	        string	延緩更新地址
     *
     *  blockCode	string	客戶身分權限代碼
     *  statmFlag	string	是否申請電子帳單 (Y：已申請、N：未申請)
     *  userCode	string	使用者登入代碼
     *  nID	        string	National ID 身分證號/居留證號 (只顯示前三後二，其他用X代替)
     *  nickname	string	暱稱
     *  fpcFlag	    string	是否強制更換密碼(Y/N，預設為N)
     *  rmsFlag	    string	是否提醒設定email(Y/N，預設為N)
     *
     * <error>
     **/
    public static void getUserInfo(ResponseListener responseListener,
                                  Activity activity) throws JSONException {
        String apiName = "ccd020601";
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
     * <h5> CCD030601 Credit Card Auto Payment Arrangement信用卡自動扣款查詢 </h5>
     * <request>
     *
     * <response>
     *  autoPayFlag	string	是否設定自動扣款 Y:是 N:無
     *  bankNO		string	扣款銀行代碼(3碼)
     *  bankName	string	扣款銀行名稱
     *  bankAccount	string	扣款帳號
     *  debitDate		string	扣款日YYYYMMDD
     *  debitAmount	string	扣款金額
     *  currency		string	扣款幣別
     *
     * <error>
     * */
    public static void settingCreditCardAutoPaymentArrangement(ResponseListener responseListener,
                                  Activity activity) throws JSONException {
        String apiName = "ccd030601";
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
     * <h5> CCD040401 上傳個人圖片 </h5>
     * <request>
     *  @param pic	string	Base64字串
     *  @param picExtension	string	圖片副檔名
     * <response>
     *
     * <error>
     * */
    public static String uploadPictureApiName = "ccd040401";
    public static void uploadPicture(String pic, String picExtension, ResponseListener responseListener,
                                                               Activity activity) throws JSONException {
        String apiName = uploadPictureApiName;
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();
        if(TextUtils.isEmpty(pic)) {
            tmpBodyParam.addProperty("pic", "");
        } else {
            tmpBodyParam.addProperty("pic", "data:image/png;base64," + pic);
        }
        tmpBodyParam.addProperty("picExtension", picExtension);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD040402 下載個人圖片</h5>
     * <request>
     *
     * <response>
     *  pic	string	Base64字串
     *  picExtension	string	圖片副檔名
     * <error>
     * */
    public static void downloadPicture(String downloadTime, ResponseListener responseListener, Activity activity) throws JSONException {
        String apiName = "ccd040402";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("downloadTime", downloadTime);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD040403更新個人資料Email </h5>
     * <request>
     *  @param email    string      Email
     *  @param emailID  string      emailID
     *
     * <response>
     *
     * <error>
     * */
    public static void updateEmail(String email, String emailID, ResponseListener responseListener,
                                   Activity activity) throws JSONException {
        String apiName = "ccd040403";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("email", email);
        tmpBodyParam.addProperty("emailID", emailID);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD040404 變更手機號碼 </h5>
     * <request>
     *  @param phoneNumber	string	電話號碼
     *  @param phoneID	string	行動電話識別碼
     *  @param phoneCtryCode    string  國碼
     *
     * <response>
     *  email    string      Email
     *
     * <error>
     * */
    public static  String updatePhoneCodeApiName = "ccd040404"; //ForOTP
    public static void updatePhone(String phoneNumber, String phoneID, String phoneCtryCode, ResponseListener responseListener,
                                   Activity activity) throws JSONException {
        String apiName = updatePhoneCodeApiName;
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("phoneNumber", phoneNumber);
        tmpBodyParam.addProperty("phoneID", phoneID);
        tmpBodyParam.addProperty("phoneCtryCode", phoneCtryCode);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD040405 變更通訊地址 </h5>
     * <request>
     *  @param city	        string	縣市
     *  @param district		string	區域
     *  @param zipCode		string	郵遞區號
     *  @param address		string	地址
     *  @param addressID	string	住址識別碼
     *
     * <response>
     *
     * <error>
     * */
    public static  String updateAddressApiName = "ccd040405"; //ForOTP

    public static void updateAddress(String city, String district, String zipCode, String address, String addressID, ResponseListener responseListener,
                                   Activity activity) throws JSONException {
        String apiName = "ccd040405";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("city", city);
        tmpBodyParam.addProperty("district", district);
        tmpBodyParam.addProperty("zipCode", zipCode);
        tmpBodyParam.addProperty("address", address);
        tmpBodyParam.addProperty("addressID", addressID);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    //TODO 還在修改
    /**
     * <h5> CCD040406 通知設定 </h5>
     * <request>
     *  @param msgNoticeList	List :
     *       msgItem    String
     *
     *
     *
     * <response>
     *
     * <error>
     * */
    public static void settingNotices(ArrayList<String> msgNoticeList, ResponseListener responseListener,
                                 Activity activity) throws JSONException {
        String apiName = "ccd040406";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        JsonArray msgNoticeJSONArray = new JsonArray();

        for (String msgItem: msgNoticeList) {
            JsonObject object = new JsonObject();
            object.addProperty("msgItem", msgItem);
            msgNoticeJSONArray.add(object);
        }

        tmpBodyParam.add("msgNoticeList", msgNoticeJSONArray);


        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD040407 更新暱稱 </h5>
     * <request>
     *  @param nickname    string      暱稱
     *
     * <response>
     *  nickname    string      暱稱
     *
     * <error>
     * */
    public static void updateNickName(String nickname, ResponseListener responseListener,
                                   Activity activity) throws JSONException {
        String apiName = "ccd040407";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("nickname", nickname);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD050201 信用卡申請 </h5>
     * <request>
     *  @param applyCardList    List:
     *       ccLogo	string		信用卡的產品代碼(系統用來辨別哪一張卡片，與PWeb串接會用此代碼)
     *       ccName	string		信用卡卡片名稱 eg: 豐利御璽卡
     *
     * <response>
     *
     * <error>
     * */
    public static void applyCreditCard(ArrayList<String> applyCardList, ResponseListener responseListener,
                                      Activity activity) throws JSONException {
        String apiName = "ccd050201";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        JsonArray applyCardListJSONArray = new JsonArray();

        for (String msgItem: applyCardList) {
            JsonObject object = new JsonObject();
            //TODO 建Object
            object.addProperty("ccLogo", msgItem);
            object.addProperty("ccName", msgItem);
            applyCardListJSONArray.add(object);
        }

        tmpBodyParam.add("applyCardList", applyCardListJSONArray);



        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD040408 查詢指紋登入狀態 </h5>
     * <request>
     *
     * <response>
     *
     * <error>
     * */
    public static void getTouchIDStatus(ResponseListener responseListener,
                                        Activity activity) throws JSONException {
        String apiName = "ccd040408";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("userCode", PreferenceUtil.getUserCode(activity));

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }


    /**
     * <h5> CCD040409 啟用指紋登入 </h5>
     * <request>
     *
     * <response>
     *
     * <error>
     * */
    public static void setEnableTouchID(ResponseListener responseListener,
                                     Activity activity) throws JSONException {
        String apiName = "ccd040409";
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

