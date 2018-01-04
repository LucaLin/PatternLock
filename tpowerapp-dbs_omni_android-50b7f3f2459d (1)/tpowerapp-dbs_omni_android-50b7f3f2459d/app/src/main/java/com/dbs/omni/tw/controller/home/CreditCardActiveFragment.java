package com.dbs.omni.tw.controller.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.ValidateUtil;
import com.dbs.omni.tw.util.http.mode.home.CreditCardData;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreditCardActiveFragment extends Fragment {

    public static final String TAG = "CreditCardActiveFragment";

    public static final String ARG_CARD_DATA = "arg_card_data";

    private OnActiveListener onActiveListener;

    private InputTextView inputTextCardNumber, inputTextEffectiveDate, inputActiveMima;

    public void setOnActiveListener(OnActiveListener listener) {
        this.onActiveListener = listener;
    }

    public interface OnActiveListener {
        void OnActive(String cardNumber, String expDate, String activeMima);
    }

    private Button buttonNext;

    private CreditCardData mCreditCardData;

    public static CreditCardActiveFragment newInstance(CreditCardData data) {

        Bundle args = new Bundle();

        args.putParcelable(ARG_CARD_DATA, data);

        CreditCardActiveFragment fragment = new CreditCardActiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CreditCardActiveFragment newInstance() {

        Bundle args = new Bundle();

        CreditCardActiveFragment fragment = new CreditCardActiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_credit_card_active, container, false);


        if(getArguments() != null && getArguments().containsKey(ARG_CARD_DATA)) {
            mCreditCardData = getArguments().getParcelable(ARG_CARD_DATA);
        }


        buttonNext = (Button) view.findViewById(R.id.btnNextStep);
////        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateInputContent();
            }
        });
//
//
        inputTextCardNumber = (InputTextView) view.findViewById(R.id.inputText_credit_card);
        inputTextCardNumber.setTitle(FormatUtil.getCreditCardTile(getActivity(), true));
        inputTextCardNumber.setOnFinishEdit(onFinishEditListener);

        inputTextEffectiveDate = (InputTextView) view.findViewById(R.id.inputText_effective_date);
        inputTextEffectiveDate.setTitle(FormatUtil.getEffectiveDate(getActivity(), true));
        inputTextEffectiveDate.setOnFinishEdit(onFinishEditListener);

        inputActiveMima = (InputTextView) view.findViewById(R.id.inputText_active_mima);
        inputActiveMima.setOnFinishEdit(onFinishEditListener);

        if(mCreditCardData != null) {
            inputTextCardNumber.setContent(mCreditCardData.getCcNO());
        }

        return view;
    }


    //檢查輸入內容
    private boolean isEnableNextButton(boolean isShowAlert) {
        boolean isEnable = true;

        isEnable = isEnable & inputActiveMima.getContent().length() != 0;

        isEnable = isEnable & ValidateUtil.checkEffectiveDate(getContext(),inputTextEffectiveDate.getContent().toString(), isShowAlert);
        isEnable = isEnable & ValidateUtil.checkCreditCardNumber(getContext(),inputTextCardNumber.getContent().toString(),isShowAlert);


        buttonNext.setEnabled(isEnable);
        return isEnable;
    }

    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableNextButton(false);
        }
    };

    private void ValidateInputContent() {
        if(isEnableNextButton(true)) {
            onActiveListener.OnActive(  FormatUtil.removeCreditCardFormat(inputTextCardNumber.getContent()),
                                        FormatUtil.removeDateFormatted(inputTextEffectiveDate.getContent()),
                                        inputActiveMima.getContent());
        }
    }
}
