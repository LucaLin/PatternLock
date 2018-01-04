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
import com.dbs.omni.tw.util.AddressUtil;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.UserInfoUtil;
import com.dbs.omni.tw.util.http.SettingHttpUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressModifyFragment extends Fragment {

    private InputTextView inputText_PostCode,inputText_NewCity, inputText_NewRegion,inputText_NewAddress;
    private InputTextView inputText_OldAddress;
    private Button btnNextStep;

    private OnEventListener onEventListener;

    private boolean isChangePostal = true;

    private String inputZipCode = "";
    private String inputCity = "";
    private String inputDistrict = "";
    private String inputAddress = "";

    public interface OnEventListener {
        void OnNextEvent(String inputZipCode, String inputCity, String inputDistrict, String inputAddress);
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
        View view = inflater.inflate(R.layout.fragment_address_modify, container, false);

        btnNextStep = (Button)view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnListener);
        btnNextStep.setEnabled(false);

        inputText_OldAddress = (InputTextView) view.findViewById(R.id.inputText_OldAddress);
        inputText_OldAddress.setTitle(R.string.old_address);
        if(UserInfoUtil.getsAddressDetail() != null){
            String stringAddress = UserInfoUtil.getsAddressDetail().getAddress();
            inputText_OldAddress.setContent(FormatUtil.getHiddenAddress(stringAddress));
        }

        inputText_PostCode = (InputTextView) view.findViewById(R.id.inputText_PostCode);
        inputText_PostCode.setTitle(R.string.postal_code);
        inputText_PostCode.setMaxLength(3);
        inputText_PostCode.setDigits(getContext().getResources().getString(R.string.only_number_capital_only));
        inputText_PostCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputText_PostCode.setOnFinishEdit(new InputTextView.OnFinishEditListener() {
            @Override
            public void OnFinish() {
                if(isChangePostal) {
                    AddressUtil addressUtil = new AddressUtil();
                    String city = addressUtil.getCity(inputText_PostCode.getContent());
                    String region = addressUtil.getRegion(inputText_PostCode.getContent());
                    inputText_NewCity.setContent(city);
                    inputText_NewRegion.setBaseCityInoutText(city);
                    inputText_NewRegion.setContent(region);
                } else {
                    isChangePostal = true;
                }

                isEnableNextButton();
            }
        });


        inputText_NewCity = (InputTextView) view.findViewById(R.id.inputText_NewCity);
        inputText_NewCity.setTitle(R.string.input_new_city);
        inputText_NewCity.setOnCityAndRegionListener(new InputTextView.OnCityAndRegionListener() {
            @Override
            public void OnCityFinish(String city) {
                isChangePostal = false;
                inputText_PostCode.setContent("");
                inputText_NewRegion.setBaseCityInoutText(inputText_NewCity.getContent());
                isEnableNextButton();
            }

            @Override
            public void OnRegionFinish(String region, String postal) {

            }
        });


        inputText_NewRegion = (InputTextView) view.findViewById(R.id.inputText_NewRegion);
        inputText_NewRegion.setTitle(R.string.input_new_county);
        inputText_NewRegion.setOnCityAndRegionListener(new InputTextView.OnCityAndRegionListener() {
            @Override
            public void OnCityFinish(String city) {

            }

            @Override
            public void OnRegionFinish(String region, String postal) {
                isChangePostal = false;
                inputText_PostCode.setContent(postal);
                isEnableNextButton();
            }
        });

        inputText_NewAddress = (InputTextView) view.findViewById(R.id.inputText_NewAddress);
        inputText_NewAddress.setTitle(R.string.input_new_address);
        inputText_NewAddress.setOnFinishEdit(onFinishEditListener);

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

            ((ActivityBase)getActivity()).showOTPAlertDialog(true, SettingHttpUtil.updateAddressApiName.toUpperCase(), "", new ActivityBase.OnOTPListener() {
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
                    onEventListener.OnNextEvent(inputZipCode, inputCity, inputDistrict, inputAddress);
                }
            });
        }
    };

    //檢查輸入內容
    private void isEnableNextButton() {
        if(!TextUtils.isEmpty(inputText_PostCode.getContent()) && !TextUtils.isEmpty(inputText_NewCity.getContent()) &&
                !TextUtils.isEmpty(inputText_NewRegion.getContent()) && !TextUtils.isEmpty(inputText_NewAddress.getContent())){
            inputZipCode = inputText_PostCode.getContent();
            inputCity = inputText_NewCity.getContent();
            inputDistrict = inputText_NewRegion.getContent();
            inputAddress = inputText_NewAddress.getContent();
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

}
