package com.example.r30_a.testlayout;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class flipCardActivity extends AppCompatActivity {

    private AnimatorSet RightOutSet, LeftInSet;
    private FrameLayout frameLayout;
    private ImageView cardFront, cardBack;

    private boolean isShowBack = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_card);
        frameLayout = (FrameLayout)findViewById(R.id.framelayout);
        cardFront = (ImageView)findViewById(R.id.cardfront);
        cardBack = (ImageView)findViewById(R.id.cardback);
        setAnimators();
        setCameraDistance();

    }

    public void setAnimators(){
        //設定動畫物件
        RightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(this,R.animator.anim_out);
        LeftInSet = (AnimatorSet)AnimatorInflater.loadAnimator(this,R.animator.anim_in);

        RightOutSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                //設置layout可供點取
                frameLayout.setClickable(false);

            }
        });
        LeftInSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //設置layout可供點取
                frameLayout.setClickable(true);
            }
        });


    }

    public void setCameraDistance(){
        //設定旋轉時的view不要超出螢幕
        int distance = 16000;
        float scale = getResources().getDisplayMetrics().density * distance;
        cardFront.setCameraDistance(scale);
        cardBack.setCameraDistance(scale);

    }
    public void flipcard(View view){

        if(!isShowBack){//如果卡片在正面
            RightOutSet.setTarget(cardFront);
            LeftInSet.setTarget(cardBack);
            RightOutSet.start();//正面先翻出
            LeftInSet.start();//背面翻進
            isShowBack = true;
        }else {//如果卡片在反面
            RightOutSet.setTarget(cardBack);
            LeftInSet.setTarget(cardFront);
            RightOutSet.start();//背面翻出
            LeftInSet.start();//正面翻進來
            isShowBack = false;
        }
    }
}
