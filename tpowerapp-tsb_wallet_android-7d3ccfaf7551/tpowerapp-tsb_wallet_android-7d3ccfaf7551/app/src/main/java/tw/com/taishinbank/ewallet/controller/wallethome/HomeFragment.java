package tw.com.taishinbank.ewallet.controller.wallethome;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.wallethome.WalletHomeAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.ContactListActivity;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.controller.red.MyRedEnvelopeFragment;
import tw.com.taishinbank.ewallet.controller.sv.SVAccountDetailActivity;
import tw.com.taishinbank.ewallet.controller.sv.TransactionLogFragment;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.interfaces.SystemMessageType;
import tw.com.taishinbank.ewallet.model.AppUpdateInfo;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.wallethome.WalletHomeCount;
import tw.com.taishinbank.ewallet.model.wallethome.WalletHomePushMsg;
import tw.com.taishinbank.ewallet.model.wallethome.WalletSystemMsg;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.PushMsgHelper;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;


public class HomeFragment extends Fragment implements View.OnClickListener, WalletHomeAdapter.OnItemClickedListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton buttonLocker;
    private TextView textBalance;
    private SVAccountInfo svAccountInfo;
    private ImageView imagePhoto;
    private ArrayList<WalletHomeCount> countList = new ArrayList<>();
    private ArrayList<WalletHomePushMsg> pushMsgList = new ArrayList<>();
    private HashMap<String, CountViewHolder> countMap = new HashMap<>();
    private WalletHomeAdapter adapter;
    private boolean moreFlag = false;

    private ImageLoader imageLoader;
    private ImageLoader imageLoaderForList;

    // -------
    // Request Code
    // -------
    public static final int REQUEST_LOGIN_SV_GO_MY_RED = SVLoginActivity.REQUEST_LOGIN_SV + 1;
    public static final int REQUEST_LOGIN_SV_GO_TRANSACTION_LOG_OUT = REQUEST_LOGIN_SV_GO_MY_RED + 1;
    public static final int REQUEST_LOGIN_SV_GO_TRANSACTION_LOG_IN = REQUEST_LOGIN_SV_GO_TRANSACTION_LOG_OUT + 1;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 建立中間的文字（可能是按鈕）
        View customButton = view.findViewById(R.id.btn_red_envelope);
        setCustomButton(customButton, R.string.home_tab_red_envelope, WalletHomeCount.CountType.RED_ENVELOPES);
        customButton = view.findViewById(R.id.btn_paid);
        setCustomButton(customButton, R.string.home_tab_paid, WalletHomeCount.CountType.PAY_REQUESTS);
        customButton = view.findViewById(R.id.btn_received);
        setCustomButton(customButton, R.string.home_tab_received, WalletHomeCount.CountType.INCOMES);
        customButton = view.findViewById(R.id.btn_instrument);
        setCustomButton(customButton, R.string.home_tab_instrument, WalletHomeCount.CountType.RECEIVED_COUPON);

        Button buttonPay = (Button) view.findViewById(R.id.btn_pay);
        buttonPay.setOnClickListener(this);

        Button buttonReceive = (Button) view.findViewById(R.id.btn_receive);
        buttonReceive.setOnClickListener(this);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);

        buttonLocker = (ImageButton) view.findViewById(R.id.button_locker);
        buttonLocker.setOnClickListener(this);
        ImageButton buttonSVInfo = (ImageButton) view.findViewById(R.id.button_sv_info);
        buttonSVInfo.setOnClickListener(this);

        textBalance = (TextView) view.findViewById(R.id.text_balance);
        imagePhoto = (ImageView) view.findViewById(R.id.image_photo);

