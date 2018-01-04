package tw.com.taishinbank.ewallet.controller.extra;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class MyCouponDetailSentFragment extends MyCouponDetailFragment {

    public MyCouponDetailSentFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        coupon = getArguments().getParcelable(EXTRA_COUPON);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // ----
    // Action bar
    // ----
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // TODO 待重構作法
        if(menu != null) {
            MenuItem item = menu.findItem(R.id.action_contacts);
            if (item != null) {
                item.setVisible(false);
            }
        } else {

            super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----
    // User interaction
    // ----
    protected void onButtonGiveClick() {

    }

    protected void onButtonTradeClick() {

    }
    
    // ----
    // Http
    // ----

    // ----
    // Private method
    // ----


    @Override
    protected void setViewContent() {
        txtSubtitle.setText(R.string.extra_my_coupon_used);

        // 我
        txtLegend1.setText(R.string.extra_my_coupon_your_message);
        String myNickName = (!TextUtils.isEmpty(coupon.getSenderNickName())) ? coupon.getSenderNickName()
                : PreferenceUtil.getNickname(getActivity());
        viewHolderSender.textName.setText(myNickName);
        if (TextUtils.isEmpty(coupon.getSenderDate())) {
            viewHolderSender.textMessage.setText(R.string.extra_my_coupon_no_message);
            viewHolderSender.textTime.setText("");
        } else {
            viewHolderSender.textMessage.setText(coupon.getSenderMessage());
            viewHolderSender.textTime.setText(FormatUtil.toTimeFormatted(coupon.getSenderDate()));
        }
        viewHolderSender.textAmount.setVisibility(View.GONE);
        String myMemNO = (!TextUtils.isEmpty(coupon.getSenderMemNO())) ? coupon.getSenderMemNO()
                : PreferenceUtil.getMemNO(getActivity());
        imageLoader.loadImage(myMemNO, viewHolderSender.imagePhoto);

        // 對方
        txtLegend2.setText(R.string.extra_my_coupon_friend_message);
        viewHolderReceiver.textName.setText(coupon.getToMemNickName());
        if (TextUtils.isEmpty(coupon.getReplyDate())) {
            viewHolderReceiver.textMessage.setText(R.string.extra_my_coupon_no_message);
            viewHolderReceiver.textTime.setText("");
        } else {
            viewHolderReceiver.textMessage.setText(coupon.getReplyMessage());
            viewHolderReceiver.textTime.setText(FormatUtil.toTimeFormatted(coupon.getReplyDate()));
        }
        viewHolderReceiver.textAmount.setVisibility(View.GONE);

        imageLoader.loadImage(coupon.getToMemNO(), viewHolderReceiver.imagePhoto);
    }
}
