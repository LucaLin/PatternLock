package tw.com.taishinbank.ewallet.util.http;

import android.app.Activity;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseErrorListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseListener;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.model.sv.TxOne;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;

/**
 * 與儲值有關的 Http Request (Restful API)
 *
 * Created by oster on 2016/1/8.
 */
public class SVHttpUtil extends HttpUtilBase {

    private static final String TAG = "SVHttpUtil";

    /**
     * <h5>4.30 WLT020201　會員台幣帳號歸戶查詢</h5>
     * <pre>
     * request:
     *
     * response:
     *  account
     *  balance
     *  acctType
     *  interestCate
     *
     * error:
     *  E0011 - SV Token expired(Over 10 minutes)
     *  V0001 - SV Not Login
     * </pre>
     * */
    public static void inquiryAccountList(ResponseListener responseListener,
                                          Activity activity, String tag) throws JSONException {
        String apiName = "WLT020201";
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
     * <h5>4.31 WLT020202　查詢約定提領帳號</h5>
     * <pre>
     * request:
     *
     * response:
     *  bankCode
     *  bankName
     *  account
     *
     * error:
     *  E0011 - SV Token expired(Over 10 minutes)
     *  V0001 - SV Not Login
     * </pre>
     *
     * */
    public static void inquiryDesignateAccount(ResponseListener responseListener,
                                               Activity activity, String tag) throws JSONException {
        String apiName = "WLT020202";
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
     * <h5>4.32 WLT020203　設定約定提領帳號</h5>
     * <pre>
     * request:
     *  bankType * - 1:台新銀行、2:其他銀行
     *  bankCode * 銀行代碼
     *  account  * 帳號
     *  pwd      *SV密碼(加密)
     *
     * response:
     *  bankCode
     *  bankName
     *  account
     *
     * error:
     *  E0011 - SV Token expired(Over 10 minutes)
     *  V0001 - SV Not Login
     * </pre>
     * @see tw.com.taishinbank.ewallet.interfaces.GlobalConst#CODE_IS_TAISHIN
     * @see tw.com.taishinbank.ewallet.interfaces.GlobalConst#CODE_IS_NOT_TAISHIN
     * */
    public static void saveDesignateAccount(int bankType, String bankCode, String account, String pwd,
                                            ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020203";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("bankType", bankType);
        tmpBodyParam.put("bankCode", bankCode);
        tmpBodyParam.put("account", account);
        tmpBodyParam.put("pwd", pwd);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>4.33 WLT020204 儲值</h5>
     * <pre>
     * request:
     *  account - *台幣活存帳號(來自WLT020201 會員台幣帳號歸戶查詢)
     *  amount  - *儲值金額
     *  pwd     - *SV密碼(加密)
     *
     * response:
     *  result  - Y: 加值成功, N: 加值失敗
     *  amount  - 加值金額
     *  balance - 加值後帳號餘額
     *  txTime  - SV交易時間
     *  txfSeq  - 交易序號
     *  txfdSeq - 交易明細序號
     *  rtnMsg  - 當result=N時，帶入SV rtnMsg
     *
     * error:
     *  E0011 - SV Token expired(Over 10 minutes)
     *  V0001 - SV Not Login
     * </pre>
     * */
    public static void depositeSVAccount(String account, int amount, String pwd,
                                         ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020204";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("account", account);
        tmpBodyParam.put("amount", amount);
        tmpBodyParam.put("pwd", pwd);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);

        String logNote = EventAnalyticsUtil.logFormatToAPI(apiName, "", true);
        EventAnalyticsUtil.addSpecialEvent(activity, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, logNote));
    }

    /**
     * <h5>4.34 WLT020205 提領</h5>
     * <pre>
     * request:
     *  amount  - *儲值金額
     *  pwd     - *SV密碼(加密)
     *
     * response:
     *  result  - Y: 加值成功, N: 加值失敗
     *  amount  - 加值金額
     *  balance - 加值後帳號餘額
     *  fee     - 手續費
     *  txTime  - SV交易時間
     *  txfSeq  - 交易序號
     *  txfdSeq - 交易明細序號
     *  rtnMsg  - 當result=N時，帶入SV rtnMsg
     *  monthlyCurr - 提領後本月交易額度
     *
     * error:
     *  E0011 - SV Token expired(Over 10 minutes)
     *  V0001 - SV Not Login
     * </pre>
     * */
    public static void withdrawSVAccount(int amount, String pwd,
                                         ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020205";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("amount", amount);
        tmpBodyParam.put("pwd", pwd);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);

        String logNote = EventAnalyticsUtil.logFormatToAPI(apiName, "", true);
        EventAnalyticsUtil.addSpecialEvent(activity, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, logNote));
    }

    /**
     * <h5>4.35 WLT020206 一般紅包、財神紅包、回禮紅包、轉帳付款</h5>
     * <pre>
     * request:
     *  message - 訊息(發出者)
     *  txType  - *1:一般紅包, 2:財神紅包 4:回禮紅包(只會有一筆) 5:轉帳付款
     *  amount  - *儲值金額
     *  pwd     - *SV密碼(加密)
     *  replyToTxfdSeq - 回禮交易明細序號(*txType=4時，此欄位必填，為何筆交易的回禮)
     *  txList  - [{
     *      txToMemNO - *要發送給誰的會員號碼
     *      perAmount - *轉出金額
     *    }, ......
     *  ]
     *
     *
     * response:
     *  amount     - 加值金額
     *  sender     - 加值後帳號餘額
     *  senderMem  - 手續費
     *  balance    - SV交易時間
     *  createDate - 交易序號
     *  txfSeq     - 交易明細序號
     *  txResult : [{
     *    perAmount - 金額
     *    name      - 稱呼
     *    toMem     - 收到者memSeq
     *    result    - Y: 轉帳成功, N: 轉帳失敗
     *    bancsMsg  - 轉帳訊息
     *    txfdSeq   - 交易明細序號
     *    account   - 帳號 (轉入帳號)
     *    }, ......
     *  ]
     *
     * error:
     *  E0011 - SV Token expired(Over 10 minutes)
     *  V0001 - SV Not Login
     * </pre>
     * */
    public static void trasnferFromSVAccountTo(String message, String txType, double amount, String pwd,
                                               int replyToTxfdSeq, List<TxOne> txList,
                                               ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020206";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("message", message);
        tmpBodyParam.put("txType", txType);
        tmpBodyParam.put("amount", amount);
        tmpBodyParam.put("pwd", pwd);
        if(txType.equals(RedEnvelopeType.TYPE_REPLY) && replyToTxfdSeq >= 0) {
            tmpBodyParam.put("replyToTxfdSeq", replyToTxfdSeq);
        }

        JSONArray tempTxList = new JSONArray();
        for (TxOne txOne : txList) {
            JSONObject tmpTxOneParam = new JSONObject();
            tmpTxOneParam.put("txToMemNO", txOne.getTxToMemNO());
            tmpTxOneParam.put("perAmount", txOne.getPerAmount());
            tempTxList.put(tmpTxOneParam);
        }
        tmpBodyParam.put("txList", tempTxList);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>4.36 WLT020207 留言回覆</h5>
     * <pre>
     * request:
     *  message   - *訊息內容
     *  txfSeq    - *交易訊號
     *  txfdSeq   - *交易明細序號
     *
     * response:
     *
     * error:
     *
     * </pre>
     * */
    public static void replyMessage(String message, int txfSeq, int txfdSeq,
                                    ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020207";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("message", message);
        tmpBodyParam.put("txfSeq", txfSeq);
        tmpBodyParam.put("txfdSeq", txfdSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>4.37 WLT020208 轉帳收款、均分收款</h5>
     * <pre>
     * request:
     *  message   - 訊息
     *  txType    - *6:轉帳收款(邀請付款)  7.均分收款(邀請付款)
     *  amount    - *總金額
     *  txList: [
     *    txMemNO   - *要發送給誰的memSeq
     *    perAmount - *請求金額
     *  ]
     *
     * response:
     *  result     - 加值金額
     *  message    - 加值後帳號餘額
     *  amount     - 手續費
     *  txfSeq    - SV交易時間
     *  createDate - 交易序號
     *  txResult : [{
     *      perAmount - 金額
     *      name      - 稱呼
     *      txMemNo   - 收到者memSeq
     *    }, ......
     *  ]
     *
     * error:
     *
     * </pre>
     * */
    public static void sendPaymentRequest(String message, String txType, double amount, List<TxOne> txList,
                                          ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020208";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("message", message);
        tmpBodyParam.put("txType", txType);
        tmpBodyParam.put("amount", amount);

        JSONArray tempTxList = new JSONArray();
        for (TxOne txOne : txList) {
            JSONObject tmpTxOneParam = new JSONObject();
            tmpTxOneParam.put("txMemNO", txOne.getTxToMemNO());
            tmpTxOneParam.put("perAmount", txOne.getPerAmount());
            tempTxList.put(tmpTxOneParam);
        }
        tmpBodyParam.put("txList", tempTxList);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);

        String logNote = EventAnalyticsUtil.logFormatToAPI(apiName, String.format("txType: %1$s", txType), true);
        EventAnalyticsUtil.addSpecialEvent(activity, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, logNote));
    }

    /**
     * <h5>4.37 WLT020209 確認付款-付款請求</h5>
     * <pre>
     * request:
     *  txfSeq  - *交易序號
     *  txfdSeq - *交易明細序號
     *  message - 訊息(寫進recipient_message)
     *  pwd     - *SV密碼(加密)
     *  amount  - 金額
     *  toMemNO - *發出付款請求方memSeq
     *
     * response:
     *  result    - Y: 加值成功, N: 加值失敗
     *  bancsMsg  - 轉帳訊息
     *  amount    - 加值後帳號餘額
     *  sender    - 轉帳金額
     *  senderSeq - 轉出者memSeq
     *  balance   - 儲值帳戶餘額
     *  createDate- 交易時間(YYYYMMDDHHMISS)
     *  txfSeq    - 交易序號
     *  txfdSeq   - 交易明細序號
     *  toAccount - 帳號(轉入帳號)
     *  toMemNO   - 收到者memSeq
     *  name      - 收到者稱呼
     *
     * error:
     *  E0011 - SV Token expired(Over 10 minutes)
     *  V0001 - SV Not Login
     * </pre>
     * */
    public static void transferForPaymentReq(int txfSeq, int txfdSeq, String message, String pwd,
                                             int amount, int memSeq,
                                             ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020209";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);
        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("txfSeq", txfSeq);
        tmpBodyParam.put("txfdSeq", txfdSeq);
        tmpBodyParam.put("message", message);
        tmpBodyParam.put("pwd", pwd);
        tmpBodyParam.put("amount", amount);
        tmpBodyParam.put("toMemNO", memSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);

        String logNote = EventAnalyticsUtil.logFormatToAPI(apiName, String.format("toMemNO: %1$s", memSeq), true);
        EventAnalyticsUtil.addSpecialEvent(activity, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, logNote));
    }

    /**
     * <h5>4.39 WLT020210 付款請求 － 取消付款、再次發送</h5>
     * <pre>
     * request:
     *  txfSeq      - *交易序號
     *  txfdSeq     - *交易明細序號
     *  operateType - *操作類型(1:取消付款 2:再次發送)
     *
     * response:
     *
     * error:
     *
     * </pre>
     * */
    public static void updatePaymentRequest(int txfSeq, int txfdSeq, String operateType,
                                            ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020210";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);
        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("txfSeq", txfSeq);
        tmpBodyParam.put("txfdSeq", txfdSeq);
        tmpBodyParam.put("operateType", operateType);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);

        String logNote = EventAnalyticsUtil.logFormatToAPI(apiName, String.format("operateType: %1$s", operateType), true);
        EventAnalyticsUtil.addSpecialEvent(activity, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, logNote));
    }

    /**
     * <h5>4.40 WLT020211 帳戶支出紀錄</h5>
     * <pre>
     * request:
     *  beginDate      - 起始日期(yyyyMMdd)
     *  endDate        - 結束日期(yyyyMMdd)
     *  txfdSeq        - 交易明細序號(查單筆時必填)
     *  txfSeq         - 交易序號(查單筆時必填)
     *
     * response:
     *  txType
     *  txStatus
     *  amount
     *  createDate
     *  txToMemNO
     *  name
     *  txfSeq
     *  txfdSeq
     *  senderMessage
     *  replyMessage
     *  replyDate
     *
     * error:
     *
     * </pre>
     * */
    public static void queryExpendTxLog(MonthOption monthOption, String txfdSeq, String txfSeq,
                                        ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020211";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);
        JSONObject tmpBodyParam = new JSONObject();
        String[] dates = getMonthBeginEndDate(monthOption);
        if(dates != null) {
            tmpBodyParam.put("beginDate", dates[0]);
            tmpBodyParam.put("endDate", dates[1]);
        }
        if(!TextUtils.isEmpty(txfdSeq)) {
            tmpBodyParam.put("txfdSeq", txfdSeq);
        }
        if(!TextUtils.isEmpty(txfSeq)) {
            tmpBodyParam.put("txfSeq", txfSeq);
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
     * <h5>4.41 WLT020212 帳戶支出紀錄</h5>
     * <pre>
     * request:
     *  beginDate      - 起始日期(yyyyMMdd)
     *  endDate        - 結束日期(yyyyMMdd)
     *  txfdSeq        - 交易明細序號(查單筆時必填)
     *  txfSeq         - 交易序號(查單筆時必填)
     *
     * response:
     *  txType        - 交易類型5:轉帳付款6:轉帳收款(邀請付款) 7.均分收款(邀請付款) 8:加值
     *  txStatus      - txType=6、7 需一併帶入此狀態，此狀態來自  WLT_TX_transfer_detail
     *                  (0:已交易, 1:尚未交易, 2.交易取消)
     *  amount        - 總金額
     *  createDate    - 交易時間(YYYYMMDDHHMISS)
     *  txToMemNO     - 付款方memSeq
     *  txMemName     - 付款方稱呼
     *  txfSeq        - 交易序號
     *  txfdSeq       - 交易明細序號
     *  senderMessage - 發出的訊息(付款方)
     *  replyMessage  - 回覆訊息
     *  replyDate     - 回覆訊息時間(YYYYMMDDHHMISS)
     *
     * error:
     *
     * </pre>
     * */
    public static void queryIncomeTxLog(MonthOption monthOption, String txfdSeq, String txfSeq,
                                        ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT020212";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);
        JSONObject tmpBodyParam = new JSONObject();
        String[] dates = getMonthBeginEndDate(monthOption);
        if(dates != null) {
            tmpBodyParam.put("beginDate", dates[0]);
            tmpBodyParam.put("endDate", dates[1]);
        }
        if(!TextUtils.isEmpty(txfdSeq)) {
            tmpBodyParam.put("txfdSeq", txfdSeq);
        }
        if(!TextUtils.isEmpty(txfSeq)) {
            tmpBodyParam.put("txfSeq", txfSeq);
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
     * <h5>WLT020213 儲值帳戶紀錄</h5>
     * <pre>
     * request:
     *
     * response:
     *  hasNew  String  是否有新紀錄(N:沒有、Y:有)顯示紅點
     *
     * error:
     *
     * </pre>
     *
     * */
    public static void getSVHomeHasNew(ResponseListener responseListener,
                                               Activity activity, String tag) throws JSONException {
        String apiName = "WLT020213";
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
}
