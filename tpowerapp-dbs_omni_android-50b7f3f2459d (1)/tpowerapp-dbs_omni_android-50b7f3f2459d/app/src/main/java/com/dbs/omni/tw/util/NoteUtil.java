package com.dbs.omni.tw.util;

import android.app.Activity;
import android.content.DialogInterface;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.util.http.GeneralHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.NoteData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siang on 2017/6/23.
 */

public class NoteUtil {

    private static ENUM_NOTE_TYPE mNoteType;
    private static Activity mActivity;
    private static OnNoteListener onNoteListener;

    public static NoteData otpNoteData;	//OTP

    public enum ENUM_NOTE_TYPE {
        NOTETYPE_OTP("C01"),
        NOTETYPE_OTP_EMAIL("C02");

        ENUM_NOTE_TYPE(String code) {
            this.code = code;
        }

        final public String code;
    }

    public interface OnNoteListener {
        void OnFinish();
        void OnFail();
    }

    public static ENUM_NOTE_TYPE getmNoteType() {
        return mNoteType;
    }

    public static void setmNoteType(ENUM_NOTE_TYPE mNoteType) {
        NoteUtil.mNoteType = mNoteType;
    }

    public static Activity getmActivity() {
        return mActivity;
    }

    public static void setmActivity(Activity mActivity) {
        NoteUtil.mActivity = mActivity;
    }

    public static OnNoteListener getOnNoteListener() {
        return onNoteListener;
    }

    public static void setOnNoteListener(OnNoteListener onNoteListener) {
        NoteUtil.onNoteListener = onNoteListener;
    }

    public static ResponseListener getResponseListener() {
        return responseListener;
    }

    public static void setResponseListener(ResponseListener responseListener) {
        NoteUtil.responseListener = responseListener;
    }

    //region api
    public static void callGetNotice(ENUM_NOTE_TYPE noteType, boolean loginFlag, Activity activity, OnNoteListener listener) {
        mActivity = activity;
        onNoteListener = listener;
        mNoteType = noteType;
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
                GeneralHttpUtil.getNotice(noteType.code, loginFlag, responseListener, mActivity);
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
                switch (mNoteType){
                    case NOTETYPE_OTP: //OTP
                        otpNoteData = getNoteData(result.getBody());
                        break;

                    default:
                        break;
                }
            } else {
                handleResponseError(result, ((ActivityBase) mActivity));
                onNoteListener.OnFail();
            }

        }
    };


    /**
     * getNoteData
     */
    public static NoteData getNoteData(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), NoteData.class);
    }
//endregion
}
