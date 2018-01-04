package tw.com.taishinbank.ewallet.controller.sv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.wallethome.WebViewActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;


public class SVAccountDetailActivity extends ActivityBase{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_account_detail);
        setCenterTitle(R.string.sv_account_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imagePhoto = (ImageView) findViewById(R.id.image_photo);
        TextView textBalance = (TextView) findViewById(R.id.text_balance);
        TextView textAccountType = (TextView) findViewById(R.id.text_account_type);
        TextView textMaxDepositAmount = (TextView) findViewById(R.id.text_max_deposit_amount);
        TextView textAccount = (TextView) findViewById(R.id.text_account);
        TextView textBankCode = (TextView) findViewById(R.id.text_bank_code);

        TextView textAccountStatus = (TextView) findViewById(R.id.text_account_status);
        TextView textMonthlyAmountLimit = (TextView) findViewById(R.id.text_monthly_amount_limit);
        TextView textDailyAmountLimit = (TextView) findViewById(R.id.text_daily_amount_limit);
        TextView textSingleAmountLimit = (TextView) findViewById(R.id.text_single_amount_limit);

        SVAccountInfo svAccountInfo = PreferenceUtil.getSVAccountInfo(this);

        // 設定餘額
        String formattedAmount = FormatUtil.toDecimalFormatFromString(svAccountInfo.getBalance(), true);
        textBalance.setText(formattedAmount);

        // 設定帳戶類型字串
        int level = Integer.parseInt(svAccountInfo.getAccountLevel());
        String[] levelStrs = getResources().getStringArray(R.array.sv_account_levels);
        String levelStr = (level > 0 && level < levelStrs.length) ? levelStrs[level] : svAccountInfo.getAccountLevel();
        textAccountType.setText(levelStr);
        // 設定最高儲值額度
        formattedAmount = FormatUtil.toDecimalFormatFromString(svAccountInfo.getDepositLimCurr(), true);
        textMaxDepositAmount.setText(formattedAmount);
        // 設定帳戶
        formattedAmount = FormatUtil.toAccountFormat(svAccountInfo.getPrepaidAccount());
        textAccount.setText(formattedAmount);
        // 設定銀行代碼，目前寫死為台新的
        textBankCode.setText(GlobalConst.CODE_TAISHIN_BANK);

        // 設定帳戶狀態、本月、本日、單筆交易限額
        updateStatusWithAccountInfo(svAccountInfo, textAccountStatus);
        // 計算本月、當日剩餘交易額度

        int balance = getBalance(svAccountInfo);
        formattedAmount = FormatUtil.toDecimalFormat(balance, true);
        textMonthlyAmountLimit.setText(formattedAmount);

        formattedAmount = FormatUtil.toDecimalFormat(balance, true);
        textDailyAmountLimit.setText(formattedAmount);

        formattedAmount = FormatUtil.toDecimalFormatFromString(svAccountInfo.getSingleLimCurr(), true);
        textSingleAmountLimit.setText(formattedAmount);

        // 讀取頭像
        String memNO = PreferenceUtil.getMemNO(this);
        ImageLoader imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.photo_size));
        imageLoader.loadImage(memNO, imagePhoto);

        TextView textTransactionRule = (TextView) findViewById(R.id.text_transaction_rule);
        textTransactionRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 2016/03/09:TSB提供網址
                String url = "https://sva.taishinbank.com.tw/tsb/product_intro_1.aspx";
                Intent intent = new Intent(SVAccountDetailActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, url);
                startActivity(intent);
            }
        });
    }

    private void updateStatusWithAccountInfo(SVAccountInfo svAccountInfo, TextView textBalanceStatus){
        //int dailyBalance = Integer.parseInt(svAccountInfo.getDailyLimCurr()) - Integer.parseInt(svAccountInfo.getDailyAmount());
        int balance = getBalance(svAccountInfo);
        int accountBalance = Integer.parseInt(svAccountInfo.getBalance());
        String formattedBalance;
        /* 原為「正常」、「篇低」、「0」。
           調整為顯示以下兩項的金額取小：
            1.每日交易限額-當日累積交易金額（當日可交易餘額）
            2.儲值帳戶餘額 */
        if(balance < accountBalance){
            formattedBalance = FormatUtil.toDecimalFormat(balance, true);
        }else{
            formattedBalance = FormatUtil.toDecimalFormat(accountBalance, true);
        }
        textBalanceStatus.setText(formattedBalance);
        textBalanceStatus.setTextColor(getResources().getColor(R.color.sv_balance_status_normal));
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
    // --------
    //  public
    // --------


    // -------------------
    //  Getter and setter
    // -------------------
}
