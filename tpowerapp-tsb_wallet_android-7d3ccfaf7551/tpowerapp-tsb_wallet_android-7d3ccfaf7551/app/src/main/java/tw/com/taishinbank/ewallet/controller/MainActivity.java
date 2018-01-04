package tw.com.taishinbank.ewallet.controller;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.creditcard.CreditCardFragment;
import tw.com.taishinbank.ewallet.controller.extra.EarnActivity;
import tw.com.taishinbank.ewallet.controller.extra.ExtraHomeFragment;
import tw.com.taishinbank.ewallet.controller.extra.MyCouponActivity;
import tw.com.taishinbank.ewallet.controller.extra.MyTicketActivity;
import tw.com.taishinbank.ewallet.controller.extra.MyTicketFragment;
import tw.com.taishinbank.ewallet.controller.red.MyRedEnvelopeFragment;
import tw.com.taishinbank.ewallet.controller.red.RedEnvelopeFragment;
import tw.com.taishinbank.ewallet.controller.setting.ApplicationSettedActivity;
import tw.com.taishinbank.ewallet.controller.setting.EditPersonalInfoActivity;
import tw.com.taishinbank.ewallet.controller.sv.DepositWithdrawActivity;
import tw.com.taishinbank.ewallet.controller.sv.PaymentActivity;
import tw.com.taishinbank.ewallet.controller.sv.SVHomeFragment;
import tw.com.taishinbank.ewallet.controller.sv.TransactionLogFragment;
import tw.com.taishinbank.ewallet.controller.wallethome.HomeFragment;
import tw.com.taishinbank.ewallet.controller.wallethome.MessageCenterFragment;
import tw.com.taishinbank.ewallet.controller.wallethome.PaymentOptionsFragment;
import tw.com.taishinbank.ewallet.controller.wallethome.ReceiptOptionsFragment;
import tw.com.taishinbank.ewallet.controller.wallethome.SystemMessageFragment;
import tw.com.taishinbank.ewallet.gcm.RegistrationIntentService;
import tw.com.taishinbank.ewallet.handler.ExceptionHandler;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.PermissionUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.PushMsgHelper;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;


public class MainActivity extends ActivityBase
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    private static final int RESULT_EDIT_PERSON = 7893;

    private TabHost tabHost;
    public final static String TAB_HOME = "home";
    public final static String TAB_MONEY = "money";
    public final static String TAB_CREDIT = "credit";
    public final static String TAB_GIFT = "gift";

    public final static String EXTRA_GO_MY_RED_ENVELOPE = "extra_go_my_red_envelope";
    public final static String EXTRA_GO_SV_HISTORY = "extra_go_sv_history";
    public final static String EXTRA_GO_COUPON_HISTORY = "extra_go_coupon_history";
    public final static String EXTRA_GO_RED_ENVELOPE = "extra_go_red_envelope";
    public final static String EXTRA_GO_PAY = "extra_go_pay";
    public final static String EXTRA_GO_PAGE_TAG = "extra_go_page_tag";
    public final static String EXTRA_GO_CREDIT_HISTORY = "extra_go_credit_history";
    public final static String EXTRA_GO_TICKET_LIST = "extra_go_ticket_list";

    public final static String EXTRA_BACK_TO_SOURCE_PAGE = "extra_back_to_source_page";
    public final static String EXTRA_DO_ACION_BY_URL = "extra_do_action_by_url";
    public final static String EXTRA_START_PAY_REQUEST = "extra_back_to_source_page";

    private boolean showRedEnvelopeDirectly = false;
    private boolean backToSourcePage = false;

    private ImageLoader imageLoader;
    private View headerView;
    private TextView textName;
    private ImageView imagePhoto;
    // add for push
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Fragment currentFM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(GlobalConst.ENABLE_EXCEPTION_HANDLER) {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        }
        setContentView(R.layout.activity_main);

        //傳送上次使用統計的Hit event //改為每次每次送
