package tw.com.taishinbank.ewallet.controller.wallethome;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import tw.com.taishinbank.ewallet.R;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "extra_url";

    protected WebView webView;
    protected ProgressBar progressBar;
    protected String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null){
            url = getIntent().getStringExtra(EXTRA_URL);
        }

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        webView = (WebView) findViewById(R.id.webview);
        // 設定自訂的WebViewClient，讓超連結都能在WebView中開啟，當網頁開始與結束讀取時，顯示或隱藏進度條
        webView.setWebViewClient(new CustomWebViewClient());
        // 設定自訂的WebChromeClient，取得讀取進度
        webView.setWebChromeClient(new CustomWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setJavaScriptEnabled(true);
        // 使用內建縮放功能
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        // 讓圖片調整到適合WebView大小
        webView.getSettings().setUseWideViewPort(true);

        if(url != null){
            webView.loadUrl(url);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(webView.canGoBack()){
                webView.goBack();
            }else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearCache(true);
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    private class CustomWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
            //2016/03/15,Peter:TSB PM說webView不要用URL當標題，先直接隱藏。
            setTitle("");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            //2016/03/15,Peter:TSB PM說webView不要用URL當標題，先直接隱藏。
            setTitle("");
            // 遇到網址開頭為ewallet://關閉WebView頁
            if(url != null && url.startsWith("ewallet://")){
                finish();
            }
        }
    }
}
