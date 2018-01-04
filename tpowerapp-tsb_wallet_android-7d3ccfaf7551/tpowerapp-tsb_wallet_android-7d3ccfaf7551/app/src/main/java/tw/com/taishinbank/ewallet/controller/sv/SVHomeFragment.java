package tw.com.taishinbank.ewallet.controller.sv;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.ContactListActivity;
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.gcm.WalletGcmListenerService;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.AppUpdateInfo;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.PushMsgHelper;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.responsebody.SVResponseBodyUtil;

/**
 * 儲值首頁
 */
public class SVHomeFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "SVHomeFragment";
    private ImageButton buttonLocker;
    private TextView textBalance;
    private SVAccountInfo svAccountInfo;
    private ImageView imagePhoto;
    private TextView textBalanceStatus;
    private ImageLoader imageLoader;
    private ImageView imageSVLogNew;

    private View layoutNotification;
    private TextView textNotificationMsg;

    private PushBroadcastReceiver pushBroadcastReceiver;
    private boolean hideNotificationView = true;

    public SVHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        pushBroadcastReceiver = new PushBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_home, container, false);
        setButtonText(view, R.id.btn_deposit_withdraw, R.string.sv_menu_deposit_withdraw, R.drawable.ic_e_home_deposit_withdraw);
        setButtonText(view, R.id.btn_share_cost, R.string.sv_menu_share_cost, R.drawable.ic_e_home_share_cost);
        setButtonText(view, R.id.btn_pay, R.string.sv_menu_pay, R.drawable.ic_e_home_pay);
        setButtonText(view, R.id.btn_receivables, R.string.sv_menu_receivables, R.drawable.ic_e_home_receivables);
        setButtonText(view, R.id.btn_qr_code, R.string.sv_menu_qr_code, R.drawable.ic_e_home_qr_code);
        View viewSVLog = setButtonText(view, R.id.btn_record, R.string.sv_menu_record, R.drawable.ic_e_home_record);
        imageSVLogNew = (ImageView) viewSVLog.findViewById(R.id.image_new);

        RelativeLayout layoutBalance = (RelativeLayout) view.findViewById(R.id.layout_balance);
        layoutBalance.setOnClickListener(this);

        buttonLocker = (ImageButton) view.findViewById(R.id.button_locker);
        buttonLocker.setOnClickListener(this);
        ImageButton buttonSVInfo = (ImageButton) view.findViewById(R.id.button_sv_info);
        buttonSVInfo.setOnClickListener(this);

        textBalance = (TextView) view.findViewById(R.id.text_balance);
        imagePhoto = (ImageView) view.findViewById(R.id.image_photo);

        textBalanceStatus = (TextView) view.findViewById(R.id.text_balance_status);

        layoutNotification = view.findViewById(R.id.layout_notification);
        layoutNotification.setOnClickListener(this);
        textNotificationMsg = (TextView) view.findViewById(R.id.text_notification_msg);

        return view;
    }

    private View setButtonText(View rootView, int viewId, int textResId, int drawableResId){
        View view = rootView.findViewById(viewId);
        TextView textView = (TextView) view.findViewById(android.R.id.title);
        textView.setText(textResId);
        ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
        imageView.setImageResource(drawableResId);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(getString(R.string.title_sv_account), false);
        activityBase.getWindow().setBackgroundDrawableResource(R.drawable.bg_fragment_sv_home);

        LocalBroadcastManager.getInstance(activityBase).registerReceiver(pushBroadcastReceiver,
                new IntentFilter(WalletGcmListenerService.ACTION_RECEIVED_PUSH));
        hideNotificationView = true;

        loadPhoto();

        // TODO 更新帳戶紀錄的點點

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
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(pushBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
        if(hideNotificationView) {
            layoutNotification.setVisibility(View.GONE);
        }
    }

    private void loadPhoto()
    {
        if(imageLoader == null){
            imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.photo_size));
        }
        // 設定頭像
        imageLoader.loadImage(PreferenceUtil.getMemNO(this.getActivity()), imagePhoto);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        // 如果需要登入儲值
        if(needSVLogin()) {
            if(viewId == R.id.layout_notification){
                hideNotificationView = false;
            }
            // 連到儲值帳戶登入頁
            startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            return ;
        }

        switch (viewId){
            // 如果是帳戶狀態按鈕, 開啟帳戶詳情頁
            case R.id.button_sv_info:
            case R.id.layout_balance:
                startActivity(new Intent(getActivity(), SVAccountDetailActivity.class));
                break;

            // 如果是鎖頭按鈕
            case R.id.button_locker:
                // 連到儲值帳戶登入頁
                startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                break;

            // 開啟儲值提領功能主頁
            case R.id.btn_deposit_withdraw:
                startActivity(new Intent(getActivity(), DepositWithdrawActivity.class));
                break;

            // 開啟開啟QR Code / 掃碼
            case R.id.btn_qr_code:
                startActivity(new Intent(getActivity(), QRCodeGeneratorActivity.class));
                break;

            case R.id.btn_record:
                Fragment fragment = new TransactionLogFragment();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
                ft.replace(android.R.id.tabcontent, fragment);
                ft.addToBackStack(null);
                ft.commit();
                break;

            // 開啟轉帳付款頁面
            case R.id.btn_pay:
                startActivity(new Intent(getActivity(), PaymentActivity.class));
                break;

            // 開啟轉帳收款頁面
            case R.id.btn_receivables:
                startActivity(new Intent(getActivity(), ReceiptActivity.class));
                break;

            // 開啟均分收款頁面
            case R.id.btn_share_cost:
                startActivity(new Intent(getActivity(), SharedReceiptActivity.class));
                break;

            // 通知
            case R.id.layout_notification:
                PushMsgHelper helper = new PushMsgHelper((ActivityBase) getActivity(), TAG);
                helper.doActionAccordingUrl((String) v.getTag());
                break;
        }
    }

    @SuppressWarnings( "deprecation" )
    private void updateAccountInfo() {
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
        if(svAccountInfo != null && !isSVLoginTimeExpired()) {
            buttonLocker.setVisibility(View.GONE);
            textBalance.setVisibility(View.VISIBLE);
            String formattedBalance = FormatUtil.toDecimalFormatFromString(svAccountInfo.getBalance(), true);
            textBalance.setText(formattedBalance);

            int dailyBalance = getBalance(svAccountInfo);
            int accountBalance = Integer.parseInt(svAccountInfo.getBalance());

            /* 原為「正常」、「篇低」、「0」。
               調整為顯示以下兩項的金額取小：
                1.每日交易限額-當日累積交易金額（當日可交易餘額）
                2.儲值帳戶餘額 */
            if(dailyBalance < accountBalance){
                formattedBalance = FormatUtil.toDecimalFormat(dailyBalance, true);
            }else{
                formattedBalance = FormatUtil.toDecimalFormat(accountBalance, true);
            }
            textBalanceStatus.setText(formattedBalance);
            textBalanceStatus.setTextColor(getResources().getColor(R.color.sv_balance_status_normal));
            textBalanceStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_e_home_status_more, 0);

        }else{
            buttonLocker.setVisibility(View.VISIBLE);
            textBalance.setVisibility(View.GONE);

            // 尚未登入
            textBalanceStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_e_home_status_more, 0);
            textBalanceStatus.setTextColor(getResources().getColor(R.color.sv_balance_status_normal));
            textBalanceStatus.setText(getString(R.string.balance_status_not_available));
        }
    }

    /**
     * 取得剩餘金額，比較本月與本日的剩餘金額取較小的
     * @param svAccountInfo
     * @return
     */
    private int getBalance(SVAccountInfo svAccountInfo)
    {
        int monthlyBalance = Integer.parseInt(svAccountInfo.getMonthlyLimCurr()) - Integer.parseInt(svAccountInfo.getMonthlyAmount());
        int dailyBalance = Integer.parseInt(svAccountInfo.getDailyLimCurr()) - Integer.parseInt(svAccountInfo.getDailyAmount());
        if(monthlyBalance > dailyBalance)
            return dailyBalance;
        else
            return monthlyBalance;

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
            ((ActivityBase)getActivity()).dismissProgressLoading();

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

            // 呼叫web service取得儲值是否有新紀錄
            try {
                SVHttpUtil.getSVHomeHasNew(responseListenerSVHasNew, getActivity(), TAG);
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }
        }
    };

    // 呼叫取得儲值是否有新紀錄api的listener
    private ResponseListener responseListenerSVHasNew = new ResponseListener() {

        @Override
        @SuppressWarnings("all")
        public void onResponse(ResponseResult result) {
            // TODO 預防切換tab時，切換前所用的fragment莫名會進入onResume（待釐清原因）
            if(getActivity() == null) {
                return;
            }
            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();

            boolean hasNew = false;
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                hasNew = SVResponseBodyUtil.getHasNew(result.getBody());
            // 如果是Token失效，中文是[不合法連線，請重新登入]
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }

            imageSVLogNew.setVisibility(hasNew ? View.VISIBLE : View.GONE);

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
            // 如果不是共同error
            }else if(!handleCommonError(result, (ActivityBase) getActivity())) {
                // 目前不做事
            }
        }
    };

    private boolean needSVLogin(){
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
        return !(svAccountInfo != null && !isSVLoginTimeExpired());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 判斷是否登入成功
        if(resultCode == Activity.RESULT_OK) {
            Log.d("TODO:", "Login success");
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

    private class PushBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra(WalletGcmListenerService.EXTRA_PUSH_URL);
            if(!TextUtils.isEmpty(url) && url.startsWith(WalletGcmListenerService.MyPayPushType.receive.name())) {
                textNotificationMsg.setText(intent.getStringExtra(WalletGcmListenerService.EXTRA_PUSH_MESSAGE));
                layoutNotification.setVisibility(View.VISIBLE);
                layoutNotification.setTag(url);
            }
        }
    }
}
