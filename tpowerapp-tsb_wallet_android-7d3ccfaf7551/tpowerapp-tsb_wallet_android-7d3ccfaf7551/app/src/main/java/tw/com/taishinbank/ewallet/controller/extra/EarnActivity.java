package tw.com.taishinbank.ewallet.controller.extra;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.model.extra.CouponEnter;

/**
 * 共用的約定帳戶列表頁
 */
public class EarnActivity extends ActivityBase {

    public static final String EXTRA_GO_PAGE = "EXTRA_GO_PAGE";
    public static final int PAGE_EARN_BY_ENTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.extra_menu_earn_promotion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set Fragment
        Fragment fragment;

        if(getIntent() != null && getIntent().getIntExtra(EXTRA_GO_PAGE, 0) == PAGE_EARN_BY_ENTER){
            fragment = new EarnByEnterFragment();
        }else{
            fragment = new EarnHomeFragment();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();

    }

    // ----
    // public
    // ----
    public void gotoShareToEarn() {
        Fragment fragment = new EarnByShareFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack("");
        ft.commit();
    }

    public void gotoEnterToEarn() {
        Fragment fragment = new EarnByEnterFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack("");
        ft.commit();
    }

    public void takeCoupon(CouponEnter couponEnter) {
        Fragment fragment = EarnResultByEnterFragment.newInstance(
                couponEnter, getResources().getString(R.string.extra_my_coupon_go));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack("");
        ft.commit();
    }

    // ----
    // Getter and setter
    // ----

}
