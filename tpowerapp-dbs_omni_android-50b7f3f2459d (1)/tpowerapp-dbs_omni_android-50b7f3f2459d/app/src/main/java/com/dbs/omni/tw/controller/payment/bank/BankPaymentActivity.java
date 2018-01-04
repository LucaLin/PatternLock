package com.dbs.omni.tw.controller.payment.bank;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.payment.PaymentHomeFragment;
import com.dbs.omni.tw.controller.result.ConfirmFragment;
import com.dbs.omni.tw.controller.result.ResultFailFragment;
import com.dbs.omni.tw.controller.result.ResultPassFragment;
import com.dbs.omni.tw.model.ShowTextData;
import com.dbs.omni.tw.model.payment.PaidBankData;
import com.dbs.omni.tw.setted.OmniApplication;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.http.PaymentHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.bill.PreLoginBillData;
import com.dbs.omni.tw.util.http.mode.home.CreditCardData;
import com.dbs.omni.tw.util.http.mode.payment.DBSAccountData;
import com.dbs.omni.tw.util.http.mode.payment.DBSPaymentData;
import com.dbs.omni.tw.util.http.mode.payment.OtherBankPaymentData;
import com.dbs.omni.tw.util.http.mode.payment.PreLoginPaymentData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.PaymentResponseBodyUtil;

import org.json.JSONException;

import java.util.ArrayList;

public class BankPaymentActivity extends ActivityBase {

    private static final String TAG = "BankPaymentActivity";
    public static final String EXTRA_PER_LOGIN = "EXTRA_PER_LOGIN";


    private int functionName;
    private DBSPaymentFragment dbsPaymentFragment;
    private OthersPaymentFragment othersPaymentFragment;

    private DBSAccountData dbsAccountData;
    private String stringAmount , stringAnnotation, stringNID, stringSettleNO;
    private PaidBankData paidBankData;
    private String stringTransferAccount;

    private PreLoginBillData preLoginBillData;

    private boolean isEndPage = false;
    private boolean isPreLogin = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        if(getIntent().hasExtra(EXTRA_PER_LOGIN)) {
            setTheme(R.style.AppTheme_NoActionBar_Transparent_AnimationInputMethod);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_bank_payment);


