package tw.com.taishinbank.ewallet.controller.creditcard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.wallethome.WebViewActivity;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

/**
 * Created by Siang on 3/21/16.
 */
public class MallWebViewActivity extends WebViewActivity {

    private static final String TAG = "MallWebViewActivity";
    //For Order Ticket
    private String orderToken;
    private String orderID;

    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = Uri.parse(super.url);
        orderToken = uri.getQueryParameter("token");

        buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setText("注意！請將網路切回外網，重新整理網頁");
        buttonNext.setVisibility(View.VISIBLE);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });

        if(HttpUtilBase.CreditCard_TicketPayment_Remove_Button) {
            buttonNext.setVisibility(View.GONE);
        }

        webView.setWebViewClient(new CustomWebViewClient());
        //createPayment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearCache(true);
    }

    private void setNextButton() {
        buttonNext.setText("注意！請將網路切回內網，跳至訂單確認頁");
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPaymentConfirmPage();
            }
        });
    }

    private void toPaymentConfirmPage() {
        Intent intent = new Intent(MallWebViewActivity.this, CreditPaymentConfirmActivity.class);
        intent.putExtra(CreditPaymentConfirmActivity.EXTRA_ORDER_TOKEN, orderToken);
        intent.putExtra(CreditPaymentConfirmActivity.EXTRA_ORDER_ID, orderID);
        startActivity(intent);
        finish();
    }


    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            if(url.indexOf("tswallet:\\\\") == 0) {
                Uri uri = Uri.parse(url.replace("tswallet:\\\\", "http://"));
                orderID = uri.getQueryParameter("order_ID");

                if(HttpUtilBase.CreditCard_TicketPayment_Remove_Button) {
                    toPaymentConfirmPage();
                } else {
                    setNextButton();
                }
            }

        }
    }
}
