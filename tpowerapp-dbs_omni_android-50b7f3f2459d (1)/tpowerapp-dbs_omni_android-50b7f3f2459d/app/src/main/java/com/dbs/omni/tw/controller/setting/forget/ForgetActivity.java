package com.dbs.omni.tw.controller.setting.forget;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.result.ResultFailFragment;
import com.dbs.omni.tw.controller.result.ResultPassFragment;
import com.dbs.omni.tw.model.ShowTextData;
import com.dbs.omni.tw.typeMapping.ForgetType;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.http.GeneralHttpUtil;
import com.dbs.omni.tw.util.http.SettingHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.SSOData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.GeneralResponseBodyUtil;
import com.dbs.omni.tw.util.http.responsebody.SettingResponseBodyUtil;
import com.dbs.omni.tw.util.js.ReadJSUtil;

import org.json.JSONException;

import java.util.ArrayList;

public class ForgetActivity extends ActivityBase {

    public static final String TAG = "ForgetActivity";
    public static final String EXTRA_PAGE_TYPE = "ARG_PAGE_TYPE";
    
    
    private ForgetType forgetType = ForgetType.None;

    private boolean isEndPage = false;

    private String mNID, mBirthDate, mCardNumber, mEffectiveDate, mUserCode, mExtension, mNewMima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        
        if(getIntent().hasExtra(EXTRA_PAGE_TYPE)) {
            forgetType = ForgetType.valueOf(getIntent().getStringExtra(EXTRA_PAGE_TYPE));
        }

        switch (forgetType) {
            case MIMA:
                setCenterTitleForCloseBar(R.string.forget_mima_title);
                break;
            default:
                setCenterTitleForCloseBar(R.string.forget_account_title);
                break;
        }
        setHeadHide(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        goToForgetMainPage();
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

    private void goToForgetMainPage() {

        ForgetMainFragment fragment = ForgetMainFragment.newInstance(forgetType);

        fragment.setOnForgetListener(new ForgetMainFragment.OnForgetListener() {
            @Override
            public void OnSubmit(String nID, String birthDate, String cardNumber, String effectiveDate) {
                //忘記帳號
                mNID = nID;
                mBirthDate = birthDate;
                mCardNumber = cardNumber;
                mEffectiveDate = effectiveDate;

                identityVerifyOfForgetUserCode();
            }

            @Override
            public void OnSetMima(String nID, String birthDate, String cardNumber, String effectiveDate, String userCode) {
                mNID = nID;
                mBirthDate = birthDate;
                mCardNumber = cardNumber;
                mEffectiveDate = effectiveDate;
                mUserCode = userCode;
                identityVerifyOfForgetMima();
            }
        });
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment);
        ft.commit();
    }

