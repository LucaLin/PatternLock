package com.dbs.omni.tw.controller.setting.forget;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.element.SymbolItem;
import com.dbs.omni.tw.util.UserInfoUtil;
import com.dbs.omni.tw.util.ValidateUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetMimaFragment extends Fragment {

    public static final String TAG = "SetMimaFragment";

    private OnSetMimaListener onSetMimaListener;

    public void setOnSetMimaListener(OnSetMimaListener listener) {
        this.onSetMimaListener = listener;
    }

    public interface OnSetMimaListener {
        void OnNext(String newMima);
    }

    private Button buttonNext;
//    private ForgetType forgetType = ForgetType.None;

    private InputTextView inputTextMima, inputTextMinaConfirm;
    private SymbolItem lengthLimitItme, canNotSameAccountLimitItem, canNotSetSpecialLimitItem, upperNotEqualLower, hasSameAndContinuousChar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Bundle args = getArguments();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_set_mima, container, false);
        buttonNext = (Button) view.findViewById(R.id.btnNextStep);
//        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateInputContent();
            }
        });


        inputTextMima = (InputTextView) view.findViewById(R.id.inputText_mina);
        inputTextMima.setOnFinishEdit(onFinishEditListener);
        inputTextMinaConfirm = (InputTextView) view.findViewById(R.id.inputText_mina_confirm);
        inputTextMinaConfirm.setOnFinishEdit(onFinishEditListener);

        setValidateView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isEnableNextButton();
    }

    private void setValidateView(View view) {
        lengthLimitItme = (SymbolItem) view.findViewById(R.id.length_limit);
        upperNotEqualLower = (SymbolItem) view.findViewById(R.id.upper_not_equal_to_lower);
        hasSameAndContinuousChar = (SymbolItem) view.findViewById(R.id.not_has_same_and_continuous_char);
        canNotSameAccountLimitItem = (SymbolItem) view.findViewById(R.id.can_not_same_account_limit);
        canNotSetSpecialLimitItem = (SymbolItem) view.findViewById(R.id.can_not_set_specialsymbols);
    }


    //檢查輸入內容
    private boolean isEnableNextButton() {
        boolean isEnable = true;

        boolean isUserMimaPass = ValidateUserMima(inputTextMima);
        isEnable = isEnable & isUserMimaPass;
        isEnable = isEnable & !TextUtils.isEmpty(inputTextMinaConfirm.getContent());

        confirmMina();

        buttonNext.setEnabled(isEnable);
        return isEnable;
    }

    private boolean ValidateUserMima(InputTextView inputTextView) {
        boolean isPass = true;
        String content = inputTextView.getContent();
        boolean isLengthLimitPass = ValidateUtil.isLengthLimit(content, getContext().getResources().getInteger(R.integer.password_minlength)
                ,getContext().getResources().getInteger(R.integer.password_maxlength));
        boolean isSameAndContinuousCharPass = !ValidateUtil.hasSameAndContinuousChar(content);

        lengthLimitItme.setFail(!isLengthLimitPass);

        isPass = isPass & !lengthLimitItme.isFail();

        hasSameAndContinuousChar.setFail(!isSameAndContinuousCharPass);

        isPass = isPass & !canNotSameAccountLimitItem.isFail();

        if(UserInfoUtil.getsUserCode() != null) {
            canNotSameAccountLimitItem.setFail(content.equals(UserInfoUtil.getsUserCode()));
            isPass = isPass & !canNotSameAccountLimitItem.isFail();
        }

        canNotSetSpecialLimitItem.setFail(ValidateUtil.hasSpecialORSpace(content));
        isPass = isPass & !canNotSetSpecialLimitItem.isFail();

        return isPass;
    }

    private boolean confirmMina() {
        String mina = inputTextMima.getContent();
        String confirm = inputTextMinaConfirm.getContent();
        if(TextUtils.isEmpty(mina) || TextUtils.isEmpty(confirm)) {
            return false;
        } else {
            if (!mina.equals(confirm)) {
                ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.validate_mima_confirm_fail));
                return false;
            } else {
                return true;
            }
        }
    }

    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton();
        }
    };

    private void ValidateInputContent() {
        if(isEnableNextButton()) {
            onSetMimaListener.OnNext(inputTextMima.getContent());
        }
    }

}
