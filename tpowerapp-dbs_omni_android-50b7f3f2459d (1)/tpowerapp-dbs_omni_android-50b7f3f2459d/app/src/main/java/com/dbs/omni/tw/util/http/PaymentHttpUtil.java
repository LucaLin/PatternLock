package com.dbs.omni.tw.util.http;


import android.app.Activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
public class PaymentHttpUtil extends HttpUtilBase {


    /**
     * <h5> CCD000203 查詢銀行小圖示 </h5>
     * <request>
     * @param isLogin		(loginFlag	string		Y:已登入 N:未登入)
     * @param bankList      string[]    [] 為空則查全部
     *
     * <response>
     *  bankList      List :
     *      bankNO		string	銀行代碼(3碼)
     *      bankName	string	銀行名稱
     *      appDate		string	更新日期
     *      bankIcon	string	Base64字串
     *
     * <error>
     * */
    public static void queryBankIcon(boolean isLogin, ArrayList<String> bankList, ResponseListener responseListener,
                                           Activity activity) throws JSONException {
        String apiName = "ccd000203";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        if (isLogin) {
            tmpBodyParam.addProperty("loginFlag", "Y");
        } else {
            tmpBodyParam.addProperty("loginFlag", "N");
        }

        JsonArray bankArrayList = new JsonArray();
        tmpBodyParam.add("bankList", bankArrayList);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD030101 未登入線上繳費:全國繳費網-用戶資料驗證 </h5>
     * <request>
     *  @param paymentNO	string	繳款編號(National ID證號或信用卡號)  證號10碼,卡號16碼
     *  @param bankNO		string	轉出銀行代碼(3碼)
     *  @param acctNO		string	轉出帳號
     *  @param amt			string	繳款金額
     *
     * <response>
     *
     * <error>
     * */
    public static void verifyUserDataForPreloginPayment(String paymentNO, String bankNO, String acctNO, String amt, ResponseListener responseListener,
                                     Activity activity) throws JSONException {
        String apiName = "ccd030101";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("paymentNO", paymentNO);
        tmpBodyParam.addProperty("bankNO", bankNO);
        tmpBodyParam.addProperty("acctNO", acctNO);
        tmpBodyParam.addProperty("amt", amt);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD030102未登入線上繳費:查詢本期應繳金額 </h5>
     * <request>
     *  @param nID	    string	National ID 身分證號/居留證號
     *  @param bDate	string	Date of birth生日 yyyymmdd
     *
     * <response>
     *  amtNewPurchases	string			本期新增金額 = (前期應繳-前期已繳+本期應繳)
     *  amtCurrDue		string			本期全部應繳金額
     *  amtCurrPayment	string			本期累積已繳金額  (待V+提供)
     *  amtMinPayment	string			本期最低應繳金額
     *
     *
     *
     * <error>
     * */
    public static void queryCurrentAmountForPreloginPayment(String nID, String bDate, ResponseListener responseListener,
                                                            Activity activity) throws JSONException {
        String apiName = "ccd030102";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("nID", nID);
        tmpBodyParam.addProperty("bDate", bDate);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD030104 未登入線上繳費:全國繳費網-繳款 </h5>
     * <request>
     *  @param paymentNO	string	繳款編號(National ID證號)  證號10碼
     *  @param bankNO		string	轉出銀行代碼(3碼)
     *  @param acctNO		string	轉出帳號
     *  @param amt			string	繳款金額
     *  @param settleNO 	string	銷帳編號，由使用者自行keyin信用卡號
     *
     * <response>
     *   txSEQ		string	交易序號 Channel_Tx_Ref (CCDS generated UUID)
     *   paymentNO	string	繳款編號(National ID證號)
     *   					證號10碼,卡號16碼
     *   amt			string	繳款金額
     *   bankNO		string	轉出銀行代碼(3碼)
     *   acctNO		string	轉出帳號
     *   feeAMT		string	手續費
     *
     *
     * <error>
     * */
    public static void preLoginPayment(String paymentNO, String bankNO, String acctNO, String amt, String settleNO, ResponseListener responseListener,
                                       Activity activity) throws JSONException {
        String apiName = "ccd030104";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("paymentNO", paymentNO);
        tmpBodyParam.addProperty("bankNO", bankNO);
        tmpBodyParam.addProperty("acctNO", acctNO);
        tmpBodyParam.addProperty("amt", amt);
        tmpBodyParam.addProperty("settleNO", settleNO);


        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD030201 取得便利商店繳款條碼 </h5>
     * <request>
     *
     * <response>
     *   barCodeNo1     條碼一:商店代碼
     *   barCodeNo2		條碼二:代收編號
     *   barCodeNo3		條碼三:金額代碼(應繳總額)
     *   barCodeNo4		條碼四:金額代碼(最底應繳金額)
     *
     *
     * <error>
     * */
    public static void getConvenientStoreBarcode(ResponseListener responseListener,
                                       Activity activity) throws JSONException {
        String apiName = "ccd030201";
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
     * <h5> CCD030401線上繳費:其他銀行帳戶繳款-用戶資料驗證 </h5>
     * <request>
     *  @param bankNO	string	轉出銀行代碼(3碼)
     *  @param acctNO	string	轉出帳號
     *  @param amt		string	繳款金額
     *
     * <response>
     *
     *
     * <error>
     * */
    public static void verifyUserDataForOtherBankPayment(String bankNO, String acctNO, String amt, ResponseListener responseListener,
                                                 Activity activity) throws JSONException {
        String apiName = "ccd030401";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("bankNO", bankNO);
        tmpBodyParam.addProperty("acctNO", acctNO);
        tmpBodyParam.addProperty("amt", amt);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD030402 線上繳費:其他銀行帳戶繳款-繳款 </h5>
     * <request>
     *  @param bankNO	        string	轉出銀行代碼(3碼)
     *  @param acctNO	        string	轉出帳號
     *  @param amt		        string	繳款金額
     *  @param ccNO		        string	信用卡號，Client端帶入使用者的任一信用卡號
     * <response>
     *  txSEQ	string	交易序號 Channel_Tx_Ref (CCDS generated UUID)
     *  nID		string	證號
     *  amt		string	繳款金額
     *  bankNO	string	轉出銀行代碼(3碼)
     *  acctNO	string	轉出帳號
     *  feeAMT	string	手續費
     *
     *
     *
     * <error>
     * */
    public static void otherBankPayment(String bankNO, String acctNO, String amt, String ccNO, ResponseListener responseListener,
                                                         Activity activity) throws JSONException {
        String apiName = "ccd030402";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("bankNO", bankNO);
        tmpBodyParam.addProperty("acctNO", acctNO);
        tmpBodyParam.addProperty("amt", amt);
        tmpBodyParam.addProperty("ccNO", ccNO);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**
     * <h5> CCD030501 取得活期帳戶 </h5>
     * <request>
     *
     * <response>
     *  accList	List:
     *  	acctNO		string			帳戶號碼
     *  	acctBalance	string			帳戶餘額
     *  	acctName		string			帳戶名稱
     *
     *
     * <error>
     * */
    public static void getDSBAccount(ResponseListener responseListener,
                                        Activity activity) throws JSONException {
        String apiName = "ccd030501";
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
     * <h5> CCD030502 活期帳戶繳款-用戶驗證 </h5>
     * <request>
     *  @param acctNO	string	繳款帳號
     *  @param amt		string	繳款金額
     *
     * <response>
     *
     *
     * <error>
     * */
    public static void verifyUserDataForDBSPayment(String acctNO, String amt, ResponseListener responseListener,
                                     Activity activity) throws JSONException {
        String apiName = "ccd030502";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("acctNO", acctNO);
        tmpBodyParam.addProperty("amt", amt);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }


    /**
     * <h5> CCD030503 活期帳戶繳款-繳款 </h5>
     * <request>
     *  @param acctNO	string	繳款帳號
     *  @param amt		string	繳款金額
     *  @param ccNO     string  信用卡號，Client端帶入使用者的任一信用卡號
     *
     * <response>
     *  txSEQ	string	交易序號 (CCDS generated UUID)
     *  amt		string	繳款金額
     *  acctNO	string	繳款帳號
     *
     * <error>
     * */
    public static void DBSPayment(String acctNO, String amt, String ccNO ,ResponseListener responseListener,
                                                   Activity activity) throws JSONException {
        String apiName = "ccd030503";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("acctNO", acctNO);
        tmpBodyParam.addProperty("amt", amt);
        tmpBodyParam.addProperty("ccNO", ccNO);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }



}

