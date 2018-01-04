package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.Display;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.FriendListFragment;
import tw.com.taishinbank.ewallet.controller.sv.MessageEnterFragment;
import tw.com.taishinbank.ewallet.interfaces.CouponType;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.Selectable;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.model.extra.CouponSend;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

/**
 * 共用的約定帳戶列表頁
 */
public class MyCouponActivity extends ActivityBase {

    public enum ENUM_IMAGE_SIZE
    {
        LARGE,
        MEDIUM,
        SMALL,
    }

    //從好友詳情過來的
    public static final String EXTRA_FRIEND = "EXTRA_FRIEND";

    private final String EXTRA_COUPON = "EXTRA_COUPON";
    private final String EXTRA_CONTACT = "EXTRA_CONTACT";
    private final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private final String GO_LIST = "GO_LIST";

    private Coupon coupon;
    private ArrayList<LocalContact> localContacts;
    private String message;

    //從好友詳情過來的
    private LocalContact friendContact;
    public boolean IsFromFriendDetail = false;

    public ENUM_IMAGE_SIZE imageSize = ENUM_IMAGE_SIZE.MEDIUM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.extra_my_coupon);

        if(getIntent().hasExtra(EXTRA_FRIEND))
        {
            IsFromFriendDetail = true;
            friendContact = getIntent().getParcelableExtra(EXTRA_FRIEND);
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        if(width < 640)
            imageSize = ENUM_IMAGE_SIZE.SMALL;
        else if(width > 720)
            imageSize = ENUM_IMAGE_SIZE.LARGE;

        //Set Fragment
        MyCouponFragment fragment = new MyCouponFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_COUPON, coupon);
        outState.putParcelableArrayList(EXTRA_CONTACT, localContacts);
        outState.putString(EXTRA_MESSAGE, message);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        coupon = savedInstanceState.getParcelable(EXTRA_COUPON);
        localContacts = savedInstanceState.getParcelableArrayList(EXTRA_CONTACT);
        message = savedInstanceState.getString(EXTRA_MESSAGE);
    }

    // ----
    // public
    // ----

    /**
     * 由優惠券列表，點選單一項目觸發
     * @param selectedCoupon
     */
    public void gotoDetail(Coupon selectedCoupon) {
        coupon = selectedCoupon;

        MyCouponDetailFragment detailFragment = null;
        if (CouponType.ACT.code.equals(selectedCoupon.getStatus())) {
            detailFragment = new MyCouponDetailActFragment();

        } else if (CouponType.RECEIVED.code.equals(selectedCoupon.getStatus())) {
            detailFragment = new MyCouponDetailReceivedFragment();

        } else if (CouponType.SENT.code.equals(selectedCoupon.getStatus())) {
            detailFragment = new MyCouponDetailSentFragment();

        } else if (CouponType.TRADED.code.equals(selectedCoupon.getStatus())) {
            detailFragment = new MyCouponDetailTradedFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(MyCouponDetailFragment.EXTRA_COUPON, selectedCoupon);
        detailFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, detailFragment);
        ft.addToBackStack("GO_LIST");
        ft.commit();
    }

    /**
     * 由優惠券細項，點擊轉送觸發
     */
    public void gotoPickFriend() {
        final int MAX_SELECTION_NUM = 1;

        if(getIntent().hasExtra(EXTRA_FRIEND)) {
            localContacts = new ArrayList<>();
            localContacts.add(friendContact);
            gotoMessageEnter();
            return;
        }


        //Set Fragment
        final FriendListFragment fragment = FriendListFragment.newSelectableInstance(PreferenceUtil.ENUM_USE_FRIEND.COUPON, false);
        fragment.setFriendListListener(new FriendListFragment.FriendListListener() {
            @Override
            public void onNext(ArrayList<LocalContact> list) {
                localContacts = list;
                gotoMessageEnter();
            }

            @Override
            public boolean shouldContinueUpdateList(ArrayList<Selectable<LocalContact>> selectedContacts) {
                if (selectedContacts.size() > MAX_SELECTION_NUM) {
                    fragment.resetLastSelection();
                    String msg = String.format(getString(R.string.friend_selection_exceed_limit), getString(R.string.extra_my_coupon_button_give_coupon), MAX_SELECTION_NUM);
                    showAlertDialog(msg);
                    return false;
                }
                return true;
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 由好友列表下一步觸發
     */
    public void gotoMessageEnter() {
        MessageEnterFragment.Parameters params = new MessageEnterFragment.Parameters(localContacts);
        params.setInputHint(getString(R.string.extra_my_coupon_give_message_hint));
        params.setAppbarTitle(getString(R.string.extra_give_message));
        MessageEnterFragment fragment = MessageEnterFragment.createNewInstanceWithParams(params);
        fragment.setButtonsClickListener(new MessageEnterFragment.ButtonsClickListener() {
            @Override
            public void onButton1Click(String inputMessage) {
                message = inputMessage;

                gotoDetailConfirm();

            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 送出前，確認送出明細
     * 由發送訊息觸發
     */
    public void gotoDetailConfirm() {
        MyCouponSendConfirmFragment fragment = MyCouponSendConfirmFragment.newInstance(message, coupon, localContacts);
        fragment.setListener(new MyCouponSendConfirmFragment.DetailConfirmListener() {

            @Override
            public void onNext(ArrayList<CouponSend> couponSendList) {
                gotoSendResult(couponSendList);
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 當 Server 回傳成功時，導到結果頁，
     * <b>結果頁應將之前的fragment transaction 清除</b>
     * @param couponSendList
     */
    public void gotoSendResult(ArrayList<CouponSend> couponSendList) {
        // Clear all previous pages
        getSupportFragmentManager().popBackStack(GO_LIST, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        MyCouponSendResultFragment fragment = MyCouponSendResultFragment.newInstance(couponSendList.get(0));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, fragment);
        ft.commit();
    }

    /**
     * 返回優惠券 - 未使用
     */
    public void gotoMyCoupon() {
        // Clear all previous pages
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        MyCouponFragment fragment = new MyCouponFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);

        ft.commit();
    }

    public void gotoTrade() {

        new AlertDialog.Builder(this)
                .setMessage(R.string.extra_my_coupon_confirm_to_trade_warn)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyCouponTradeFragment fragment = MyCouponTradeFragment.newInstance(coupon);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fg_container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                })
                .show();
    }

    /**
     * 返回優惠券 - 已使用
     */
    public void gotoMyUsedCoupon() {
        // Clear all previous pages
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        MyCouponFragment fragment = new MyCouponFragment();
        Bundle args = new Bundle();
        args.putInt(MyCouponFragment.EXTRA_SWITCH_TO, MyCouponFragment.COUPON_USED);
        fragment.setArguments(args);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();
    }

    /**
     * 當 Server 回傳成功時，導到結果頁，
     * <b>結果頁應將之前的fragment transaction 清除</b>
     * @param coupon
     */
    public void gotoTradeResult(Coupon coupon) {
        // Clear all previous pages
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        MyCouponTradeResultFragment fragment = MyCouponTradeResultFragment.newInstance(coupon);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, fragment);
        ft.commit();
    }



    // ----
    // Getter and setter
    // ----
    public void updateCoupon(Coupon selectCoupon)
    {
        this.coupon = selectCoupon;
    }

    public LocalContact getFriendContact()
    {
        return friendContact;
    }
    // ----
    //
    // ----
}
