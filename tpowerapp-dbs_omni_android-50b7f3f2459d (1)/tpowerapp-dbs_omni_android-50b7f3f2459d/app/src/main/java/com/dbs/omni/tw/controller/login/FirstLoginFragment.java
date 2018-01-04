package com.dbs.omni.tw.controller.login;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.util.PreferenceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstLoginFragment extends Fragment {
    public final static String TAG = "FirstLoginFragment";

    public final static String ARG_SYS_MESSAGE = "ARG_SYS_MESSAGE";

    private OnLoginListener onLoginListener;

    public void setOnLoginListener(OnLoginListener onFragmentListener) {
        this.onLoginListener = onFragmentListener;
    }

    public interface OnLoginListener {
        void OnLogin(String userCode, String mima, boolean isCheckSaveUser);
    }

    private OnForgetListener onForgetListener;

    public void setOnForgetListener(OnForgetListener listener) {
        this.onForgetListener = listener;
    }

    public interface OnForgetListener {
        void OnForgetAccount();

        void OnForgetMima();
    }

    private EditText editAccount, editMima;
    private TextView forgetText;
    private CheckBox checkboxSaveUser;
    private Button buttonLogin;
    private String mSysMessage;

    public static FirstLoginFragment newInstance(String systemMessage) {

        Bundle args = new Bundle();
        args.putString(ARG_SYS_MESSAGE, systemMessage);

        FirstLoginFragment fragment = new FirstLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_SYS_MESSAGE)) {
            mSysMessage = getArguments().getString(ARG_SYS_MESSAGE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_login, container, false);

        TextView titleText = (TextView) view.findViewById(R.id.text_title);
        titleText.setText(String.format("%1$s %2$s", getString(R.string.login_title), getString(R.string.login_subtitle)));

        TextView textSysMessage = (TextView) view.findViewById(R.id.text_sys_message);
        if (TextUtils.isEmpty(mSysMessage)) {
            textSysMessage.setVisibility(View.GONE);
        } else {
            textSysMessage.setText(mSysMessage);
        }

        buttonLogin = (Button) view.findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });

        editAccount = (EditText) view.findViewById(R.id.edit_account);
        editMima = (EditText) view.findViewById(R.id.edit_mima);

        forgetText = (TextView) view.findViewById(R.id.txt_forgot_password);

        toURLFormattedCallStyle(forgetText, getString(R.string.forgot_account_and_password));

        editAccount.addTextChangedListener(textWatcher);

        editMima.addTextChangedListener(textWatcher);

        editMima.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (TextUtils.isEmpty(editAccount.getText().toString()) || TextUtils.isEmpty(editMima.getText().toString())) {
//                    editAccount.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else {
                    if (buttonLogin.isEnabled()) {
                        gotoLogin();
                    }
                }
                return false;
            }
        });


        checkboxSaveUser = (CheckBox) view.findViewById(R.id.checkbox_save_user);
        if (PreferenceUtil.isSaveCodeStatus(getActivity())) {
            checkboxSaveUser.setChecked(true);
        }
//        checkboxSaveUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                if(!isChecked) {
//                    if(PreferenceUtil.getTouchIDStatus(getActivity())) {
//                        ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.disable_save_user_code), R.string.button_confirm, android.R.string.cancel,
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        PreferenceUtil.setSaveCodeStatus(getActivity(), false);
//                                        PreferenceUtil.setTouchIDStatus(getActivity(), false);
//                                    }
//                                },
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }, false);
//                    } else {
//                        PreferenceUtil.setTouchIDStatus(getActivity(), isChecked);
//                    }
//                } else {
//                    PreferenceUtil.setSaveCodeStatus(getActivity(), isChecked);
//                }
//            }
//        });

        setEnableLoginButton();
        return view;
    }

    private void setEnableLoginButton() {
        String account = editAccount.getText().toString();
        String mima = editMima.getText().toString();

        if ((account.length() >= 6 && account.length() <= 15) && (mima.length() >= 6 && mima.length() <= 30)) {
            buttonLogin.setEnabled(true);
        } else {
            buttonLogin.setEnabled(false);
        }

    }

    private void gotoLogin() {

        String userCode = editAccount.getText().toString();

        onLoginListener.OnLogin(userCode, editMima.getText().toString(), checkboxSaveUser.isChecked());
    }

    private void toURLFormattedCallStyle(TextView view, String content) {

        ClickableSpan forgetAccountClickable = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onForgetListener.OnForgetAccount();
            }
        };

        ClickableSpan forgetMimaClickable = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onForgetListener.OnForgetMima();
            }
        };
        // 帳號
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(forgetAccountClickable,
                3,
                5,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE),
                3,
                5,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 密碼
        spannableString.setSpan(forgetMimaClickable,
                spannableString.length() - 3,
                spannableString.length() - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE),
                spannableString.length() - 3,
                spannableString.length() - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setHighlightColor(android.R.color.white);
        view.setText(spannableString);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setEnableLoginButton();
        }
    };
}
