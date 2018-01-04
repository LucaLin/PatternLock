package com.dbs.omni.tw.controller.setting.editProfile.changeUserData;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.result.ResultFailFragment;
import com.dbs.omni.tw.controller.result.ResultPassFragment;
import com.dbs.omni.tw.controller.setting.PersonlServiceFragment;
import com.dbs.omni.tw.model.ShowTextData;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.UserInfoUtil;
import com.dbs.omni.tw.util.fingeprint.FingerprintUtil;
import com.dbs.omni.tw.util.http.SettingHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.mode.setting.AddressDetail;
import com.dbs.omni.tw.util.http.mode.setting.PhoneDetail;
import com.dbs.omni.tw.util.http.responsebody.GeneralResponseBodyUtil;
import com.dbs.omni.tw.util.http.responsebody.SettingResponseBodyUtil;

import org.json.JSONException;

import java.util.ArrayList;

public class ChangeUserDataActivity extends ActivityBase {

    private static final String TAG = "ChangeUserDataActivity";
//    private EditText edtColumn;
    private int functionName;
//    private String titleArray[];
//    private String contentArray[];
    private String accountAfterModify = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_data);

        //取得Function Name
        Intent intent = this.getIntent();
        functionName = intent.getIntExtra(PersonlServiceFragment.FUNCTION＿NAME , 0);

        switch (functionName){
            case R.string.personal_service_list_account_modify:  //變更使用者帳號
                setCenterTitle(R.string.personal_service_list_account_modify);
                AccountModifyFragment accountModifyFragment = new AccountModifyFragment();

                //前往完成頁面
                accountModifyFragment.setOnEventListener(new AccountModifyFragment.OnEventListener() {
                    @Override
                    public void OnNextEvent(String accountBeforeEncryption, String accountAfterEncryption, String rKey) {
//                        gotoConfirmPage();
                        accountAfterModify = accountBeforeEncryption;
                        callChangeUserCode(accountBeforeEncryption, accountAfterEncryption, rKey);
                    }
                });

                goToPage(accountModifyFragment , false);
                break;

            case R.string.personal_service_list_password_modify: //變更密碼
                setCenterTitle(R.string.personal_service_list_password_modify);
                PasswordModifyFragment passwordModifyFragment = new PasswordModifyFragment();

                //前往完成頁面
                passwordModifyFragment.setOnEventListener(new PasswordModifyFragment.OnEventListener() {
                    @Override
                    public void OnNextEvent(String oldMimaAfterEncryption, String newMimaAfterEncryption, String rKey) {
                        callChangeMima(oldMimaAfterEncryption, newMimaAfterEncryption, rKey);
                    }
                });

                goToPage(passwordModifyFragment , false);
                break;

            case R.string.personal_service_list_email_modify: //變更電子信箱
                setCenterTitle(R.string.personal_service_list_email_modify);
                EmailModifyFragment dataModifyFragment = new EmailModifyFragment();

                //前往完成頁面
                dataModifyFragment.setOnEventListener(new EmailModifyFragment.OnEventListener() {
                    @Override
                    public void OnNextEvent(String newEmail) {
                        callUpdateEmail(newEmail);
                    }

                });

                goToPage(dataModifyFragment , false);
                break;


            case R.string.personal_service_list_address_modify: //變更通訊地址
                setCenterTitle(R.string.personal_service_list_address_modify);
                AddressModifyFragment addressModifyFragment = new AddressModifyFragment();

                //前往完成頁面
                addressModifyFragment.setOnEventListener(new AddressModifyFragment.OnEventListener() {
                    @Override
                    public void OnNextEvent(String inputZipCode, String inputCity, String inputDistrict, String inputAddress) {
                        callUpdateAddress(inputZipCode, inputCity, inputDistrict, inputAddress);
                    }

                });

                goToPage(addressModifyFragment , false);
                break;

            case R.string.personal_service_list_phone_modify: //變更手機號碼
                setCenterTitle(R.string.personal_service_list_phone_modify);
                PhoneModifyFragment phoneModifyFragment = new PhoneModifyFragment();

                //前往完成頁面
                phoneModifyFragment.setOnEventListener(new PhoneModifyFragment.OnEventListener() {
                    @Override
                    public void OnNextEvent(String newPhone, String newCountrycode) {
                        callUpdatePhone(newPhone, newCountrycode);
                    }
                });

                goToPage(phoneModifyFragment , false);
                break;
        }

    }

    //前往頁面
    private void goToPage(Fragment fragment , boolean isAddBack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.changeUserDataFrameLayout, fragment);

        if(isAddBack == true){
            ft.addToBackStack(TAG);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }

        ft.commit();
    }

    private void goToResultPass(int textUpId , int textDownId, ArrayList<ShowTextData> list) {
        // Clear all previous pages
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ResultPassFragment fragment;

        if(functionName == R.string.personal_service_list_phone_modify){
            fragment = ResultPassFragment.newInstance(
                    getString(textUpId),
                    getString(textDownId),
                    getString(R.string.finished),
                    list,
                    ResultPassFragment.IS_MODIFY_PHONE);
        }else if(functionName == R.string.personal_service_list_address_modify){
            fragment = ResultPassFragment.newInstance(
                    getString(textUpId),
                    getString(textDownId),
                    getString(R.string.finished),
                    list,
                    ResultPassFragment.IS_MODIFY_ADDRESS);
        }else{
            if(functionName == R.string.personal_service_list_account_modify) {
                //關閉TouchID 清資料
                FingerprintUtil.cleanTouchID(this);
            }

            fragment = ResultPassFragment.newInstance(
                    getString(textUpId),
                    getString(textDownId),
                    getString(R.string.finished),
                    list);
        }

        fragment.setOnResultPassListener(new ResultPassFragment.OnResultPassListener() {
            @Override
            public void OnEnd() {
                goToHomePage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.replace(R.id.changeUserDataFrameLayout, fragment);
        ft.commit();
    }

    private void goToResultPassModifyPassword(int textUpId , int textDownId) {
        //關閉TouchID 清資料
        FingerprintUtil.cleanTouchID(this);

        // Clear all previous pages
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ArrayList<ShowTextData> list = new ArrayList<>();

        ResultPassFragment fragment = ResultPassFragment.newInstance(
                    getString(textUpId),
                    getString(textDownId),
                    getString(R.string.finished),
                    list,
                    ResultPassFragment.IS_MODIFY_PASSWORD);

        fragment.setOnResultPassListener(new ResultPassFragment.OnResultPassListener() {
            @Override
            public void OnEnd() {
                goToHomePage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.replace(R.id.changeUserDataFrameLayout, fragment);
        ft.commit();
    }

    private void goToResultFail(int textUpId, int textDownId, String message) {
        // Clear all previous pages
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ResultFailFragment fragment = ResultFailFragment.newInstance(
                getString(textUpId), getString(textDownId), message);

        fragment.setOnResultFailListener(new ResultFailFragment.OnResultFailListener() {
            @Override
            public void OnFail() {
                goToHomePage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.replace(R.id.changeUserDataFrameLayout, fragment);
        ft.commit();
    }

    //結束結果頁後前往首頁
    private void goToHomePage() {
        finish();
    }

    private void callChangeUserCode(String accountBeforeEncryption, String accountAfterEncryption, String rKey){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(ChangeUserDataActivity.this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                SettingHttpUtil.changeUserCode(accountBeforeEncryption, accountAfterEncryption, rKey, responseListener_changeUserCode, ChangeUserDataActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_changeUserCode = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                ArrayList<ShowTextData> list = new ArrayList<>();
                list.add(new ShowTextData(getString(R.string.result_personal_service_account_after_updated),accountAfterModify));
                goToResultPass(R.string.result_personal_service_account_modify_textup, R.string.result_personal_service_data_modify_textdown_pass, list);
            } else {
                goToResultFail(R.string.result_personal_service_account_modify_textup, R.string.result_personal_service_data_modify_textdown_fail, result.getReturnMessage());
            }
        }
    };

    private void callChangeMima(String oldMimaAfterEncryption, String newMimaAfterEncryption, String rKey){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(ChangeUserDataActivity.this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                SettingHttpUtil.changeMima(oldMimaAfterEncryption, newMimaAfterEncryption, rKey, responseListener_changeMima, ChangeUserDataActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_changeMima = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                goToResultPassModifyPassword(R.string.result_personal_service_password_modify_textup, R.string.result_personal_service_data_modify_textdown_pass);
            } else {
                goToResultFail(R.string.result_personal_service_password_modify_textup, R.string.result_personal_service_data_modify_textdown_fail, result.getReturnMessage());
            }
        }
    };

    private void callUpdateEmail(String newEmail){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(ChangeUserDataActivity.this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                SettingHttpUtil.updateEmail(newEmail, UserInfoUtil.getsEmailDetail().getEmailID(), responseListener_updateEmail, ChangeUserDataActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_updateEmail = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                ArrayList<ShowTextData> list = new ArrayList<>();
                list.add(new ShowTextData(getString(R.string.result_personal_service_email_after_updated), GeneralResponseBodyUtil.getEmail(result.getBody())));
                goToResultPass(R.string.result_personal_service_email_modify_textup, R.string.result_personal_service_data_modify_textdown_pass, list);
            } else {
                goToResultFail(R.string.result_personal_service_email_modify_textup, R.string.result_personal_service_data_modify_textdown_fail, result.getReturnMessage());
            }
        }
    };

    private void callUpdatePhone(String newPhone, String newCountryCode){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(ChangeUserDataActivity.this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                SettingHttpUtil.updatePhone(newPhone, UserInfoUtil.getsPhoneDetail().getPhoneID(), newCountryCode, responseListener_updatePhone, ChangeUserDataActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_updatePhone = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                PhoneDetail phoneDetail = SettingResponseBodyUtil.getPhoneDetail(result.getBody());

                ArrayList<ShowTextData> list = new ArrayList<>();
                list.add(new ShowTextData(getString(R.string.result_personal_service_phone_after_updated), phoneDetail.getUpdatedPhoneNumber()));
                goToResultPass(R.string.result_personal_service_phone_modify_textup, R.string.result_personal_service_data_modify_textdown_pass, list);
            } else {
                goToResultFail(R.string.result_personal_service_phone_modify_textup, R.string.result_personal_service_data_modify_textdown_fail, result.getReturnMessage());
            }
        }
    };

    private void callUpdateAddress(String inputZipCode, String inputCity, String inputDistrict, String inputAddress){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(ChangeUserDataActivity.this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                SettingHttpUtil.updateAddress(inputCity, inputDistrict, inputZipCode, inputAddress, UserInfoUtil.getsAddressDetail().getAddressID(), responseListener_updateAddress, ChangeUserDataActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_updateAddress = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                AddressDetail addressDetail = SettingResponseBodyUtil.getAddressDetail(result.getBody());

                ArrayList<ShowTextData> list = new ArrayList<>();
                list.add(new ShowTextData(getString(R.string.result_personal_service_address_after_updated), addressDetail.getUpdatedAddress()));
                goToResultPass(R.string.result_personal_service_address_modify_textup, R.string.result_personal_service_data_modify_textdown_pass, list);
            } else {
                goToResultFail(R.string.result_personal_service_address_modify_textup, R.string.result_personal_service_data_modify_textdown_fail, result.getReturnMessage());
            }
        }
    };


}
