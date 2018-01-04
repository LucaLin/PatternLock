package com.dbs.omni.tw.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.util.http.SettingHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.mode.setting.AddressDetail;
import com.dbs.omni.tw.util.http.mode.setting.EmailDetail;
import com.dbs.omni.tw.util.http.mode.setting.PhoneDetail;
import com.dbs.omni.tw.util.http.mode.setting.UserInfoDetail;
import com.dbs.omni.tw.util.http.mode.setting.UserNameDetail;
import com.dbs.omni.tw.util.http.responsebody.SettingResponseBodyUtil;

import org.json.JSONException;

/**
 * Created by siang on 2017/6/23.
 */

public class UserInfoUtil {

    private static Activity mActivity;

    private static UserInfoDetail sUserInfoDetail;
    private static UserNameDetail sNameDetail;
    private static EmailDetail sEmailDetail;
    private static PhoneDetail sPhoneDetail;
    private static AddressDetail sAddressDetail;

    private static String sBlockCode;	//客戶身分權限代碼
    private static String sStatmFlag;	//是否申請電子帳單 (Y：已申請、N：未申請)
    private static String sUserCode;	//使用者登入代碼
    private static String sNID;	//National ID 身分證號/居留證號 (只顯示前三後二，其他用X代替)
    private static String sNickname;	//暱稱
    private static String fpcFlag;	//是否強制更換密碼(Y/N，預設為N)
    private static String rmsFlag;	//是否提醒設定email(Y/N，預設為N)

    private static OnUserInfoListener onUserInfoListener;

    public interface OnUserInfoListener {
        void OnFinish();
        void OnFail();
    }


    public static UserInfoDetail getsUserInfoDetail() {
        return sUserInfoDetail;
    }

    public static void setsUserInfoDetail(UserInfoDetail sUserInfoDetail) {
        UserInfoUtil.sUserInfoDetail = sUserInfoDetail;
    }

    public static UserNameDetail getsNameDetail() {
        return sNameDetail;
    }

    public static void setsNameDetail(UserNameDetail sNameDetail) {
        UserInfoUtil.sNameDetail = sNameDetail;
    }

    public static EmailDetail getsEmailDetail() {
        return sEmailDetail;
    }

    public static void setsEmailDetail(EmailDetail sEmailDetail) {
        UserInfoUtil.sEmailDetail = sEmailDetail;
    }

    public static PhoneDetail getsPhoneDetail() {
        return sPhoneDetail;
    }

    public static void setsPhoneDetail(PhoneDetail sPhoneDetail) {
        UserInfoUtil.sPhoneDetail = sPhoneDetail;
    }

    public static AddressDetail getsAddressDetail() {
        return sAddressDetail;
    }

    public static void setsAddressDetail(AddressDetail sAddressDetail) {
        UserInfoUtil.sAddressDetail = sAddressDetail;
    }

    public static String getsBlockCode() {
        return sBlockCode;
    }

    public static void setsBlockCode(String sBlockCode) {
        UserInfoUtil.sBlockCode = sBlockCode;
    }

    public static String getsStatmFlag() {
        return sStatmFlag;
    }

    public static void setsStatmFlag(String sStatmFlag) {
        UserInfoUtil.sStatmFlag = sStatmFlag;
    }

    public static String getsUserCode() {
        return sUserCode;
    }

    public static void setsUserCode(String sUserCode) {
        UserInfoUtil.sUserCode = sUserCode;
    }

    public static String getsNID() {
        return sNID;
    }

    public static void setsNID(String sNID) {
        UserInfoUtil.sNID = sNID;
    }

//    public static String getShowNickname() {
//
//        if(TextUtils.isEmpty(sNickname)) {
//            if(getsNameDetail() == null)
//                return "";
//
//            if(TextUtils.isEmpty(getsNameDetail().getFirstName())) {
//               return "";
//            } else {
//                if(getsNameDetail().getFirstName().length() < 2) {
//                    return getsNameDetail().getFullName();
//                } else {
//                    return getsNameDetail().getFirstName();
//                }
//            }
//        } else {
//            return sNickname;
//        }
//    }

    public static String getsNickname() {
        return sNickname;
    }

    public static void setsNickname(String sNickname) {
        UserInfoUtil.sNickname = sNickname;
    }


    public static ResponseListener getResponseListener() {
        return responseListener;
    }

    public static void setResponseListener(ResponseListener responseListener) {
        UserInfoUtil.responseListener = responseListener;
    }

    //region api
    public static void getUserInfo(Activity activity, OnUserInfoListener listener) {
        mActivity = activity;
        onUserInfoListener = listener;
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(mActivity)) {
            ((ActivityBase) mActivity).showAlertDialog(activity.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                SettingHttpUtil.getUserInfo(responseListener, mActivity);
                ((ActivityBase) mActivity).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private static ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            if(mActivity == null)
                return;

            ((ActivityBase) mActivity).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                sUserInfoDetail = SettingResponseBodyUtil.getUserInfoDetail(result.getBody());

                sNameDetail = sUserInfoDetail.getNameDetl();
                sEmailDetail = sUserInfoDetail.getEmailDetl();
                sAddressDetail = sUserInfoDetail.getAddressDetl();
                sPhoneDetail = sUserInfoDetail.getPhoneDetl();

                sBlockCode = sUserInfoDetail.getBlockCode();
                sStatmFlag = sUserInfoDetail.getStatmFlag();
                sUserCode = sUserInfoDetail.getUserCode();
                sNID = sUserInfoDetail.getnID();
                sNickname = sUserInfoDetail.getNickname();

                String showNickName;
                if(TextUtils.isEmpty(sNickname)) {
                    if(getsNameDetail() == null) {
                        showNickName = "";
                    } else {
                        if (TextUtils.isEmpty(getsNameDetail().getFirstName())) {
                            showNickName = "";
                        } else {
                            if (getsNameDetail().getFirstName().length() < 2) {
                                showNickName = getsNameDetail().getFullName();
                            } else {
                                showNickName = getsNameDetail().getFirstName();
                            }
                        }
                    }
                } else {
                    showNickName = sNickname;
                }
                PreferenceUtil.setNickname(mActivity, showNickName);

                onUserInfoListener.OnFinish();
            } else {
                handleResponseError(result, ((ActivityBase) mActivity));
                onUserInfoListener.OnFail();
            }

        }
    };
//endregion
}
