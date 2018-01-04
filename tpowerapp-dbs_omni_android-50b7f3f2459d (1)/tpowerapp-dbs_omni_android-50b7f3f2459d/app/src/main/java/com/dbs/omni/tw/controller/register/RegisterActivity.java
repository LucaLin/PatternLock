package com.dbs.omni.tw.controller.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.login.LoginActivity;
import com.dbs.omni.tw.controller.result.ConfirmFragment;
import com.dbs.omni.tw.controller.result.ResultFailFragment;
import com.dbs.omni.tw.controller.result.ResultPassFragment;
import com.dbs.omni.tw.model.ShowTextData;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.typeMapping.AccountType;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.http.RegisterHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.RegisterData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.GeneralResponseBodyUtil;
import com.dbs.omni.tw.util.http.responsebody.RegisterResponseBodyUtil;

import org.json.JSONException;

import java.util.ArrayList;

public class RegisterActivity extends ActivityBase {

    private static final String TAG = "RegisterActivity";


    public interface OnRegisterListener {
        void onNext(RegisterData registerData);
    }

    private RegisterData mRegisterData;
    public boolean isEnableTouchID = false;
    private boolean isEndPage = false;

    private ArrayList<ShowTextData> mShowTextDatas;

    private String mUserType;  // 1.ANZ 用戶+卡友 2.卡友
    private String mExtension; // 手機號碼(加密)，Client端不需解密，呼叫發送OTP時，request需帶入此欄位
    private AccountType mRegisterType = AccountType.DBS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setCenterTitleForCloseBar(R.string.register_header);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHeadHide(false);

        goToRegisterIdentityPage();



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

