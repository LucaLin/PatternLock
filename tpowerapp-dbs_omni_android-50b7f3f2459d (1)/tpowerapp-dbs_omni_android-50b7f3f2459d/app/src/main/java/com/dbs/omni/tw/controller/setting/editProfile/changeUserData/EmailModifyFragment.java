package com.dbs.omni.tw.controller.setting.editProfile.changeUserData;


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
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.UserInfoUtil;
import com.dbs.omni.tw.util.http.SettingHttpUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailModifyFragment extends Fragment {

    private Button btnNextStep;
    private InputTextView inputTextNewEmail, inputTextNewEmailAgain, iputTextView_OldEmail;
    private EmailModifyFragment.OnEventListener onEventListener;

    String inputEmail = "";

    public interface OnEventListener {
        void OnNextEvent(String newEmail);
    }

    public void setOnEventListener(EmailModifyFragment.OnEventListener listener) {
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
        View view = inflater.inflate(R.layout.fragment_email_modify, container, false);

        btnNextStep = (Button) view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnListener);
        btnNextStep.setEnabled(false);

        iputTextView_OldEmail = (InputTextView) view.findViewById(R.id.iputTextView_OldEmail);
        iputTextView_OldEmail.setTitle(R.string.old_email);
        if (UserInfoUtil.getsEmailDetail() != null) {
            String stringEmail = UserInfoUtil.getsEmailDetail().getEmail();
            iputTextView_OldEmail.setContent(FormatUtil.getHiddenEmail(stringEmail));
        }

        inputTextNewEmail = (InputTextView) view.findViewById(R.id.inputText_NewEmail);
        inputTextNewEmail.setTitle(R.string.input_new_email);
        inputTextNewEmail.setOnFinishEdit(onFinishEditListener);

        inputTextNewEmailAgain = (InputTextView) view.findViewById(R.id.inputText_NewEmail_Again);
        inputTextNewEmailAgain.setTitle(R.string.input_new_email_again);
        inputTextNewEmailAgain.setOnFinishEdit(onFinishEditListener);

        return view;
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
            if(getActivity() == null)
                return;

            //顯示Email OTP
            ((ActivityBase)getActivity()).showOTPAlertDialogEmail(true, SettingHttpUtil.ChangeMimaApiName.toUpperCase(), inputEmail, new ActivityBase.OnOTPListener() {
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
                    onEventListener.OnNextEvent(inputEmail);
                }

            });
        }
    };

    //檢查輸入內容
    private void isEnableNextButton() {
        if (confirmEmail()) {
            inputEmail = inputTextNewEmail.getContent();
            btnNextStep.setEnabled(true);
        } else {
            btnNextStep.setEnabled(false);
        }
    }

    private InputTextView.OnFinishEditListener onFinishEditListener = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton();
        }
    };

    private boolean confirmEmail() {
        String Email = inputTextNewEmail.getContent();
        String EmailAgain = inputTextNewEmailAgain.getContent();
        if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(EmailAgain)) {
            return false;
        } else {
            if (!Email.equals(EmailAgain)) {
                ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.validate_email_confirm_fail));
                return false;
            } else {
                return true;
            }
        }
    }


}
