package tw.com.taishinbank.ewallet.controller.wallethome;


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
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.controller.sv.QRCodeGeneratorActivity;
import tw.com.taishinbank.ewallet.controller.sv.ReceiptActivity;
import tw.com.taishinbank.ewallet.controller.sv.SharedReceiptActivity;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;

/**
 * 儲值首頁
 */
public class ReceiptOptionsFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ReceiptOptionsFragment";

    private SVAccountInfo svAccountInfo;

    public ReceiptOptionsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_wallethome_receipt_options, container, false);

        Button button = (Button) view.findViewById(R.id.button_sv_receipt);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.button_sv_scan_qr_code);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.button_shared_receipt);
        button.setOnClickListener(this);

        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.wallethome_choose_receipt);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(getString(R.string.home_btn_receive));

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
        // 收款功能都跟儲值有關，所以須先確認是否需要先進行登入
        // 如果需要登入儲值
        if(needSVLogin()) {
            // 連到儲值帳戶登入頁
            startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            return ;
        }

        int viewId = v.getId();

        switch (viewId){
            // 轉帳收款
            case R.id.button_sv_receipt:
                // 如果需要登入儲值
                if(needSVLogin()) {
                    // 連到儲值帳戶登入頁
                    startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                    return ;
                }
                startActivity(new Intent(getActivity(), ReceiptActivity.class));
                break;

            // 掃碼收付
            case R.id.button_sv_scan_qr_code:
                startActivity(new Intent(getActivity(), QRCodeGeneratorActivity.class));
                break;

            // 均分收款
            case R.id.button_shared_receipt:
                startActivity(new Intent(getActivity(), SharedReceiptActivity.class));
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
}
