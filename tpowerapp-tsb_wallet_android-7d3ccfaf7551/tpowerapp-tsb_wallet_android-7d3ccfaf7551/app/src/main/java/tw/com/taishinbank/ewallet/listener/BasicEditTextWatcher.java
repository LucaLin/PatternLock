package tw.com.taishinbank.ewallet.listener;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.util.FormatUtil;

/**
 * 根據輸入內容變化背景與右側drawable樣式的TextWatcher
 */
public class BasicEditTextWatcher implements TextWatcher {

    public static int EDIT_LEVEL_NORMAL;
    public static int EDIT_LEVEL_CORRECT;
    public static int EDIT_LEVEL_ERROR;
    protected Drawable backgroundDrawable;
    protected Drawable rightDrawable;
    private String inputFormatRegularExpression;

    /**
     * @param editText 要監看的EditText
     * @param inputFormatRegularExpression 用來判斷輸入文字格式是否正確的regular expression字串
     */
    public BasicEditTextWatcher(EditText editText, String inputFormatRegularExpression){
        Resources resources = editText.getContext().getResources();
        EDIT_LEVEL_NORMAL = resources.getInteger(R.integer.edit_level_normal);
        EDIT_LEVEL_CORRECT = resources.getInteger(R.integer.edit_level_correct);
        EDIT_LEVEL_ERROR = resources.getInteger(R.integer.edit_level_error);

        backgroundDrawable = editText.getBackground();
        // []順序: left, top, right, bottom
        rightDrawable = editText.getCompoundDrawables()[2];

        this.inputFormatRegularExpression = inputFormatRegularExpression;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 如果有輸入
        if (s != null && s.length() > 0) {
            // 如果是正確格式，狀態為Correct
            if (FormatUtil.isCorrectFormat(s, inputFormatRegularExpression)) {
                setLevel(EDIT_LEVEL_CORRECT);
            } else {// 否則為Error
                setLevel(EDIT_LEVEL_ERROR);
            }
        } else {// 如果沒有輸入，狀態為Normal
            setLevel(EDIT_LEVEL_NORMAL);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    protected void setLevel(int level){
        backgroundDrawable.setLevel(level);
        rightDrawable.setLevel(level);
    }

    protected int getLevel(){
        return backgroundDrawable.getLevel();
    }
}
