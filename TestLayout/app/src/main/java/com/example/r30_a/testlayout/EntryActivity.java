package com.example.r30_a.testlayout;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.testlayout.cellBean.CellPageActivity;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class EntryActivity extends AppCompatActivity {
    Button btnGO,btnAlert,btnToOpenGL,btn3D2,btnWave, btnDialog,btnGame,btntoflip,btnGuaGua,
            btnButtonEffect, btnExplosion, btnPile,btnDraw,btnRecycle,btnCollapse
            ,btnCellBean,btnTest;

    ImageButton btnOK,btnCancel;
    TextView txvAnswer;
    EditText edt;
    int answer;

    ShimmerLayout shimmerLayout;
    // private AnimatorSet RightOutSet, LeftInSet;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
       /* RightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(this,R.animator.anim_out);
        LeftInSet = (AnimatorSet)AnimatorInflater.loadAnimator(this,R.animator.anim_in);

        RightOutSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                shimmerLayout.setClickable(false);
            }
        });

        LeftInSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                shimmerLayout.setClickable(true);
            }
        });*/

       btnTest = (Button)findViewById(R.id.btnTest);
       btnTest.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this, TestActivity.class));
           }
       });


        btnCellBean = (Button)findViewById(R.id.btnCellBean);
        btnCellBean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this, CellPageActivity.class));
            }
        });


        btnCollapse = (Button)findViewById(R.id.btnCollape);
        btnCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this,MainActivity.class));
            }
        });


        btnRecycle = (Button)findViewById(R.id.btnRecycle);
        btnRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this,RecycleViewActivity.class));
            }
        });

        btnDraw = (Button)findViewById(R.id.btnDraw);
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this,huahuaActivity.class));
            }
        });

        btnPile = (Button)findViewById(R.id.btnpile);
        btnPile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this, pileActivity.class));
            }
        });

        btnExplosion = (Button)findViewById(R.id.btnExplo);
        btnExplosion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this,explosionActivity.class));
            }
        });

        btnButtonEffect = (Button)findViewById(R.id.btnButtonEffect);
        btnButtonEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this,btnEffectActivity.class));
            }
        });

        btnGuaGua = (Button)findViewById(R.id.btnGuaGua);
        btnGuaGua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this, guaguaka.class));
            }
        });

        shimmerLayout = (ShimmerLayout)findViewById(R.id.shimmerlayout);
        shimmerLayout.startShimmerAnimation();



        btntoflip = (Button)findViewById(R.id.btntoflip);
        btntoflip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setAnim(v);
                startActivity(new Intent(EntryActivity.this,flipCardActivity.class));
            }
        });

        btnGame = (Button)findViewById(R.id.btnGame);
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this, game_ctivity.class));
            }
        });

        btnDialog = (Button)findViewById(R.id.btnDialog);
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(Main3Activity.this);
                dialogBuilder.withTitle("test title")
                            .withTitleColor("#00FF00")
                            .withMessage("this is message")
                            .withMessageColor("#00AA00")
                            .withDuration(700)
                           // .withEffect(Effectstype.Newspager)
                          //  .withEffect(Effectstype.RotateBottom)
                           // .withEffect(Effectstype.Shake)
                              .withEffect(Effectstype.RotateBottom)
                            .show();*/
                startActivity(new Intent(EntryActivity.this,alert_dialogActivity.class));
            }
        });

        btnWave = (Button)findViewById(R.id.btnWave);
        btnWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this,waveView.class));
            }
        });

        btnToOpenGL = (Button)findViewById(R.id.btnToOpenGL);
        btnToOpenGL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this,Main4Activity_opengl.class));
            }
        });


        btn3D2 = (Button)findViewById(R.id.btn3D2);


        btnGO = (Button)findViewById(R.id.btngo);


        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction t = fm.beginTransaction();

                BlankFragment fg = new BlankFragment();

                t.add(R.id.layout,fg,"MYfg");
                t.commit();
            }
        });
        btnAlert = (Button)findViewById(R.id.btnAlert);
        //呼叫客製化的DIALOG
        //findview的時候前面要加上dialog,以辨識出是客製裡的view，就可以設定動作
        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //建立對話框
                final Dialog dialog = new Dialog(EntryActivity.this);
                //自訂view的來源layout
                dialog.setContentView(R.layout.fragment_blank);
                //細部的動作設定，此範例為辨識非機器人的辨識對話框，view來自於自訂layout
                txvAnswer = (TextView)dialog.findViewById(R.id.txvAns);
                answer = (int)(Math.random()*999999)+1;
                while(String.valueOf(answer).length()<6){
                    answer = (int)(Math.random()*999999)+1;
                }
                txvAnswer.setText(String.valueOf("提示答案"+answer));

                edt = (EditText)dialog.findViewById(R.id.editText);

                btnOK = (ImageButton)dialog.findViewById(R.id.btnOK);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Integer.parseInt(edt.getText().toString())==answer){
                            dialog.cancel();
                            Toast.makeText(EntryActivity.this,"恭喜你輸入正確",Toast.LENGTH_SHORT).show();
                        }else if(Integer.parseInt(edt.getText().toString())!=answer ||
                                edt.getText()==null){
                            Toast.makeText(EntryActivity.this,"答案錯了囉！",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.show();

                btnCancel = (ImageButton)dialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }

        });

    }
   /* public void setAnim(View v){
        RightOutSet.setTarget(v);
        LeftInSet.setTarget(v);
        RightOutSet.start();
        LeftInSet.start();
    }*/

}






/*
水波按鈕

repositories {
    mavenCentral()
    mavenLocal()
}

    compile 'com.github.markushi:circlebutton:1.1'

    <at.markushi.ui.CircleButton/>
 */