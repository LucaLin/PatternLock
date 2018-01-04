package com.dbs.omni.tw.controller.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.payment.bank.BankPaymentActivity;
import com.dbs.omni.tw.controller.payment.convenient.ConvenientStorePaymentActivity;
import com.dbs.omni.tw.element.PaymentButton;
import com.dbs.omni.tw.setted.OmniApplication;

public class PaymentHomeFragment extends Fragment {

    public static final String FUNCTION＿NAME = "FUNCTIONNAME";

    private PaymentButton buttonPaymentDBS;
    private PaymentButton buttonPaymentOthers;
    private PaymentButton buttonPaymentConvenientStore;

    private int tempMinAmountThisMonth = 10000; //測試用數值

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).setCenterTitle(R.string.center_title_payment);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((ActivityBase) getActivity()).setHeadHide(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_home, container, false);

        buttonPaymentDBS = (PaymentButton)view.findViewById(R.id.button_payment_DBS);
        buttonPaymentOthers = (PaymentButton)view.findViewById(R.id.button_payment_others);
        buttonPaymentConvenientStore = (PaymentButton)view.findViewById(R.id.button_payment_convenient_store);

        initButton(buttonPaymentDBS, R.drawable.ic_payment_dbs
                , R.string.payment_DBS_main_title, R.string.payment_DBS_sub_title);
        initButton(buttonPaymentOthers, R.drawable.ic_payment_others
                , R.string.payment_others_main_title, R.string.payment_others_sub_title);
        initButton(buttonPaymentConvenientStore, R.drawable.ic_payment_convenient_store
                , R.string.payment_convenient_store_main_title, R.string.payment_convenient_store_sub_title);

        return view;
    }

    private void initButton(PaymentButton button, int iconId, int mainTitleId, int subTitleId){
        button.setIcon(iconId);
        button.setMainTitle(mainTitleId);
        button.setSubTitle(subTitleId);
        button.setTag(mainTitleId);
        button.setOnClickListener(btnListener);
    }

    private Button.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int)v.getTag();
            Intent intent = new Intent();

            switch (tag) {

                //星展銀行帳戶繳款
                case R.string.payment_DBS_main_title:
                    if(OmniApplication.sBillOverview == null || TextUtils.isEmpty(OmniApplication.sBillOverview.getAmtCurrDue()) || TextUtils.isEmpty(OmniApplication.sBillOverview.getAmtMinPayment())){
                        ((ActivityBase) getActivity()).showAlertDialog(getActivity().getString(R.string.alert_null_bill_object), android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, true);
                    }else{
                        intent.setClass(getActivity() , BankPaymentActivity.class);
                        intent.putExtra(FUNCTION＿NAME , R.string.payment_DBS_main_title);
                        startActivity(intent);
                    }

                    break;

                //他行帳戶繳款
                case R.string.payment_others_main_title:
                    if(OmniApplication.sBillOverview == null || TextUtils.isEmpty(OmniApplication.sBillOverview.getAmtCurrDue()) || TextUtils.isEmpty(OmniApplication.sBillOverview.getAmtMinPayment())){
                        ((ActivityBase) getActivity()).showAlertDialog(getActivity().getString(R.string.alert_null_bill_object), android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, true);
                    }else{
                        if(OmniApplication.sBillOverview.getDoubleAmtMinPayment() > 100000){
                            ((ActivityBase) getActivity()).showAlertDialog(getActivity().getString(R.string.alert_minimum_price_greater_than_100000), android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, true);
                        }else {
                            intent.setClass(getActivity(), BankPaymentActivity.class);
                            intent.putExtra(FUNCTION＿NAME, R.string.payment_others_main_title);
                            startActivity(intent);
                        }
                    }

                    break;

                //超商繳款
                case R.string.payment_convenient_store_main_title:
                    if(OmniApplication.sBillOverview == null || TextUtils.isEmpty(OmniApplication.sBillOverview.getAmtCurrDue()) || TextUtils.isEmpty(OmniApplication.sBillOverview.getAmtMinPayment())){
                        ((ActivityBase) getActivity()).showAlertDialog(getActivity().getString(R.string.alert_null_bill_object), android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, true);
                    }else{

                        if(OmniApplication.sBillOverview.getDoubleAmtMinPayment() > 20000){
                            ((ActivityBase) getActivity()).showAlertDialog(getActivity().getString(R.string.alert_minimum_price_greater_than_20000), android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, true);
                        }else{
                            intent.setClass(getActivity() , ConvenientStorePaymentActivity.class);
                            intent.putExtra(ConvenientStorePaymentActivity.EXTRA_MAX_PAID, OmniApplication.sBillOverview.getDoubleAmtCurrDue());
                            intent.putExtra(ConvenientStorePaymentActivity.EXTRA_MIN_PAID, OmniApplication.sBillOverview.getDoubleAmtMinPayment());
                            startActivity(intent);
                        }
                    }

                    break;
            }
        }
    };


}
