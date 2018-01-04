package com.dbs.omni.tw.controller.payment.bank;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.element.SymbolItem;
import com.dbs.omni.tw.model.element.SelectItemData;
import com.dbs.omni.tw.model.payment.PaidBankData;
import com.dbs.omni.tw.util.BitmapUtil;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.UserInfoUtil;
import com.dbs.omni.tw.util.http.PaymentHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class OthersPaymentFragment extends Fragment {

    public static final String TAG = "OthersPaymentFragment";
    public static final String ARG_FROM_PER_LOGIN = "ARG_FROM_PER_LOGIN";
    public static final String ARG_MAX_PAID = "ARG_MAX_PAID";
    public static final String ARG_MIN_PAID = "ARG_MIN_PAID";
    public static final String ARG_USER_ID = "ARG_USER_ID";

    private Button btnNextStep;
    private InputTextView inputTextPaymentPrice,inputTextTransferAccount, inputTextCardNumber;
    private TextView textViewLeftUp;
    private TextView textViewLeftMiddle;
    private TextView textViewRightUp;
    private TextView textViewRightMiddle;
    private Button buttonAnimation;
    private ImageView imageViewPaymentLeft;
    private TextView textView_payment_left;
    private SymbolItem textViewHintPrice,textViewHintID,textViewHintFee;
    private AnimationDrawable frameAnimation;
    private CheckBox checkboxAgreeNote;

    private String stringAmount, stringAnnotation, stringTransferAccount;
    private String stringNID = "";
    private Double maxPaid, minPaid;

    private PaidBankData paidBankData;

    private boolean isPreLoginFlow = false;

    private OthersPaymentFragment.OnEventListener onEventListener;

    public interface OnEventListener {
        void OnNextEvent(String stringAmount, String stringAnnotation, String stringTransferAccount, String nID);
        void OnNextEventByPreLogin(String stringAmount, String stringAnnotation, String stringTransferAccount, String nID, String settleNO);
        void ChooseBankEvent();
    }

    public void setOnEventListener (OthersPaymentFragment.OnEventListener listener) {
        this.onEventListener = listener;
    }

    public static OthersPaymentFragment newInstance(String maxPaid, String minPaid) {
        Bundle args = new Bundle();

        args.putBoolean(ARG_FROM_PER_LOGIN, false);
        args.putDouble(ARG_MAX_PAID, Double.valueOf(maxPaid));
        args.putDouble(ARG_MIN_PAID, Double.valueOf(minPaid));

        OthersPaymentFragment fragment = new OthersPaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static OthersPaymentFragment newInstance(boolean isPreLogin, String maxPaid, String minPaid, String userID) {

        Bundle args = new Bundle();

        args.putBoolean(ARG_FROM_PER_LOGIN, isPreLogin);
        args.putDouble(ARG_MAX_PAID, Double.valueOf(maxPaid));
        args.putDouble(ARG_MIN_PAID, Double.valueOf(minPaid));
        args.putString(ARG_USER_ID, userID);

        OthersPaymentFragment fragment = new OthersPaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActivityBase) getActivity()).setHeadHide(false);


        if(getArguments() !=null && getArguments().containsKey(ARG_FROM_PER_LOGIN)) {
            isPreLoginFlow = getArguments().getBoolean(ARG_FROM_PER_LOGIN);
        }

        if(getArguments() !=null && getArguments().containsKey(ARG_MAX_PAID)) {
            maxPaid = getArguments().getDouble(ARG_MAX_PAID);
        }

        if(getArguments() !=null && getArguments().containsKey(ARG_MIN_PAID)) {
            minPaid = getArguments().getDouble(ARG_MIN_PAID);
        }


        if (isPreLoginFlow) {
            if(getArguments() !=null && getArguments().containsKey(ARG_USER_ID)) {
                stringNID = getArguments().getString(ARG_USER_ID);
            }
        } else {
            if(!TextUtils.isEmpty(UserInfoUtil.getsNID())){
                stringNID = UserInfoUtil.getsNID();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_others_payment, container, false);

        final String minPaidString = FormatUtil.toDecimalFormat(getContext(), minPaid, true);
        final String maxPaidString = FormatUtil.toDecimalFormat(getContext(), maxPaid, true);

        btnNextStep = (Button)view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnNextStepListener);
        btnNextStep.setEnabled(false);

        textViewHintID = (SymbolItem) view.findViewById(R.id.textView_hint_ID);
        textViewHintPrice = (SymbolItem) view.findViewById(R.id.textView_hint_price);
        textViewHintFee = (SymbolItem) view.findViewById(R.id.textView_hint_fee);

        textViewHintID.setTextContent(getString(R.string.user_id_title) + stringNID);
        textViewHintPrice.setTextContent(getString(R.string.min_amount_title) + minPaidString);
        textViewHintFee.setTextContent(getString(R.string.hint_transfer_fee));

        inputTextCardNumber = (InputTextView) view.findViewById(R.id.inputText_credit_card);
        inputTextCardNumber.setTitle(FormatUtil.getCreditCardTile(getActivity(), true));
        inputTextCardNumber.setOnFinishEdit(onFinishEditListener);
        if(isPreLoginFlow) {
            inputTextCardNumber.setVisibility(View.VISIBLE);
        } else {
            inputTextCardNumber.setVisibility(View.GONE);
        }


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
                    textViewHintPrice.setTextContent(getString(R.string.min_amount_title) + minPaidString );
                }else if(item == selectItems.get(1)){ //選擇最低
                    textViewHintPrice.setTextContent(getString(R.string.all_amount_title) + maxPaidString);
                }else if(item == selectItems.get(2)){ //選擇其他
                    textViewHintPrice.setTextContent(getString(R.string.min_amount_title) + minPaidString + "\n" + getString(R.string.all_amount_title) + maxPaidString);
                }
            }
        }, 0, selectItems.size() - 1);

        //點擊輸入欄位
        inputTextPaymentPrice.setOnChangeEditContentListener(new InputTextView.OnChangeEditContentListener() {
            @Override
            public void OnOtherItem() {
                textViewHintPrice.setTextContent(getString(R.string.min_amount_title) + minPaidString + "\n" + getString(R.string.all_amount_title) + maxPaidString);
            }
        });
