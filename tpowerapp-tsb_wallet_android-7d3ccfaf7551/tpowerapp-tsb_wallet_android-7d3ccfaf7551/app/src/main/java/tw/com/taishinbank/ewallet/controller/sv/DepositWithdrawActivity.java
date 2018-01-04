package tw.com.taishinbank.ewallet.controller.sv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;


public class DepositWithdrawActivity extends ActivityBase implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_deposit_withdraw);
        setCenterTitle(R.string.sv_menu_deposit_withdraw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imagePhoto = (ImageView) findViewById(R.id.image_photo);
        TextView textBalance = (TextView) findViewById(R.id.text_balance);
        TextView textAccountType = (TextView) findViewById(R.id.text_account_type);
        TextView textMaxDepositAmount = (TextView) findViewById(R.id.text_max_deposit_amount);
        TextView textAccount = (TextView) findViewById(R.id.text_account);
        TextView textBankCode = (TextView) findViewById(R.id.text_bank_code);

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

        // 讀取頭像
        String memNO = PreferenceUtil.getMemNO(this);
        ImageLoader imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.photo_size));
        imageLoader.loadImage(memNO, imagePhoto);

        Button buttonDeposit = (Button) findViewById(R.id.button_deposit);
        buttonDeposit.setOnClickListener(this);
        Button buttonWithdraw = (Button) findViewById(R.id.button_withdraw);
        buttonWithdraw.setOnClickListener(this);
    }

    // --------
    //  public
    // --------
    @Override
    public void onClick(View v) {
        // 儲值
        if(v.getId() == R.id.button_deposit){
            // 開啟儲值列表頁
            Intent intent = new Intent(this, DepositActivity.class);
            startActivity(intent);

        // 提領
        }else if(v.getId() == R.id.button_withdraw){

            Intent intent = new Intent(DepositWithdrawActivity.this, WithdrawActivity.class);
            startActivity(intent);
        }
    }

    // -------------------
    //  Getter and setter
    // -------------------
}
