package com.example.r30_a.testlayout;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
按鈕動畫的範例練習
 */
public class Main2Activity extends AppCompatActivity {

    public ImageButton btnMain,btnSearch,btnLocate,btnPlus,btnMan,btn10s,btnSetting,
            btnBluetooth,btnWifi,btnPerson,btnMail,btnShop,btnHelp,btnInfo,btnFinger;
    public ImageView roll,imageAlarm,image3D,btnFly;
    TextView txvCount;
    public Animation animTraslate, animRotate, animScale;
    public static int width, height;
    public RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0,0);
    public boolean isclick = false;
    public boolean isXrotate = false;public boolean isYrotate = false;public boolean isSet = false;

    int statusBarHeight,contentViewTop,titleBarHeight;
    float ClickX, ClickY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        startRoll();
        setButton();
        cannotsee();



        Display display = getWindowManager().getDefaultDisplay();
        height = display.getHeight();
        width = display.getWidth();
        //設定每一個view的原始margin，然後套用到view上面
        params.height = 10; params.width = 60;
        params.setMargins(20,height-98, 0, 0);


        btnFly.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    ClickX = event.getX();
                    ClickY = event.getY();

                }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    v.setX(event.getRawX()-ClickX);
                    v.setY(event.getRawY()-ClickY-getStatusBarHeight()-getTitleBarHeight());

                }



                return true;
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            /*setTranlate各參數代表意義：
                x要到的坐標，y要到的高度與最後要到的差100比較準，最後x的坐標，最後y的坐標，對應哪一個按鈕，動畫的秒數
                 */
            @Override
            public void onClick(View v) {
                if(isSet == false){
                    cansee();
                    btnSetting.setEnabled(false);
                    isSet = true;
                    btnSetting.startAnimation(setRotate(360,0.5f,0.5f));

                    btnBluetooth.startAnimation(setTranslate(0.0f,-140.0f,330,height-850, btnBluetooth, 200 ));
                    btnWifi.startAnimation(setTranslate(90f,-75,420,height-800,btnWifi,300));
                    btnPerson.startAnimation(setTranslate(160f,20,490,height-705,btnPerson,400));
                    btnMail.startAnimation(setTranslate(90f,115,420,height-610,btnMail,500));
                    btnShop.startAnimation(setTranslate(0f,160f,330,height-565,btnShop,600));
                    btnHelp.startAnimation(setTranslate(-90,115,240,height-610,btnHelp,700));
                    btnInfo.startAnimation(setTranslate(-160f,20,170,height-705,btnInfo,800));
                    btnFinger.startAnimation(setTranslate(-90,-75,240,height-800,btnFinger,900));

            }else{

                    cannotsee();
                    btnSetting.setEnabled(false);
                    isSet = false;
                    btnSetting.startAnimation(setRotate(360,0.5f,0.5f));
                    btnBluetooth.startAnimation(setTranslate(0.0f,140.0f,330,height-710,btnBluetooth,900));
                    btnWifi.startAnimation(setTranslate(-90f,70,330,height-725,btnWifi,800));
                    btnPerson.startAnimation(setTranslate(-160f,0,330,height-725,btnPerson,700));
                    btnMail.startAnimation(setTranslate(-90f,-90,330,height-725,btnMail,600));
                    btnShop.startAnimation(setTranslate(0f,-140f,330,height-725,btnShop,500));
                    btnHelp.startAnimation(setTranslate(90,-125,330,height-725,btnHelp,400));
                    btnInfo.startAnimation(setTranslate(160f,0,330,height-725,btnInfo,300));
                    btnFinger.startAnimation(setTranslate(90,70,330,height-725,btnFinger,200));
                }
                btnSetting.setEnabled(true);

            }
        });




        imageAlarm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Animation anim =null;
                anim = AnimationUtils.loadAnimation(Main2Activity.this,R.anim.scale);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    imageAlarm.startAnimation(anim);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    imageAlarm.clearAnimation();
                }
                return false;
            }
        });

        //3D翻轉的動畫
        image3D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isXrotate == false){
                    isXrotate = true;
                    set3D(Main2Activity.this,v,0,0,200,360,0,false);
                //翻轉回來
                /*Anim3D anim3D2 = new Anim3D(Main2Activity.this,0,0,-200,true);
                anim3D2.setDuration(850);
                anim3D2.setStartOffset(300);//設定動畫間隔時間
                v.measure(0,0);
                anim3D2.setCenter(v.getWidth()/2, v.getHeight()/2);
                animSet.addAnimation(anim3D2);*/

                }else {
                    isXrotate = false;
                    set3D(Main2Activity.this,v,0,0,-200,-360,0,false);
                }
            }
        });

        image3D.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(isYrotate == false){
                    isYrotate = true;
                set3D(Main2Activity.this,v,0,0,150,0,360,false);
                }else {
                    isYrotate = true;
                    set3D(Main2Activity.this,v,0,0,-150,0,-360,false);
                }
                return true;}
        });

        //倒數計時的功能
        btn10s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startRoll();
                btn10s.setEnabled(false);
                CountDownTimer timer = new CountDownTimer(11000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        millisUntilFinished -= 1000;
                        if(millisUntilFinished >4000){
                        String str = String.format("%s", String.valueOf(millisUntilFinished/1000));
                        txvCount.setText(str+" seconds");
                        }else{
                            String str = String.format("%s", String.valueOf(millisUntilFinished/1000));
                            txvCount.setText(str+" seconds");
                            txvCount.setTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onFinish() {
                        txvCount.setText("Time Out!!!!!");
                        Animation anim = AnimationUtils.loadAnimation(Main2Activity.this,R.anim.shake);
                        imageAlarm.startAnimation(anim);
                        btn10s.setEnabled(true);
                        txvCount.setTextColor(Color.BLACK);
                        //roll.clearAnimation();
                    }

                };
                timer.start();
            }
        });





        //按到小人後呈現放大效果
        btnMan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Animation anim =null;
                anim = AnimationUtils.loadAnimation(Main2Activity.this,R.anim.enlarge);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    btnMan.startAnimation(anim);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    btnMan.clearAnimation();
                }
                return false;
            }
        });






        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlus.startAnimation(setScale(2f,2f));
                btnLocate.startAnimation(setScale(0,0));
                btnSearch.startAnimation(setScale(0,0));
            }
        });

        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLocate.startAnimation(setScale(2f,2f));
                btnPlus.startAnimation(setScale(0,0));
                btnSearch.startAnimation(setScale(0,0));
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSearch.startAnimation(setScale(2f,2f));
                btnLocate.startAnimation(setScale(0,0));
                btnPlus.startAnimation(setScale(0,0));

            }
        });
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*setTranlate各參數代表意義：
                x要到的坐標，y要到的高度與最後要到的差100比較準，最後x的坐標，最後y的坐標，對應哪一個按鈕，動畫的秒數
                 */
                //isclick在此類似開關的功能
                if(isclick == false){
                    isclick = true;

                    btnMain.startAnimation(setRotate(360.0f,0.5f,0.5f));
                    btnSearch.startAnimation(setTranslate(4.0f,-300.0f,24,height-400, btnSearch, 400 ));
                    btnLocate.startAnimation(setTranslate(80.0f,-240.0f,100,height-340,btnLocate,600));
                    btnPlus.startAnimation(setTranslate(140.0f,-170.0f,160, height-270,btnPlus,800));

                }else {
                    isclick = false;
                    btnMain.startAnimation(setRotate(360.0f,0.5f,0.5f));
                    btnSearch.startAnimation(setTranslate(-8.0f,300.0f,20,height-98, btnSearch, 800 ));
                    btnLocate.startAnimation(setTranslate(-80.0f,250.0f,20,height-98,btnLocate,600));
                    btnPlus.startAnimation(setTranslate(-140.0f,200.0f,20, height-98,btnPlus,400));

                }
            }
        });

    }
