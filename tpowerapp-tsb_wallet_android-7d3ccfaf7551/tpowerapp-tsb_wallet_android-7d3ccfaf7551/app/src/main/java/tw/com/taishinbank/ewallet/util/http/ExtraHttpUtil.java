package tw.com.taishinbank.ewallet.util.http;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseErrorListener;
import tw.com.taishinbank.ewallet.listener.JsonResponseListener;
import tw.com.taishinbank.ewallet.model.FileRawData;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.http.event.DownloadEvent;

/**
 * 與信用卡有關的 Http Request (Restful API)
 *
 * Created by Siang on 2016/1/8.
 */
public class ExtraHttpUtil extends HttpUtilBase {

    private static final String TAG = "ExtraHttpUtil";

    /**
     * <h5>WLT060101 查詢已使用優惠券</h5>
     * <pre>
     * request:
     *
     * response:
     * cpSeq            優惠券序號
     * msmSeq           會員/優惠券序號mapping序號
     * status           序號狀態(1:優惠活動 2:好友贈送)
     * lastUpdate       最後更新(YYYYMMDDHHMISS)，倒序
     * readFlag         讀取狀態(0:未讀1:已讀)(0要秀紅點)
     * title            優惠券主標
     * subTitle         優惠券副標
     * imagePathS       優惠券圖片路徑(小)
     * imagePathM       優惠券圖片路徑(中)
     * imagePathL       優惠券圖片路徑(大)
     *
     * error:
     * </pre>
     * */
    public static void queryUsedCoupon(ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT060101";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }


    /**
     * <h5>WLT060102 查詢未使用優惠券</h5>
     * <pre>
     * request:
     *
     * response:
     * cpSeq            優惠券序號
     * msmSeq           會員/優惠券序號mapping序號
     * status           序號狀態(1:優惠活動 2:好友贈送)
     * lastUpdate       最後更新(YYYYMMDDHHMISS)，倒序
     * readFlag         讀取狀態(0:未讀1:已讀)(0要秀紅點)
     * title            優惠券主標
     * subTitle         優惠券副標
     * imagePathS       優惠券圖片路徑(小)
     * imagePathM       優惠券圖片路徑(中)
     * imagePathL       優惠券圖片路徑(大)
     *
     * error:
     * </pre>
     * */
    public static void queryUnusedCoupon(ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT060102";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>WLT060103查詢優惠券明細</h5>
     * <pre>
     * request:
     *  cpSeq  int  優惠券序號
     *  msmSeq int  會員優惠券序號mapping序號
     *
     * response:
     * cpSeq                int 優惠券序號
     * msmSeq               int 會員/優惠券序號mapping序號
     * status            String 序號狀態(1:優惠活動 2:好友贈送)
     * lastUpdate        String 最後更新(YYYYMMDDHHMISS)，倒序
     * readFlag          String 讀取狀態(0:未讀1:已讀)(0要秀紅點)
     * title             String 優惠券主標
     * subTitle          String 優惠券副標
     * imagePathS        String 優惠券圖片路徑(小)
     * imagePathM        String 優惠券圖片路徑(中)
     * imagePathL        String 優惠券圖片路徑(大)
     * content           String 優惠券內容
     * notes             String 優惠券注意事項
     * startDate         String 使用時間起日(YYYYMMDDHHMISS)
     * endDate           String 使用時間迄日(YYYYMMDDHHMISS)
     * branchFlag        String 是否有分店(Y:有N:無)(Y要秀箭頭)
     * serialDisplayType String 序號顯示類型
     *                          1:全部顯示(預設)
     *                          2:QR CODE
     *                          3:BAR CODE
     *                          4:文字
     *                          5:不顯示
     *                          6:QR CODE and BAR CODE
     *                          7:QR CODE and文字
     *                          8:BAR CODE and文字
     * onLineDate        String 上線日期(YYYYMMDDHHMISS)
     * downLineDate      String 下線日期(YYYYMMDDHHMISS)
     * storeName         String 店家名稱
     * storeAddress      String 店家地址
     * storePhone        String 店家電話
     * senderMemNO          int 轉送者會員代碼(mem_seq)
     *                          (status=3為自己)
     * senderNickName    String 轉送者暱稱
     * senderMessage     String 轉送者留言
     * senderDate        String 轉送者留言日期(YYYYMMDDHHMISS)
     * toMemNO              int 收到者會員代碼(mem_seq)
     *                          (status=1、2、4為自己)
     * toMemNickName     String 收到者暱稱
     * replyMessage      String 收到者留言
     * replyDate         String 收到者留言日期(YYYYMMDDHHMISS)
     * createDate        String 優惠券領取時間(YYYYMMDDHHMISS)
     * exchangeDate      String 優惠券兌換時間(YYYYMMDDHHMISS)
     * serialNO          String 優惠券序號(看serialDisplayType要以哪種形式顯示)
     *
     * taSeq             Integer 票券商系統編號，用來下載圖檔
     * ticketAgencyPhone   String  票券商電話
     * ticketAgencyAddress String  票券商地址
     * error:
     * </pre>
     * */
    public static void queryCouponDetail(String cpSeq, String msmSeq, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT060103";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("cpSeq", cpSeq);
        tmpBodyParam.put("msmSeq", msmSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * WLT060201領取優惠券
     * @param shareCode
     * @param responseListener
     * @param activity
     * @param tag
     */
    public static void earnCoupon(String shareCode, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT060201";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("shareCode", shareCode);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }


    /**
     * WLT060202 轉送優惠券
     * @param cpSeq Request
     * @param msmSeq
     * @param message
     * @param toMemNO    *收到者會員代碼(mem_seq)
     * @param responseListener
     * @param activity
     * @param tag
     *
     * <pre>
     * Response: NA
     */
    public static void sendMessage(int cpSeq, int msmSeq, String message, String toMemNO, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT060202";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("cpSeq", cpSeq);
        tmpBodyParam.put("msmSeq", msmSeq);
        tmpBodyParam.put("message", message);
        tmpBodyParam.put("toMemNO", toMemNO);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }



    /**
     * WLT060204回覆優惠券訊息
     * @param cpSeq Request
     * @param msmSeq
     * @param message
     * @param responseListener
     * @param activity
     * @param tag
     * <pre>
     *     Response: NA
     *
     */
    public static void replyMessage(int cpSeq, int msmSeq, String message, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT060204";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("cpSeq", cpSeq);
        tmpBodyParam.put("msmSeq", msmSeq);
        tmpBodyParam.put("message", message);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * <h5>WLT010314查詢分享碼</h5>
     * <pre>
     * request:
     *  NA
     *
     * response:
     *  shareCode            String *分享馬（6碼英數、大寫）
     *
     * error:
     * </pre>
     * */
    public static void queryInviteCode(ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT010314";
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
     * WLT060203 兌換優惠券
     * @param msmSeq    int		*會員優惠券序號mapping序號
     * @param tag
     * <pre>
     * Response: NA
     * </pre>
     *
     */
    public static void tradedCoupon(int msmSeq, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT060203";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("msmSeq", msmSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }



    /**
     * WLT060104 查詢優惠券店家
     * @param cpSeq    int		*優惠券序號
     * @param tag
     * <pre>
     * Response: NA
     * </pre>
     *
     */
    public static void queryStoreInformation(int cpSeq, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT060104";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("cpSeq", cpSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * WLT070104 電子票券查詢
     * @param type    int		0.未使用 / 1.已使用 / 2.已退貨
     * @param tag
     * <pre>
     *
     * </pre>
     *
     */
    public static void queryTickets(int type, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT070104";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("type", type);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * WLT070105 電子票券查詢 – 單筆明細
     * @param etkSeq	Int		*電子票券在錢包系統ID
     * <pre>
     *
     * </pre>
     *
     */
    public static void queryTicketDetail(String etkSeq, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT070105";
        String apiUrl = url + apiName;

        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("etkSeq", etkSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * WLT070106  退貨訂單查詢
     * @param odrSeq	Int		*訂單 錢包系統ID
     * <pre>
     *
     * </pre>
     *
     */
    public static void queryReturnOrderForTicket(String odrSeq, ResponseListener responseListener, Activity activity, String tag) throws JSONException {
        String apiName = "WLT070106";
        String apiUrl = url + apiName;

        if (mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("odrSeq", odrSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, requestParam, jsonResponseListener, errorListener);
        request.setTag(tag);
        mQueue.add(request);
    }

    /**
     * WLT060105 票券商圖示下載
     * @param taSeq    String		*票券商系統編號
     * @param tag
     * <pre>
     * Response: NA
     * </pre>
     *
     */
    public static DownloadCouponAsyncTask task;
    public static void downloadCouponAgencyImage(int taSeq, String iconUpateTime, DownloadEvent.FinishDownloadListener finishDownloadListener, Activity activity) throws JSONException  {

        task = new DownloadCouponAsyncTask(activity, "WLT060105");

        String apiName = "WLT060105";
        JSONObject tmpBodyParam = new JSONObject();
        tmpBodyParam.put("taSeq", taSeq);

        JSONObject requestParam = getRequestParam(apiName, tmpBodyParam, activity);
        task.jHeader = requestParam;
        task.filename = String.valueOf(taSeq) + "_" + iconUpateTime + ".jpg";
        task.setFinishDownloadListener(finishDownloadListener);
        task.execute();
    }

    public static String getCouponAgenyImageSavePath(int taSeq, String iconUpdateTime) {
        return ContactUtil.CouponAgencyFolderPath + File.separator + String.valueOf(taSeq) + "_" + iconUpdateTime + ".jpg";
    }

    public static void stopDownloadCoupon()
    {
        if(task != null)
        {
            task.cancel(true);
        }
    }
    public static boolean isDownloadSuccess()
    {
        if(task != null) {
           return task.isFinish;
        } else {
            return false;
        }

    }
    // ----
    // Inner interface
    // ----
    static class DownloadCouponAsyncTask extends AsyncTask<String, Void, FileRawData> {
        boolean isFinish = false;
        String apiUrl = OFFICIAL_SERVER_URL;
        String apiName = "";

        Context context;

        JSONObject jHeader;
        String filename;

        private int responseCode = 200;


        private DownloadEvent.FinishDownloadListener finishDownloadListener;

        public void setFinishDownloadListener(DownloadEvent.FinishDownloadListener finishDownloadListener)
        {
            this.finishDownloadListener = finishDownloadListener;
        }

        public DownloadCouponAsyncTask(Context context, String apiName) {
            this.context = context;
            this.apiName = apiName;

        }

        @Override
        protected FileRawData doInBackground(String... params) {
            FileRawData newFileRawData = new FileRawData();

            try {
                newFileRawData.setImagePath(filename);

                // Create connection to send GCM Message request.
                URL url = new URL(apiUrl + apiName);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    // Prepare json request
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    outputStream = conn.getOutputStream();
                    outputStream.write(jHeader.toString().getBytes());

                    // Send GCM message content.
                    responseCode = conn.getResponseCode();
                    String returnCode = conn.getHeaderField("returnCode");
                    if (responseCode != 200 || !returnCode.equals("S0000")) {
                        return newFileRawData;
                    }

                    // Read GCM response.
                    inputStream = conn.getInputStream();

                    newFileRawData.setFileContent(GeneralHttpUtil.readToBytesFully(inputStream));
                    Log.d("ExtraHttpUtil", "Response Code:" + responseCode + " The size of " + newFileRawData.getFileContent().length);
                    Log.d("ExtraHttpUtil", "Return message:" + new String(newFileRawData.getFileContent(), Charset.forName("UTF-8")));

                    String folderPath = ContactUtil.CouponAgencyFolderPath;
                    String filePath = folderPath + File.separator + newFileRawData.getImagePath();
                    File newFile = new File(filePath);

                    File folder = new File(newFile.getParent());
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
                    isFinish = true;
                } finally {
                    if(outputStream != null)
                        outputStream.close();
                    if(inputStream != null)
                        inputStream.close();
                }
            } catch (IOException e) {
//                e.printStackTrace();
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
}