            setPreLoginPayment();
        } else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_bank_payment);


            //取得Function Name
            Intent intent = this.getIntent();
            functionName = intent.getIntExtra(PaymentHomeFragment.FUNCTION＿NAME , 0);

            switch (functionName) {
                case R.string.payment_DBS_main_title:  //星展銀行帳戶繳款
                    setCenterTitleForCloseBar(R.string.payment_DBS_main_title);
                    dbsPaymentFragment = DBSPaymentFragment.newInstance(OmniApplication.sBillOverview.getAmtCurrDue(), OmniApplication.sBillOverview.getAmtMinPayment());

                    dbsPaymentFragment.setOnEventListener(new DBSPaymentFragment.OnEventListener() {
                        @Override
                        //前往確認頁面
                        public void OnNextEvent(String strAMount, String strAnnotation) {
                            stringAmount = strAMount;
                            stringAnnotation = strAnnotation;

                            if (dbsAccountData != null && (!TextUtils.isEmpty(stringAmount))) {
                                gotoConfirmPage(dbsPaymentFragment.TAG);
                            }
                        }

                        //前往選取帳號頁面
                        public void ChooseAccountEvent(double doubleAmount) {
                            setCenterTitleForCloseBar(R.string.choose_paid_account);
                            gotoChooseAccountPage(doubleAmount);
                        }
                    });

                    goToPage(dbsPaymentFragment, false);
                    break;

                case R.string.payment_others_main_title: //他行帳務繳款
                    setCenterTitleForCloseBar(R.string.payment_others_main_title);
                    goToOthersPayment(false);
                    break;
            }

        }
    }

    private void setPreLoginPayment() {

        setCenterTitleForCloseBar(R.string.payment_others_main_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHeadHide(false);

        PreLoginPaymentFragment fragment = new PreLoginPaymentFragment();

        //前往確認頁面
        fragment.setOnPreLoginPaymentListener(new PreLoginPaymentFragment.OnPreLoginPaymentListener() {
            @Override
            public void OnNext(PreLoginBillData data) {
                preLoginBillData = data;
                goToOthersPayment(true);
            }
        });


        goToPage(fragment, false);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        changeBackBarAction();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isEndPage) {
            return;
        }

        super.onBackPressed();

        changeBackBarAction();
    }

    //前往頁面
    private void goToPage(Fragment fragment , boolean isAddBack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.bankPaymentFrameLayout, fragment);

        if(isAddBack == true){
            ft.addToBackStack(TAG);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }

        ft.commit();
    }

    private void goToOthersPayment(boolean isPreLogin) {
        this.isPreLogin = isPreLogin;

        if(isPreLogin) {
            changeHeadBackClose(false);
            othersPaymentFragment = OthersPaymentFragment.newInstance(isPreLogin, preLoginBillData.getAmtCurrDue(), preLoginBillData.getAmtMinPayment(), preLoginBillData.getnID());
        } else {
            othersPaymentFragment = OthersPaymentFragment.newInstance(OmniApplication.sBillOverview.getAmtCurrDue(), OmniApplication.sBillOverview.getAmtMinPayment());
        }
        //前往確認頁面
        othersPaymentFragment.setOnEventListener(new OthersPaymentFragment.OnEventListener() {
            @Override
            public void OnNextEvent(String strAmount, String strAnnotation, String strTransferAccount, String nID) {
                stringAmount = strAmount;
                stringAnnotation = strAnnotation;
                stringTransferAccount = strTransferAccount;
                stringNID = nID;

                if (paidBankData != null && (!TextUtils.isEmpty(strAmount))) {
                    gotoConfirmPage(othersPaymentFragment.TAG);
                }
            }

            @Override
            public void OnNextEventByPreLogin(String strAmount, String strAnnotation, String strTransferAccount, String nID, String settleNO) {
                stringAmount = strAmount;
                stringAnnotation = strAnnotation;
                stringTransferAccount = strTransferAccount;
                stringNID = nID;
                stringSettleNO = settleNO;
                if (paidBankData != null && (!TextUtils.isEmpty(stringAmount))) {
                    gotoConfirmPage(othersPaymentFragment.TAG);
                }
            }

            public void ChooseBankEvent() {
                setCenterTitleForCloseBar(R.string.choose_other_account);
                gotoChooseBankPage();
            }
        });

        goToPage(othersPaymentFragment, isPreLogin);
    }

    //星展帳戶繳款:前往帳號選擇頁
    private void gotoChooseAccountPage(double doubleAmount) {
        changeHeadBackClose(false);

        ChooseAccountFragment chooseAccountFragment = ChooseAccountFragment.newInstance(doubleAmount);
        chooseAccountFragment.setOnChooseAccountListener(new ChooseAccountFragment.OnChooseAccountListener() {
            @Override
            public void onChoose(DBSAccountData data) {

                dbsAccountData = data;

                //回到上一頁
                onBackPressed();
                //把資料帶回繳款夜
                dbsPaymentFragment.setAccount(dbsAccountData);
            }
        });

        goToPage(chooseAccountFragment , true);
    }

    //他行賬戶繳款:前往銀行選擇頁
    private void gotoChooseBankPage() {
        changeHeadBackClose(false);

        ChooseBankFragment chooseBankFragment = new ChooseBankFragment();
        chooseBankFragment.setOnChooseBankListener(new ChooseBankFragment.OnChooseBankListener() {
            @Override
            public void onChoose(PaidBankData data) {

                paidBankData = data;

                //回到上一頁
                onBackPressed();
                //把資料帶回繳款夜
                othersPaymentFragment.setBank(data);
            }
        });

        goToPage(chooseBankFragment , true);
    }

    //前往確認頁
    private void gotoConfirmPage(final String TAG){
        // 修改 back按鈕圖示 第二頁無法判斷是否為第二頁 故只能這樣寫
        changeHeadBackClose(false);

        //加上金錢符號
        String paymentAmount = FormatUtil.toDecimalFormat(this, stringAmount, true);

        ConfirmFragment fragment = new ConfirmFragment();

        //星展銀行繳款
        if(TAG.equalsIgnoreCase(DBSPaymentFragment.TAG)){
            fragment = ConfirmFragment.newInstance( getString(R.string.ready_to_paid),
                    paymentAmount, setDataToConfirmPageDBS());

            fragment.setOnConfirmListener(new ConfirmFragment.OnConfirmListener() {
                @Override
                public void OnNext() {
                    callDBSPayment();
                }
            });
        } else if (TAG.equalsIgnoreCase(OthersPaymentFragment.TAG)){ //他行
            fragment = ConfirmFragment.newInstance( getString(R.string.ready_to_paid),
                    paymentAmount, setDataToConfirmPageOthers());

            fragment.setOnConfirmListener(new ConfirmFragment.OnConfirmListener() {
                @Override
                public void OnNext() {
                    if(isPreLogin) {
                        callPreloginPayment();
                    } else {
                        callOtherBankPayment();
                    }
                }
            });
        }



        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.addToBackStack(TAG);

        ft.replace(R.id.bankPaymentFrameLayout, fragment, ConfirmFragment.TAG);
        ft.commit();
    }

    private void geToResultPass(ArrayList<ShowTextData> list) {

        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        isEndPage = true;

        ResultPassFragment fragment = ResultPassFragment.newInstance(
                getString(R.string.result_payment_textup_pass),
                getString(R.string.result_payment_textdown_pass),
                getString(R.string.finished),
                list);

        fragment.setOnResultPassListener(new ResultPassFragment.OnResultPassListener() {
            @Override
            public void OnEnd() {
                finish();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);

        ft.replace(R.id.bankPaymentFrameLayout, fragment);
        ft.commit();

        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    private void geToResultFail(String message) {
        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        isEndPage = true;

        ResultFailFragment fragment = ResultFailFragment.newInstance(
                getString(R.string.result_payment_textup_fail),
                getString(R.string.result_payment_textdown_fail), message);

        fragment.setOnResultFailListener(new ResultFailFragment.OnResultFailListener() {
            @Override
            public void OnFail() {
                finish();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);

        ft.replace(R.id.bankPaymentFrameLayout, fragment);
        ft.commit();

    }

    private ArrayList<ShowTextData> setDataToConfirmPageDBS() {
        ArrayList<ShowTextData> list = new ArrayList<>();

        list.add(new ShowTextData(getString(R.string.payment_way), getString(R.string.payment_DBS_main_title)));
        list.add(new ShowTextData(getString(R.string.payment_account), dbsAccountData.getAcctName(), FormatUtil.toHideCardNumberShortString(dbsAccountData.getAcctNO())));
        list.add(new ShowTextData(getString(R.string.payment_price), FormatUtil.toDecimalFormat(this, stringAmount, true) + stringAnnotation));

        return list;
    }

    private ArrayList<ShowTextData> setDataToConfirmPageOthers() {
        ArrayList<ShowTextData> list = new ArrayList<>();


        list.add(new ShowTextData(getString(R.string.transfer_bank_code), paidBankData.getBankName()+" "+paidBankData.getBankNo()));
        list.add(new ShowTextData(getString(R.string.user_id), FormatUtil.getHiddenNID(stringNID)));
        list.add(new ShowTextData(getString(R.string.payment_price), FormatUtil.toDecimalFormat(this, stringAmount, true) + stringAnnotation));
        list.add(new ShowTextData(getString(R.string.transfer_account), stringTransferAccount));

        return list;
    }

    private void callDBSPayment(){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(BankPaymentActivity.this)) {
            BankPaymentActivity.this.showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                String stringCcNo = "";
                if(OmniApplication.sLoginData != null){
                    ArrayList<CreditCardData> list = OmniApplication.sLoginData.getCcList();
                    stringCcNo = list.get(0).getCcNO();
                }

                PaymentHttpUtil.DBSPayment(dbsAccountData.getAcctNO(), stringAmount, stringCcNo, responseListener_DBSPayment, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_DBSPayment = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                ArrayList<ShowTextData> list = new ArrayList();

                DBSPaymentData dbsPaymentData = PaymentResponseBodyUtil.getDBSPaymentData(result.getBody());


                list.add(new ShowTextData(getString(R.string.payment_way), getString(R.string.payment_DBS_main_title)));
                list.add(new ShowTextData(getString(R.string.payment_account), dbsAccountData.getAcctName(), dbsPaymentData.getAcctNO()));
                list.add(new ShowTextData(getString(R.string.payment_price), FormatUtil.toDecimalFormat(BankPaymentActivity.this, dbsPaymentData.getAmt(), true) + stringAnnotation ));
                list.add(new ShowTextData(getString(R.string.transaction_number), dbsPaymentData.getTxSEQ()));

                geToResultPass(list);
            } else {

//                // 如果是共同error，不繼續呼叫另一個api
                if (!handleCommonError(result, BankPaymentActivity.this)) {
                    geToResultFail(result.getReturnMessage());
                }
            }
        }
    };

    private void callOtherBankPayment(){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(BankPaymentActivity.this)) {
            BankPaymentActivity.this.showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                String stringCcNo = "";
                if(OmniApplication.sLoginData != null){
                    ArrayList<CreditCardData> list = OmniApplication.sLoginData.getCcList();
                    stringCcNo = list.get(0).getCcNO();
                }

                PaymentHttpUtil.otherBankPayment(paidBankData.getBankNo(), stringTransferAccount, stringAmount, stringCcNo, responseListener_otherBankPayment, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_otherBankPayment = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                ArrayList<ShowTextData> list = new ArrayList();

                OtherBankPaymentData otherBankPaymentData = PaymentResponseBodyUtil.getOtherBankPaymentData(result.getBody());

                list.add(new ShowTextData(getString(R.string.transfer_bank_code), paidBankData.getBankName()+" "+otherBankPaymentData.getBankNO()));
                list.add(new ShowTextData(getString(R.string.user_id), FormatUtil.getHiddenNID(otherBankPaymentData.getnID())));
                list.add(new ShowTextData(getString(R.string.payment_price), FormatUtil.toDecimalFormat(BankPaymentActivity.this, otherBankPaymentData.getAmt(), true) + stringAnnotation));
                list.add(new ShowTextData(getString(R.string.transfer_account), otherBankPaymentData.getAcctNO()));
                list.add(new ShowTextData(getString(R.string.transaction_number), otherBankPaymentData.getTxSEQ()));

                geToResultPass(list);
            } else {
                // 如果是共同error，不繼續呼叫另一個api
                if (!handleCommonError(result, BankPaymentActivity.this)) {
                    geToResultFail(result.getReturnMessage());
                }
            }
        }
    };


    private void callPreloginPayment(){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(BankPaymentActivity.this)) {
            BankPaymentActivity.this.showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {

                PaymentHttpUtil.preLoginPayment(stringNID, paidBankData.getBankNo(), stringTransferAccount, stringAmount, stringSettleNO, responseListener_preLoginPayment, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_preLoginPayment = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                ArrayList<ShowTextData> list = new ArrayList();

                PreLoginPaymentData preLoginPaymentData = PaymentResponseBodyUtil.getPreLoginPaymentData(result.getBody());

                list.add(new ShowTextData(getString(R.string.transfer_bank_code), paidBankData.getBankName()+" "+preLoginPaymentData.getBankNO()));
                list.add(new ShowTextData(getString(R.string.user_id), FormatUtil.getHiddenNID(preLoginPaymentData.getPaymentNO())));
                list.add(new ShowTextData(getString(R.string.payment_price), FormatUtil.toDecimalFormat(BankPaymentActivity.this, preLoginPaymentData.getAmt(), true)
                        + stringAnnotation));
                list.add(new ShowTextData(getString(R.string.transfer_account), preLoginPaymentData.getAcctNO()));
                list.add(new ShowTextData(getString(R.string.transaction_number), preLoginPaymentData.getTxSEQ()));

                geToResultPass(list);
            } else {
                // 如果是共同error，不繼續呼叫另一個api
                if (!handleCommonError(result, BankPaymentActivity.this)) {
                    geToResultFail(result.getReturnMessage());
                }
            }
        }
    };

}
