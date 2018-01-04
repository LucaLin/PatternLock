package tw.com.taishinbank.ewallet.controller.creditcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URL;
import java.net.URLDecoder;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;

public class CreditCardAuthWebActivity extends ActivityBase implements View.OnClickListener{

    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_RESULT_URL = "extra_result_url";
    public static final String EXTRA_RESULT_CODE = "extra_result_code";
    public static final String EXTRA_RESULT_MSG = "extra_result_msg";
    private String inputURL;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_webview);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.title_credit_card_auth);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        inputURL = getIntent().getStringExtra(EXTRA_URL);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.requestFocus();
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(inputURL);
    }

    @Override
    protected void onResume() {
        super.onResume();
//

    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearCache(true);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

    }

//
//    public void dismissProgressLoading() {
//        ((ActivityBase) this).dismissProgressLoading();
//    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            // do your stuff here
            if(!url.equals(inputURL))
            {
                try {
                    Intent intent = getIntent();
                    intent.putExtra(EXTRA_RESULT_URL, url);
                    intent.putExtra(EXTRA_RESULT_CODE, "");
                    intent.putExtra(EXTRA_RESULT_MSG, "");
                    try {
                        url = URLDecoder.decode(url, "UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(url.indexOf("ret_code") != -1) {
                        URL url_result = new URL(url);
                        boolean bResult = false;
                        String query = url_result.getQuery();
                        String[] arrayQuery = query.split("&");

                        for (String str : arrayQuery) {
                            String[] arrayVelue = str.split("=");
                            if (arrayVelue[0].equals("ret_code")) {
                                intent.putExtra(EXTRA_RESULT_CODE, arrayVelue[1]);
                                intent.putExtra(EXTRA_RESULT_URL, url);
                                if (arrayVelue[1].equals("00"))
                                    bResult = true;
                            } else if (arrayVelue[0].equals("ret_msg")) {
                                if (arrayVelue.length != 2)
                                    intent.putExtra(EXTRA_RESULT_MSG, "");
                                else
                                    intent.putExtra(EXTRA_RESULT_MSG, arrayVelue[1]);
                            }
                        }

                        if (bResult) {
                            setResult(RESULT_OK, intent);
                        } else {
                            setResult(RESULT_CANCELED, intent);
                        }
                        finish();
                    }
                }
                catch (Exception ex)
                {
                    Intent intent = getIntent();
                    intent.putExtra(EXTRA_RESULT_URL, url);
                    intent.putExtra(EXTRA_RESULT_CODE, "");
                    intent.putExtra(EXTRA_RESULT_MSG, "URL Check Crash");
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }
    //
        }
    }

}
