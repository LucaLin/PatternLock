package tw.com.taishinbank.ewallet.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import tw.com.taishinbank.ewallet.controller.WalletApplication;
import tw.com.taishinbank.ewallet.dbhelper.CreditCardDBHelper;
import tw.com.taishinbank.ewallet.dbhelper.DatabaseHelper;
import tw.com.taishinbank.ewallet.dbhelper.HitRecordDBHelper;
import tw.com.taishinbank.ewallet.dbhelper.SpecialEventsDBHelper;
import tw.com.taishinbank.ewallet.imagehelper.DiskCache;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;

public class PreferenceUtil {

    private final static String PREFS_NAME = "TSB_Wallet";
    private final static String PREF_KEY_FIRSTTIME_USE = "isFirstTimeUse";
    private final static String PREF_KEY_HAS_UPLOAD_CONTACTS= "hasUploadContacts";
//    private final static String PREF_KEY_HAS_LOGIN = "hasLoginBefore";
//    private final static String PREF_KEY_CONTACTS_JSON_TEMP = "contactsJsonTemp";
    private final static String PREF_KEY_PUSH_TOKEN = "pushtoken";
    private final static String PREF_KEY_WALLET_TOKEN = "tokenID";
    private final static String PREF_KEY_SV_TOKEN = "svTokenID";
    private final static String PREF_KEY_MEM_NO = "memno";
    private final static String PREF_KEY_LAST_TIME_MEM_NO = "lastTimeMemno";
    private final static String PREF_KEY_NICKNAME = "nickname";
    private final static String PREF_KEY_SV_LOGIN_TIME= "svLoginTime";
    private final static String PREF_KEY_USER_ID= "userId";
    private final static String PREF_KEY_SV_ACCOUNT= "svAccount";
    private final static String PREF_KEY_PHONE = "phonenumber";
    private final static String PREF_KEY_EMAIL = "email";
    private final static String PREF_KEY_LAST_SHOW_UPDATE = "lastShowUpdate";
    private final static String PREF_KEY_LAST_CHECK_SYSTEM_MESSAGE = "lastCheckSystemMessage";
    private final static String PREF_KEY_SYSTEM_MSG_READ_LIST = "systemMsgReadList";
    private final static String PREF_KEY_FIRSTTIME_USE_FRIEND = "isFirstTimeUseFriend";
    private final static String PREF_KEY_FIRSTTIME_USE_FRIEND_RECEIPT = "isFirstTimeUseFriendReceipt";
    private final static String PREF_KEY_FIRSTTIME_USE_FRIEND_SHARE_RECEIPT = "isFirstTimeUseFriendShareReceipt";
    private final static String PREF_KEY_FIRSTTIME_USE_FRIEND_PAYMENT = "isFirstTimeUseFriendPayment";
    private final static String PREF_KEY_FIRSTTIME_USE_FRIEND_RED_GENERAL = "isFirstTimeUseFriendRedGeneral";
    private final static String PREF_KEY_FIRSTTIME_USE_FRIEND_RED_MONEY_GOD = "isFirstTimeUseFriendRedMoneyGod";
    private final static String PREF_KEY_FIRSTTIME_USE_FRIEND_COUPON = "isFirstTimeUseFriendCoupon";
    private final static String PREF_KEY_IS_BANK_MEMBER = "isBankMem";
    private final static String PREF_KEY_HAS_LOAD_PERSONAL_DATA = "hasLoadPersonalData";
    private final static String PREF_KEY_INIT_WALLET_TOKEN = "initTokenID";
    private final static String PREF_KEY_DEVICE_ID = "deviceID";
    private final static String PREF_KEY_ACCOUNT_NOT_EXIST = "isAccountNotExist";

    public enum ENUM_USE_FRIEND
    {
        FRIEND,
        RECEIPT,
        SHARE_RECEIPT,
        PAYMENT,
        RED_GENERAL,
        RED_MONEY_GOD,
        COUPON
    }
/////
//    以下機制是為了  若登出後，登入同一個人信用卡資料不刪除
/////
//    public static void checkLoginSameMember(Context context) {
//
//        try {
//            String decMemNO = sharedMethods.AESDecrypt(getLastTimeMemNO(context));
//            if(!getMemNO(context).equals(decMemNO)) {
//                CreditCardDBHelper cardDBHelper = new CreditCardDBHelper(context);
//                cardDBHelper.deleteAll();
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.remove(PREF_KEY_LAST_TIME_MEM_NO);
//    }
//
//    public static void manualLogout(Context context) {
//        try {
//            String encMemNO = sharedMethods.AESEncrypt(getMemNO(context));
//            setLastTimeMemNO(context, encMemNO);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        clearAllPreferences(context, true);
//    }
//
// /**
//    * 回傳前一次登入的memNO
//    */
//    public static String getLastTimeMemNO(Context context){
//        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        return pref.getString(PREF_KEY_LAST_TIME_MEM_NO, "");
//    }
//
//    /**
//     * 設定前一次登入的memNO
//     */
//    public static void setLastTimeMemNO(Context context, String value) {
//        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString(PREF_KEY_LAST_TIME_MEM_NO, value);
//        editor.commit();
//    }


