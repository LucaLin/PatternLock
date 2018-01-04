package com.dbs.omni.tw.util.fingeprint;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Switch;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.setted.OmniApplication;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.http.SettingHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;

import org.json.JSONException;

/**
 * Created by siang on 2017/6/5.
 */

public class FingerprintUtil {
    private static Activity mActivity;
    private static Switch mTouchIDSwitch;
    
    public static OnFingerprintListener onFingerprintListener;
    public interface OnFingerprintListener {
        void OnFinish();
        void OnFail();
    }

    public static void launcherFingerprint(Activity activity, FingerprintAuthenticationDialogFragment.OnFingerprinListener listener) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) { // < 23 (不支援版本低於6.0)
            return;
        } else {
            new FingerprintCore(activity, listener);
        }
    }

    public static void detectFingerprint(Activity activity, boolean isShowAlert, FingerprintCore.OnDetectFingerprintListener listener) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) { // < 23 (不支援版本低於6.0)
            listener.OnIsNotSupport();
            return;
        } else {
            new FingerprintCore(activity, isShowAlert, listener);
        }
    }

    /**
     *  修改密碼帳號時 關閉使用
     */
    public static void cleanTouchID(Activity activity) {
        ((ActivityBase) activity).showAlertDialog(activity.getString(R.string.modify_password_disable_save_user_code), android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, false);

        PreferenceUtil.cleanTouchIDData(activity);

    }
    

    public static void detectFingerprint(Activity activity, Switch touchIDSwitch, final boolean isShowAlert, OnFingerprintListener listener) {
        mActivity = activity;
        mTouchIDSwitch = touchIDSwitch;
        onFingerprintListener = listener;
        
        FingerprintUtil.detectFingerprint(activity, isShowAlert, new FingerprintCore.OnDetectFingerprintListener() {
            @Override
            public void OnIsSupport() {
                if (PreferenceUtil.isSaveCodeStatus(mActivity)) {
                    if(TextUtils.isEmpty(PreferenceUtil.getSaveCode(mActivity))) {
                        setServerTouchIDStatus();
                    } else {
                        setTouchStatus(false);
                    }
                } else {
                    if (isShowAlert) {
                        ((ActivityBase) mActivity).showAlertDialog(mActivity.getString(R.string.fingerprint_enable_and_save_user_code), android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(TextUtils.isEmpty(PreferenceUtil.getSaveCode(mActivity))) {
                                    setServerTouchIDStatus();
                                } else {
                                    setTouchStatus(false);
                                }

                                dialog.dismiss();
                            }
                        }, false);
                    }
                }
            }

            @Override
            public void OnIsClose() {
                PreferenceUtil.setTouchIDStatus(mActivity, false);
                if(mTouchIDSwitch != null) {
                    mTouchIDSwitch.setChecked(false);
                }
                if(onFingerprintListener != null) {
                    onFingerprintListener.OnFail();
                }
            }

            @Override
            public void OnIsNotSupport() {
                if(onFingerprintListener != null) {
                    onFingerprintListener.OnFail();
                }
            }
        });
    }

    private static void setTouchStatus(boolean isSaveCode) {
        if(isSaveCode) {
            PreferenceUtil.setSaveCode(mActivity, OmniApplication.sPCode);
            PreferenceUtil.setSaveR(mActivity, OmniApplication.sRCode);
            PreferenceUtil.setUserCode(mActivity, OmniApplication.sUCode);
        }

        PreferenceUtil.setTouchIDStatus(mActivity, true);

        if(!PreferenceUtil.isSaveCodeStatus(mActivity)) {
            PreferenceUtil.setUserCode(mActivity, OmniApplication.sUCode);
            PreferenceUtil.setSaveCodeStatus(mActivity, true);
        }
        
        if(onFingerprintListener != null) {
            onFingerprintListener.OnFinish();
        }
    }

//region Call api
    private static void setServerTouchIDStatus() {

        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(mActivity)) {
            ((ActivityBase) mActivity).showAlertDialog(mActivity.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                SettingHttpUtil.setEnableTouchID(responseListener, mActivity);
                ((ActivityBase) mActivity).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static ResponseListener responseListener = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            ((ActivityBase) mActivity).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                setTouchStatus(true);
            } else {
                handleResponseError(result, (ActivityBase) mActivity);
                if(mTouchIDSwitch != null) {
                    mTouchIDSwitch.setChecked(false);
                }
                if(onFingerprintListener != null) {
                    onFingerprintListener.OnFail();
                }
            }

        }
    };
//endregion
}
