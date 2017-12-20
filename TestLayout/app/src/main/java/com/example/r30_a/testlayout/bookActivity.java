package com.example.r30_a.testlayout;

import android.app.ActionBar;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class bookActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView startImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隱藏action bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //隱藏status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);




        setContentView(R.layout.activity_book);
        ((View)this.findViewById(android.R.id.content)).setOnClickListener(this);
        initImage();
    }

    @Override
    public void onClick(View v) {
       /* startActivity(new Intent(this,MainActivity.class));
        overridePendingTransition(R.anim.fab_fade_in,R.anim.fab_fade_out);
        this.finish();*/
    }


    private  void initImage(){
        startImage = (ImageView)findViewById(R.id.startView);
        //startImage.setImageResource(R.mipmap.noway);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.6f,1.0f,1.6f,1.0f, Animation.RELATIVE_TO_SELF,0);
        scaleAnimation.setDuration(1000);
        //動畫播放完後保持原來形狀
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //開場動畫結束後進入activity
                startActivity(new Intent(bookActivity.this,EntryActivity.class));
                overridePendingTransition(R.anim.fab_fade_in,R.anim.fab_fade_out);
                music_player.stop(bookActivity.this);
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        startImage.startAnimation(scaleAnimation);

        music_player.playmusic(this,R.raw.welcome);
    }
}
