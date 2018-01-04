package tw.com.taishinbank.ewallet.controller.setting;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;

public class ModifyPhoneOrEmailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ModifyPhoneOrEmailFragment";
    private EditText editCellPhone;
    private EditText editEmail;
    private Button buttonNext;
    private TextView textCellPhoneError;
    private TextView textEmailError;

    private OnButtonNextClickedListener mListener;
    public static final int PAGE_INDEX = 1;
    private EditPersonalInfoActivity.ENUM_UPDATE_ITEM update_item;
    private static final String ARG_ITEM = "ARG_ITEM";

    public static ModifyPhoneOrEmailFragment newInstance(EditPersonalInfoActivity.ENUM_UPDATE_ITEM item) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        ModifyPhoneOrEmailFragment fragment = new ModifyPhoneOrEmailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ModifyPhoneOrEmailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        update_item = (EditPersonalInfoActivity.ENUM_UPDATE_ITEM) getArguments().getSerializable(ARG_ITEM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modify_mail_or_phone, container, false);
        buttonNext = (Button) view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);

        RelativeLayout layout_email = (RelativeLayout) view.findViewById(R.id.recyclerlayout_email);
        RelativeLayout layout_phone = (RelativeLayout) view.findViewById(R.id.recyclerlayout_phone);
        View headline = view.findViewById(R.id.headline);
        String message;
        String notice = null;
        switch (update_item)
        {
            case PHONE:
                message = String.format(getString(R.string.modify_email_phone_subtitle), getString(R.string.modify_phone_subtitle));
                ((ActivityBase)getActivity()).setHeadline(headline, R.string.modify_phone_title, message);
                layout_phone.setVisibility(View.VISIBLE);
                layout_email.setVisibility(View.GONE);
                notice = String.format(getString(R.string.modify_notice), getString(R.string.modify_notice_phone));
                break;

            case EMAIL:
                message = String.format(getString(R.string.modify_email_phone_subtitle), getString(R.string.edit_personal_email_title));
                ((ActivityBase)getActivity()).setHeadline(headline, R.string.modify_email_title, message);
                layout_phone.setVisibility(View.GONE);
                layout_email.setVisibility(View.VISIBLE);
                notice = String.format(getString(R.string.modify_notice), getString(R.string.modify_notice_email));
                break;
        }



        TextView textNotice = (TextView) view.findViewById(R.id.txt_caution_content);
        textNotice.setText(notice);


        // 輸入框
        editCellPhone = (EditText) view.findViewById(R.id.edit_cellphone);
        editEmail = (EditText) view.findViewById(R.id.edit_email);

        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editCellPhone.addTextChangedListener(new BasicEditTextWatcher(editCellPhone, getString(R.string.cellphone_format_regular_expression)) {
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

                // 如果原本有顯示錯誤訊息，當輸入框內容有變化時，隱藏錯誤訊息
                if(textEmailError.getVisibility() == View.VISIBLE){
                    textEmailError.setVisibility(View.GONE);
                }
            }
        });

        // 錯誤訊息
        textCellPhoneError = (TextView) view.findViewById(R.id.text_cellphone_error);
        textEmailError = (TextView) view.findViewById(R.id.text_email_error);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNextButtonEnable();
    }

    @Override
    public void onClick(View view){
        int viewId = view.getId();

        if(viewId == R.id.button_next) {
            // 檢查email格式
            // TODO email格式
            if (update_item.equals(EditPersonalInfoActivity.ENUM_UPDATE_ITEM.EMAIL)) {
                if (FormatUtil.isCorrectFormat(editEmail.getText().toString(), FormatUtil.EMAIL_INVALID_FORMAT)) {
                    // 如果沒有網路連線，顯示提示對話框
                    if (!NetworkUtil.isConnected(getActivity())) {
                        ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, true);
                        return;
                    }
                    ((UserInfoModifyActivity) getActivity()).email = editEmail.getText().toString();
                    mListener.onButtonNextClicked(UserInfoModifyActivity.EMAIL_PAGE_CERTIFICATION);

                } else {
                    textEmailError.setText(getString(R.string.register_email_error));
                    textEmailError.setVisibility(View.VISIBLE);
                }
            }
            else if (update_item.equals(EditPersonalInfoActivity.ENUM_UPDATE_ITEM.PHONE))
            {
                // 如果沒有網路連線，顯示提示對話框
                if (!NetworkUtil.isConnected(getActivity())) {
                    ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, true);
                    return;
                }
                ((UserInfoModifyActivity) getActivity()).phoneNumber = editCellPhone.getText().toString();
                if (mListener != null) {
                    mListener.onButtonNextClicked(UserInfoModifyActivity.PHONE_PAGE_CERTIFICATION);
                }
            }

        }
    }

    /**
     * 檢查是否enable下一步按鈕
     */
    private void checkNextButtonEnable(){
        if(editEmail.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT
            || editCellPhone.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT){
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
    public void onPause() {
        super.onPause();
        ((ActivityBase)getActivity()).hideKeyboard();
    }
}
