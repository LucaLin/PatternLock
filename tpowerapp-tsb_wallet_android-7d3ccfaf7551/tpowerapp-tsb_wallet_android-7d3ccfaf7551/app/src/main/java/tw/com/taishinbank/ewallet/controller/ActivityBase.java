package tw.com.taishinbank.ewallet.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.BuildConfig;
import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.model.AppUpdateInfo;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class ActivityBase extends AppCompatActivity {

    private ProgressDialog progressDialog;

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
        setCenterTitle(title, true);
    }

    public void setCenterTitle(String title, boolean overrideActionBar){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            TextView textViewTitle = (TextView) toolbar.findViewById(R.id.custom_title);
            textViewTitle.setText(title);
            // TODO 邏輯需修改
            if(overrideActionBar) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_header_back);
            }else{
                setupDrawerToggle();
            }
        }
    }


    /**
     * 設置drawer與toggle
     */
    public void setupDrawerToggle(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

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
     * 變更左邊navigation的圖示
     * @param iconResId 圖示的resource id
     */
    public void setLeftIcon(int iconResId) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(iconResId);
        }
    }


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
     * 顯示需要重新登入的提示對話框
     * @param message server傳回來的訊息
     */
    public void showAlertDialogAndReset(String message, final Context context){
        showAlertDialog(message,
                R.string.reset_login,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceUtil.clearAllPreferences(context);
                        dialog.dismiss();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }, false);
    }

    /**
     * 導回首頁提示對話框
     * @param message server傳回來的訊息
     */
    public void showAlertDialogAndToHome(String message, final Context context){
        showAlertDialog(message,
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }, false);
    }

    public void showAlertIfNeedUpdate(final AppUpdateInfo appUpdateInfo){
        if(appUpdateInfo != null){
            String currentVer = BuildConfig.VERSION_NAME;
            String msg = String.format(getString(R.string.dialog_app_update_msg), appUpdateInfo.getAppVersion(),
                    (appUpdateInfo.getAppDesc() == null) ? "" : appUpdateInfo.getAppDesc());
            // 如果server回傳的url為空，就用預設的網址
            if(TextUtils.isEmpty(appUpdateInfo.getAppUrl())){
                appUpdateInfo.setAppUrl(GlobalConst.APP_UPDATE_URL);
            // 如果url開頭不是網址，寫死自動補給他
            }else if(!(appUpdateInfo.getAppUrl().startsWith("http://") || appUpdateInfo.getAppUrl().startsWith("https://"))){
                appUpdateInfo.setAppUrl("http://".concat(appUpdateInfo.getAppUrl()));
            }
            final Uri uri = Uri.parse(appUpdateInfo.getAppUrl());

            // 目前版本 < 強制更新版本 => 強制更新
            if(currentVer.compareTo(appUpdateInfo.getForceAppVersion()) < 0){
                showAlertDialog("", msg, R.string.dialog_app_update_button_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (uri != null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                            if (browserIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(browserIntent);
                                finish();
                            }
                        }
                    }
                }, false);
            }else {
                // 目前版本 < 最新版本 => 建議更新
                if (currentVer.compareTo(appUpdateInfo.getAppVersion()) < 0) {
                    showAlertDialog("", msg, R.string.dialog_app_update_button_update, android.R.string.cancel,
                            // 按下更新
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(uri != null){
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                                        if(browserIntent.resolveActivity(getPackageManager()) != null) {
                                            PreferenceUtil.setLastShowUpdate(getApplicationContext());
                                            startActivity(browserIntent);
                                            dialog.dismiss();
                                        }
                                    }
                                }
                            },
                            // 按下取消
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PreferenceUtil.setLastShowUpdate(getApplicationContext());
                                    dialog.dismiss();
                                }
                            }, false);
                // 不需更新
                }else{
                    PreferenceUtil.setLastShowUpdate(getApplicationContext());
                }
            }
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 讓所有子activity都固定為直向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
}