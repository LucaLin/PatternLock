package tw.com.taishinbank.ewallet.controller.extra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.sv.ResultFragmentBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class ReceiveCouponResultFragment extends ResultFragmentBase {

    private Coupon coupon;
    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ReceiveCouponActivity)getActivity()).setCenterTitle(R.string.sv_result_action_reply);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        coupon = getArguments().getParcelable(ReceiveCouponActivity.EXTRA_COUPON);
        message = getArguments().getString(ReceiveCouponActivity.EXTRA_MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setViewHold(view);

        setViewContent();

        setListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // 根據是否顯示可勾選的列表，設定顯示於右上角的選單項目
        if (menu == null) {
            super.onPrepareOptionsMenu(menu);
        } else {
            MenuItem item = menu.findItem(android.R.id.home);
            if (item != null) {
                item.setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----
    // Private method
    // ----

    protected void setViewHold(View view) {
        super.setViewHold(view);

    }


    protected void setViewContent() {
        txtResultTitle.setText(R.string.reply_message_success);
        txtFromToWho.setText(getResources().getString(R.string.reply_message_to, getActivity()) + " " + coupon.getSenderNickName());

        // Set Date
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        txtDate.setText(FormatUtil.toTimeFormatted(sdf.format(currentTime), false));

        txtDollarSign.setVisibility(View.GONE);
        txtAmount.setVisibility(View.GONE);
        {
            // 設定有底線的TextView Disapper
            lytSendingMsg.setVisibility(View.GONE);
            lytSvResult.setVisibility(View.VISIBLE);

            imgSvResult.setImageResource(R.drawable.ic_e_leave_message_succeed);
        }

        lytCautionArea.setVisibility(View.VISIBLE);
        txtCautionTitle.setVisibility(View.GONE);
        txtCautionContent.setVisibility(View.GONE);

        lytSendingMsg.setVisibility(View.VISIBLE);
        txtSendingMessage.setVisibility(View.VISIBLE);
        txtSendingMessage.setText(message);

        btnAction1.setText(R.string.extra_got_coupon);
        btnAction2.setVisibility(View.GONE);

        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(String.valueOf(coupon.getSenderMemNO()), imgPhoto);
    }

    protected void setListener() {
        //Go to EXTRA_HOME
        btnAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReceiveCouponActivity) getActivity()).gotoMyCoupon();

            }
        });
    }

    // ----
    // Class, Interface, enum
    // ----


}
