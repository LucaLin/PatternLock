package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.model.sv.BankAccount;
import tw.com.taishinbank.ewallet.model.sv.DepositWithdrawResult;
import tw.com.taishinbank.ewallet.model.sv.DesignateAccount;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.SVResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class DepositWithdrawAuthFragment extends Fragment {

    private static final String TAG = "DepositWithdrawAuthFragment";
    // -- View Hold --
    private TextView textBankAccountTitle;
    private TextView textBankTitle;
    private TextView textBankAccount;

    private TextView textDivider1;
    private TextView textDivider2;

    private TextView textSVAccount;
    private TextView textTotalAmount;
    private TextView textTotalAmountTitle;

    private Button buttonNext;

    private PasswordCaptchaInputFragment mimaCaptchaInputFragment;

    public static final int FROM_DEPOSIT = 1;
    public static final int FROM_WITHDRAW = FROM_DEPOSIT + 1;

    private static final String ARG_FROM_PAGE  = "arg_from_page";
    private int fromPage;

    /**
     * 用來建立Fragment
     */
    public static DepositWithdrawAuthFragment newInstance(int fromPage) {
        DepositWithdrawAuthFragment f = new DepositWithdrawAuthFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FROM_PAGE, fromPage);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments() != null) {
            fromPage = getArguments().getInt(ARG_FROM_PAGE);
        }
    }

    public DepositWithdrawAuthFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_deposit_withdraw_auth, container, false);

        mimaCaptchaInputFragment = (PasswordCaptchaInputFragment) getChildFragmentManager().findFragmentById(R.id.fragment_password_captcha);

        // Set view hold
        setViewHold(view);
        setViewListener();

        // Set view content, value, listener

        // Set value
        // 儲值
        if(fromPage == FROM_DEPOSIT){
            DepositActivity depositActivity = (DepositActivity) getActivity();
            BankAccount bankAccount = depositActivity.getBankAccount();
            textBankAccountTitle.setText(R.string.sv_account_transfer_from_with_colon);
            textBankTitle.setText(bankAccount.getBankTitle());
            String formattedAccount = FormatUtil.toAccountFormat(bankAccount.getAccount());
            textBankAccount.setText(formattedAccount);
            textDivider1.setText(R.string.transfer_from);

            textDivider2.setText(R.string.sv_amount_deposit_to);
            SVAccountInfo svAccountInfo = PreferenceUtil.getSVAccountInfo(depositActivity);
            formattedAccount = FormatUtil.toAccountFormat(svAccountInfo.getPrepaidAccount());
            textSVAccount.setText(formattedAccount);
            String formattedAmount = FormatUtil.toDecimalFormatFromString(depositActivity.getInputtedAmount());
            textTotalAmount.setText(formattedAmount);

            buttonNext.setText(R.string.button_confirm_deposit);

        // 提領
        }else if(fromPage == FROM_WITHDRAW) {
            WithdrawActivity withdrawActivity = (WithdrawActivity) getActivity();
            DesignateAccount bankAccount = withdrawActivity.getBankAccount();
            textBankAccountTitle.setText(R.string.sv_account_transfer_to_with_colon);
            textBankTitle.setText(bankAccount.getBankTitle());
            String formattedAccount = FormatUtil.toAccountFormat(bankAccount.getAccount());
            textBankAccount.setText(formattedAccount);
            textDivider1.setText(R.string.sv_amount_withdraw_to);

            textDivider2.setText(R.string.transfer_from);
            SVAccountInfo svAccountInfo = PreferenceUtil.getSVAccountInfo(withdrawActivity);
            formattedAccount = FormatUtil.toAccountFormat(svAccountInfo.getPrepaidAccount());
            textSVAccount.setText(formattedAccount);
            String formattedAmount = FormatUtil.toDecimalFormatFromString(withdrawActivity.getInputtedAmount());
            textTotalAmount.setText(formattedAmount);
            textTotalAmountTitle.setText(R.string.sv_withdraw_amount_with_dollar_sign);

            buttonNext.setText(R.string.button_confirm_withdraw);
        }

        //
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(fromPage == FROM_WITHDRAW) {
            ((ActivityBase) getActivity()).setCenterTitle(R.string.sv_withdraw_auth_title);
        }
        mimaCaptchaInputFragment.setInputsChangedListener(inputsChangedListener);
        updateNextButtonStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
        mimaCaptchaInputFragment.setInputsChangedListener(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 預防再次onCreateView時有例外（說fragment id重複）
        mimaCaptchaInputFragment = (PasswordCaptchaInputFragment) getChildFragmentManager().findFragmentById(R.id.fragment_password_captcha);
        if(mimaCaptchaInputFragment != null) {
            getChildFragmentManager().beginTransaction().remove(mimaCaptchaInputFragment).commitAllowingStateLoss();
            mimaCaptchaInputFragment = null;
        }
    }

    // ---
    // My methods
    // ---

    protected void setViewHold(View parentView) {
        textBankAccountTitle = (TextView) parentView.findViewById(R.id.text_bank_account_title);
        textBankTitle = (TextView) parentView.findViewById(R.id.text_bank_title);
        textBankAccount = (TextView) parentView.findViewById(R.id.text_bank_account);
        textDivider1 = (TextView) parentView.findViewById(R.id.text_divider_1);
        textDivider2 = (TextView) parentView.findViewById(R.id.text_divider_2);
        textSVAccount = (TextView) parentView.findViewById(R.id.text_sv_account);
        textTotalAmount = (TextView) parentView.findViewById(R.id.text_total_amount);
        textTotalAmountTitle = (TextView) parentView.findViewById(R.id.text_total_amount_title);

        buttonNext = (Button) parentView.findViewById(R.id.button_next);
    }

    protected void setViewListener() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClicked();
            }
        });
    }

    // 根據是否有輸入密碼跟正確驗證碼enable/disable按鈕
    private void updateNextButtonStatus() {
        if(mimaCaptchaInputFragment.hasValidInputs()){
            buttonNext.setEnabled(true);
        } else {
            buttonNext.setEnabled(false);
        }
    }

    // ----
    // User interaction
    // ----

    protected void onNextClicked() {
        //Check 1 - Captcha correct
        if(!mimaCaptchaInputFragment.validateCaptcha()){
            // 更新按鈕狀態
            updateNextButtonStatus();
            return ;
        }

        //Check 2 - Has network
        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        // 儲值
        if(fromPage == FROM_DEPOSIT) {
            DepositActivity depositActivity = (DepositActivity) getActivity();
            //Call webservice API
            try {
                String userPwInAES = sharedMethods.AESEncrypt(mimaCaptchaInputFragment.getPassword());
                SVHttpUtil.depositeSVAccount(depositActivity.getBankAccount().getAccount(),
                        Integer.valueOf(depositActivity.getInputtedAmount()),
                        userPwInAES, responseListener, depositActivity, TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // 提領
        }else if(fromPage == FROM_WITHDRAW){
            WithdrawActivity withdrawActivity = (WithdrawActivity) getActivity();
            //Call webservice API
            try {
                String userPwInAES = sharedMethods.AESEncrypt(mimaCaptchaInputFragment.getPassword());
                SVHttpUtil.withdrawSVAccount(Integer.valueOf(withdrawActivity.getInputtedAmount()),
                        userPwInAES, responseListener, withdrawActivity, TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    // -------------------
    //  Listeners
    // -------------------

    // 儲值的response listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ActivityBase activityBase = (ActivityBase)getActivity();
            activityBase.dismissProgressLoading();

            String returnCode = result.getReturnCode();

            EventAnalyticsUtil.addSpecialEvent(getActivity(), new SpecialEvent(SpecialEvent.TYPE_SERVER_API, EventAnalyticsUtil.logFormatToAPI(result.getApiName(), String.format("Return code: %1$s, Message: %2$s", returnCode, result.getReturnMessage()))));

            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                DepositWithdrawResult depositWithdrawResult = SVResponseBodyUtil.getDepositWithdrawResult(result.getBody());
                gotoResult(depositWithdrawResult);

            }else{
                // 如果是儲值密碼錯誤
                if (returnCode.equals(ResponseResult.RESULT_INCORRECT_SV_MIMA)) {
                    activityBase.showAlertDialog(result.getReturnMessage(),
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, true);

                // 其他如果不是共同error，則帶到下一頁
                } else if(!handleCommonError(result, activityBase)){
                    DepositWithdrawResult depositWithdrawResult = new DepositWithdrawResult();
                    depositWithdrawResult.setResult("N");
                    depositWithdrawResult.setAmount(0);
                    depositWithdrawResult.setRtnMsg(result.getReturnMessage());
                    gotoResult(depositWithdrawResult);
                }

            }
        }
    };

    private void gotoResult(DepositWithdrawResult depositWithdrawResult){
        // 儲值
        if(fromPage == FROM_DEPOSIT){
            DepositActivity depositActivity = (DepositActivity) getActivity();
            depositActivity.gotoResult(depositWithdrawResult);
            // 提領
        } else if(fromPage == FROM_WITHDRAW){
            WithdrawActivity withdrawActivity = (WithdrawActivity) getActivity();
            withdrawActivity.gotoResult(depositWithdrawResult);
        }
    }

    private PasswordCaptchaInputFragment.InputsChangedListener
            inputsChangedListener = new PasswordCaptchaInputFragment.InputsChangedListener() {
        @Override
        public void onInputsChanged() {
            updateNextButtonStatus();
        }
    };
}
