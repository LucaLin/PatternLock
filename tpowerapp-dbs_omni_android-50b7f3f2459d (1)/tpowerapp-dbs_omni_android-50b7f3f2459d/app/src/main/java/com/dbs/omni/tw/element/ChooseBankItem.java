package com.dbs.omni.tw.element;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;

/**
 * Created by siang on 2017/4/17.
 */

public class ChooseBankItem extends RelativeLayout {

    private ImageView imageView_Bank_Icon;
    private TextView text_balance_not_enough;
    private TextView text_account_name;
    private TextView text_balance;
    private TextView text_account_number;

    public ChooseBankItem(Context context) {
        super(context);
        init();
    }

    public ChooseBankItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        View view = inflate(getContext(), R.layout.element_choose_bank, this);
        imageView_Bank_Icon = (ImageView) view.findViewById(R.id.imageView_bank_icon);
        text_balance_not_enough = (TextView) view.findViewById(R.id.text_balance_not_enough);
        text_account_name = (TextView) view.findViewById(R.id.text_account_name);
        text_balance = (TextView) view.findViewById(R.id.text_balance);
        text_account_number = (TextView) view.findViewById(R.id.text_account_number);
    }

    public void setIcon(int iconID) {
        imageView_Bank_Icon.setImageResource(iconID);
    }

    public void setTextBalanceEnough(String string) {
        text_balance_not_enough.setText(string);
    }

    public void setTextBalanceEnough(int stringID) {
        text_balance_not_enough.setText(stringID);
    }

    public String getTextBalanceEnough() {
        return text_balance_not_enough.getText().toString();
    }

    public void setTextAccountName(String string) {
        text_account_name.setText(string);
    }

    public void setTextAccountName(int stringID) {
        text_account_name.setText(stringID);
    }

    public String getTextAccountName() {
        return text_account_name.getText().toString();
    }

    public void setTextBalance(String string) {
        text_balance.setText(string);
    }

    public void setTextBalance(int stringID) {
        text_balance.setText(stringID);
    }

    public String getTextBalance() {
        return text_balance.getText().toString();
    }

    public void setTextAccountNumber(String string) {
        text_account_number.setText(string);
    }

    public void setTextAccountNumber(int stringID) {
        text_account_number.setText(stringID);
    }

    public String getTextAccountNumber() {
        return text_account_number.getText().toString();
    }

//    public void setHint(String string) {
//        textContent.setHint(string);
//    }
//
//
//    public void setHint(int stringID) {
//        textContent.setHint(stringID);
//    }

//    public String getContent() {
//        return textContent.getContent().toString();
//    }
//
//    public void addTextChangedListener(TextWatcher textWatcher){
//        textContent.addTextChangedListener(textWatcher);
//    }
//
//    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
//        textContent.setOnFocusChangeListener(onFocusChangeListener);
//    }
//
//    public void setMaxLength(int maxLength) {
//        textContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
//    }
//
//    public void setInputType (int type) {
//        textContent.setInputType(type);
//    }
//
//    public void setDigits(String string) {
//        textContent.setKeyListener(DigitsKeyListener.getInstance(string));
//    }
}
