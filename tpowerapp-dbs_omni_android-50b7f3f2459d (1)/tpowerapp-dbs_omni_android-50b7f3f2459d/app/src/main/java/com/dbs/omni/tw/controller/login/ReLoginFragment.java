package com.dbs.omni.tw.controller.login;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.util.BitmapUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.fingeprint.FingerprintAuthenticationDialogFragment;
import com.dbs.omni.tw.util.fingeprint.FingerprintUtil;
import com.dbs.omni.tw.util.http.SettingHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.SettingResponseBodyUtil;

import org.json.JSONException;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReLoginFragment extends Fragment {

    public final static String ARG_SYS_MESSAGE = "ARG_SYS_MESSAGE";

    private OnReLoginListener onReLoginListener;

    public void setOnReLoginListener (OnReLoginListener onReLoginListener) {
        this.onReLoginListener = onReLoginListener;
    }

    private FirstLoginFragment.OnForgetListener onForgetListener;
    public void setOnForgetListener(FirstLoginFragment.OnForgetListener listener) {
        this.onForgetListener = listener;
    }
    public interface OnForgetListener {
        void OnForgetAccount();
        void OnForgetMima();
    }

    public interface OnReLoginListener {
        void onLogin(String mima);
        void onChangeUser();
        void onTouchIDLogin();
    }

    private EditText editMima;
    private Button buttonLogin;
    private String mSysMessage;

    public static ReLoginFragment newInstance(String systemMessage) {

        Bundle args = new Bundle();
        args.putString(ARG_SYS_MESSAGE, systemMessage);

        ReLoginFragment fragment = new ReLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_SYS_MESSAGE)) {
            mSysMessage = getArguments().getString(ARG_SYS_MESSAGE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_re_login, container, false);

        TextView titleText = (TextView) view.findViewById(R.id.text_title);
        titleText.setText(String.format("%1$s %2$s", getString(R.string.login_title), getString(R.string.login_subtitle)));

        TextView textSysMessage = (TextView) view.findViewById(R.id.text_sys_message);
        if(TextUtils.isEmpty(mSysMessage)) {
            textSysMessage.setVisibility(View.GONE);
        } else {
            textSysMessage.setText(mSysMessage);
        }

        TextView textUserName = (TextView) view.findViewById(R.id.text_user_name);
        textUserName.setText(String.format(getString(R.string.reLogin_nickname_format), PreferenceUtil.getNickname(getActivity())));

        buttonLogin = (Button) view.findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReLoginListener.onLogin(editMima.getText().toString());
            }
        });

        editMima = (EditText) view.findViewById(R.id.edit_mima);

        TextView textView = (TextView) view.findViewById(R.id.text_change_user);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReLoginListener.onChangeUser();
            }
        });
//        Button button = (Button) findViewById(R.id.park);
//        button

        TextView forgetText = (TextView) view.findViewById(R.id.txt_forgot_password);

        toURLFormattedCallStyle(forgetText, getString(R.string.forgot_account_and_password));
//        editAccount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(TextUtils.isEmpty(editAccount.getTextContent().toString()) || TextUtils.isEmpty(editMima.getTextContent().toString()) ) {
////                    editAccount.setImeOptions(EditorInfo.IME_ACTION_DONE);
//                } else {
//                    goToHome();
//                }
//
//                return false;
//            }
//        });


        editMima.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(TextUtils.isEmpty(editMima.getText().toString()) ) {
//                    editAccount.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else {
                    if(buttonLogin.isEnabled()) {
                        onReLoginListener.onLogin(editMima.getText().toString());
                    }
                }
                return false;
            }
        });

        editMima.addTextChangedListener(new TextWatcher() {
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
        });

        if(PreferenceUtil.getTouchIDStatus(getActivity())) {
            getTouchIDStatus();
        }

        setHeaderImage(view);
        setEnableLoginButton();
        return view;
    }

    private void setEnableLoginButton() {
        String mima = editMima.getText().toString();

        if ((mima.length() >= 6 && mima.length() <= 30)) {
            buttonLogin.setEnabled(true);
        } else {
            buttonLogin.setEnabled(false);
        }

    }

    private void setHeaderImage (View view) {
        ImageView image_avatar = (ImageView)view.findViewById(R.id.image_avatar);
        //設定頭像
        String folderPath = GlobalConst.FolderPath;
        String filePath = folderPath + File.separator + "avatar" + ".png";
        int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        File imgFile = new File(filePath);
        Bitmap bmAva = BitmapUtil.decodeSampledBitmap(imgFile.getAbsolutePath(), photoSize, photoSize);

        if (bmAva != null){
            image_avatar.setImageBitmap(bmAva);
        }
    }


//    private void goToHome () {
//        PreferenceUtil.setIsLogin(getActivity(), true);
//        Intent intent = new Intent(getActivity(), MainActivity.class);
////        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

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
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE) ,
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
//region TouchID
    private void checkTouchID (String fplStatus, String devUUID)
    {
        if(fplStatus.equalsIgnoreCase("1")) {
            String currentDevUUID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            if(devUUID.equals(currentDevUUID)) {
                FingerprintUtil.launcherFingerprint(getActivity(), new FingerprintAuthenticationDialogFragment.OnFingerprinListener() {
                    @Override
                    public void OnAuthenticated() {
                        //以後帶touch ID 密碼
                        onReLoginListener.onTouchIDLogin();
                    }
                });
            } else {
                PreferenceUtil.cleanTouchIDData(getContext());
            }
        } else {
            PreferenceUtil.cleanTouchIDData(getContext());
        }


    }

//endregion
//region api
    private void getTouchIDStatus() {
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
                SettingHttpUtil.getTouchIDStatus(responseListener, getActivity());
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
                String fplStatus = SettingResponseBodyUtil.getFplStatus(result.getBody());
                String devUUID = SettingResponseBodyUtil.getDevUUID(result.getBody());
                checkTouchID(fplStatus, devUUID);

            } else {
                handleResponseError(result, ((ActivityBase)getActivity()));
            }

        }
    };
//endregion
}
