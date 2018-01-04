package tw.com.taishinbank.ewallet.util.responsebody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.model.extra.StoreData;
import tw.com.taishinbank.ewallet.model.extra.TicketDetailData;
import tw.com.taishinbank.ewallet.model.extra.TicketListData;

/**
 * 與加值服務有關的 JSON Parser
 *
 * Created by oster on 2016/1/8.
 */
public class ExtraResponseBodyUtil {

    /**
     * 轉換轉成優惠券列表
     *
     * @param jsonBody data of http response body through server.
     * @return List<Coupon>
     */
    public static List<Coupon> parseToCouponList(JSONObject jsonBody) {
        ArrayList<Coupon> list = new ArrayList<>();
        try {
            JSONArray tmpList = jsonBody.getJSONArray("couponList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<Coupon>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

  //

    /**
     * 取得exchangeDate
     */
    public static String getExchangeDate(JSONObject object){
        String exchangeDate = "";
        try {
            exchangeDate = object.getString("exchangeDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return exchangeDate;
    }

    /**
     * 取得senderDate
     */
    public static String getSenderDate(JSONObject object){
        String senderDate = "";
        try {
            senderDate = object.getString("senderDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return senderDate;
    }

    /**
     * 取得優惠卷
     */
    public static Coupon getCoupon(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), Coupon.class);
    }

    /**
     * 取得分享碼
     */
    public static String getInviteCode(JSONObject object){
        String shareCode = "";
        try {
            shareCode = object.getString("shareCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shareCode;
    }


    /**
     * 轉換轉成店家資訊列表
     *
     * @param jsonBody data of http response body through server.
     * @return List<StoreData>
     */
    public static List<StoreData> parseToStoreList(JSONObject jsonBody) {
        ArrayList<StoreData> list = new ArrayList<>();
        try {
            JSONArray tmpList = jsonBody.getJSONArray("storeList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<StoreData>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 轉換轉成電子票券列表
     *
     * @param jsonBody data of http response body through server.
     * @return List<TicketListData>
     */
    public static List<TicketListData> parseToTicketList(JSONObject jsonBody) {
        ArrayList<TicketListData> list = new ArrayList<>();
        try {
            JSONArray tmpList = jsonBody.getJSONArray("tickets");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<TicketListData>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取得電子票卷詳情
     */
    public static TicketDetailData getTicketDetail(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), TicketDetailData.class);
    }

    /**
     * 取得票券交易狀態
     */
    public static String getTradeStatus(JSONObject object){
        String status = "";
        try {
            status = object.getString("Status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }
}
