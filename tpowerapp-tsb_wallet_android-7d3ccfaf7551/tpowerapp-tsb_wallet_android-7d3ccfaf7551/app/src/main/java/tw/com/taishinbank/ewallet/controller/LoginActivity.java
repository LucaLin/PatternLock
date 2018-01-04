package tw.com.taishinbank.ewallet.controller;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.DiskCache;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.AppUpdateInfo;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PermissionUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class LoginActivity extends ActivityBase implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    private EditText editUserId;
    private EditText editMima;
    private EditText editCellPhone;
    private LinearLayout buttonLogin;
    private String userIdInAES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 設定上方客製的toolbar與置中的標題
        setCenterTitle(R.string.title_activity_welcome);

        // 設定標題與子標題
        setHeadline(null, R.string.login_title, R.string.login_subtitle);

        buttonLogin = (LinearLayout) findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(this);
        buttonLogin.setEnabled(false);
        Button buttonRegister = (Button) findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(this);
        Button buttonForgotMima = (Button) findViewById(R.id.button_forgot_password);
        buttonForgotMima.setOnClickListener(this);

        editUserId = (EditText) findViewById(R.id.edit_userid);
        editMima = (EditText) findViewById(R.id.edit_password);
        editCellPhone = (EditText) findViewById(R.id.edit_cellphone);

        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editUserId.addTextChangedListener(new BasicEditTextWatcher(editUserId, getString(R.string.userid_format_regular_expression)){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if(getLevel() == EDIT_LEVEL_CORRECT){
                    if(!sharedMethods.validateUserIDforROC(s.toString())){
                        setLevel(EDIT_LEVEL_ERROR);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkLoginButtonEnable();
            }
        });


        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editCellPhone.addTextChangedListener(new BasicEditTextWatcher(editCellPhone, getString(R.string.cellphone_format_regular_expression)) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkLoginButtonEnable();
            }
        });

        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editMima.addTextChangedListener(new BasicEditTextWatcher(editMima, getString(R.string.password_format_regular_expression)) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkLoginButtonEnable();
            }
        });
        // 設置密碼輸入框的FocusChangeListener
        editMima.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editMima.setText("");
                }
            }
        });

        // 先確認是否有讀取外部儲存空間的權限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // 如果已經有權限
        if (!PermissionUtil.needGrantRuntimePermission(this, permissions,
                PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
            // 砍光已經有的圖檔目錄
            DiskCache.clear();
        }

        if(PreferenceUtil.needCheckUpdate(this) && NetworkUtil.isConnected(this)){
            // 呼叫確認app版本更新api
            try {
                GeneralHttpUtil.checkAppUpdate(responseListenerCheckUpdate, this, TAG);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // 砍光已經有的圖檔目錄
                DiskCache.clear();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    @Override
    public void onClick(View view){
        int viewId = view.getId();
        // 按下登入的處理
        if(viewId == R.id.button_login){
            // 如果沒有網路連線，顯示提示對話框
            if(!NetworkUtil.isConnected(this)){
                showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
                return ;
            }

            // 呼叫api登入
            try {
                userIdInAES = sharedMethods.AESEncrypt(editUserId.getText().toString());
                String userPwInAES = sharedMethods.AESEncrypt(editMima.getText().toString());
                GeneralHttpUtil.loginToWallet(userIdInAES, editCellPhone.getText().toString(), userPwInAES, responseListener, this, TAG);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        // 按下註冊的處理
        } else if(viewId == R.id.button_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);

        // 按下忘記密碼的處理
        } else if(viewId == R.id.button_forgot_password) {
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        }
    }

    private void queryUserPersonalData()
    {

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(this)){
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        // 呼叫api登入
        try {
            GeneralHttpUtil.queryPersonalData(responseListener_personaldata, this, TAG);
            showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 檢查是否enable登入按鈕
     */
    private void checkLoginButtonEnable(){
        if(editUserId.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
            && editCellPhone.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
            && editMima.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT){
            buttonLogin.setEnabled(true);
        }else{
            buttonLogin.setEnabled(false);
        }
    }

    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得memNo跟nickname、tokenID(generated by server)
                String memNo = GeneralResponseBodyUtil.getMemNo(result.getBody());
                String nickname = GeneralResponseBodyUtil.getNickname(result.getBody());

                // 成功登入後，將加密的身分證字號、memNO與nickname、tokenID存到preference
                PreferenceUtil.setUserId(LoginActivity.this, userIdInAES);
                PreferenceUtil.setMemNO(LoginActivity.this, memNo);
                PreferenceUtil.setNickname(LoginActivity.this, nickname);
                PreferenceUtil.setWalletToken(LoginActivity.this, result.getTokenID());
                PreferenceUtil.setIsBankMem(LoginActivity.this, GeneralResponseBodyUtil.getIsBankMem(result.getBody()));

//                PreferenceUtil.checkLoginSameMember(LoginActivity.this);

                CreditCardUtil.Load17ServerCreditCardListToDB(LoginActivity.this);


                queryUserPersonalData();
//                // 跳轉至主頁面
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, LoginActivity.this);
            }
        }
    };

    private ResponseListener responseListener_personaldata = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得memNo跟nickname、tokenID(generated by server)
                String phoneEncrypt = GeneralResponseBodyUtil.getPhone(result.getBody());
                String emailEncrypt = GeneralResponseBodyUtil.getEmail(result.getBody());
                PreferenceUtil.setEmail(LoginActivity.this, emailEncrypt);
                PreferenceUtil.setPhoneNumber(LoginActivity.this, phoneEncrypt);

                PreferenceUtil.setHasLoadPersonalData(LoginActivity.this, true);
                // 跳轉至主頁面
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, LoginActivity.this);
            }
        }
    };


    // 確認app更新的api callback
    private ResponseListener responseListenerCheckUpdate = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                AppUpdateInfo appUpdateInfo = GeneralResponseBodyUtil.getAppUpdateInfo(result.getBody());
                showAlertIfNeedUpdate(appUpdateInfo);
            // 如果是Token失效，在此不處理，以免被登出後導到這頁無窮迴圈。
            }else if(returnCode.equals(ResponseResult.RESULT_TOKEN_EXPIRED)){
            // 如果不是共同error
            }else if(!handleCommonError(result, LoginActivity.this)) {
                // 目前不做事
            }
        }
    };
}
