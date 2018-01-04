package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.captcha.Captcha;
import tw.com.taishinbank.ewallet.captcha.TextCaptcha;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.sv.BankAccount;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class NewDesignateAccountAuthFragment extends Fragment {

    private static final String TAG = "NewDesignateAccountAuthFragment";
    // -- View Hold --
    private TextView title;

    //
    private TextView txtBankTitle;
    private TextView txtBankAccount;

    //
    private ImageView imageCaptcha;
    private ImageButton buttonRefresh;
    private EditText editMima;
    private EditText editCaptcha;
    private TextView textCaptchaError;
    private Captcha captcha;

    private Button btnNext;

    // -- Data Model --
    private int captchaLengthLimit;

    public NewDesignateAccountAuthFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_new_bk_designate_account_auth, container, false);

        // Set view hold
        setViewHold(view);
        setViewListener();

        // Set view content, value, listener
        btnNext.setText(R.string.button_confirm_add);
        btnNext.setEnabled(false);
        title.setText(R.string.designate_bank_account_confirm);

        // Set value
        NewDesignateAccountActivity parentActivity = (NewDesignateAccountActivity) getActivity();
        BankAccount bankAccount = parentActivity.getSelectedBankAccount();
        txtBankTitle.setText(bankAccount.getBankTitle());
        String formattedAccount = FormatUtil.toAccountFormat(bankAccount.getAccount());
        txtBankAccount.setText(formattedAccount);

        //
        captchaLengthLimit = getResources().getInteger(R.integer.captcha_length);
        randomizeCaptcha();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((ActivityBase)getActivity()).hideKeyboard();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    // ---
    // My methods
    // ---

    protected void setViewHold(View parentView) {
        title = (TextView) parentView.findViewById(android.R.id.title);
        TextView text1 = (TextView) parentView.findViewById(android.R.id.text1);
        text1.setVisibility(View.GONE);
        txtBankTitle = (TextView) parentView.findViewById(R.id.txt_bank_title);
        txtBankAccount = (TextView) parentView.findViewById(R.id.txt_account_no);

        imageCaptcha = (ImageView) parentView.findViewById(R.id.image_captcha);
        buttonRefresh = (ImageButton) parentView.findViewById(R.id.button_refresh);
        editMima = (EditText) parentView.findViewById(R.id.edit_password);
        editCaptcha = (EditText) parentView.findViewById(R.id.edit_captcha);
        textCaptchaError = (TextView) parentView.findViewById(R.id.text_captcha_error);

        btnNext = (Button) parentView.findViewById(R.id.btn_next);
    }

    protected void setViewListener() {
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshCaptchaClicked();
            }
        });

        editMima.addTextChangedListener(new BasicEditTextWatcher(editMima, getString(R.string.sv_password_format_regular_expression)) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                // 根據是否有輸入密碼跟正確驗證碼enable/disable按鈕
                onEditPasswordTextChanged();
            }
        });

        editCaptcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onEditCaptchaTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClicked();
            }
        });
    }

    // 產生文字（數字與英文字母）的圖形驗證碼
    private void randomizeCaptcha() {
        captcha = new TextCaptcha(getResources().getDimensionPixelSize(R.dimen.image_captcha_width),
                getResources().getDimensionPixelSize(R.dimen.image_captcha_height),
                captchaLengthLimit,
                TextCaptcha.TextOptions.NUMBERS_ONLY,
                getResources().getDimensionPixelSize(R.dimen.image_captcha_textsize));
        imageCaptcha.setImageBitmap(captcha.getImage());
    }

    // 根據是否有輸入密碼跟正確驗證碼enable/disable按鈕
    private void updateNextButtonStatus() {
        if(editMima.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
                && !TextUtils.isEmpty(editCaptcha.getText()) && editCaptcha.getText().length() >= captchaLengthLimit){
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    // ----
    // User interaction
    // ----

    protected void onEditPasswordTextChanged() {
        updateNextButtonStatus();
    }

    protected void onEditCaptchaTextChanged() {
        updateNextButtonStatus();
    }

    protected void onRefreshCaptchaClicked() {
        randomizeCaptcha();
        editCaptcha.setText("");
        updateNextButtonStatus();
    }

    protected void onNextClicked() {
        //Check 1 - Captcha correct
        if(!captcha.checkAnswer(editCaptcha.getText().toString())) {
            // 顯示錯誤訊息
            textCaptchaError.setVisibility(View.VISIBLE);
            // 更新驗證碼
            randomizeCaptcha();
            // 清空驗證碼輸入框
            editCaptcha.setText("");
            // 更新按鈕狀態
            updateNextButtonStatus();

            return ;
        } else {
            textCaptchaError.setVisibility(View.INVISIBLE);
        }

        NewDesignateAccountActivity parentActivity = (NewDesignateAccountActivity) getActivity();
        BankAccount bankAccount = parentActivity.getSelectedBankAccount();
        //Check 2 - Has network
        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(parentActivity)){
            parentActivity.showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        //Call webservice API
        try {
            int type = bankAccount.getBankCode().equals(GlobalConst.CODE_TAISHIN_BANK) ? GlobalConst.CODE_IS_TAISHIN : GlobalConst.CODE_IS_NOT_TAISHIN;
            String userPwInAES = sharedMethods.AESEncrypt(editMima.getText().toString());
            SVHttpUtil.saveDesignateAccount(type, bankAccount.getBankCode(), bankAccount.getAccount(), userPwInAES, responseListener, getActivity(), TAG);
            parentActivity.showProgressLoading();
        } catch (JSONException e) {
            // TODO
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // -------------------
    //  Response Listener
    // -------------------

    // 設定約定帳戶的response listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ActivityBase activityBase = (ActivityBase)getActivity();
            activityBase.dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {

                //Success then end the end process.
                NewDesignateAccountActivity a = (NewDesignateAccountActivity) getActivity();
                a.endProcess();
                NewDesignateAccountAuthFragment.this.getExitTransition();
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

}
