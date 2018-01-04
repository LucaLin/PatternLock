package com.dbs.omni.tw.controller.register;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.util.http.mode.register.RegisterData;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterIdentityFragment extends Fragment {

    public static final String TAG = "RegisterIdentityFragment";
    private RegisterActivity.OnRegisterListener onRegisterListener;

    public void setOnRegisterListener (RegisterActivity.OnRegisterListener listener) {
        this.onRegisterListener = listener;
    }

    private InputTextView inputTextUserID;
    private Button buttonNext;
    private RegisterData mRegisterData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_identity, container, false);
        buttonNext = (Button) view.findViewById(R.id.btnNextStep);
//        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterData = new RegisterData();
                mRegisterData.setnID(inputTextUserID.getContent());
                onRegisterListener.onNext(mRegisterData);
            }
        });


//        ArrayList<SelectItemData> selectItems = new ArrayList<>();
//        selectItems.add(new SelectItemData(FormatUtil.getPaymentAmountSubTitle(getContext(), true), 234000, false));
//        selectItems.add(new SelectItemData(FormatUtil.getPaymentAmountSubTitle(getContext(), false), 2300, false));
//        selectItems.add(new SelectItemData(getContext().getString(R.string.other_amount_title)));

        inputTextUserID = (InputTextView) view.findViewById(R.id.inputText_user_id);
        inputTextUserID.setOnValidateListener(new InputTextView.OnValidateListener() {
            @Override
            public void OnPass() {
                buttonNext.setEnabled(true);
            }

            @Override
            public void OnFail() {
                buttonNext.setEnabled(false);
            }
        });
//        inputTextUserID.setSelectItems(selectItems, new InputTextView.OnSelectListener() {
//            @Override
//            public void onSelect(SelectItemData item) {
////                ((ActivityBase)getActivity()).showAlertDialog(item.getContent());
//            }
//        });
//        inputTextUserID.setSelectOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(inputTextUserID.isValidatePass()) {
            buttonNext.setEnabled(true);
        } else {
            buttonNext.setEnabled(false);
        }
    }


}
