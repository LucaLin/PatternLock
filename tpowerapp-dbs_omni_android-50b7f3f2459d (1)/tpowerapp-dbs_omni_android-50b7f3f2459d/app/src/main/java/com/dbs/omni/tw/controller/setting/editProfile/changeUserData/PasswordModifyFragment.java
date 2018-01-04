package com.dbs.omni.tw.controller.setting.editProfile.changeUserData;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.element.SymbolItem;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.UserInfoUtil;
import com.dbs.omni.tw.util.ValidateUtil;
import com.dbs.omni.tw.util.http.GeneralHttpUtil;
import com.dbs.omni.tw.util.http.SettingHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.SSOData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.GeneralResponseBodyUtil;
import com.dbs.omni.tw.util.js.ReadJSUtil;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordModifyFragment extends Fragment {

    private Button btnNextStep;
    private InputTextView inputText_OldMima, inputText_NewMima, inputText_NewMimaAgain;
    private SymbolItem lengthLimitItme, canNotSameAccountLimitItem, canNotSetSpecialLimitItem, upperNotEqualLower, hasSameAndContinuousChar;

    private PasswordModifyFragment.OnEventListener onEventListener;

    boolean isUserMimaPassOld, isUserMimaPassNew, isUserMimaPassNewAgain;
    private String oldMimaAfterEncryption, newMimaAfterEncryption, rKey, mExtension;

    public interface OnEventListener {
        void OnNextEvent(String oldMimaAfterEncryption, String newMimaAfterEncryption, String rKey);
    }

    public void setOnEventListener (PasswordModifyFragment.OnEventListener listener) {
        this.onEventListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActivityBase) getActivity()).setHeadHide(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_modify, container, false);

        btnNextStep = (Button)view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnListener);
        btnNextStep.setEnabled(false);

        inputText_OldMima = (InputTextView) view.findViewById(R.id.inputText_OldMima);
        inputText_OldMima.setTitle(R.string.input_old_password);
        inputText_OldMima.setOnFinishEdit(onFinishEditListenerOldMima);

        inputText_NewMima = (InputTextView) view.findViewById(R.id.inputText_NewMima);
        inputText_NewMima.setTitle(R.string.input_new_password);
        inputText_NewMima.setOnFinishEdit(onFinishEditListenerNewMima);

        inputText_NewMimaAgain = (InputTextView) view.findViewById(R.id.inputText_NewMima_Again);
        inputText_NewMimaAgain.setTitle(R.string.input_new_password_again);
        inputText_NewMimaAgain.setOnFinishEdit(onFinishEditListenerNewMima);

        setValidateView(view);

        return view;
    }

    private void setValidateView(View view) {
        lengthLimitItme = (SymbolItem) view.findViewById(R.id.length_limit);
        upperNotEqualLower = (SymbolItem) view.findViewById(R.id.upper_not_equal_to_lower);
        hasSameAndContinuousChar = (SymbolItem) view.findViewById(R.id.not_has_same_and_continuous_char);
        canNotSameAccountLimitItem = (SymbolItem) view.findViewById(R.id.can_not_same_account_limit);
        canNotSetSpecialLimitItem = (SymbolItem) view.findViewById(R.id.can_not_set_specialsymbols);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private Button.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isEnableNextButton()) {
                getKeyToEncryption();
            }
        }
    };

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
        String mina = inputText_NewMima.getContent();
        String confirm = inputText_NewMimaAgain.getContent();
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

//    private void isEnableNextButton() {
//        if((isUserMimaPassOld & isUserMimaPassNew & isUserMimaPassNewAgain) == true) {
//            btnNextStep.setEnabled(true);
//            return;
//        }
//        btnNextStep.setEnabled(false);
//    }

    //檢查輸入內容
    private boolean isEnableNextButton() {
        boolean isEnable = true;

        boolean isUserMimaPass = ValidateUserMima(inputText_NewMima);
        isEnable = isEnable & isUserMimaPass;
        isEnable = isEnable & !TextUtils.isEmpty(inputText_NewMimaAgain.getContent());

        isEnable = isEnable & confirmMina();

        isEnable = isEnable & !TextUtils.isEmpty(inputText_OldMima.getContent());

        btnNextStep.setEnabled(isEnable);
        return isEnable;
    }


    private InputTextView.OnFinishEditListener onFinishEditListenerOldMima  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton();
        }
    };

    private InputTextView.OnFinishEditListener onFinishEditListenerNewMima  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton();
        }
    };

//    private InputTextView.OnFinishEditListener onFinishEditListenerNewMimaAgain  = new InputTextView.OnFinishEditListener() {
//        @Override
//        public void OnFinish() {
//            if(confirmMina()) {
//                isEnableNextButton(false);
//            }
//        }
//    };

    private void getKeyToEncryption() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
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
                if(getActivity() == null)
                    return;

                // 取得列表
                final SSOData ssoData = GeneralResponseBodyUtil.getSSOData(result.getBody());

                rKey = ssoData.getrKey();
                if(GlobalConst.DisableEncode) {
                    oldMimaAfterEncryption = inputText_OldMima.getContent();
                    newMimaAfterEncryption = inputText_NewMima.getContent();
                    callVerifyOfChangeMima();
                } else {
                    //加密第一個變數
                    ReadJSUtil.encpty(getActivity(), ssoData.getpKey(), ssoData.getrKey(), inputText_OldMima.getContent(), new ReadJSUtil.OnRunScriptListener() {
                        @Override
                        public void OnReturn(String result) {
                            oldMimaAfterEncryption = result;

                            //加密第二個變數
                            ReadJSUtil.encpty(getActivity(), ssoData.getpKey(), ssoData.getrKey(), inputText_NewMima.getContent(), new ReadJSUtil.OnRunScriptListener() {
                                @Override
                                public void OnReturn(String result) {
                                    newMimaAfterEncryption = result;
                                    callVerifyOfChangeMima();
                                }

                                @Override
                                public void OnNoFound() {
                                }
                            });
                        }

                        @Override
                        public void OnNoFound() {
                        }
                    });
                }
            } else {
                ((ActivityBase)getActivity()).dismissProgressLoading();
                handleResponseError(result, (ActivityBase) getActivity());

            }

        }
    };

    private void callVerifyOfChangeMima(){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                SettingHttpUtil.verifyOfChangeMima(oldMimaAfterEncryption, newMimaAfterEncryption, rKey, responseListener_verifyOfChangeMima, getActivity());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_verifyOfChangeMima = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                mExtension = GeneralResponseBodyUtil.getExtension(result.getBody());
                if(TextUtils.isEmpty(mExtension)) {
                    mExtension = "";
                }

                //顯示OTP
                ((ActivityBase)getActivity()).showOTPAlertDialog(true, SettingHttpUtil.ChangeMimaApiName.toUpperCase(), mExtension, new ActivityBase.OnOTPListener() {
                    @Override
                    public void OnClose(AlertDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void OnResend(AlertDialog dialog) {

                    }

                    @Override
                    public void OnFail(AlertDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void OnSuccess(AlertDialog dialog) {
                        dialog.dismiss();
                        onEventListener.OnNextEvent(oldMimaAfterEncryption, newMimaAfterEncryption, rKey);
                    }

                });
            } else {
                // 如果是共同error，不繼續呼叫另一個api
                if (handleCommonError(result, (ActivityBase) getActivity())) {

                    return;
                } else {
                    showAlert((ActivityBase) getActivity(), result.getReturnMessage());
                }
            }
        }
    };

}
