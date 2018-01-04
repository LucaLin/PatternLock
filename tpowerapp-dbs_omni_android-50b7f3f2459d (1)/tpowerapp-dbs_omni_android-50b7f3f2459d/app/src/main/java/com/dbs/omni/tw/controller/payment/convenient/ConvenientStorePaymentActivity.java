package com.dbs.omni.tw.controller.payment.convenient;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.http.PaymentHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.payment.ConvenientStoreBarcodeData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.PaymentResponseBodyUtil;

import org.json.JSONException;

public class ConvenientStorePaymentActivity extends ActivityBase {

    private static final String TAG = "ConvenientStorePaymentActivity";

    public static final String EXTRA_MAX_PAID = "EXTRA_MAX_PAID";
    public static final String EXTRA_MIN_PAID = "EXTRA_MIN_PAID";

    private Button button_max_amount , button_min_amount;
    private String[] inputData;
    private String stringBarCode1, stringBarCode2, stringBarCode3, stringBarCode4;
    private double maxPaid, minPaid;
    private int tempMaxAmountThisMonth = 10000; //測試用數值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convenient_store_payment);

        setCenterTitleForCloseBar(R.string.payment_convenient_store_main_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHeadHide(false);

        maxPaid = getIntent().getDoubleExtra(EXTRA_MAX_PAID, 0);
        minPaid = getIntent().getDoubleExtra(EXTRA_MIN_PAID, 0);

        button_max_amount = (Button)findViewById(R.id.button_max_amount);
        button_min_amount = (Button)findViewById(R.id.button_min_amount);

        button_max_amount.setOnClickListener(buttonListener);
        button_min_amount.setOnClickListener(buttonListener);

        callGetConvenientStoreBarcode();
    }

    private Button.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_max_amount:
                    //金額超過兩萬時 , 無法點擊全額繳清 , 會跳alert
                    if(tempMaxAmountThisMonth > 20000){
                        showAlertDialog(getString(R.string.alert_maximum_price_greater_than_20000), android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, true);
                        break;
                    }

                    button_max_amount.setTextColor(ContextCompat.getColor(ConvenientStorePaymentActivity.this,R.color.colorRedPrimary));
                    button_max_amount.setBackgroundResource(R.drawable.bg_bottom_red_line);

                    button_min_amount.setTextColor(ContextCompat.getColor(ConvenientStorePaymentActivity.this,R.color.colorGrayPrimaryDark));
                    button_min_amount.setBackgroundResource(android.R.color.transparent);

                    inputData = getStringBarCodeArray(R.id.button_max_amount);
                    goToPage(R.id.button_max_amount);
                    break;


                case R.id.button_min_amount:
                    button_min_amount.setTextColor(ContextCompat.getColor(ConvenientStorePaymentActivity.this,R.color.colorRedPrimary));
                    button_min_amount.setBackgroundResource(R.drawable.bg_bottom_red_line);

                    button_max_amount.setTextColor(ContextCompat.getColor(ConvenientStorePaymentActivity.this,R.color.colorGrayPrimaryDark));
                    button_max_amount.setBackgroundResource(android.R.color.transparent);

                    inputData = getStringBarCodeArray(R.id.button_min_amount);
                    goToPage(R.id.button_min_amount);
                    break;
            }
        }
    };

    //前往頁面
    private void goToPage(int buttonID) {
        BarcodeFragment barcodeFragment;
        if (buttonID == R.id.button_max_amount) {
            barcodeFragment = BarcodeFragment.newInstance(inputData, maxPaid);
        } else {
            barcodeFragment = BarcodeFragment.newInstance(inputData, minPaid);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, barcodeFragment);

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        ft.commit();
    }

    private void callGetConvenientStoreBarcode(){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(this)) {
            (this).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                PaymentHttpUtil.getConvenientStoreBarcode(responseListener_GetConvenientStoreBarcode, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    private ResponseListener responseListener_GetConvenientStoreBarcode = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                ConvenientStoreBarcodeData getConvenientStoreBarcodeData = PaymentResponseBodyUtil.GetConvenientStoreBarcode(result.getBody());
                stringBarCode1 = getConvenientStoreBarcodeData.getBarCodeNo1();
                stringBarCode2 = getConvenientStoreBarcodeData.getBarCodeNo2();
                stringBarCode3 = getConvenientStoreBarcodeData.getBarCodeNo3();
                stringBarCode4 = getConvenientStoreBarcodeData.getBarCodeNo4();

                //預設進入頁面為全額繳清 , 金額超過兩萬元則選擇繳交最低金額
                if(tempMaxAmountThisMonth > 20000){
                    buttonListener.onClick(button_min_amount);
                }else{
                    buttonListener.onClick(button_max_amount);
                }
            } else {
                // 如果是共同error，不繼續呼叫另一個api
                handleResponseError(result, ConvenientStorePaymentActivity.this);
            }

        }
    };

    private String[] getStringBarCodeArray(int buttonID) {
        String[] string = new String[3];

        string[0] = stringBarCode1;
        string[1] = stringBarCode2;

        if (buttonID == R.id.button_max_amount){
            string[2] = stringBarCode3;
        }else {
            string[2] = stringBarCode4;
        }

        return string;
    }
}
