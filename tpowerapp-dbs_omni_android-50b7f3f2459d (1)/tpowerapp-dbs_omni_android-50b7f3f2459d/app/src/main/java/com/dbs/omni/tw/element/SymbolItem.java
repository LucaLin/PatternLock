package com.dbs.omni.tw.element;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;


public class SymbolItem extends LinearLayout {

    private TextView textContent;
    private TextView textPoint;

    private String mAttrText;
    private boolean isFail = false;

    public SymbolItem(Context context) {
        super(context);
        init();
    }

    public SymbolItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.SymbolItem);
        mAttrText = styledAttrs.getString(R.styleable.SymbolItem_textContent);

        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.element_symbol_item, this);
        textContent = (TextView) view.findViewById(R.id.text_content);
        textPoint = (TextView) view.findViewById(R.id.text_point);
        if(!TextUtils.isEmpty(mAttrText)) {
            textContent.setText(mAttrText);
        }
    }

    public void setTextContent(String string) {
        textContent.setText(string);
    }

    public void setText(int id) {
        textContent.setText(id);
    }

    public String getTextContent() {
        return  textContent.getText().toString();
    }

    public TextView getView(){return textContent;}

    public void setFail(boolean isFail) {
        this.isFail = isFail;
        if(isFail) {
            textContent.setTextColor(ContextCompat.getColor(getContext() ,R.color.colorRedPrimary));
            textPoint.setTextColor(ContextCompat.getColor(getContext(),R.color.colorRedPrimary));
        } else {
            textContent.setTextColor(ContextCompat.getColor(getContext() ,R.color.colorGrayPrimaryMedium));
            textPoint.setTextColor(ContextCompat.getColor(getContext() ,R.color.colorGrayPrimaryMedium));
        }
    }

    public boolean isFail() {
        return isFail;
    }
}
