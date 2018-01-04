package com.dbs.omni.tw.element.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by siang on 2017/6/9.
 */

public class CardNumberTextWatcher implements TextWatcher {

    int currInt = 0;
    String beforeText = "";

    private EditText editText;
    public CardNumberTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        String text = editText.getText().toString();
        beforeText = text;
        text = text.replace("-", "");
        currInt = text.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = editText.getText().toString();
        String afterText = text;
        text = text.replace("-", "");
        if ((text.length() % 4 == 0) && ( text.length() > currInt )) {
            editText.setText(editText.getText() + "-");
            editText.setSelection(editText.length());
        }
        else if(( afterText.length() < beforeText.length() )) // 長度包含-
        {
            String delText = beforeText.replace(s, "");
            if(delText.equals("-"))
            {
                afterText =  afterText.substring(0, afterText.length()-1);
                editText.setText(afterText);
                editText.setSelection(editText.length());
            }
        }
    }
}
