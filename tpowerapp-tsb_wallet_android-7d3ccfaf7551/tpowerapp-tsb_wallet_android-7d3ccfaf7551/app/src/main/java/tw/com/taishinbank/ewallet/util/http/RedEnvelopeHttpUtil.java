package tw.com.taishinbank.ewallet.util.http;

import android.app.Activity;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseErrorListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseListener;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;

/**
 * 紅包相關的api
 * Created by liuyu on 2015/11/22.
 */

public class RedEnvelopeHttpUtil extends HttpUtilBase {


    static final int TIMEOUT_SEND_RED_ENVELOPE = 90 * 1000; // 預設為90秒

    private static final String TAG = "RedEnvelopeHttpUtil";


    /*4.12	WLT020102 紅包首頁(新增未讀)
    * <request>
    * (非必填)
    * txDateBegin   *收到錢包區間起始日(YYYYMM)
    * txDateEnd     *收到錢包區間結束日(YYYYMM)
    *
    * <response>
    * sender        發出者稱呼
    * amount        紅包金額
    * createDate    收到紅包的時間(YYYYMMDDHHMISS)
    * senderMessage 發出者的訊息
    * txfdSeq       交易明細序號
    * txfSeq        交易序號
    * replyMessage  回覆訊息
    * replyTime     回覆訊息時間(YYYYMMDDHHMISS)
    * lastUpdate    最後更新(YYYYMMDDHHMISS)
    * senderMem     發出者memSeq
    * toMem         收到者memSeq
    * toMemName     收到者稱呼(回覆訊息的人)
    * txType        1:一般紅包 2:財神紅包
    * readFlag      讀取狀態(0:未讀1:已讀)(0要秀紅點)
    *
    * <error>
    * */
    public static void getRedEnvelopeHomePage(String txDateBegin, String txDateEnd,
                                              ResponseListener responseListener,
                                              Activity activity, String tag) throws JSONException {
        String apiName = "WLT020102";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);
        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("txDateBegin",txDateBegin);
        tmpBodyParam.put("txDateEnd", txDateEnd);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.13	WLT020103 收到的紅包(新增未讀)
    * <request>
    * (日期非必填)
    * txDateBegin   *收到錢包區間起始日(YYYYMM)
    * txDateEnd     *收到錢包區間結束日(YYYYMM)
    * txfdSeq       *交易明細序號(查單筆時必填)
    * txfSeq        *交易序號(查單筆時必填)
    *
    * <response>
    * txSelfList,
    * txHeaderList:txDetailList
    *
    * <error>
    * */
    public static void getRedEnvelopeReceived(String txDateBegin, String txDateEnd, String txfdSeq, String txfSeq,
                                              ResponseListener responseListener,
                                              Activity activity, String tag) throws JSONException {
        String apiName = "WLT020103";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("txDateBegin",txDateBegin);
        tmpBodyParam.put("txDateEnd", txDateEnd);
        tmpBodyParam.put("txfdSeq",txfdSeq);
        tmpBodyParam.put("txfSeq", txfSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.14	WLT020104 發出的紅包(新增未讀)
    * <request>
    * (日期非必填)
    * txDateBegin   *收到錢包區間起始日(YYYYMM)
    * txDateEnd     *收到錢包區間結束日(YYYYMM)
    * txfdSeq       *交易明細序號(查單筆時必填)
    * txfSeq        *交易序號(查單筆時必填)
    *
    * <response>
    * txHeaderList:txDetailList
    *
    * <error>
    * */
    public static void getRedEnvelopeSend(String txDateBegin, String txDateEnd, String txfdSeq, String txfSeq,
                                          ResponseListener responseListener,
                                          Activity activity, String tag) throws JSONException {
        String apiName = "WLT020104";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        tmpBodyParam.put("txDateBegin",txDateBegin);
        tmpBodyParam.put("txDateEnd", txDateEnd);
        tmpBodyParam.put("txfdSeq",txfdSeq);
        tmpBodyParam.put("txfSeq", txfSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /*4.15	WLT020105 查詢帳戶資訊
    * <request>
    * (header 的 svTokenID(儲值主機驗證碼) 此欄位必填)
    *
    * <response>
    * prepaidAccount	儲值支付帳號
    * accountLevel	    帳戶等級
    * balance			儲值帳戶餘額
    *
    * depositLimCurr	儲值限額
    * singleLimCurr	    單筆交易限額
    * dailyLimCurr	    每日交易限額
    * monthlyLimCurr	每月交易限額
    *
    * depositLimMax	    法定儲值限額
    * singleLimMax	    法定單筆限額
    * dailyLimMax		法定每日限額
    * monthlyLimMax	    法定每月限額
    *
    * dailyAmount		當日累積交易金額
    * monthlyAmount	    當月累積交易金額
    *
    * <error>
    * */
    public static void getSVAccountInfo(ResponseListener responseListener,
                                        Activity activity, String tag) throws JSONException {
        String apiName = "WLT020105";
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

    public static void sendRedEnvelope(String message, String type, double totalAmount,
                                       String[] memNos, String[] amounts, String mima, String replyToTxfdSeq,
                                       ResponseListener responseListener,
                                       Activity activity, String tag) throws JSONException {
        String apiName = "WLT020206";
        String apiUrl = url + apiName;
//        String apiUrl = url + apiName+"F"; // 可測試包含失敗的mock
        String logNote = EventAnalyticsUtil.logFormatToAPI(apiName, String.format("Type: %1$s", type), true);

        
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        // String 紅包訊息(發出者)
        tmpBodyParam.put("message", message);
        // String *1:一般紅包, 2:財神紅包
        tmpBodyParam.put("txType", type);
        // Double *總金額
        tmpBodyParam.put("amount", totalAmount);
        // String SV密碼(加密)
        tmpBodyParam.put("pwd", mima);
        // 回禮交易明細序號(*txType=4時，此欄位必填，為何筆交易的回禮)
        if(type.equals(RedEnvelopeType.TYPE_REPLY) && !TextUtils.isEmpty(replyToTxfdSeq)){
            tmpBodyParam.put("replyToTxfdSeq", replyToTxfdSeq);
            logNote += " replyToTxfdSeq: " + replyToTxfdSeq;
        }

        JSONArray txList = new JSONArray();
        if(memNos != null && amounts != null) {
            for (int i = 0; i < memNos.length; i++) {
                JSONObject object = new JSONObject();
                try {
                    // TODO 改成正確型態
                    // 要發送給誰的會員號碼(Int)
                    object.put("txToMemNO", memNos[i]);
                    // 轉出金額(Double)
                    object.put("perAmount", Double.valueOf(amounts[i]));
                    txList.put(object);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        tmpBodyParam.putOpt("txList", txList);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_SEND_RED_ENVELOPE, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);

        EventAnalyticsUtil.addSpecialEvent(activity, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, logNote));
    }


    /*4.17	WLT020207　回覆紅包訊息、留言回覆
    * <request>
    * txfSeq        * 交易序號
    * txfdSeq       * 交易明細序號
    * replyMsg      * 回覆訊息
    *
    * <response>
    *
    * <error>
    * */
    public static void replyRedEnvelopeMsg(String txfSeq, String txfdSeq, String replyMsg,
                                           ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020207";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("txfSeq", txfSeq);
        tmpBodyParam.put("txfdSeq", txfdSeq);
        tmpBodyParam.put("replyMsg", replyMsg);
        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }


    /** <h5> WLT030101 手機號碼認證 </h5>
     * <pre>
     * request:
     *  phone        * 10碼手機號碼
     *  createDate   由推播訊息呼叫此API時，須帶入該則訊息的createDate
     *  url          由推播訊息呼叫此API時，須帶入該則訊息的url
     *
     * response:
     *  wltFlag      Y: 錢包會員/N: 非錢包會員
     *  svFlag       Y: 儲值會員/N: 非儲值會員
     *  name         暱稱(若無則回姓+名)
     *  memNO        會員序號(mem_seq)
     *  phone        手機號碼
     *
     * error:
     *
     * </pre>
     * */
    public static void getAccountCheckByPhone(String phone, String createDate, String pushUrl,
                                              ResponseListener responseListener,
                                              Activity activity, String tag) throws JSONException {
        String apiName = "WLT030101";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("phone", phone);

        if(!TextUtils.isEmpty(createDate)){
            tmpBodyParam.put("createDate", createDate);
        }
        if(!TextUtils.isEmpty(pushUrl)){
            tmpBodyParam.put("url", pushUrl);
        }

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }
}