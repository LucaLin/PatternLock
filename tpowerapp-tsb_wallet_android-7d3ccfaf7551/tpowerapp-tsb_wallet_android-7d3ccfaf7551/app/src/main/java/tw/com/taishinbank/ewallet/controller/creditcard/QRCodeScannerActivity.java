package tw.com.taishinbank.ewallet.controller.creditcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.Result;

import org.json.JSONException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.CreditCardHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.CreditCardResponseBodyUtil;

/**
 * 由產生條碼導頁過來
 *
 * Created by oster on 2016/1/5.
 */
public class QRCodeScannerActivity extends ActivityBase implements ZXingScannerView.ResultHandler {
    private static final String TAG = "QRCodeScannerActivity";

    private boolean useFlash = false;
    private boolean useAutofocus = true;
    private int cameraId = -1;

    private ZXingScannerView scannerView;
    private Button btnNext;

    private String urlString;

    @Override
    @SuppressWarnings("null")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_qrcode_scanner);
        setCenterTitle(R.string.card_payment_result_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set View hold
        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setText(getString(R.string.credit_card_show_pay_code));
        TextView txtAimToScan = (TextView) findViewById(R.id.txt_aim_to_scan);
        txtAimToScan.setText(getString(R.string.credit_card_aim_to_scan));
        scannerView = (ZXingScannerView) findViewById(R.id.barcode_scanner);

        // Set View Content

        // Set Action
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CreditCardUtil.GetCreditCardList(QRCodeScannerActivity.this).size() == 0) {
                    showAlertDialog(getString(R.string.nocard_alert));
                } else {
                    Intent intent = new Intent(QRCodeScannerActivity.this, CreditCardPaymentLoginActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.startCamera(cameraId);
        scannerView.setFlash(useFlash);
        scannerView.setAutoFocus(useAutofocus);

        scannerView.setResultHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    // ----
    // private method
    // ----

    // ----
    // Listener
    // ----

    /**
     * Callback by the Barcode scanner
     * @param result Barcode detected.
     */
    @Override
    public void handleResult(Result result) {
//        Toast.makeText(this, result.getText(), Toast.LENGTH_LONG).show();

        // 在此處理掃描結果
        if ( !URLUtil.isValidUrl(result.getText())) {
            new AlertDialog.Builder(this)
                .setMessage(R.string.qr_code_format_can_not_read)
                .setPositiveButton(R.string.sv_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 繼續掃描
                        scannerView.startCamera(cameraId);
                    }
                }).show();

        } else {
            urlString = result.getText();
//            Intent intent = new Intent(this, WebViewActivity.class);
//            intent.putExtra(WebViewActivity.EXTRA_URL, urlString);
//            intent.putExtra(WebViewActivity.EXTRA_CREDIT_PAY_MODE, true);
//            startActivity(intent);

//            // 如果沒有網路連線，顯示提示對話框
            if (!NetworkUtil.isConnected(this)) {
                this.showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scannerView.startCamera(cameraId);
                        dialog.dismiss();
                    }
                }, true);
            } else {
                try {
                    this.showProgressLoading();
                    CreditCardHttpUtil.GetTicketOrderToken(responseListener, this, TAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        // 繼續掃描
        //scannerView.startCamera();
    }

    // ----
    //  Class, interface,
    // ----

    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {

                String orderToken = CreditCardResponseBodyUtil.getTicketOrderToken(result.getBody());
                gotoWebView(orderToken);

            } else {
                // 如果是Token失效，中文是[不合法連線，請重新登入]
                if(!handleCommonError(result, QRCodeScannerActivity.this)){
                    showAlertDialog(result.getReturnMessage());
                }

                scannerView.startCamera(cameraId);
            }
        }
    };

    private void gotoWebView(String orderToken) {
        urlString += "?token=" + orderToken + "&redirect=tswallet:\\\\";
        Intent intent = new Intent(this, MallWebViewActivity.class);
        intent.putExtra(MallWebViewActivity.EXTRA_URL, urlString);
        startActivity(intent);
        finish();
    }

}
