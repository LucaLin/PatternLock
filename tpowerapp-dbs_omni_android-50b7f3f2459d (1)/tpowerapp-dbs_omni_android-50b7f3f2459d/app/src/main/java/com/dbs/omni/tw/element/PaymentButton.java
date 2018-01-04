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

public class PaymentButton extends RelativeLayout {

    private ImageView imageIcon;
    private TextView textMainTitle;
    private TextView textSubTitle;

    public PaymentButton(Context context) {
        super(context);
        init();
    }

    public PaymentButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        View view = inflate(getContext(), R.layout.element_payment_button, this);
        imageIcon = (ImageView) view.findViewById(R.id.imageView_icon);
        textMainTitle = (TextView) view.findViewById(R.id.textView_main_title);
        textSubTitle = (TextView) view.findViewById(R.id.textView_sub_title);
    }

    public void setIcon(int iconID) {
        imageIcon.setImageResource(iconID);
    }

    public void setMainTitle(String string) {
        textMainTitle.setText(string);
    }

    public void setMainTitle(int stringID) {
        textMainTitle.setText(stringID);
    }

    public String getMainTitle() {
        return textMainTitle.getText().toString();
    }

    public void setSubTitle(String string) {
        textSubTitle.setText(string);
    }

    public void setSubTitle(int stringID) {
        textSubTitle.setText(stringID);
    }

    public String getSubTitle() {
        return textSubTitle.getText().toString();
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
