package com.dbs.omni.tw.element;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.model.setting.ApplyCreditCardData;

/**
 * Created by siang on 2017/4/17.
 */

public class CreditCardItemView extends LinearLayout {

    private ImageButton buttonSelectCard;
    private ImageView imageSelected;
    private TextView textCardName;
    private View view;
    private int index; // 第幾個

    public CreditCardItemView(Context context) {
        super(context);
        init();
    }

    public CreditCardItemView(Context context, ApplyCreditCardData data, int index) {
        super(context);
        init();

        setIndex(index);
        setTextCardName(data.getCardName());
        setOnItemEventListener(data.getOnItemEventListener());
        view.setVisibility(VISIBLE);
    }

    public void setCreditCardItem(ApplyCreditCardData data, int index) {
        setIndex(index);
        setTextCardName(data.getCardName());
        setOnItemEventListener(data.getOnItemEventListener());
    }

    public CreditCardItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private OnItemEventListener onItemEventListener;

    public interface OnItemEventListener {
        void OnSelectItem(int index);
        void OnShowMore(int index);
    }

    public void setOnItemEventListener(OnItemEventListener listener) {
        onItemEventListener = listener;
    }

    private void init() {
        view = inflate(getContext(), R.layout.element_credit_card, this);
        buttonSelectCard = (ImageButton) findViewById(R.id.button_card_bg);
        buttonSelectCard.setOnClickListener(buttonSelectedListener);

        imageSelected = (ImageView) findViewById(R.id.image_selected);

        textCardName = (TextView)findViewById(R.id.text_card_name);
        textCardName.setOnClickListener(textCardNameListener);
        textCardName.setPaintFlags(textCardName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void setTextCardName(int stringID) {
        textCardName.setText(stringID);
    }

    public void setTextCardName(String string) {
        textCardName.setText(string);
    }

    public void setCardFace(int imageID) { buttonSelectCard.setBackgroundResource(imageID); }

    public void setSelcetListener(OnClickListener listener) {
        imageSelected.setOnClickListener(listener);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Button.OnClickListener buttonSelectedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            imageSelected.setVisibility(VISIBLE);
            onItemEventListener.OnSelectItem(index);
        }
    };

    public TextView.OnClickListener textCardNameListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onItemEventListener.OnShowMore(index);
        }
    };

    public void cancelSelect() {
        imageSelected.setVisibility(GONE);
    }
}
