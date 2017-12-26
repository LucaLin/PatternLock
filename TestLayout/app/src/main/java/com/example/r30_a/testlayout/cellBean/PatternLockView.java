package com.example.r30_a.testlayout.cellBean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.r30_a.testlayout.Config;
import com.example.r30_a.testlayout.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by R30-A on 2017/12/12.
 */
//開始畫出小球的畫盤
public class PatternLockView extends View{
    //線的屬性
    private int color;
    private int hitColor;
    private int errorColor;
    private int fillColor;
    private float lineWidth;

    private ResultState resultState;
    CellBean drawBean;

    private List<CellBean> cellBeanList;
    private List<Integer> hitList, hitAgainList;
    private int hitSize;

    private Paint paint;
    ArraySet set;

    float endX, endY;//小球連結的最後坐標位子
    public OnPatternChangeListener listener;

    public PatternLockView(Context context) {
        this(context,null);
    }
    public PatternLockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    public PatternLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs, defStyleAttr);
    }

    public void setOnPatternChangedListener(OnPatternChangeListener listener){
        this.listener = listener;
    }

    private void init(Context context, AttributeSet attr, int defStyleAttr){
        initattr(context,attr,defStyleAttr);
        initData();
    }
//設定各種狀態的顏色提示
    private void initattr(Context context, AttributeSet attr, int defStyleAttr){
        final TypedArray t = context.obtainStyledAttributes(attr, R.styleable.PatternLockerView,defStyleAttr,0);

        this.color = t.getColor(R.styleable.PatternLockerView_plv_color, Color.parseColor("#2196F3"));
        this.hitColor = t.getColor(R.styleable.PatternLockerView_plv_hitColor,Color.parseColor("#3F51B5"));
        this.errorColor = t.getColor(R.styleable.PatternLockerView_plv_errorColor,Color.parseColor("#F44336"));
        this.fillColor = t.getColor(R.styleable.PatternLockerView_plv_fillColor,Color.parseColor("#FAFAFA"));
        this.lineWidth = t.getDimension(R.styleable.PatternLockerView_plv_lineWidth,1);

        t.recycle();

        this.setColor(this.color);
        this.setHitColor(this.hitColor);
        this.setErrorColor(this.errorColor);
        this.setFillColor(this.fillColor);
        this.setLineWidth(this.lineWidth);

    }

    public void setColor(int color){
        this.color = color;
        postInvalidate();
    }
    public void setHitColor(int hitColor){
        this.hitColor = hitColor;
        postInvalidate();
    }
    public void setErrorColor(int errorColor){
        this.errorColor = errorColor;
        postInvalidate();
    }
    public void setFillColor(int fillColor){
        this.fillColor = fillColor;
        postInvalidate();
    }
    public void setLineWidth(float lineWidth){
        this.lineWidth = lineWidth;
        postInvalidate();
    }
//設置畫筆跟列表
    private void initData(){
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(this.lineWidth);

        this.hitList = new ArrayList<>();
        this.hitAgainList = new ArrayList<>();

        set = new ArraySet();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //取得最低值
        int a = Math.min(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(a,a);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //第一次打開畫面時, 取得小球列表，根據建構式及setting頁面建立小球數
        if  ((this.cellBeanList == null) && CellSettingPageActivity.setCell == 2){
            this.cellBeanList = new CellBeanFactory(getWidth(),getHeight()).getCellBeanListfor44();

        }else if  ((this.cellBeanList == null) && CellSettingPageActivity.setCell == 1){
            this.cellBeanList = new CellBeanFactory(getWidth(),getHeight()).getCellBeanList();
        }

        //根據配置好的球數與球盤畫出圓圈
        if (!CellSettingPageActivity.ishide) {
            drawLine(canvas);

        }
        drawCircles(canvas);
    }

    private void drawLine(Canvas canvas){
        if(!this.hitList.isEmpty()){
        Path path = new Path();

        CellBean first = this.cellBeanList.get(this.hitList.get(0));
        path.moveTo(first.x, first.y);//從碰到的第一個球坐標開始

        for(int i = 1; i< this.hitList.size(); i++){//不知道會碰到幾個，所以用for
            CellBean nextBean = this.cellBeanList.get(this.hitList.get(i));
            path.lineTo(nextBean.x, nextBean.y);
            nextBean.isHit = true;
        }
        //設定線條結束的地方, 線條呈動態移動
        if(((this.endX != 0) || (this.endY !=0)) /*&& (this.hitList.size() < 16)*/){
            path.lineTo(this.endX, this.endY);
        }
        this.paint.setColor(this.getColorByState(this.resultState));
        this.paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, this.paint);
        }
    }

private int getColorByState(ResultState state){
        return state == ResultState.OK ? this.hitColor : this.errorColor;
}