//        inputTextPaymentPrice.setSelectOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        inputTextTransferAccount = (InputTextView)view.findViewById(R.id.inputText_TransferAccount);
        inputTextTransferAccount.setOnFinishEdit(onFinishEditListener);
        inputTextTransferAccount.setDigits(getContext().getResources().getString(R.string.only_number_capital_only));
        inputTextTransferAccount.setInputType(InputType.TYPE_CLASS_PHONE);

        textViewLeftUp = (TextView)view.findViewById(R.id.textView_left_up);
        textViewLeftMiddle = (TextView)view.findViewById(R.id.textView_left_middle);
        textViewRightUp = (TextView)view.findViewById(R.id.textView_right_up);
        textViewRightMiddle = (TextView)view.findViewById(R.id.textView_right_middle);

        textViewLeftUp.setText(R.string.choose_paid_account);
        textViewRightUp.setText(R.string.price_of_current_payment);
        textViewRightMiddle.setText(FormatUtil.toDecimalFormat(getContext(), maxPaid, true));

        imageViewPaymentLeft = (ImageView) view.findViewById(R.id.imageView_payment_left);

        //設定左邊按鈕的閃爍動畫
        buttonAnimation = (Button)view.findViewById(R.id.button_animation);
//        // Get the background, which has been compiled to an AnimationDrawable object.
        frameAnimation = (AnimationDrawable) buttonAnimation.getBackground();