    private void goToRegisterIdentityPage() {


        RegisterIdentityFragment fragment = new RegisterIdentityFragment();

        fragment.setOnRegisterListener(new OnRegisterListener() {
            @Override
            public void onNext(RegisterData registerData) {
                mRegisterData = registerData;
                toIdentityDifferentiate(mRegisterData.getnID());

            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);
        ft.replace(R.id.fragment_content, fragment);
        ft.commit();
    }

    private void goToRegisterAccountTypePage() {
        // 修改 back按鈕圖示 第二頁無法判斷是否為第二頁 故只能這樣寫
        changeHeadBackClose(false);

        RegisterAccountTypeFragment fragment = RegisterAccountTypeFragment.newInstance(mUserType);

        fragment.setOnFragmentListener(new RegisterAccountTypeFragment.OnFragmentListener() {
            @Override
            public void onDBSNext() {
                mRegisterType = AccountType.DBS;
                goToRegisterDBSMainPage();
            }

            @Override
            public void onANZNext() {
                mRegisterType = AccountType.ANZ;
                goToRegisterANZMainPage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment, RegisterAccountTypeFragment.TAG);
        ft.commit();
    }

    private void goToRegisterDBSMainPage() {

//        changeBackBarAction();

        RegisterDBSMainFragment fragment = RegisterDBSMainFragment.newInstance(mRegisterData);

        fragment.setOnRegisterListener(new OnRegisterListener() {
            @Override
            public void onNext(RegisterData registerData) {
                mRegisterData = registerData;
                toVerifyUserCode();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment, RegisterDBSMainFragment.TAG);
        ft.commit();
    }

    private void goToRegisterANZMainPage() {

//        changeBackBarAction();

        RegisterANZMainFragment fragment = RegisterANZMainFragment.newInstance(mRegisterData);

        fragment.setOnRegisterListener(new OnRegisterListener() {
            @Override
            public void onNext(RegisterData registerData) {
                mRegisterData = registerData;
                toVerifyUserCode();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment, RegisterANZMainFragment.TAG);
        ft.commit();
    }

    private void goToRegisterAccountDataPage(AccountType accountType) {
        RegisterAccountDataFragment fragment = RegisterAccountDataFragment.newInstance(accountType, mRegisterData);

        fragment.setOnRegisterListener(new RegisterAccountDataFragment.OnRegisterListener() {
            @Override
            public void onNext(RegisterData registerData) {
                mRegisterData = registerData;
                mShowTextDatas = getShowData(registerData.isEanbleTouchID());
                goToConfirmPage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment, RegisterAccountDataFragment.TAG);
        ft.commit();
    }

    private void goToConfirmPage() {
        ConfirmFragment fragment = ConfirmFragment.newInstance( getString(R.string.button_confirm),
                                                                getString(R.string.register_content_title), mShowTextDatas);

        fragment.setOnConfirmListener(new ConfirmFragment.OnConfirmListener() {
            @Override
            public void OnNext() {
                showOTPAlertDialog(false, RegisterHttpUtil.createUserApiName.toUpperCase(), mExtension, new ActivityBase.OnOTPListener() {
                    @Override
                    public void OnClose(AlertDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void OnResend(AlertDialog dialog) {

                    }

                    @Override
                    public void OnFail(AlertDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void OnSuccess(AlertDialog dialog) {
                        dialog.dismiss();
                        toCreateUserMember();
                    }

                    //                    @Override
//                    public void OnEnter(AlertDialog dialog) {
//                        dialog.dismiss();
//                        toCreateUserMember();
////                        geToResultPass();
//                    }
                });
            }

//            @Override
//            public void OnFail() {
////                geToResultFail();
//            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right); //進入結果頁時設定
//        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, 0, 0);
        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment, ConfirmFragment.TAG);
        ft.commit();

//        Intent intent = new Intent(this, ConfirmFragment.class);
//        intent.putExtra(ConfirmFragment.ARG_PAGE_TYPE, CommonPageType.REGISTER.toString());
//        intent.putExtra(ConfirmFragment.ARG_TITLE, getString(R.string.register_header));
//        intent.putExtra(ConfirmFragment.ARG_TEXTUP, getString(R.string.button_confirm));
//        intent.putExtra(ConfirmFragment.ARG_TEXTDOWN, getString(R.string.register_content_title));
//        intent.putExtra(ConfirmFragment.ARG_CONTENT_HASMAP, getMockData());
//
//        startActivity(intent);
    }

    private void geToResultPass() {

        isEndPage = true;
        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ResultPassFragment fragment = ResultPassFragment.newInstance(
                getString(R.string.register_result_pass_header_top),
                getString(R.string.register_result_pass_header_down),
                getString(R.string.login_immediately),
                mShowTextDatas);

        fragment.setOnResultPassListener(new ResultPassFragment.OnResultPassListener() {
            @Override
            public void OnEnd() {
//                if(isEnableTouchID) {
                PreferenceUtil.setSaveCodeStatus(RegisterActivity.this, isEnableTouchID);
                PreferenceUtil.setTouchIDStatus(RegisterActivity.this, isEnableTouchID);
//                }

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

    private void geToResultFail(String errorMessage) {
        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        isEndPage = true;
        ResultFailFragment fragment = ResultFailFragment.newInstance(
                getString(R.string.register_result_fail_header_top),
                getString(R.string.register_result_fail_header_down), errorMessage);

        fragment.setOnResultFailListener(new ResultFailFragment.OnResultFailListener() {
            @Override
            public void OnFail() {
                goToLoginPage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment);
        ft.commit();

    }

    private void goToHomePage() {
        PreferenceUtil.cleanUserDate(this);

        if(GlobalConst.DisableEncode) {
            mRegisterData.setUserCode("EVALIN");
            mRegisterData.setpCode("EvaLinCode");
        }

        Intent intent = getIntent();
        intent.putExtra(LoginActivity.EXTRA_REGISTER_DATA, mRegisterData);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void goToLoginPage() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

//region Mock
//    private ArrayList<ShowTextData> getMockData() {
//        ArrayList<ShowTextData> list = new ArrayList<>();
//
//        list.add(new ShowTextData(getString(R.string.user_id_title), FormatUtil.getHiddenNID("A122145577")));
//        list.add(new ShowTextData(FormatUtil.getDateOfBirthTile(this, false), FormatUtil.toDateFormatted("19831216")));
//        list.add(new ShowTextData(FormatUtil.getCreditCardTile(this, false), FormatUtil.toHideCardNumberString("1234-5555-8888-0099")));
//        list.add(new ShowTextData(getString(R.string.effective_date), "11/32"));
//        list.add(new ShowTextData(getString(R.string.user_account), "XXXXA1234"));
//        list.add(new ShowTextData(FormatUtil.getNickNameTile(this, false), "韋恩"));
//        list.add(new ShowTextData(getString(R.string.personal_service_list_touch_id), getString(R.string.swicth_open)));
//
//        return list;
//    }

    private ArrayList<ShowTextData> getShowData(boolean isEnableTouchID) {
        ArrayList<ShowTextData> list = new ArrayList<>();

        list.add(new ShowTextData(getString(R.string.user_id_title), FormatUtil.getHiddenNID(mRegisterData.getnID())));
        list.add(new ShowTextData(FormatUtil.getDateOfBirthTile(this, false), FormatUtil.toDateFormatted(mRegisterData.getbDate())));
        if(!TextUtils.isEmpty(mRegisterData.getCcNO())) {
            list.add(new ShowTextData(FormatUtil.getCreditCardTile(this, false), FormatUtil.toHideCardNumberString(FormatUtil.toCreditCardFormat(mRegisterData.getCcNO()))));
        }
        if(!TextUtils.isEmpty(mRegisterData.getExpDate())) {
            list.add(new ShowTextData(getString(R.string.effective_date), FormatUtil.toEffectiveDateFormatted(mRegisterData.getExpDate())));
        }

        list.add(new ShowTextData(getString(R.string.user_account), mRegisterData.getUserCode()));

        if(!TextUtils.isEmpty(mRegisterData.getAnzUserCode())) {
            list.add(new ShowTextData(getString(R.string.anz_account_user_id), mRegisterData.getAnzUserCode()));
        }

        if(!TextUtils.isEmpty(mRegisterData.getNickname())) {
            list.add(new ShowTextData(FormatUtil.getNickNameTile(this, false), mRegisterData.getNickname()));
        }

        if(isEnableTouchID) {
            list.add(new ShowTextData(getString(R.string.personal_service_list_touch_id), getString(R.string.swicth_open)));
        } else {
            list.add(new ShowTextData(getString(R.string.personal_service_list_touch_id), getString(R.string.swicth_close)));
        }
        return list;
    }
//endregion


//region api
    private void toIdentityDifferentiate(String nID) {
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
                RegisterHttpUtil.identityDifferentiate(nID, responseListener, this);
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
                mUserType = RegisterResponseBodyUtil.getUserType(result.getBody());
                mExtension = GeneralResponseBodyUtil.getExtension(result.getBody());

                // 取得列表
                goToRegisterAccountTypePage();

            } else {

                handleResponseError(result,RegisterActivity.this);

            }

        }
    };

    private void toVerifyUserCode() {
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

                RegisterHttpUtil.verifyUserCode(mRegisterData.getUserCode(), responseListener_verifyUserCode, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_verifyUserCode = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                goToRegisterAccountDataPage(mRegisterType);
//                switch (mRegisterType) {
//                    case DBS:
//                        goToRegisterAccountDataPage(mRegisterType);
//                        break;
//                    case ANZ:
//                        goToRegisterAccountDataPage(mRegisterType);
//                        break;
//                }


            } else {

                handleResponseError(result, RegisterActivity.this);

            }
        }
    };

    private void toCreateUserMember() {
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

                RegisterHttpUtil.createUserMember(mRegisterData, responseListener_CreateUserMember, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_CreateUserMember = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {

                geToResultPass();

            } else {
                if(!handleCommonError(result, RegisterActivity.this)) {
                    geToResultFail(result.getReturnMessage());
                }

            }
        }
    };

//endregion
}
