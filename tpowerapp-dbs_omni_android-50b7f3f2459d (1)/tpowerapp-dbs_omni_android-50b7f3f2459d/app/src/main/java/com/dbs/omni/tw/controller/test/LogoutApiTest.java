package com.dbs.omni.tw.controller.test;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.http.RegisterHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.LogoutDate;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.mode.SSOData;
import com.dbs.omni.tw.util.http.responsebody.RegisterResponseBodyUtil;

import org.json.JSONException;

/**
 * Created by siang on 2017/6/1.
 */

public class LogoutApiTest {
    public final static String TAG = "logoutApiTest";

    private Activity activity;

    private SSOData ssoData;

    public LogoutApiTest(Activity activity) {
        this.activity = activity;
        logout();
    }


    private void logout() {

        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(activity)) {
            ((ActivityBase) activity).showAlertDialog(activity.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取得收到的紅包
            try {
                //本月
                RegisterHttpUtil.logoutUser("1" ,responseListener, activity);
                ((ActivityBase) activity).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    private ResponseListener responseListener = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            if(activity == null)
                return;

            ((ActivityBase) activity).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
//                getSystmInfo();
                LogoutDate logoutDate = RegisterResponseBodyUtil.getLogoutDate(result.getBody());
                Log.v("logoutDate", logoutDate.getLogoutTime());
            } else {
                handleResponseError(result, ((ActivityBase) activity));

                // 如果是共同error，不繼續呼叫另一個api
//                if (handleCommonError(result, ((ActivityBase) activity))) {
//
//                    return;
//                } else {
//                    showAlert(((ActivityBase) activity), result.getReturnMessage());
//                }
            }

        }
    };

}
