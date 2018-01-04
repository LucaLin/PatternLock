package com.dbs.omni.tw.util.http.mode.register;

import org.json.JSONObject;

public class ResponseResult {

    /* --------------- Server API --------------- */
    // 成功
    public static final String RESULT_SUCCESS = "M0000";
    // Session 失效
    public static final String RESULT_SESSION_FAIL = "M0003";
    // OTP驗證流程關閉
    public static final String RESULT_OTP_CLOSE = "M0006";

    // 需要強制更新app版本
//    public static final String RESULT_FORCE_UPDATE = "S9595";
//    // 該服務暫停
//    public static final String RESULT_SERVICE_STOP = "S9998";
//    // Token失效
//    public static final String RESULT_TOKEN_EXPIRED = "E0011";
//    // 註冊的手機/email認證碼不符
//    public static final String RESULT_INVALID_CODE = "E0024";
//    // email與原本的相同
//    public static final String RESULT_INVALID_EMAIL_SAME_CODE = "E0095";
//    // email已被使用
//    public static final String RESULT_INVALID_EMAIL_EXISTED_CODE = "E0094";
//    // 手機已被使用
//    public static final String RESULT_INVALID_PHONE_EXISTED_CODE = "E0020";
//    // SV Token失效
//    public static final String RESULT_SV_TOKEN_EXPIRED = "V0001";
//    // 查無此用戶
//    public static final String RESULT_MEMBER_NOT_FOUND = "E0006";
//    // 優惠代碼已使用
//    public static final String RESULT_SHARE_CODE_HAS_USED = "E0060";
//    // 尚未綁定帳戶
//    public static final String RESULT_NO_DESIGNATE_ACCOUNT = "V0013";
//    // 儲值密碼錯誤
//    public static final String RESULT_INCORRECT_SV_MIMA = "V0004";
//    // 優惠代碼已使用
//    public static final String RESULT_TICKET_PAYMET_TRANS_RETRY = "E0092";
//    // 帳號不存在 已經刪除
//    public static final String RESULT_ACCOUNT_NOT_EXIST = "E0102";

    /* --------------- app自訂error -------------- */
//    public static final String RESULT_SYSTEM_ERROR = "-1";
    // Volley回來的無法連線
    public static final String RESULT_CONNECTION_ERROR = "-2";
    // Volley request timeout
    public static final String RESULT_CONNECTION_TIMEOUT = "-21";
    // Http回傳的error
    public static final String RESULT_HTTP_RESPONSE_ERROR = "-3";
    // 解析json資料error
    public static final String RESULT_JSON_ERROR = "-4";

    protected String returnCode;
    protected String returnMessage;

    protected String txSN;
    protected String txDate;
    protected String txID;
    protected String channel;
    protected String lang;
    protected String sessionID;

    protected String apiName;
    private JSONObject body;
    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject object) {
        this.body = object;
    }

    /**
     *
     * @return
     *     The returnCode
     */
    public String getReturnCode() {
        return returnCode;
    }

    /**
     *
     * @param returnCode
     *     The returnCode
     */
    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    /**
     *
     * @return
     *     The returnMessage
     */
    public String getReturnMessage() {
        return returnMessage;
    }

    /**
     *
     * @param returnMessage
     *     The returnMessage
     */
    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public String getTxSN() {
        return txSN;
    }

    public void setTxSN(String txSN) {
        this.txSN = txSN;
    }

    public String getTxDate() {
        return txDate;
    }

    public void setTxDate(String txDate) {
        this.txDate = txDate;
    }

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    /**
     * 回傳錯誤代碼是否為app自定義的
     */
    public static boolean isAppError(String errorCode){
        return errorCode.equals(RESULT_CONNECTION_ERROR)
                || errorCode.equals(RESULT_HTTP_RESPONSE_ERROR)
                || errorCode.equals(RESULT_JSON_ERROR);
    }

}