//        EventAnalyticsUtil.uploadHitRecordEvents(this);

        // 如果有特定的intent extra資料，開啟我的紅包頁
        if(getIntent() != null && getIntent().hasExtra(EXTRA_GO_MY_RED_ENVELOPE)) {
            // 先設置基本的畫面
            setupViews();
            tabHost.setCurrentTabByTag(TAB_GIFT);

            Fragment fragment = new RedEnvelopeFragment();
            gotoFragment(fragment, false);
            fragment = MyRedEnvelopeFragment.newInstance(getIntent().getIntExtra(MyRedEnvelopeFragment.EXTRA_DEFAULT_TAB, MyRedEnvelopeFragment.TAB_RECEIVED));
            gotoFragment(fragment, false);

        // 如果有特定的intent extra資料，開啟儲值交易記錄
        } else if(getIntent() != null && getIntent().hasExtra(EXTRA_GO_SV_HISTORY)) {
            // 先設置基本的畫面
            setupViews();
            tabHost.setCurrentTabByTag(TAB_MONEY);

            // 顯示紅包類型選擇頁
            Fragment fragment = TransactionLogFragment.newInstance(getIntent().getIntExtra(TransactionLogFragment.EXTRA_SWITCH_TO, TransactionLogFragment.TRX_OUT));
            gotoFragment(fragment, false);

            // 如果有特定的intent extra資料，開啟儲值支付 - 付款
        } else if(getIntent() != null && getIntent().hasExtra(EXTRA_GO_COUPON_HISTORY)) {
            // 先設置基本的畫面
            setupViews();
            tabHost.setCurrentTabByTag(TAB_GIFT);

            Intent intent = new Intent(this, MyCouponActivity.class);
            startActivity(intent);

            // 如果有特定的intent extra資料，開啟儲值支付 - 付款
        } else if(getIntent() != null && getIntent().hasExtra(EXTRA_GO_PAY)) {
            // 先設置基本的畫面
            setupViews();
            tabHost.setCurrentTabByTag(TAB_MONEY);

            //
            Intent intent = new Intent();
            intent.setClass(this, PaymentActivity.class);
            intent.putExtra(PaymentActivity.EXTRA_RECEIVER, getIntent().getParcelableExtra(PaymentActivity.EXTRA_RECEIVER));

            startActivity(intent);

        // 如果有特定的intent extra資料，開啟紅包類型選擇頁
        }else if(getIntent() != null && getIntent().hasExtra(EXTRA_GO_RED_ENVELOPE)) {
            // 取得是否返回前一頁
            backToSourcePage = getIntent().hasExtra(EXTRA_BACK_TO_SOURCE_PAGE);
            // 先設置基本的畫面
            setupViews();
            tabHost.setCurrentTabByTag(TAB_GIFT);

            // 顯示紅包首頁
            Fragment fragment = new RedEnvelopeFragment();
            gotoFragment(fragment, false);

        // 如果有特定的intent extra資料，設定tab
        }else if(getIntent() != null && getIntent().hasExtra(EXTRA_GO_PAGE_TAG)) {
            setupViews();
            String page = getIntent().getStringExtra(EXTRA_GO_PAGE_TAG);
            tabHost.setCurrentTabByTag(page);

        // 如果有推播的資料
        }else if(getIntent() != null && getIntent().hasExtra(EXTRA_DO_ACION_BY_URL)) {
            setupViews();

            String url = getIntent().getStringExtra(EXTRA_DO_ACION_BY_URL);
            PushMsgHelper helper = new PushMsgHelper(this, TAG);
            helper.doActionAccordingUrl(url);

            // 如果有特定的intent extra資料，開啟信用卡交易紀錄
        }else if(getIntent() != null && getIntent().hasExtra(EXTRA_GO_CREDIT_HISTORY)) {
            // 先設置基本的畫面
            setupViews();
            tabHost.setCurrentTabByTag(TAB_CREDIT);

            Fragment fragment = new tw.com.taishinbank.ewallet.controller.creditcard.TransactionLogFragment();
            gotoFragment(fragment, false);

            // 如果有特定的intent extra資料，開啟票券夾
        } else if(getIntent() != null && getIntent().hasExtra(EXTRA_GO_TICKET_LIST)) {
            // 先設置基本的畫面
            setupViews();
            tabHost.setCurrentTabByTag(TAB_GIFT);

            Intent intent = new Intent(this, MyTicketActivity.class);
            if(getIntent().hasExtra(MyTicketFragment.EXTRA_SWITCH_TO)) {
                intent.putExtra(MyTicketFragment.EXTRA_SWITCH_TO, getIntent().getIntExtra(MyTicketFragment.EXTRA_SWITCH_TO, MyTicketFragment.TICKET_UNUSED));
            }

            startActivity(intent);
        } else {//首次登入無任何資料時會從這進入

            // 一開啟app就註冊push以取得token
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }

            // 檢查是否第一次使用
            if (PreferenceUtil.isFirstTimeUse(this)) {
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                // 用是否有nickname來判斷是否登入過
                if (!TextUtils.isEmpty(PreferenceUtil.getNickname(this))) {
                    //傳送特殊Log至server
                    EventAnalyticsUtil.uploadSpecialEvents(this);

                    setupViews();
                    // 如果沒有上傳過
                    if(!PreferenceUtil.hasUploadContacts(this)) {
                        // TODO 暫先不上傳
                        // 先確認是否有讀取聯絡人資料的權限
//                        if (!PermissionUtil.needGrantRuntimePermission(this, Manifest.permission.READ_CONTACTS,
//                                PermissionUtil.PERMISSION_REQUEST_CODE_READ_CONTACTS)) {
//                            // 開始讀取並上傳聯絡人資料，在不同的thread
//                            new UploadContactsAsyncTask(this, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                            // TODO 不管結果先設為上傳過
//                            PreferenceUtil.setHasUploadContacts(this, true);
//                        }
                    }

                    // 先確認是否有讀取外部儲存空間的權限
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (PermissionUtil.needGrantRuntimePermission(this, permissions,
                            PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
                        // DO NOTHING CURRENTLY
                    }

                    // Get Credit Card Data
                    CreditCardUtil.InitGlobalGetCreditCardList(this);

                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null && intent.hasExtra(EXTRA_DO_ACION_BY_URL)) {
            if(tabHost.getCurrentTabTag().equals(TAB_HOME)) {
                tabHost.setCurrentTabByTag(TAB_HOME);
                changeTabContent(TAB_HOME);
            }else{
                tabHost.setCurrentTabByTag(TAB_HOME);
            }
            String url = intent.getStringExtra(EXTRA_DO_ACION_BY_URL);
            PushMsgHelper helper = new PushMsgHelper(this, TAG);
            helper.doActionAccordingUrl(url);
        }else if(intent != null && intent.hasExtra(EXTRA_GO_COUPON_HISTORY)) {
            // 先設置基本的畫面
            tabHost.setCurrentTabByTag(TAB_GIFT);

            intent = new Intent(this, MyCouponActivity.class);
            startActivity(intent);

            // 如果有特定的intent extra資料，開啟儲值支付 - 付款
        }
    }

    /**
     * 畫面元件設定
     */
    private void setupViews(){
        // 設置toolbar與置中的標題文字
        setCenterTitle(R.string.app_name);

        // 設置drawer與toggle
        setupDrawerToggle();

        // 設置navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = getLayoutInflater().inflate(R.layout.nav_header_main, navigationView, false);
        textName = (TextView) headerView.findViewById(R.id.text_name);
        imagePhoto = (ImageView) headerView.findViewById(R.id.imageView);
        updateHeaderInfo();

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditPersonalInfoActivity.class);

                if (intent != null) {
                    // TODO 確認什麼時候要改變這個flag
                    startActivityForResult(intent, RESULT_EDIT_PERSON);
                }
            }
        });

        navigationView.addHeaderView(headerView);

        // 設置tab host
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                changeTabContent(tabId);
            }
        });

        // 加入tab
        addTab(TAB_HOME, getString(R.string.tab_home), R.drawable.ic_home_tab_home);
        addTab(TAB_MONEY, getString(R.string.tab_money), R.drawable.ic_home_tab_deposit_payment);
        addTab(TAB_CREDIT, getString(R.string.tab_credit), R.drawable.ic_home_tab_credit_card);
        addTab(TAB_GIFT, getString(R.string.tab_gift), R.drawable.ic_home_tab_top_up);

        // TODO 當有登入畫面後，改成讀取preference
        if(showRedEnvelopeDirectly){
            // 設定顯示最後一個tab
            tabHost.setCurrentTabByTag(TAB_GIFT);
            // disable其他tab
            tabHost.getTabWidget().getChildAt(0).setEnabled(false);
            tabHost.getTabWidget().getChildAt(1).setEnabled(false);
            tabHost.getTabWidget().getChildAt(2).setEnabled(false);
        }

        if(GlobalConst.DISABLE_CREDIT_CARD){
            tabHost.getTabWidget().getChildAt(2).setEnabled(false);
        }
    }

    private void changeTabContent(String tabId){
        Fragment fragment = null;

        switch (tabId) {
            case TAB_HOME:
                // 如果是直接顯示紅包首頁的版本，不切換fragment
                if (!showRedEnvelopeDirectly) {
                    fragment = new HomeFragment();
                }
                break;
            case TAB_MONEY:
                fragment = new SVHomeFragment();
                break;
            case TAB_CREDIT:
                fragment = new CreditCardFragment();
                break;
            case TAB_GIFT:
                fragment = new ExtraHomeFragment();
                break;
        }

        if (fragment != null) {
            if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(android.R.id.tabcontent, fragment);
            ft.commit();
        }
    }

    private void updateHeaderInfo()
    {
        if(imageLoader == null){
            imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.photo_size));
        }
        // 設定頭像
        imageLoader.loadImage(PreferenceUtil.getMemNO(this), imagePhoto);

        // 設定名稱
        textName.setText(PreferenceUtil.getNickname(this));
    }

    /**
     * 加入tab到tab host
     */
    private void addTab(String tag, String title, int iconResId){
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
    private View createTabItemView(String title, int iconResId){
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
        ImageView imageIcon = (ImageView) view.findViewById(android.R.id.icon);
        imageIcon.setImageResource(iconResId);
        TextView textTitle = (TextView) view.findViewById(android.R.id.title);
        textTitle.setText(title);
        return view;
    }

    @Override
    public void onBackPressed() {

//        if(currentFM != null && currentFM.getClass() == ApplicationSettedActivity.class)
//        {
//            ApplicationSettedActivity fragment = (ApplicationSettedActivity)currentFM;
//            fragment.saveSetting();
//            return;
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        // 如果要返回前一個呼叫頁，結束目前頁面
        } else if(backToSourcePage){
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            // home鍵在此不處理，留給其他各頁的fragment處理
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @return True if the event was handled, false otherwise.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // 儲值/提領，先確認是否須登入
        if (id == R.id.nav_deposit_withdraw) {
            if(((WalletApplication)getApplication()).needSVLogin()){
                // 連到儲值帳戶登入頁
                startActivityForResult(new Intent(this, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            }else {
                startActivity(new Intent(this, DepositWithdrawActivity.class));
            }

        // 儲值帳戶付款，開啟付款主頁，只顯示儲值帳戶選項
        } else if (id == R.id.nav_pay) {
            Fragment paymentOptionsFragment = PaymentOptionsFragment.newInstance(PaymentOptionsFragment.ONLY_SV);
            gotoFragment(paymentOptionsFragment, true);

        // 儲值帳戶收款，開啟收款主頁
        } else if (id == R.id.nav_receivables) {
            Fragment receiptOptionsFragment = new ReceiptOptionsFragment();
            gotoFragment(receiptOptionsFragment, true);

        // 儲值帳戶付款，開啟付款主頁，只顯示信用卡選項
        } else if (id == R.id.nav_only_credit_card) {
            if(GlobalConst.DISABLE_CREDIT_CARD) {
                showAlertDialog(getString(R.string.msg_credit_card_is_disabled));
                return true;
            }
            Fragment paymentOptionsFragment = PaymentOptionsFragment.newInstance(PaymentOptionsFragment.ONLY_CARD);
            gotoFragment(paymentOptionsFragment, true);

        // 賺取優惠，前往輸入優惠碼頁
        } else if (id == R.id.nav_earn_sale) {
            Intent intent = new Intent(this, EarnActivity.class);
            intent.putExtra(EarnActivity.EXTRA_GO_PAGE, EarnActivity.PAGE_EARN_BY_ENTER);
            startActivity(intent);

        // 開啟訊息中心
        } else if (id == R.id.nav_news_feed) {
            Fragment fragment = new MessageCenterFragment();
            gotoFragment(fragment, true);

        // 開啟系統訊息
        } else if (id == R.id.nav_system_message) {
            Fragment systemMessageFragment = new SystemMessageFragment();
            gotoFragment(systemMessageFragment, true);

        // 開啟設定
        } else if (id == R.id.nav_settings) {
//            Fragment fragment = new ApplicationSettedActivity();
//            gotoFragment(fragment, true);
            Intent intent = new Intent(this, ApplicationSettedActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_READ_CONTACTS) {
//            // 有權限存取
//            if (PermissionUtil.verifyPermissions(grantResults)) {
//                // 開始讀取並上傳聯絡人資料
//                new UploadContactsAsyncTask(this, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//            // TODO 沒有權限時要做什麼
//            } else {
//
//                // permission denied, boo! Disable the
//                // functionality that depends on this permission.
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeaderInfo();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

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

    /**
     * 切換到指定的fragment
     */
    private void gotoFragment(Fragment fragment, boolean withAnimation){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(withAnimation) {
            ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        }
        ft.replace(android.R.id.tabcontent, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_EDIT_PERSON)
        {
            imageLoader.removeFromMemCache(PreferenceUtil.getMemNO(this));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // DO NOTHING CURRENTLY
            }
        }
    }

    // add for push
//    private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            SharedPreferences sharedPreferences =
//                    PreferenceManager.getDefaultSharedPreferences(context);
//            boolean sentToken = sharedPreferences
//                    .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
//            if (sentToken) {
//                Toast.makeText(MainActivity.this, R.string.gcm_send_message, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(MainActivity.this, R.string.token_error_message, Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
}
