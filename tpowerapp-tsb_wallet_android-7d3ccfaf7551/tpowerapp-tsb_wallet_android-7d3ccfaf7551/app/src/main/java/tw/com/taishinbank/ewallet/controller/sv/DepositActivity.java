package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.model.sv.BankAccount;
import tw.com.taishinbank.ewallet.model.sv.DepositWithdrawResult;

public class DepositActivity extends ActivityBase {

    public static final String EXTRA_GO_ORIGINAL_PROCESS_AFTER_SUCCESS = "EXTRA_GO_ORIGINAL_PROCESS_AFTER_SUCCESS";

    private BankAccount bankAccount;
    private String inputtedAmount;
    private boolean allowGoingBack = true;
    private String originalProcess = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.sv_deposit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        originalProcess = getIntent().getStringExtra(EXTRA_GO_ORIGINAL_PROCESS_AFTER_SUCCESS);

        //Set Fragment
        AccountListFragment fragment = new AccountListFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();

    }

    // --------
    //  public
    // --------

    public void gotoFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void gotoAmountInput(){
        final DepositAmountInputFragment fragment = DepositAmountInputFragment.newInstance(bankAccount);
        fragment.setListener(new AmountInputFragmentBase.AmountInputListener() {
            @Override
            public void onNextClicked(String inputtedAmount) {
                setInputtedAmount(inputtedAmount);
                gotoPasswordInput();
            }

            @Override
            public void onInfoClicked() {
                /*  點擊箭頭進入帳戶詳情：
                    帳戶類型：第一類、第二類、第三類
                    當日可儲值餘額：$xx,xxx
                    單筆儲值上限：$xx,xxx
                    活存帳戶餘額：$xx,xxx
                    本次儲值可用餘額：$xx,xxx (紅色字) */
                fragment.showSVDetail(SVAccountDetailFragment.ENUM_TYPE.DEPOSIT,
                        (int)bankAccount.getBalance());
            }

            @Override
            public void onInputChanged(long inputtedAmount) {

            }
        });
        gotoFragment(fragment);
    }

    public void gotoPasswordInput(){
        DepositWithdrawAuthFragment fragment = DepositWithdrawAuthFragment.newInstance(DepositWithdrawAuthFragment.FROM_DEPOSIT);
        gotoFragment(fragment);
    }

    public void gotoResult(DepositWithdrawResult result){
        DepositWithdrawResultFragment fragment;
        if(TextUtils.isEmpty(originalProcess)){
            fragment = DepositWithdrawResultFragment.newInstance(DepositWithdrawResultFragment.FROM_DEPOSIT, result);
        }else {
            fragment = DepositWithdrawResultFragment.newInstance(DepositWithdrawResultFragment.FROM_DEPOSIT, result, originalProcess);
        }
        fragment.setListener(new DepositWithdrawResultFragment.OnRetryClickedListener() {
            @Override
            public void onRetryClicked() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                allowGoingBack = true;
                onBackPressed();
            }
        });
        gotoFragment(fragment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        allowGoingBack = false;
    }

    @Override
    public void onBackPressed() {
        if(allowGoingBack) {
            super.onBackPressed();
        }
    }

    // -------------------
    //  Getter and setter
    // -------------------

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getInputtedAmount() {
        return inputtedAmount;
    }

    public void setInputtedAmount(String inputtedAmount) {
        this.inputtedAmount = inputtedAmount;
    }
}
