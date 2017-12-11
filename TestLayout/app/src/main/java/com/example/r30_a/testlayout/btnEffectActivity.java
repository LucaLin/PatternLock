package com.example.r30_a.testlayout;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class btnEffectActivity extends AppCompatActivity {
    ImageButton imageAdd;
    AnimationSet animSet;
    TranslateAnimation translateAnimation;
    AlphaAnimation alphaAnimation;
    ScaleAnimation scaleAnimation;
    TextView txvadd;
    ProgressBar progressBar;
    ObjectAnimator objectAnimator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btn_effect);
        init();

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        objectAnimator = ObjectAnimator.ofInt(progressBar,"progress",1,500);
        objectAnimator.setDuration(1500);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        /*progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.incrementProgressBy(10);
            }
        });*/



        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animSet = new AnimationSet(true);
                translateAnimation = new TranslateAnimation(0,0,0,-60);
                alphaAnimation = new AlphaAnimation(1,0);
                scaleAnimation = new ScaleAnimation(1,0.6f,1,0.6f,1,0.5f,1,0.5f);
                scaleAnimation.setDuration(200);
                animSet.addAnimation(translateAnimation);
                animSet.addAnimation(alphaAnimation);
                animSet.setDuration(800);
                animSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        txvadd.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                txvadd.setVisibility(View.VISIBLE);
                txvadd.startAnimation(animSet);
                imageAdd.startAnimation(scaleAnimation);


            }
        });


    }
    public void init(){
        imageAdd = (ImageButton)findViewById(R.id.imgadd);
        txvadd = (TextView)findViewById(R.id.txvadd);
        txvadd.setVisibility(View.INVISIBLE);
    }


}


