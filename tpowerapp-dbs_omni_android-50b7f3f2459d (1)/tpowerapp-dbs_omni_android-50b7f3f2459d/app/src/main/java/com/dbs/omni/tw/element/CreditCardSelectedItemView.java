package com.dbs.omni.tw.element;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.model.setting.ApplyCreditCardData;

/**
 * Created by siang on 2017/4/17.
 */

public class CreditCardSelectedItemView extends LinearLayout {

    private ImageView imageCard;
    private ImageButton buttonCancel;
    private TextView textCardName;
    private View view;

    private int selectIndex; // 第幾個

    public CreditCardSelectedItemView(Context context) {
        super(context);
        init();
    }

    public CreditCardSelectedItemView(Context context, ApplyCreditCardData data, int selectIndex) {
        super(context);
        init();

        setSelectIndex(selectIndex);
        setTextCardName(data.getCardName());
    }

    public void setCreditCardItem(ApplyCreditCardData data, int index) {
        setSelectIndex(index);
        setTextCardName(data.getCardName());
    }

    public CreditCardSelectedItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private OnItemCancelListener onItemCancelListener;

    public interface OnItemCancelListener {
        void OnCancelSelect(View view, int selectIndex);
    }

    public void setOnItemCancelListener(OnItemCancelListener listener) {
        onItemCancelListener = listener;
    }

    private void init() {
        view = inflate(getContext(), R.layout.element_credit_card_for_selected, this);

        imageCard = (ImageView) findViewById(R.id.image_card_bg);
        buttonCancel = (ImageButton) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(buttonCancelListener);


        textCardName = (TextView)findViewById(R.id.text_card_name);
    }

    public void setTextCardName(int stringID) {
        textCardName.setText(stringID);
    }

    public void setTextCardName(String string) {
        textCardName.setText(string);
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public OnClickListener buttonCancelListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onItemCancelListener.OnCancelSelect(view ,selectIndex);
        }
    };

    public void isEnableCancelButton(boolean isEnable) {
        if(isEnable) {
            buttonCancel.setVisibility(VISIBLE);
        } else {
            buttonCancel.setVisibility(GONE);
        }
    }

}
