package tw.com.taishinbank.ewallet.controller.sv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.wallethome.WebViewActivity;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.util.FormatUtil;


public class SVAccountDetailFragment extends DialogFragment {

    public enum ENUM_TYPE {
        DEPOSIT,
        WITHDRAW,
        PAY,
        RED
    }

    private static final String ARG_SV_ACCOUNT = "arg_sv_account";
//    private static final String ARG_COMMON_TITLE = "arg_common_title";
//    private static final String ARG_ACCOUNT_TITLE = "arg_account_title";

    private static final String ARG_USE_TYPE = "arg_use_type";
    private static final String ARG_ACCOUNT_BALANCE = "arg_account_balance";
    private static final String ARG_SHOW_GO_DEPOSIT = "arg_show_go_deposit";

    private SVAccountInfo svAccountInfo;
    private ENUM_TYPE useType;
    private String commonTitle;
    private String accountTitle;
    private int accountBalance;
    private boolean showGoDeposit = false;

    /**
     * 用來建立Fragment
     */
    public static SVAccountDetailFragment newInstance(ENUM_TYPE type, int accountBalance, SVAccountInfo svAccountInfo, boolean showGoDeposit) {
        SVAccountDetailFragment f = new SVAccountDetailFragment();

        Bundle args = new Bundle();
        args.putString(ARG_USE_TYPE, type.toString());
        args.putInt(ARG_ACCOUNT_BALANCE, accountBalance);
        args.putParcelable(ARG_SV_ACCOUNT, svAccountInfo);
        args.putBoolean(ARG_SHOW_GO_DEPOSIT, showGoDeposit);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogWithoutHorizontalPadding);
        useType = ENUM_TYPE.valueOf(getArguments().getString(ARG_USE_TYPE));
        accountBalance = getArguments().getInt(ARG_ACCOUNT_BALANCE);
        svAccountInfo = getArguments().getParcelable(ARG_SV_ACCOUNT);
        showGoDeposit = getArguments().getBoolean(ARG_SHOW_GO_DEPOSIT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sv_account_detail, container, false);

        // Watch for button clicks.
        Button buttonRight = (Button)view.findViewById(R.id.button_right);
        Button buttonLeft = (Button)view.findViewById(R.id.button_left);

        // 判斷是否顯示馬上儲值
        if(showGoDeposit){
            // 左邊馬上儲值
            buttonLeft.setOnClickListener(onGoDepositClickListener);
            buttonLeft.setText(R.string.sv_account_detail_deposit);
            buttonRight.setVisibility(View.VISIBLE);
            // 右邊確認返回
            buttonRight.setOnClickListener(onBackClickListener);
            buttonRight.setText(R.string.sv_account_detail_back);
        }else{
            buttonLeft.setOnClickListener(onBackClickListener);
            buttonLeft.setText(R.string.sv_account_detail_back);
        }

        TextView textAccountType = (TextView) view.findViewById(R.id.text_account_type);
        String[] accountTypes = getResources().getStringArray(R.array.sv_account_levels);
        textAccountType.setText(accountTypes[Integer.parseInt(svAccountInfo.getAccountLevel())]);

        TextView textDailyAmountLimit = (TextView) view.findViewById(R.id.text_daily_amount_limit);
        TextView textSingleAmountLimit = (TextView) view.findViewById(R.id.text_single_amount_limit);
        TextView textBalanceAmountLimit = (TextView) view.findViewById(R.id.text_balance_amount);
        TextView textAmountLimit = (TextView) view.findViewById(R.id.text_amount_limit);

        TextView textDailyAmountLimitTitle = (TextView) view.findViewById(R.id.text_daily_amount_limit_title);
        TextView textSingleAmountLimitTitle = (TextView) view.findViewById(R.id.text_single_amount_limit_title);
        TextView textBalanceAmountLimitTitle = (TextView) view.findViewById(R.id.text_balance_amount_title);
        TextView textAmountLimitTitle = (TextView) view.findViewById(R.id.text_amount_limit_title);

        //int dailyBalance = Integer.parseInt(svAccountInfo.getDailyLimCurr()) - Integer.parseInt(svAccountInfo.getDailyAmount());

        int dailyBalance = 0;
        switch (useType)
        {
            case DEPOSIT:
                commonTitle = getString(R.string.sv_deposit);
                accountTitle = getString(R.string.sv_account_detail_bank_account);
                dailyBalance = getDailyAmountLimitWithAccountInfo(svAccountInfo);
                break;
            case WITHDRAW:
                commonTitle = getString(R.string.sv_withdraw);
                accountTitle = getString(R.string.title_sv_account);
                dailyBalance = getBalance(svAccountInfo);
                break;
            case PAY:
            case RED:
                commonTitle = getString(R.string.sv_pay);
                accountTitle = getString(R.string.title_sv_account);
                dailyBalance = getBalance(svAccountInfo);
                break;
        }

