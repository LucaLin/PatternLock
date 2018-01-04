package com.dbs.omni.tw.controller.installment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.installment.setting.InstallmentSettingActivity;
import com.dbs.omni.tw.controller.installment.unbilled.UnbilledInstallmentActivity;
import com.dbs.omni.tw.element.PaymentButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstallmentHomeButtonFragment extends Fragment {

    private PaymentButton buttonUnbilledInstallment;
    private PaymentButton buttonInstallmentSetting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_installment_home_button, container, false);

        buttonUnbilledInstallment = (PaymentButton)view.findViewById(R.id.button_unbilled_installment);
        buttonInstallmentSetting = (PaymentButton)view.findViewById(R.id.button_installment_setting);

        initButton(buttonUnbilledInstallment, R.drawable.ic_unbilled_installment
                , R.string.installment_unbilled_main_title, R.string.installment_unbilled_sub_title);
        initButton(buttonInstallmentSetting, R.drawable.ic_installment_setting
                , R.string.installment_setting_main_title, R.string.installment_setting_sub_title);

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
                //未出帳交易分期
                case R.string.installment_unbilled_main_title:
                    intent.setClass(getActivity(), UnbilledInstallmentActivity.class);
                    startActivity(intent);
                    break;

                //自動分期設定
                case R.string.installment_setting_main_title:
                    intent.setClass(getActivity(), InstallmentSettingActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

}
