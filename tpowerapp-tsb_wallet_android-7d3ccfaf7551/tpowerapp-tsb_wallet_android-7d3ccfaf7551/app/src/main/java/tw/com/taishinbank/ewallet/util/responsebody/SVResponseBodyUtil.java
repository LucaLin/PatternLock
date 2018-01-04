package tw.com.taishinbank.ewallet.util.responsebody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.sv.BankAccount;
import tw.com.taishinbank.ewallet.model.sv.DepositWithdrawResult;
import tw.com.taishinbank.ewallet.model.sv.DesignateAccount;
import tw.com.taishinbank.ewallet.model.sv.ReceiptResult;
import tw.com.taishinbank.ewallet.model.sv.ReceiveRequestPaymentResult;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionIn;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionOut;

/**
 * 與儲值有關的 JSON Parser
 *
 * Created by oster on 2016/1/8.
 */
public class SVResponseBodyUtil {
    /**
     * 取得儲值帳戶資訊
     */
    public static SVAccountInfo getSVAccountInfo(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), SVAccountInfo.class);
    }

    /**
     * 取得登入儲值帳戶後得到的系統時間
     */
    public static String getSVLoginTime(JSONObject object){
        String sysTime = "";
        try {
            sysTime = object.getString("sysTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sysTime;
    }

    /**
     * 取得約定帳戶資訊
     */
    public static DesignateAccount getDesignateAccount(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), DesignateAccount.class);
    }

    /**
     * 取得紅包首頁列表
     */
    public static ArrayList<BankAccount> getBankAccountList(JSONObject object){
        ArrayList<BankAccount> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("accountList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<BankAccount>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取得儲值/提領結果資訊
     */
    public static DepositWithdrawResult getDepositWithdrawResult(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), DepositWithdrawResult.class);
    }

    /**
     * 轉換成支出記錄物件
     *
     * @param object server through server.
     * @return List<SVTransactionOut>
     */
    public static List<SVTransactionOut> parseTransactionOutList(JSONObject object) {
        ArrayList<SVTransactionOut> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("txList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<SVTransactionOut>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 轉換成收到記錄物件
     *
     * @param object server through server.
     * @return List<SVTransactionIn>
     */
    public static List<SVTransactionIn> parseTransactionInList(JSONObject object) {
        ArrayList<SVTransactionIn> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("txList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<SVTransactionIn>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 轉換成付款要求發送結果的物件
     */
    public static ReceiptResult parseReceiptResult(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), ReceiptResult.class);
    }

    /**
     * 轉換成收到付款請求-確認付款結果的物件
     */
    public static ReceiveRequestPaymentResult parseReceiveRequestPaymentResult(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), ReceiveRequestPaymentResult.class);
    }

    /**
     * 回傳儲值首頁是否有新紀錄
     */
    public static boolean getHasNew(JSONObject object){
        try {
            String wltFlag = object.getString("hasNew");
            if(wltFlag != null && wltFlag.equalsIgnoreCase("Y")){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
