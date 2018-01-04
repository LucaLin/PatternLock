package tw.com.taishinbank.ewallet.controller.red;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.captcha.Captcha;
import tw.com.taishinbank.ewallet.captcha.TextCaptcha;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeInputData;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResult;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.RedEnvelopeResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class PasswordInputActivity extends ActivityBase implements View.OnClickListener {

    private static final String TAG = "MyRedEnvelopeFragment";

    private ImageView imageCaptcha;
    private EditText editMima;
    private EditText editCaptcha;
    private TextView textCaptchaError;
    private Captcha captcha;
    private RedEnvelopeInputData inputData;
    private Button buttonNext;
    private SVAccountInfo svAccountInfo;
    private int captchaLengthLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_input);

        // 設置toolbar與置中的標題文字與返回鈕
        setCenterTitle(R.string.title_amount_input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 取得財神紅包物件
        inputData = getIntent().getParcelableExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA);

        editCaptcha = (EditText) findViewById(R.id.edit_captcha);

        buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);

        ImageButton buttonRefresh = (ImageButton) findViewById(R.id.button_refresh);
        buttonRefresh.setOnClickListener(this);

        // 設置接收紅包的人名
        TextView textNamesTo = (TextView) findViewById(R.id.text_names_to);
        textNamesTo.setText(inputData.getMergedNames());
        TextView textNamesNumber = (TextView) findViewById(R.id.text_names_number);
        textNamesNumber.setText(inputData.getNamesNumberString());

        // 設置轉帳總金額
        TextView textAmount = (TextView) findViewById(R.id.text_amount);
        textAmount.setText(FormatUtil.toDecimalFormat(inputData.getTotalAmount()));

        ImageView imagePhoto = (ImageView) findViewById(R.id.image_photo);
        if(inputData.getMemNOs().length > 1) {
            imagePhoto.setImageResource(R.drawable.img_taishin_photo_dark);
        } else {
            // 設定頭像
            ImageLoader imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.list_photo_size));
            imageLoader.loadImage(inputData.getMemNOs()[0], imagePhoto);
        }


        TextView textNameFrom = (TextView) findViewById(R.id.text_name_from);

        // 從preference取得會員暱稱，並設定會員暱稱
        String nickname = PreferenceUtil.getNickname(this);
        textNameFrom.setText(nickname);

        // 設定帳戶
        TextView textAccount = (TextView) findViewById(R.id.text_account);
        svAccountInfo = PreferenceUtil.getSVAccountInfo(this);
        String account = getString(R.string.transfer_from_account) + FormatUtil.toAccountFormat(svAccountInfo.getPrepaidAccount());
        textAccount.setText(account);

        editMima = (EditText) findViewById(R.id.edit_password);
        // 設置密碼輸入框的TextWatcher
        editMima.addTextChangedListener(new BasicEditTextWatcher(editMima, getString(R.string.sv_password_format_regular_expression)) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                // 根據是否有輸入密碼跟正確驗證碼enable/disable按鈕
                checkNextButtonEnable();
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

        captchaLengthLimit = getResources().getInteger(R.integer.captcha_length);
        EditText editCaptcha = (EditText) findViewById(R.id.edit_captcha);
        // 設置驗證碼輸入框的TextWatcher
        editCaptcha.addTextChangedListener(editCaptchaTextWatcher);

        textCaptchaError = (TextView) findViewById(R.id.text_captcha_error);

        imageCaptcha = (ImageView) findViewById(R.id.image_captcha);
        showChallenge();
        checkNextButtonEnable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_next){

            if(!captcha.checkAnswer(editCaptcha.getText().toString())){
                // 顯示錯誤訊息
                textCaptchaError.setVisibility(View.VISIBLE);
                // 更新驗證碼
                showChallenge();
                // 清空驗證碼輸入框
                editCaptcha.setText("");
                // 更新按鈕狀態
                checkNextButtonEnable();
                return ;
            }else{
                textCaptchaError.setVisibility(View.INVISIBLE);
            }

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

            // 呼叫發送紅包的API
            try {

                String userPwInAES = sharedMethods.AESEncrypt(editMima.getText().toString());
                RedEnvelopeHttpUtil.sendRedEnvelope(inputData.getBlessing(), inputData.getType(),
                        inputData.getTotalAmount(), inputData.getMemNOs(), inputData.getAmounts(),
                        userPwInAES, inputData.getReplyToTxfdSeq(), responseListener, this, TAG);
                showProgressLoading();
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if(v.getId() == R.id.button_refresh){
            // 更新驗證碼
            showChallenge();
            // 清空驗證碼輸入框
            editCaptcha.setText("");
            // 更新按鈕狀態
            checkNextButtonEnable();
        }
    }

    private void showChallenge(){
        // 產生文字（數字與英文字母）的圖形驗證碼
        captcha = new TextCaptcha(getResources().getDimensionPixelSize(R.dimen.image_captcha_width),
                getResources().getDimensionPixelSize(R.dimen.image_captcha_height),
                captchaLengthLimit,
                TextCaptcha.TextOptions.NUMBERS_ONLY,
                getResources().getDimensionPixelSize(R.dimen.image_captcha_textsize));
        imageCaptcha.setImageBitmap(captcha.getImage());
    }

    // 驗證碼輸入框的TextWatcher
    private TextWatcher editCaptchaTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 根據是否有輸入密碼跟正確驗證碼enable/disable按鈕
            checkNextButtonEnable();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 檢查是否enable按鈕
     */
    private void checkNextButtonEnable(){
        if(editMima.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
                && !TextUtils.isEmpty(editCaptcha.getText()) && editCaptcha.getText().length() >= captchaLengthLimit){
            buttonNext.setEnabled(true);
        }else{
            buttonNext.setEnabled(false);
        }
    }

    // 呼叫發送紅包的API的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();

            EventAnalyticsUtil.addSpecialEvent(PasswordInputActivity.this, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, EventAnalyticsUtil.logFormatToAPI(result.getApiName(), String.format("Return code: %1$s, Message: %2$s", returnCode, result.getReturnMessage()))));

            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表，傳給下一頁
                RedEnvelopeSentResult sentResult = RedEnvelopeResponseBodyUtil.getRedEnvelopSentResult(result.getBody());

                // 更新儲值帳戶的餘額資訊
                if(!TextUtils.isEmpty(sentResult.getBalance())) {
                    svAccountInfo.setBalance(sentResult.getBalance());
                    Gson gson = new Gson();
                    PreferenceUtil.setSVAccountInfo(PasswordInputActivity.this, gson.toJson(svAccountInfo, SVAccountInfo.class));
                }
                sentResult.setAccount(svAccountInfo.getPrepaidAccount());

                Intent intent = new Intent(PasswordInputActivity.this, SendingResultActivity.class);
                intent.putExtra(SendingResultActivity.EXTRA_SENT_RESULT, sentResult);
                intent.putExtra(SendingResultActivity.EXTRA_BLESSING, inputData.getBlessing());
                startActivity(intent);
            }else{
//                if(returnCode.equals(ResponseResult.RESULT_CONNECTION_TIMEOUT)) {
//                    // 123紅包版的，特別處理 - by Oster - Peter say:因為儲值系統的問題，數量過多可能無法即時回應
//                    showAlertDialogAndToHome("紅包發送成功，請稍後查詢。", PasswordInputActivity.this);

                // 執行預設的錯誤處理 
                handleResponseError(result, PasswordInputActivity.this);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 判斷是否登入成功
        if(resultCode == Activity.RESULT_OK && requestCode == SVLoginActivity.REQUEST_LOGIN_SV) {
            // 更新svAccount
            svAccountInfo = PreferenceUtil.getSVAccountInfo(this);
        }
    }
}
