package com.dbs.omni.tw.element;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.textwatcher.CardNumberTextWatcher;
import com.dbs.omni.tw.element.textwatcher.DateTextWatcher;
import com.dbs.omni.tw.element.textwatcher.EffectiveDateTextWatcher;
import com.dbs.omni.tw.model.element.SelectItemData;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.ValidateUtil;
import com.dbs.omni.tw.util.sharedMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by siang on 2017/4/17.
 */

public class InputTextView extends RelativeLayout {
    private static final String TAG = "InputTextiew";

    private static final String EXTRA_EDIT_CONTENT = "EXTRA_EDIT_CONTENT";
    private static final String EXTRA_SUB_CONTENT = "EXTRA_SUB_CONTENT";
    private static final String EXTRA_AMOUNT_SING = "EXTRA_AMOUNT_SING";
    private static final String EXTRA_SHOW_DECIMAL_FORMAT = "EXTRA_SHOW_DECIMAL_FORMAT";
    private static final String EXTRA_SHOW_KEYBORUND = "EXTRA_SHOW_KEYBORUND";
    private static final String EXTRA_IS_VALIDTEPASS = "EXTRA_IS_VALIDTEPASS";

    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putString(EXTRA_EDIT_CONTENT, mEditContent.getText().toString());
        bundle.putString(EXTRA_SUB_CONTENT, mTextSubContent.getText().toString());
        bundle.putString(EXTRA_AMOUNT_SING, mTextAmountSign.getText().toString());
        bundle.putBoolean(EXTRA_SHOW_DECIMAL_FORMAT, mShowDecimalFormat);
        bundle.putBoolean(EXTRA_SHOW_KEYBORUND, mShowKeyborund);
        bundle.putBoolean(EXTRA_IS_VALIDTEPASS, mIsValidatePass);
        return bundle;

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mEditContent.setText(bundle.getString(EXTRA_EDIT_CONTENT));
            mTextSubContent.setText(bundle.getString(EXTRA_SUB_CONTENT));
            mTextAmountSign.setText(bundle.getString(EXTRA_AMOUNT_SING));
            mShowDecimalFormat = bundle.getBoolean(EXTRA_SHOW_DECIMAL_FORMAT);
            mShowKeyborund = bundle.getBoolean(EXTRA_SHOW_KEYBORUND);
            mIsValidatePass = bundle.getBoolean(EXTRA_IS_VALIDTEPASS);

            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    private OnCityAndRegionListener onCityAndRegionListener;

    public void setOnCityAndRegionListener(OnCityAndRegionListener listener){
        onCityAndRegionListener = listener;
    }

    public interface OnCityAndRegionListener {
        void OnCityFinish(String city);
        void OnRegionFinish(String region, String postal);
    }

    public interface OnSelectListener {
        void onSelect(SelectItemData item);
    }
    private OnSelectListener onSelectListener;
    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnActionListener {
        void onEditorAction(TextView v, int actionId, KeyEvent event);
    }
    private OnActionListener onActionListener;
    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    private OnChangeEditContentListener onChangeEditContentListener;

    public void setOnChangeEditContentListener(OnChangeEditContentListener onChangeEditContentListener) {
        this.onChangeEditContentListener = onChangeEditContentListener;
    }

    public interface OnChangeEditContentListener {
        void OnOtherItem();
    }

    private OnValidateListener onValidateListener;

    public void setOnValidateListener(OnValidateListener listener) {
        onValidateListener = listener;
    }

    public interface OnValidateListener {
        void OnPass();
        void OnFail();
    }

    private OnFinishEditListener onFinishEditListener;

    public void setOnFinishEdit(OnFinishEditListener listener) {
        onFinishEditListener = listener;
    }

    public interface OnFinishEditListener {
        void OnFinish();
    }

    public enum InputTextType {
        None(0),
        UserID(1),
        Amount(2),
        CreditCard(3),
        EffectiveDate(4),
        BirthDate(5),
        Password(6),
        City(7),
        Region(8),
        NickName(9),
        UserAccount(10),
        Email(11),
        PhoneNumber(12),
        Display(13)
        ;

        public int getValue()
        {
            return value;
        }

        private int value;

        InputTextType(int value)
        {
            this.value = value;
        }

