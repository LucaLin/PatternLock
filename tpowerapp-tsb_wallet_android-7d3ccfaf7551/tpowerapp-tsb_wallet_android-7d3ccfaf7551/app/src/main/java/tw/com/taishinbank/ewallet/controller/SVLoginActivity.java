package tw.com.taishinbank.ewallet.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.wallethome.WebViewActivity;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class SVLoginActivity extends ActivityBase implements View.OnClickListener {

    private static final String TAG = "SVLoginActivity";
    public static final int REQUEST_LOGIN_SV = 123;
    private EditText editUserId;
    private EditText editMima;
    private EditText editUserName;
    private Button buttonLogin;
    private String sysTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sv_login);

        // 設定上方客製的toolbar與置中的標題
        setCenterTitle(R.string.sv_login_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 設定標題與子標題
        setHeadline(null, getString(R.string.sv_login_subtitle));

        LinearLayout buttonCustomerService = (LinearLayout) findViewById(R.id.button_customer_service);
        buttonLogin = (Button) findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(this);
        buttonCustomerService.setOnClickListener(this);

        editUserId = (EditText) findViewById(R.id.edit_userid);
        editMima = (EditText) findViewById(R.id.edit_password);
        editUserName = (EditText) findViewById(R.id.edit_username);

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

        // 設定身分證字號與輸入框狀態
        try {
            String userIdInAes = PreferenceUtil.getUserId(this);
            editUserId.setText(sharedMethods.AESDecrypt(userIdInAes));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // TODO 改成正確的密碼格式
        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editMima.addTextChangedListener(new BasicEditTextWatcher(editMima, getString(R.string.sv_password_format_regular_expression)) {
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

        editUserName.addTextChangedListener(new BasicEditTextWatcher(editUserName, getString(R.string.sv_username_format_regular_expression)) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkLoginButtonEnable();
            }
        });

        // 取得註冊連結
        String url = HttpUtilBase.getSvRegisterUrl(this);

        // 設定馬上註冊的連結文字
        TextView textRegisterSV = (TextView) findViewById(R.id.text_register_sv);
        SpannableString spannableString = new SpannableString(getString(R.string.sv_login_register_text));
        spannableString.setSpan(new OpenWebViewURLSpan(url, this), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);     //网络
        textRegisterSV.setText(spannableString);
        textRegisterSV.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginButtonEnable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        // 按下登入的處理
        if (viewId == R.id.button_login) {
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

            // 呼叫web service執行登入
            try {
                String userIdInAES = sharedMethods.AESEncrypt(editUserId.getText().toString());
                String userPwInAES = sharedMethods.AESEncrypt(editMima.getText().toString());
                GeneralHttpUtil.loginInToSVserver(userIdInAES, editUserName.getText().toString(), userPwInAES, responseListener, this, TAG);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // 按下聯絡客服的處理
        } else if (viewId == R.id.button_customer_service) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + "02-26553355"));
            startActivity(callIntent);
        }
    }

    /**
     * 檢查是否enable登入按鈕
     */
    private void checkLoginButtonEnable() {
//        editUserId.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
//                &&
        if ( editMima.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
                && editUserName.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT) {
            buttonLogin.setEnabled(true);
        } else {
            buttonLogin.setEnabled(false);
        }
    }


    // 呼叫api的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            // 成功的話
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 將svToken跟sysTime存入Preference
                PreferenceUtil.setSVToken(SVLoginActivity.this, result.getSvTokenID());

                // 記錄server回傳的時間
                // 由於mock回傳時間不會變&server時間跟手機時間可能有出入，這裏抓手機上的時間
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                sysTime = df.format(c.getTime());

                // 呼叫web service取得儲值帳戶資訊
                try {
                    RedEnvelopeHttpUtil.getSVAccountInfo(responseListenerSV, SVLoginActivity.this, TAG);
                    showProgressLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO
                }

            } else {
                // 如果不是共同error
                if(!handleCommonError(result, SVLoginActivity.this)){
                    // TODO 其他不成功的判斷與處理
//                showDialog(result.getReturnMessage());
                    showCustomDialog();
                }
            }
        }
    };

    /**
     * 顯示客製的對話框
     */
    private void showCustomDialog(){

        final Dialog dialog = new Dialog(this, R.style.RedEnvelopeDialog);//指定自定義樣式
        dialog.setContentView(R.layout.dialog_red_envelope);//指定自定義layout

        //新增自定義按鈕點擊監聽
        Button btn = (Button)dialog.findViewById(R.id.button_left);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        btn = (Button)dialog.findViewById(R.id.button_right);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 顯示dialog
        dialog.show();
    }

    // 呼叫取得儲值帳戶資訊api的listener
    private ResponseListener responseListenerSV = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();

            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 儲存server回傳的系統時間
                PreferenceUtil.setSVLoginTime(SVLoginActivity.this, sysTime);
                // 儲值帳戶資訊存sharedpreference
                PreferenceUtil.setSVAccountInfo(SVLoginActivity.this, result.getBody().toString());

                // 設定結果
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, SVLoginActivity.this);
            }
        }
    };

    private class OpenWebViewURLSpan extends URLSpan {
        private Context context;
        public OpenWebViewURLSpan(String url, Context context) {
            super(url);
            this.context = context;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_URL, getURL());
            context.startActivity(intent);
        }
    }
}
