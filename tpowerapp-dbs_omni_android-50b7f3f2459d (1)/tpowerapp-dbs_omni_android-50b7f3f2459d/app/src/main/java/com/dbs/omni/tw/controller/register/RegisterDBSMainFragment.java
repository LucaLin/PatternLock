package com.dbs.omni.tw.controller.register;


import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.element.SymbolItem;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.ValidateUtil;
import com.dbs.omni.tw.util.http.RegisterHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.RegisterData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterDBSMainFragment extends Fragment {

    public static final String TAG = "RegisterDBSMainFragment";

    public static final String ARG_REGISTER_DATA = "ARG_REGISTER_DATA";

    private RegisterActivity.OnRegisterListener onRegisterListener;

    public void setOnRegisterListener (RegisterActivity.OnRegisterListener listener) {
        this.onRegisterListener = listener;
    }


    private Button buttonNext;
    private TextView textAgreeNote;
    private CheckBox checkboxAgreeNote;
    private InputTextView inputTextBirth, inputTextUserCode, inputTextEffectiveDate, inputTextCardNumber;
    private SymbolItem lengthLimitItme, canNotForUserIdLimitItem, canNotOnlyNumberLimitItem,
            canNotSameOrContinuousLimitItem, canNotSameMimaLimitItem, canNotSetSpecialLimitItem;

    private RegisterData mRegisterData;

    public static RegisterDBSMainFragment newInstance(RegisterData registerData) {

        Bundle args = new Bundle();

        args.putParcelable(ARG_REGISTER_DATA, registerData);

        RegisterDBSMainFragment fragment = new RegisterDBSMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_REGISTER_DATA)) {
            mRegisterData = getArguments().getParcelable(ARG_REGISTER_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_dbs_main, container, false);
        buttonNext = (Button) view.findViewById(R.id.btnNextStep);
////        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateInputContent();
            }
        });
//
//
        checkboxAgreeNote = (CheckBox) view.findViewById(R.id.checkbox_agree_notes);
        checkboxAgreeNote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    isEnableNextButton(false);
                } else {
                    buttonNext.setEnabled(false);
                }
            }
        });

        textAgreeNote = (TextView) view.findViewById(R.id.text_agree_notes);
        textAgreeNote.setPaintFlags(textAgreeNote.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textAgreeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkboxAgreeNote.setChecked(true);
            }
        });

        inputTextCardNumber = (InputTextView) view.findViewById(R.id.inputText_credit_card);
        inputTextCardNumber.setTitle(FormatUtil.getCreditCardTile(getActivity(), true));
        inputTextCardNumber.setOnFinishEdit(onFinishEditListener);

        inputTextEffectiveDate = (InputTextView) view.findViewById(R.id.inputText_effective_date);
        inputTextEffectiveDate.setTitle(FormatUtil.getEffectiveDate(getActivity(), true));
        inputTextEffectiveDate.setOnFinishEdit(onFinishEditListener);

        inputTextBirth = (InputTextView) view.findViewById(R.id.inputText_date_of_birth);
        inputTextBirth.setTitle(FormatUtil.getDateOfBirthTile(getActivity(), true));
        inputTextBirth.setOnFinishEdit(onFinishEditListener);

        inputTextUserCode = (InputTextView) view.findViewById(R.id.inputText_account_user_code);
        inputTextUserCode.setOnFinishEdit(onFinishEditListener);

        setValidateView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isEnableNextButton(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private void setValidateView(View view) {
        lengthLimitItme = (SymbolItem) view.findViewById(R.id.length_limit);
        canNotForUserIdLimitItem = (SymbolItem) view.findViewById(R.id.can_not_for_user_id_limit);
        canNotOnlyNumberLimitItem = (SymbolItem) view.findViewById(R.id.can_not_only_number_limit);
        canNotSameOrContinuousLimitItem = (SymbolItem) view.findViewById(R.id.can_not_same_or_continuous_limit);
        canNotSameMimaLimitItem = (SymbolItem) view.findViewById(R.id.can_not_same_mima_limit);
        canNotSetSpecialLimitItem = (SymbolItem) view.findViewById(R.id.can_not_set_specialsymbols);
    }

    private boolean ValidateUserCode(InputTextView inputTextView) {
        boolean isPass = true;
        String content = inputTextView.getContent();
        if(TextUtils.isEmpty(content)) {
            return false;
        }

        boolean isLengthLimitPass = ValidateUtil.isLengthLimit(content, getContext().getResources().getInteger(R.integer.account_minlength)
                ,getContext().getResources().getInteger(R.integer.account_maxlength));

        lengthLimitItme.setFail(!isLengthLimitPass);
        isPass = isPass & !lengthLimitItme.isFail();

        if(mRegisterData != null) {
            String userID = mRegisterData.getnID();
            if(!TextUtils.isEmpty(mRegisterData.getnID())) {
                canNotForUserIdLimitItem.setFail(content.equalsIgnoreCase(userID));
                isPass = isPass & !canNotForUserIdLimitItem.isFail();
            }
        }

        canNotOnlyNumberLimitItem.setFail(ValidateUtil.isOnlyNumber(content));
        isPass = isPass & !canNotOnlyNumberLimitItem.isFail();

        canNotSameOrContinuousLimitItem.setFail(ValidateUtil.hasSameAndContinuousChar(content));
        isPass = isPass & !canNotSameOrContinuousLimitItem.isFail();

        canNotSetSpecialLimitItem.setFail(ValidateUtil.hasSpecialORSpace(content));
        isPass = isPass & !canNotSetSpecialLimitItem.isFail();

        return isPass;
    }

    //檢查輸入內容
    private boolean isEnableNextButton(boolean isShowAlert) {
        boolean isEnable = true;

        boolean isUserCodePass = ValidateUserCode(inputTextUserCode);
        isEnable = isEnable & isUserCodePass;
        isEnable = isEnable & checkboxAgreeNote.isChecked();

//        isEnable = isEnable & ValidateUtil.checkBirthday(getContext(), inputTextBirth.getContent().toString(), isShowAlert);
//        isEnable = isEnable & ValidateUtil.checkEffectiveDate(getContext(),inputTextEffectiveDate.getContent().toString(), isShowAlert);
//        isEnable = isEnable & ValidateUtil.checkCreditCardNumber(getContext(),inputTextCardNumber.getContent().toString(),isShowAlert);

        isEnable = isEnable & inputTextBirth.isValidatePass();
        isEnable = isEnable & inputTextEffectiveDate.isValidatePass();
        isEnable = isEnable & inputTextCardNumber.isValidatePass();

        buttonNext.setEnabled(isEnable);
        return isEnable;
    }

    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton(false);
        }
    };

    private void ValidateInputContent() {
        if(isEnableNextButton(true)) {
            mRegisterData.setUserCode(inputTextUserCode.getContent());
            mRegisterData.setbDate(FormatUtil.removeDateFormatted(inputTextBirth.getContent()));
            mRegisterData.setCcNO(FormatUtil.removeCreditCardFormat(inputTextCardNumber.getContent()));
            mRegisterData.setExpDate(FormatUtil.removeDateFormatted(inputTextEffectiveDate.getContent()));

            toVerifyUserData();

        }
    }


    //region api
    private void toVerifyUserData() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getContext())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                RegisterHttpUtil.verifyUserData(mRegisterData.getbDate(), mRegisterData.getCcNO(), mRegisterData.getExpDate(), mRegisterData.getnID(), responseListener, getActivity());
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;
            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                onRegisterListener.onNext(mRegisterData);
            } else {
                handleResponseError(result, ((ActivityBase)getActivity()));
            }

        }
    };
//endregion

}
