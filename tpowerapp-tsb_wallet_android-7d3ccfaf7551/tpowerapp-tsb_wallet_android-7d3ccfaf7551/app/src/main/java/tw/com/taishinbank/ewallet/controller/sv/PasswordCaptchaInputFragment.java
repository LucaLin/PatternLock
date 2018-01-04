package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.captcha.Captcha;
import tw.com.taishinbank.ewallet.captcha.TextCaptcha;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;

public class PasswordCaptchaInputFragment extends Fragment {

    // -- View Hold --
    private ImageView imageCaptcha;
    private ImageButton buttonRefresh;
    private EditText editMima;
    private EditText editCaptcha;
    private TextView textCaptchaError;
    private Captcha captcha;

    // -- Data Model --
    private int captchaLengthLimit;

    // -- Callback --
    private InputsChangedListener listener;

    public PasswordCaptchaInputFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.m_password_captcha, container, false);

        // Set view hold
        setViewHold(view);
        setViewListener();

        // Set value
        captchaLengthLimit = getResources().getInteger(R.integer.captcha_length);
        randomizeCaptcha();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((ActivityBase)getActivity()).hideKeyboard();
    }

    // ---
    // My methods
    // ---

    protected void setViewHold(View parentView) {
        imageCaptcha = (ImageView) parentView.findViewById(R.id.image_captcha);
        buttonRefresh = (ImageButton) parentView.findViewById(R.id.button_refresh);
        editMima = (EditText) parentView.findViewById(R.id.edit_password);
        editCaptcha = (EditText) parentView.findViewById(R.id.edit_captcha);
        textCaptchaError = (TextView) parentView.findViewById(R.id.text_captcha_error);
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

    // 回傳是否有輸入密碼跟正確驗證碼
    public boolean hasValidInputs(){
        return (editMima.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
                && !TextUtils.isEmpty(editCaptcha.getText()) && editCaptcha.getText().length() >= captchaLengthLimit);
    }

    public String getPassword(){
        return editMima.getText().toString();
    }

    // ----
    // User interaction
    // ----

    protected void onEditPasswordTextChanged() {
        if(listener != null){
            listener.onInputsChanged();
        }
    }

    protected void onEditCaptchaTextChanged() {
        if(listener != null){
            listener.onInputsChanged();
        }
    }

    protected void onRefreshCaptchaClicked() {
        randomizeCaptcha();
        editCaptcha.setText("");
        if(listener != null){
            listener.onInputsChanged();
        }
    }

    public boolean validateCaptcha(){
        boolean isValid = captcha.checkAnswer(editCaptcha.getText().toString());
        if(!isValid) {// 顯示錯誤訊息
            textCaptchaError.setVisibility(View.VISIBLE);
            // 更新驗證碼
            randomizeCaptcha();
            // 清空驗證碼輸入框
            editCaptcha.setText("");
        }else{
            textCaptchaError.setVisibility(View.INVISIBLE);
        }
        return isValid;
    }

    public InputsChangedListener getInputsChangedListener() {
        return listener;
    }

    public void setInputsChangedListener(InputsChangedListener inputsChangedListener) {
        this.listener = inputsChangedListener;
    }

    // ----
    // Interface
    // ----
    public interface InputsChangedListener{
        void onInputsChanged();
    }

}
