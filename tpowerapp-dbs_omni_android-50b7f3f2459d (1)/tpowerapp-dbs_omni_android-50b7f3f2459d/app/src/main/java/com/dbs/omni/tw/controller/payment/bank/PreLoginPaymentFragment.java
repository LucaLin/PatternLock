package com.dbs.omni.tw.controller.payment.bank;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.ValidateUtil;
import com.dbs.omni.tw.util.http.PaymentHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.bill.PreLoginBillData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.BillResponseBodyUtil;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreLoginPaymentFragment extends Fragment {

    private OnPreLoginPaymentListener onPreLoginPaymentListener;

    public void setOnPreLoginPaymentListener(OnPreLoginPaymentListener onPreLoginPaymentListener) {
        this.onPreLoginPaymentListener = onPreLoginPaymentListener;
    }

    public interface OnPreLoginPaymentListener {
        void OnNext(PreLoginBillData data);
    }

    private Button buttonNext;
    private InputTextView inputTextBirth, inputTextUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_per_login_payment, container, false);
        buttonNext = (Button) view.findViewById(R.id.btnNextStep);
////        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateInputContent();
            }
        });
//
        inputTextUserID = (InputTextView) view.findViewById(R.id.inputText_user_id);
        inputTextUserID.setOnValidateListener(new InputTextView.OnValidateListener() {
            @Override
            public void OnPass() {
                isEnableNextButton(false);
            }

            @Override
            public void OnFail() {
                isEnableNextButton(false);
            }
        });

        inputTextBirth = (InputTextView) view.findViewById(R.id.inputText_date_of_birth);
        inputTextBirth.setTitle(FormatUtil.getDateOfBirthTile(getActivity(), true));
        inputTextBirth.setOnFinishEdit(onFinishEditListener);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(inputTextUserID.isValidatePass()) {
            buttonNext.setEnabled(true);
        } else {
            buttonNext.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase)getActivity()).dismissProgressLoading();
    }

    private void ValidateInputContent() {
        if(isEnableNextButton(true)) {
            qureyCurrentAmountForPreloginPayment();
        }
    }

    //檢查輸入內容
    private boolean isEnableNextButton(boolean isShowAlert) {
        boolean isEnable = true;

        isEnable = isEnable & inputTextUserID.isValidatePass();
        isEnable = isEnable & ValidateUtil.checkBirthday(getContext(), inputTextBirth.getContent().toString(), isShowAlert);

        buttonNext.setEnabled(isEnable);
        return isEnable;
    }

    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton(false);
        }
    };


//region api
    private void qureyCurrentAmountForPreloginPayment() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getContext())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                PaymentHttpUtil.queryCurrentAmountForPreloginPayment(inputTextUserID.getContent(), FormatUtil.removeDateFormatted(inputTextBirth.getContent()), responseListener, getActivity());
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;
            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                PreLoginBillData preLoginBillData = BillResponseBodyUtil.getPreLoginBillData(result.getBody());
                preLoginBillData.setnID(inputTextUserID.getContent());
                onPreLoginPaymentListener.OnNext(preLoginBillData);

            } else {
                handleResponseError(result, ((ActivityBase)getActivity()));
            }

        }
    };
//endregion
}
