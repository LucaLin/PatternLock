package com.dbs.omni.tw.element.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by siang on 2017/6/9.
 */

public class EffectiveDateTextWatcher implements TextWatcher {

    String beforeText = "";
    int currInt = 0;
    boolean isMonthS = false;

    private EditText editText;
    public EffectiveDateTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        String text = editText.getText().toString();
        beforeText = text;
        if(beforeText.length() == 1 && beforeText.equals("1"))
        {
            isMonthS = true;
        }


        text = text.replace("/", "");
        currInt = text.length();


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = editText.getText().toString();
//                int afterInt = text.length();
        if(( text.length() > beforeText.length() )) {
            if(beforeText.length() == 0 && (text.length() - beforeText.length() == 1))
            {
                if(Integer.valueOf(text) > 1)
                {
                    text = "0" + text;
                    editText.setText(text);
                    return;
                }

            }
            else if(isMonthS)
            {
                if(text.length() == 2 && Integer.valueOf(text) > 12)
                {
                    editText.setText(beforeText);
                    editText.setSelection(editText.length());
                    return;
                }
            }

        }


        if(text.length() > currInt && text.length() == 2) {
            editText.setText(text + "/");
            editText.setSelection(editText.length());
        }
        else if(( text.length() < beforeText.length() )) // 長度包含-
        {
            String delText = beforeText.replace(s, "");
            if(delText.equals("/"))
            {
                text = text.substring(0, text.length()-1);
                editText.setText(text);
                editText.setSelection(editText.length());
            }
        }
    }
}
