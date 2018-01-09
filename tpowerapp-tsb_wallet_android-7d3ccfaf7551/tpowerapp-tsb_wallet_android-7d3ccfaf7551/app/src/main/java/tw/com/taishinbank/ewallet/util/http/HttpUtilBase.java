package tw.com.taishinbank.ewallet.util.http;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tw.com.taishinbank.ewallet.BuildConfig;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

/**
 * 基礎Class，後續再依模組(錢包、儲值、紅包、信用卡)實作延伸　Http Request。
 * Http Request 有可能一支API 分別用在不同的地方，所以集中於此.
 *
 * Created by oster on 2016/1/8.
 */
public class HttpUtilBase {


    static final int TIMEOUT = 90 * 1000; // 預設為30秒 -->  同步ios設定90s
    static final int MAX_RETRY_TIMES = -1; // 預設為完全不retry

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public static RequestQueue mQueue;
    public static RequestQueue mQueueForAnalyticsEven;
    /**
     * Mock環境屬性
     */
    // mock response
    static final String MOCK_SERVER_URL = "https://dl.dropboxusercontent.com/u/50511547/JSON_TSB/";
    // mock 儲值註冊網址
    static final String MOCK_SV_REGISTER_URL_Y = "https://sva.taishinbank.com.tw/taishinWallet/hasbank/R-W-01.aspx?id=";
    static final String MOCK_SV_REGISTER_URL_N = "https://sva.taishinbank.com.tw/taishinWallet/new/R-N-01-02.aspx?id=";

    /**
     * 內網測試環境
     */
    // 內網開發測試 server
    static final String OFFICIAL_SERVER_URL = "http://10.13.114.85:8092/walletAPI/";
    // 內網開發測試 儲值註冊網址
    static final String OFFICIAL_SV_REGISTER_URL_Y = "http://172.19.1.142/taishinWallet/hasbank/R-W-01.aspx?id=";
    static final String OFFICIAL_SV_REGISTER_URL_N = "http://172.19.1.142/taishinWallet/new/R-N-01-02.aspx?id=";
    // 17 server IP
    public static final String E7_SERVER_IP = "10.13.114.199";
    public static final boolean E7_SERVER_IS_HTTPS = false; // 設定 true: https / false: http
    public static final boolean CreditCard_TicketPayment_Remove_Button = false; // 是否移除墨攻WebView下方的按鈕
    public static final String MOHIST_STORE_URL = "http://61.216.14.79/TaishinMall_1/"; //商城墨攻URL

//     UAT server
//    static final String OFFICIAL_SERVER_URL = "http://10.13.114.211:8092/walletAPI/";
//    // UAT 儲值註冊網址
//    static final String OFFICIAL_SV_REGISTER_URL_Y = "http://172.19.1.142/taishinWallet/hasbank/R-W-01.aspx?id=";
//    static final String OFFICIAL_SV_REGISTER_URL_N = "http://172.19.1.142/taishinWallet/new/R-N-01-02.aspx?id=";
//    // 17 server IP
//    public static final String E7_SERVER_IP = "10.13.114.52";
//    public static final boolean E7_SERVER_IS_HTTPS = false; // 設定 true: https / false: http
//    public static final boolean CreditCard_TicketPayment_Remove_Button = false; // 是否移除墨攻WebView下方的按鈕
//    public static final String MOHIST_STORE_URL = "http://61.216.14.79/TaishinMall/"; //商城墨攻URL
    /**
     * 對外開放正式環境
     */
//    // official launch server
//    static final String OFFICIAL_SERVER_URL = "https://richart.tw/walletAPI/";
//    // 對外開放
//    static final String OFFICIAL_SV_REGISTER_URL_Y = "https://sva.taishinbank.com.tw/taishinWallet/hasbank/R-W-01.aspx?id=";
//    static final String OFFICIAL_SV_REGISTER_URL_N = "https://sva.taishinbank.com.tw/taishinWallet/new/R-N-01-02.aspx?id=";
//    // 17 server IP
//    public static final String E7_SERVER_IP = "richart.tw/17api"; // TODO 上版前請修改為正式的17 Server IP
//    public static final boolean E7_SERVER_IS_HTTPS = true; // 設定 true: https / false: http
//    public static final boolean CreditCard_TicketPayment_Remove_Button = true; // 是否移除墨攻WebView下方的按鈕
//    public static final String MOHIST_STORE_URL = "https://www.mohist.com.tw/TaishinMall/"; //商城墨攻URL


    static final String url = GlobalConst.UseOfficialServer ? OFFICIAL_SERVER_URL : MOCK_SERVER_URL;
//    public static final String SV_REGISTER_URL = GlobalConst.UseOfficialServer ? OFFICIAL_SV_REGISTER_URL : MOCK_SV_REGISTER_URL;

