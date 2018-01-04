package com.dbs.omni.tw.element;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;

public class NoteBar extends RelativeLayout {

    private TextView mTextContent;
    private ImageButton mButtonCancel;
    private RelativeLayout mBackground;


    public enum ENUM_NOTE_TYPE {
        ADVERTISEMENT,
        NOTICE
    }

    public NoteBar(Context context) {
        super(context);
        init();
    }

    public NoteBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteBar(Context context, String content, OnClickListener onCancelClickListener, ENUM_NOTE_TYPE type) {
        super(context);
        init();

        setContent(content);
        setOnCancelClickListener(onCancelClickListener);
        setStyle(type);

        this.setTag(type);
    }

    public NoteBar(Context context, String content, String adLink , OnClickListener onCancelClickListener, ENUM_NOTE_TYPE type) {
        super(context);
        init();

        setContent(content);
        setURL(adLink);
        setOnCancelClickListener(onCancelClickListener);
        setStyle(type);

        this.setTag(type);
    }

    private void init() {
        final View view = inflate(getContext(), R.layout.element_note_bar, this);
        mTextContent = (TextView) view.findViewById(R.id.text_note_content);
        mButtonCancel = (ImageButton) view.findViewById(R.id.button_cancel);
        mBackground = (RelativeLayout) view.findViewById(R.id.relativeLayout_bg);

        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    public void setContent(String string) {
        mTextContent.setText(string);
    }

    public void setContent(int stringID) {
        mTextContent.setText(stringID);
    }

    public String getContent() {
        return mTextContent.getText().toString();
    }

    public void setStyle(ENUM_NOTE_TYPE type) {
        switch (type) {
            case ADVERTISEMENT:
                mBackground.setBackgroundResource(R.color.ad_background_color);
                mTextContent.setPaintFlags(mTextContent.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                break;

            case NOTICE:
                mBackground.setBackgroundResource(R.color.notice_background_color);
                break;
        }
    }

    public void setOnCancelClickListener(OnClickListener listener) {
        mButtonCancel.setOnClickListener(listener);
    }


    public void setURL(String url) {

        mTextContent.setText(Html.fromHtml("<a href=\""+url+"\">"+mTextContent.getText()+"</a>"));
        mTextContent.setMovementMethod(LinkMovementMethod.getInstance());
        mTextContent.setLinkTextColor(Color.WHITE);
    }
}
