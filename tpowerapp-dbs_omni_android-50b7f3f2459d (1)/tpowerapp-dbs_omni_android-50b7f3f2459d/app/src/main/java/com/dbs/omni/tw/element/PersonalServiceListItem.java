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

public class PersonalServiceListItem extends RelativeLayout {

    private ImageView imageIcon;
    private TextView textTitle;

    public PersonalServiceListItem(Context context) {
        super(context);
        init();
    }

    public PersonalServiceListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        View view = inflate(getContext(), R.layout.element_personal_service_list_item, this);
        imageIcon = (ImageView) view.findViewById(R.id.image_icon);
        textTitle = (TextView) view.findViewById(R.id.text_title);
    }

    public void setIcon(int iconID) {
        imageIcon.setImageResource(iconID);
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
