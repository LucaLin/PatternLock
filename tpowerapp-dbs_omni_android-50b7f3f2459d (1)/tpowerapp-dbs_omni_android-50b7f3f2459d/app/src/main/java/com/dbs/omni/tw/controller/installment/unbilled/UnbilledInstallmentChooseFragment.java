package com.dbs.omni.tw.controller.installment.unbilled;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.element.InstallmentButtonItem;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnbilledInstallmentChooseFragment extends Fragment {

    public static final String TAG = "InstallmentChooseFragment";

    private TextView textview_installment_amount, textview_number_of_periods;
    private TextView textview_annual_interest_rate, textview_installment_amount_per_period;
    private InstallmentButtonItem button_3_month, button_6_month, button_12_month;
    private InstallmentButtonItem button_18_month, button_24_month, button_36_month;
    private Button btnNextStep;

    private UnbilledInstallmentChooseFragment.OnEventListener onEventListener;

    public interface OnEventListener {
        void OnNextEvent(ArrayList<String> list);
    }

    public void setOnEventListener (UnbilledInstallmentChooseFragment.OnEventListener listener) {
        this.onEventListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_unbilled_installment_choose, container, false);

        textview_installment_amount = (TextView)view.findViewById(R.id.textview_installment_amount);
        textview_number_of_periods = (TextView)view.findViewById(R.id.textview_number_of_periods);
        textview_annual_interest_rate = (TextView)view.findViewById(R.id.textview_annual_interest_rate);
        textview_installment_amount_per_period = (TextView)view.findViewById(R.id.textview_installment_amount_per_period);

        button_3_month = (InstallmentButtonItem)view.findViewById(R.id.button_3_month);
        button_6_month = (InstallmentButtonItem)view.findViewById(R.id.button_6_month);
        button_12_month = (InstallmentButtonItem)view.findViewById(R.id.button_12_month);
        button_18_month = (InstallmentButtonItem)view.findViewById(R.id.button_18_month);
        button_24_month = (InstallmentButtonItem)view.findViewById(R.id.button_24_month);
        button_36_month = (InstallmentButtonItem)view.findViewById(R.id.button_36_month);

        button_3_month.setTextView("3");
        button_6_month.setTextView("6");
        button_12_month.setTextView("12");
        button_18_month.setTextView("18");
        button_24_month.setTextView("24");
        button_36_month.setTextView("36");

        button_3_month.setOnClickListener(btnListener);
        button_6_month.setOnClickListener(btnListener);
        button_12_month.setOnClickListener(btnListener);
        button_18_month.setOnClickListener(btnListener);
        button_24_month.setOnClickListener(btnListener);
        button_36_month.setOnClickListener(btnListener);

        btnNextStep = (Button)view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnNextStepListener);

        return view;
    }

    private Button.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_3_month:
                    resetAllButton();
                    buttonPressed(button_3_month);
                    break;

                case R.id.button_6_month:
                    resetAllButton();
                    buttonPressed(button_6_month);
                    break;

                case R.id.button_12_month:
                    resetAllButton();
                    buttonPressed(button_12_month);
                    break;

                case R.id.button_18_month:
                    resetAllButton();
                    buttonPressed(button_18_month);
                    break;

                case R.id.button_24_month:
                    resetAllButton();
                    buttonPressed(button_24_month);
                    break;

                case R.id.button_36_month:
                    resetAllButton();
                    buttonPressed(button_36_month);
                    break;
            }

        }
    };

    private void resetAllButton(){
        button_3_month.setBackground(R.drawable.bg_installment_button_white);
        button_6_month.setBackground(R.drawable.bg_installment_button_white);
        button_12_month.setBackground(R.drawable.bg_installment_button_white);
        button_18_month.setBackground(R.drawable.bg_installment_button_white);
        button_24_month.setBackground(R.drawable.bg_installment_button_white);
        button_36_month.setBackground(R.drawable.bg_installment_button_white);

        button_3_month.setTextViewColor(R.color.installment_button_upside_text_color);
        button_6_month.setTextViewColor(R.color.installment_button_upside_text_color);
        button_12_month.setTextViewColor(R.color.installment_button_upside_text_color);
        button_18_month.setTextViewColor(R.color.installment_button_upside_text_color);
        button_24_month.setTextViewColor(R.color.installment_button_upside_text_color);
        button_36_month.setTextViewColor(R.color.installment_button_upside_text_color);
    }

    private void buttonPressed(InstallmentButtonItem button){
        button.setBackground(R.drawable.bg_installment_button_red);
        button.setTextViewColor(android.R.color.white);

        textview_number_of_periods.setText(button.getTextView() + getString(R.string.per_month));
    }

    private Button.OnClickListener btnNextStepListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<String> list = new ArrayList<>();
            list.add(textview_installment_amount.getText().toString());
            list.add(textview_number_of_periods.getText().toString());
            list.add(textview_annual_interest_rate.getText().toString());
            list.add(textview_installment_amount_per_period.getText().toString());

            onEventListener.OnNextEvent(list);
        }
    };

}
