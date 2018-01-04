package tw.com.taishinbank.ewallet.util.responsebody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.model.AppUpdateInfo;
import tw.com.taishinbank.ewallet.model.ContactImage;
import tw.com.taishinbank.ewallet.model.RemoteContact;
import tw.com.taishinbank.ewallet.model.wallethome.WalletHomeCount;
import tw.com.taishinbank.ewallet.model.wallethome.WalletHomePushMsg;
import tw.com.taishinbank.ewallet.model.wallethome.WalletSystemMsg;

public class GeneralResponseBodyUtil {
    /**
     * 取得是否為網銀會員(只有註冊(WLT010101)、登入(WLT010201)會有)
     */
    public static String getIsBankMem(JSONObject object){
        String isBankMem = "";
        try {
            isBankMem = object.getString("isBankMem");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isBankMem;
    }

    /**
     * 取得會員代號
     */
    public static String getMemNo(JSONObject object){
        String memNo = "";
        try {
            memNo = object.getString("memNO");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return memNo;
    }

    /**
     * 取得好友列表
     */
    public static ArrayList<RemoteContact> getRemoteContactList(JSONObject object){
        ArrayList<RemoteContact> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("friendsList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<RemoteContact>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 回傳手機號碼驗證結果：是否為錢包會員
     */
    public static boolean getWalletFlag(JSONObject object){
        try {
            String wltFlag = object.getString("wltFlag");
            if(wltFlag != null && wltFlag.equalsIgnoreCase("Y")){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 回傳手機號碼驗證結果：是否為儲值會員
     */
    public static boolean getSVFlag(JSONObject object){
        try {
            String svFlag = object.getString("svFlag");
            if(svFlag != null && svFlag.equalsIgnoreCase("Y")){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 回傳nickname
     */
    public static String getNickname(JSONObject object){
        String nickname = "";
        try {
            nickname = object.getString("nickname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nickname;
    }

    /**
     * 取得頭像第一筆結果
     */
    public static ContactImage getImage(JSONObject object){
        ContactImage contactImage = null;
        try {
            JSONArray tmpList = object.getJSONArray("memPicList");
            Gson gson = new Gson();
            ArrayList<ContactImage> list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<ContactImage>>() {
            }.getType());
            if(list != null && list.size() > 0){
                contactImage = list.get(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contactImage;
    }

    /**
     * 取得頭像第多筆結果
     */
    public static ArrayList<ContactImage> getImages(JSONObject object){
        ArrayList<ContactImage> list = new ArrayList<ContactImage>();

        try {
            JSONArray tmpList = object.getJSONArray("memPicList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<ContactImage>>() {
            }.getType());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取得顯示名稱
     */
    public static String getName(JSONObject object){
        String name = "";
        try {
            name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 取得電話號碼
     */
    public static String getPhone(JSONObject object){
        String phone = "";
        try {
            phone = object.getString("phone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phone;
    }

    /**
     * 取得信箱
     */
    public static String getEmail(JSONObject object){
        String phone = "";
        try {
            phone = object.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phone;
    }

    /**
     * 取得忘記密碼驗證用的ID
     */
    public static String getVerifyID(JSONObject object){
        String phone = "";
        try {
            phone = object.getString("verifyID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phone;
    }

    /**
     * 取得密碼錯誤次數
     */
    public static int getPasswordErrorCount(JSONObject object){
        int count = 0;
        try {
            count = object.getInt("errorCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 取得錢包首頁的數量資訊列表
     */
    public static ArrayList<WalletHomeCount> getCountList(JSONObject object){
        ArrayList<WalletHomeCount> list = new ArrayList<>();

        try {
            JSONArray tmpList = object.getJSONArray("countList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<WalletHomeCount>>() {
            }.getType());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取得錢包首頁的推播訊息列表
     */
    public static ArrayList<WalletHomePushMsg> getPushMsgList(JSONObject object){
        ArrayList<WalletHomePushMsg> list = new ArrayList<>();

        try {
            JSONArray tmpList = object.getJSONArray("pushMsgList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<WalletHomePushMsg>>() {
            }.getType());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 回傳資料筆數是否大於10筆
     */
    public static boolean getMoreFlag(JSONObject object){
        try {
            String moreFlag = object.getString("moreFlag");
            if(moreFlag != null && moreFlag.equalsIgnoreCase("Y")){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 轉換成系統通知物件
     *
     * @param object server through server.
     */
    public static ArrayList<WalletSystemMsg> parseSystemMessageList(JSONObject object) {
        ArrayList<WalletSystemMsg> list = new ArrayList<>();
        try {
            JSONArray tmpList = object.getJSONArray("boardList");
            Gson gson = new Gson();
            list = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<WalletSystemMsg>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 取得確認app更新的結果
     */
    public static AppUpdateInfo getAppUpdateInfo(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), AppUpdateInfo.class);
    }
}
