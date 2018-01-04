package tw.com.taishinbank.ewallet.util.responsebody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.model.red.RedEnvelopeHomeListData;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeReceivedHeader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentHeader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResult;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResultEach;

/**
 * 與紅包有關的 JSON Parser
 *
 * Created by oster on 2016/1/8.
 */
public class RedEnvelopeResponseBodyUtil {
    /**
     * 取得紅包首頁列表
     */
    public static ArrayList<RedEnvelopeHomeListData> getRedEnvelopeHomeList(JSONObject object){
        ArrayList<RedEnvelopeHomeListData> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("wltTxDetailList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<RedEnvelopeHomeListData>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取得自己收到的紅包列表（用來顯示在我的紅包-收到的列表）
     */
    public static ArrayList<RedEnvelopeReceivedHeader> getRedEnvelopReceivedSelfList(JSONObject object){
        ArrayList<RedEnvelopeReceivedHeader> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("txSelfList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<RedEnvelopeReceivedHeader>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取得收到的紅包列表詳情
     */
    public static ArrayList<RedEnvelopeReceivedHeader> getRedEnvelopReceivedList(JSONObject object){
        ArrayList<RedEnvelopeReceivedHeader> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("txHeaderList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<RedEnvelopeReceivedHeader>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取得發出的紅包列表
     */
    public static ArrayList<RedEnvelopeSentHeader> getRedEnvelopSentList(JSONObject object){
        ArrayList<RedEnvelopeSentHeader> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("txHeaderList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<RedEnvelopeSentHeader>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取得發紅包的結果
     */
    public static RedEnvelopeSentResult getRedEnvelopSentResult(JSONObject object){
        // TODO 待確認是否可以直接這樣轉成物件，不行的話就要用下面方式做物件
        Gson gson = new Gson();
        RedEnvelopeSentResult sentResult = gson.fromJson(object.toString(), RedEnvelopeSentResult.class);
//        RedEnvelopeSentResult sentResult = new RedEnvelopeSentResult();
//        try {
//            sentResult.setAmount(object.getDouble("amount"));
//            sentResult.setSender(object.getString("sender"));
//            sentResult.setBalance(object.getDouble("balance"));
//            sentResult.setCreateDate(object.getString("createDate"));
//            sentResult.setTxfSeq(object.getInt("txfSeq"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        // 物件的list還是要另外作轉換，否則回來會是null
        RedEnvelopeSentResultEach[] list = null;
        try {
            // TODO 測試資料的Header名稱
            JSONArray tmpList = object.getJSONArray("txResult");
            list = gson.fromJson(tmpList.toString(), new TypeToken<RedEnvelopeSentResultEach[]>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sentResult.setTxResult(list);
        return sentResult;
    }
}