        // 設定本日交易限額
        textDailyAmountLimitTitle.setText(String.format(getString(R.string.sv_account_detail_daily_curr_with_colon), commonTitle));
        String formattedAmount = FormatUtil.toDecimalFormat(dailyBalance, true);
        textDailyAmountLimit.setText(formattedAmount);

        // 設定單筆交易限額(依api個人單筆限額)//Peter@2016/02/24
        textSingleAmountLimitTitle.setText(String.format(getString(R.string.sv_account_detail_single_curr_with_colon), commonTitle));
        formattedAmount = FormatUtil.toDecimalFormat(Integer.parseInt(svAccountInfo.getSingleLimCurr()), true);
        textSingleAmountLimit.setText(formattedAmount);

        // 帳戶餘額
        textBalanceAmountLimitTitle.setText(String.format(getString(R.string.sv_account_detail_balance_with_colon), accountTitle));
        formattedAmount = FormatUtil.toDecimalFormat(accountBalance, true);
        textBalanceAmountLimit.setText(formattedAmount);

        // 設定本次交易限額
        textAmountLimitTitle.setText(String.format(getString(R.string.sv_account_detail_curr_limit_with_colon), commonTitle));
        /* 金額顯示與限制：以下三項取小
            1.每日交易限額-當日累積交易金額（當日可交易餘額）
            2.單筆交易上限(依api個人單筆限額)//Peter@2016/02/24
            3.儲值/活存帳戶餘額 */
        int maxAmountLimit = Math.min(
                Math.min(dailyBalance, Integer.parseInt(svAccountInfo.getSingleLimCurr())),
                accountBalance);
        formattedAmount = FormatUtil.toDecimalFormat(maxAmountLimit, true);
        textAmountLimit.setText(formattedAmount);

        TextView textTransactionRule = (TextView) view.findViewById(R.id.text_transaction_rule);
        textTransactionRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 2016/03/09:TSB提供網址
                String url = "https://sva.taishinbank.com.tw/tsb/product_intro_1.aspx";
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, url);
                startActivity(intent);
            }
        });

        return view;
    }

    private View.OnClickListener onBackClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            dismiss();
        }
    };

    private View.OnClickListener onGoDepositClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            // 開啟儲值列表頁
            Intent intent = new Intent(getActivity(), DepositActivity.class);
            if(useType == ENUM_TYPE.PAY) {
                intent.putExtra(DepositActivity.EXTRA_GO_ORIGINAL_PROCESS_AFTER_SUCCESS, getString(R.string.sv_menu_pay));
            }else if(useType == ENUM_TYPE.RED){
                intent.putExtra(DepositActivity.EXTRA_GO_ORIGINAL_PROCESS_AFTER_SUCCESS, getString(R.string.send_red_envelope));
            }
            startActivity(intent);
            dismiss();
        }
    };

    private int getDailyAmountLimitWithAccountInfo(SVAccountInfo svAccountInfo){
        //int dailyBalance = Integer.parseInt(svAccountInfo.getDailyLimCurr()) - Integer.parseInt(svAccountInfo.getDailyAmount());
        int balance = getBalance(svAccountInfo); // 本月/本日 可交易餘額
        int availableBalance = Integer.parseInt(svAccountInfo.getDepositLimCurr()) - Integer.parseInt(svAccountInfo.getBalance());
        String formattedBalance;
        /* 原為「正常」、「篇低」、「0」。
           調整為顯示以下兩項的金額取小：
            1.每日交易限額-當日累積交易金額（當日可交易餘額）
            2.儲值帳戶餘額 */
        if(balance < availableBalance){
            return balance;
        }else{
            return availableBalance;
        }
    }

    private int getBalance(SVAccountInfo svAccountInfo)
    {
        int monthlyBalance = Integer.parseInt(svAccountInfo.getMonthlyLimCurr()) - Integer.parseInt(svAccountInfo.getMonthlyAmount());
        int dailyBalance = Integer.parseInt(svAccountInfo.getDailyLimCurr()) - Integer.parseInt(svAccountInfo.getDailyAmount());
        if(monthlyBalance > dailyBalance)
            return dailyBalance;
        else
            return monthlyBalance;

    }
}