package tw.com.taishinbank.ewallet.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class RegisterFragment3_1 extends Fragment implements View.OnClickListener {

    private static final String TAG = "RegisterFragment3_1";

    private EditText editMima;
    private EditText editMimaAgain;
    private Button buttonFinish;

    private OnButtonNextClickedListener mListener;
    public static final int PAGE_INDEX = RegisterFragment2_2.PAGE_INDEX + 1;

    public RegisterFragment3_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register3_1, container, false);
        buttonFinish = (Button) view.findViewById(R.id.button_finish);
        buttonFinish.setOnClickListener(this);

        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.register_title3_1, R.string.register_subtitle3_1);

        editMima = (EditText) view.findViewById(R.id.edit_password);
        editMimaAgain = (EditText) view.findViewById(R.id.edit_password_again);

        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editMima.addTextChangedListener(new BasicEditTextWatcher(editMima, getString(R.string.password_format_regular_expression)) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 做父類別的欄位格式檢核
                super.onTextChanged(s, start, before, count);

                // 通知第二個密碼欄位更新狀態
                editMimaAgain.setText(editMimaAgain.getText().toString());
            }

        });

        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editMimaAgain.addTextChangedListener(new BasicEditTextWatcher(editMimaAgain, getString(R.string.password_format_regular_expression)) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 做父類別的欄位格式檢核
                super.onTextChanged(s, start, before, count);
                // 如果格式正確再來檢查跟第一個密碼一不一樣
                if (editMimaAgain.getBackground().getLevel() == EDIT_LEVEL_CORRECT) {
                    // 如果一樣，狀態為Correct
                    if (editMima.getText().toString().equals(editMimaAgain.getText().toString())) {
                        setLevel(EDIT_LEVEL_CORRECT);
                    } else {// 否則為Error
                        setLevel(EDIT_LEVEL_ERROR);
                    }
                }
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
        // 按完成的處理
        if(viewId == R.id.button_finish) {
            // 如果沒有網路連線，顯示提示對話框
            if(!NetworkUtil.isConnected(getActivity())){
                ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
                return ;
            }

            // 呼叫web service設定密碼
            try {
                String pwInAES = sharedMethods.AESEncrypt(editMima.getText().toString());
                GeneralHttpUtil.setMemberPassword(pwInAES, ((RegisterActivity) getActivity()).memNo,
                        responseListener, getActivity(), TAG);
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 檢查是否enable按鈕
     */
    private void checkNextButtonEnable(){
        if(editMima.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
            && editMimaAgain.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT){
            buttonFinish.setEnabled(true);
        }else{
            buttonFinish.setEnabled(false);
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

    // 呼叫api的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 成功的話，跳至下一頁
                if (mListener != null) {
                    mListener.onButtonNextClicked(RegisterFragment3_2.PAGE_INDEX);
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
