package tw.com.taishinbank.ewallet.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

public class ResetPasswordFragment2 extends Fragment implements View.OnClickListener {

    private static final String TAG = "ResetPasswordFragment2";

    private EditText editCode;
    private Button buttonNext;

    private OnButtonNextClickedListener mListener;
    public static final int PAGE_INDEX = ResetPasswordFragment1.PAGE_INDEX + 1;

    public ResetPasswordFragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password_2, container, false);
        buttonNext = (Button) view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);

        // 設定上面的標題
        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.reset_password, R.string.reset_password_subtitle_2);

        editCode = (EditText) view.findViewById(R.id.edit_code);
        // 有輸入就可enable下一步的按鈕
        editCode.addTextChangedListener(new BasicEditTextWatcher(editCode, null) {
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
        // 按下一步的處理
        if(viewId == R.id.button_next){
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

            ResetPasswordActivity resetMimaActivity = (ResetPasswordActivity)getActivity();

            // 呼叫web service 驗證email系統預設密碼的api
            try {
                GeneralHttpUtil.resetPwdEmailCertCheck(resetMimaActivity.verifyID, resetMimaActivity.memNo,
                        editCode.getText().toString(),
                        checkResponseListener, getActivity(), TAG);
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }
        }
    }

    /**
     * 檢查是否enable按鈕
     */
    private void checkNextButtonEnable(){
        if(editCode.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT){
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

    // 呼叫驗證api的listener
    private ResponseListener checkResponseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            // 如果returnCode是成功;
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 開啟下一頁
                if (mListener != null) {
                    mListener.onButtonNextClicked(ResetPasswordFragment3.PAGE_INDEX);
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