//        // 設定頭像
//        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.photo_size));
//        imageLoader.loadImage(PreferenceUtil.getMemNO(this.getActivity()), imagePhoto);

        updateCountText(countList);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 停止refresh的動畫
                swipeRefreshLayout.setRefreshing(false);
                // 如果沒有網路連線，顯示提示對話框
                if (!NetworkUtil.isConnected(getActivity())) {
                    ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, true);
                    return;
                }

                getWalletHomeList();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageLoaderForList = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        // 設定adapter
        adapter = new WalletHomeAdapter(getActivity(), imageLoaderForList);
        adapter.setOnItemClickedListener(this);
        adapter.setOnMoreClickedListener(new WalletHomeAdapter.OnMoreClickedListener() {
            @Override
            public void onClicked() {
                Fragment fragment = new MessageCenterFragment();
                gotoFragment(fragment, true);
            }
        });
        recyclerView.setAdapter(adapter);
        // 設定layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // Pause image loader to ensure smoother scrolling when flinging
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    imageLoaderForList.setPauseWork(true);
                } else {
                    imageLoaderForList.setPauseWork(false);
                }
            }
        });
        // TODO 加入動態訊息中心的按鈕
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(getString(R.string.app_name), false);
        activityBase.getWindow().setBackgroundDrawableResource(R.drawable.bg_fragment_wallet_home);

        checkPickImage();

        // 更新帳戶資訊如果登入過儲值帳戶且儲值帳戶登入時間還沒過期，隱藏鎖頭顯示餘額
        updateAccountInfo();

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(activityBase)){
            activityBase.showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        // For財神紅包版直接更新至有儲值跟優惠券版，
        // 如果沒有取過個人資料就背景呼叫api取得個人資料。
        if(!PreferenceUtil.hasLoadPersonalData(activityBase)) {
            // 呼叫api偷偷查詢個人資料
            try {
                GeneralHttpUtil.queryPersonalData(responseListenerPersonaldata, activityBase, TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 呼叫web service取得儲值帳戶資訊
        try {
            RedEnvelopeHttpUtil.getSVAccountInfo(responseListenerSV, activityBase, TAG);
            activityBase.showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private void setCustomButton(View view, int titleResId, String type){
        CountViewHolder viewHolder = new CountViewHolder(view, titleResId);
        countMap.put(type, viewHolder);
        view.setOnClickListener(this);
    }

    private void updateCountText(List<WalletHomeCount> countList){
        if(countList != null) {
            for (WalletHomeCount item : countList) {
                CountViewHolder viewHolder = countMap.get(item.getType());
                viewHolder.setNumber(item.getCount());
                viewHolder.setShowHasNew(item.hasNew());
            }
        }
    }

    private void checkPickImage()
    {
        if(imageLoader == null){
            imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.photo_size));
        }
        // 設定頭像
        imageLoader.loadImage(PreferenceUtil.getMemNO(this.getActivity()), imagePhoto);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            /* 上方快速按鈕 */
            case R.id.btn_pay:
                // 開啟付款主頁
                Fragment paymentOptionsFragment = new PaymentOptionsFragment();
                gotoFragment(paymentOptionsFragment, true);
                break;
            case R.id.btn_receive:
                // 開啟收款主頁
                Fragment receiptOptionsFragment = new ReceiptOptionsFragment();
                gotoFragment(receiptOptionsFragment, true);
                break;

            /* 鎖頭按鈕 */
            case R.id.button_locker:
                // 連到儲值帳戶登入頁
                startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                break;

            /* 動態提示 */
            // 本日紅包，進入加值服務-我的紅包（須登入）
            case R.id.btn_red_envelope:
                if(!needSVLogin()) {
                    gotoFragment(new MyRedEnvelopeFragment(), true);
                }else{
                    // 否則連到儲值帳戶登入頁
                    startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), REQUEST_LOGIN_SV_GO_MY_RED);
                }
                break;

            // 待付款項，進入儲值帳戶-帳戶紀錄（tab：帳戶支出）（須登入）
            case R.id.btn_paid:
                if(!needSVLogin()) {
                    gotoFragment(TransactionLogFragment.newInstance(TransactionLogFragment.TRX_OUT), true);
                }else{
                    // 否則連到儲值帳戶登入頁
                    startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), REQUEST_LOGIN_SV_GO_TRANSACTION_LOG_OUT);
                }
                break;

            // 本月收款，進入儲值帳戶-帳戶紀錄（tab：帳戶收入）（須登入）
            case R.id.btn_received:
                if(!needSVLogin()) {
                    gotoFragment(TransactionLogFragment.newInstance(TransactionLogFragment.TRX_IN), true);
                }else{
                    // 否則連到儲值帳戶登入頁
                    startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), REQUEST_LOGIN_SV_GO_TRANSACTION_LOG_IN);
                }
                break;

            // 優惠券，加值服務-優惠券
            case R.id.btn_instrument:
               // startActivity(new Intent(getActivity(), MyCouponActivity.class));
                Intent intent = new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_GO_COUPON_HISTORY, "");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;

            // 如果是帳戶資訊按鈕, 開啟帳戶詳情頁
            case R.id.button_sv_info:
                // 如果需要登入儲值
                if(needSVLogin()) {
                    // 連到儲值帳戶登入頁
                    startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                    return ;
                }
                startActivity(new Intent(getActivity(), SVAccountDetailActivity.class));
                break;
        }
    }

    /**
     * 切換到指定的fragment
     */
    private void gotoFragment(Fragment fragment, boolean withAnimation){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(withAnimation) {
            ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        }
        ft.replace(android.R.id.tabcontent, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @SuppressWarnings( "deprecation" )
    private void updateAccountInfo() {
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
        if(svAccountInfo != null && !isSVLoginTimeExpired()) {
            buttonLocker.setVisibility(View.GONE);
            textBalance.setVisibility(View.VISIBLE);
            String formattedBalance = FormatUtil.toDecimalFormatFromString(svAccountInfo.getBalance(), true);
            textBalance.setText(formattedBalance);
        }else{
            buttonLocker.setVisibility(View.VISIBLE);
            textBalance.setVisibility(View.GONE);
        }
    }

    /**
     * 回傳距離上次登入儲值帳戶是否超過指定時間（10分鐘）
     */
    private boolean isSVLoginTimeExpired(){
        String svLoginTime = PreferenceUtil.getSVLoginTime(getActivity());
        if(!TextUtils.isEmpty(svLoginTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                // 將上次登入時間字串轉成date物件
                Date lastLoginTime = sdf.parse(svLoginTime);

                // 取得現在時間
                Calendar c = Calendar.getInstance();
                // 判斷是否已經超過上次登入時間10分鐘
                c.add(Calendar.MINUTE, -10);
                if(lastLoginTime.after(c.getTime())){
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    // 呼叫取得儲值帳戶資訊api的listener
    private ResponseListener responseListenerSV = new ResponseListener() {


        @Override
        @SuppressWarnings("all")
        public void onResponse(ResponseResult result) {
            // TODO 預防切換tab時，切換前所用的fragment莫名會進入onResume（待釐清原因）
            if(getActivity() == null) {
                return;
            }
            ((ActivityBase) getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 儲值帳戶資訊存sharedpreference
                PreferenceUtil.setSVAccountInfo(getActivity(), result.getBody().toString());
                updateAccountInfo();

                // 跟iOS同步，如果是token或儲值帳戶的svToken過期，變更為鎖頭
            } else if(returnCode.equals(ResponseResult.RESULT_TOKEN_EXPIRED)
                    || returnCode.equals(ResponseResult.RESULT_SV_TOKEN_EXPIRED)){
                PreferenceUtil.setSVAccountInfo(getActivity(), null);
                updateAccountInfo();

            } else {
                // TODO 不成功的判斷與處理
            }

            getWalletHomeList();
        }
    };

    private void getWalletHomeList(){
        // 呼叫web service api 取得首頁動態訊息列表
        try {
            GeneralHttpUtil.getWalletHomeListData(responseListener, getActivity(), TAG);
            ((ActivityBase)getActivity()).showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    private boolean needSVLogin(){
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
        return !(svAccountInfo != null && !isSVLoginTimeExpired());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 判斷是否登入成功
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case REQUEST_LOGIN_SV_GO_MY_RED:
                    gotoFragment(new MyRedEnvelopeFragment(), true);
                    break;
                case REQUEST_LOGIN_SV_GO_TRANSACTION_LOG_OUT:
                    gotoFragment(TransactionLogFragment.newInstance(TransactionLogFragment.TRX_OUT), true);
                    break;
                case REQUEST_LOGIN_SV_GO_TRANSACTION_LOG_IN:
                    gotoFragment(TransactionLogFragment.newInstance(TransactionLogFragment.TRX_IN), true);
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // TODO 預防快速切換tab時progressLoading沒關
        if(getActivity() != null) {
            ((ActivityBase)getActivity()).dismissProgressLoading();
        }
    }

    // 呼叫取得首頁列表api的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 成功的話，更新畫面列表
                countList = GeneralResponseBodyUtil.getCountList(result.getBody());
                pushMsgList = GeneralResponseBodyUtil.getPushMsgList(result.getBody());
                moreFlag = GeneralResponseBodyUtil.getMoreFlag(result.getBody());
                // 如果還沒存過錢包token，將token存入
                if(TextUtils.isEmpty(PreferenceUtil.getPrefKeyInitWalletToken(getActivity()))){
                    PreferenceUtil.setPrefKeyInitWalletToken(getActivity(), result.getTokenID());
                }
                // 更新列表資料
                updateCountText(countList);
                adapter.setShowAmount(!needSVLogin());
                adapter.setList(pushMsgList, moreFlag);
            }else{
                // 執行預設的錯誤處理
                handleResponseError(result, (ActivityBase) getActivity());
            }

            if (PreferenceUtil.needCheckSystemMessage(getActivity())) {
                checkSystemMessage();
            }else{
                checkAppUpdate();
            }
        }
    };

    // 確認app更新的api callback
    private ResponseListener responseListenerCheckUpdate = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null){
                return;
            }

            final ActivityBase activityBase = ((ActivityBase) getActivity());
            activityBase.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                AppUpdateInfo appUpdateInfo = GeneralResponseBodyUtil.getAppUpdateInfo(result.getBody());
                activityBase.showAlertIfNeedUpdate(appUpdateInfo);
            }else {
                // 如果不是共同error
                if(!handleCommonError(result, activityBase)){
                    // 目前不做事
                }
            }
        }
    };

    private void checkSystemMessage() {
        // 呼叫api取得系統訊息列表
        try {
            GeneralHttpUtil.querySystemMessage(null, responseListenerSystemMessage, getActivity(), TAG);
            ((ActivityBase) getActivity()).showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    // 呼叫系統公告的listener
    private ResponseListener responseListenerSystemMessage = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null){
                return ;
            }
            ((ActivityBase) getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                ArrayList<WalletSystemMsg> systemMsgsList = GeneralResponseBodyUtil.parseSystemMessageList(result.getBody());
                // 檢查是否有type=5的系統訊息，要show alert
                if(systemMsgsList.size() > 0) {
                    for (final WalletSystemMsg item : systemMsgsList) {
                        if(item.getBbType().equals(SystemMessageType.WALLET_HOME_SHOW_ALERT.getCode())){
                            ((ActivityBase)getActivity()).showAlertDialog("", item.getTitle(), R.string.dialog_system_msg_button_detail, android.R.string.cancel,
                                    // 按下詳情
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Fragment fragment = SystemMessageDetailFragment.newInstance(item);
                                            gotoFragment(fragment, true);
                                        }
                                    },
                                    // 按下取消
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }, true);
                            break;
                        }
                    }
                }
                PreferenceUtil.setLastCheckSystemMessage(getActivity());
            } else {
                // 不處理
            }

            checkAppUpdate();
        }
    };

    private void checkAppUpdate(){
        if(PreferenceUtil.needCheckUpdate(getActivity())) {
            // 呼叫確認app版本更新api
            try {
                GeneralHttpUtil.checkAppUpdate(responseListenerCheckUpdate, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ResponseListener responseListenerPersonaldata = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            String returnCode = result.getReturnCode();
            if(getActivity() == null){
                return;
            }
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得電話跟email
                String phoneEncrypt = GeneralResponseBodyUtil.getPhone(result.getBody());
                String emailEncrypt = GeneralResponseBodyUtil.getEmail(result.getBody());
                PreferenceUtil.setEmail(getActivity(), emailEncrypt);
                PreferenceUtil.setPhoneNumber(getActivity(), phoneEncrypt);

                PreferenceUtil.setHasLoadPersonalData(getActivity(), true);
            }
        }
    };

    @Override
    public void onItemClicked(WalletHomePushMsg item) {
        PushMsgHelper helper = new PushMsgHelper((ActivityBase)getActivity(), TAG);
        helper.doActionAccordingUrl(item.getUrl());
    }

    /**
     * count文字區的ViewHolder
     */
    private class CountViewHolder{
        public TextView txtTitle;
        public TextView txtNumber;
        public ImageView imgHasNew;

        public CountViewHolder(View itemView, int titleResId) {
            txtTitle = (TextView)itemView.findViewById(android.R.id.title);
            txtTitle.setText(titleResId);
            txtNumber = (TextView)itemView.findViewById(R.id.number);
            imgHasNew = (ImageView)itemView.findViewById(R.id.img_has_new);
        }

        public void setNumber(int number){
            txtNumber.setText(String.valueOf(number));
            int textColor;
            if(number > 0){
                textColor = getResources().getColor(R.color.wallethome_button_text);
            }else{
                textColor = getResources().getColor(R.color.wallethome_button_text_0_record);
            }
            txtTitle.setTextColor(textColor);
            txtNumber.setTextColor(textColor);
        }

        public void setShowHasNew(boolean hasNew){
            imgHasNew.setVisibility(hasNew ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 如果是聯絡人圖示，開啟聯絡人列表
        if (id == R.id.action_contacts) {
            startActivity(new Intent(getActivity(), ContactListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
