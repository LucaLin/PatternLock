package tw.com.taishinbank.ewallet.controller.extra;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.CouponType;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.view.TextViewWithUnderLine;

public class ReceiveCouponFragment extends Fragment {

    // -- View Hold --

    protected TextView        txtResultTitle           ;
    protected TextView        txtDate                  ;
    protected ImageView       imgPhoto                 ;
    protected View            lytNameArea              ;
    protected TextView        txtFrom                  ;
    protected TextView        txtTitle1                ;
    protected TextView        txtTitle2                ;

    protected TextView        txtSendingMessage        ;
    protected TextViewWithUnderLine txtSendingMessageLine;
    protected View            lytSendingMsg            ;

    protected Button          btnReply                ;


    protected Button          btnCoupon               ;

    // -- Data Model --
    private Coupon coupon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ReceiveCouponActivity)getActivity()).setCenterTitle(R.string.extra_event);
        ((ReceiveCouponActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        coupon = getArguments().getParcelable(ReceiveCouponActivity.EXTRA_COUPON);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extra_coupon_receive, container, false);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----
    // Private method
    // ----

    protected void setViewHold(View view) {
        txtResultTitle        = (TextView    ) view.findViewById(R.id.txt_result_title          );
        txtDate               = (TextView    ) view.findViewById(R.id.txt_date                  );
        imgPhoto              = (ImageView   ) view.findViewById(R.id.img_photo                 );
        lytNameArea           =                view.findViewById(R.id.lyt_name_area             );
        txtFrom               = (TextView    ) view.findViewById(R.id.txt_from            );
        txtTitle1             = (TextView    ) view.findViewById(R.id.txt_title1     );
        txtTitle2             = (TextView    ) view.findViewById(R.id.txt_title2           );

        lytSendingMsg         =                view.findViewById(R.id.lyt_sending_msg           );
        txtSendingMessageLine = (TextViewWithUnderLine) view.findViewById(R.id.txt_sending_msg_line);
        txtSendingMessage     = (TextView    ) view.findViewById(R.id.txt_sending_msg);

        btnReply              = (Button      ) view.findViewById(R.id.btn_reply              );
        btnCoupon             = (Button      ) view.findViewById(R.id.btn_go_coupon              );

    }


    protected void setViewContent() {
        // 顯示最後更新時間
        txtDate.setText(FormatUtil.toTimeFormatted(coupon.getLastUpdate(), false));

        txtTitle1.setText(coupon.getTitle());
        txtTitle2.setText(coupon.getSubTitle());

        {
            // 設定有底線的TextView行數、底線顏色、底線寬度

            Resources resources = getResources();
            txtSendingMessageLine.setNumberOfLines(resources.getInteger(R.integer.red_envelope_sent_result_blessing_lines));
            txtSendingMessageLine.setLineColor(resources.getColor(R.color.red_envelope_divider));
            txtSendingMessageLine.setLineWidth(resources.getDimensionPixelSize(R.dimen.red_envelope_sent_result_divider_height));

            txtSendingMessage.setText(coupon.getSenderMessage());
        }

        // 如果是收到且未回覆訊息，才顯示回覆留言按鈕
        if(coupon.getStatus().equals(CouponType.RECEIVED.code) && TextUtils.isEmpty(coupon.getReplyMessage())){
            btnReply.setVisibility(View.VISIBLE);
        }else{
            btnReply.setVisibility(View.GONE);
        }

        // 為系統發送的優惠活動
        if(coupon.getStatus().equals(CouponType.ACT.code)) {
            // 顯示台新logo，來自LetPay電子錢包
            imgPhoto.setImageResource(R.drawable.img_taishin_photo_dark_shadow);
            txtFrom.setText(R.string.extra_got_coupon_from_ewallet);
            // 設定按鈕文字
            btnCoupon.setText(getString(R.string.extra_got_coupon));
            // 不顯示留言
            lytSendingMsg.setVisibility(View.INVISIBLE);

        // 好友轉送
        }else if(coupon.getStatus().equals(CouponType.RECEIVED.code)) {
            // 顯示發送方頭像、來自對方暱稱
            txtFrom.setText(coupon.getSenderNickName());
            ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
            imageLoader.loadImage(String.valueOf(coupon.getSenderMemNO()), imgPhoto);
            // 設定按鈕文字
            btnCoupon.setText(getString(R.string.extra_got_coupon));
            // 顯示發送方留言
            lytSendingMsg.setVisibility(View.VISIBLE);

        // 如果是已轉送與已兌換，不顯示領取優惠券按鈕
        } else {
            btnCoupon.setVisibility(View.GONE);
            lytNameArea.setVisibility(View.INVISIBLE);
            imgPhoto.setVisibility(View.INVISIBLE);

            // 已轉送，將title設為優惠券已XX，不顯示留言
            if(coupon.getStatus().equals(CouponType.SENT.code)) {
                txtResultTitle.setText(getString(R.string.extra_my_coupon).concat(CouponType.SENT.description));
                lytSendingMsg.setVisibility(View.INVISIBLE);

            // 已兌換
            }else if(coupon.getStatus().equals(CouponType.TRADED.code)){
                txtResultTitle.setText(getString(R.string.extra_my_coupon).concat(CouponType.TRADED.description));
                // 顯示發送方留言
                lytSendingMsg.setVisibility(View.VISIBLE);
            }
        }

    }

    protected void setListener() {
        //Go to home
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReceiveCouponActivity) getActivity()).gotoReplyMessage();
            }
        });
        //Go to history
        btnCoupon.setOnClickListener(new View.OnClickListener() {
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