    //    private static final String url = "http://requestb.in/yo32gsyo";
    static final String DEFAULT_DEVICE_UUID = "forInternalTest_deviceID";
    // 手機os版本 ex: android 4.4 iso 9.1
    static String deviceVersion = Build.VERSION.RELEASE;
    // 電子錢包APP版本 ex: 1.3.1 (這邊讀取app的build.gradle所設定的值)
    static String appVersion = BuildConfig.VERSION_NAME;
    // 手機序號(push token)
    //TSB紅米：fZy0QXmCXNk:APA91bHk0jrUXBJRxV7u1FRxsdb9nMMlqwO9s914we19gopnUFRC60YROUpiJzI3Y21BnruUklhhetZ3haUM-tjeGPsJIe2R9Z1j7lKjlzazncqfldmkjcmxef1fgZE0oqoEZ3JFzPu8
    static String deviceUUID = "forInternalTest_deviceID";
    // 手機廠牌型號ex: iphone6s, hTC M9
    static String deviceBrand = Build.MANUFACTURER + " " + Build.MODEL;
    // 機身碼
    static String uniqueValue = "forInternalTest_uniqueValue";
    // API代碼
    static String apiCode ="";
    // (登入後由server產生)
    static String tokenID ="";
    // 儲值系統token(10分鐘過期)
    static String svTokenID ="";

    public static String getSvRegisterUrl(Context context){
        String svRegisterUrl;
        if (GlobalConst.UseOfficialServer && PreferenceUtil.isBankMem(context)){
            svRegisterUrl = OFFICIAL_SV_REGISTER_URL_Y;
        }
        else if (GlobalConst.UseOfficialServer){
            svRegisterUrl = OFFICIAL_SV_REGISTER_URL_N;
        }
        else if(PreferenceUtil.isBankMem(context)){
            svRegisterUrl = MOCK_SV_REGISTER_URL_Y;
        }
        else{
            svRegisterUrl = MOCK_SV_REGISTER_URL_N;
        }
        try {
            String userId = sharedMethods.AESDecrypt(PreferenceUtil.getUserId(context));
            svRegisterUrl += userId;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            svRegisterUrl += "&idType=11&email=";
            String emailDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getEmail(context));
            if(!TextUtils.isEmpty(emailDecrypt)) {
                svRegisterUrl += emailDecrypt;
            }
            svRegisterUrl += "&mobile=";
            String phoneDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getPhoneNumber(context));
            if(!TextUtils.isEmpty(phoneDecrypt)) {
                svRegisterUrl += phoneDecrypt;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return svRegisterUrl;
    }

    static JSONObject addRequestHeader(Context context, String apiNickname) throws JSONException {

        String txDate = DATE_FORMAT.format(new java.util.Date());

        JSONObject tmpHeaderParam = new JSONObject();

        tmpHeaderParam.put("deviceVersion",deviceVersion);
        tmpHeaderParam.put("appVersion", appVersion);
        tmpHeaderParam.put("deviceType", "android");

        // 從preference取得push token
        String tmpDeviceUUID = PreferenceUtil.getPushToken(context);
        if (!TextUtils.isEmpty(tmpDeviceUUID)){
            deviceUUID = tmpDeviceUUID;
        }else{
            deviceUUID = DEFAULT_DEVICE_UUID;
        }
        tmpHeaderParam.put("deviceUUID", deviceUUID);

        tmpHeaderParam.put("deviceBrand", deviceBrand);

        tmpHeaderParam.put("txDate",txDate);
        tmpHeaderParam.put("apiCode", apiNickname);

        // 從preference取得tokenID
        tokenID = PreferenceUtil.getWalletToken(context);
        tmpHeaderParam.put("tokenID", tokenID);

        // 從preference取得svTokenID
        svTokenID = PreferenceUtil.getSVToken(context);
        tmpHeaderParam.put("svTokenID", svTokenID);

        String tmpUniqueValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(!TextUtils.isEmpty(tmpUniqueValue)){
            uniqueValue = tmpUniqueValue;
        }else{
            String tmpInitWalletToken = PreferenceUtil.getPrefKeyInitWalletToken(context);
            if(!TextUtils.isEmpty(tmpInitWalletToken)) {
                uniqueValue = tmpInitWalletToken;
            }else{
                uniqueValue = "forInternalTest_uniqueValue";
            }
        }
        tmpHeaderParam.put("uniqueValue", uniqueValue);

        return tmpHeaderParam;
    }

    static JSONObject getRequestParam(String apiName, JSONObject bodyParam, Context context) throws JSONException {
        JSONObject tmpReqParam = new JSONObject();
        JSONObject reqParam = new JSONObject();
        tmpReqParam.putOpt("header", addRequestHeader(context, apiName));
        tmpReqParam.putOpt("body", bodyParam);

        reqParam.putOpt(apiName + "request", tmpReqParam);

        return reqParam;
    }

    /**
     * return String[], [0]是beginDate, [1]是endDate
     */
    static String[] getMonthBeginEndDate(MonthOption monthOption){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        // 如果是兩個月，起始時間為上個月1號，結束日期為今天
        if(monthOption == MonthOption.LATEST_2_MONTH){
            String[] dates = new String[2];
            // endDate
            dates[1] = df.format(c.getTime());
            // beginDate
            c.add(Calendar.MONTH, -1);
            c.set(Calendar.DAY_OF_MONTH, 1);
            dates[0] = df.format(c.getTime());
            return dates;
        // 否則不帶日期參數
        }else{
            return null;
        }
    }

    public enum MonthOption {
        LATEST_1_MONTH,
        LATEST_2_MONTH,
        NA
    }


    //cancel queue Request for tag
    public static void cancelQueue(String tag) {
        if (mQueue != null) {
            mQueue.cancelAll(tag);
        }
    }

}
