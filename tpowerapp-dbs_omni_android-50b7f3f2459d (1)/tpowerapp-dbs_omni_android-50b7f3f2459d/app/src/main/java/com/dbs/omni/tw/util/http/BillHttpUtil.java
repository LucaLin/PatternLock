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
public class BillHttpUtil extends HttpUtilBase {

    /**<h5> CCD020101 帳單總覽 </h5>
     * <request>
     *
     * <response>
     *  stmtYM			string	帳單月份YYYYMM
     *  stmtCycleDate		string	帳單結帳日 YYYYMMDD
     *  paymentDueDate	string	繳款截止日 YYYYMMDD
     *  creditLine			string	信用額度 (待V+提供)
     *  paymentPeriodStart	string	入帳期間起YYYYMMDD
     *  paymentPeriodEnd	string	入帳期間迄YYYYMMDD
     *  amtPastDue		string	前期應繳金額
     *  amtPayment		string	前期繳款金額
     *  amtNewPurchases	string	本期新增金額 = (前期應繳-前期已繳+本期應繳)
     *  amtCurrDue		string	本期全部應繳金額
     *  amtCurrPayment	string	本期累積已繳金額  (待V+提供)
     *  amtMinPayment	string	本期最低應繳金額
     *  domCashAvail		string	預借現金額度  (待V+提供)
     *  creditRate	    string	信用年利率 11.33  (待V+提供)
     *  rPoints	        string			Reward points 紅利點數(沒有塞0)
     *  mPoints	        string			Miles points飛行積金(沒有塞0)
     *  crPoints        string			Cash rebate points 現金點數(沒有塞0)
     *  paymentDetail	List :
     *  	txDate	string	交易日期YYYYMMDD (待V+提供)
     *  	postDate	string	入帳日期YYYYMMDD (待V+提供)
     *  	txDesc	string	消費明細 (待V+提供)
     *  	txAmt	string	消費金額(NTD) (待V+提供)
//     *  	orglAmt	string	原始消費金額 (待V+提供)
//     *  	orglCry	string	原始消費幣別 (待V+提供)
     *
     * <error>
     **/
    public static void getBillOverview(ResponseListener responseListener,
                                                     Activity activity) throws JSONException {
        String apiName = "ccd020101";
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

    //TODO  未確認
    /**<h5> CCD020201 未出帳帳單 </h5>
     * <request>
     *
     * <response>
     *  unbillStart				未出帳起日MMDD？(V+未提供)
     *  ccLimit				信用額度(V+未提供)
     *  unbillAmt				未出帳金額 (V+未提供)
     *  avlBalance				available balance可用餘額 (V+未提供)
     *
     *
     * <error>
     **/
    public static void getUnBilledOverview(ResponseListener responseListener,
                                       Activity activity) throws JSONException {
        String apiName = "ccd020201";
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

    /**<h5> CCD020202 未出帳帳單交易明細 </h5>
     * <request>
     *  ccID string   卡片ID (可選) 輸入""會回全部

     *
     * <response>
     *  unbillTXDetail	List :
     *  	ccNO	string	信用卡卡號
     *  	ccFlag	string	M：正卡S:附卡
     *  	cardName	string	卡片名稱
     *  	ccLogo	string	信用卡的產品代碼(系統用來辨別哪一張卡片，與PWeb串接會用此代碼)
     *
     *  unbillTXList	List :
     *  	txDate	string	交易日期YYYYMMDD
     *  	postDate	string	入帳日期YYYYMMDD
     *  	xDesc	string	消費明細
     *  	txAmt	string	消費金額(NTD)
     *  	orglAmt	string	原始消費金額
     *  	orglCry	string	原始消費幣別
     *
     *
     *
     * <error>
     **/
    public static void getUnBilledDetail(String ccID, ResponseListener responseListener,
                                           Activity activity) throws JSONException {
        String apiName = "ccd020202";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("ccID", ccID);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD020301 本期帳單交易明細</h5>
     * <request>
     *
     * <response>
     *  stmtYM	string	帳單月份YYYYMM
     *  cardList	List :
     *  	ccFlag	string	M:正卡, S:附卡
     *  	ccNO	string	信用卡卡號
     *  	cardName	string	卡片名稱
     *  	ccLogo	string	信用卡的產品代碼(系統用來辨別哪一張卡片，與PWeb串接會用此代碼)
     *      currentTXList List :
     *  	    txDate	string	交易日期YYYYMMDD
     *  	    postDate	string	入帳日期YYYYMMDD
     *  	    txDesc	string	消費明細
     *  	    txAmt	string	消費金額(NTD)
     *  	    orglAmt	string	原始消費金額
     *  	    orglCry	string	原始消費幣別
     *
     *
     *
     * <error>
     **/
    public static void getCurrentBilledDetail(ResponseListener responseListener,
                                         Activity activity) throws JSONException {
        String apiName = "ccd020301";
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


    /**<h5> CCD020401下載電子帳單 </h5>
     * <request>
     *  @param eBillNO	        string	帳單流水號
     *  @param eBillYYYY	    string	帳單日期-年 YYYY
     *  @param eBillMM	        string	帳單日期-月 MM
     *  @param eBillFileName	string	帳單PDF檔名
     *
     * <response>
     *  eBillFile	string		PDF檔案 Base64 string
     *
     * <error>
     **/
    public static void downloadeBillFile(String eBillNO, String eBillYYYY, String eBillMM, String eBillFileName, ResponseListener responseListener,
                                              Activity activity) throws JSONException {
        String apiName = "ccd020401";
        String apiUrl = getApiUrl(apiName);
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("eBillNO", eBillNO);
        tmpBodyParam.addProperty("eBillYYYY", eBillYYYY);
        tmpBodyParam.addProperty("eBillMM", eBillMM);
        tmpBodyParam.addProperty("eBillFileName", eBillFileName);

        JsonObject requestParam = getRequestParam(apiName.toUpperCase(), tmpBodyParam, activity);
        JsonResponseListener jsonResponseListener = new JsonResponseListener(apiName, responseListener);
        JsonResponseErrorListener errorListener = new JsonResponseErrorListener(apiName, responseListener);

        JsonObjectRequest request = new JsonObjectRequest(apiUrl, new JSONObject(requestParam.toString()), jsonResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

    /**<h5> CCD030301 查詢電子帳單 </h5>
     * <request>
     *
     * <response>
     * eBillList	List :
     * 	eBillNO		    string	帳單流水號
     * 	eBillYYYY		string	帳單日期-年 YYYY
     * 	eBillMM		    string	帳單日期-月 MM
     * 	eBillFromDate	string	帳單起日YYYYMMDD
     * 	eBillToDate	    string	帳單迄日YYYYMMDD
     *
     *
     * <error>
     **/
    public static void geteBillList(ResponseListener responseListener,
                                    Activity activity) throws JSONException {
        String apiName = "ccd030301";
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


//    /**<h5> 下載電子帳單 </h5>
//     * <request>
//     *  @param eBillNO	        string	帳單流水號
//     *  @param eBillYYYY	    string	帳單日期-年 YYYY
//     *  @param eBillMM	        string	帳單日期-月 MM
//     *  @param eBillFileName	string	帳單PDF檔名
//     *
//     *
//     * <response>
//     *
//     * <error>
//     **/
//    public static void downloadBillFile(Context context, String fileURL, FileDownloadEvent.FinishDownloadListener listener) {
//        FileDownloadAsyncTask task = new FileDownloadAsyncTask(context, fileURL);
//        task.folderPath = GlobalConst.PDFFileFolderPath ;
//
//        task.setFinishDownloadListener(listener);
//        task.execute();
//    }
//
//
//
//    //Share
//    static class FileDownloadAsyncTask extends AsyncTask<String, Void, FileRawData> {
//        boolean isFinish = false;
//        int merSeq;
//
//        String downloadUrl;
//        Context context;
//
//        String folderPath;
//        String filePath;
//
//        private int responseCode = 200;
//
//
//        private FileDownloadEvent.FinishDownloadListener finishDownloadListener;
//
//        public void setFinishDownloadListener(FileDownloadEvent.FinishDownloadListener finishDownloadListener)
//        {
//            this.finishDownloadListener = finishDownloadListener;
//        }
//
//        public FileDownloadAsyncTask(Context context, String fileUrl) {
//            this.context = context;
//            this.downloadUrl = fileUrl;
//
//        }
//
//        @Override
//        protected FileRawData doInBackground(String... params) {
//            FileRawData newFileRawData = new FileRawData();
//            try {
//
//
//                // Create connection to send GCM Message request.
//                URL url;
//
//                File file = new File(downloadUrl);
//                newFileRawData.setFilePath(file.getName());
//                url = new URL(downloadUrl);
//
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//                OutputStream outputStream = null;
//                InputStream inputStream = null;
//                try {
//                    // Prepare json request
//                    conn.setRequestProperty("Content-Type", "application/json");
//                    conn.setRequestMethod("POST");
//                    conn.setDoOutput(true);
//
//                    outputStream = conn.getOutputStream();
//
//                    // Send GCM message content.
//                    responseCode = conn.getResponseCode();
//                    String returnCode = conn.getHeaderField("returnCode");
//                    if (responseCode != 200) {
//                        return newFileRawData;
//                    }
//                } finally {
//                    if(outputStream != null) {
//                        try {
//                            outputStream.close();
//                        } catch (IOException ex) {
////                            Log.e(TAG, "close crash");
//                        }
//                    }
//                }
//
//                try {
//                    // Read GCM response.
//                    inputStream = conn.getInputStream();
//
//                    newFileRawData.setFileContent(readToBytesFully(inputStream));
//                    Log.d("ExtraHttpUtil", "Response Code:" + responseCode + " The size of " + newFileRawData.getFileContent().length);
//                    Log.d("ExtraHttpUtil", "Return message:" + new String(newFileRawData.getFileContent(), Charset.forName("UTF-8")));
//                } finally {
//                    if(inputStream != null) {
//                        try {
//                            inputStream.close();
//                        } catch (IOException ex) {
////                            Log.e(TAG, "close crash");
//                        }
//                    }
//                }
//
//                filePath = folderPath + File.separator + newFileRawData.getFilePath();
//                File newFile = new File(filePath);
//
//                File folder = new File(newFile.getParent());
//                if (!folder.exists()) {
//                    boolean ret = folder.mkdirs();
//                    if (!ret)
//                        folder.mkdir();
//                }
//
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(newFile);
//                    fos.write(newFileRawData.getFileContent());
//                } finally {
//                    if(fos != null) {
////                        fos.flush();
//                        safeClose(fos);
//                    }
//                }
//                isFinish = true;
//
//
//            } catch (IOException e) {
////                e.printStackTrace();
//            }
//            return newFileRawData;
//        }
//
//        @Override
//        protected void onPostExecute(FileRawData fileRawData) {
//            if(isFinish) {
//                finishDownloadListener.onFinishDownload(filePath);
//            } else {
//                finishDownloadListener.onErrorDownload();
//            }
//
//            super.onPostExecute(fileRawData);
//        }
//
//        public int getResponseCode() {
//            return responseCode;
//        }
//    }
//
//
//
//    public static byte[] readToBytesFully(InputStream input) throws IOException {
//        byte[] buffer = new byte[8192];
//        int bytesRead;
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        while ((bytesRead = input.read(buffer)) != -1)
//        {
//            output.write(buffer, 0, bytesRead);
//        }
//        return output.toByteArray();
//    }
//
//    public static void safeClose(FileOutputStream fileOutputStream) {
//        if (fileOutputStream != null) {
//            try {
//                fileOutputStream.close();
//            } catch (IOException e) {
//                Log.e("FileOutputStream Close", "close crash");
//            }
//        }
//    }

}

