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
import com.dbs.omni.tw.element.ShowTextItem;
import com.dbs.omni.tw.element.SymbolItem;
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
public class AccountModifyFragment extends Fragment {

    private Button btnNextStep;
    private InputTextView inputTextNewAccount, inputTextView_OldAccount;
    private SymbolItem lengthLimitItme, canNotForUserIdLimitItem, canNotOnlyNumberLimitItem,
            canNotSameOrContinuousLimitItem, canNotSameMimaLimitItem, canNotSetSpecialLimitItem;

    private OnEventListener onEventListener;

    private String accountBeforeEncryption, accountAfterEncryption, rKey, mExtension;

    public interface OnEventListener {
        void OnNextEvent(String accountBeforeEncryption, String accountAfterEncryption, String rKey);
    }

    public void setOnEventListener (OnEventListener listener) {
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
        View view = inflater.inflate(R.layout.fragment_account_modify, container, false);

        btnNextStep = (Button)view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnListener);
        btnNextStep.setEnabled(false);

        inputTextView_OldAccount = (InputTextView) view.findViewById(R.id.inputTextView_OldAccount);
        inputTextView_OldAccount.setTitle(R.string.old_account);
        if(!TextUtils.isEmpty(UserInfoUtil.getsUserCode())){
            inputTextView_OldAccount.setContent(UserInfoUtil.getsUserCode());
        }

        inputTextNewAccount = (InputTextView) view.findViewById(R.id.inputText_NewAccount);
        inputTextNewAccount.setTitle(R.string.input_new_account);
        inputTextNewAccount.setOnFinishEdit(onFinishEditListener);

        setValidateView(view);

        return view;
    }

    private void setValidateView(View view) {
        lengthLimitItme = (SymbolItem) view.findViewById(R.id.length_limit);
        canNotForUserIdLimitItem = (SymbolItem) view.findViewById(R.id.can_not_for_user_id_limit);
        canNotOnlyNumberLimitItem = (SymbolItem) view.findViewById(R.id.can_not_only_number_limit);
        canNotSameOrContinuousLimitItem = (SymbolItem) view.findViewById(R.id.can_not_same_or_continuous_limit);
        canNotSameMimaLimitItem = (SymbolItem) view.findViewById(R.id.can_not_same_mima_limit);
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
            getKeyToEncryption();
        }
    };

    private boolean ValidateUserCode(InputTextView inputTextView) {
        boolean isPass = true;
        String content = inputTextView.getContent();
        boolean isLengthLimitPass = ValidateUtil.isLengthLimit(content, getContext().getResources().getInteger(R.integer.account_minlength)
                ,getContext().getResources().getInteger(R.integer.account_maxlength));

        lengthLimitItme.setFail(!isLengthLimitPass);
        isPass = isPass & !lengthLimitItme.isFail();

        if(UserInfoUtil.getsNID() != null) {
            String userID = UserInfoUtil.getsNID();
            if(!TextUtils.isEmpty(UserInfoUtil.getsNID())) {
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
    private void isEnableNextButton() {
        boolean isUserCodePass = ValidateUserCode(inputTextNewAccount);

        if(isUserCodePass == true){
            btnNextStep.setEnabled(true);
            return;
        }
        btnNextStep.setEnabled(false);
    }

    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton();
        }
    };

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
            if(getActivity() == null)
                return;

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                final SSOData ssoData = GeneralResponseBodyUtil.getSSOData(result.getBody());
//                if(GlobalConst.DisableEncode) {
//
//                    accountBeforeEncryption = inputTextNewAccount.getContent();
//                    accountAfterEncryption = accountBeforeEncryption;
//                    rKey = ssoData.getrKey();
//                    callVerifyOfChangeUserCode();
//                } else {
                    accountBeforeEncryption = inputTextNewAccount.getContent();
                    rKey = ssoData.getrKey();

                    ReadJSUtil.encpty(getActivity(), ssoData.getpKey(), ssoData.getrKey(), accountBeforeEncryption, new ReadJSUtil.OnRunScriptListener() {
                        @Override
                        public void OnReturn(String result) {
                            accountAfterEncryption = result;
                            callVerifyOfChangeUserCode();
                        }

                        @Override
                        public void OnNoFound() {
                        }
                    });
//                }

            } else {
                ((ActivityBase)getActivity()).dismissProgressLoading();
                handleResponseError(result, (ActivityBase) getActivity());

            }

        }
    };

    private void callVerifyOfChangeUserCode(){
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
                SettingHttpUtil.verifyOfChangeUserCode(accountBeforeEncryption, accountAfterEncryption, rKey, responseListener_verifyOfChangeUserCode, getActivity());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_verifyOfChangeUserCode = new ResponseListener() {
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
                ((ActivityBase)getActivity()).showOTPAlertDialog(true, SettingHttpUtil.changeUserCodeApiName.toUpperCase(), mExtension, new ActivityBase.OnOTPListener() {
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
                        onEventListener.OnNextEvent(accountBeforeEncryption, accountAfterEncryption, rKey);
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
