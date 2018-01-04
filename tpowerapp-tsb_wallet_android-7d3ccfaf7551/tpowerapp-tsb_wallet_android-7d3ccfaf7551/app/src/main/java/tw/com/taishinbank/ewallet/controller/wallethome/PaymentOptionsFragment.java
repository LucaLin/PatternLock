package tw.com.taishinbank.ewallet.controller.wallethome;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.controller.creditcard.CreditCardPaymentLoginActivity;
import tw.com.taishinbank.ewallet.controller.creditcard.QRCodeScannerActivity;
import tw.com.taishinbank.ewallet.controller.sv.PaymentActivity;
import tw.com.taishinbank.ewallet.controller.sv.QRCodeGeneratorActivity;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PermissionUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;

/**
 * 儲值首頁
 */
public class PaymentOptionsFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "PaymentOptionsFragment";
    private static final String ARG_SHOW_OPTION = "ARG_SHOW_OPTION";
    public static final int ONLY_SV = 1;
    public static final int ONLY_CARD = 2;

    private SVAccountInfo svAccountInfo;
    private int showOption;

    public static PaymentOptionsFragment newInstance(int showOption) {
        Bundle args = new Bundle();
        args.putInt(ARG_SHOW_OPTION, showOption);
        PaymentOptionsFragment fragment = new PaymentOptionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PaymentOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments() != null) {
            showOption = getArguments().getInt(ARG_SHOW_OPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallethome_payment_options, container, false);

        Button button = (Button) view.findViewById(R.id.button_sv_pay);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.button_sv_scan_qr_code);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.button_card_scan_qr_code);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.button_card_show_qr_code);
        button.setOnClickListener(this);
        if(showOption == ONLY_SV) {
            View layoutCard = view.findViewById(R.id.layout_card);
            layoutCard.setVisibility(View.GONE);
        }else if(showOption == ONLY_CARD) {
            View layoutCard = view.findViewById(R.id.layout_sv);
            layoutCard.setVisibility(View.GONE);
        }

        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.wallethome_choose_payment);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(getString(R.string.home_btn_pay));

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();


        // 跟儲值有關的功能須先確認是否需要先進行登入
        switch (viewId){
            /* 儲值相關 */
            // 轉帳付款
            case R.id.button_sv_pay:
                // 如果需要登入儲值
                if(needSVLogin()) {
                    // 連到儲值帳戶登入頁
                    startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                    return ;
                }
                startActivity(new Intent(getActivity(), PaymentActivity.class));
                break;

            // 掃碼收付
            case R.id.button_sv_scan_qr_code:
                // 如果需要登入儲值
                if(needSVLogin()) {
                    // 連到儲值帳戶登入頁
                    startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                    return ;
                }
                startActivity(new Intent(getActivity(), QRCodeGeneratorActivity.class));
                break;


            /* 信用卡 */
            // 掃碼付款
            case R.id.button_card_scan_qr_code:
                if(GlobalConst.DISABLE_CREDIT_CARD) {
                    ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_credit_card_is_disabled));
                    break;
                }

                launchScanner();
                break;

            // 顯示付款碼
            case R.id.button_card_show_qr_code:
                if(GlobalConst.DISABLE_CREDIT_CARD) {
                    ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_credit_card_is_disabled));
                    break;
                }
                if(CreditCardUtil.GetCreditCardList(getContext()).size() == 0) {
                    ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.nocard_alert), R.string.button_confirm,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   // gotoFragment(new CreditCardFragment(), true);
                                    goToHome(MainActivity.TAB_CREDIT);
                                }
                            }, false);
                }
                else {
                    startActivity(new Intent(getActivity(), CreditCardPaymentLoginActivity.class));
                }
                break;
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
     * 開啟首頁
     */
    private void goToHome(String page){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(page != null) {
            intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, page);
        }
        startActivity(intent);
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
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 儲值帳戶資訊存sharedpreference
                PreferenceUtil.setSVAccountInfo(getActivity(), result.getBody().toString());

                // 跟iOS同步，如果是token或儲值帳戶的svToken過期，變更為鎖頭
            } else if(returnCode.equals(ResponseResult.RESULT_TOKEN_EXPIRED)
                    || returnCode.equals(ResponseResult.RESULT_SV_TOKEN_EXPIRED)){
                PreferenceUtil.setSVAccountInfo(getActivity(), null);

            } else {
                // TODO 不成功的判斷與處理
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void launchScanner() {
        String[] permissionsForScan = {Manifest.permission.CAMERA};
        if (!PermissionUtil.needGrantRuntimePermission(this, permissionsForScan,
                PermissionUtil.PERMISSION_REQUEST_CODE_SCAN)) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), QRCodeScannerActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_SCAN) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                launchScanner();
            }else {
                PermissionUtil.showNeedPermissionDialog(getActivity(), permissions, grantResults);
            }
        }
    }
}
