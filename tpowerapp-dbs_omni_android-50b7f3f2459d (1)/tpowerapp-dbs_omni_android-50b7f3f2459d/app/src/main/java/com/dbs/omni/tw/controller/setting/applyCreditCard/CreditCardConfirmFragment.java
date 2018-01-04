package com.dbs.omni.tw.controller.setting.applyCreditCard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.CreditCardSelectedItemView;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.model.setting.ApplyCreditCardData;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreditCardConfirmFragment extends Fragment {

    public static final String SELECT_CARD_LIST = "SELECT_CARD_LIST";
    public static final String CARD_LIST = "CARD_LIST";

    private LayoutInflater inflater;
    private LinearLayout linearLayoutCardSelected;
    private Button btnNextStep;
    private InputTextView inputTextDefaultPhoneNumber;

    private CreditCardConfirmFragment.OnEventListener onEventListener;

    private int selectCount = 0;

    public interface OnEventListener {
        void OnNextEvent();
    }

    public void setOnEventListener (CreditCardConfirmFragment.OnEventListener listener) {
        this.onEventListener = listener;
    }

    public static CreditCardConfirmFragment newInstance(ArrayList<String> selectContents, ArrayList<ApplyCreditCardData> cardContents) {

        Bundle args = new Bundle();
        args.putStringArrayList(SELECT_CARD_LIST, selectContents);
        args.putParcelableArrayList(CARD_LIST, cardContents);

        CreditCardConfirmFragment fragment = new CreditCardConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActivityBase) getActivity()).setHeadHide(false);

        inflater = getActivity().getLayoutInflater();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_credit_card_confirm, container, false);

        btnNextStep = (Button)view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnListener);
        btnNextStep.setEnabled(false);

        inputTextDefaultPhoneNumber = (InputTextView) view.findViewById(R.id.inputText_Default_PhoneNumber);
        inputTextDefaultPhoneNumber.setTitle(R.string.default_phone_number);
        inputTextDefaultPhoneNumber.setOnFinishEdit(onFinishEditListener);

        linearLayoutCardSelected = (LinearLayout) view.findViewById(R.id.linearLayoutCardSelected);

        //依照array長度,依序add view
        for(int i=0 ; i<((ApplyCreditCardActivity) getActivity()).mSelectCardList.size() ; i++){
            if(!((ApplyCreditCardActivity) getActivity()).mSelectCardList.get(i).isEmpty()){
                addItemView(linearLayoutCardSelected, Integer.valueOf(((ApplyCreditCardActivity) getActivity()).mSelectCardList.get(i)));
            }
        }

        selectCount = ((ApplyCreditCardActivity) getActivity()).mSelectCardList.size();

        return view;
    }

    //region add scroll view item的method
    private void addItemView(final LinearLayout rootLayout, int selcetedIndex){

        ApplyCreditCardData data = ((ApplyCreditCardActivity) getActivity()).mCardList.get(selcetedIndex);

        CreditCardSelectedItemView itemView = new CreditCardSelectedItemView(getContext(), data, selcetedIndex);
        itemView.isEnableCancelButton(true);
        itemView.setOnItemCancelListener(new CreditCardSelectedItemView.OnItemCancelListener() {
            @Override
            public void OnCancelSelect(View view, int selectIndex) {
                ((ApplyCreditCardActivity) getActivity()).mSelectCardList.remove(String.valueOf(selectIndex));
                ((ApplyCreditCardActivity) getActivity()).mCardViewList.get(selectIndex).cancelSelect();
                rootLayout.removeView(view);

                selectCount -= 1;
                isEnableNextButton();
            }
        });

        rootLayout.addView(itemView);
    }

    private Button.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onEventListener.OnNextEvent();
        }
    };

    //檢查輸入內容
    private void isEnableNextButton() {
        if(selectCount > 0){
            if(inputTextDefaultPhoneNumber.getContent() != null && !inputTextDefaultPhoneNumber.getContent().isEmpty()){
                btnNextStep.setEnabled(true);
                return;
            }
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