        public static InputTextType valueOf(int id) {
            if(id == UserID.getValue()) {
                return UserID;
            } else if(id == Amount.getValue()) {
                return Amount;
            } else if(id == CreditCard.getValue()) {
                return CreditCard;
            }  else if(id == EffectiveDate.getValue()) {
                return EffectiveDate;
            } else if(id == BirthDate.getValue()) {
                return BirthDate;
            } else if(id == Password.getValue()) {
                return Password;
            } else if(id == City.getValue()) {
                return City;
            } else if(id == Region.getValue()) {
                return Region;
            } else if(id == NickName.getValue()) {
                return NickName;
            } else if(id == UserAccount.getValue()) {
                return UserAccount;
            } else if(id == Email.getValue()) {
                return Email;
            } else if(id == PhoneNumber.getValue()) {
                return PhoneNumber;
            } else if(id == Display.getValue()) {
                return Display;
            } else {
                return None;
            }
        }
    }

    private View view;
    private TextView mTextTitle, mTextAmountSign, mTextSubContent;
    private EditText mEditContent;
    private RelativeLayout mButtonSelect, mButtonClean;

    private InputTextType inputTextType = InputTextType.None;
    private String mAttrTextTitle, mAttrTextAmountSign;
    private boolean mHasSelectButton;

    private AlertDialog mSelectDialog;
    private List<SelectItemData> mSelectItems;

    private boolean mIsInit = false;
    private boolean mShowDecimalFormat = false;
    private boolean mShowKeyborund = false;
    private boolean mIsValidatePass = false;

    private String mDefaultOtherSubContent;


    public InputTextView(Context context) {
        super(context);
        init();
    }

    public InputTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.InputTextView);
        mHasSelectButton = styledAttrs.getBoolean(R.styleable.InputTextView_hasSelectButton, false);
        mAttrTextTitle = styledAttrs.getString(R.styleable.InputTextView_textTitle);
        mAttrTextAmountSign = styledAttrs.getString(R.styleable.InputTextView_AmountSign);
        inputTextType = InputTextType.valueOf(styledAttrs.getInteger(R.styleable.InputTextView_inputTextType, 0));
        styledAttrs.recycle();

        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        String content = mEditContent.getText().toString();
        if(mIsInit && !TextUtils.isEmpty(content)) {
            changeEditStatus(false);

        }
        mIsInit = false;

        if(mShowKeyborund) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditContent, 0);
            mShowKeyborund = false;
        }
    }

    private void init() {
        mIsInit = true;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.element_input_text, this, true);
//        inflate(getContext(), R.layout.element_input_text, this);
        mTextTitle = (TextView) view.findViewById(R.id.text_title);
        mTextSubContent = (TextView) view.findViewById(R.id.text_sub_content);
        mTextAmountSign = (TextView) view.findViewById(R.id.text_amount_sign);
        mEditContent = (EditText) view.findViewWithTag("InputTextEdit");

        mButtonClean = (RelativeLayout) view.findViewById(R.id.button_clear);
        mButtonSelect = (RelativeLayout) view.findViewById(R.id.button_select);


        //init
//        relativeLayoutRightButton.setVisibility(GONE);
//        mTextSubContent.setVisibility(GONE);
//        mTextSubContent.setVisibility(GONE);

//        mTextTitle.setAnimation(getAnimation(R.style.DialogAnimation));

        if(!TextUtils.isEmpty(mAttrTextTitle)) {
            setTitle(mAttrTextTitle);
        }

        if(mHasSelectButton) {
//            relativeLayoutRightButton.setVisibility(VISIBLE);
            mButtonSelect.setVisibility(VISIBLE);
        } else {
            mButtonSelect.setVisibility(GONE);
        }




        mEditContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    validateContent();
                    if(onActionListener != null) {
                        onActionListener.onEditorAction(null, 0, null);
                    }
                }

                changeEditStatus(hasFocus);

            }
        });




        mEditContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(!TextUtils.isEmpty(mEditContent.getContent().toString()) ) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mEditContent.clearFocus();
