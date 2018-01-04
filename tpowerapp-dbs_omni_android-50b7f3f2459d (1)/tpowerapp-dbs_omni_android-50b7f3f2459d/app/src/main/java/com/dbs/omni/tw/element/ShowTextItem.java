package com.dbs.omni.tw.element;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;

/**
 * Created by siang on 2017/4/17.
 */

public class ShowTextItem extends RelativeLayout {

    private TextView textTitle;
    private TextView textContent;

    public ShowTextItem(Context context) {
        super(context);
        init();
    }

    public ShowTextItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        View view = inflate(getContext(), R.layout.element_show_text, this);
        textTitle = (TextView) view.findViewById(R.id.text_title);
        textContent = (TextView) view.findViewById(R.id.text_content);
    }

    public void setTitle(String string) {
        textTitle.setText(string);
    }

    public void setTitle(int stringID) {
        textTitle.setText(stringID);
    }

    public String getTitle() {
        return textTitle.getText().toString();
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
