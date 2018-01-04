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

public class BarcodeItem extends RelativeLayout {

    private ImageView image_barcode;
    private TextView textview_barcode;

    public BarcodeItem(Context context) {
        super(context);
        init();
    }

    public BarcodeItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        View view = inflate(getContext(), R.layout.element_barcode_item, this);
        image_barcode = (ImageView) view.findViewById(R.id.image_barcode);
        textview_barcode = (TextView) view.findViewById(R.id.textView_barcode);
    }

    public void setImage(int iconID) {
        image_barcode.setImageResource(iconID);
    }

    public void setContent(String string) {
        textview_barcode.setText(string);
    }

    public void setContent(int stringID) {
        textview_barcode.setText(stringID);
    }

    public String getContent() {
        return textview_barcode.getText().toString();
    }

    public ImageView getImageView() {
        return image_barcode;
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
