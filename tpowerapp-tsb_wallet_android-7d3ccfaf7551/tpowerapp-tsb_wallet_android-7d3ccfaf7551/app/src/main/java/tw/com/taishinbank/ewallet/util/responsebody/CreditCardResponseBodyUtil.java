package tw.com.taishinbank.ewallet.util.responsebody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.model.creditcard.CreditCardTransaction;
import tw.com.taishinbank.ewallet.model.extra.TicketOrderData;

public class CreditCardResponseBodyUtil {
    /**
     * 取得交易紀錄筆數
     */
    public static int getTotalCount(JSONObject object){
        int totalCount = 0;
        try {
            totalCount = object.getInt("totalCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return totalCount;
    }


    /**
     * 轉換成信用卡交易記錄物件
     *
     * @param object server through server.
     * @return List<SVTransactionOut>
     */
    public static List<CreditCardTransaction> parseTransactionList(JSONObject object) {
        ArrayList<CreditCardTransaction> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("walletPaymentList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<CreditCardTransaction>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    //

    /**
     * 取得墨攻交易授權token
     */
    public static String getTicketOrderToken(JSONObject object){
        String orderToken = "";
        try {
            orderToken = object.getString("orderToken");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderToken;
    }


    /**
     * 取得電子票卷之訂票資料
     */
    public static TicketOrderData getTicketOrderData(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), TicketOrderData.class);
    }
}
