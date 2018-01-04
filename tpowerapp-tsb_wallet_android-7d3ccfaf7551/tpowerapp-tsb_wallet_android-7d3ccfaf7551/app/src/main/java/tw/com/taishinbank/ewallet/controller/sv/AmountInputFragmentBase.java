package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class AmountInputFragmentBase extends Fragment implements View.OnClickListener{

    private static final String TAG_DIALOG_FRAGMENT = "dialog";
    protected EditText editAmount;
    protected int maxAmountLimit;
    protected int inputLengthLimit;
    protected Button buttonNext;
    protected View layoutInfo;
    protected TextView textInfo;
    protected ImageView imageWarning;
    protected ImageView imageInfoArrow;
    protected SVAccountInfo svAccountInfo;
    protected TextView textBankAccountTitle;
    protected View layoutAccountInfo;
    protected View layoutBankAccountInfo;
    protected TextView textAccount;
    protected TextView textBankTitle;
    protected ImageView imagePhoto;
    protected TextView textNames;
    protected TextView textNamesNumber;

    protected AmountInputListener listener;

    protected boolean inputButtonsClickable = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sv_amount_input, container, false);

        // 初始輸入框的顯示數值
        editAmount = (EditText) view.findViewById(R.id.edit_amount);
        editAmount.setText("0");

        buttonNext = (Button) view.findViewById(R.id.button_next);
        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(this);

        imageWarning = (ImageView) view.findViewById(R.id.image_warning);
        textInfo = (TextView) view.findViewById(R.id.text_info);

        imageInfoArrow = (ImageView) view.findViewById(R.id.btn_info_arrow);

        layoutInfo = view.findViewById(R.id.layout_info);
        layoutInfo.setOnClickListener(this);
        layoutInfo.setClickable(true);

        // 設各個按鈕的clickListener
        setInputButtonsOnClickListener(view);

        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());

        textBankAccountTitle = (TextView) view.findViewById(R.id.text_bank_account_title);

        // 根據頁面隱藏/顯示layout
        layoutAccountInfo = view.findViewById(R.id.layout_account_info);
        layoutBankAccountInfo = view.findViewById(R.id.layout_bank_account_info);

        textAccount = (TextView) view.findViewById(R.id.text_account);
        textBankTitle = (TextView) view.findViewById(R.id.text_bank_title);

        imagePhoto = (ImageView) view.findViewById(R.id.image_photo);
        textNames = (TextView) view.findViewById(R.id.text_names);
        textNamesNumber = (TextView) view.findViewById(R.id.text_names_number);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 根據文字內容，更新按鈕狀態
        String amountString = (editAmount.getText() != null) ? editAmount.getText().toString() : "";
        // 如果已包含逗號，先去掉逗號
        if(amountString.contains(",")){
            amountString = amountString.replace(",", "");
        }

        long amountNum;
        // 如果為空字串，則設為0，否則設為字串表示的數值
        if (amountString.equals("")) {
            amountNum = 0;
        } else {
            amountNum = Long.parseLong(amountString);
        }

        updateButtonStatusWithAmount(amountNum);
    }

    private void setInputButtonsOnClickListener(View view){
        int[] buttonResIds = {R.id.btn_delete, R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
                R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9};
        for (int buttonResId : buttonResIds) {
            View button = view.findViewById(buttonResId);

            if(inputButtonsClickable) {
                button.setOnClickListener(this);
            }else{
                button.setClickable(false);
            }
        }
    }

    /**
     * 在layout有指定每個按鈕onClick會執行此方法
     */
    public void onButtonClick(View view) {
        int id = view.getId();
        String amountString = (editAmount.getText() != null) ? editAmount.getText().toString() : "";
        // 如果已包含逗號，先去掉逗號
        if(amountString.contains(",")){
            amountString = amountString.replace(",", "");
        }

        long amountNum;

        // 判斷按下的按鈕
        switch (id) {
            // 如果是數字鍵，在目前輸入框文字加上數字
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
                // 如果輸入長度已達上限，不允許輸入
                if(!amountString.equals("0") && amountString.length() >= inputLengthLimit){
                    return ;
                }
                amountString += view.getTag();
                break;
            // 如果是刪除鍵，將最右邊的數字拿掉
            case R.id.btn_delete:
                if (amountString.length() <= 1) {
                    amountString = "";
                } else {
                    amountString = amountString.substring(0, amountString.length() - 1);
                }
                break;
        }

        // 如果為空字串，則設為0，否則設為字串表示的數值
        if (amountString.equals("")) {
            amountNum = 0;
        } else {
            amountNum = Long.parseLong(amountString);
        }

        updateButtonStatusWithAmount(amountNum);
        editAmount.setText(FormatUtil.toDecimalFormat(amountNum));
        if(listener != null){
            listener.onInputChanged(amountNum);
        }
    }

    public void updateButtonStatusWithAmount(long amountNum){
        // 根據輸入的數量更新按鈕狀態與輸入框的顯示文字
        setInfoShowAsWarning(amountNum > maxAmountLimit);
        buttonNext.setEnabled(amountNum > 0 && amountNum <= maxAmountLimit);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_next){

            String amountString = (editAmount.getText() != null) ? editAmount.getText().toString() : "";
            // 如果已包含逗號，先去掉逗號
            if(amountString.contains(",")){
                amountString = amountString.replace(",", "");
            }

            if(listener != null){
                listener.onNextClicked(amountString);
            }
        } else if(v.getId() == R.id.layout_info){
            if(listener != null){
                listener.onInfoClicked();
            }
        } else{
            onButtonClick(v);
        }
    }

    /**
     * 顯示帳戶詳情對話框
     * @param useType 使用的類型
     * @param accountBalance -1 表示使用儲值帳戶餘額
     */
    public void showSVDetail(SVAccountDetailFragment.ENUM_TYPE useType, int accountBalance, boolean showGoDeposit){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENT);
        if (prev != null) {
            ft.remove(prev);
        }
        if(accountBalance < 0){
            accountBalance = Integer.parseInt(svAccountInfo.getBalance());
        }
        // Create and show the dialog.
        DialogFragment newFragment = SVAccountDetailFragment.newInstance(useType, accountBalance, svAccountInfo, showGoDeposit);
        newFragment.show(ft, TAG_DIALOG_FRAGMENT);
    }

    public void showSVDetail(SVAccountDetailFragment.ENUM_TYPE useType, int accountBalance){
        showSVDetail(useType, accountBalance, false);
    }

    public void setInfoShowAsWarning(boolean showAsWarning){
        if(showAsWarning) {
            imageInfoArrow.setImageResource(R.drawable.ic_e_home_status_more_red);
            textInfo.setTextColor(getResources().getColor(R.color.sv_amount_input_info_warning));
            imageWarning.setVisibility(View.VISIBLE);
        }else{
            imageInfoArrow.setImageResource(R.drawable.ic_main_input_balance_arrow);
            textInfo.setTextColor(getResources().getColor(R.color.sv_amount_input_info));
            imageWarning.setVisibility(View.GONE);
        }
    }

    public void setInfoText(String msg){
        textInfo.setText(msg);
    }

    public void setListener(AmountInputListener listener) {
        this.listener = listener;
    }


    public interface AmountInputListener{
        void onNextClicked(String inputtedAmount);
        void onInfoClicked();
        void onInputChanged(long inputtedAmount);
    }
}