    /**
     * 清除所有使用者資料（除了是否第一次使用）
     */
    public static void clearAllPreferences(Context context) {
        clearAllPreferences(context, false);
    }

    public static void clearAllPreferences(Context context, boolean filter){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PREF_KEY_PUSH_TOKEN)
                .remove(PREF_KEY_WALLET_TOKEN)
                .remove(PREF_KEY_SV_TOKEN)
                .remove(PREF_KEY_MEM_NO)
                .remove(PREF_KEY_NICKNAME)
                .remove(PREF_KEY_SV_LOGIN_TIME)
                .remove(PREF_KEY_USER_ID)
                .remove(PREF_KEY_SV_ACCOUNT)
                .remove(PREF_KEY_HAS_UPLOAD_CONTACTS)
                .remove(PREF_KEY_PHONE)
                .remove(PREF_KEY_EMAIL)
                .remove(PREF_KEY_LAST_SHOW_UPDATE)
                .remove(PREF_KEY_LAST_CHECK_SYSTEM_MESSAGE)
                .remove(PREF_KEY_SYSTEM_MSG_READ_LIST)
                .remove(PREF_KEY_FIRSTTIME_USE_FRIEND)
                .remove(PREF_KEY_FIRSTTIME_USE_FRIEND_COUPON)
                .remove(PREF_KEY_FIRSTTIME_USE_FRIEND_RED_GENERAL)
                .remove(PREF_KEY_FIRSTTIME_USE_FRIEND_RED_MONEY_GOD)
                .remove(PREF_KEY_FIRSTTIME_USE_FRIEND_PAYMENT)
                .remove(PREF_KEY_FIRSTTIME_USE_FRIEND_SHARE_RECEIPT)
                .remove(PREF_KEY_FIRSTTIME_USE_FRIEND_RECEIPT)
                .remove(PREF_KEY_IS_BANK_MEMBER)
                .remove(PREF_KEY_HAS_LOAD_PERSONAL_DATA)
                .remove(PREF_KEY_ACCOUNT_NOT_EXIST)
                .commit();

        //刪除DB資料
        if(!filter) {
            CreditCardDBHelper cardDBHelper = new CreditCardDBHelper(context);
            cardDBHelper.deleteAll();
        }
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.deleteAll();

        EventAnalyticsUtil.uploadSpecialEvents(context);
        SpecialEventsDBHelper specialEventsDBHelper = new SpecialEventsDBHelper(context);
        specialEventsDBHelper.deleteAll();
        HitRecordDBHelper hitRecordDBHelper = new HitRecordDBHelper(context);
        hitRecordDBHelper.deleteAll();

        //刪除資料夾
        DiskCache.clear();
        WalletApplication.GlobalHeadImageList.clear();
        CreditCardUtil.ClearGlobalGetCreditCardList(context);
    }

    /**
     * 設定是否為網銀會員
     */
    public static void setIsBankMem(Context context, String value){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_IS_BANK_MEMBER, value);
        editor.commit();
    }

    /**
     * 回傳是否為網銀會員
     */
    public static boolean isBankMem(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_IS_BANK_MEMBER, "N").equalsIgnoreCase("Y");
    }

    /**
     * 回傳是否第一次使用
     */
    public static boolean isFirstTimeUse(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_KEY_FIRSTTIME_USE, true);
    }

    /**
     * 設定是否第一次使用好友
     */
    public static void setFirstTimeUse(Context context, boolean value){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_KEY_FIRSTTIME_USE, value);
        editor.commit();
    }

    /**
     * 回傳是否第一次使用好友
     */
    public static boolean isFirstTimeUseFriend(Context context, ENUM_USE_FRIEND useType){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        switch (useType)
        {
            case FRIEND:
                return pref.getBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND, true);
            case RECEIPT:
                return pref.getBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_RECEIPT, true);
            case SHARE_RECEIPT:
                return pref.getBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_SHARE_RECEIPT, true);
            case PAYMENT:
                return pref.getBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_PAYMENT, true);
            case RED_GENERAL:
                return pref.getBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_RED_GENERAL, true);
            case RED_MONEY_GOD:
                return pref.getBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_RED_MONEY_GOD, true);
            case COUPON:
                return pref.getBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_COUPON, true);
            default:
                return true;
        }

    }

    /**
     * 設定是否第一次使用好友
     */
    public static void setFirstTimeUseFriend(Context context, boolean value, ENUM_USE_FRIEND useType){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        switch (useType)
        {
            case FRIEND:
                editor.putBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND, value);
                break;
            case RECEIPT:
                editor.putBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_RECEIPT, value);
                break;
            case SHARE_RECEIPT:
                editor.putBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_SHARE_RECEIPT, value);
                break;
            case PAYMENT:
                editor.putBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_PAYMENT, value);
                break;
            case RED_GENERAL:
                editor.putBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_RED_GENERAL, value);
                break;
            case RED_MONEY_GOD:
                editor.putBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_RED_MONEY_GOD, value);
                break;
            case COUPON:
                editor.putBoolean(PREF_KEY_FIRSTTIME_USE_FRIEND_COUPON, value);
                break;
        }

        editor.commit();
    }



    /**
     * 回傳是否上傳過聯絡人
     */
    public static boolean hasUploadContacts(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_KEY_HAS_UPLOAD_CONTACTS, false);
    }

    /**
     * 設定是否上傳過聯絡人
     */
    public static void setHasUploadContacts(Context context, boolean value){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_KEY_HAS_UPLOAD_CONTACTS, value);
        editor.commit();
    }

