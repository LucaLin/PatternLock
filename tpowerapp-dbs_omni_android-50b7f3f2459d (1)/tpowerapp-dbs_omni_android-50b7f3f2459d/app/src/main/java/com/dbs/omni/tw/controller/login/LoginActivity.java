package com.dbs.omni.tw.controller.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.MainActivity;
import com.dbs.omni.tw.controller.home.CreditCardActiveActivity;
import com.dbs.omni.tw.controller.payment.bank.BankPaymentActivity;
import com.dbs.omni.tw.controller.register.RegisterActivity;
import com.dbs.omni.tw.controller.setting.forget.ForgetActivity;
import com.dbs.omni.tw.element.LoginBottomBarItem;
import com.dbs.omni.tw.interfaces.FragmentListener;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.setted.OmniApplication;
import com.dbs.omni.tw.typeMapping.ForgetType;
import com.dbs.omni.tw.util.BitmapUtil;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.UserInfoUtil;
import com.dbs.omni.tw.util.fingeprint.FingerprintUtil;
import com.dbs.omni.tw.util.http.GeneralHttpUtil;
import com.dbs.omni.tw.util.http.RegisterHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.LoginData;
import com.dbs.omni.tw.util.http.mode.register.RegisterData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.mode.SSOData;
import com.dbs.omni.tw.util.http.responsebody.GeneralResponseBodyUtil;
import com.dbs.omni.tw.util.http.responsebody.RegisterResponseBodyUtil;
import com.dbs.omni.tw.util.http.typeMapping.SwitchImageType;
import com.dbs.omni.tw.util.js.ReadJSUtil;

import org.json.JSONException;

import java.util.Calendar;

public class LoginActivity extends ActivityBase {
    public final static String TAG = "LoginActivity";

    public final static String EXTRA_REGISTER_DATA = "EXTRA_REGISTER_DATA";
    public final static String EXTRA_USER_MIMA = "EXTRA_USER_MIMA";

    public static final int REQUEST_REGISTER = 111;

    private String mUserCode;
    private String mMima;
    private String mEncptyMima;
    private String mRKey;
    private boolean isCheckSaveUser = false;

    private boolean isEnableTouchIDPProcess = false;

    private boolean isCrossApp = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region 背景
//        LinearLayout linearLayoutLogin =(LinearLayout) findViewById(R.id.linearLayout_login);
//        linearLayoutLogin.setBackgroundResource();

        //endregion

        // 初始登入狀態
        PreferenceUtil.setIsLogin(this, false);


        initView();



