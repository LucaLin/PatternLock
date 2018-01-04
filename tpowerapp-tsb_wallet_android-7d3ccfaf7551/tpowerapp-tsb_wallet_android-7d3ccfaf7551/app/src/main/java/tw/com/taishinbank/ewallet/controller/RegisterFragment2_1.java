package tw.com.taishinbank.ewallet.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.DateTimeUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

public class RegisterFragment2_1 extends Fragment implements View.OnClickListener {

    private static final String TAG = "RegisterFragment2_1";

    private Button buttonNext;
    private Button buttonSendAgain;
    private TextView textCountdownTime;
    private TextView textShowEncoded;
    private EditText editCode;

    private CountDownTimer countdownTimer;

    private OnButtonNextClickedListener mListener;
    public static final int PAGE_INDEX = RegisterFragment.PAGE_INDEX + 1;
    public static final int TEN_MINUTES = 10*60*1000;
    public static final int ONE_SECOND = 1000;

    public RegisterFragment2_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register2_1, container, false);
        buttonNext = (Button) view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);
        buttonSendAgain = (Button) view.findViewById(R.id.button_send_again);
        buttonSendAgain.setOnClickListener(this);
        textCountdownTime = (TextView) view.findViewById(R.id.text_countdown_time);
        // 設定上面的標題
        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.register_title2_1, R.string.register_subtitle2_1);

        editCode = (EditText) view.findViewById(R.id.edit_code);

        // 有輸入就可enable下一步的按鈕
        editCode.addTextChangedListener(new BasicEditTextWatcher(editCode, null){
            @Override
            public void afterTextChanged(Editable s){
                super.afterTextChanged(s);
                checkNextButtonEnable();
            }
        });

        textShowEncoded = (TextView) view.findViewById(R.id.text_show);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 設定隱碼顯示文字
        textShowEncoded.setText(FormatUtil.getEncodedCellPhoneNumber(((RegisterActivity) getActivity()).phoneNumber));

        // 如果timer還沒被初始過
        if(countdownTimer == null){
            // TODO refactor
            countdownTimer = new CountDownTimer(TEN_MINUTES+ONE_SECOND, ONE_SECOND) {

                public void onTick(long millisUntilFinished) {
                    millisUntilFinished -= ONE_SECOND;
                    String str = DateTimeUtil.convertMilliSecondsToMmSs(millisUntilFinished);
                    textCountdownTime.setText(str);
                }

                public void onFinish() {
                    buttonSendAgain.setEnabled(true);
                }
            };

            sendCodeRequest();
        }
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

            // 呼叫web service 驗證簡訊認證碼的api
            try {
                GeneralHttpUtil.phoneCertCheck(((RegisterActivity) getActivity()).memNo,
                        editCode.getText().toString(),
                        checkResponseListener, getActivity(), TAG);
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }
        }else if(viewId == R.id.button_send_again){
            // 再次發送簡訊認證碼
            sendCodeRequest();
        }
    }

    private void startTimer(){
        countdownTimer.start();
        buttonSendAgain.setEnabled(false);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(countdownTimer != null){
            countdownTimer.cancel();
            countdownTimer = null;
        }
    }

    /**
     * 發送驗證碼
     */
    private void sendCodeRequest(){
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

        // 呼叫web service 傳送簡訊認證碼的api
        try {
            GeneralHttpUtil.phoneCertRequest(((RegisterActivity) getActivity()).memNo, responseListener, getActivity(), TAG);
            ((ActivityBase)getActivity()).showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    // 呼叫發送驗證碼api的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 成功的話，啟動頁面上的timer
                startTimer();
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    // 呼叫驗證api的listener
    private ResponseListener checkResponseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 開啟下一頁
                if (mListener != null) {
                    mListener.onButtonNextClicked(RegisterFragment2_2.PAGE_INDEX);
                }
            }else{
                // 認證碼不符或手機網路連不到server等手機端問題
                if(returnCode.equals(ResponseResult.RESULT_INVALID_CODE)
                        || ResponseResult.isAppError(returnCode)){
                    // 僅跳對話框顯示驗證碼錯誤
                    showDialog(result.getReturnMessage(), false);

                } else {
                    // 如果不是共同error
                    if(!handleCommonError(result, (ActivityBase) getActivity())) {
                        // TODO 其他不成功的判斷與處理
                        // 跳對話框顯示錯誤訊息，並於按下按鈕重設timer與按鈕
                        showDialog(result.getReturnMessage(), true);
                    }
                }
            }
        }
    };

    private void showDialog(String message, final boolean resetTimer){
        ((ActivityBase)getActivity()).showAlertDialog(message,
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if(resetTimer) {
                            // 停止timer
                            countdownTimer.cancel();
                            // 重設時間文字與按鈕
                            String str = DateTimeUtil.convertMilliSecondsToMmSs(TEN_MINUTES);
                            textCountdownTime.setText(str);
                            buttonSendAgain.setEnabled(true);
                        }
                    }
                }, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((ActivityBase)getActivity()).hideKeyboard();
    }
}
