package tw.com.taishinbank.ewallet.controller.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class UserInfoChangeCertificationActivity extends ActivityBase implements View.OnClickListener {

    private static final String TAG = "UserInfoChangeCertificationActivity";

    private EditText editUserId;
    private EditText editMima;
    private Button buttonConfirm;
    EditPersonalInfoActivity.ENUM_UPDATE_ITEM update_item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_change_certification);
        buttonConfirm = (Button) findViewById(R.id.button_confirm);
        buttonConfirm.setEnabled(false);
        buttonConfirm.setOnClickListener(this);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.drawer_item_settings);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        update_item = EditPersonalInfoActivity.ENUM_UPDATE_ITEM.valueOf(getIntent().getStringExtra(EditPersonalInfoActivity.EXTRA_CHANGE_ITEM));
        View topInfoView = findViewById(R.id.headline);

        String message = "";
        switch (update_item)
        {
            case PASSWORD:
                message = String.format(getString(R.string.userinfo_change_message), getString(R.string.login_password));
                break;

            case EMAIL:
                message = String.format(getString(R.string.userinfo_change_message), getString(R.string.edit_personal_email_title));
                break;
            case PHONE:
                message = String.format(getString(R.string.userinfo_change_message), getString(R.string.edit_personal_phone_title));
                break;
        }
        this.setHeadline(topInfoView, R.string.userinfo_change_certification, message);


        // 輸入框
        editUserId = (EditText) findViewById(R.id.edit_userid);
        editMima = (EditText) findViewById(R.id.edit_password);

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
                checkNextButtonEnable();
            }
        });

        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editMima.addTextChangedListener(new BasicEditTextWatcher(editMima, getString(R.string.password_format_regular_expression)) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 做父類別的欄位格式檢核
                super.onTextChanged(s, start, before, count);
                checkNextButtonEnable();
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        if (viewId == R.id.button_confirm){
            try {
                String userIdInAES = sharedMethods.AESEncrypt(editUserId.getText().toString());
                String pwInAES = sharedMethods.AESEncrypt(editMima.getText().toString());
                String phoneDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getPhoneNumber(this));
                String emailDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getEmail(this));
                GeneralHttpUtil.CertificationUser(userIdInAES, pwInAES, phoneDecrypt, emailDecrypt, GeneralHttpUtil.ENUM_CERTIFICATION_TYPE.USER_INFO_CHANGE, responseListener, this, TAG);
                this.showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 檢查是否enable下一步按鈕
     */
    private void checkNextButtonEnable(){
        if(editUserId.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
                && editMima.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT){
            buttonConfirm.setEnabled(true);
        }else{
            buttonConfirm.setEnabled(false);
        }
    }


    private ResponseListener responseListener= new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode不是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS) || !GlobalConst.UseOfficialServer)
            {
                ToChangeUserInfoPage();
            }
            else
            {
                // 執行預設的錯誤處理 
                handleResponseError(result, UserInfoChangeCertificationActivity.this);
            }
        }
    };

    private void ToChangeUserInfoPage() {
        if(getIntent().hasExtra(EditPersonalInfoActivity.EXTRA_CHANGE_ITEM))
        {
            Intent intent = null;
            intent = new Intent(this, UserInfoModifyActivity.class);
            intent.putExtra(EditPersonalInfoActivity.EXTRA_CHANGE_ITEM, update_item.toString());

            startActivity(intent);
        }
    }

}
