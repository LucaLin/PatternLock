package com.example.r30_a.testlayout;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class btnEffectActivity extends AppCompatActivity {
    Button btnpop,btncontext,btnpopwindow;
    ImageButton imageAdd;
    AnimationSet animSet;
    TranslateAnimation translateAnimation;
    AlphaAnimation alphaAnimation;
    ScaleAnimation scaleAnimation;
    TextView txvadd,txvpop;
    ProgressBar progressBar;
    ObjectAnimator objectAnimator;
    PopupMenu popupMenu;
    PopupWindow popupWindow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btn_effect);
        init();
        registerForContextMenu(btncontext);
        btnpop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        btnpopwindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[]location = new int[2];
                Point point= new Point();

                v.getLocationOnScreen(location);

                point.x = location[0];
                point.y = location[1];

                //showStatusPopup(btnEffectActivity.this,point);




            }
        });




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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.popupmenu,menu);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.next:
                txvpop.setText("next");
                break;
            case R.id.last:
                txvpop.setText("last");
                break;
            case R.id.set:
                txvpop.setText("set");
                break;




        }


        return super.onContextItemSelected(item);
    }

    public void init(){
        btnpopwindow = (Button)findViewById(R.id.btnpopwindow);
        btncontext = (Button)findViewById(R.id.btncontext);
        btnpop = (Button)findViewById(R.id.btnpop);
        imageAdd = (ImageButton)findViewById(R.id.imgadd);
        txvadd = (TextView)findViewById(R.id.txvadd);
        txvpop = (TextView)findViewById(R.id.txvpop);
        txvadd.setVisibility(View.INVISIBLE);

    }


    private void showPopupMenu(View view){
        popupMenu = new PopupMenu(this,view);

        popupMenu.getMenuInflater().inflate(R.menu.popupmenu,popupMenu.getMenu());


        //控制每一個item的點擊事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.next:
                        txvpop.setText("next");
                        break;
                    case R.id.last:
                        txvpop.setText("last");
                        break;
                    case R.id.set:
                        txvpop.setText("set");
                        break;




                }


                return true;
            }
        });


        //控件消失時的事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        })
        ;

    }
    private void showStatusPopup(Activity context, Point point){

        //配置自定layout
        LinearLayout viewGroup = (LinearLayout)context.findViewById(R.id.linearlayout);
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popup_layout,null);
        //建立popwindow
        popupWindow = new PopupWindow(context);
        popupWindow.setContentView(view);
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        //調整出現的位置
        int offsetX = -20;
        int offsetY = 50;
        //清除預設的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //秀出popwindow的動作with調整後的位置
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, point.x+offsetX, point.y+offsetY);



    }

}


