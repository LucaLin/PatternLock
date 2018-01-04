package com.dbs.omni.tw.controller;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.element.SymbolItem;
import com.dbs.omni.tw.gcm.TimeoutReceiver;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.setted.OmniApplication;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.NoteUtil;
import com.dbs.omni.tw.util.PermissionUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.http.GeneralHttpUtil;
import com.dbs.omni.tw.util.http.HttpUtilBase;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.NoteListDetail;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.GeneralResponseBodyUtil;

import org.json.JSONException;

import java.util.Calendar;


public class ActivityBase extends AppCompatActivity implements PermissionUtil.OnPermissionListener {

    private ProgressDialog progressDialog;
    private String TAG;

    //  OTP
    private View otpView;
    private CountDownTimer countdownTimer;
    public static final int TEN_MINUTES = 10*1000;
    public static final int ONE_SECOND = 1000;
    private TextView textCountdownTime;
    private Button btnOTPResend;
    private OnOTPListener onOTPListener;
    private AlertDialog mOTPDialog;
    private String mOpaque, mPhoneNumber;

    public interface OnOTPListener {
        void OnClose(AlertDialog dialog);
        void OnResend(AlertDialog dialog);
        void OnFail(AlertDialog dialog);
        void OnSuccess(AlertDialog dialog);
    }

    public interface OnCardDetailListener {
        void OnClose(AlertDialog dialog);
        void OnMoreDetail(AlertDialog dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();

        // 讓所有子activity都固定為直向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 將畫面設為全畫面、延至status
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        if (GlobalConst.ENABLE_CHECK_RT_AND_AUTH) {
//            checkIsRT();
//        }

        openGPS();
    }

    @Override
    protected void onResume() {
        super.onResume();

        HttpUtilBase.initApiHeader(this);
//        if (GlobalConst.ENABLE_CHECK_RT_AND_AUTH) {
//            checkIsRT();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        EventAnalyticsUtil.close();
//        CreditCardUtil.close();
        HttpUtilBase.cancelQueue(TAG);
        OmniApplication.removeGPS();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 返回鈕
            case android.R.id.home:
                // 做按下返回鍵的事，這樣動畫才會一致。
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void showProgressLoading() {
        // 顯示loading...對話框
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, getString(R.string.progress_dialog_title), getString(R.string.loading), true, false);
        } else if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    public void dismissProgressLoading() {
        // 關閉loading...對話框
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 設定上方客製的toolbar與置中的標題
     */
    public void setCenterTitle(int titleResId) {
        setCenterTitle(getString(titleResId));
    }

    /**
     * 設定上方客製的toolbar與置中的標題
     */
    public void setCenterTitle(String title) {
        setCenterTitle(title, true, false);
    }

    public void setCenterTitleForCloseBar(int titleResId) {
        setCenterTitleForCloseBar(getString(titleResId));
    }

    public void setCenterTitleForCloseBar(String title) {
        setCenterTitle(title, true, true);
    }

    public void setCenterTitle(String title, boolean overrideActionBar, boolean isCloseBar ) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            TextView textViewTitle = (TextView) toolbar.findViewById(R.id.custom_title);
            textViewTitle.setText(title);
//            try {
//                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
//                int themeResId = packageInfo.applicationInfo.theme;
//                if( getResources().getResourceEntryName(themeResId) == getResources().newTheme().) {
//                    textViewTitle.setTextColor(R.color.colorRedPrimary);
//                } else if( getResources().getResourceEntryName(themeResId) == getResources().getResourceEntryName(R.style.AppTheme_NoActionBar_Transparent_AnimationInputMethod)) {
//                    textViewTitle.setTextColor(R.color.colorGrayPrimaryDark);
//                }
//
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }


//
            ImageView statusBar = (ImageView) findViewById(R.id.custom_status_bar);
            int statusBarHeight = getStatusBarHeight();
            ViewGroup.LayoutParams statusBarParams = statusBar.getLayoutParams();
            statusBarParams.height = statusBarHeight;
            statusBar.setLayoutParams(statusBarParams);
            // TODO 邏輯需修改
            if(overrideActionBar) {
                changeHeadBackClose(isCloseBar);
            }else{
//                setupDrawerToggle();
            }
        }
    }

