package tw.com.taishinbank.ewallet.util.http;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseErrorListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseListener;
import tw.com.taishinbank.ewallet.model.ContactImage;
import tw.com.taishinbank.ewallet.model.FileRawData;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.log.HitRecord;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.model.setting.PushData;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.http.event.DownloadEvent;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;

/**
 * 錢包通用面的Http Request。註冊、設定等等…
 *
 * Created by oster on 2016/1/8.
 */
public class GeneralHttpUtil extends HttpUtilBase {


    public static class ENUM_CERTIFICATION_TYPE {
        public static final String USER_INFO_CHANGE = "0";
        public static final String FORGOT_MIMA = "1";
        public static final String MIMA_CERTIFICATION = "2";
    }


    private static final String TAG = "GeneralHttpUtil";

    /*4.3	WLT010101 註冊1
            * <request>
            * custID	*身份証字號(AES加密)
            * phone`	*手機號碼(10碼)
            * mail      *
            *
            * <response>
            * memNO
            *
            * <error>
            * E001:該證號已註冊
            * E777:該筆資料非台新員工
            * */
    public static void signUpForWallet(String custID, String phone, String mail,
                                       ResponseListener responseListener,
                                       Activity activity, String tag) throws JSONException {
        String apiName = "WLT010101";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("custID",custID);
        tmpBodyParam.put("phone", phone);
        tmpBodyParam.put("mail", mail);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.4	WLT010102 手機簡訊認證碼-註冊
            * <request>
            * memNO     會員代碼(mem_seq)
            *
            * <response>
            * sysTime   壓碼時間(YYYYMMDDHHMISS)
            *
            * <error>
            * */
    public static void phoneCertRequest(String memNO,
                                        ResponseListener responseListener,
                                        Activity activity, String tag) throws JSONException {
        String apiName = "WLT010102";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("memNO", memNO);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.5	WLT010103 驗證簡訊認證碼
            * <request>
            * memNO     *會員代碼(mem_seq)
            * vCode     *認證碼
            *
            * <response>
            *
            * <error>
            * E0xx:驗證碼錯誤
            * */
    public static void phoneCertCheck(String memNO, String vCode,
                                      ResponseListener responseListener,
                                      Activity activity, String tag) throws JSONException {
        String apiName = "WLT010103";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("memNO",memNO);
        tmpBodyParam.put("vCode", vCode);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.6	WLT010104 電子信箱認證碼-註冊
            * <request>
            * memNO     *會員代碼(mem_seq)
            *
            * <response>
            * sysTime   壓碼時間(YYYYMMDDHHMMSS)
            *
            * <error>
            * */
    public static void emailCertRequest(String memNO,
                                        ResponseListener responseListener,
                                        Activity activity, String tag) throws JSONException {
        String apiName = "WLT010104";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("memNO",memNO);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.7	WLT010105 電子信箱認證碼
            * <request>
            * memNO     *會員代碼(mem_seq)
            * vCode     *認證碼
            *
            * <response>
            *
            * <error>
            * E0xx:驗證碼錯誤
            * */
    public static void emailCertCheck(String memNO, String vCode,
                                      ResponseListener responseListener,
                                      Activity activity, String tag) throws JSONException {
        String apiName = "WLT010105";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("memNO",memNO);
        tmpBodyParam.put("vCode", vCode);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.8	WLT010106 設定登入密碼
            * <request>
            * password     *密碼(AES加密)
            * memNO        *會員代碼(memSeq)
            *
            * <response>
            *
            * <error>
            * */
    public static void setMemberPassword(String mima, String memNO,
                                         ResponseListener responseListener,
                                         Activity activity, String tag) throws JSONException {
        String apiName = "WLT010106";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("password",mima);
        tmpBodyParam.put("memNO", memNO);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.9	WLT010107 設定稱呼
            * <request>
            * memNO     *會員代碼(mem_seq)
            * nickname     *稱呼
            *
            * <response>
            *
            * <error>
            * */
    public static void setMemberNickname(String memNO, String nickname,
                                         ResponseListener responseListener,
                                         Activity activity, String tag) throws JSONException {
        String apiName = "WLT010107";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("memNO",memNO);
        tmpBodyParam.put("nickname", nickname);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.10	WLT010201  登入錢包系統
            * <request>
            * custID	*身份証字號(AES加密)
            * phone     *手機號碼(10碼)
            * pw    	*使用者密碼(AES加密)
            *
            * <response>
            *
            * <error>
            * E0xx:該證號已註冊
            * */
    public static void loginToWallet(String custID, String phone, String pwd,
                                     ResponseListener responseListener,
                                     Activity activity, String tag) throws JSONException {
        String apiName = "WLT010201";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("custID",custID);
        tmpBodyParam.put("phone", phone);
        tmpBodyParam.put("pwd", pwd);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.11	WLT020101 登入儲值系統
            * <request>
            * custID    *使用者身份証字號(AES加密)
            * custCode  *使用者代碼
            * pwd       *使用者密碼(AES加密)
            *
            * <response>
            * sysTime   WS主機時間(非儲值主機, (YYYYMMDDHHMISS))
            *
            * <error>
            * */
    public static void loginInToSVserver(String custID, String custCode, String pwd,
                                         ResponseListener responseListener,
                                         Activity activity, String tag) throws JSONException {
        String apiName = "WLT020101";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("custID",custID);
        tmpBodyParam.put("custCode", custCode);
        tmpBodyParam.put("pwd", pwd);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.19	WLT030102 更新通訊錄/加入好友
            * <request>
            * JSONObject friendList
            * {
            * phone     *手機號碼(09開頭，10碼)
            * listName  *姓
            * firstName *名
            * }
            *
            * <response>
            *
            * <error>
            * */
    public static void addFriends(ArrayList<LocalContact> localContacts,
                                  ResponseListener responseListener,
                                  Activity activity, String tag) throws JSONException {
        String apiName = "WLT030102";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        // 轉成web service api能用的json物件
        JSONArray friendList = new JSONArray();
        for(int i = 0; i < localContacts.size(); i++){
            LocalContact localContact = localContacts.get(i);
            JSONObject contact = new JSONObject();
            try {
                // 手機號碼(09開頭，10碼)
                contact.put("phone", localContact.getPhoneNumber());
                // 姓
                contact.put("lastName", localContact.getFamilyName());
                // 名
                contact.put("firstName", localContact.getGivenName());
                friendList.put(contact);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("friendsList", friendList);
        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.20	WLT030103 取得好友清單
            * (header 的 svTokenID(儲值主機驗證碼) 此欄位必填)
            *  <request>
            *
            * <response>
            * friendsList
            *
            * <error>
            * */
    public static void getFriendsList(
            ResponseListener responseListener,
            Activity activity, String tag) throws JSONException {
        String apiName = "WLT030103";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    public static void uploadImage(String imageBase64Str,
                                   ResponseListener responseListener,
                                   Activity activity, String tag) throws JSONException {

        String apiName = "WLT010301";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("pic", imageBase64Str);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.13   WLT010302 多筆圖片下載
           *  <request>
           * JSONObject memNOList
           * {
           * memNO      錢包會員序號
           * }
           *
           * <response>
           * JSONObject memPicList
           * {
           * memNO      錢包會員序號
           * pic        *圖片字串 (加密)
           * }
           *
           * <error>
           * */
    public static ContactImage downloadImages(String memNo,
                                              Activity activity, String tag) throws JSONException {
        final String TAG = "downloadImages";

        String apiName = "WLT010302";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        JSONArray memNOList = new JSONArray();
        if(memNo != null) {
            JSONObject object = new JSONObject();
            try {
                object.put("memNO", memNo);
                memNOList.put(object);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        tmpBodyParam.putOpt("memNOList", memNOList);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, future, future);
        request.setTag(tag);
        mQueue.add(request);

        ContactImage contactImage = null;
        try {
            JSONObject response = future.get(TIMEOUT, TimeUnit.SECONDS);
            if(response != null){
                try {
                    // 取得response物件
                    JSONObject objectResponse = response.getJSONObject(apiName + "response");
                    // 取得header
                    JSONObject objectHeader = objectResponse.getJSONObject("header");
                    Log.d(TAG, apiName + " header = " + objectHeader.toString());
                    String returnCode = objectHeader.getString("returnCode");
                    if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                        // 取得body
                        JSONObject objectBody = objectResponse.getJSONObject("body");
                        contactImage = GeneralResponseBodyUtil.getImage(objectBody);
                        if(contactImage != null) {
                            Log.d(TAG, "has pic = " + !TextUtils.isEmpty(contactImage.getPic()));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "Retrieve api call interrupted." + e);
        } catch (ExecutionException e) {
            Log.d(TAG, "Retrieve api call failed." + e);
        } catch (TimeoutException e) {
            Log.d(TAG, "Retrieve api call timed out." + e);
        } catch (OutOfMemoryError e) {
            Log.d(TAG, "Retrieve api OOM." + e);
        }
        return contactImage;
    }

    public static void getMultiImages(ArrayList<String> memNOs,
                                      ResponseListener responseListener,
                                      Activity activity, String tag) throws JSONException {
        String apiName = "WLT010302";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        JSONArray memNOList = new JSONArray();

        if(memNOs.size() > 0)
        {
            JSONObject object = new JSONObject();
            try{
                for (String memNO: memNOs) {
                    object.put("memNO", memNO);
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
            memNOList.put(object);
        }

        tmpBodyParam.put("memNOList", memNOList);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /*	WLT010309 變更稱呼
        * (以header 的 token為依據)
        * <request>
        * nickname          *字串
        *
        * <response>
        *
        * <error>
        * */
    public static void uploadNickname(String nickName,
                                      ResponseListener responseListener,
                                      Activity activity, String tag) throws JSONException {

        String apiName = "WLT010309";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("nickname", nickName);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*	WLT010303 會員資料變更認證
        * (以header 的 token為依據)
        * <request>
        * custID          *字串
        * pwd          *字串
        * phone          *字串
        * mail          *字串
        * type          *字串 0:個人資料修改驗證 1:忘記密碼身份驗證 2:錢包密碼驗證
        * <response>
        *
        * <error>
        * */
    public static void CertificationUser(String userID, String mima, String phone, String email, String type,
                                         ResponseListener responseListener,
                                         Activity activity, String tag) throws JSONException {

        String apiName = "WLT010303";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("custID", userID);
        tmpBodyParam.put("pwd", mima);
        tmpBodyParam.put("phone", phone);
        tmpBodyParam.put("mail", email);
        tmpBodyParam.put("type", type );

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*	WLT010304 變更密碼
        * (以header 的 token為依據)
        * <request>
        * pwd          *字串
        *
        * <response>
        *
        * <error>
        * */
    public static void uploadPassword(String mima,
                                      ResponseListener responseListener,
                                      Activity activity, String tag) throws JSONException {

        String apiName = "WLT010304";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("pwd", mima);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*WLT010307 變更電子郵件-發送認證碼
        * <request>
        * newMail     *使用者新電子郵件
        * confirm     *第一次呼叫是N
        *
        * <response>
        * sysTime   壓碼時間(YYYYMMDDHHMMSS)
        *
        * <error>
        * */
    public static void modifyEmailCertRequest(String newMail, String confirm,
                                              ResponseListener responseListener,
                                              Activity activity, String tag) throws JSONException {
        String apiName = "WLT010307";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("newMail", newMail);
        tmpBodyParam.put("confirm", confirm);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*WLT010308 變更電子郵件-驗證驗證碼
        * <request>
        * vCode     *認證碼
        *
        * <response>
        *
        * <error>
        * E0xx:驗證碼錯誤
        * */
    public static void modifyEmailCertCheck(String vCode,
                                            ResponseListener responseListener,
                                            Activity activity, String tag) throws JSONException {
        String apiName = "WLT010308";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("vCode", vCode);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*WLT010305 變更手機-發送認證碼
       * <request>
       * newPhone     使用者新手機號碼(10碼)
       *
       * <response>
       * sysTime   壓碼時間(YYYYMMDDHHMISS)
       *
       * <error>
       * */
    public static void modifyPhoneCertRequest(String newPhone,
                                              ResponseListener responseListener,
                                              Activity activity, String tag) throws JSONException {
        String apiName = "WLT010305";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("newPhone", newPhone);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*WLT010306 變更手機-驗證認證碼
           * <request>
           * vCode     *認證碼
           *
           * <response>
           *
           * <error>
           * E0xx:驗證碼錯誤
           * */
    public static void modifyPhoneCertCheck(String vCode,
                                            ResponseListener responseListener,
                                            Activity activity, String tag) throws JSONException {
        String apiName = "WLT010306";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("vCode", vCode);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>4.22 WLT010310 忘記密碼-發送認證碼</h5>
     * <pre>
     * request:
     * custID   *使用者身分證號
     * mail     *使用者電子郵件
     *
     * response:
     * verifyID 忘記密碼驗證用
     * memNO    會員代碼(mem_seq)
     * sysTime  壓碼時間(YYYYMMDDHHMISS)
     *
     * error:
     * </pre>
     * */
    public static void resetPwdEmailCertRequest(String custID, String mail,
                                                ResponseListener responseListener,
                                                Activity activity, String tag) throws JSONException {
        String apiName = "WLT010310";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("custID", custID);
        tmpBodyParam.put("mail", mail);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>4.23 WLT010311 忘記密碼-驗證信箱驗證碼</h5>
     * <pre>
     * request:
     * verifyID *忘記密碼驗證用
     * memNO    *會員代碼(mem_seq)
     * vCode    *認證碼
     *
     * response:
     * verifyID 忘記密碼驗證用
     * memNO    會員代碼(mem_seq)
     *
     * error:
     * </pre>
     * */
    public static void resetPwdEmailCertCheck(String verifyID, String memNO, String vCode,
                                              ResponseListener responseListener,
                                              Activity activity, String tag) throws JSONException {
        String apiName = "WLT010311";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("verifyID", verifyID);
        tmpBodyParam.put("memNO", memNO);
        tmpBodyParam.put("vCode", vCode);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>4.23 WLT010311 忘記密碼-變更密碼</h5>
     * <pre>
     * request:
     * verifyID     *忘記密碼驗證用
     * memNO        *會員代碼(mem_seq)
     * newPassword  *密碼，需要加密
     *
     * response:
     *
     * error:
     * </pre>
     * */
    public static void resetPwdModifyPassword(String verifyID, String memNO, String newMima,
                                              ResponseListener responseListener,
                                              Activity activity, String tag) throws JSONException {
        String apiName = "WLT010312";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("verifyID", verifyID);
        tmpBodyParam.put("memNO", memNO);
        tmpBodyParam.put("newPassword", newMima);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }


    /**
     * <h5> WLT010313 查詢個人資料</h5>
     * <pre>
     * request:
     *
     * response:
     *  nickName	String		會員暱稱
     *  pwd     	String		會員密碼(須加密)
     *  email   	String		會員電子信箱(須加密)
     *  phone   	String		會員電話(須加密)
     *  errorCount 	int 		密碼錯誤次數
     *  svFlag	    String		Y:儲值會員/ N:非儲值會員
     *
     * error:
     * </pre>
     * */
    public static void queryPersonalData(ResponseListener responseListener,
                                         Activity activity, String tag) throws JSONException {
        String apiName = "WLT010313";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();


        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>WLT030201 系統公告查詢</h5>
     * <pre>
     * request:
     * bbSeq        Integer     (非必填）查單筆公告詳情須帶入此欄位

     * response:
     * title	    String		主標
     * content	    String		公告內容
     * bbType	    String		公告類別
     *                          1:優惠公告(預設,目前只有這種1/19)
     *                          2:系統公告(維修…等)
     *                          3:邀請註冊儲值
     *                          4:邀請綁定信用卡
     * onLineDate   String      上線時間(yyyyMMdd)
     * downLineDate String      下線時間(yyyyMMdd)
     * createDate   String      建立日期(yyyyMMddHHmmss)
     * modifyDate   String      變更日期(yyyyMMddHHmmss)
     *
     * error:
     * </pre>
     * */
    public static void querySystemMessage(Integer bbSeq, ResponseListener responseListener,
                                          Activity activity, String tag) throws JSONException {
        String apiName = "WLT030201";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        if(bbSeq != null && bbSeq.intValue() > 0){
            tmpBodyParam.put("bbSeq", bbSeq.intValue());
        }

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }



    /**
     *
     * @param filename
     * @param finishDownloadListener
     * @param activity For the header information.
     * @throws JSONException
     */
    public static DownloadCouponAsyncTask task;
    public static void downloadCoupon(String filename, DownloadEvent.FinishDownloadListener finishDownloadListener, Activity activity) throws JSONException  {

        task = new DownloadCouponAsyncTask(activity, "WLT060205");

//        task.jHeader = addRequestHeader(activity, "WLT060205");
//        task.jHeader.put("imagePath", filename);
        String apiName = "WLT060205";
        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("imagePath", filename);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        task.jHeader = requestParam;
        task.filename = filename;
        task.folderPath = ContactUtil.FolderPath;
        task.setFinishDownloadListener(finishDownloadListener);
        task.execute();
    }

    public static void downloadTicket(String filename, DownloadEvent.FinishDownloadListener finishDownloadListener, Activity activity) throws JSONException  {

        task = new DownloadCouponAsyncTask(activity);

        task.filename = filename;
        task.folderPath = ContactUtil.TicketFolderPath;
        task.setFinishDownloadListener(finishDownloadListener);
        task.execute();
    }

    public static void stopDownloadCoupon()
    {
        if(task != null)
        {
            task.cancel(true);
        }
    }
    // ----
    // Inner interface
    // ----
    static class DownloadCouponAsyncTask extends AsyncTask<String, Void, FileRawData> {
        String apiUrl = OFFICIAL_SERVER_URL;
        String apiName = "";

        Context context;

        JSONObject jHeader;
        String filename;
        String folderPath;

        boolean isExternal = false; //for ticket


        private int responseCode = 200;


        private DownloadEvent.FinishDownloadListener finishDownloadListener;

        public void setFinishDownloadListener(DownloadEvent.FinishDownloadListener finishDownloadListener)
        {
            this.finishDownloadListener = finishDownloadListener;
        }

        public DownloadCouponAsyncTask(Context context) {
            this.context = context;
            isExternal = true;
        }

        public DownloadCouponAsyncTask(Context context, String apiName) {
            this.context = context;
            this.apiName = apiName;

        }

        @Override
        protected FileRawData doInBackground(String... params) {
            FileRawData newFileRawData = new FileRawData();

            try {
                // Create connection to send GCM Message request.
                URL url;
                if(!isExternal) {
                    newFileRawData.setImagePath(filename);
                    url = new URL(apiUrl + apiName);
                } else {
                    File file = new File(filename);
                    newFileRawData.setImagePath(file.getName());
                    url = new URL(filename);
                }

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    // Prepare json request
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    outputStream = conn.getOutputStream();
                    if (!isExternal) {
                        outputStream.write(jHeader.toString().getBytes());
                    }
                    // Send GCM message content.
                    responseCode = conn.getResponseCode();
                    String returnCode = conn.getHeaderField("returnCode");
                    if (!isExternal) {
                        if (responseCode != 200 || !returnCode.equals("S0000")) {
                            return newFileRawData;
                        }
                    } else {
                        if (responseCode != 200) {
                            return newFileRawData;
                        }
                    }

                    // Read GCM response.
                    inputStream = conn.getInputStream();

                    newFileRawData.setFileContent(readToBytesFully(inputStream));
                    Log.d("GeneralHttpUtil", "Response Code:" + responseCode + " The size of " + newFileRawData.getFileContent().length);
                    Log.d("GeneralHttpUtil", "Return message:" + new String(newFileRawData.getFileContent(), Charset.forName("UTF-8")));

                    //String folderPath = ContactUtil.FolderPath;
                    String filePath = folderPath + File.separator + newFileRawData.getImagePath();
                    File newFile = new File(filePath);

                    File folder = new File(newFile.getParent(), ContactUtil.NOMEDIA);
                    if (!folder.exists()) {
                        boolean ret = folder.mkdirs();
                        if (!ret)
                            folder.mkdir();
                    }
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(newFile);
                        fos.write(newFileRawData.getFileContent());

                    } finally {
                        if (fos != null) {
                            fos.flush();
                            fos.close();
                        }
                    }
                } finally {
                    if(outputStream != null)
                        outputStream.close();
                    if(inputStream != null)
                        inputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return newFileRawData;
        }

        @Override
        protected void onPostExecute(FileRawData fileRawData) {
            finishDownloadListener.onFinishDownload();
            super.onPostExecute(fileRawData);
        }

        public int getResponseCode() {
            return responseCode;
        }
    }

    public static byte[] readToBytesFully(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }


    /**
     * <h5> WLT010401 錢包首頁 </h5>
     * <pre>
     * request:
     *
     * response:
     *  countList : [{
     *    type              - String   統計類別
     *                                 redEnvelopes：本月紅包
     *                                 payRequests：待付款項
     *                                 incomes：本月收款
     *                                 receivedCoupon：優惠券
     *    hasNew            - String   是否有新紀錄(N:沒有、Y:有)顯示紅點
     *    count             - int      數量
     *    }, ......
     *  ]
     *  pushMsgList : [{
     *    pushSender        - Integer  發送推播者(mem_seq)
     *    senderNickName    - String   發送推播者暱稱
     *    pushMessage       - String   推播訊息內容
     *    senderMessage     - String   發送推播者輸入訊息內容
     *    pushDate          - String   推播時間(yyyyMMddHHmmss)
     *    amount            - double   交易金額
     *    couponTitle       - String   優惠券標題
     *
     *    recepient         - Integer  收到推播者(self.mem_seq)
     *    countType         - String   動態提示統計類別(參考api Description)
     *    pushType          - String   推播類別(參考api Description)
     *    readFlag          - String   讀取狀態(0:未讀1:已讀)(0要秀紅點)
     *
     *    createDate        - String   訊息建立時間(yyyyMMddHHmmss)
     *    moreFlag          - String   大於10筆：Y
     *                                 未超過10筆：N
     *    url               - String   對應6.2 Push Scheme中的url，來自wlt_message_center.push_scheme
     *                                 Case1：webview:${網址}
     *                                 Case2：redhome
     *                                 Case3：redrecordin:${txfSeq}|${txfdSeq}
     *                                 Case4：redrecordout:${txfSeq}|${txfdSeq}
     *                                 Case5：receive:${txfSeq}|${txfdSeq}
     *                                 Case6：outgoing:${txfSeq}|${txfdSeq}
     *                                 Case7：payreq:${txfSeq}|${txfdSeq}
     *                                 Case8：svin
     *                                 Case9：svout
     *                                 Case10：coupon
     *                                 Case11：http、https:${網址}
     *                                 Case12：newfriend:${self.memPhone}
     *                                 Case13：nc:${bbType}
     *    }, ......
     *  ]
     *
     * error:
     * </pre>
     * */
    public static void getWalletHomeListData(ResponseListener responseListener,
                                             Activity activity, String tag) throws JSONException {
        String apiName = "WLT010401";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5> WLT050101 動態訊息中心 </h5>
     * <pre>
     * request:
     *  pushType            - String   *訊息類別
     *                                 0：全部(僅會出現在request的參數中)
     *                                 1：紅包
     *                                 2：好友加入
     *                                 3：邀請成為儲值會員
     *                                 4：儲值帳戶
     *                                 5：優惠券
     *                                 6：系統訊息
     *                                 7：電子票券(信用卡正向部分，先保留)
     *
     *  beginDate           - String   起始日期(yyyyMMdd)
     *  endDate             - String   結束日期(yyyyMMdd)
     *
     * response:
     *  pushMsgList : [{
     *    pushSender        - Integer  發送推播者(mem_seq)
     *    senderNickName    - String   發送推播者暱稱
     *    pushMessage       - String   推播訊息內容
     *    senderMessage     - String   發送推播者輸入訊息內容
     *    pushDate          - String   推播時間(yyyyMMddHHmmss)
     *    amount            - double   交易金額
     *    couponTitle       - String   優惠券標題
     *
     *    recepient         - Integer  收到推播者(self.mem_seq)
     *    countType         - String   動態提示統計類別(參考api Description)
     *    pushType          - String   推播類別(參考api Description)
     *    readFlag          - String   讀取狀態(0:未讀1:已讀)(0要秀紅點)
     *
     *    createDate        - String   訊息建立時間(yyyyMMddHHmmss)
     *    moreFlag          - String   大於10筆：Y
     *                                 未超過10筆：N
     *    url               - String   對應6.2 Push Scheme中的url，來自wlt_message_center.push_scheme
     *                                 Case1：webview:${網址}
     *                                 Case2：redhome
     *                                 Case3：redrecordin:${txfSeq}|${txfdSeq}
     *                                 Case4：redrecordout:${txfSeq}|${txfdSeq}
     *                                 Case5：receive:${txfSeq}|${txfdSeq}
     *                                 Case6：outgoing:${txfSeq}|${txfdSeq}
     *                                 Case7：payreq:${txfSeq}|${txfdSeq}
     *                                 Case8：svin
     *                                 Case9：svout
     *                                 Case10：coupon
     *                                 Case11：http、https:${網址}
     *                                 Case12：newfriend:${self.memPhone}
     *                                 Case13：nc:${bbType}
     *    }, ......
     *  ]
     *
     * error:
     * </pre>
     * */
    public static void getMessageCenterListData(String pushType, MonthOption monthOption, ResponseListener responseListener,
                                                Activity activity, String tag) throws JSONException {
        String apiName = "WLT050101";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("pushType", pushType);
        String[] dates = getMonthBeginEndDate(monthOption);
        if(dates != null) {
            tmpBodyParam.put("beginDate", dates[0]);
            tmpBodyParam.put("endDate", dates[1]);
        }

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5> WLT050201 查詢推播狀態 </h5>
     * <pre>
     * request:
     *
     * response:
     *  pushList : [{
     *  psSeq	    Integer		推播設定序號(當設定為開啟時，此欄位會是null)
     *  pushType	String		推播類別
     *  switchFlag	String		設定狀態(0:關閉，1:開啟)
     *  ]
     *
     * error:
     * </pre>
     * */
    public static void loadPushSetting(ResponseListener responseListener,
                                       Activity activity, String tag) throws JSONException {
        String apiName = "WLT050201";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5> WLT050102 好友互動歷史紀錄</h5>
     * <pre>
     * request:
     * memNO	    Integer		*互動好友
     * beginDate	String		起始時間(yyyyMMdd)
     * endDate	    String		結束時間(yyyyMMdd)

     *
     *
     * response:
     *  pushList : [{
     *  psSeq	    Integer		推播設定序號(當設定為開啟時，此欄位會是null)
     *  pushType	String		推播類別
     *  switchFlag	String		設定狀態(0:關閉，1:開啟)
     *  ]
     *
     * error:
     * </pre>
     * */
    public static void getMessageCenterListDataForFriend(String memNO, MonthOption monthOption, ResponseListener responseListener,
                                                Activity activity, String tag) throws JSONException {
        String apiName = "WLT050102";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("memNO", memNO);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        // 如果是兩個月，起始時間為上個月1號，結束日期為今天
        if(monthOption == MonthOption.LATEST_1_MONTH) {
            String[] dates = new String[2];
            // endDate
            dates[1] = df.format(c.getTime());
            // beginDate
            c.set(Calendar.DAY_OF_MONTH, 1);
            dates[0] = df.format(c.getTime());

            tmpBodyParam.put("beginDate", dates[0]);
            tmpBodyParam.put("endDate", dates[1]);
        }

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5> WLT050202 推播設定 </h5>
     * <pre>
     * request:
     *  pushList : [{
     *  psSeq	    Integer		推播設定序號(當設定為開啟時，此欄位會是null)
     *  pushType	String		推播類別
     *  switchFlag	String		設定狀態(0:關閉，1:開啟)
     *  ]
     *
     * response:
     *
     * error:
     * </pre>
     * */
    public static void savePushSetting(ArrayList<PushData> pushDataArrayList, ResponseListener responseListener,
                                       Activity activity, String tag) throws JSONException {
        String apiName = "WLT050202";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        JSONArray pushList = new JSONArray();

        if(pushDataArrayList.size() > 0)
        {

            try{
                for (PushData pushData: pushDataArrayList) {
                    JSONObject object = new JSONObject();
                    object.put("psSeq", pushData.getPsSeq());
                    object.put("pushType", pushData.getPushType());
                    object.put("switchFlag", pushData.getSwitchFlag());
                    pushList.put(object);
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        tmpBodyParam.put("pushList", pushList);


        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5> WLT030301 強制更版查詢 </h5>
     * <pre>
     * request:
     *
     * response:
     *  appDesc            String    版本說明
     *  appVersion         String    最新版號
     *  forceAppVersion    String    強制更新版本號
     *  appUrl             String    下載網址
     *
     * error:
     * </pre>
     * */
    public static void checkAppUpdate(ResponseListener responseListener,
                                      Activity activity, String tag) throws JSONException {
        String apiName = "WLT030301";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5> WLT030104 發送邀請推播 </h5>
     * <pre>
     * request:
     * pushType	    String	*push type(*目前只會有3)
     *                      1:轉帳推播 → 紅包
     *                      2:好友加入
     *                      3:邀請成為儲值會員
     *                      4:儲值帳戶
     *                      5:優惠券
     *                      6:系統訊息
     *                      7:電子票券
     * Repeating Structure : recipientList
     * memNO	    Int		*會員代碼(接收者)
     *
     * response:
     *
     * error:
     * </pre>
     * */
    public static void sendInvitePush(String pushType, ArrayList<String> memNOs, ResponseListener responseListener,
                                      Activity activity, String tag) throws JSONException {
        String apiName = "WLT030104";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("pushType", pushType);

        JSONArray memNOList = new JSONArray();
        if(memNOs.size() > 0)
        {
            JSONObject object = new JSONObject();
            try{
                for (String memNO: memNOs) {
                    object.put("memNO", memNO);
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
            memNOList.put(object);
        }

        tmpBodyParam.put("recipientList", memNOList);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5> WLT030401 點擊事件紀錄 </h5>
     * <pre>
     * request:
     * hitRecordList : [{
     *  hitEvent,           String		E01:邀請好友成為台新錢包成員
                                        E02:邀請錢包好友成為儲值帳號
                                        E03:分享邀請代碼
                                        String		T01:手機號碼
                                        T02:通訊錄聯絡人
                                        T03:Line
                                        T04:facebook
                                        T05:推播
                                        T06:email
                                        T07:簡訊

     *  hitType              String		E01:邀請好友成為台新錢包成員
                                        E02:邀請錢包好友成為儲值帳號
                                        E03:分享邀請代碼
                                        String		T01:手機號碼
                                        T02:通訊錄聯絡人
                                        T03:Line
                                        T04:facebook
                                        T05:推播
                                        T06:email
                                        T07:簡訊

     * }]
     *
     * response:
     *
     * error:
     * </pre>
     * */
    public static void uploadHitRecords(ArrayList<HitRecord> hitRecords, ResponseListener responseListener,
                                        Context context) throws JSONException {
        String apiName = "WLT030401";
        String apiUrl = url + apiName;
        if(mQueueForAnalyticsEven == null)
            mQueueForAnalyticsEven = Volley.newRequestQueue(context);

        JSONObject tmpBodyParam = new JSONObject();

        JSONArray hitRecordList = new JSONArray();

        if(hitRecords.size() > 0)
        {

            try{
                for (HitRecord hitRecord: hitRecords) {
                    JSONObject object = new JSONObject();
                    object.put("hitEvent", hitRecord.getHitEvent());
                    object.put("hitType", hitRecord.getHitType());
                    hitRecordList.put(object);
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        tmpBodyParam.put("hitRecordList", hitRecordList);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, context);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
//        request.setTag(tag);
        mQueueForAnalyticsEven.add(request);
    }

    /**
     * <h5> WLT030402 特殊事件紀錄 </h5>
     * <pre>
     * request:
     * specialEventList : [{
     *      type,	                    String	type	App自訂事件類型
     *      note                        String	note	App自訂內容
     * }]
     *
     * response:
     *
     * error:
     * </pre>
     * */
    public static void uploadSpecialEvents(ArrayList<SpecialEvent> specialEvents, ResponseListener responseListener,
                                           Context context) throws JSONException {
        String apiName = "WLT030402";
        String apiUrl = url + apiName;
        if(mQueueForAnalyticsEven == null)
            mQueueForAnalyticsEven = Volley.newRequestQueue(context);

        JSONObject tmpBodyParam = new JSONObject();

        JSONArray specialEventList = new JSONArray();

        if(specialEvents.size() > 0)
        {

            try{
                for (SpecialEvent specialEvent: specialEvents) {
                    JSONObject object = new JSONObject();
                    object.put("type", specialEvent.getType());
                    object.put("note", specialEvent.getNote());
                    specialEventList.put(object);
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        tmpBodyParam.put("specialEventList", specialEventList);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, context);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
//        request.setTag(tag);
        mQueueForAnalyticsEven.add(request);
    }

    /**
     * <h5> WLT010202 登出錢包系統 </h5>
     * <pre>
     * request:
     *
     * response:
     *
     * error:
     * </pre>
     * */
    public static void logoutWallet(ResponseListener responseListener, Context context) throws JSONException {
        String apiName = "WLT010202";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(context);

        JSONObject tmpBodyParam = new JSONObject();
        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, context);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        request.setTag(tag);
        mQueue.add(request);
    }
}

