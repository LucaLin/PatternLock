package tw.com.taishinbank.ewallet.interfaces;

import android.content.DialogInterface;
import android.content.Intent;

import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public abstract class ResponseListener {
    public abstract void onResponse(ResponseResult result);

    /**
     * 如果returnCode是共同要被處理的error，result會在此方法被處理並回傳true，否則回傳false
     */
    public static boolean handleCommonError(ResponseResult result, final ActivityBase activityBase){
        String returnCode = result.getReturnCode();
        // Token失效，中文是[不合法連線，請重新登入]
        if(returnCode.equals(ResponseResult.RESULT_TOKEN_EXPIRED)){
            activityBase.showAlertDialogAndReset(result.getReturnMessage(), activityBase);
            return true;

        // 儲值token過期，跳儲值登入頁
        }else if(returnCode.equals(ResponseResult.RESULT_SV_TOKEN_EXPIRED)){
            activityBase.startActivityForResult(new Intent(activityBase, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            return true;

        // 強迫更新，到google play
        }else if(returnCode.equals(ResponseResult.RESULT_FORCE_UPDATE)){
            activityBase.showForceUpdate(result.getReturnMessage());
            return true;

        // 服務暫停
        }else if(returnCode.equals(ResponseResult.RESULT_SERVICE_STOP)){
            activityBase.showServiceStop(result.getReturnMessage());
            return true;
        }else if(returnCode.equals(ResponseResult.RESULT_ACCOUNT_NOT_EXIST)){
            PreferenceUtil.setAccountNotExist(activityBase, true);
            activityBase.showServiceStop(result.getReturnMessage());
            return true;
        }
        // TODO
        return false;
    }

    /**
     * 預設處理錯誤代碼的方法
     */
    public static void handleResponseError(ResponseResult result, ActivityBase activityBase){
        if(!handleCommonError(result, activityBase)){
            showAlert(activityBase, result.getReturnMessage());
        }
    }

    /**
     * 簡單彈出錯誤訊息
     */
    public static void showAlert(ActivityBase activityBase, String message) {
        activityBase.showAlertDialog(message, android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
    }


}
