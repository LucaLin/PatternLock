package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class PaymentAmountInputFragment extends AmountInputFragmentBase {

    private static final String ARG_FRIEND_LIST = "arg_friend_list";
    private static final String ARG_FIXED_INPUT_AMOUNT = "arg_fixed_input_amount";
    private List<LocalContact> friendList;
    private String fixedInputAmount = null;

    /**
     * 用來建立Fragment
     */
    public static PaymentAmountInputFragment newInstance(ArrayList<LocalContact> contactList) {
        PaymentAmountInputFragment f = new PaymentAmountInputFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_FRIEND_LIST, contactList);
        f.setArguments(args);

        return f;
    }

    /**
     * 用來建立Fragment
     */
    public static PaymentAmountInputFragment newInstance(ArrayList<LocalContact> contactList, String fixedInputAmount) {
        PaymentAmountInputFragment f = new PaymentAmountInputFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_FRIEND_LIST, contactList);
        args.putString(ARG_FIXED_INPUT_AMOUNT, fixedInputAmount);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            friendList = getArguments().getParcelableArrayList(ARG_FRIEND_LIST);
            fixedInputAmount = getArguments().getString(ARG_FIXED_INPUT_AMOUNT);
            inputButtonsClickable = (fixedInputAmount == null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // 其他顯示頭像跟人名
        layoutAccountInfo.setVisibility(View.VISIBLE);
        layoutBankAccountInfo.setVisibility(View.GONE);

        if (friendList != null && !friendList.isEmpty()) {
            if (friendList.size() > 1) {
                imagePhoto.setImageResource(R.drawable.img_taishin_photo_dark);
            } else {
                // 設定頭像
                ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
                imageLoader.loadImage(friendList.get(0).getMemNO(), imagePhoto);
            }
            // 設定接收者的顯示名稱
            textNames.setText(ContactUtil.concatNames(friendList));
            textNamesNumber.setText(ContactUtil.getNamesNumberString(friendList));
        }

        // 如果有預先輸入的金額，設定到輸入框
        if(fixedInputAmount != null){
            String formattedAmount = FormatUtil.toDecimalFormatFromString(fixedInputAmount);
            editAmount.setText(formattedAmount);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((ActivityBase) getActivity()).setCenterTitle(R.string.sv_menu_pay);

        // 預防從馬上儲值回來，更新一下
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());

        /* 金額顯示與限制：以下三項取小
            1.每日交易限額-當日累積交易金額（當日可交易餘額）
            2.單筆交易上限(依api個人單筆限額)//Peter@2016/02/24
            3.儲值帳戶餘額 */
        int dailyBalance = Integer.parseInt(svAccountInfo.getDailyLimCurr()) - Integer.parseInt(svAccountInfo.getDailyAmount());
        maxAmountLimit = Math.min(
                Math.min(dailyBalance, Integer.parseInt(svAccountInfo.getSingleLimCurr())),
                Integer.parseInt(svAccountInfo.getBalance()));
        String formattedAmount = FormatUtil.toDecimalFormat(maxAmountLimit);
        textInfo.setText(String.format(getString(R.string.sv_amount_input_info_pay), formattedAmount));

        inputLengthLimit = String.valueOf(maxAmountLimit).length();

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
}
