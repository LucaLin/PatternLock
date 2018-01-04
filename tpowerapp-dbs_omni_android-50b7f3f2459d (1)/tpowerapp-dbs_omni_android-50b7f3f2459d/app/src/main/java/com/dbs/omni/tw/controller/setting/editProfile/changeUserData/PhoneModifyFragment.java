package com.dbs.omni.tw.controller.setting.editProfile.changeUserData;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
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
public class PhoneModifyFragment extends Fragment {

    private Button btnNextStep;
    private InputTextView inputTextCountryCode, inputTextNewPhone, inputTextView_OldPhone;

    private PhoneModifyFragment.OnEventListener onEventListener;

    private String newPhone = "";
    private String newCountryCode = "";
    private boolean isPhoneNumberCorrect;

    public interface OnEventListener {
        void OnNextEvent(String newPhone, String newCountryCode);
    }

    public void setOnEventListener (PhoneModifyFragment.OnEventListener listener) {
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
        View view = inflater.inflate(R.layout.fragment_phone_modify, container, false);

        btnNextStep = (Button)view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnListener);
        btnNextStep.setEnabled(false);

        inputTextView_OldPhone = (InputTextView) view.findViewById(R.id.inputTextView_OldPhone);
        inputTextView_OldPhone.setTitle(R.string.old_phone);
        if(UserInfoUtil.getsPhoneDetail() != null){
            String stringPhoneNumber = UserInfoUtil.getsPhoneDetail().getPhoneNumber();
            inputTextView_OldPhone.setContent(FormatUtil.getHiddenPhoneNumber(stringPhoneNumber));
        }

        inputTextNewPhone = (InputTextView) view.findViewById(R.id.inputText_NewPhone);
        inputTextNewPhone.setTitle(R.string.input_new_phone);
        inputTextNewPhone.setOnFinishEdit(onFinishEditListener_PhoneNumber);

        inputTextCountryCode = (InputTextView) view.findViewById(R.id.inputText_CountryCode);
        inputTextCountryCode.setTitle(R.string.input_country_code);
        inputTextCountryCode.setOnFinishEdit(onFinishEditListener_CountryCode);
        inputTextCountryCode.setDigits(getContext().getResources().getString(R.string.only_number_capital_only));
        inputTextCountryCode.setMaxLength(getContext().getResources().getInteger(R.integer.countrycode_maxlength));
        inputTextCountryCode.setInputType(InputType.TYPE_CLASS_PHONE);
        inputTextCountryCode.setContent(getString(R.string.taiwan_country_code));

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

            //組出OTP要用的電話號碼 , 如果國碼為+886且電話第一碼為0時 , 去掉第一碼
            String phoneTemp;
            if(newCountryCode.equals(getString(R.string.taiwan_country_code)) && newPhone.substring(0,1).equals("0")){
                newPhone = newPhone.substring(1);
            }

            phoneTemp = newCountryCode + newPhone;

            ((ActivityBase)getActivity()).showOTPAlertDialog(true, SettingHttpUtil.updatePhoneCodeApiName.toUpperCase(), phoneTemp, new ActivityBase.OnOTPListener() {
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
                    onEventListener.OnNextEvent(newPhone, newCountryCode);
                }

//                @Override
//                public void OnEnter(AlertDialog dialog) {
//                    dialog.dismiss();
//                    onEventListener.OnNextEvent();
//                }
            });
        }
    };

    //檢查輸入內容
    private void isEnableNextButton() {
        if(!TextUtils.isEmpty(inputTextCountryCode.getContent())) {
            if (isPhoneNumberCorrect == true){
                newPhone = inputTextNewPhone.getContent();
                newCountryCode = inputTextCountryCode.getContent();
                btnNextStep.setEnabled(true);
                return;
            }
        }
        btnNextStep.setEnabled(false);
    }

    private InputTextView.OnFinishEditListener onFinishEditListener_PhoneNumber  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isPhoneNumberCorrect = true;
            String string = inputTextNewPhone.getContent();
            int stringLengthOri = string.length(); //取得輸入的原長度 , 用來避免下方無窮迴圈

            //如果為台灣國碼
            if(inputTextCountryCode.getContent().equals(getString(R.string.taiwan_country_code))){
                //如果長度不合就顯示alert
                if(string.length() < 9 || string.length() > 10){
                    isPhoneNumberCorrect = false;
                    ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.validate_fail));
                }else{
                    //如果長度為9則補0
                    if(string.length() == 9){
                        string = "0" + string;
                    }

                    //如果不符合長度為10且為09開頭則顯示alert
                    if(!string.matches(getContext().getString(R.string.cellphone_format_regular_expression))){
                        isPhoneNumberCorrect = false;
                        ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.validate_fail));
                    }else if(stringLengthOri == 9){ //原長度若是9需要把值回填到UI
                        inputTextNewPhone.setContent(string);
                    }
                }
            }
            isEnableNextButton();
        }
    };

    private InputTextView.OnFinishEditListener onFinishEditListener_CountryCode  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            String string = inputTextCountryCode.getContent();

            //如果沒有輸入就帶+886 , 如果為其他就加上+號
            if (TextUtils.isEmpty(string)){
                inputTextCountryCode.setContent(getString(R.string.taiwan_country_code));
            }else if(!string.substring(0,1).equals("+")){
                string = "+" + string;
                inputTextCountryCode.setContent(string);
            }
            isEnableNextButton();
        }
    };



}
