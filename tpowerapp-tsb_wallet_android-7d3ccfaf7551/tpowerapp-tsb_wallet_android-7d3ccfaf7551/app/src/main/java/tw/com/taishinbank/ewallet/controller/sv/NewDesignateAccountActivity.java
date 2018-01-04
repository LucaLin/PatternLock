package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;
import tw.com.taishinbank.ewallet.model.sv.BankAccount;

/**
 *
 */
public class NewDesignateAccountActivity extends ActivityBase implements OnButtonNextClickedListener {

    private BankAccount selectedBankAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.designate_bank_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set Fragment
        NewDesignateAccountFragment fragment = new NewDesignateAccountFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();

        //
    }

    // ----
    // public
    // ----
    //Flow step 1
    public void gotoAuth(BankAccount selectedBankAccount) {
        this.selectedBankAccount = selectedBankAccount;

        NewDesignateAccountAuthFragment fragment = new NewDesignateAccountAuthFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, fragment);
        ft.commit();
    }

    //Flow step 2
    public void endProcess() {
        finish();
    }

    // ----
    // Getter and setter
    // ----

    @Override
    public void onButtonNextClicked(int nextPage) {

    }

    public BankAccount getSelectedBankAccount() {
        return selectedBankAccount;
    }
}
