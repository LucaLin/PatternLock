package tw.com.taishinbank.ewallet.controller.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.DateTimeUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

public class CertificationEmailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CertificationEmailFragment";

    private EditText editCode;
    private Button buttonNext;
    private Button buttonSendAgain;
    private TextView textCountdownTime;
    private TextView textShowEncoded;

    private CountDownTimer countdownTimer;

    private OnButtonNextClickedListener mListener;


    public CertificationEmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modify_mail_or_phone_cert, container, false);
        buttonNext = (Button) view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);
        buttonSendAgain = (Button) view.findViewById(R.id.button_send_again);
        buttonSendAgain.setOnClickListener(this);

        TextView textCode = (TextView) view.findViewById(R.id.text_code);
        textCode.setText(R.string.register_email_code);

        textCountdownTime = (TextView) view.findViewById(R.id.text_countdown_time);
        // 設定上面的標題
        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.register_title2_2, R.string.modify_email_cert_subtitle);

        editCode = (EditText) view.findViewById(R.id.edit_code);
        // 有輸入就可enable下一步的按鈕
        editCode.addTextChangedListener(new BasicEditTextWatcher(editCode, null) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkNextButtonEnable();
            }
        });

        textShowEncoded = (TextView) view.findViewById(R.id.text_show);

        TextView textNotice = (TextView) view.findViewById(R.id.txt_caution_content);
        textNotice.setText(String.format(getString(R.string.modify_notice), getString(R.string.modify_notice_email)));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 設定隱碼顯示文字
        textShowEncoded.setText(FormatUtil.getEncodedEmail(((UserInfoModifyActivity) getActivity()).email));

        if(countdownTimer == null){
            // TODO refactor
            countdownTimer = new CountDownTimer(UserInfoModifyActivity.TEN_MINUTES + UserInfoModifyActivity.ONE_SECOND, UserInfoModifyActivity.ONE_SECOND) {

                public void onTick(long millisUntilFinished) {
                    millisUntilFinished -= UserInfoModifyActivity.ONE_SECOND;
                    String str = DateTimeUtil.convertMilliSecondsToMmSs(millisUntilFinished);
                    textCountdownTime.setText(str);
                }

                public void onFinish() {
                    buttonSendAgain.setEnabled(true);
                }
            };
            sendCodeRequest(true);
        }
        checkNextButtonEnable();
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
                GeneralHttpUtil.modifyEmailCertCheck(editCode.getText().toString(), checkResponseListener, getActivity(), TAG);
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }
        }else if(viewId == R.id.button_send_again){
            // 再次發送驗證碼
            sendCodeRequest(false);
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

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    /**
     * 發送驗證碼
     */
    private void sendCodeRequest(boolean isFisrt){
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
            if(isFisrt) {
                GeneralHttpUtil.modifyEmailCertRequest(((UserInfoModifyActivity) getActivity()).email, "N", responseListener, getActivity(), TAG);
            } else {
                GeneralHttpUtil.modifyEmailCertRequest(((UserInfoModifyActivity) getActivity()).email, "Y", responseListener, getActivity(), TAG);
            }
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
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)|| !GlobalConst.UseOfficialServer) {
                // 成功的話，啟動頁面上的timer
                startTimer();
            }else{
                // 如果不是共同error
               if(returnCode.equals(ResponseResult.RESULT_INVALID_EMAIL_EXISTED_CODE) || returnCode.equals(ResponseResult.RESULT_INVALID_EMAIL_SAME_CODE)) {
                    ((ActivityBase)getActivity()).showAlertDialog(result.getReturnMessage(), R.string.modify_userinfor_cancel_button_cancel, android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendCodeRequest(false);
                                    dialog.dismiss();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getFragmentManager().popBackStack();
                                }
                            },
                             false);
                }  else if(!handleCommonError(result, (ActivityBase) getActivity())){
                    // 跳對話框顯示錯誤訊息，並於按下按鈕重設timer與按鈕
                    showDialog(result.getReturnMessage(), true);
                }
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
            // 如果returnCode是成功;
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS) || !GlobalConst.UseOfficialServer) {
                // 回到編輯首頁
                Intent intent = new Intent(getActivity(), EditPersonalInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(EditPersonalInfoActivity.EXTRA_CHANGE_ITEM, EditPersonalInfoActivity.ENUM_UPDATE_ITEM.EMAIL.toString());
                intent.putExtra(EditPersonalInfoActivity.EXTRA_CHANGE_DATA, ((UserInfoModifyActivity)getActivity()).email);
                startActivity(intent);
            }else{

                // 認證碼不符或手機網路連不到server等手機端問題
                if(returnCode.equals(ResponseResult.RESULT_INVALID_CODE)
                        || ResponseResult.isAppError(returnCode)) {
                    // 僅跳對話框顯示驗證碼錯誤
                    showDialog(result.getReturnMessage(), false);

                // 如果不是共同error
                } else if(!handleCommonError(result, (ActivityBase) getActivity())){
                    // 跳對話框顯示錯誤訊息，並於按下按鈕重設timer與按鈕
                    showDialog(result.getReturnMessage(), true);
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
                            String str = DateTimeUtil.convertMilliSecondsToMmSs(UserInfoModifyActivity.TEN_MINUTES);
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