//                    changeEditStatus(false);
//                }

                if(onActionListener != null) {
                    onActionListener.onEditorAction(v, actionId, event);
                }

                return false;
            }
        });

        mButtonClean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditContent.setText("");
            }
        });

        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!mEditContent.isFocused()) {
                        setFocusEdit();
                    }
                }
                return false;
            }
        });


        setInputTextType(inputTextType);


        if(mHasSelectButton) {
            mButtonSelect.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectDialog != null) {
                        mSelectDialog.show();
                    }
                }
            });
        }

    }

    private void setFocusEdit() {
        mShowKeyborund = true;
//        mSelectDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mEditContent.requestFocus();
        mEditContent.setSelection(mEditContent.getText().length());

//        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
////                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        imm.showSoftInput(mEditContent, 0);

    }

    public void setSelectItems(ArrayList<SelectItemData> items, OnSelectListener onSelectListener) {
        setSelectDialog(items);
        setOnSelectListener(onSelectListener);
    }

    public void setSelectItems(ArrayList<SelectItemData> items, OnSelectListener onSelectListener, int defaultIndex) {
        setSelectDialog(items);
        setOnSelectListener(onSelectListener);
        setSelectedItem(defaultIndex);
    }

    public void setSelectItems(ArrayList<SelectItemData> items, OnSelectListener onSelectListener, int defaultIndex, int otherIndex) {



        setSelectDialog(items);
        setOnSelectListener(onSelectListener);

        //設定手動輸入 後面的文字
        SelectItemData otherItem = items.get(otherIndex);
        setmDefaultOtherSubContent(otherItem.getContent());

        //設定預設的許選項
        setSelectedItem(defaultIndex);
    }

    private void setSelectDialog(ArrayList<SelectItemData> items) {
        mSelectItems = items;

        // 1. Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        // 2. Chain together various setter methods to set the dialog characteristics
        View view = inflater.inflate(R.layout.inputtext_select_view, null);

        builder.setView(view);

        // On devices prior to Honeycomb, the button order (left to right) was POSITIVE - NEUTRAL - NEGATIVE.
        // On newer devices using the Holo theme, the button order (left to right) is now NEGATIVE - NEUTRAL - POSITIVE.
        builder.setPositiveButton(null, null);

        // 3. Get the AlertDialog from create()
        mSelectDialog = builder.create();

        LinearLayout linearLayoutItems = (LinearLayout) view.findViewById(R.id.linearLayout);
        for (int i = 0 ; i < items.size() ; i++) {
            addSelectItem(linearLayoutItems, items.get(i), i);
        }

        Button buttonCancel = (Button) view.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectDialog != null) {
                    mSelectDialog.dismiss();
                }
            }
        });

        Window window = mSelectDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setWindowAnimations(R.style.DialogAnimation);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        mSelectDialog.setCancelable(true);
//        dialog.show();

    }

    private void addSelectItem (LinearLayout linearLayout, SelectItemData item, Object tag) {
        InputTextSelectItem selectItem = new InputTextSelectItem(getContext());
        if(!TextUtils.isEmpty(item.getContent())) {
            selectItem.setContent(item.getContent());
        }

        if(item.getAmount() != null) {
            if(mShowDecimalFormat) {
                selectItem.setAmount(FormatUtil.toDecimalFormat(getContext(), item.getAmount(), true));
            } else {
                selectItem.setAmount(item.getAmount().toString());
            }
        }

        selectItem.setOnClickListener(itemOnClickListener);

        selectItem.setTag(tag);

        linearLayout.addView(selectItem);
    }

    private OnClickListener itemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
