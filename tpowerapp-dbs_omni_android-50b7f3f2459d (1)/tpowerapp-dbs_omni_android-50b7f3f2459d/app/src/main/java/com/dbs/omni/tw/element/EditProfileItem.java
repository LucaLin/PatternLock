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

public class EditProfileItem extends RelativeLayout {

    private TextView textTitle;
    private TextView textContent;
    private TextView textWaitForUpdate;

    public EditProfileItem(Context context) {
        super(context);
        init();
    }

    public EditProfileItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        View view = inflate(getContext(), R.layout.element_edit_profile_item, this);
        textTitle = (TextView) view.findViewById(R.id.text_title);
        textContent = (TextView) view.findViewById(R.id.text_content);
        textWaitForUpdate = (TextView) view.findViewById(R.id.text_wait_for_update);
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

    public void turnOnTextWaitForUpdate(){
        textWaitForUpdate.setVisibility(VISIBLE);
    }

    public void turnOffTextWaitForUpdate(){
        textWaitForUpdate.setVisibility(INVISIBLE);
    }

}
