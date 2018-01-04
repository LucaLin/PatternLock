package com.dbs.omni.tw.element;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;

/**
 * Created by siang on 2017/4/17.
 */

public class InstallmentButtonItem extends RelativeLayout {

    private RelativeLayout relativeLayout;
    private TextView textView_month;
    private TextView textView_unit;

    public InstallmentButtonItem(Context context) {
        super(context);
        init();
    }

    public InstallmentButtonItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        View view = inflate(getContext(), R.layout.element_installment_button_item, this);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout_bg);
        textView_month = (TextView) view.findViewById(R.id.textView_month);
        textView_unit = (TextView) view.findViewById(R.id.textView_unit);
    }

    public void setBackground(int backgroundID){
        relativeLayout.setBackgroundResource(backgroundID);
    }

    public void setTextView(String string) {
        textView_month.setText(string);
    }

    public void setTextView(int stringID) {
        textView_month.setText(stringID);
    }

    public String getTextView() {
        return textView_month.getText().toString();
    }

    public void setTextViewColor(int colorID) {
        textView_month.setTextColor(ContextCompat.getColor(getContext(), colorID));

        //由於Leo白色按鈕的文字顏色上下不同...所以用判斷式塞顏色
        if(colorID == R.color.installment_button_upside_text_color){
            textView_unit.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrayPrimaryMedium));
        }else{
            textView_unit.setTextColor(ContextCompat.getColor(getContext(), colorID));
        }
    }
}