//            mSelectItems.get((int)v.getTag());
            SelectItemData item = mSelectItems.get((int)v.getTag());

            if(mSelectDialog != null) {
                mSelectDialog.dismiss();
            }

            if(item.isToEdit()) {
                setFocusEdit();
            } else {
                setSelectedItem(item);
            }

            onSelectListener.onSelect(item);
        }
    };

    /**
     * 有選單才能用
     */
    public void setSelectedItem(int index) {

        if(mSelectItems != null) {
            if(index < mSelectItems.size()) {
                SelectItemData item = mSelectItems.get(index);
                setSelectedItem(item);
            }
        }
    }

    private void setSelectedItem(SelectItemData item) {
        if(mShowDecimalFormat) {
            setContent(FormatUtil.toDecimalFormat(getContext(), item.getAmount()));
        } else {
            setContent(item.getAmount().toString());
        }
        setTextSubContent(item.getContent());
    }


    public void setmDefaultOtherSubContent(String subContent) {
        mDefaultOtherSubContent = subContent;
        setTextSubContent(subContent);
    }

    public void setKeyboardActionDone() {
        mEditContent.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    public void setTitle(String string) {
        mTextTitle.setText(string);
        mEditContent.setHint(string);
    }

    public void setTitle(int stringID) {
        mTextTitle.setText(stringID);
        mEditContent.setHint(stringID);
    }

    public String getTitle() {
        return mTextTitle.getText().toString();
    }


    public String getContent() {
        return mEditContent.getText().toString();
    }


    public String getContent(boolean hasAmountSign) {
        if(hasAmountSign) {
            return String.format("%1$s%2$s", mTextAmountSign.getText().toString(), getContent());
        } else {
            return getContent();
        }
    }

    public double getAmount(){
        double doubleAmount = 0;

        if(mEditContent.getText().toString() != null && !mEditContent.getText().toString().isEmpty()){
            String stringAmount = mEditContent.getText().toString();
            stringAmount = stringAmount.replace(",", "");
            doubleAmount = Double.parseDouble(stringAmount);
        }

        return doubleAmount;
    }

    public void setContent(String string) {
        switch (inputTextType) {
            case CreditCard:
                mEditContent.setText(FormatUtil.toCreditCardFormat(string));
                break;
            default:
                mEditContent.setText(string);
                break;
        }

        changeEditStatus(false);


    }

    public void setContent(int stringID) {
        mEditContent.setText(stringID);
        changeEditStatus(false);
    }

//    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
//        mEditContent.setOnFocusChangeListener(onFocusChangeListener);
//    }

    public void setMaxLength(int maxLength) {
        ArrayList<InputFilter> filters = new ArrayList<>(Arrays.asList(mEditContent.getFilters()));
        filters.add(new InputFilter.LengthFilter(maxLength));
        mEditContent.setFilters(filters.toArray(new InputFilter[filters.size()]));
    }

    public void setInputType (int type) {
        mEditContent.setRawInputType(type);
    }

    public void setDigits(String string) {
        mEditContent.setKeyListener(DigitsKeyListener.getInstance(string));
    }

    public void setTextSubContent(String string) {
        mTextSubContent.setVisibility(VISIBLE);
        mTextSubContent.setText(String.format("(%s)",string));
    }

    public void setTextSubContent(int stringID) {
        mTextSubContent.setVisibility(VISIBLE);
        mTextSubContent.setText(String.format("(%s)",getContext().getString(stringID)));
    }

    public String getTextSubContent() {
        return mTextSubContent.getText().toString();
    }

    public void setAmountSign(String string) {
        mAttrTextAmountSign = string;
    }

    public void setAmountSign(int stringID) {
        mAttrTextAmountSign = getContext().getString(stringID);
    }

    private String getAmountSign() {
        return mAttrTextAmountSign;
    }

    public void addTextChangedListener(TextWatcher textWatcher){
        mEditContent.addTextChangedListener(textWatcher);
    }

    public void setSelectOnClickListener(OnClickListener listener) {
        mButtonSelect.setOnClickListener(listener);
    }

    private void setLockInputText(boolean isLock) {
        mEditContent.setFocusable(!isLock);
        mButtonClean.setVisibility(GONE);
    }






    private void changeEditStatus(boolean isEdit) {
        if(isEdit) {

            mButtonClean.setVisibility(VISIBLE);
            mButtonSelect.setVisibility(GONE);
            mEditContent.setHint("");

            if( mTextTitle.getVisibility() == GONE) {
                mTextTitle.setVisibility(VISIBLE);
                //動畫
//                mTextTitle.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_animation_for_inputtext));
            }

        } else {
            if(TextUtils.isEmpty(mEditContent.getText())){
               mEditContent.setHint(mTextTitle.getText());
               mTextTitle.setVisibility(GONE);
            } else {
               mTextTitle.setVisibility(VISIBLE);
            }

            if(mHasSelectButton) {
               mButtonSelect.setVisibility(VISIBLE);
            } else {
               mButtonSelect.setVisibility(GONE);
            }

            mButtonClean.setVisibility(GONE);

            if(onFinishEditListener != null) {
                onFinishEditListener.OnFinish();
            }
        }
    }

    public EditText getEditText() {
        return mEditContent;
    }




    public void setInputTextType(InputTextType type) {
        switch (type) {
            case UserID:
                setInputTypeUserID();
                break;
            case Amount:
                setInputTypeAmount();
                break;
            case CreditCard:
                setInputTypeCardNumber();
                break;
            case EffectiveDate:
                setInputTypeEffectiveDate();
                break;
            case BirthDate:
                setInputTypeDate();
                break;
            case Password:
                setInputTypePassword();
                break;
            case City:
                setInputTypeCity();
                break;
            case Region:
                setInputTypeRegion(null);
                break;
            case NickName:
                setInputTypeNickName();
                break;
            case UserAccount:
                setInputTypeUserAccount();
                break;
            case Email:
                setInputTypeEmail();
                break;
            case PhoneNumber:
                setInputTypePhoneNumber();
                break;
            case Display:
                setInputTypeDisplay();
                break;
        }
    }

    //設定為輸入身分證
    private void setInputTypeUserID() {
        mEditContent.setAllCaps(true);
        setDigits(getContext().getResources().getString(R.string.characters_number_capital_only));
        setMaxLength(getContext().getResources().getInteger(R.integer.userid_maxlength));
        setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS );
    }

    private void setInputTypePassword() {
        setDigits(getContext().getResources().getString(R.string.characters_number_letter_only));
        setMaxLength(getContext().getResources().getInteger(R.integer.password_maxlength));
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
    }

    private void setInputTypeNickName() {

        setMaxLength(getContext().getResources().getInteger(R.integer.nickname_maxlength));
        ArrayList<InputFilter> filters = new ArrayList<>(Arrays.asList(mEditContent.getFilters()));

        InputFilter filter =  new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        filters.add(filter);
        mEditContent.setFilters(filters.toArray(new InputFilter[filters.size()]));
    }

    //設定為輸入金額
    private void setInputTypeAmount() {
        mShowDecimalFormat = true;

        setDigits(getContext().getResources().getString(R.string.only_number_capital_only));
//        setMaxLength(getContext().getResources().getInteger(R.integer.userid_maxlength));
        setInputType( InputType.TYPE_CLASS_NUMBER);

        mEditContent.addTextChangedListener(new TextWatcher() {
            private boolean isAddAmountSign = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(start ==0 && count != 0 && !TextUtils.isEmpty(mAttrTextAmountSign) && !isAddAmountSign) {
                    mTextAmountSign.setText(mAttrTextAmountSign);
                    mTextAmountSign.setVisibility(VISIBLE);
                    isAddAmountSign = true;
                } else {
                    if(TextUtils.isEmpty(mEditContent.getText())) {
                        mTextAmountSign.setVisibility(GONE);
                        isAddAmountSign = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if(!hasFocus) {
                    if(mShowDecimalFormat) {
                        if (!TextUtils.isEmpty(mEditContent.getText())) {
                            mEditContent.setText(FormatUtil.toDecimalFormat(getContext(), Float.valueOf(FormatUtil.cleanDecimalFormat(mEditContent.getText().toString()))));
                        } else {
                            mEditContent.setText(FormatUtil.cleanDecimalFormat(mEditContent.getText().toString()));
                        }
                    }

                    validateContent();
                } else {

                    if(!TextUtils.isEmpty(mDefaultOtherSubContent)) {
                        setTextSubContent(mDefaultOtherSubContent);
                        onChangeEditContentListener.OnOtherItem();
                    } else {
                        mTextSubContent.setText("");
                    }

                    if(mShowDecimalFormat) {
                        if (!TextUtils.isEmpty(mEditContent.getText())) {
                            mEditContent.setText(FormatUtil.cleanDecimalFormat(mEditContent.getText().toString()));
                        }
                    }

                }

                changeEditStatus(hasFocus);
            }
        });
    }

    private void setInputTypeEffectiveDate() {

        setDigits(getContext().getResources().getString(R.string.only_number_capital_only));
        setMaxLength(getContext().getResources().getInteger(R.integer.effective_date_maxlength));
        setInputType( InputType.TYPE_CLASS_NUMBER);


        mEditContent.addTextChangedListener(new EffectiveDateTextWatcher(mEditContent));
    }

    private void setInputTypeCardNumber() {

        setDigits(getContext().getResources().getString(R.string.only_number_capital_only));
        setMaxLength(getContext().getResources().getInteger(R.integer.credit_card_maxlength));
        setInputType( InputType.TYPE_CLASS_NUMBER);


        mEditContent.addTextChangedListener(new CardNumberTextWatcher(mEditContent));

        mEditContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    String text = mEditContent.getText().toString();
                    text = text.replace("-", "");
                    if (text.length() == 16) {
                        mEditContent.setText(FormatUtil.toCreditCardFormat(text));
                    }
                    validateContent();
                    //  editText_cardnumber.requestFocus();
                }

                changeEditStatus(hasFocus);
            }
        });
    }

    private void setInputTypeDate() {

        setDigits(getContext().getResources().getString(R.string.only_number_capital_only));
        setMaxLength(getContext().getResources().getInteger(R.integer.date_maxlength));
        setInputType( InputType.TYPE_CLASS_DATETIME);

        mEditContent.addTextChangedListener(new DateTextWatcher(mEditContent));
    }

    private void setInputTypeNumber() {

        setDigits(getContext().getResources().getString(R.string.only_number_capital_only));
        setInputType( InputType.TYPE_CLASS_NUMBER);
    }

    private void setInputTypeCity(){
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AddressPickView addressPickView = AddressPickView.newCityInstance();
                    addressPickView.setOnSelectedListener(new AddressPickView.OnSelectedListener() {
                        @Override
                        public void OnSelectedCity(String city) {
                            setContent(city);
                            if(onCityAndRegionListener != null) {
                                onCityAndRegionListener.OnCityFinish(city);
                            }
                        }

                        @Override
                        public void OnSelectedRegion(String region, String postal) {

                        }
                    });
                    addressPickView.show(((Activity)getContext()).getFragmentManager(), TAG);
                }
                return false;
            }
        });

       mEditContent.setOnFocusChangeListener(new OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if(hasFocus) {
                   AddressPickView addressPickView = AddressPickView.newCityInstance();
                   addressPickView.setOnSelectedListener(new AddressPickView.OnSelectedListener() {
                       @Override
                       public void OnSelectedCity(String city) {
                           setContent(city);
                           if(onCityAndRegionListener != null) {
                               onCityAndRegionListener.OnCityFinish(city);
                           }
                       }

                       @Override
                       public void OnSelectedRegion(String region, String postal) {

                       }
                   });
                   addressPickView.show(((Activity) getContext()).getFragmentManager(), TAG);
               }
               mEditContent.clearFocus();
           }
       });
    }



    public void setBaseCityInoutText(String cityString) {
        if(inputTextType.equals(InputTextType.Region)) {
            setInputTypeRegion(cityString);
        }
    }

    private void setInputTypeRegion(final String cityString){
        setContent("");
//        if(TextUtils.isEmpty(cityString)) {
//            view.setEnabled(false);
//            return;
//        }
//
//        view.setEnabled(true);

        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //只在縣市已經有值時才執行動
                    if(!TextUtils.isEmpty(cityString)) {
                        AddressPickView addressPickView = AddressPickView.newRegionInstance(cityString);
                        addressPickView.setOnSelectedListener(new AddressPickView.OnSelectedListener() {
                            @Override
                            public void OnSelectedCity(String city) {

                            }

                            @Override
                            public void OnSelectedRegion(String region, String postal) {
                                setContent(region);
                                if (onCityAndRegionListener != null) {
                                    onCityAndRegionListener.OnRegionFinish(region, postal);
                                }
                            }
                        });
                        addressPickView.show(((Activity) getContext()).getFragmentManager(), TAG);
                    }
                }
                return false;
            }
        });

        mEditContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    //只在縣市已經有值時才執行動作
                    if(!TextUtils.isEmpty(cityString)) {
                        AddressPickView addressPickView = AddressPickView.newRegionInstance(cityString);
                        addressPickView.setOnSelectedListener(new AddressPickView.OnSelectedListener() {
                            @Override
                            public void OnSelectedCity(String city) {

                            }

                            @Override
                            public void OnSelectedRegion(String region, String postal) {
                                setContent(region);
                                if (onCityAndRegionListener != null) {
                                    onCityAndRegionListener.OnRegionFinish(region, postal);
                                }
                            }
                        });
                        addressPickView.show(((Activity) getContext()).getFragmentManager(), TAG);
                    }
                }

                mEditContent.clearFocus();
            }
        });
    }

    private void setInputTypeUserAccount() {
        mEditContent.setAllCaps(true);
        setDigits(getContext().getResources().getString(R.string.characters_number_capital_only));
        setMaxLength(getContext().getResources().getInteger(R.integer.account_maxlength));
        setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS );
    }

    private void setInputTypeEmail() {
        setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    private void setInputTypePhoneNumber() {
        setDigits(getContext().getResources().getString(R.string.only_number_capital_only));
        setMaxLength(getContext().getResources().getInteger(R.integer.cellphone_maxlength));
        setInputType(InputType.TYPE_CLASS_PHONE);
    }

    private void setInputTypeDisplay(){
        mEditContent.setFocusable(false);
        mEditContent.setFocusableInTouchMode(false);

        mButtonClean.setClickable(false);
        mButtonClean.setVisibility(GONE);
    };

    private void validateContent() {
        switch (inputTextType) {
            case UserID:
                validateUserID();
                break;
            case Amount:
                break;
            case CreditCard:
                validateCreditCard();
                break;
            case EffectiveDate:
                validateEffectiveDate();
                break;
            case BirthDate:
                validateBirthday();
                break;
            case Password:
                break;
            case City:
                break;
            case Region:
                break;
            case NickName:
                break;
            case UserAccount:
                break;
            case Email:
                validateEmail();
                break;
            case PhoneNumber:
                validatePhoneNumber();
                break;
            case Display:
                break;
        }
    }

    private void validateUserID() {
        String string = getContent();
        if(string.matches(getContext().getString(R.string.userid_format_regular_expression))) {
            validateResult(sharedMethods.validateUserIDforROC(string), getContext().getString(R.string.validate_fail));
        } else if(string.matches(getContext().getString(R.string.resident_permit_format_regular_expression))){
            validateResult(sharedMethods.validateUserIDforResidentPermit(string), getContext().getString(R.string.validate_fail));
        } else {
            validateResult(false, getContext().getString(R.string.validate_fail));
        }
    }

    private void validateEmail() {
        String string = getContent();
        if(string.matches(FormatUtil.EMAIL_INVALID_FORMAT)) {
            validateResult();
        } else {
            validateResult(false, getContext().getString(R.string.validate_fail));
        }
    }

    private void validatePhoneNumber(){
        String string = getContent();
//        if(string.matches(getContext().getString(R.string.cellphone_format_regular_expression))){
            validateResult();
//        } else {
//            validateResult(false);
//        }
    }

    private void validateBirthday(){
        String string = getContent();
        if(ValidateUtil.checkBirthday(string)){
           validateResult();
        } else {
            validateResult(false, getContext().getString(R.string.validate_fail));
        }
    }

    private void validateCreditCard() {
        String string = getContent();
        if(ValidateUtil.checkCreditCardNumber(string)){
            validateResult();
        } else {
            validateResult(false, getContext().getString(R.string.validate_length_fail));
        }
    }

    private void validateEffectiveDate() {
        String string = getContent();
        if(ValidateUtil.checkEffectiveDate(string)){
            validateResult();
        } else {
            validateResult(false, getContext().getString(R.string.validate_fail));
        }
    }



    private void validateResult() {
        validateResult(true, "");
    }

    private void validateResult(boolean isPass, String message) {
        setIsValidatePass(isPass);

        if(!isPass) {
            ((ActivityBase) getContext()).showAlertDialog(getTitle() + message);
            if(onValidateListener != null) {
                onValidateListener.OnFail();
            }
        } else {
            if(onValidateListener != null) {
                onValidateListener.OnPass();
            }
        }
    }

    public boolean isValidatePass() {
        return mIsValidatePass;
    }

    public void setIsValidatePass(boolean mIsValidatePass) {
        this.mIsValidatePass = mIsValidatePass;
    }
}
