package com.example.r30_a.testlayout;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

/*dependencies {

        compile 'com.nineoldandroids:library:2.4.0'
        compile 'com.github.sd6352051.niftydialogeffects:niftydialogeffects:1.0.0@aar'
        }*/



public class alert_dialogActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnNewspaper, btnRotateBottom, btnShake, btnFadeIn
            , btnSlideTop, btnSlideBottom, btnSlideRight, btnSlideLeft
            , btnFlipH, btnFlipV, btnSlit, btnOK;
    Effectstype effectstype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);

        findview();

    }


    public void findview(){
        btnNewspaper = (Button)findViewById(R.id.btnNewspaper);btnNewspaper.setOnClickListener(this);
        btnRotateBottom = (Button)findViewById(R.id.btnRotateButton);btnRotateBottom.setOnClickListener(this);
        btnShake = (Button)findViewById(R.id.btnShake);btnShake.setOnClickListener(this);
        btnFadeIn = (Button)findViewById(R.id.btnFadeIn);btnFadeIn.setOnClickListener(this);
        btnSlideTop = (Button)findViewById(R.id.btnSlideTop);btnSlideTop.setOnClickListener(this);
        btnSlideBottom = (Button)findViewById(R.id.btnSlideBottom);btnSlideBottom.setOnClickListener(this);
        btnSlideRight = (Button)findViewById(R.id.btnSlideRight);btnSlideRight.setOnClickListener(this);
        btnSlideLeft = (Button)findViewById(R.id.btnSlideLeft);btnSlideLeft.setOnClickListener(this);
        btnFlipH = (Button)findViewById(R.id.btnFliph);btnFlipH.setOnClickListener(this);
        btnFlipV = (Button)findViewById(R.id.btnFlipv);btnFlipV.setOnClickListener(this);
        btnSlit = (Button)findViewById(R.id.btnSlit);btnSlit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNewspaper:effectstype = Effectstype.Newspager;break;
            case R.id.btnRotateButton:effectstype = Effectstype.RotateBottom;break;
            case R.id.btnShake:effectstype = Effectstype.Shake;break;
            case R.id.btnFadeIn:effectstype = Effectstype.Fadein;break;
            case R.id.btnSlideTop:effectstype = Effectstype.Slidetop;break;
            case R.id.btnSlideBottom:effectstype = Effectstype.SlideBottom;break;
            case R.id.btnSlideLeft:effectstype = Effectstype.Slideleft;break;
            case R.id.btnSlideRight:effectstype = Effectstype.Slideright;break;
            case R.id.btnFliph:effectstype = Effectstype.Fliph;break;
            case R.id.btnFlipv:effectstype = Effectstype.Flipv;break;
            case R.id.btnSlit:effectstype = Effectstype.Slit;break;
        }

        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder//.withTitle("test title")
                //.withTitleColor("#00FF00")
               // .withMessage("this is message")
                //.withMessageColor("#00AA00")
                .withDuration(500)
                .withEffect(effectstype)
                .setCustomView(R.layout.fragment_blank,this);
                //.withDividerColor(Color.BLUE)

        dialogBuilder.show();

    }
}


