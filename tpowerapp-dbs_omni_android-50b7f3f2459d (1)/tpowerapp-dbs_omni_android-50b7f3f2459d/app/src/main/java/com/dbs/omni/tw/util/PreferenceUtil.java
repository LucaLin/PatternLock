package com.dbs.omni.tw.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by siang on 2017/4/11.
 */

public class PreferenceUtil {
    private final static String PREFS_NAME = "DBS_OMNI";

    //權限提醒
    //for android 5.x
    public enum ENUM_PERMISSION_TYPE
    {
        CAMERA,
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE,
        WRITE_SETTINGS
    }
    private final static String PREF_KEY_OPEN_PERMISSION_CAMERA = "openPermissioncCamera";
    private final static String PREF_KEY_OPEN_PERMISSION_READ_EXTERNAL_STORAGE = "openPermissioncReadExternalStorage";
    private final static String PREF_KEY_OPEN_PERMISSION_WRITE_EXTERNAL_STORAGE = "openPermissioncWriteExternalStorage";
    private final static String PREF_KEY_OPEN_PERMISSION_WRITE_SETTINGS = "openPermissioncWriteSettings";

    private final static String PREF_KEY_OPEN_PERMISSION_GPS = "openPermissioncGPS";

    private final static String PREF_KEY_PUSH_TOKEN = "PREF_KEY_PUSH_TOKEN";

    //其他
    private final static String PREF_KEY_IS_LOGIN = "islogin";
    private final static String PREF_KEY_NICKNAME = "nickname";

    // user data
    private final static String PREF_KEY_SAVE_USER_CODE_STATUS = "PREF_KEY_SAVE_USER_CODE_STATUS";
    private final static String PREF_KEY_USER_CODE = "PREF_KEY_USER_CODE";
    private final static String PREF_KEY_SAVE_LAST_LOGIN_TIME= "PREF_KEY_SAVE_LAST_LOGIN_TIME";
    private final static String PREF_KEY_SAVE_LOGIN_TIME= "PREF_KEY_SAVE_LOGIN_TIME";

    private final static String PREF_KEY_HEADER_DOWNLOAD_TIME = "PREF_KEY_HEADER_DOWNLOAD_TIME";


    //touchID
    private final static String PREF_KEY_OPEN_TOUCHID = "PREF_KEY_OPEN_TOUCHID";
    private final static String PREF_KEY_SAVE_CODE = "PREF_KEY_SAVE_CODE";
    private final static String PREF_KEY_SAVE_R= "PREF_KEY_SAVE_R";

    /**
     * Clean User Data (for Chang User)
     */
    public static void cleanUserDate(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PREF_KEY_IS_LOGIN)
                .remove(PREF_KEY_NICKNAME)
                .remove(PREF_KEY_OPEN_TOUCHID)
                .remove(PREF_KEY_SAVE_USER_CODE_STATUS)
                .remove(PREF_KEY_USER_CODE)
                .remove(PREF_KEY_SAVE_LAST_LOGIN_TIME)
                .remove(PREF_KEY_SAVE_LOGIN_TIME)
                .remove(PREF_KEY_HEADER_DOWNLOAD_TIME)
                .remove(PREF_KEY_SAVE_CODE)
                .remove(PREF_KEY_SAVE_R)
                .commit();

