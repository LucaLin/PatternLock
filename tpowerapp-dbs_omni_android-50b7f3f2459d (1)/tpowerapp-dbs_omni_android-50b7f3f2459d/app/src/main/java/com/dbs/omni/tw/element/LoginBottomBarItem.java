package com.dbs.omni.tw.element;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;


public class LoginBottomBarItem extends LinearLayout {

    private TextView textTitle;
    private ImageView imageIcon;

    public LoginBottomBarItem(Context context) {
        super(context);
        init();
    }

    public LoginBottomBarItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.element_login_bottom_bar_item, this);
        textTitle = (TextView) view.findViewById(R.id.text_title);
        imageIcon = (ImageView) view.findViewById(R.id.image_icon);
    }

    public void setText(String string) {
        textTitle.setText(string);
    }

    public void setText(int id) {
        textTitle.setText(id);
    }

    public String getText() {
        return  textTitle.getText().toString();
    }

    public void setBackgroundResource (int id) {
        imageIcon.setBackgroundResource(id);
    }

    public void setBackground (Drawable drawable) {
        imageIcon.setBackground(drawable);
    }
}
