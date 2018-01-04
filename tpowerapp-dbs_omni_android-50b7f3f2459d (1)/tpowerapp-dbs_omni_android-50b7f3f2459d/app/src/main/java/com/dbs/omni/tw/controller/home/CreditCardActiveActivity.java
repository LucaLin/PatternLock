package com.dbs.omni.tw.controller.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.result.ResultFailFragment;
import com.dbs.omni.tw.controller.result.ResultPassFragment;
import com.dbs.omni.tw.model.ShowTextData;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.http.HomeHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.home.CreditCardData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class CreditCardActiveActivity extends ActivityBase {

    private static final String TAG = "CreditCardActiveActivity";

    public static final String EXTRA_CARD_DATA = "extra_card_data";

    private boolean isEndPage = false;
    private boolean isPreLogin = false;
    private CreditCardData mCreditCardData;

    private String mCardNumber, mExpDate, activeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(getIntent().hasExtra(EXTRA_CARD_DATA)) {
            setTheme(R.style.AppTheme_NoActionBar_Transparent_ActivityAnimation);
            mCreditCardData = getIntent().getParcelableExtra(EXTRA_CARD_DATA);
            isPreLogin = false;
        } else {
            isPreLogin = true;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_active);

        setCenterTitleForCloseBar(R.string.online_active_card);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHeadHide(false);

        goToCreditCardActive();



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

    private void goToCreditCardActive() {
        CreditCardActiveFragment fragment;
        if(mCreditCardData != null) {
            fragment = CreditCardActiveFragment.newInstance(mCreditCardData);
        } else {
            fragment = CreditCardActiveFragment.newInstance();
        }

        if(fragment != null) {
            fragment.setOnActiveListener(new CreditCardActiveFragment.OnActiveListener() {
                @Override
                public void OnActive(String cardNumber, String expDate, String activeMima) {
                    mCardNumber = cardNumber;
                    mExpDate = expDate;

                    if(isPreLogin) {
                        goToActiveCreditCardForPreLogin(activeMima);
                    } else {
                        goToActiveCreditCard(activeMima);
                    }
                }
            });

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);
            ft.replace(R.id.fragment_content, fragment);
            ft.commit();
        }
    }

    private void geToResultPass() {

        isEndPage = true;
        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ResultPassFragment fragment = ResultPassFragment.newInstance(
                getString(R.string.active_success),
                getString(R.string.online_active_card),
                getString(R.string.finished),
                getMockData());

        fragment.setOnResultPassListener(new ResultPassFragment.OnResultPassListener() {
            @Override
            public void OnEnd() {
                goToHomePage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment);
        ft.commit();

        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    private void geToResultFail(String msg) {
        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        isEndPage = true;

        ResultFailFragment fragment = ResultFailFragment.newInstance(
                getString(R.string.register_result_fail_header_down),
                getString(R.string.register_result_fail_header_down), msg);

        fragment.setOnResultFailListener(new ResultFailFragment.OnResultFailListener() {
            @Override
            public void OnFail() {
                goToHomePage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment);
        ft.commit();

    }

    private void goToHomePage() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        finish();
    }


//region Mock
    private ArrayList<ShowTextData> getMockData() {



        ArrayList<ShowTextData> list = new ArrayList<>();

        list.add(new ShowTextData(getString(R.string.credit_card_number_title), FormatUtil.toHideCardNumberString(mCardNumber)));
        list.add(new ShowTextData(FormatUtil.getEffectiveDate(this, false), mExpDate));
        list.add(new ShowTextData(getString(R.string.credit_card_active_time), activeTime));

        return list;
    }
//endregion

//region
    private void goToActiveCreditCardForPreLogin(String activeMima) {

        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                HomeHttpUtil.activeCreditCardForPreLogin(mCardNumber, mExpDate, activeMima, responseListener_forPreLogin, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_forPreLogin = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                Calendar calendar = Calendar.getInstance();
                activeTime = FormatUtil.toTimeFormatted(calendar.getTime());
                geToResultPass();
            } else {
                if(!handleCommonError(result, CreditCardActiveActivity.this)) {
                    geToResultFail(result.getReturnMessage());
                }
            }

        }
    };

    private void goToActiveCreditCard(String activeMima) {

        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {

                HomeHttpUtil.activeCreditCard(mCreditCardData.getCcID(), mCardNumber, mExpDate, activeMima, responseListener, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                Calendar calendar = Calendar.getInstance();
                activeTime = FormatUtil.toTimeFormatted(calendar.getTime());
                geToResultPass();
            } else {
                if(!handleCommonError(result, CreditCardActiveActivity.this)) {
                    geToResultFail(result.getReturnMessage());
                }
            }

        }
    };
//endregion

}