        //刪除資料夾
        sharedMethods.clear();
    }

    /**
     * Only clean TouchID set data (for change 帳號/密碼)
     */
    public static void cleanTouchIDData(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PREF_KEY_OPEN_TOUCHID)
                .remove(PREF_KEY_SAVE_CODE)
                .remove(PREF_KEY_SAVE_R)
                .commit();
    }


    /**
     * 設定是否開放權限
     */
    public static void setPermissionSetted(Context context, boolean value, ENUM_PERMISSION_TYPE permissionType){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        switch (permissionType) {
            case CAMERA:
                editor.putBoolean(PREF_KEY_OPEN_PERMISSION_CAMERA, value);
                break;

            case READ_EXTERNAL_STORAGE:
                editor.putBoolean(PREF_KEY_OPEN_PERMISSION_READ_EXTERNAL_STORAGE, value);
                break;

            case WRITE_EXTERNAL_STORAGE:
                editor.putBoolean(PREF_KEY_OPEN_PERMISSION_WRITE_EXTERNAL_STORAGE, value);
                break;

            case WRITE_SETTINGS:
                editor.putBoolean(PREF_KEY_OPEN_PERMISSION_WRITE_SETTINGS, value);
                break;
        }

        editor.commit();
    }

    public static boolean getPermissionSetted(Context context, ENUM_PERMISSION_TYPE permissionType) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        switch (permissionType) {
            case CAMERA:
                return pref.getBoolean(PREF_KEY_OPEN_PERMISSION_CAMERA, false);

            case READ_EXTERNAL_STORAGE:
                return pref.getBoolean(PREF_KEY_OPEN_PERMISSION_READ_EXTERNAL_STORAGE, false);

            case WRITE_EXTERNAL_STORAGE:
                return pref.getBoolean(PREF_KEY_OPEN_PERMISSION_WRITE_EXTERNAL_STORAGE, false);

            case WRITE_SETTINGS:
                return pref.getBoolean(PREF_KEY_OPEN_PERMISSION_WRITE_SETTINGS, false);
            default:
                return false;
        }

    }

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
     * 回傳是否登入
     */
    public static Boolean getIsLogin(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_KEY_IS_LOGIN, false);
    }

    /**
     * 設定是否登入
     */
    public static void setIsLogin(Context context, Boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_KEY_IS_LOGIN, value);
        editor.commit();
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
     * 回傳是否開啟touch ID
     */
    public static Boolean getTouchIDStatus(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_KEY_OPEN_TOUCHID, false);
    }

    /**
     * 設定是否開啟touch ID
     */
    public static void setTouchIDStatus(Context context, Boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_KEY_OPEN_TOUCHID, value);
        editor.commit();
    }





    /**
     * 回傳是否記錄帳號
     */
    public static Boolean isSaveCodeStatus(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_KEY_SAVE_USER_CODE_STATUS, false);
    }

    /**
     * 設定是否記錄帳號
     */
    public static void setSaveCodeStatus(Context context, Boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_KEY_SAVE_USER_CODE_STATUS, value);
        editor.commit();
    }

    /**
     * 回傳上次登入時間
     */
    public static String getLastLoginTime(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_SAVE_LAST_LOGIN_TIME, "");
    }

    /**
     * 設定上次登入時間
     */
    public static void setLastLoginTime(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_SAVE_LAST_LOGIN_TIME, value);
        editor.commit();
    }

    /**
     * 回傳登入時間
     */
    public static String getLoginTime(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_SAVE_LOGIN_TIME, "");
    }

    /**
     * 設定登入時間
     */
    public static void setLoginTime(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_SAVE_LOGIN_TIME, value);
        editor.commit();
    }

    /**
     * 回傳user code
     */
    public static String getUserCode(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_USER_CODE, "");
    }

    /**
     * 設定user code
     */
    public static void setUserCode(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_USER_CODE, value);
        editor.commit();
    }

    /**
     * 回傳Header Download Time
     */
    public static String getHeaderDownloadTime(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_KEY_HEADER_DOWNLOAD_TIME, "");
    }

    /**
     * 設定Header Download Time
     */
    public static void setHeaderDownloadTime(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_HEADER_DOWNLOAD_TIME, value);
        editor.commit();
    }



    /**
     * ...
     */
    public static String getSaveCode(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String value = pref.getString(PREF_KEY_SAVE_CODE, "");
        if(TextUtils.isEmpty(value)) {
            return "";
        } else {
            KeyStoreUtil keyStoreUtil = new KeyStoreUtil(context);
            return keyStoreUtil.decryptString(KeyStoreUtil.SAVE_TAG, value);
        }
    }

    public static void setSaveCode(Context context, String value) {
        KeyStoreUtil keyStoreUtil = new KeyStoreUtil(context);
        String encryptString = keyStoreUtil.encryptString(KeyStoreUtil.SAVE_TAG, value);

        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_SAVE_CODE, encryptString);
        editor.commit();
    }


    public static String getSaveR(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String value = pref.getString(PREF_KEY_SAVE_R, "");
        if(TextUtils.isEmpty(value)) {
            return "";
        } else {
            KeyStoreUtil keyStoreUtil = new KeyStoreUtil(context);
            return keyStoreUtil.decryptString(KeyStoreUtil.SAVE_TAG, value);
        }
    }

    public static void setSaveR(Context context, String value) {
        KeyStoreUtil keyStoreUtil = new KeyStoreUtil(context);
        String encryptString = keyStoreUtil.encryptString(KeyStoreUtil.SAVE_TAG, value);

        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_SAVE_R, encryptString);
        editor.commit();
    }

}
