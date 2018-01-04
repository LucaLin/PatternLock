package tw.com.taishinbank.ewallet.util;

import android.hardware.fingerprint.FingerprintManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListResourceBundle;

/**
 * Created by Siang on 3/22/16.
 */
public class InputFilterUtil {

    public enum ENUM_FILTER_TYPE {
        FILTER_SPACE,
        FILTER_WRAP
    }

    public static void setFilter(EditText editText, ENUM_FILTER_TYPE[] types) {
        ArrayList<InputFilter> filterList = new ArrayList<>(Arrays.asList(editText.getFilters()));
        for (ENUM_FILTER_TYPE type: types) {
            switch (type)
            {
                case FILTER_SPACE:
                    filterList.add(filterSpace());
                    break;
                case FILTER_WRAP:
                    filterList.add(filterWrap());
                    break;
            }
        }

        editText.setFilters(filterList.toArray( new InputFilter[0]));
    }

    private static InputFilter filterSpace() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(dest.length() == 0) {
                    if(source.toString().equals(" "))
                        return "";
                }
                return null;
            }
        };
        return filter;
    }

    private static InputFilter filterWrap() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String text = source.toString();
                if(text.contains("\n")) {
                    text = text.replace("\n", "");
                    return text;
                }
                return null;
            }
        };
        return filter;
    }
}