        //tid:transitID uid:userCode func:summary, bill, barcode
        String schema = getIntent().getScheme();
        if(!TextUtils.isEmpty(schema) && schema.equals("dbsommi")) {
            if(!crossAppToPage()) {
                //若 cross app  過程失敗將導回原本流程
//                initView();
            }
        }

//
//        //region 畫面
//        Fragment fragment;
//
//        //未登入過login
//        if(true) {
//            fragment = new FirstLoginFragment();
//        }else { //有登入過login
//
//        }
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.fragment_content, fragment);
//        ft.commit();
        //endregion
    }

    private void initView() {
        setBottomBar();
        goToMainLoginPage();
    }

    private void setDefBackground(int id) {
        LinearLayout linearLayoutLogin = (LinearLayout) findViewById(R.id.linearLayout_login_bg);
        linearLayoutLogin.setBackgroundResource(id);
    }

    private void setBackground(String sourceString) {
        final LinearLayout linearLayoutLogin = (LinearLayout) findViewById(R.id.linearLayout_login_bg);

        Bitmap bitmap;
        if(URLUtil.isValidUrl(sourceString)) {
            BitmapUtil.setBackgroundFromURL(sourceString, new BitmapUtil.DownloadImageTask.OnDownloadImagListener() {
                @Override
                public void OnFinish(Bitmap bitmap) {
                    if(bitmap != null) {
                        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                        linearLayoutLogin.setBackground(drawable);
                    }
                }
            });
        } else {
            bitmap = BitmapUtil.base64ToBitmap(sourceString);
            if(bitmap != null) {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                linearLayoutLogin.setBackground(drawable);
            }
        }
    }



    private void setOverlay(int id) {
        RelativeLayout overlay = (RelativeLayout) findViewById(R.id.relativeLayout_overlay);
        overlay.setBackgroundResource(id);
    }

    private void setBottomBar() {
        LoginBottomBarItem itemRegister = (LoginBottomBarItem) findViewById(R.id.item_register);
        LoginBottomBarItem itemPayBills = (LoginBottomBarItem) findViewById(R.id.item_pay_bills);
        LoginBottomBarItem itemBidCard = (LoginBottomBarItem) findViewById(R.id.item_bid_card);
        LoginBottomBarItem itemActiveCard = (LoginBottomBarItem) findViewById(R.id.item_active_card);

        itemRegister.setBackgroundResource(R.drawable.ic_register);
        itemRegister.setText(R.string.online_registration);

        itemPayBills.setBackgroundResource(R.drawable.ic_pay_bills);
        itemPayBills.setText(R.string.online_pay_bills);

        itemBidCard.setBackgroundResource(R.drawable.ic_bid_card);
        itemBidCard.setText(R.string.personal_service_list_apply_credit_card);

        itemActiveCard.setBackgroundResource(R.drawable.ic_active_card);
        itemActiveCard.setText(R.string.online_active_card);


        itemRegister.setOnClickListener(itemOnClickListener);
        itemPayBills.setOnClickListener(itemOnClickListener);
        itemBidCard.setOnClickListener(itemOnClickListener);
        itemActiveCard.setOnClickListener(itemOnClickListener);
//        itemActiveCard.setOnClickListener(itemOnClickListener);
    }


    private void goToMainLoginPage() {
        setDefBackground(R.drawable.bg_login_main);
        // set server bg image
        getBGImage(SwitchImageType.LOGIN_MAIN);

        LoginMainFragment fragment = new LoginMainFragment();

        fragment.setOnFragmentListener(new FragmentListener.OnFragmentListener() {
            @Override
            public void onNext() {
                BitmapUtil.DownloadImageTaskClose();
//                 new LoginApiTest(LoginActivity.this);
               getSystmBulletin();

            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, fragment);
        ft.commit();
    }

    private void goToFirstLoginPage(String systemMessage) {
        setDefBackground(R.drawable.bg_login);
        // set server bg image
//        getBGImage(SwitchImageType.FIRST_LOGIN);

        setOverlay(R.drawable.bg_login_overlay);
        FirstLoginFragment fragment = FirstLoginFragment.newInstance(systemMessage);;

        fragment.setOnForgetListener(new FirstLoginFragment.OnForgetListener() {
            @Override
            public void OnForgetAccount() {
                goToForgetAccount();
            }

            @Override
            public void OnForgetMima() {
                goToForgetMima();
            }
        });

        fragment.setOnLoginListener(new FirstLoginFragment.OnLoginListener() {
            @Override
            public void OnLogin(String userCode, String mima, boolean isSaveUser) {
                BitmapUtil.DownloadImageTaskClose();
                mUserCode = userCode;
                mMima = mima;
                isCheckSaveUser = isSaveUser;
//                if(GlobalConst.UseLocalMock) {
//                    goToHome();
//                } else {
                    goToLogin();
//                }
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, fragment);
        ft.commit();
    }

    private void goToReLoginPage(final String systemMessage) {
        setDefBackground(R.drawable.bg_login_2);
        // set server bg image
//        getBGImage(SwitchImageType.RE_LOGIN);

        setOverlay(R.drawable.bg_login_overlay_2);
        ReLoginFragment fragment = ReLoginFragment.newInstance(systemMessage);

        fragment.setOnForgetListener(new FirstLoginFragment.OnForgetListener() {
            @Override
            public void OnForgetAccount() {
                goToForgetAccount();
            }

            @Override
            public void OnForgetMima() {
                goToForgetMima();
            }
        });

        fragment.setOnReLoginListener(new ReLoginFragment.OnReLoginListener() {
            @Override
            public void onLogin(String mima) {
                BitmapUtil.DownloadImageTaskClose();
                mUserCode = PreferenceUtil.getUserCode(LoginActivity.this);
                mMima = mima;
//                if(GlobalConst.UseLocalMock) {
//                    goToHome();
//                } else {
                    goToLogin();
//                }
            }

            @Override
            public void onChangeUser() {
                BitmapUtil.DownloadImageTaskClose();
                goToChangeUser(systemMessage);
            }

            @Override
            public void onTouchIDLogin() {
                login(PreferenceUtil.getUserCode(LoginActivity.this), PreferenceUtil.getSaveCode(LoginActivity.this), PreferenceUtil.getSaveR(LoginActivity.this), "Y");
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, fragment);
        ft.commit();
    }




//region Listener
    private View.OnClickListener itemOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewID = v.getId();

            switch (viewID) {
                case R.id.item_register:
                    goToRegisterPage();
                    break;

                case R.id.item_pay_bills:
                    goToPaymentBills();
                    break;

                case R.id.item_bid_card:
                    //goToWeb
                    break;

                case R.id.item_active_card:
                    goToCreditCardActivePage();
                    break;
            }
        }
    };
//endregion


//region Go To Page Function

    //從註冊完成點擊馬上登入
    private void autoLogin(RegisterData data) {
        mUserCode = data.getUserCode();
        mEncptyMima = data.getpCode();
        mRKey = data.getrKey();
        isEnableTouchIDPProcess = data.isEanbleTouchID();
//        if(GlobalConst.UseLocalMock) {
//            goToHome();
//        } else {
            login(mUserCode, mEncptyMima, mRKey, "N");
//        }
    }

    private void goToChangeUser(final String systemMessage) {
        showAlertDialog(getString(R.string.disable_save_user_code), R.string.button_confirm, android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceUtil.cleanUserDate(LoginActivity.this);
                        goToFirstLoginPage(systemMessage);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, false);

    }

    private void goToHome () {
        PreferenceUtil.setIsLogin(this, true);
        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToHome (LoginData data) {
        Calendar calendar = Calendar.getInstance();

        OmniApplication.sUCode = mUserCode;
        OmniApplication.sPCode = mEncptyMima;
        OmniApplication.sRCode = mRKey;

        if(isCheckSaveUser) {
            PreferenceUtil.setSaveCodeStatus(this, true);
            PreferenceUtil.setUserCode(this, mUserCode);
        }

        PreferenceUtil.setLastLoginTime(this, PreferenceUtil.getLoginTime(this));
        PreferenceUtil.setLoginTime(this, FormatUtil.toTimeFormatted(calendar.getTime()));
        PreferenceUtil.setIsLogin(this, true);

        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_LOGIN_DATA, data);

        if(isCrossApp) {
            Uri uri = getIntent().getData();
            String func = uri.getQueryParameter("func");
            String page = null;
            if(func.equalsIgnoreCase("summary")) {
                page = MainActivity.TAB_HOME;
            } else if(func.equalsIgnoreCase("ebill")) {
                page = MainActivity.TAB_BILL;
            } else if(func.equalsIgnoreCase("barcode")) {
                page = MainActivity.TAB_PAYMENT;
            } else {
                page = null;
            }

            if(!TextUtils.isEmpty(page)) {
                intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, page);
            }
        }

        if(isEnableTouchIDPProcess) {
            FingerprintUtil.detectFingerprint(this, null, true, new FingerprintUtil.OnFingerprintListener() {
                @Override
                public void OnFinish() {
                    startActivity(intent);
                    finish();
                }

                @Override
                public void OnFail() {
                    startActivity(intent);
                    finish();
                }
            });
        } else {

//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void goToRegisterPage() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_REGISTER);
    }

    private void goToCreditCardActivePage() {
        Intent intent = new Intent(this, CreditCardActiveActivity.class);
        startActivity(intent);
    }

    private void goToForgetAccount() {
        Intent intent = new Intent(this, ForgetActivity.class);
        intent.putExtra(ForgetActivity.EXTRA_PAGE_TYPE, ForgetType.ACCOUNT.toString());
        startActivity(intent);
    }

    private void goToForgetMima() {
        Intent intent = new Intent(this, ForgetActivity.class);
        intent.putExtra(ForgetActivity.EXTRA_PAGE_TYPE, ForgetType.MIMA.toString());
        startActivity(intent);
    }

    private  void goToPaymentBills() {
        Intent intent = new Intent(this, BankPaymentActivity.class);
        intent.putExtra(BankPaymentActivity.EXTRA_PER_LOGIN, true);
        startActivity(intent);
    }

    /**
     *  status // 0:未開始 1:changUserDevice裝置結束 2:
     */

    private void toLoginProcess(int status) {
        switch (status) {
            case 0:
                Log.d("Login check Process", "user device");
                if (!TextUtils.isEmpty(OmniApplication.sLoginData.getDcFlag()) &&
                        !OmniApplication.sLoginData.getDcFlag().equalsIgnoreCase("1")) {
                    showChangeUserDeviceAlert();
                    PreferenceUtil.cleanTouchIDData(this);
                } else {
                    toLoginProcess(1);
                }
                return;

            case 1:
                Log.d("Login check Process", "get user info");
                UserInfoUtil.getUserInfo(LoginActivity.this, new UserInfoUtil.OnUserInfoListener() {
                    @Override
                    public void OnFinish() {
                        goToHome(OmniApplication.sLoginData);
                    }

                    @Override
                    public void OnFail() {
                        goToHome(OmniApplication.sLoginData);
                    }
                });
                return;
        }
    }

//     case 1:
//             Log.d("Login check Process", "change password");
//                if(!TextUtils.isEmpty(OmniApplication.sLoginData.getFpcFlag()) &&
//                        OmniApplication.sLoginData.getFpcFlag().equalsIgnoreCase("Y")) {
//        toLoginProcess(2);
//    } else {
//        toLoginProcess(2);
//    }
//                return;
//            case 2:
//                    Log.d("Login check Process", "change email");
//                if(!TextUtils.isEmpty(OmniApplication.sLoginData.getRmsFlag()) &&
//                        OmniApplication.sLoginData.getRmsFlag().equalsIgnoreCase("Y")) {
//        toLoginProcess(3);
//    } else {
//        toLoginProcess(3);
//    }
//                return;
//endregion

//region cell api

    private void getBGImage(SwitchImageType type) {

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
                GeneralHttpUtil.getSwitchImage(type , responseListener_getBGImage, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_getBGImage = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                String pWebURL = GeneralResponseBodyUtil.getpWebURL(result.getBody());
                setBackground(pWebURL);
            } else {
//                handleResponseError(result, LoginActivity.this);
                handleCommonError(result, LoginActivity.this);
            }

        }
    };


    private void getSystmBulletin() {
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
                GeneralHttpUtil.getSystemBulletin(responseListener_getSystemBulletin, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_getSystemBulletin = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            String systemMessage = "";
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                systemMessage = GeneralResponseBodyUtil.getContent(result.getBody());
            } else {
                handleCommonError(result, LoginActivity.this);
            }


            if(PreferenceUtil.isSaveCodeStatus(LoginActivity.this)) {
                goToReLoginPage(systemMessage);
            } else {
                goToFirstLoginPage(systemMessage);
            }
        }
    };

    private void goToLogin() {
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
                if(GlobalConst.DisableEncode) {
                    if(!GlobalConst.UseOfficialServer)
                        mUserCode = "THINKPOWER";
                    mEncptyMima = mMima;
                    login(mUserCode, mEncptyMima, ssoData.getrKey(), "N");
                } else {
                    ReadJSUtil.encpty(LoginActivity.this, ssoData.getpKey(), ssoData.getrKey(), mMima, new ReadJSUtil.OnRunScriptListener() {
                        @Override
                        public void OnReturn(String result) {
                            mEncptyMima = result;
                            login(mUserCode, mEncptyMima, ssoData.getrKey(), "N");
                        }

                        @Override
                        public void OnNoFound() {
                        }
                    });

                }
            } else {
                dismissProgressLoading();
                handleResponseError(result, LoginActivity.this);

            }

        }
    };

    private void login(String userCode, String mima, String rKey, String fplFlag) {
        mRKey = rKey;
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
                RegisterHttpUtil.loginUser(userCode, mima, rKey, fplFlag, responseListener, this);
//                showProgressLoading();
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
                OmniApplication.sLoginData = RegisterResponseBodyUtil.getLoginDate(result.getBody());

//                //get OTP note
//                NoteUtil.callGetNotice(NoteUtil.NOTETYPE_OTP, false, LoginActivity.this, new NoteUtil.OnNoteListener() {
//                    @Override
//                    public void OnFinish() {
//                    }
//                    @Override
//                    public void OnFail() {
//                    }
//                });

                toLoginProcess(0);

//                goToHome(loginData);
            } else {
                handleResponseError(result, LoginActivity.this);
            }

        }
    };

