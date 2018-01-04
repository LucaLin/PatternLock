package tw.com.taishinbank.ewallet.util.http;

import android.app.Activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseErrorListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseListener;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;

/**
 * 與信用卡有關的 Http Request (Restful API)
 *
 * Created by Siang on 2016/1/8.
 */
public class CreditCardHttpUtil extends HttpUtilBase {

    private static final String TAG = "CreditCardHttpUtil";

    /**
     * <h5>WLT040101 信用卡交易紀錄查詢</h5>
     * <pre>
     * request:
     * verifyID *忘記密碼驗證用
     * memNO    *會員代碼(mem_seq)
     * vCode    *認證碼
     *
     * response:
     * totalCount 交易紀錄筆數
     * -- walletPaymentList object--
     * storeName	        String		交易店點名稱
     * merchantTradeDate	String		交易日期
     * tradeAmount	        double		交易金額
     * tradeStatus	        int		交易狀態
     * tradeStatusName	    String		交易狀態名稱
     * cardNumberShelter	String		信用卡號
     * cardName	            String		信用卡別(待確認)
     * bankServicePhone 	String		信用卡發卡行服務電話
     * bankName	            String		信用卡發卡行
     * storeTel	            String		商家電話
     * storeAddress	        String		商家住址
     * error:
     * </pre>
     * */
    public static void queryCreditCardTransactionLog(String beginDate, String endDate, String tradeStatus, ResponseListener responseListener,
                                                     Activity activity, String tag) throws JSONException {
        String apiName = "WLT040101";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        if(beginDate != null && !beginDate.equals(""))
            tmpBodyParam.put("beginDate", beginDate);
        if(endDate != null && !endDate.equals(""))
            tmpBodyParam.put("endDate", endDate);
        tmpBodyParam.put("tradeStatus", tradeStatus);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    //正向 掃描付款
    /**
     * <h5>WLT070101 取得訂單交易授權</h5>
     * <pre>
     *
     * response:
     * orderToken	String		*與墨攻訂單交易授權碼
     * </pre>
     * */
    public static void GetTicketOrderToken(ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT070101";
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
     * <h5>WLT070102 查詢建立訂單</h5>
     * <pre>
     * request
     * orderToken   	String		*訂單授權碼
     * orderId      	String		*訂單單號
     *
     * response:
     * platformId	    String		票券商城平台的 ID
     * mid	            String		用來表示商城中的特定店家
     * orderId	        String		商城平台自訂的訂單編號
     * price	        Double		訂單總價格
     * isRefundable	    String		訂單是否可退貨(1/0)
     * isPayable	    String		可否再次進行交易(1.可 / 0. 不可)
     * memo	            String		備註
     * title	        String		商品名稱
     * unitPrice       	Double		票券單價
     * iconUrl	        String		圖示網址
     * note	            String		電子票券須知
     * storeTel	        String		商家客服電話
     * storeAddress	    String		商家位置
     * storeName	    String		商家名稱
     * count	        Integer		數量
     * status       	String		訂單狀態
                                     0. 新增
                                     1. 確認中
                                     2. 付款中
                                     27.付款失敗
                                     3. 產生票券中
                                     37.票券產生失敗
                                     4. 交易完成
     * createDate	    String		產生日期
     * lastUpdate	    String		最後修改日期
     *
     *
     *
     * </pre>
     * */
    public static void queryTickenOrderDetail(String orderToken, String orderId, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT070102";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("orderToken", orderToken);
        tmpBodyParam.put("orderId", orderId);
        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>WLT070103 訂單交易</h5>
     * <pre>
     * request
     * orderToken	    String		*訂單交易授權碼
     * orderId	        String		*訂單單號
     * cardToken	    String		*信用卡卡片授權
     *
     * response:
     *
     * </pre>
     * */
    public static void paymentOrderFormCreditCard(String orderToken, String orderId, String cardToken, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT070103";
        String apiUrl = url + apiName;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("orderToken", orderToken);
        tmpBodyParam.put("orderId", orderId);
        tmpBodyParam.put("cardToken", cardToken);
        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);

        String logNote = EventAnalyticsUtil.logFormatToAPI(apiName, "OrderID: " + orderId, true);
        EventAnalyticsUtil.addSpecialEvent(activity, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, logNote));
    }

    /**
     * FOR TEST
     * {
     "Mid": "<string>",
     "OrderId":"<string>",
     "Price":"200",
     "Memo":"<string>",
     "IsRefundable":"1 or 0",
     "PlatformId":"1",
     "OrderDetail":{
     "Title":"<String>",
     "Price":"3000",
     "IconUrl": "",
     "Note":"",
     "StoreTel":"",
     "StoreAddress":"",
     "StoreName": "",
     "Count":4
     }
     }
     */
//    public static void createOrder(final String orderToken, String orderId, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
//
//        String apiName = "WLT070201";
//        String apiUrl = "http://10.13.114.85:8092/walletAPI/provider/" + apiName;
//
//        if(mQueue == null)
//            mQueue = Volley.newRequestQueue(activity);
//
//        JSONObject tmpBodyParam = new JSONObject();
//        tmpBodyParam.put("Mid", "OSTER-INF-1");
//        tmpBodyParam.put("OrderId", orderId);
//        tmpBodyParam.put("Price", 2);
//        tmpBodyParam.put("Memo", "Jack商品剛出爐，請小心使用");
//        tmpBodyParam.put("IsRefundable", "1");
//        tmpBodyParam.put("PlatformId", "1");
//
//        JSONObject tmpOrderDetail = new JSONObject();
//        tmpOrderDetail.put("Title", "Jack女僕服務一日券"+orderId);
//        tmpOrderDetail.put("Price", 1);
//        tmpOrderDetail.put("IconUrl","");
//        tmpOrderDetail.put("Note", "為保持Code得品質，請耐心等待");
//        tmpOrderDetail.put("StoreTel", "02-2345-1134");
//        tmpOrderDetail.put("StoreAddress", "台北市內湖區舊宗路207號");
//        tmpOrderDetail.put("StoreName", "Jack賣身店");
//        tmpOrderDetail.put("Count", 2);
//
//        tmpBodyParam.put("OrderDetail", tmpOrderDetail);
//
//        JsonObjectRequest request = new JsonObjectRequest(apiUrl, tmpBodyParam, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("test create payment", response.toString());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//            public Map<String, String> getHeaders() {
//                Map<String, String> mHeaders = new HashMap<String, String>();
//                mHeaders.put("txDate", DATE_FORMAT.format(new java.util.Date()));
//                mHeaders.put("orderToken", orderToken);
//                mHeaders.put("apiCode", "WLT070201");
//
//                return mHeaders;
//            }
//        };
//
//        mQueue.add(request);
//    }

}