    public void changeHeadBackClose(boolean isCloseBar) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(isCloseBar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        } else {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

//    /**
//     * 設置drawer與toggle （選單）
//     */
//    public void setupDrawerToggle(){
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//    }

    /**
     * 設定headline的文字內容
     * @param rootView      如果是在fragment，傳入Headline的View; 如果是在Activity，直接傳入null
     * @param titleResId    第一行標題文字resource id
     * @param subtitleResId 第二行子標題文字resource id
     */
    public void setHeadline(View rootView, int titleResId, int subtitleResId) {
        setHeadline(rootView, titleResId, getString(subtitleResId));
    }

    /**
     * 設定headline的文字內容
     * @param rootView      如果是在fragment，傳入Headline的View; 如果是在Activity，直接傳入null
     * @param titleResId    第一行標題文字resource id
     * @param subtitleString 第二行子標題文字String
     */
    public void setHeadline(View rootView, int titleResId, String subtitleString) {
        TextView title, subtitle;
        if (rootView != null) {
            title = (TextView) rootView.findViewById(android.R.id.title);
            subtitle = (TextView) rootView.findViewById(android.R.id.text1);
        } else {
            title = (TextView) findViewById(android.R.id.title);
            subtitle = (TextView) findViewById(android.R.id.text1);
        }

        if(titleResId > 0){
            title.setText(titleResId);
        }else {
            title.setVisibility(View.GONE);
        }

        if(TextUtils.isEmpty(subtitleString)){
            subtitle.setVisibility(View.GONE);
        }else {
            subtitle.setText(subtitleString);
        }
    }


    /**
     * 設定headline的文字內容
     * @param rootView      如果是在fragment，傳入Headline的View; 如果是在Activity，直接傳入null
     * @param titleResId    第一行標題文字resource id
     */
    public void setHeadline(View rootView, int titleResId) {
        setHeadline(rootView, titleResId, null);
    }

    /**
     * 設定headline的文字內容
     * @param rootView      如果是在fragment，傳入Headline的View; 如果是在Activity，直接傳入null
     * @param subtitleString 第二行子標題文字String
     */
    public void setHeadline(View rootView, String subtitleString) {
        setHeadline(rootView, 0, subtitleString);
    }


    /**
     * 設定 ActionBar 背景為紅色
     */
    public void setHeadNoTransparentBackground() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            toolbar.setBackgroundResource(R.color.colorRedPrimary);
            TextView textViewTitle = (TextView) toolbar.findViewById(R.id.custom_title);

            if (Build.VERSION.SDK_INT < 23) {
                textViewTitle.setTextAppearance(this, R.style.AppTheme_AppToolbar_White_Title);
            } else {
                textViewTitle.setTextAppearance(R.style.AppTheme_AppToolbar_White_Title);
            }
        }
    }

    /**
     * 設定 ActionBar 背景為紅色
     */
    public void setHeadTransparentBackground() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            toolbar.setBackgroundResource(android.R.color.transparent);
        }
    }

    /**
     * 設定當前一頁面是要用哪個圖示
     */
    public void changeBackBarAction() {
        int backStack = getSupportFragmentManager().getBackStackEntryCount();
        if(backStack != 0) {
            changeHeadBackClose(false);
        } else {
            changeHeadBackClose(true);
        }
    }

    /**
     * 變更左邊navigation的圖示
     * @param iconResId 圖示的resource id
     */
    public void setLeftIcon(int iconResId) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(iconResId);
        }
    }

    public void setHeadHide(boolean isHide) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView statusBar = (ImageView) findViewById(R.id.custom_status_bar);
        if(isHide) {
            statusBar.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        } else {
            statusBar.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    public void setStatusBarShow(boolean isShow) {
        ImageView statusBar = (ImageView) findViewById(R.id.custom_status_bar);
        if(!isShow) {
            statusBar.setVisibility(View.GONE);
        } else {
            statusBar.setVisibility(View.VISIBLE);
        }
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
//
//    public int getActionBarHeight() {
//        int actionBarHeight = 0;
//        TypedValue tv = new TypedValue();
//        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
//        {
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
//        }
//
//        return actionBarHeight;
//    }
    /**
     * 顯示只有一個按鈕的提示對話框
     * @param message 要顯示的訊息
     * @param btnResId 按鈕文字資源id
     * @param btnClickListener 按鈕點擊事件listener
     * @param cancelable 點擊背景是否可取消
     */
    public void showAlertDialog(String message, int btnResId, DialogInterface.OnClickListener btnClickListener, boolean cancelable){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
               .setTitle(R.string.dialog_title)
               .setCancelable(cancelable);

        builder.setPositiveButton(btnResId, btnClickListener);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 顯示只有一個按鈕的提示對話框
     * @param message 要顯示的訊息
     * @param positiveBtnResId positive按鈕文字資源id
     * @param negativeBtnResId negative按鈕文字資源id
     * @param positiveBtnClickListener positive按鈕點擊事件listener
     * @param negativeBtnClickListener negative按鈕點擊事件listener
     * @param cancelable 點擊背景是否可取消
     */
    public void showAlertDialog(String message, int positiveBtnResId, int negativeBtnResId, DialogInterface.OnClickListener positiveBtnClickListener, DialogInterface.OnClickListener negativeBtnClickListener, boolean cancelable){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(R.string.dialog_title)
                .setCancelable(cancelable);

        // On devices prior to Honeycomb, the button order (left to right) was POSITIVE - NEUTRAL - NEGATIVE.
        // On newer devices using the Holo theme, the button order (left to right) is now NEGATIVE - NEUTRAL - POSITIVE.
        builder.setPositiveButton(positiveBtnResId, positiveBtnClickListener);
        builder.setNegativeButton(negativeBtnResId, negativeBtnClickListener);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 顯示只有一個按鈕的提示對話框
     * @param title 要顯示的標題
     * @param message 要顯示的訊息
     * @param positiveBtnResId positive按鈕文字資源id
     * @param negativeBtnResId negative按鈕文字資源id
     * @param positiveBtnClickListener positive按鈕點擊事件listener
     * @param negativeBtnClickListener negative按鈕點擊事件listener
     * @param cancelable 點擊背景是否可取消
     */
    public void showAlertDialog(String title, String message, int positiveBtnResId, int negativeBtnResId, DialogInterface.OnClickListener positiveBtnClickListener, DialogInterface.OnClickListener negativeBtnClickListener, boolean cancelable){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(cancelable);

        // On devices prior to Honeycomb, the button order (left to right) was POSITIVE - NEUTRAL - NEGATIVE.
        // On newer devices using the Holo theme, the button order (left to right) is now NEGATIVE - NEUTRAL - POSITIVE.
        builder.setPositiveButton(positiveBtnResId, positiveBtnClickListener);
        builder.setNegativeButton(negativeBtnResId, negativeBtnClickListener);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 顯示只有一個按鈕的提示對話框
     * @param title 要顯示的標題
     * @param message 要顯示的訊息
     * @param positiveBtnResId positive按鈕文字資源id
     * @param positiveBtnClickListener positive按鈕點擊事件listener
     * @param cancelable 點擊背景是否可取消
     */
    public void showAlertDialog(String title, String message, int positiveBtnResId, DialogInterface.OnClickListener positiveBtnClickListener, boolean cancelable){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(cancelable);

        // On devices prior to Honeycomb, the button order (left to right) was POSITIVE - NEUTRAL - NEGATIVE.
        // On newer devices using the Holo theme, the button order (left to right) is now NEGATIVE - NEUTRAL - POSITIVE.
        builder.setPositiveButton(positiveBtnResId, positiveBtnClickListener);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * 收到需強迫更新的error code，按鈕寫死開google play的連結
     */
    public void showForceUpdate(String msg){
        showAlertDialog("", msg, R.string.dialog_app_update_button_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse(GlobalConst.APP_UPDATE_URL);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                if(browserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(browserIntent);
                    finish();
                }
            }
        }, false);
    }

    /**
     * 收到服務暫停的error code，按下按鈕關閉對話框
     */
    public void showServiceStop(String msg){
        showAlertDialog("", msg, android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, false);
    }

    /**
     * 顯示提示對話框
     * @param message 傳回來的訊息
     */
    public void showAlertDialog(String message){
        showAlertDialog(message,
                R.string.button_confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, false);
    }


    /**
     * 顯示客製的Card Detail對話框
     */
    public void showCardDetailAlertDialog(final OnCardDetailListener onCardDetailListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        View view = inflater.inflate(R.layout.dialog_card_detail, null);

        builder.setView(view);

        // 3. Get the AlertDialog from create()
        final AlertDialog dialog = builder.create();
        ImageButton btnCloseDialog = (ImageButton)view.findViewById(R.id.btnCloseDialog);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardDetailListener.OnClose(dialog);
            }
        });
        Button btnMoreDetail = (Button)view.findViewById(R.id.btnMoreDetail);
        btnMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardDetailListener.OnMoreDetail(dialog);
            }
        });

        dialog.show();

    }


    /**
     * 隱藏畫面上的鍵盤
     */
    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 如果鍵盤正顯示在畫面上，隱藏鍵盤
        if(imm.isAcceptingText()) {
            if(getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }


    public boolean isCurrentFragment(String fragmentTag){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        return fragment != null && fragment.isVisible();
    }




//
//    private void checkIsRT() {
//
//        if(this.getClass() == SplashActivity.class) {
//            return;
//        }
//
//        if(sharedMethods.isRTed() == 100) {
//
//            if(!WalletApplication.isRootLock) {
//                WalletApplication.isRootLock = true;
//                sendRTDeviceLog();
//            }
//
//            showAlertDialog(getString(R.string.alert_is_root_message), R.string.button_confirm, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                }
//            },false);
//
//        }
//    }
//
//
//    private void sendRTDeviceLog() {
//        EventAnalyticsUtil.addSpecialEvent(this, new SpecialEvent(SpecialEvent.TYPE_ROOT, "DeviceID: "+PreferenceUtil.getDeviceID(this)));
//        EventAnalyticsUtil.setSpecialEventsListener(new EventAnalyticsUtil.SpecialEventsListener() {
//            @Override
//            public void OnUploadSpecialEventsEnd() {
//                //未登入狀態不再執行登出
//                if (!TextUtils.isEmpty(PreferenceUtil.getNickname(ActivityBase.this))) {
//                    try {
//                        GeneralHttpUtil.logoutWallet(null, ActivityBase.this);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Intent intent = new Intent(ActivityBase.this, LoginActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                PreferenceUtil.clearAllPreferences(ActivityBase.this);
//            }
//        });
//
//        EventAnalyticsUtil.uploadSpecialEvents(this); // 不管是否有登入都執行，若未登入server會有留filelog
//    }


    private void setTimeoutNotification() {

        // 取得定時呼叫管理器
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 封裝訊息
        Intent notificationIntent = new Intent(this, TimeoutReceiver.class);
        notificationIntent.putExtra("title", "xxx");
        notificationIntent.putExtra("content", "ddd");
//        notificationIntent.putExtra("iconSmall",);
        notificationIntent.putExtra("requestCode", 0);
        // 整理定時需要的資訊
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 設定時間
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5 );
        // 加入定時
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);

    }


    //region start GPS
    private void openGPS() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        // 如果已經有權限
        if (!PermissionUtil.needGrantRuntimePermission(this, permissions,
                PermissionUtil.PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION)) {

            OmniApplication.locationServiceInitial();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // 砍光已經有的圖檔目錄
                openGPS();
            }
        }
    }

    @Override
    public void onRequestPermissionsResultForAndroid5(int requestCode) {
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION) {
            // 有權限存取
                // 砍光已經有的圖檔目錄
                openGPS();
        }
    }


    //endregion

