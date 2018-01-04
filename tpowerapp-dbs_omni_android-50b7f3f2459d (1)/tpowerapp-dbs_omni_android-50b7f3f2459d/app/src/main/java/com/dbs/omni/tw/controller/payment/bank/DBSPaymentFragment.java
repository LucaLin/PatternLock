package com.dbs.omni.tw.controller.payment.bank;


import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.element.SymbolItem;
import com.dbs.omni.tw.model.element.SelectItemData;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.http.PaymentHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.payment.DBSAccountData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;

import org.json.JSONException;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DBSPaymentFragment extends Fragment {

    public static final String TAG = "DBSPaymentFragment";

    public static final String ARG_MAX_PAID = "ARG_MAX_PAID";
    public static final String ARG_MIN_PAID = "ARG_MIN_PAID";

    private Button btnNextStep;
    private InputTextView inputTextPaymentPrice;
    private TextView textViewLeftUp;
    private TextView textViewLeftMiddle;
    private TextView textViewLeftDown;
    private TextView textViewRightUp;
    private TextView textViewRightMiddle;
    private TextView textViewRighttDown;
    private Button buttonPaymentLeft, buttonAnimation;
    private SymbolItem textViewHintPrice;
    private AnimationDrawable frameAnimation;

    private String stringAmount, stringAnnotation;
    private double maxPaid, minPaid, doubleAmount;

    private DBSAccountData dbsAccountData;
    private double doubleBalance;

    private DBSPaymentFragment.OnEventListener onEventListener;

    public interface OnEventListener {
        void OnNextEvent(String stringAmount, String stringAnnotation);
        void ChooseAccountEvent(double doubleAmount);
    }

    public void setOnEventListener (DBSPaymentFragment.OnEventListener listener) {
        this.onEventListener = listener;
    }

    public static DBSPaymentFragment newInstance(String maxPaid, String minPaid) {
        
        Bundle args = new Bundle();

        args.putDouble(ARG_MAX_PAID, Double.valueOf(maxPaid));
        args.putDouble(ARG_MIN_PAID, Double.valueOf(minPaid));

        DBSPaymentFragment fragment = new DBSPaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActivityBase) getActivity()).setHeadHide(false);

        if(getArguments() !=null && getArguments().containsKey(ARG_MAX_PAID)) {
            maxPaid = getArguments().getDouble(ARG_MAX_PAID);
        }

        if(getArguments() !=null && getArguments().containsKey(ARG_MIN_PAID)) {
            minPaid = getArguments().getDouble(ARG_MIN_PAID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dbs_payment, container, false);

        textViewHintPrice = (SymbolItem) view.findViewById(R.id.textView_hint_price);
        textViewHintPrice.setTextContent(getString(R.string.min_amount_title) + getString(R.string.amount_sign) + minPaid);

        btnNextStep = (Button)view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnNextStepListener);
        btnNextStep.setEnabled(false);

        inputTextPaymentPrice = (InputTextView)view.findViewById(R.id.inputText_payment_price);
        inputTextPaymentPrice.setOnFinishEdit(onFinishEditListener);

        //設定繳款金額彈出視窗的內容
        final ArrayList<SelectItemData> selectItems = new ArrayList<>();
        selectItems.add(new SelectItemData(FormatUtil.getPaymentAmountSubTitle(getContext(), true), maxPaid, false));
        selectItems.add(new SelectItemData(FormatUtil.getPaymentAmountSubTitle(getContext(), false), minPaid, false));
        selectItems.add(new SelectItemData(getContext().getString(R.string.other_amount_title)));

        //設定繳款金額欄位
        inputTextPaymentPrice.setSelectItems(selectItems, new InputTextView.OnSelectListener() {
            @Override
            public void onSelect(SelectItemData item) {
//                ((ActivityBase)getActivity()).showAlertDialog(item.getContent());

                if(item == selectItems.get(0)){ //選擇全繳
                    textViewHintPrice.setTextContent(getString(R.string.min_amount_title) + getString(R.string.amount_sign) + minPaid);
                }else if(item == selectItems.get(1)){ //選擇最低
                    textViewHintPrice.setTextContent(getString(R.string.all_amount_title) + getString(R.string.amount_sign) + maxPaid);
                }else if(item == selectItems.get(2)){ //選擇其他
                    textViewHintPrice.setTextContent(getString(R.string.min_amount_title) + getString(R.string.amount_sign) + minPaid + "\n" + getString(R.string.all_amount_title) + getString(R.string.amount_sign) + maxPaid);
                }

            }
        }, 0, selectItems.size() - 1);

        //點擊輸入欄位
        inputTextPaymentPrice.setOnChangeEditContentListener(new InputTextView.OnChangeEditContentListener() {
            @Override
            public void OnOtherItem() {
                textViewHintPrice.setTextContent(getString(R.string.min_amount_title) + getString(R.string.amount_sign) + minPaid + "\n" + getString(R.string.all_amount_title) + getString(R.string.amount_sign) + maxPaid);
            }
        });
