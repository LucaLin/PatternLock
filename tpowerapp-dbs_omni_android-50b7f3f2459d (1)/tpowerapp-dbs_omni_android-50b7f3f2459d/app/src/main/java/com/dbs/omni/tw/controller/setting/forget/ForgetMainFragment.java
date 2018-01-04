package com.dbs.omni.tw.controller.setting.forget;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.typeMapping.ForgetType;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.ValidateUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetMainFragment extends Fragment {

    public static final String TAG = "ForgetMainFragment";
    public static final String ARG_PAGE_TYPE = "ARG_PAGE_TYPE";

    private InputTextView inputTextUserID,inputTextBirth,inputTextCardNumber, inputTextEffectiveDate, inputTextUserCode;


    private OnForgetListener onForgetListener;

    public void setOnForgetListener (OnForgetListener onForgetListener) {
        this.onForgetListener = onForgetListener;
    }

    public interface OnForgetListener {
        void OnSubmit(String nID, String birthDate, String cardNumber, String effectiveDate);
        void OnSetMima(String nID, String birthDate, String cardNumber, String effectiveDate, String userCode);
    }

    private Button buttonNext;
    private ForgetType forgetType = ForgetType.None;

    public static ForgetMainFragment newInstance(ForgetType type) {

        Bundle args = new Bundle();

        args.putString(ARG_PAGE_TYPE, type.toString());

        ForgetMainFragment fragment = new ForgetMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();

        if(args.containsKey(ARG_PAGE_TYPE)) {
            forgetType = ForgetType.valueOf(args.getString(ARG_PAGE_TYPE));
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_main, container, false);
        buttonNext = (Button) view.findViewById(R.id.btnNextStep);
//        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (forgetType) {
                    case ACCOUNT:
                        onForgetListener.OnSubmit(inputTextUserID.getContent(),
                                FormatUtil.removeDateFormatted(inputTextBirth.getContent()),
                                FormatUtil.removeCreditCardFormat(inputTextCardNumber.getContent()),
                                FormatUtil.removeDateFormatted(inputTextEffectiveDate.getContent()));
                        break;
                    case MIMA:
                        onForgetListener.OnSetMima(inputTextUserID.getContent(),
                                FormatUtil.removeDateFormatted(inputTextBirth.getContent()),
                                FormatUtil.removeCreditCardFormat(inputTextCardNumber.getContent()),
                                FormatUtil.removeDateFormatted(inputTextEffectiveDate.getContent()),
                                inputTextUserCode.getContent());
                        break;
                    default:
                        break;
                }
//                on.onNext();
            }
        });


//        TextView textAgreeNote = (TextView) view.findViewById(R.id.text_agree_notes);
//        textAgreeNote.setPaintFlags(textAgreeNote.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//
//
        inputTextUserID = (InputTextView) view.findViewById(R.id.inputText_user_id);
        inputTextUserID.setOnFinishEdit(onFinishEditListener);

        inputTextBirth = (InputTextView) view.findViewById(R.id.inputText_date_of_birth);
        inputTextBirth.setTitle(FormatUtil.getDateOfBirthTile(getActivity(), true));
        inputTextBirth.setOnFinishEdit(onFinishEditListener);

        inputTextCardNumber = (InputTextView) view.findViewById(R.id.inputText_credit_card);
        inputTextCardNumber.setTitle(FormatUtil.getCreditCardTile(getActivity(), true));
        inputTextCardNumber.setOnFinishEdit(onFinishEditListener);

        inputTextEffectiveDate = (InputTextView) view.findViewById(R.id.inputText_effective_date);
        inputTextEffectiveDate.setTitle(FormatUtil.getEffectiveDate(getActivity(), true));
        inputTextEffectiveDate.setOnFinishEdit(onFinishEditListener);

        inputTextUserCode = (InputTextView) view.findViewById(R.id.inputText_account_user_code);
        inputTextUserCode.setOnFinishEdit(onFinishEditListener);

        if(forgetType.equals(ForgetType.MIMA)) {
            inputTextUserCode.setVisibility(View.VISIBLE);
        }

        return view;
    }

    //檢查輸入內容
    private boolean ValidateUserCode(InputTextView inputTextView) {
        boolean isPass = true;
        String content = inputTextView.getContent();
        boolean isLengthLimitPass = ValidateUtil.isLengthLimit(content, getContext().getResources().getInteger(R.integer.account_minlength)
                ,getContext().getResources().getInteger(R.integer.account_maxlength));

        isPass = isPass & isLengthLimitPass;


        isPass = isPass & !ValidateUtil.isOnlyNumber(content);

        isPass = isPass & !ValidateUtil.hasSameAndContinuousChar(content);

        isPass = isPass & !ValidateUtil.hasSpecialORSpace(content);

        return isPass;
    }

    private boolean isEnableNextButton() {
        boolean isEnable = true;

        if(forgetType.equals(ForgetType.MIMA)) {
            boolean isUserCodePass = ValidateUserCode(inputTextUserCode);
            isEnable = isEnable & isUserCodePass;
        }

//        isEnable = isEnable & ValidateUtil.checkBirthday(getContext(), inputTextBirth.getContent().toString(), isShowAlert);
//        isEnable = isEnable & ValidateUtil.checkEffectiveDate(getContext(),inputTextEffectiveDate.getContent().toString(), isShowAlert);
//        isEnable = isEnable & ValidateUtil.checkCreditCardNumber(getContext(),inputTextCardNumber.getContent().toString(),isShowAlert);
        isEnable = isEnable & inputTextUserID.isValidatePass();
        isEnable = isEnable & inputTextBirth.isValidatePass();
        isEnable = isEnable & inputTextEffectiveDate.isValidatePass();
        isEnable = isEnable & inputTextCardNumber.isValidatePass();

        buttonNext.setEnabled(isEnable);
        return isEnable;
    }

    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton();
        }
    };

//    private void ValidateInputContent() {
//        if(isEnableNextButton(true)) {
//
//        }
//    }

}
