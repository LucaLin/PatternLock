package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.model.LocalContact;

/**
 * 由產生條碼導頁過來
 *
 * Created by oster on 2016/1/5.
 */
public class QRCodeScannerActivity extends ActivityBase implements ZXingScannerView.ResultHandler {

    private boolean useFlash = false;
    private boolean useAutofocus = true;
    private int cameraId = -1;

    private ZXingScannerView scannerView;
    private Button btnNext;

    @Override
    @SuppressWarnings("null")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_qrcode_scanner);
        setCenterTitle(R.string.my_qr_code);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set View hold
        btnNext = (Button) findViewById(R.id.btn_next);
        scannerView = (ZXingScannerView) findViewById(R.id.barcode_scanner);

        // Set View Content

        // Set Action
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    // ----
    // private method
    // ----

    private void gotoPayFlow(LocalContact localContact) {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtra(PaymentActivity.EXTRA_RECEIVER, localContact);
        intent.putExtra(MainActivity.EXTRA_GO_PAY, "");

        startActivity(intent);
        finish();
    }

    private QRPerson parseQRPerson(String pQRString) {
        //Parse the String - TSB-MemberNumber:%s-MemberNickName:%s
        String[] qrStrings = pQRString.split("[-:]");
        if (qrStrings.length == 0)
            return null;

        if (qrStrings.length != 5)
            return null;

        if (!"TSB".equals(qrStrings[0]) || !"MemberNumber".equals(qrStrings[1]) ||  !"MemberNickName".equals(qrStrings[3]))
            return null;

        QRPerson qrPerson = new QRPerson(qrStrings[2], qrStrings[4]);
        return qrPerson;
    }

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
        QRPerson qrPerson = parseQRPerson(result.getText());
        if (qrPerson == null) {
            new AlertDialog.Builder(this)
                .setMessage(R.string.qr_code_format_can_not_read)
                .setPositiveButton(R.string.sv_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 繼續掃描
                        scannerView.startCamera();
                    }
                }).show();

        } else {
//            DatabaseHelper databaseHelper = new DatabaseHelper(this);
//            List<LocalContact> selectedFriendList = databaseHelper.searchByMemNo(qrPerson.memberNumber);
//
//            if (selectedFriendList == null || selectedFriendList.size() <= 0) {
//                new AlertDialog.Builder(this)
//                        .setMessage(R.string.qr_code_not_in_frient_list)
//                        .setPositiveButton(R.string.sv_retry, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // 繼續掃描
//                                scannerView.startCamera();
//                            }
//                        }).show();
//                return;
//            }
//
//            if (!selectedFriendList.get(0).isSVAccount()) {
//                new AlertDialog.Builder(this)
//                        .setMessage(R.string.qr_code_not_sv_member)
//                        .setPositiveButton(R.string.sv_retry, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // 繼續掃描
//                                scannerView.startCamera();
//                            }
//                        }).show();
//                return;
//            }

            LocalContact localContact = new LocalContact();
            localContact.setMemNO(qrPerson.memberNumber);
            localContact.setNickname(qrPerson.memberNickName);

            gotoPayFlow(localContact);
        }

        // 繼續掃描
        //scannerView.startCamera();
    }

    // ----
    //  Class, interface,
    // ----
    class QRPerson {
        String memberNumber;
        String memberNickName;

        public QRPerson(String memberNumber, String memberNickName) {
            this.memberNumber = memberNumber;
            this.memberNickName = memberNickName;
        }
    }
}
