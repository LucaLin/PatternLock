package tw.com.taishinbank.ewallet.controller.extra;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import tw.com.taishinbank.ewallet.controller.creditcard.MallWebViewActivity;
import tw.com.taishinbank.ewallet.controller.red.RedEnvelopeFragment;
import tw.com.taishinbank.ewallet.controller.sv.SVAccountDetailActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.AppUpdateInfo;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.CreditCardHttpUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.CreditCardResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;

/**
 * 儲值首頁
 */
public class ExtraHomeFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ExtraHomeFragment";

    private ImageButton buttonLocker;
    private TextView textBalance;

    private SVAccountInfo svAccountInfo;
    private ImageView imagePhoto;
    private ImageLoader imageLoader;

    public ExtraHomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_extra_home, container, false);
        setButtonText(view, R.id.btn_red_home,       R.string.extra_menu_red_home     , R.drawable.ic_g_home_red_envelope);
        setButtonText(view, R.id.btn_coupon,         R.string.extra_menu_coupon   , R.drawable.ic_g_home_ticket);
        setButtonText(view, R.id.btn_eticket,        R.string.extra_menu_eticket , R.drawable.ic_g_home_coupon);
        setButtonText(view, R.id.btn_earn_promotion, R.string.extra_menu_earn_promotion , R.drawable.ic_g_home_earn_sale);
        setButtonText(view, R.id.btn_store,          R.string.extra_menu_store    , R.drawable.ic_g_home_shopping);
        setButtonText(view, R.id.btn_promotion_act,  R.string.extra_menu_promotion_act , R.drawable.ic_g_home_activities);

        //TODO 先不顯示未實作功能的按鈕
        {
           // view.findViewById(R.id.btn_eticket).setVisibility(View.INVISIBLE);
//            view.findViewById(R.id.btn_store).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.btn_promotion_act).setVisibility(View.INVISIBLE);
        }

        buttonLocker = (ImageButton) view.findViewById(R.id.button_locker);
        buttonLocker.setOnClickListener(this);
        ImageButton buttonSVInfo = (ImageButton) view.findViewById(R.id.button_sv_info);
        buttonSVInfo.setOnClickListener(this);

        textBalance = (TextView) view.findViewById(R.id.text_balance);
        imagePhoto = (ImageView) view.findViewById(R.id.image_photo);

//        // 設定頭像
//        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.photo_size));
//        imageLoader.loadImage(PreferenceUtil.getMemNO(this.getActivity()), imagePhoto);

        return view;
    }

    private void setButtonText(View rootView, int viewId, int textResId, int drawableResId){
        View view = rootView.findViewById(viewId);
        TextView textView = (TextView) view.findViewById(android.R.id.title);
        textView.setText(textResId);
        ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
        imageView.setImageResource(drawableResId);
        view.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(getString(R.string.extra_home), false);
        activityBase.getWindow().setBackgroundDrawableResource(R.drawable.bg_fragment_extra_home);

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

        int viewId = v.getId();

        switch (viewId){
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

            // 鎖頭按鈕
            case R.id.button_locker:
                // 連到儲值帳戶登入頁
                startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                break;

            // 開啟紅包主頁
            case R.id.btn_red_home:
                Fragment fragment = new RedEnvelopeFragment();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
                ft.replace(android.R.id.tabcontent, fragment);
                ft.addToBackStack(null);
                ft.commit();
                break;

            // 開啟優惠券
            case R.id.btn_coupon:
                getActivity().startActivity(new Intent(getActivity(), MyCouponActivity.class));
                break;

            // 電子票券
            case R.id.btn_eticket:
                getActivity().startActivity(new Intent(getActivity(), MyTicketActivity.class));
                break;

            // 賺取優惠
            case R.id.btn_earn_promotion:
                startActivity(new Intent(getActivity(), EarnActivity.class));
                break;

            // 商城
            case R.id.btn_store:
                getStoreOrderToken();
                break;

            // 優惠活動
            case R.id.btn_promotion_act:
                break;
        }
    }

    private void updateAccountInfo() {
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
        if(svAccountInfo != null && !isSVLoginTimeExpired()) {
            buttonLocker.setVisibility(View.GONE);
            textBalance.setVisibility(View.VISIBLE);
            String formattedBalance = FormatUtil.toDecimalFormatFromString(svAccountInfo.getBalance(), true);
            textBalance.setText(formattedBalance);

        } else {
            buttonLocker.setVisibility(View.VISIBLE);
            textBalance.setVisibility(View.GONE);

        }
    }

    /**
     * 切換到指定的fragment
     */
    private void gotoFragment(Fragment fragment, boolean withAnimation){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (withAnimation) {
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
        if (!TextUtils.isEmpty(svLoginTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                // 將上次登入時間字串轉成date物件
                Date lastLoginTime = sdf.parse(svLoginTime);

                // 取得現在時間
                Calendar c = Calendar.getInstance();
                // 判斷是否已經超過上次登入時間10分鐘
                c.add(Calendar.MINUTE, -10);

                if (lastLoginTime.after(c.getTime())) {
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
            }else{
                // 如果不是共同error
                if(!handleCommonError(result, activityBase)){
                    // 目前不做事
                }
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

    private void getStoreOrderToken() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                ((ActivityBase) getActivity()).showProgressLoading();
                CreditCardHttpUtil.GetTicketOrderToken(responseListenerOrderToken, getActivity(), TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private ResponseListener responseListenerOrderToken = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {

                String orderToken = CreditCardResponseBodyUtil.getTicketOrderToken(result.getBody());
                gotoStoreWebView(orderToken);

            } else {
                // 如果是Token失效，中文是[不合法連線，請重新登入]
                if(!handleCommonError(result, ((ActivityBase) getActivity()))){
                    ((ActivityBase) getActivity()).showAlertDialog(result.getReturnMessage());
                }

            }
        }
    };

    private void gotoStoreWebView(String orderToken) {
        String urlString = HttpUtilBase.MOHIST_STORE_URL;
        urlString += "?token=" + orderToken + "&redirect=tswallet:\\\\";
        Intent intent = new Intent(getContext(), MallWebViewActivity.class);
        intent.putExtra(MallWebViewActivity.EXTRA_URL, urlString);
        startActivity(intent);
    }
}
