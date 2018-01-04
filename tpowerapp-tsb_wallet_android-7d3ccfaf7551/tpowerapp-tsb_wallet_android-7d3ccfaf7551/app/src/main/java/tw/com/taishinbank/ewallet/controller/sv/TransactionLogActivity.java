package tw.com.taishinbank.ewallet.controller.sv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;

/**
 * 共用的約定帳戶列表頁
 */
public class TransactionLogActivity extends ActivityBase implements OnButtonNextClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.sv_account_history);

        //Set Fragment
        TransactionLogFragment fragment = new TransactionLogFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();

    }

    // ----
    // public
    // ----
    public void gotoDetail() {
        Intent intent = new Intent();
        intent.setClass(this, NewDesignateAccountActivity.class);
        startActivity(intent);
    }

    // ----
    // Getter and setter
    // ----

    @Override
    public void onButtonNextClicked(int nextPage) {

    }
}
