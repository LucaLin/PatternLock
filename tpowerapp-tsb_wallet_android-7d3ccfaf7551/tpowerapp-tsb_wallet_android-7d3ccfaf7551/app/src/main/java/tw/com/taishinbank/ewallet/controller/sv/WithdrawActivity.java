package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.model.sv.DepositWithdrawResult;
import tw.com.taishinbank.ewallet.model.sv.DesignateAccount;

public class WithdrawActivity extends ActivityBase {

    private DesignateAccount bankAccount;
    private String inputtedAmount;
    private boolean allowGoingBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.sv_withdraw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set Fragment
        DesignateAccountFragment fragment = DesignateAccountFragment.newInstance(DesignateAccountActivity.FROM_WITHDRAW);

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
        final WithdrawAmountInputFragment fragment = WithdrawAmountInputFragment.newInstance(bankAccount);
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
                    當日可提領餘額：$xx,xxx
                    單筆提領上限：$xx,xxx
                    儲值帳戶餘額：$xx,xxx
                    本次提領可用餘額：$xx,xxx (紅色字) */
                fragment.showSVDetail(SVAccountDetailFragment.ENUM_TYPE.WITHDRAW,
                        -1);
            }

            @Override
            public void onInputChanged(long inputtedAmount) {

            }
        });
        gotoFragment(fragment);
    }

    public void gotoPasswordInput(){
        DepositWithdrawAuthFragment fragment = DepositWithdrawAuthFragment.newInstance(DepositWithdrawAuthFragment.FROM_WITHDRAW);
        gotoFragment(fragment);
    }

    public void gotoResult(DepositWithdrawResult result){
        DepositWithdrawResultFragment fragment = DepositWithdrawResultFragment.newInstance(DepositWithdrawResultFragment.FROM_WITHDRAW, result);
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

    public DesignateAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(DesignateAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getInputtedAmount() {
        return inputtedAmount;
    }

    public void setInputtedAmount(String inputtedAmount) {
        this.inputtedAmount = inputtedAmount;
    }
}
