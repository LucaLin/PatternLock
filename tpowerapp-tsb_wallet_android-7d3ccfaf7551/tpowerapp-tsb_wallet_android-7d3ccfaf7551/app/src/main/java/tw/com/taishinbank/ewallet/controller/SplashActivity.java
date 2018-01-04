package tw.com.taishinbank.ewallet.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import tw.com.taishinbank.ewallet.R;

public class SplashActivity extends ActivityBase {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 利用handler再過幾次後
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
