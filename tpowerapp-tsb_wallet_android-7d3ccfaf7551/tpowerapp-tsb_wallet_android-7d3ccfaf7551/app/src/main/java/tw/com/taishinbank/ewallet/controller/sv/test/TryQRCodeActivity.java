package tw.com.taishinbank.ewallet.controller.sv.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.sv.QRCodeGeneratorActivity;

/**
 * 啟動QR Code 產生頁面
 */
public class TryQRCodeActivity extends Activity {

    private Button btnTestQrCodeGenerator;

    public TryQRCodeActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blank);

        btnTestQrCodeGenerator = new Button(this);
        btnTestQrCodeGenerator.setText("My QR Code");
        btnTestQrCodeGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNoAccount();
            }
        });

        LinearLayout lyt = (LinearLayout) findViewById(R.id.lyt_test);
        lyt.addView(btnTestQrCodeGenerator);
    }

    protected void testNoAccount() {
        Intent intent = new Intent();
        intent.setClass(this, QRCodeGeneratorActivity.class);

        startActivity(intent);
    }

}