//縮放或放大的效果
    public Animation setScale(float x,float y){
        //給定縮放的坐標，1是原始大小~放大或縮小的值；
        animScale = new ScaleAnimation(1,x,1,y,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.45f);
        animScale.setInterpolator(this, android.R.anim.accelerate_decelerate_interpolator);
        animScale.setDuration(500);
        animScale.setFillAfter(false);
        return animScale;

    }
//旋轉的效果
    public Animation setRotate(float toDegrees, float pivotXValue, float pivotYValue){
        //設定轉動的參數，角度從0~?度，轉的中心點，與對準的x，y軸
        //ps：若想設定為原地旋轉，x與y值要一樣
        animRotate = new RotateAnimation(0, toDegrees, Animation.RELATIVE_TO_SELF, pivotXValue, Animation.RELATIVE_TO_SELF,pivotYValue);
        //轉動效果需要監聽器
        animRotate.setDuration(500);
        animRotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {
                //設定動畫完畢後，view是否要回到原來的位置
                animRotate.setFillAfter(true);
            }
        });
        return animRotate;
    }
    public Animation setTranslate(float toX, float toY,
                                  final int lastX, final int lastY,
                                  final ImageButton button, long duration){
        animTraslate = new TranslateAnimation(0,toX,0, toY);
            //fromX 動畫開始時的點離當前view的x坐標差值，toX表離開時的差值
            //fromY 動畫開始時的點離當前view的x坐標差值，toY表離開時的差值
        animTraslate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                //設定動畫結束後的view的margin
                params = new RelativeLayout.LayoutParams(0,0);
               params.height = 70;
               params.width = 60;
                params.setMargins(lastX, lastY, 0, 0);
                button.setLayoutParams(params);
                button.clearAnimation();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        animTraslate.setDuration(duration);
        return animTraslate;
    }


    public void startRoll(){
        roll = (ImageButton)findViewById(R.id.btnroll);
        //使用animatinset可以一次裝兩種以上的動畫
        AnimationSet animationSet = new AnimationSet(false);
        //Animation anim = AnimationUtils.loadAnimation(this,R.anim.rotate);
        //anim.setInterpolator(new LinearInterpolator());//動畫無限旋轉，中間不停頓
        //roll.setAnimation(anim);

        //旋轉的動畫配置
        //參數分別為：角度從幾度到幾度，旋轉的中心點，與x、y中心坐標的位置
        RotateAnimation rAnim = new RotateAnimation(0,-360,Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        rAnim.setDuration(800);
        rAnim.setRepeatCount(-1);//設定無限循環
        rAnim.setInterpolator(new LinearInterpolator());//讓旋轉過程不間斷

        //移動的動畫配置
        //參數分別為：移動的樣式，與x、y要移動的距離-2~2
        TranslateAnimation tAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-4f,
                Animation.RELATIVE_TO_SELF,4f,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0);
        tAnim.setDuration(1500);
        tAnim.setRepeatCount(-1);
        tAnim.setRepeatMode(Animation.REVERSE);//移動方式為來回移動

        animationSet.addAnimation(rAnim);
        animationSet.addAnimation(tAnim);
        roll.setAnimation(animationSet);
        roll.startAnimation(animationSet);
    }

    public void setButton(){
        btnFly = (ImageButton)findViewById(R.id.btnFly);
        btnFinger = (ImageButton)findViewById(R.id.btnFinger);
        btnInfo = (ImageButton)findViewById(R.id.btnInfo);
        btnHelp = (ImageButton)findViewById(R.id.btnHelp);
        btnShop = (ImageButton)findViewById(R.id.btnShop);
        btnMail = (ImageButton)findViewById(R.id.btnMail);
        btnPerson = (ImageButton)findViewById(R.id.btnPerson);
        btnWifi = (ImageButton)findViewById(R.id.btnWifi);
        btnBluetooth = (ImageButton)findViewById(R.id.imgBluetooth);
        btnSetting = (ImageButton)findViewById(R.id.imageSetting);
        image3D = (ImageView)findViewById(R.id.image3D);
        btn10s = (ImageButton)findViewById(R.id.btn10s);
        imageAlarm = (ImageView)findViewById(R.id.imageAlarm);
        txvCount = (TextView)findViewById(R.id.txv1);
        btnMain = (ImageButton)findViewById(R.id.mainButton);
        btnSearch = (ImageButton)findViewById(R.id.btnSearch);
        btnSearch.setLayoutParams(params);
        btnLocate = (ImageButton)findViewById(R.id.btnLocate);
        btnLocate.setLayoutParams(params);
        btnPlus = (ImageButton)findViewById(R.id.btnPlus);
        btnPlus.setLayoutParams(params);
        btnMan = (ImageButton)findViewById(R.id.btnMan);
    }

    /*
    context取得畫面像素，確保不失真
    v 要控制的view為何
    x、y  坐標
    depthZ 處理3d效果的z軸深度
    x、ydegrees控制水平或垂直翻轉的角度
    reverse控制轉動效果的控件，false表第一次是正轉
     */
    public void set3D(Context context, View v, float x, float y, float depthZ, float Xdegrees,float Ydegrees ,boolean reverse){
        AnimationSet animSet = new AnimationSet(false);
        Anim3D anim3D = new Anim3D(context, x , y , depthZ, Xdegrees, Ydegrees, reverse);
        anim3D.setDuration(1350);//設定轉動的速度
        v.measure(0,0);//偵測view的大小
        anim3D.setCenter(v.getWidth()/2,v.getHeight()/2);//取得該view的中心點，除以2即為中心點
        anim3D.setFillAfter(true);//設定true，動畫結束後不再返回原位
        anim3D.setInterpolator(new LinearInterpolator());//動畫次數之間不停止
        anim3D.setRepeatCount(-1);//設定無限循環
        animSet.addAnimation(anim3D);
        v.startAnimation(animSet);
    }


    public void cansee(){
        btnFinger.setVisibility(View.VISIBLE);
        btnInfo.setVisibility(View.VISIBLE);
        btnHelp.setVisibility(View.VISIBLE);
        btnShop.setVisibility(View.VISIBLE);
        btnBluetooth.setVisibility(View.VISIBLE);
        btnWifi.setVisibility(View.VISIBLE);
        btnPerson.setVisibility(View.VISIBLE);
        btnMail.setVisibility(View.VISIBLE);

    }
    public void cannotsee(){
        btnFinger.setVisibility(View.GONE);
        btnInfo.setVisibility(View.GONE);
        btnHelp.setVisibility(View.GONE);
        btnShop.setVisibility(View.GONE);
        btnBluetooth.setVisibility(View.GONE);
        btnWifi.setVisibility(View.GONE);
        btnPerson.setVisibility(View.GONE);
        btnMail.setVisibility(View.GONE);

    }
//取得狀態欄的高度
    public int getStatusBarHeight(){
        int BarHeight = getResources().getIdentifier("status_bar_height","dimen","android");
        if(BarHeight > 0){
            statusBarHeight = getResources().getDimensionPixelSize(BarHeight);
        }
        return statusBarHeight;
    }
    //取得標題欄的高度
    public int getTitleBarHeight(){
        Window window = getWindow();
        contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        titleBarHeight = contentViewTop - statusBarHeight;


        return titleBarHeight;
    }
}



