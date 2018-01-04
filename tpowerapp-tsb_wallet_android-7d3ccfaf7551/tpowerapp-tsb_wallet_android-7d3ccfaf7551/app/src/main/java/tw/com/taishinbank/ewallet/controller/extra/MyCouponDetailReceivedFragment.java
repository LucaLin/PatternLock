package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class MyCouponDetailReceivedFragment extends MyCouponDetailFragment {

    public MyCouponDetailReceivedFragment() {

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
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.extra_my_coupon_confirm_to_give_warn)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onConfirmToGiveClick();
                    }
                })
                .show();
    }

    protected void onButtonTradeClick() {
        ((MyCouponActivity) getActivity()).gotoTrade();
    }

    protected void onConfirmToGiveClick() {
        ((MyCouponActivity) getActivity()).gotoPickFriend();
    }
    
    // ----
    // Http
    // ----


    // ----
    // Private method
    // ----

    @Override
    protected void setViewContent() {
        txtSubtitle.setText(R.string.extra_my_coupon_unused);

        // 對方
        txtLegend1.setText(R.string.extra_my_coupon_friend_message);
        viewHolderSender.textName.setText(coupon.getSenderNickName());
        if (TextUtils.isEmpty(coupon.getSenderDate())) {
            viewHolderSender.textMessage.setText(R.string.extra_my_coupon_no_message);
            viewHolderSender.textTime.setText("");
        } else {
            viewHolderSender.textMessage.setText(coupon.getSenderMessage());
            viewHolderSender.textTime.setText(FormatUtil.toTimeFormatted(coupon.getSenderDate()));
        }
        viewHolderSender.textAmount.setVisibility(View.GONE);
        imageLoader.loadImage(coupon.getSenderMemNO(), viewHolderSender.imagePhoto);

        // 我
        txtLegend2.setText(R.string.extra_my_coupon_your_message);
        String myNickName = (!TextUtils.isEmpty(coupon.getToMemNickName())) ? coupon.getToMemNickName()
                : PreferenceUtil.getNickname(getActivity());
        viewHolderReceiver.textName.setText(myNickName);
        if (TextUtils.isEmpty(coupon.getReplyDate())) {
            viewHolderReceiver.textMessage.setText(R.string.extra_my_coupon_no_message);
            viewHolderReceiver.textTime.setText("");
        } else {
            viewHolderReceiver.textMessage.setText(coupon.getReplyMessage());
            viewHolderReceiver.textTime.setText(FormatUtil.toTimeFormatted(coupon.getReplyDate()));
        }
        viewHolderReceiver.textAmount.setVisibility(View.GONE);
        String myMemNO = (!TextUtils.isEmpty(coupon.getToMemNO())) ? coupon.getToMemNO()
                : PreferenceUtil.getMemNO(getActivity());
        imageLoader.loadImage(myMemNO, viewHolderReceiver.imagePhoto);

        txtCaution.setText(R.string.extra_my_coupon_caution);

        lytActionArea.setVisibility(View.VISIBLE);
        lytCautionArea.setVisibility(View.VISIBLE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        // 取得現在時間
        String today = sdf.format(c.getTime());
        // 如果使用期間已過期
        if(today.compareTo(coupon.getEndDate().substring(0, 8)) > 0) {
            btnTrade.setVisibility(View.GONE);
            btnGive.setEnabled(false);
            btnGive.setText(R.string.extra_my_coupon_button_expired);
        }

        // 設定馬上註冊的連結文字
//        SpannableString spannableString = new SpannableString(getString(R.string.extra_my_coupon_caution));
//        spannableString.setSpan(
//                new URLSpan(getString(R.string.sv_login_register_url)),
//                spannableString.length() - 6, /// 最後邊的六個字 - 請聯絡店家。
//                spannableString.length(),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);     //网络
//        txtCaution.setText(spannableString);
//        txtCaution.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