//        inputTextPaymentPrice.setSelectOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });

        textViewLeftUp = (TextView)view.findViewById(R.id.textView_left_up);
        textViewLeftMiddle = (TextView)view.findViewById(R.id.textView_left_middle);
        textViewLeftDown = (TextView)view.findViewById(R.id.textView_left_down);
        textViewRightUp = (TextView)view.findViewById(R.id.textView_right_up);
        textViewRightMiddle = (TextView)view.findViewById(R.id.textView_right_middle);
        textViewRighttDown = (TextView)view.findViewById(R.id.textView_right_down);

        textViewLeftUp.setText(R.string.choose_paid_account);
        textViewRightUp.setText(R.string.price_of_current_payment);
        textViewRightMiddle.setText(getString(R.string.amount_sign) + Double.toString(maxPaid));

        buttonPaymentLeft = (Button)view.findViewById(R.id.imageView_payment_left);

        //設定左邊按鈕的閃爍動畫
        buttonAnimation = (Button)view.findViewById(R.id.button_animation);
//        // Get the background, which has been compiled to an AnimationDrawable object.
        frameAnimation = (AnimationDrawable) buttonAnimation.getBackground();
//        // Start the animation (looped playback by default).
        frameAnimation.start();

        //由於動畫疊在前方 , 所以把listener接給buttonAnimation
        buttonAnimation.setOnClickListener(btnLeftListener);

        textViewLeftUp.setText(R.string.choose_paid_account);

        if(dbsAccountData != null) {
            setAccount(dbsAccountData);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActivityBase) getActivity()).setCenterTitleForCloseBar(R.string.payment_DBS_main_title);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private Button.OnClickListener btnLeftListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doubleAmount = inputTextPaymentPrice.getAmount();
            onEventListener.ChooseAccountEvent(doubleAmount);
        }
    };

    private Button.OnClickListener btnNextStepListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stringAmount = String.valueOf(inputTextPaymentPrice.getAmount());
            stringAnnotation = inputTextPaymentPrice.getTextSubContent();
            doubleAmount = inputTextPaymentPrice.getAmount();

            //檢查帳戶是否餘額不足
            if(doubleBalance < doubleAmount){
                ((ActivityBase) getActivity()).showAlertDialog(getActivity().getString(R.string.alert_balance_not_enough), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        showChineseMoneyAlert(intAmount);

                        callVerifyUserDataForDBSPayment();
                    }
                }, true);
            }else{
//                showChineseMoneyAlert(intAmount);
                callVerifyUserDataForDBSPayment();
            }
        }
    };

//    //顯示中文金額的alert
//    private void showChineseMoneyAlert(int intAmount){
//        String stringAmountChinese = chineseMoneyFormat.toChineseMoneyUpper(intAmount);
//        stringAmountChinese = getString(R.string.alert_chinese_amount) + stringAmountChinese;
//
//        ((ActivityBase)getActivity()).showAlertDialog(stringAmountChinese, R.string.button_confirm, android.R.string.cancel,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        onEventListener.OnNextEvent(stringAmount, stringAnnotation);
//                    }
//                },
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }, false);
//    }

    public void setAccount(DBSAccountData data) {
        dbsAccountData = data;
        doubleBalance = Double.valueOf(data.getAcctBalance());

        textViewLeftUp.setText(FormatUtil.toHideCardNumberShortString(data.getAcctNO()));
        textViewLeftMiddle.setText(data.getAcctName());

        Double doubleBalance = Double.parseDouble(data.getAcctBalance());
        textViewLeftDown.setText(FormatUtil.toDecimalFormat(getContext(), doubleBalance, true));

        buttonPaymentLeft.setBackgroundResource(R.drawable.bg_payment_upside_button_dbs);

        //停止動畫並把動畫按鈕設為透明
        frameAnimation.stop();
        buttonAnimation.setBackgroundResource(android.R.color.transparent);

        isEnableNextButton();
    }


    //檢查輸入內容
    private void isEnableNextButton() {
        if(textViewLeftMiddle != null && !textViewLeftMiddle.getText().toString().isEmpty()){
            if(inputTextPaymentPrice.getContent() != null && !inputTextPaymentPrice.getContent().isEmpty()){
                btnNextStep.setEnabled(true);
                return;
            }
        }

        btnNextStep.setEnabled(false);
    }

    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton();
        }
    };

    private void callVerifyUserDataForDBSPayment(){
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
                PaymentHttpUtil.verifyUserDataForDBSPayment(dbsAccountData.getAcctNO(), stringAmount,responseListener_verifyUserDataForDBSPayment, getActivity());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private ResponseListener responseListener_verifyUserDataForDBSPayment = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                onEventListener.OnNextEvent(stringAmount, stringAnnotation);
            } else {
                // 如果是共同error，不繼續呼叫另一個api
                if (handleCommonError(result, (ActivityBase) getActivity())) {

                    return;
                } else {
                    showAlert((ActivityBase) getActivity(), result.getReturnMessage());
                }
            }
        }
    };

}
