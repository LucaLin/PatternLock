package com.example.r30_a.testlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class waveView extends AppCompatActivity {

    private ImageView image1;
    private waveviewController waveview;
    private TextView txvProgress;
    int progressNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_view);
        txvProgress = (TextView)findViewById(R.id.txvProgress);

        image1 = (ImageView)findViewById(R.id.image1);
        waveview = (waveviewController)findViewById(R.id.waveView);

        //設定image要隨著水波移動，使用y來控制
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2,-2);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        waveview.setOnWaveAnimationListener(new waveviewController.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                layoutParams.setMargins(0,0,0,(int)y+1);
                image1.setLayoutParams(layoutParams);
            }
        });





    }
}
