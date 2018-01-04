package com.dbs.omni.tw.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.bill.BillHomeFragment;
import com.dbs.omni.tw.controller.home.HomeFragment;
import com.dbs.omni.tw.controller.installment.InstallmentHomeFragment;
import com.dbs.omni.tw.controller.login.LoginActivity;
import com.dbs.omni.tw.controller.payment.PaymentHomeFragment;
import com.dbs.omni.tw.controller.setting.PersonlServiceFragment;
import com.dbs.omni.tw.gcm.RegistrationIntentService;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.http.HttpUtilBase;
import com.dbs.omni.tw.util.http.PWebHttpUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActivityBase {

    public final static String TAG = "MainActivity";

    private TabHost tabHost;
    public final static String TAB_HOME = "home";
    public final static String TAB_BILL = "bill";
    public final static String TAB_PAYMENT = "payment";
    public final static String TAB_INSTALLMENT = "installment";
    public final static String TAB_SETTING = "setting";

    public final static String EXTRA_GO_PAGE_TAG = "extra_go_page_tag";
    public final static String EXTRA_LOGIN_DATA = "extra_login_data";

    //gcm push
    public final static String EXTRA_DO_ACION_BY_URL = "extra_do_action_by_url";

    // add for push
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 一開啟app就註冊push以取得token
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


//        AWSPerfomanceUtil.initialise(this);
//        AWSPerfomanceUtil.sendlog(this);

        //Cross App or Web開啟LESPAY APP
        //tid:transitID uid:userCode func:summary, bill, barcode
//        String schema = getIntent().getScheme();
//        if(!TextUtils.isEmpty(schema) && schema.equals("dbsommi")) {
//            if(!crossAppToPage()) {
//                //若 cross app  過程失敗將導回原本流程
//                initMain();
//            }
//        } else {
        testPWEB();
        initMain();
//        }
    }

    private void testPWEB() {
        try {
            PWebHttpUtil.getCreditCardsInfo(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("PWEB", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("PWEB", error.getMessage());
                }
            }, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void initMain() {


        //
        if (TextUtils.isEmpty(HttpUtilBase.sessionID) || !PreferenceUtil.getIsLogin(this)) {
            PreferenceUtil.setIsLogin(this, false);
            gotoLoginPage();

        } else {
//            PreferenceUtil.setIsLogin(this, false);
            setupViews();

            if (getIntent() != null) {
                goToExtra();
            }
//
////            //判斷是否需要去到別的TAB
//            if(getIntent() != null && getIntent().hasExtra(EXTRA_SWITCH)){
//                goExtra();
//            }

        }
    }


    /**
     * 畫面元件設定
     */
    private void setupViews() {

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                changeTabContent(tabId);
            }
        });

        // 加入tab
        addTab(TAB_HOME, getString(R.string.tab_home), R.drawable.ic_home_tab);
        addTab(TAB_BILL, getString(R.string.tab_statement), R.drawable.ic_bill_tab);
        addTab(TAB_PAYMENT, getString(R.string.tab_payment), R.drawable.ic_payment_tab);
        addTab(TAB_INSTALLMENT, getString(R.string.tab_installment), R.drawable.ic_instalment_tab);
        addTab(TAB_SETTING, getString(R.string.tab_setting), R.drawable.ic_setting_tab);
//        changeTabContent(TAB_HOME);
    }


//region ActionBar and TabBar

    private void changeTabContent(String tabId) {
        Fragment fragment = null;

        switch (tabId) {
            case TAB_HOME:
                // 如果是直接顯示紅包首頁的版本，不切換fragment
                fragment = new HomeFragment();
                break;
            case TAB_BILL:
                fragment = new BillHomeFragment();
                break;
            case TAB_PAYMENT:
                fragment = new PaymentHomeFragment();
                break;
            case TAB_INSTALLMENT:
                fragment = new InstallmentHomeFragment();
                break;
            case TAB_SETTING:
                fragment = new PersonlServiceFragment();
//                ((PersonlServiceFragment) fragment).setOnLogoutListener(new PersonlServiceFragment.OnLogoutListener() {
//                    @Override
//                    public void OnLogout() {
//                        gotoLoginPage();
//                    }
//                });
                break;
        }
//
        if (fragment != null) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(android.R.id.tabcontent, fragment);
            ft.commit();


        }
    }

    /**
     * 加入tab到tab host
     */
    private void addTab(String tag, String title, int iconResId) {
        TabHost.TabSpec spec = tabHost.newTabSpec(tag)
                .setIndicator(createTabItemView(title, iconResId))
                .setContent(new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String tag) {
                        return findViewById(android.R.id.tabcontent);
                    }
                });
        tabHost.addTab(spec);
    }

    /**
     * 建立tab項目的view
     */
    private View createTabItemView(String title, int iconResId) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
        ImageView imageIcon = (ImageView) view.findViewById(android.R.id.icon);
        imageIcon.setImageResource(iconResId);
        TextView textTitle = (TextView) view.findViewById(android.R.id.title);
        textTitle.setText(title);
        return view;
    }

//endregion

//region Go to pager function

    private void gotoLoginPage() {
        PreferenceUtil.setIsLogin(this, false);
        Intent intent = new Intent(this, LoginActivity.class);

        String schema = getIntent().getScheme();
        if(!TextUtils.isEmpty(schema) && schema.equals("dbsommi")) {
            intent.setData(getIntent().getData());
        }

        startActivity(intent);
        finish();
    }

// endregion

    //region 自動倒頁
    private void goToExtra() {
        if (getIntent().hasExtra(EXTRA_GO_PAGE_TAG)) {
            String page = getIntent().getStringExtra(EXTRA_GO_PAGE_TAG);
            tabHost.setCurrentTabByTag(page);
        }
    }
//endregion


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}