    private void goToSetPassword() {
        // 修改 back按鈕圖示 第二頁無法判斷是否為第二頁 故只能這樣寫
        changeHeadBackClose(false);

        SetMimaFragment fragment = new SetMimaFragment();

        fragment.setOnSetMimaListener(new SetMimaFragment.OnSetMimaListener() {
            @Override
            public void OnNext(String newMima) {
                mNewMima =newMima;
                startOTP(SettingHttpUtil.forgetMimaApiName);
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right); //進入結果頁時設定
//        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, 0, 0);
        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment, SetMimaFragment.TAG);
        ft.commit();
    }

    private void goToResultPass() {
        goToResultPass("");
    }

    private void goToResultPass(String updatedString) {

        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        isEndPage = true;

        ResultPassFragment fragment;

        switch (forgetType) {

            case MIMA:  //變更使用者密碼
                fragment = ResultPassFragment.newInstance(
                        getString(R.string.result_personal_service_password_modify_textup),
                        getString(R.string.result_personal_service_data_modify_textdown_pass),
                        getString(R.string.finished),
                        null,
                        ResultPassFragment.IS_MODIFY_PASSWORD);

                break;
            default:  //變更使用者帳號
                fragment = ResultPassFragment.newInstance(
                        getString(R.string.result_personal_service_account_modify_textup),
                        getString(R.string.result_personal_service_data_modify_textdown_pass),
                        getString(R.string.finished),
                        getShowData(updatedString));

                break;
        }

        fragment.setOnResultPassListener( new ResultPassFragment.OnResultPassListener() {
            @Override
            public void OnEnd() {
                goToHomePage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.replace(R.id.fragment_content, fragment);
        ft.commit();

    }

    private void goToResultFail(String message) {

        isEndPage = true;

        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


        ResultFailFragment fragment;
        // Clear all previous pages
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        switch (forgetType) {

            case MIMA:  //變更使用者密碼
                fragment = ResultFailFragment.newInstance(
                        getString(R.string.result_personal_service_password_modify_textup),
                        getString(R.string.result_personal_service_data_modify_textdown_fail), message);

                break;
            default:  //變更使用者帳號
                fragment = ResultFailFragment.newInstance(
                        getString(R.string.result_personal_service_account_modify_textup),
                        getString(R.string.result_personal_service_data_modify_textdown_fail), message);

                break;
        }


        fragment.setOnResultFailListener(new ResultFailFragment.OnResultFailListener() {
            @Override
            public void OnFail() {
                goToHomePage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.replace(R.id.fragment_content, fragment);
        ft.commit();

    }

    private void goToHomePage() {
        finish();
    }

    private ArrayList<ShowTextData> getShowData(String showString) {
        ArrayList<ShowTextData> list = new ArrayList<>();

        switch (forgetType) {
            case ACCOUNT:
                list.add(new ShowTextData(getString(R.string.edit_profile_user_account), showString));
                break;

            case MIMA:  //變更電子信箱
//                list.add(new ShowTextData(getString(R.string.result_personal_service_email_after_updated), showString));
                break;
        }

        return list;
    }


    private void startOTP(String apiName) {
        showOTPAlertDialog(false, apiName.toUpperCase(), mExtension, new ActivityBase.OnOTPListener() {
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

                        switch (forgetType) {
                            case MIMA:
                                toSetNewMima();
                                break;
                            default:
                                toForgetUserCode();
                                break;
                        }
                    }

//                    @Override
//                    public void OnEnter(AlertDialog dialog) {
//                        dialog.dismiss();
//                        goToResultPass();
//                    }
                });


    }

    private void toSetNewMima() {
        getSSO(); //加密
    }

//region api
    private void identityVerifyOfForgetUserCode() {

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
                SettingHttpUtil.identityVerifyOfForgetUserCode(mNID , mBirthDate, mCardNumber, mEffectiveDate, responseListener_identityVerify_forgetUserCode, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_identityVerify_forgetUserCode = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                mExtension = GeneralResponseBodyUtil.getExtension(result.getBody());
                startOTP(SettingHttpUtil.forgetUserCodeApiName);
            } else {
                handleResponseError(result, ForgetActivity.this);
            }

        }
    };

    private void toForgetUserCode() {

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
                SettingHttpUtil.forgetUserCode(mNID , mBirthDate, mCardNumber, mEffectiveDate, responseListener_forgetUserCode, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_forgetUserCode = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                String newUserCode = SettingResponseBodyUtil.getUserCode(result.getBody());
                goToResultPass(newUserCode);
            } else {
                if(!handleCommonError(result, ForgetActivity.this)) {
                   goToResultFail(result.getReturnMessage());
                }
            }

        }
    };

    private void identityVerifyOfForgetMima() {

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
                SettingHttpUtil.identityVerifyOfForgetMima(mNID , mBirthDate, mCardNumber, mEffectiveDate, mUserCode, responseListener_identityVerify_forgetMima, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_identityVerify_forgetMima = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                mExtension = GeneralResponseBodyUtil.getExtension(result.getBody());
                goToSetPassword();
            } else {
                handleResponseError(result, ForgetActivity.this);
            }

        }
    };

    private void toReSettingMimaOfForgetMima(String newPCode, String rKey) {

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
                SettingHttpUtil.reSettingMimaOfForgetMima(mUserCode, newPCode, rKey, mNID, mBirthDate, mCardNumber ,mEffectiveDate, responseListener_reSettingMima, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_reSettingMima = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                String newUserCode = SettingResponseBodyUtil.getUserCode(result.getBody());
                goToResultPass(newUserCode);
            } else {
                if(!handleCommonError(result, ForgetActivity.this)) {
                    goToResultFail(result.getReturnMessage());
                }
            }

        }
    };

    private void getSSO() {
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
                GeneralHttpUtil.getSSOKey(responseListenerForGetSSO, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private ResponseListener responseListenerForGetSSO = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {



            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                final SSOData ssoData = GeneralResponseBodyUtil.getSSOData(result.getBody());

                ReadJSUtil.encpty(ForgetActivity.this, ssoData.getpKey(), ssoData.getrKey(), mNewMima, new ReadJSUtil.OnRunScriptListener() {
                    @Override
                    public void OnReturn(String result) {
//                        login(mUserCode, result, ssoData);
                        dismissProgressLoading();
                        toReSettingMimaOfForgetMima(result, ssoData.getrKey());
                    }

                    @Override
                    public void OnNoFound() {
                        dismissProgressLoading();
                    }
                });


            } else {
                dismissProgressLoading();

                handleResponseError(result, ForgetActivity.this);

            }


        }
    };

//endregion


//    //region Mock
//    private ArrayList<ShowTextData> getMockData() {
//        ArrayList<ShowTextData> list = new ArrayList<>();
//
//        switch (forgetType) {
//            case ACCOUNT:  //變更使用者帳號
//                list.add(new ShowTextData(getString(R.string.result_personal_service_account_after_updated), "XXXXXXXXXX"));
//                break;
//
//            case MIMA:  //變更電子信箱
//                list.add(new ShowTextData(getString(R.string.result_personal_service_email_after_updated), "XXXXXXXXXX@Xmail.com"));
//                break;
//        }
//
//        return list;
//    }
//    //endregion
}