//endregion

//region cross app
    private boolean crossAppToPage() {
        Uri uri = getIntent().getData();
        String tID = uri.getQueryParameter("tID");
        String uid = uri.getQueryParameter("uid");

        if(!TextUtils.isEmpty(tID) && !TextUtils.isEmpty(uid)) {
            verifyTransitID(tID, uid);
            return true;
        } else {
            return false;
        }

    }


    //region call api
    private void verifyTransitID(String transitID, String userCode) {

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
                GeneralHttpUtil.verifyTransitID(transitID, userCode, responseListener_crossLogin, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ResponseListener responseListener_crossLogin = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                isCrossApp = true;
                OmniApplication.sLoginData = RegisterResponseBodyUtil.getLoginDate(result.getBody());
                toLoginProcess(0);
//                UserInfoUtil.getUserInfo(LoginActivity.this, new UserInfoUtil.OnUserInfoListener() {
//                    @Override
//                    public void OnFinish() {
//                        goToHome(OmniApplication.sLoginData);
//                    }
//
//                    @Override
//                    public void OnFail() {
//                        goToHome(OmniApplication.sLoginData);
//                    }
//                });

//                goToHome(loginData);
            } else {
                isCrossApp = false;
                handleResponseError(result, LoginActivity.this);
            }

        }
    };

//    private void

    //endregion
//endregion

// region 登入後特殊處理

    private void showChangeUserDeviceAlert() {
        showAlertDialog(getString(R.string.change_user_device_message), android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeUserDevice();
                dialog.dismiss();
            }
        }, false);
    }

    private void changeUserDevice() {

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
                GeneralHttpUtil.changeUserDevice(responseListener_changeUserDevice, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ResponseListener responseListener_changeUserDevice = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                toLoginProcess(1);
            } else {
                handleResponseError(result, LoginActivity.this);
            }

        }
    };



// endregion

//region

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_REGISTER) {
            if(resultCode == RESULT_OK) {
                if (data.hasExtra(EXTRA_REGISTER_DATA)) {
                    autoLogin((RegisterData) data.getParcelableExtra(EXTRA_REGISTER_DATA));
                }
            }

        }
    }


//endregion
}
