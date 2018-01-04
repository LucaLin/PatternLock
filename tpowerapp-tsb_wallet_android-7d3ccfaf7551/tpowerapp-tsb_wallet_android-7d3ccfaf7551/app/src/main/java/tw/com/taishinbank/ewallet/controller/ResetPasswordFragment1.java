package tw.com.taishinbank.ewallet.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class ResetPasswordFragment1 extends Fragment implements View.OnClickListener {

    private static final String TAG = "ResetPasswordFragment1";
    private EditText editUserId;
    private EditText editEmail;
    private Button buttonNext;
    private String userIdInAES;

    private OnButtonNextClickedListener mListener;
    public static final int PAGE_INDEX = 1;


    public ResetPasswordFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password_1, container, false);
        buttonNext = (Button) view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);
        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.reset_password, R.string.reset_password_subtitle_1);

        // 輸入框
        editUserId = (EditText) view.findViewById(R.id.edit_userid);
        editEmail = (EditText) view.findViewById(R.id.edit_email);

        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editUserId.addTextChangedListener(new BasicEditTextWatcher(editUserId, getString(R.string.userid_format_regular_expression)){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if(getLevel() == EDIT_LEVEL_CORRECT){
                    if(!sharedMethods.validateUserIDforROC(s.toString())){
                        setLevel(EDIT_LEVEL_ERROR);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkNextButtonEnable();
            }
        });

        editEmail.addTextChangedListener(new BasicEditTextWatcher(editEmail, FormatUtil.EMAIL_INVALID_FORMAT) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkNextButtonEnable();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNextButtonEnable();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onClick(View view){
        int viewId = view.getId();
        // 按下登入的處理
        if(viewId == R.id.button_login){
            // 切換至登入頁
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else if(viewId == R.id.button_next){
            // 如果沒網路連線，顯示提示對話框
            if(!NetworkUtil.isConnected(getActivity())){
                ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
                return ;
            }

            // 呼叫 web service
            try {
                userIdInAES = sharedMethods.AESEncrypt(editUserId.getText().toString());
                GeneralHttpUtil.resetPwdEmailCertRequest(userIdInAES, editEmail.getText().toString(), responseListener, getActivity(), TAG);
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 檢查是否enable下一步按鈕
     */
    private void checkNextButtonEnable(){
        if(editUserId.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
            && editEmail.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT){
            buttonNext.setEnabled(true);
        }else{
            buttonNext.setEnabled(false);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnButtonNextClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                ResetPasswordActivity activity = ((ResetPasswordActivity) getActivity());

                // 將接下來需要的資訊先暫存在activity
                activity.memNo = GeneralResponseBodyUtil.getMemNo(result.getBody());
                activity.verifyID = GeneralResponseBodyUtil.getVerifyID(result.getBody());

                // 跳至下個頁面
                if (mListener != null) {
                    mListener.onButtonNextClicked(ResetPasswordFragment2.PAGE_INDEX);
                }
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        ((ActivityBase)getActivity()).hideKeyboard();
    }
}