//region OTP & api
    /**
     * 顯示客製的OTP對話框
     */
    public void showOTPAlertDialog(OnOTPListener listener) {
        if (PreferenceUtil.getIsLogin(this)){
            showOTPAlertDialog(true, "", "", listener);
        } else {
            showOTPAlertDialog(false, "", "", listener);
        }
    }

    public void showOTPAlertDialog(final boolean isLogin, final String txID, final String extension, OnOTPListener listener){

        onOTPListener = listener;
        sendOTPRequest(isLogin, txID, extension, "");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        otpView = inflater.inflate(R.layout.dialog_otp, null);

        builder.setView(otpView);

        // 3. Get the AlertDialog from create()
        mOTPDialog = builder.create();

        textCountdownTime = (TextView) otpView.findViewById(R.id.text_countdown_time);
        final EditText editOTP = (EditText) otpView.findViewById(R.id.edtOtp);
        final TextView txtOtpTitle= (TextView) otpView.findViewById(R.id.txtOtpTitle);
        final TextView txtNoteStart = (TextView) otpView.findViewById(R.id.txtNoteStart);
        final TextView txtNoteEnd= (TextView) otpView.findViewById(R.id.txtNoteEnd);
        final LinearLayout linearLayout_Note= (LinearLayout) otpView.findViewById(R.id.linearLayout_Note);

        if(NoteUtil.otpNoteData == null){
            //get OTP note
            NoteUtil.callGetNotice(NoteUtil.ENUM_NOTE_TYPE.NOTETYPE_OTP, false, ActivityBase.this, new NoteUtil.OnNoteListener() {
                @Override
                public void OnFinish() {
                    setNoteForOTP(txtNoteStart, txtNoteEnd, linearLayout_Note);
                }
                @Override
                public void OnFail() {
                }
            });
        }else {
            setNoteForOTP(txtNoteStart, txtNoteEnd, linearLayout_Note);
        }

        ImageButton btnCloseDialog = (ImageButton)otpView.findViewById(R.id.btnCloseDialog);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOTPListener.OnClose(mOTPDialog);
            }
        });
        btnOTPResend = (Button) otpView.findViewById(R.id.btnResend);
        btnOTPResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTPRequest(isLogin, txID, extension, "");
                editOTP.setText("");
                onOTPListener.OnResend(mOTPDialog);
            }
        });

        Button btnEnter = (Button)otpView.findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerifyOTP(isLogin, txID, editOTP.getText().toString(), mOpaque);
            }
        });

