package tw.com.taishinbank.ewallet.controller.extra;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.model.extra.Coupon;

/**
 *
 */
public class ReceiveCouponActivity extends ActivityBase {

    public static final String EXTRA_COUPON = "EXTRA_COUPON";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String EXTRA_HAS_REPLY = "EXTRA_HAS_REPLY";

    private Coupon coupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);



        coupon = getIntent().getParcelableExtra(EXTRA_COUPON);
        //Set Fragment Arguments
        ReceiveCouponFragment fragment = new ReceiveCouponFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_COUPON, coupon);
        fragment.setArguments(bundle);

        //Start Fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();
    }

    // ----
    // public - Flow control
    // ----
    public void gotoReplyMessage() {
        CouponMessageEnterFragment fragment = CouponMessageEnterFragment.createNewInstanceWithParams(coupon);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack("");
        ft.commit();

        fragment.setButtonsClickListener(new CouponMessageEnterFragment.ButtonsClickListener() {
            @Override
            public void onButton1Click(String inputMessage) {
                gotoSendingResult(inputMessage);
            }

        });
    }

    public void gotoMyCoupon() {
        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
//        MyCouponFragment fragment = new MyCouponFragment();
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.fg_container, fragment);
//        ft.commit();
//        Intent intent = new Intent(this, MyCouponActivity.class);
//        startActivity(intent);

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_GO_COUPON_HISTORY, "");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        finish();

    }

    public void gotoSendingResult(String inputMessage) {
        //Set Fragment
        ReceiveCouponResultFragment fragment = new ReceiveCouponResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ReceiveCouponActivity.EXTRA_COUPON, coupon);
        bundle.putString(ReceiveCouponActivity.EXTRA_MESSAGE, inputMessage);
        fragment.setArguments(bundle);

        //Start Fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();

    }

    // ----
    // Http
    // ----
}