//畫出圓球的方法
    private void drawCircles(Canvas canvas) {
        this.paint.setStyle(Paint.Style.FILL);

        //根據球的數量一一畫出
        for (int i = 0; i < cellBeanList.size(); i++) {
            //將每個球依序傳給小球物件
            drawBean = cellBeanList.get(i);
            //外面的圈圈
            this.paint.setColor(this.color);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius, paint);
            //上色
            this.paint.setColor(this.fillColor);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius - lineWidth, paint);

        }

        set = new ArraySet();

        //使用者畫線時開始動作
        if (hitList.size() != 0) {

            for (int j = 0; j < hitList.size() - 1; j++) {
                //使用set不可重複特性來判斷圓球的重複情形，使用draw & drawAgain兩布林變數來控制開關

                //強制加入碰到的第一顆球並上色
                if (j == 0) {
                    drawBean = cellBeanList.get(hitList.get(j));
                    drawBean.Draw = true;
                    set.add(hitList.get(j));
                //處理碰到的下一個球，比較前後數字不一時 && 不是最後一顆球時
                }else if (hitList.get(j) != hitList.get(j + 1) && hitList.size() - 1 != (j + 1)) {

                    drawBean = cellBeanList.get(hitList.get(j + 1));
                    drawBean.Draw = true;
                    //檢查是否有重複的球跑進清單，有的話就要再畫一次，沒有的話就單純加入set內供下一次比較
                    if(set.contains(hitList.get(j+1))){
                        drawBean.DrawAgain  =true;
                    }else {
                        set.add(hitList.get(j+1));
                    }
                //強制加入碰到的最後一顆球並上色
                }else  if(hitList.size() - 1 == (j + 1)){

                    drawBean = cellBeanList.get(hitList.get(j+1));
                    drawBean.Draw = true;
                   // set.add(hitList.get(j+1));
                }

//-----------------------------------------------------------------------------
                //上色處理
                if (drawBean.Draw) {
                    this.paint.setColor(this.getColorByState(this.resultState));
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius, paint);
                    //上色
                    this.paint.setColor(this.fillColor);
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius - this.lineWidth, paint);
                    //中間的點
                    this.paint.setColor(this.getColorByState(this.resultState));
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 5f, paint);

                }
                if ((drawBean.Draw && drawBean.DrawAgain) && (CellSettingPageActivity.isrepeat)){
                    this.paint.setColor(Color.GREEN);
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius, paint);
                    //上色
                    this.paint.setColor(this.fillColor);
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius - this.lineWidth, paint);

                    this.paint.setColor(Color.GREEN);
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 5f, paint);
                }
            }

        }
    }

//處理各種觸碰狀況
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
            return super.onTouchEvent(event);
        }
        boolean isHandle = false;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                isHandle = true;
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(event);
                isHandle =true;
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(event);
                isHandle = true;
                break;
            default:break;
        }

        postInvalidate();
        return isHandle ? true : super.onTouchEvent(event);
    }


    //處理按下去時要處理的動作
    private void handleActionDown(MotionEvent event){
        //1. 先重新配置畫盤
        clearHitData();
        //2. 更新按到的球有哪些
        updateHitState(event);
        //3. 設定監聽器
        if(this.listener !=null){
            this.listener.onStart(this);
            setHitlist(hitList);
        }

    }
    //處理移動時要處理的動作
    private void handleActionMove(MotionEvent event){
        //1. 更新點擊的狀況
        updateHitState(event);
        //2. 更新移動到的坐標位置
        this.endX = event.getX();
        this.endY = event.getY();
        //3. 通知監聽, 監聽改變中的hitlist(點中的球)

        final int size = this.hitList.size();
        if((this.listener != null) && (this.hitSize != size)){
            this.hitSize = size;
            this.listener.onChange(this, this.hitList);

        }
    }
    //處理手指離開時的動作
    private void handleActionUp(MotionEvent event){
        //1. 更新坐標
        updateHitState(event);
        Log.d("234","324");
        this.endX = 0;
        this.endY = 0;

        //2. 通知監聽
        if(this.listener!= null){
            setHitlist(hitList);
            hitAgainList.remove(0);
            this.listener.onComplete(this,this.hitAgainList);

        }
        //3. 有必要的話開始計時器
        if(this.hitList.size() > 0){
            set.clear();
            starTimer();
        }

    }

    //重新整理成正確的list
    public List<Integer> setHitlist(List<Integer> list) {
        for(int i = 0; i< list.size()-1;i++) {//index限制讀到倒數第二個



            //第一次比對數字是否相同，不相同著加入前項到新list
        if((i+1) <= list.size()){

           if (i !=0 && list.get(i) != list.get(i + 1)) {
                hitAgainList.add(list.get(i));

           }
        }//第二次強迫加入最後點到的球的編號
            if(i+1 == list.size()-1){
                hitAgainList.add(list.get(i+1));
            }
        }
        return hitAgainList;
    }

    //設定放開手指後開始計時圖形保留時間
    private void starTimer(){
        setEnabled(false);
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                setEnabled(true);
                clearHitState();
            }
        },1000);//1秒後回復
    }

    public void clearHitState(){
        clearHitData();
        if(this.listener != null){
            this.listener.onClear(this);
        }
        postInvalidate();
    }

    private void clearHitData(){//清除所有list
        this.resultState  =ResultState.OK;
        for(int i =0; i< this.hitList.size(); i++){
            this.cellBeanList.get(hitList.get(i)).isHit = false;
            this.cellBeanList.get(hitList.get(i)).Draw = false;
            this.cellBeanList.get(hitList.get(i)).DrawAgain = false;
        }

        this.hitList.clear();//set = new ArraySet();
        this.hitSize = 0;
    }

    private List updateHitState(MotionEvent event){
        final float x = event.getX();
        final float y = event.getY();
        //根據摸到的位置來增加選取到的小球數
        for(CellBean c : this.cellBeanList) {
            //把碰到的球的位置放給list，不一定是第一個，就根據摸到哪個，給它對應的小球id
            if (!c.isHit && c.of(x, y)) {//如何增加hitlist的長度
                c.isHit = true;
                //hitfirst, 再一個boolean ishitagain控制重複到的號碼
                this.hitList.add(c.id);
            }
            for(CellBean cc : this.cellBeanList){
                if(c.isHit && c.of(x,y)){
                    this.hitList.add(c.id);
                }
            }
        }
        return hitList;
    }


    public void setResultState(ResultState resultState){
        this.resultState = resultState;
    }
}
