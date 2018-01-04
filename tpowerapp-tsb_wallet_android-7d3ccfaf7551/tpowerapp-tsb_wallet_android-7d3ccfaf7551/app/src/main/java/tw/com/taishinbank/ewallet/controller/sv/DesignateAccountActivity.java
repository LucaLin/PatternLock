package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;

/**
 * 共用的約定帳戶列表頁
 */
public class DesignateAccountActivity extends ActivityBase {

    public static final String KEY_BANK_ACCOUNT = "BANK_ACCOUNT";
    public static final String KEY_FROM_PAGE = "FROM_PAGE";

    public static final int FROM_PROFILE = 0;
    public static final int FROM_WITHDRAW = 4;

    private int fromPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);

        //Set initial value
        fromPage = getIntent().getIntExtra(KEY_FROM_PAGE, 0);

        // 根據呼叫的來源頁面設定appbar的標題
        if(fromPage == FROM_PROFILE) {
            setCenterTitle(R.string.designate_bank_account);
        }else if(fromPage == FROM_WITHDRAW){
            setCenterTitle(R.string.sv_withdraw);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set Fragment
        DesignateAccountFragment fragment = DesignateAccountFragment.newInstance(fromPage);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();

    }

    // ----
    // public
    // ----


    // ----
    // Getter and setter
    // ----

}
