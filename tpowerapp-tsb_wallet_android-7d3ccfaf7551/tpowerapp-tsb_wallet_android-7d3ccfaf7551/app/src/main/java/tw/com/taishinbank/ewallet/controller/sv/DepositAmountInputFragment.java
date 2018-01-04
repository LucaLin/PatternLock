package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.model.sv.BankAccount;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class DepositAmountInputFragment extends AmountInputFragmentBase {

    private static final String ARG_ACCOUNT_DATA= "arg_account_data";
    private BankAccount bankAccount;


    /**
     * 用來建立Fragment
     */
    public static DepositAmountInputFragment newInstance(BankAccount bankAccount) {
        DepositAmountInputFragment f = new DepositAmountInputFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ACCOUNT_DATA, bankAccount);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bankAccount = getArguments().getParcelable(ARG_ACCOUNT_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        layoutAccountInfo.setVisibility(View.GONE);
        layoutBankAccountInfo.setVisibility(View.VISIBLE);

        // 顯示轉出帳戶資訊
        textBankAccountTitle.setText(R.string.sv_account_transfer_from);
        if (bankAccount != null) {
            // 設定帳號
            String formattedAccount = FormatUtil.toAccountFormat(bankAccount.getAccount());
            // 設定銀行代碼與名字
            textAccount.setText(formattedAccount);
            textBankTitle.setText(bankAccount.getBankTitle());


            /* 金額顯示與限制：以下三項取小
                1.每日交易限額-當日累積交易金額（當日可交易餘額）
                2.單筆交易上限(依api個人單筆限額)//Peter@2016/02/24
                3.活存帳戶餘額 */
            int dailyBalance = Integer.parseInt(svAccountInfo.getDailyLimCurr()) - Integer.parseInt(svAccountInfo.getDailyAmount());
            maxAmountLimit = Math.min(
                    Math.min(dailyBalance, Integer.parseInt(svAccountInfo.getSingleLimCurr())),
                    (int) bankAccount.getBalance());
        }

        String formattedAmount = FormatUtil.toDecimalFormat(maxAmountLimit);
        textInfo.setText(String.format(getString(R.string.sv_amount_input_info_deposit), formattedAmount));

        inputLengthLimit = String.valueOf(maxAmountLimit).length();

        return view;
    }
}
