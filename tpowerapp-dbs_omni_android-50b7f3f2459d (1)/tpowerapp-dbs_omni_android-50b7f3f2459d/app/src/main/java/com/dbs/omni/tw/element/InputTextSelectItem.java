package com.dbs.omni.tw.element;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;

/**
 * Created by siang on 2017/4/17.
 */

public class InputTextSelectItem extends LinearLayout {

    private TextView textAmount;
    private TextView textContent;

    public InputTextSelectItem(Context context) {
        super(context);
        init();
    }

    public InputTextSelectItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        View view = inflate(getContext(), R.layout.inputtext_select_item, this);
        textAmount = (TextView) view.findViewById(R.id.text_amount);
        textContent = (TextView) view.findViewById(R.id.text_content);
    }

    public void setAmount(String string) {
        textAmount.setText(string);
    }

    public void setAmount(int stringID) {
        textAmount.setText(stringID);
    }

    public String getAmount() {
        return textAmount.getText().toString();
    }

    public void setContent(String string) {
        textContent.setText(string);
    }

    public void setContent(int stringID) {
        textContent.setText(stringID);
    }

    public String getContent() {
        return textContent.getText().toString();
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
