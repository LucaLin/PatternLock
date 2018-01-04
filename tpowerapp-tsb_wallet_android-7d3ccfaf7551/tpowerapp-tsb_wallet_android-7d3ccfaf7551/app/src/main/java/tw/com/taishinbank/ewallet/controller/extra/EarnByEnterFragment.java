package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.CouponEnter;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

public class EarnByEnterFragment extends Fragment {

    private static final String TAG = "EarnByEnterFragment";
    // -- View Hold --
    private EditText txtPromotionCode;
    private Button btnTakeIt;

    public EarnByEnterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_earn_by_enter, container, false);

        // Set view hold
        txtPromotionCode = (EditText) view.findViewById(R.id.txt_promotion_code);
        btnTakeIt = (Button) view.findViewById(R.id.btn_take_it);

        // Set listener
        btnTakeIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTakeCoupon();
            }
        });
        txtPromotionCode.addTextChangedListener(new BasicEditTextWatcher(txtPromotionCode, null) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                onPromotionCodeTextChanged(s);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkButtonEnable();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    // ----
    // Http
    // ----
    private void earnCoupon() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                ExtraHttpUtil.earnCoupon(txtPromotionCode.getText().toString(), responseListenerEarnCoupon, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ResponseListener responseListenerEarnCoupon = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
        if (getActivity() == null)
            return;

        ((ActivityBase) getActivity()).dismissProgressLoading();
        String returnCode = result.getReturnCode();

        // 如果returnCode是成功
        if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
            //
            CouponEnter couponEnter = new Gson().fromJson(result.getBody().toString(), CouponEnter.class);
            ((EarnActivity) getActivity()).takeCoupon(couponEnter);

        } else {
            // 已經用過分享碼
            if(returnCode.equals(ResponseResult.RESULT_SHARE_CODE_HAS_USED)) {
                ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.extra_earn_enter_error_dialog_title),
                        result.getReturnMessage(),
                        R.string.button_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, true);
            } else {
                // 執行預設的錯誤處理
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }

        }
    };


    // ----
    // My methods
    // ---

    // ----
    // User interaction
    // ----
    protected void clickTakeCoupon() {
        earnCoupon();
    }

    protected void onPromotionCodeTextChanged(Editable s) {
        checkButtonEnable();
    }


    /**
     * 檢查是否enable按鈕
     */
    private void checkButtonEnable(){
        if(txtPromotionCode.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT){
            btnTakeIt.setEnabled(true);
        }else{
            btnTakeIt.setEnabled(false);
        }
    }

}
