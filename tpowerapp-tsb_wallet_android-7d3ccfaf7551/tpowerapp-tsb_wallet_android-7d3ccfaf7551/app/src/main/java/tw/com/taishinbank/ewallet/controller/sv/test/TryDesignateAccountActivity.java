package tw.com.taishinbank.ewallet.controller.sv.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.extra.ReceiveCouponActivity;
import tw.com.taishinbank.ewallet.controller.sv.DesignateAccountActivity;
import tw.com.taishinbank.ewallet.controller.sv.ReceivePaymentActivity;
import tw.com.taishinbank.ewallet.controller.sv.ReceiveRequestActivity;
import tw.com.taishinbank.ewallet.interfaces.CouponType;
import tw.com.taishinbank.ewallet.interfaces.TransactionStatus;
import tw.com.taishinbank.ewallet.interfaces.TransactionType;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionIn;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionOut;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

/**
 * 用來啟動獨立 activity flow - 新增、修改約定帳戶，收到付款請求、收到付款
 *
 * Created by oster on 2016/1/5.
 */
public class TryDesignateAccountActivity extends Activity {

    private static final String TAG = "TryDesignateAccountActivity";

    private Button btnTestNoAccount;
    private Button btnTestHasAccount;

    private Button btnTestReceiveRequest;
    private Button btnTestReceivePayment;

    private Button btnTestDownloadRawData;
    private Button btnTestReceiveCoupon;

    public TryDesignateAccountActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blank);

        btnTestNoAccount = new Button(this);
        btnTestNoAccount.setText("No Account");
        btnTestNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNoAccount();
            }
        });

        btnTestHasAccount = new Button(this);
        btnTestHasAccount.setText("Has Account");
        btnTestHasAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testHasAccount();
            }
        });

        btnTestReceiveRequest = new Button(this);
        btnTestReceiveRequest.setText("Receive Request.");
        btnTestReceiveRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testReceiveRequest();
            }
        });

        btnTestReceivePayment = new Button(this);
        btnTestReceivePayment.setText("Receive Payment");
        btnTestReceivePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testReceivePayment();
            }
        });

        btnTestDownloadRawData = new Button(this);
        btnTestDownloadRawData.setText("Download Raw Data");
        btnTestDownloadRawData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GeneralHttpUtil.downloadCoupon("edm_01.jpg", null, TryDesignateAccountActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnTestReceiveCoupon = new Button(this);
        btnTestReceiveCoupon.setText("Receive coupon");
        btnTestReceiveCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testReceiveCoupon();
            }
        });

        LinearLayout lyt = (LinearLayout) findViewById(R.id.lyt_test);
        lyt.addView(btnTestNoAccount);
        lyt.addView(btnTestHasAccount);
        lyt.addView(btnTestReceiveRequest);
        lyt.addView(btnTestReceivePayment);
        lyt.addView(btnTestDownloadRawData);
        lyt.addView(btnTestReceiveCoupon);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
    }

    protected void testNoAccount() {
        Intent intent = new Intent();
        intent.setClass(this, DesignateAccountActivity.class);

        startActivity(intent);
//        finish();
    }


    private void testHasAccount() {
//        BankAccount ba = new BankAccount("2341-100-414-9321", "台新銀行", true);

//        Intent intent = new Intent();
//        intent.setClass(this, DesignateAccountActivity.class);
//        intent.putExtra(DesignateAccountActivity.KEY_BANK_ACCOUNT, ba);
//        startActivity(intent);
//        finish();
    }

    private void testReceiveRequest() {
        Intent intent = new Intent();

        SVTransactionOut out = new SVTransactionOut();
        out.setReplyMessage("This is Oster test for you...");
        out.setName("Oster.Test");
        out.setSenderMessage("Oster send the payment request to you. Do you know that?");
        out.setAmount("49000");
        out.setTxToMemNO(99);
        out.setCreateDate("20160113175101");
        out.setTxfSeq(177);
        out.setTxfdSeq(277);
        out.setTxStatus(TransactionStatus.AWAITING.getCode());
        out.setTxType(TransactionType.TRANSFER_TO.code);

        intent.putExtra(ReceiveRequestActivity.EXTRA_SV_TRX_OUT, out);
        intent.setClass(this, ReceiveRequestActivity.class);
        startActivity(intent);
    }

    private void testReceivePayment() {
        Intent intent = new Intent();

        SVTransactionIn in = new SVTransactionIn();
        in.setReplyMessage("This is Oster test for you...");
        in.setTxMemName("Oster.Test");
        in.setTxMemNO(12);
        in.setSenderMessage("Oster send the payment request to you. Do you know that?");
        in.setAmount("49000");
        in.setCreateDate("20160113175101");
        in.setTxfSeq(177);
        in.setTxfdSeq(277);
        in.setTxStatus(TransactionStatus.AWAITING.getCode());
        in.setTxType(TransactionType.TRANSFER_TO.code);

        intent.putExtra(ReceivePaymentActivity.EXTRA_SV_TRX_IN, in);
        intent.setClass(this, ReceivePaymentActivity.class);
        startActivity(intent);
    }

    private void testReceiveCoupon() {
        // Init Coupon
        Coupon coupon = new Coupon();
        coupon.setStatus(CouponType.RECEIVED.code);

        coupon.setContent("優惠券優惠內容。一二三四五六七八。");
        coupon.setTitle("玉米濃湯");
        coupon.setSubTitle("好幾碗");

        coupon.setReplyMessage(null);
        coupon.setToMemNickName(null);
        coupon.setReplyDate(null);

        coupon.setSenderMessage("Sender Message from Oster");
        coupon.setSenderNickName("Handsome Oster");
        coupon.setSenderDate("20160111214322");


        coupon.setStoreName("濃濃美-IMF");
        coupon.setStoreAddress("美食街499號100F - ");
        coupon.setStorePhone("(02)2263004");
        coupon.setCreateDate("2016020140315");
        coupon.setStartDate("20160311");
        coupon.setEndDate("20160403");
        coupon.setSerialNO("UNUSED-ABZN123456789");

        //
        Intent intent = new Intent();
        intent.setClass(this, ReceiveCouponActivity.class);
        intent.putExtra(ReceiveCouponActivity.EXTRA_COUPON, coupon);


        startActivity(intent);
    }
}