//        // Start the animation (looped playback by default).
        frameAnimation.start();

        //由於動畫疊在前方 , 所以把listener接給buttonAnimation
        buttonAnimation.setOnClickListener(btnLeftListener);

        textView_payment_left = (TextView)view.findViewById(R.id.textView_payment_left);

        textViewLeftUp.setText(R.string.choose_paid_account);

        if(paidBankData != null) {
            setBank(paidBankData);
        }

        checkboxAgreeNote = (CheckBox) view.findViewById(R.id.checkbox_agree_notes);
        checkboxAgreeNote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isEnableNextButton();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isPreLoginFlow) {
            ((ActivityBase) getActivity()).setCenterTitle(R.string.payment_others_main_title);
        } else {
            ((ActivityBase) getActivity()).setCenterTitleForCloseBar(R.string.payment_others_main_title);
        }
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
            onEventListener.ChooseBankEvent();
        }
    };

    private Button.OnClickListener btnNextStepListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stringAmount = String.valueOf(inputTextPaymentPrice.getAmount());
            stringAnnotation = inputTextPaymentPrice.getTextSubContent();
            stringTransferAccount = inputTextTransferAccount.getContent();

            double doubleAmount = inputTextPaymentPrice.getAmount();
//            String stringAmountChinese = chineseMoneyFormat.toChineseMoneyUpper(doubleAmount);
//            stringAmountChinese = getString(R.string.alert_chinese_amount) + stringAmountChinese;

            if (doubleAmount > 100000){
                ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.alert_price_greater_than_100000), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
            }else{
//                ((ActivityBase)getActivity()).showAlertDialog(stringAmountChinese, R.string.button_confirm, android.R.string.cancel,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
                if(isPreLoginFlow) {
                    cellVerifyUserDataForPreloginPayment();
                } else {
                    callVerifyUserDataForOtherBankPayment();
                }
//                            }
//                        },
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, false);
            }
        }
    };

    public void setBank(PaidBankData data) {
        paidBankData = data;
        textViewLeftUp.setText(data.getBankName());
        textViewLeftMiddle.setText(getString(R.string.code)+ data.getBankNo());

        //停止動畫並把動畫按鈕設為透明
        frameAnimation.stop();
        buttonAnimation.setBackgroundResource(android.R.color.transparent);

        if(!TextUtils.isEmpty(data.getBankIcon())){
            Bitmap bitmap = BitmapUtil.base64ToBitmap(data.getBankIcon());
            imageViewPaymentLeft.setBackground(new BitmapDrawable(getResources(), bitmap));
        }else{
            imageViewPaymentLeft.setBackgroundResource(R.drawable.bg_payment_upside_button_white);
            String string = data.getBankName().substring(0, 1);
            textView_payment_left.setText(string);
            textView_payment_left.bringToFront();
        }

        isEnableNextButton();
    }

    //檢查輸入內容
    private void isEnableNextButton() {
        boolean isEnable = false;


        if(textViewLeftMiddle != null && !textViewLeftMiddle.getText().toString().isEmpty()){
            if(inputTextPaymentPrice.getContent() != null && !inputTextPaymentPrice.getContent().isEmpty()){
                if(inputTextTransferAccount.getContent() != null && !inputTextTransferAccount.getContent().isEmpty()){
                    if(checkboxAgreeNote.isChecked()){
                        isEnable = true;
                    }
                }
            }
        }
        if(isPreLoginFlow) {
            isEnable = isEnable & inputTextCardNumber.isValidatePass();
        }

        btnNextStep.setEnabled(isEnable);
    }

    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton();
        }
    };

    private void callVerifyUserDataForOtherBankPayment(){
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
                PaymentHttpUtil.verifyUserDataForOtherBankPayment(paidBankData.getBankNo(), stringTransferAccount, stringAmount,responseListener_verifyUserDataForOtherBankPayment, getActivity());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private ResponseListener responseListener_verifyUserDataForOtherBankPayment = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                onEventListener.OnNextEvent(stringAmount, stringAnnotation, stringTransferAccount, stringNID);
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


    private void cellVerifyUserDataForPreloginPayment(){
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
                PaymentHttpUtil.verifyUserDataForPreloginPayment(stringNID, paidBankData.getBankNo(), stringTransferAccount, stringAmount, responseListener_verifyUserDataForPreLoginPayment, getActivity());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private ResponseListener responseListener_verifyUserDataForPreLoginPayment = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                onEventListener.OnNextEventByPreLogin(stringAmount, stringAnnotation, stringTransferAccount, stringNID, FormatUtil.removeCreditCardFormat(inputTextCardNumber.getContent()));
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
