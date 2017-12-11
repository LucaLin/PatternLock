package com.example.r30_a.testlayout;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class game_ctivity extends AppCompatActivity implements View.OnClickListener{

    ImageView btnTwo, btnZero, btnFive, btnComputer,Blackman;
    TextView txvResult;
    int ComAns;
    RotateAnimation rotateAnimation;
    music_player music_player1, music_player2;
    Switch btnSwitch;
    int sound;
    SoundPool soundPool = new SoundPool(1,AudioManager.STREAM_MUSIC,0);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ctivity);
        music_player1.playmusic(game_ctivity.this,R.raw.gamble);

        init();

        TranslateAnimation tAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-2f,
                Animation.RELATIVE_TO_SELF,2f,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0);
        tAnim.setDuration(1500);
        tAnim.setRepeatCount(-1);
        tAnim.setRepeatMode(Animation.REVERSE);//移動方式為來回移動

        Blackman.setAnimation(tAnim);



    }

    private void init(){
        Blackman = (ImageView)findViewById(R.id.btnBlackman);
        btnTwo = (ImageView) findViewById(R.id.btnTwo);
        btnTwo.setOnClickListener(this);
        btnZero = (ImageView)findViewById(R.id.btnZero);
        btnZero.setOnClickListener(this);
        btnFive = (ImageView)findViewById(R.id.btnFive);
        btnFive.setOnClickListener(this);
        btnComputer = (ImageView)findViewById(R.id.btnComputer);
        txvResult = (TextView)findViewById(R.id.txvResult);
        btnSwitch = (Switch)findViewById(R.id.btnSwitch);
        btnSwitch.setChecked(true);
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    music_player1.playmusic(game_ctivity.this,R.raw.gamble);
                }else {
                    music_player1.stop(game_ctivity.this);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        //建立按鈕音效
        sound = soundPool.load(this,R.raw.coin04,5);
        //設定監聽器，確保播放前已裝載完成
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sound,1.0f,1.0f,0,0,1);
            }
        });
        rotateAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(500);


        //電腦隨機出拳
        ComAns = (int)(Math.random()*3+1);


        if(ComAns==1){
            btnComputer.setImageResource(R.drawable.two);
            btnComputer.setAnimation(rotateAnimation);
        }else if(ComAns==2) {
            btnComputer.setImageResource(R.drawable.zero);
            btnComputer.setAnimation(rotateAnimation);
        }else {
            btnComputer.setImageResource(R.drawable.five);
            btnComputer.setAnimation(rotateAnimation);
        }


        //玩家出的拳

        switch (v.getId()){
            case R.id.btnTwo:


                btnTwo.setAnimation(rotateAnimation);
                if(ComAns ==1){

                    txvResult.setText("平手");
                }else if(ComAns==2){
                    txvResult.setText("輸了");
                }else {
                    txvResult.setText("you win");
                }


                break;
            case R.id.btnZero:


                btnZero.setAnimation(rotateAnimation);
                if(ComAns ==1){
                    txvResult.setText("you win");
                }else if(ComAns==2){
                    txvResult.setText("平手");
                }else {
                    txvResult.setText("輸了");
                }


                break;
            case R.id.btnFive:
                btnFive.setAnimation(rotateAnimation);
                if(ComAns ==1){
                    txvResult.setText("輸了");
                }else if(ComAns==2){
                    txvResult.setText("you win");
                }else {
                    txvResult.setText("平手");
                }
                break;





        }
    }
}