//    /**
//     * 回傳是否登入過
//     */
//    public static boolean hasLoginBefore(Context context){
//        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        return pref.getBoolean(PREF_KEY_HAS_LOGIN, false);
//    }
//
//    /**
//     * 設定是否登入過
//     */
//    public static void setHasLoginBefore(Context context, boolean value) {
//        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putBoolean(PREF_KEY_HAS_LOGIN, value);
//        editor.commit();
//    }

    /**
     * 回傳push token
     */
    public static String getPushToken(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_PUSH_TOKEN, "");
    }

    /**
     * 設定push token
     */
    public static void setPushToken(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_PUSH_TOKEN, value);
        editor.commit();
    }

    /**
     * 回傳wallet token
     */
    public static String getWalletToken(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_WALLET_TOKEN, "");
    }

    /**
     * 設定wallet token
     */
    public static void setWalletToken(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_WALLET_TOKEN, value);
        editor.commit();
    }

    /**
     * 回傳svToken
     */
    public static String getSVToken(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_SV_TOKEN, "");
    }

    /**
     * 設定svToken
     */
    public static void setSVToken(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_SV_TOKEN, value);
        editor.commit();
    }

    /**
     * 回傳memNO
     */
    public static String getMemNO(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_MEM_NO, "");
    }

    /**
     * 設定memNO
     */
    public static void setMemNO(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_MEM_NO, value);
        editor.commit();
    }

    /**
     * 設定Phone Number
     */
    public static void setPhoneNumber(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_PHONE, value);
        editor.commit();
    }

    /**
     * 回傳 Phone Number
     */
    public static String getPhoneNumber(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_PHONE, "");
    }

    /**
     * 設定Email
     */
    public static void setEmail(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_EMAIL, value);
        editor.commit();
    }

    /**
     * 回傳Email
     */
    public static String getEmail(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_EMAIL, "");
    }

    /**
     * 回傳nickname
     */
    public static String getNickname(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_NICKNAME, "");
    }

    /**
     * 設定nickname
     */
    public static void setNickname(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_NICKNAME, value);
        editor.commit();
    }

    /**
     * 回傳登入儲值系統回傳的時間
     */
    public static String getSVLoginTime(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_SV_LOGIN_TIME, "");
    }

    /**
     * 設定登入儲值系統回傳的時間
     */
    public static void setSVLoginTime(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_SV_LOGIN_TIME, value);
        editor.commit();
    }

    /**
     * 回傳身分證字號
     */
    public static String getUserId(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_USER_ID, "");
    }

    /**
     * 設定身分證字號
     */
    public static void setUserId(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_USER_ID, value);
        editor.commit();
    }

    /**
     * 回傳身分證字號
     */
    public static SVAccountInfo getSVAccountInfo(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String info = pref.getString(PREF_KEY_SV_ACCOUNT, "");
        Gson gson = new Gson();
        return gson.fromJson(info, SVAccountInfo.class);
    }

    /**
     * 設定身分證字號
     */
    public static void setSVAccountInfo(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_SV_ACCOUNT, value);
        editor.commit();
    }

    /**
     * 回傳上次顯示更新對話框的日期
     */
    private static long getLastShowUpdate(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getLong(PREF_KEY_LAST_SHOW_UPDATE, -1);
    }

    /**
     * 設定上次顯示更新對話框的時間
     */
    public static void setLastShowUpdate(Context context) {
        // 取得當日時間
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(PREF_KEY_LAST_SHOW_UPDATE, c.getTimeInMillis());
        editor.commit();
    }

    /**
     * 回傳是否需要檢查更新
     */
    public static boolean needCheckUpdate(Context context){
        long lastShowUpdateTime = getLastShowUpdate(context);

        if(lastShowUpdateTime < 0){
            return true;
        }

        // 取得當日時間
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return lastShowUpdateTime < c.getTimeInMillis();
    }

    /**
     * 回傳是否需要檢查系統訊息
     */
    public static boolean needCheckSystemMessage(Context context){
        long lastShowUpdateTime = getLastCheckSystemMessage(context);

        if(lastShowUpdateTime < 0){
            return true;
        }

        // 取得當日時間
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return lastShowUpdateTime < c.getTimeInMillis();
    }

    /**
     * 回傳上次顯示系統訊息對話框的日期
     */
    private static long getLastCheckSystemMessage(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getLong(PREF_KEY_LAST_CHECK_SYSTEM_MESSAGE, -1);
    }

    /**
     * 設定上次顯示系統訊息對話框的時間
     */
    public static void setLastCheckSystemMessage(Context context) {
        // 取得當日時間
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(PREF_KEY_LAST_CHECK_SYSTEM_MESSAGE, c.getTimeInMillis());
        editor.commit();
    }


    /**
     * 存入已讀的系統訊息時間列表
     */
    public static void setSystemMessageReadList(Context context, Set<String> stringSet) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(PREF_KEY_SYSTEM_MSG_READ_LIST, stringSet);
        editor.commit();
    }

    /**
     * 取得已讀的系統訊息時間列表
     */
    public static Set<String> getSystemMessageReadList(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> stringSet = pref.getStringSet(PREF_KEY_SYSTEM_MSG_READ_LIST, null);
        if(stringSet == null){
            stringSet = new HashSet<>();
        }
        return stringSet;
    }

    /**
     * 設定是否讀取過個人資料
     */
    public static void setHasLoadPersonalData(Context context, boolean value){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_KEY_HAS_LOAD_PERSONAL_DATA, value);
        editor.commit();
    }

    /**
     * 回傳是否讀取過個人資料
     */
    public static boolean hasLoadPersonalData(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_KEY_HAS_LOAD_PERSONAL_DATA, false);
    }


    /**
     * 設定安裝後第一次取得的錢包token
     */
    public static void setPrefKeyInitWalletToken(Context context, String value){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_INIT_WALLET_TOKEN, value);
        editor.commit();
    }

    /**
     * 回傳是否設定過安裝後第一次取得的token
     */
    public static String getPrefKeyInitWalletToken(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_INIT_WALLET_TOKEN, null);
    }


    /**
     * 回傳身DeviceID
     * 紀錄第一次使用抓到的ID, 未來都固定用這一組ID, 除非移除APP重新安裝才會再次設定
     * 目前適用在信用卡
     */
    public static String getDeviceID(Context context){

        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String deviceID = pref.getString(PREF_KEY_DEVICE_ID, "");

        if(TextUtils.isEmpty(deviceID)) {
            String tmpUniqueValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(tmpUniqueValue)) {
                deviceID = tmpUniqueValue;
            } else {
                String tmpInitWalletToken = PreferenceUtil.getPrefKeyInitWalletToken(context);
                if (!TextUtils.isEmpty(tmpInitWalletToken)) {
                    deviceID = tmpInitWalletToken;
                } else {
                    deviceID = PreferenceUtil.getWalletToken(context);
                }
            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREF_KEY_DEVICE_ID, deviceID);
            editor.commit();
        }

        return pref.getString(PREF_KEY_DEVICE_ID, "");
    }

    /**
     * 回傳帳戶不存在狀態
     */
    public static boolean getAccountNotExist(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_KEY_ACCOUNT_NOT_EXIST, false);
    }

    /**
     * 設定帳戶不存在狀態
     */
    public static void setAccountNotExist(Context context, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_KEY_ACCOUNT_NOT_EXIST, value);
        editor.commit();
    }

//    /**
//     * 回傳儲存的聯絡人列表
//     */
//    public static String getContactsJsonTemp(Context context){
//        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        return pref.getString(PREF_KEY_CONTACTS_JSON_TEMP, null);
//    }
//
//    /**
//     * 設定聯絡人列表
//     */
//    public static void setContactsJsonTemp(Context context, ArrayList<LocalContact> value) {
//        String jsonString = null;
//        if(value != null) {
//            Gson gson = new Gson();
//            jsonString = gson.toJson(value);
//        }
//        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString(PREF_KEY_CONTACTS_JSON_TEMP, jsonString);
//        editor.commit();
//    }
}
