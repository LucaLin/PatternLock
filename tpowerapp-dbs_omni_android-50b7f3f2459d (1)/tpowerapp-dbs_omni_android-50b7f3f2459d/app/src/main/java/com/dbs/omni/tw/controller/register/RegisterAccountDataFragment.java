package com.dbs.omni.tw.controller.register;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.element.SymbolItem;
import com.dbs.omni.tw.typeMapping.AccountType;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.UserInfoUtil;
import com.dbs.omni.tw.util.ValidateUtil;
import com.dbs.omni.tw.util.fingeprint.FingerprintCore;
import com.dbs.omni.tw.util.fingeprint.FingerprintUtil;
import com.dbs.omni.tw.util.http.GeneralHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.RegisterData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.mode.SSOData;
import com.dbs.omni.tw.util.http.responsebody.GeneralResponseBodyUtil;
import com.dbs.omni.tw.util.js.ReadJSUtil;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterAccountDataFragment extends Fragment {

    public static final String TAG = "RegisterAccountDataFragment";

    public static final String ARG_ACCOUNT_TYPE = "ARG_ACCOUNT_TYPE";
    public static final String ARG_REGISTER_DATA = "ARG_REGISTER_DATA";

    public interface OnRegisterListener {
        void onNext(RegisterData registerData);
    }

    private OnRegisterListener onRegisterListener;

    public void setOnRegisterListener (OnRegisterListener listener) {
        this.onRegisterListener = listener;
    }


    private Button buttonNext;
    private Switch touchIDSwitch;
    private AccountType accountType;
    private InputTextView inputTextMima, inputTextMinaConfirm, inputTextNickname;
    private SymbolItem lengthLimitItme, canNotSameAccountLimitItem, canNotSetSpecialLimitItem, upperNotEqualLower, hasSameAndContinuousChar;

    private RegisterData mRegisterData;
    private AccountType mAccountType;

    public static RegisterAccountDataFragment newInstance(AccountType type, RegisterData registerData) {

        Bundle args = new Bundle();

        args.putInt(ARG_ACCOUNT_TYPE, type.getValue());
        args.putParcelable(ARG_REGISTER_DATA, registerData);

        RegisterAccountDataFragment fragment = new RegisterAccountDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_ACCOUNT_TYPE)) {
            mAccountType = AccountType.valueOf(getArguments().getInt(ARG_ACCOUNT_TYPE));
        }

        if(getArguments() != null && getArguments().containsKey(ARG_REGISTER_DATA)) {
            mRegisterData = getArguments().getParcelable(ARG_REGISTER_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_account_data, container, false);
        buttonNext = (Button) view.findViewById(R.id.btnNextStep);
//        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateInputContent();
            }
        });

        //註冊初始
//        PreferenceUtil.setTouchIDStatus(getActivity(), false);
        touchIDSwitch = (Switch) view.findViewById(R.id.switchTouchID);
        touchIDSwitch.setChecked(((RegisterActivity) getActivity()).isEnableTouchID);
        touchIDSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    detecetFingerprint(true);
                } else {
                    ((RegisterActivity) getActivity()).isEnableTouchID = false;
                }
            }
        });

        inputTextNickname = (InputTextView) view.findViewById(R.id.inputText_nickname);
        inputTextNickname.setTitle(String.format("%1$s %2$s", getString(R.string.user_nickname_title), getString(R.string.user_nickname_hint)));

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

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private void setValidateView(View view) {
        lengthLimitItme = (SymbolItem) view.findViewById(R.id.length_limit);
        upperNotEqualLower = (SymbolItem) view.findViewById(R.id.upper_not_equal_to_lower);
        hasSameAndContinuousChar = (SymbolItem) view.findViewById(R.id.not_has_same_and_continuous_char);
        canNotSameAccountLimitItem = (SymbolItem) view.findViewById(R.id.can_not_same_account_limit);
        canNotSetSpecialLimitItem = (SymbolItem) view.findViewById(R.id.can_not_set_specialsymbols);
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


    //檢查輸入內容
    private boolean isEnableNextButton() {
        boolean isEnable = true;

        boolean isUserMimaPass = ValidateUserMima(inputTextMima);
        isEnable = isEnable & isUserMimaPass;
        isEnable = isEnable & !TextUtils.isEmpty(inputTextMinaConfirm.getContent());

        isEnable = isEnable & confirmMina();

        buttonNext.setEnabled(isEnable);
        return isEnable;
    }


    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton();
        }
    };

    private void ValidateInputContent() {
        if(isEnableNextButton()) {
            getSSO();
        }
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

    private void detecetFingerprint(final boolean isShowAlert) {
        FingerprintUtil.detectFingerprint(getActivity(), isShowAlert, new FingerprintCore.OnDetectFingerprintListener() {
            @Override
            public void OnIsSupport() {
                if(isShowAlert) {
                    ((ActivityBase) getActivity()).showAlertDialog("同步幫您啟用「指紋辨識」以及「記住您的帳號」功能，您可以指紋辨識取代使用者密碼進行登入。", R.string.button_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((RegisterActivity) getActivity()).isEnableTouchID = true;
                            dialog.dismiss();
                        }
                    }, false);
                }
            }

            @Override
            public void OnIsClose() {
                ((RegisterActivity) getActivity()).isEnableTouchID = false;
                touchIDSwitch.setChecked(false);
            }

            @Override
            public void OnIsNotSupport() {
                ((RegisterActivity) getActivity()).isEnableTouchID = false;
            }
        });
    }

//region api
    private void getSSO() {
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
                GeneralHttpUtil.getSSOKey(responseListenerForGetSSO, getActivity());
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private ResponseListener responseListenerForGetSSO = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {



            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                final SSOData ssoData = GeneralResponseBodyUtil.getSSOData(result.getBody());
//                if(GlobalConst.DisableEncode) {
//                    mRegisterData.setpCode(inputTextMima.getContent());
//                    mRegisterData.setrKey(ssoData.getrKey());
//                    mRegisterData.setNickname(inputTextNickname.getContent());
//                    mRegisterData.setEanbleTouchID(touchIDSwitch.isChecked());
//                    onRegisterListener.onNext(mRegisterData);
//                } else {
                    ReadJSUtil.encpty(getContext(), ssoData.getpKey(), ssoData.getrKey(), inputTextMima.getContent(), new ReadJSUtil.OnRunScriptListener() {
                        @Override
                        public void OnReturn(String result) {
    //                        login(mUserCode, result, ssoData);
                            if(getActivity() != null) {
                                ((ActivityBase) getActivity()).dismissProgressLoading();
                            }

                            mRegisterData.setpCode(result);
                            mRegisterData.setrKey(ssoData.getrKey());
                            mRegisterData.setNickname(inputTextNickname.getContent());
                            mRegisterData.setEanbleTouchID(touchIDSwitch.isChecked());
                            onRegisterListener.onNext(mRegisterData);

                        }

                        @Override
                        public void OnNoFound() {
                            if(getActivity() != null) {
                                ((ActivityBase) getActivity()).dismissProgressLoading();
                            }
                        }
                    });
//                }

            } else {
                if(getActivity() == null)
                    return;

                ((ActivityBase)getActivity()).dismissProgressLoading();
                handleResponseError(result, ((ActivityBase)getActivity()));

            }


        }
    };
//endregion
}