//        mOTPDialog.show();

    }

    private void setNoteForOTP(TextView start, TextView End, LinearLayout noteList){
        start.setText(NoteUtil.otpNoteData.getConStart());
        End.setText(NoteUtil.otpNoteData.getConEnd());

        for(NoteListDetail data : NoteUtil.otpNoteData.getNoteList()){
            addItemViewForOTP(noteList, data.getContent());
        }
    }

    private void addItemViewForOTP(LinearLayout linearLayout, String strContent) {

        SymbolItem symbolItem = new SymbolItem(this);

        //設定text
        symbolItem.setTextContent(strContent);

        linearLayout.addView(symbolItem);
    }

    /**
     * 顯示客製的OTP對話框(Email)
     */
    public void showOTPAlertDialogEmail(final boolean isLogin, final String txID, final String email, OnOTPListener listener){

        onOTPListener = listener;
        sendOTPRequest(isLogin, txID, "", email);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        otpView = inflater.inflate(R.layout.dialog_otp, null);

        builder.setView(otpView);

        // 3. Get the AlertDialog from create()
        mOTPDialog = builder.create();

        textCountdownTime = (TextView) otpView.findViewById(R.id.text_countdown_time);
        final EditText editOTP = (EditText) otpView.findViewById(R.id.edtOtp);
        final TextView txtOtpTitle= (TextView) otpView.findViewById(R.id.txtOtpTitle);
        final TextView txtNoteStart = (TextView) otpView.findViewById(R.id.txtNoteStart);
        final TextView txtNoteEnd= (TextView) otpView.findViewById(R.id.txtNoteEnd);
        final LinearLayout linearLayout_Note= (LinearLayout) otpView.findViewById(R.id.linearLayout_Note);

        if(NoteUtil.otpNoteData == null){
            //get OTP note
            NoteUtil.callGetNotice(NoteUtil.ENUM_NOTE_TYPE.NOTETYPE_OTP_EMAIL, false, ActivityBase.this, new NoteUtil.OnNoteListener() {
                @Override
                public void OnFinish() {
                    setNoteForOTP(txtNoteStart, txtNoteEnd, linearLayout_Note);
                }
                @Override
                public void OnFail() {
                }
            });
        }else {
            setNoteForOTP(txtNoteStart, txtNoteEnd, linearLayout_Note);
        }

        ImageButton btnCloseDialog = (ImageButton)otpView.findViewById(R.id.btnCloseDialog);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOTPListener.OnClose(mOTPDialog);
            }
        });
        btnOTPResend = (Button) otpView.findViewById(R.id.btnResend);
        btnOTPResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTPRequest(isLogin, txID, "", email);
                editOTP.setText("");
                onOTPListener.OnResend(mOTPDialog);
            }
        });

        Button btnEnter = (Button)otpView.findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerifyOTP(isLogin, txID, editOTP.getText().toString(), mOpaque);
            }
        });



    }

    private void updateOTPAlter(String opaque, String phoneNumber) {
        if(otpView == null)
            return;

        TextView txtOtpTitle= (TextView) otpView.findViewById(R.id.txtOtpTitle);
        txtOtpTitle.setText(opaque.substring(0,4));

        TextView txtPhoneNum= (TextView) otpView.findViewById(R.id.txtToWhere);
        txtPhoneNum.setText(phoneNumber);

        CreateCountDownTimer();
    }

    private void CreateCountDownTimer()
    {
        if(countdownTimer == null) {
            // TODO refactor
            countdownTimer = new CountDownTimer(TEN_MINUTES, ONE_SECOND) {

                public void onTick(long millisUntilFinished) {
                    millisUntilFinished -= ONE_SECOND;
                    String str = String.format("%ss", String.valueOf(millisUntilFinished/1000));
                    textCountdownTime.setText(str);
                    textCountdownTime.setVisibility(View.VISIBLE);
                    btnOTPResend.setEnabled(false);
                }

                @Override
                public void onFinish() {
                    btnOTPResend.setEnabled(true);
                }

            };
        }
        countdownTimer.start();
    }

    private void sendOTPRequest(boolean isLogin, String txID, String extension, String email) {
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

                GeneralHttpUtil.sendOTPRequest(isLogin, txID, extension, email, responseListener_otp, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_otp = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                mOpaque = GeneralResponseBodyUtil.getOpaque(result.getBody());
                mPhoneNumber = GeneralResponseBodyUtil.getPhoneNumber(result.getBody());

                mOTPDialog.show();
                updateOTPAlter(mOpaque, mPhoneNumber);
            } else {
                if(!handleCommonError(result, ActivityBase.this)) {
                    if(returnCode.equals(ResponseResult.RESULT_OTP_CLOSE)) {
                        onOTPListener.OnSuccess(mOTPDialog);
                    } else {

                        showAlertDialog(result.getReturnMessage(), android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                mOTPDialog.dismiss();
                            }
                        }, true);
                    }
                }


            }
        }
    };



    private void sendVerifyOTP(boolean isLogin, String txID, String otp, String opaque) {
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

                GeneralHttpUtil.sendVerifyOTP(isLogin, txID, otp, opaque, responseListener_verifyOTP, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_verifyOTP = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                onOTPListener.OnSuccess(mOTPDialog);
            } else {

                if(!handleCommonError(result, ActivityBase.this)) {

                    showAlertDialog(result.getReturnMessage(), android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, true);

                    onOTPListener.OnFail(mOTPDialog);
                }
            }
        }
    };
//endregion
}