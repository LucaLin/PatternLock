package com.example.r30_a.patternlock.Controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.example.r30_a.patternlock.R;
import com.example.r30_a.patternlock.SettingPageActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Luca on 2018/2/14.
 */

public class PatternLockView extends View {
    //線的屬性
    private int color;
    private int hitColor;
    private int errorColor;
    private int hitAgainColor;
    private int fillColor;
    private float lineWidth;


    private ResultState resultState;
    Bean drawBean;

    private List<Bean> BeanList;
    private List<Integer> hitList, hitAgainList;
    private int hitSize;

    private Paint paint;
    Set set, firstset,secondset ;
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

        color = t.getColor(R.styleable.PatternLockerView_plv_color, Color.parseColor("#2196F3"));
        hitColor = t.getColor(R.styleable.PatternLockerView_plv_hitColor,Color.parseColor("#3F51B5"));
        errorColor = t.getColor(R.styleable.PatternLockerView_plv_errorColor,Color.parseColor("#F44336"));
        hitAgainColor = t.getColor(R.styleable.PatternLockerView_plv_hitAgainColor,Color.parseColor("#00ff00"));
        fillColor = t.getColor(R.styleable.PatternLockerView_plv_fillColor,Color.parseColor("#FAFAFA"));
        lineWidth = t.getDimension(R.styleable.PatternLockerView_plv_lineWidth,10);

        t.recycle();

        setColor(this.color);
        setHitColor(this.hitColor);
        setErrorColor(this.errorColor);
        setFillColor(this.fillColor);
        setLineWidth(this.lineWidth);

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
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(8);

        hitList = new ArrayList<>();
        hitAgainList = new ArrayList<>();
        set = new LinkedHashSet();

        firstset = new TreeSet<Integer>();
        secondset = new TreeSet<Integer>();

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
        if  ((BeanList == null) && SettingPageActivity.setCell == 44){
            BeanList = new BeanFactory(getWidth(),getHeight()).getCellBeanListfor44();

        }else if  ((BeanList == null) && SettingPageActivity.setCell == 33){
            BeanList = new BeanFactory(getWidth(),getHeight()).getCellBeanList();
        }
        //根據配置好的球數與球盤畫出圓圈
        if (!SettingPageActivity.ishide) {
            drawLine(canvas);

        }
        if(SettingPageActivity.isIgnore ){
            if(SettingPageActivity.isRepeat){
                drawCircles(canvas);
            }else {
                drawCircleNoIgnore(canvas);
            }
        }else {
            if(SettingPageActivity.isRepeat){
                drawCircles(canvas);
            }else {
                drawCircleNoIgnore(canvas);
            }
        }
    }
    private void drawLine(Canvas canvas){
        if(!hitList.isEmpty()){
            Path path = new Path();

            Bean first = BeanList.get(hitList.get(0));
            path.moveTo(first.x, first.y);//從碰到的第一個球坐標開始

            for(int i = 1; i< hitList.size(); i++){//不知道會碰到幾個，所以用for
                Bean nextBean = BeanList.get(hitList.get(i));
                path.lineTo(nextBean.x, nextBean.y);
            }
            //設定線條結束的地方, 線條呈動態移動
            if(endX != 0 || endY !=0 ){
                path.lineTo(endX, endY);

            }
            paint.setColor(this.getColorByState(this.resultState));
            paint.setStyle(Paint.Style.STROKE);

            canvas.drawPath(path, this.paint);
        }
    }

    private int getColorByState(ResultState state){
        return state == ResultState.OK ? hitColor : errorColor;
    }
    private int getRepeatColorByState(ResultState state){
        return state == ResultState.OK ? hitAgainColor : errorColor;
    }

    //畫出圓球的方法
    private void drawCircles(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);

        //根據球的數量一一畫出
        for (int i = 0; i < BeanList.size(); i++) {
            //將每個球依序傳給小球物件
            drawBean = BeanList.get(i);
            //外面的圈圈
            paint.setColor(color);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
            //上色
            paint.setColor(fillColor);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f-10 , paint);

            paint.setColor(color);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
        }
        set = new TreeSet();

        //使用者畫線時開始動作
        if (hitList.size() != 0) {

            for (int j = 0; j < hitList.size() - 1; j++) {
                //使用set不可重複特性來判斷圓球的重複情形，使用draw & drawAgain兩布林變數來控制開關

                //強制加入碰到的第一顆球並上色
                if (j == 0) {
                    drawBean = BeanList.get(hitList.get(j));
                    drawBean.Draw = true;
                    set.add(hitList.get(j));
                    //處理碰到的下一個球，比較前後數字不一時 && 不是最後一顆球時
                }
                if(j ==1){
                    drawBean = BeanList.get(hitList.get(1));
                    drawBean.Draw = true;
                    set.add(hitList.get(1));
                }
                if (hitList.get(j) != hitList.get(j + 1) && hitList.size() - 1 != (j + 1) && j != 0) {

                    drawBean = BeanList.get(hitList.get(j + 1));
                    drawBean.Draw = true;
                    //檢查是否有重複的球跑進清單，有的話就要再畫一次，沒有的話就單純加入set內供下一次比較
                    if (set.contains(hitList.get(j + 1))) {
                        drawBean.DrawAgain = true;
                    } else {
                        set.add(hitList.get(j + 1));
                    }
                    //強制加入碰到的最後一顆球並上色
                } else if (hitList.size() - 1 == (j + 1)) {

                    drawBean = BeanList.get(hitList.get(j + 1));
                    drawBean.Draw = true;
                }

//-----------------------------------------------------------------------------
                //上色處理

                if (drawBean.Draw) {
                    paint.setColor(this.getColorByState(this.resultState));
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
                    //上色
                    paint.setColor(this.fillColor);
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f - 10, paint);
                    //中間的點

                    paint.setColor(this.getColorByState(this.resultState));
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
                }

                if ((drawBean.Draw && drawBean.DrawAgain) && (SettingPageActivity.showrepeat)) {
                    paint.setColor(this.getColorByState(this.resultState));
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
                    //上色
                    paint.setColor(this.fillColor);
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f - 10, paint);

                    paint.setColor(this.getRepeatColorByState(this.resultState));
                    canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
                }
            }
        }
    }
    private void drawCircleNoIgnore(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);

        //根據球的數量一一畫出
        for (int i = 0; i < BeanList.size(); i++) {
            //將每個球依序傳給小球物件
            drawBean = BeanList.get(i);
            //外面的圈圈
            paint.setColor(this.color);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
            //上色
            paint.setColor(this.fillColor);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f - 10, paint);

            paint.setColor(this.color);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
        }

        for(int i = 0; i < hitList.size();i++){
            drawBean = BeanList.get(hitList.get(i));

            //  if (drawBean.Draw) {
            paint.setColor(this.getColorByState(this.resultState));
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
            //上色
            paint.setColor(this.fillColor);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f - 10, paint);
            //中間的點

            paint.setColor(this.getColorByState(this.resultState));
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
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
        chooseUpdateMethod(event);
        //3. 設定監聽器
        if(listener !=null){
            listener.onStart(this);
        }
    }
    //處理移動時要處理的動作
    private void handleActionMove(MotionEvent event){

        //1. 更新點擊的狀況
        chooseUpdateMethod(event);
        //2. 更新移動到的坐標位置
        endX = event.getX();
        endY = event.getY();
        //3. 通知監聽, 監聽改變中的hitlist(點中的球)

        final int size = hitList.size();
        if(listener != null && hitSize != size){
            hitSize = size;
            listener.onChange(this,hitList);

        }
    }
    //處理手指離開時的動作
    private void handleActionUp(MotionEvent event){

        //1. 更新坐標
        chooseUpdateMethod(event);
        this.endX = 0;
        this.endY = 0;

        //2. 通知監聽
        if(listener!= null){
            setHitlist(hitList);

            if(hitAgainList.size() != 0 && SettingPageActivity.isRepeat){
                hitAgainList.remove(0);
            }
            listener.onComplete(this,this.hitAgainList);

        }
        //3. 有必要的話開始計時器
        if(hitList.size() > 0){
            set.clear();
            starTimer();
        }
    }

    //重新整理成正確的list
    public List<Integer> setHitlist(List<Integer> list) {
        for(int i = 0; i< list.size()-1;i++) {//index限制讀到倒數第二個

            if(i==0 && !hitAgainList.contains(list.get(0))){
                hitAgainList.add(list.get(i));
            }

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
        },700);//0.7秒後回復
    }

    public void clearHitState(){
        clearHitData();
        if(this.listener != null){
            this.listener.onClear(this);
        }
        postInvalidate();
    }

    private void clearHitData(){//清除所有list
        resultState  =ResultState.OK;
        for(int i =0; i< this.hitList.size(); i++){
            BeanList.get(hitList.get(i)).isHit = false;
            BeanList.get(hitList.get(i)).Draw = false;
            BeanList.get(hitList.get(i)).DrawAgain = false;
        }
        hitAgainList.clear();
        hitList.clear();
        hitSize = 0;
    }

    private void updateDownState(MotionEvent event){
        final float x = event.getX();
        final float y = event.getY();

        for (Bean c : BeanList) {
            if(!c.isHit && c.of(x,y)){
                c.isHit = true;
                hitList.add(c.id);
            }
            for(Bean cc : BeanList){
                if(c.isHit && c.of(x,y)){
                    hitList.add(c.id);
                    c.isHit =false;
                }
            }
        }

    }

    private void updateDownStatenoIgnoreNoRepeat(MotionEvent event){
        final float x = event.getX();
        final float y = event.getY();

        for (Bean c : BeanList) {
            if(!c.isHit && c.ofnoIgnore(x,y)){
                c.isHit = true;
                hitList.add(c.id);
            }

        }

    }
    private void updateDownStateRepeatnoIgnore(MotionEvent event){
        final float x = event.getX();
        final float y = event.getY();

        for (Bean c : BeanList) {
            if(!c.isHit && c.ofnoIgnore(x,y)){
                c.isHit = true;
                hitList.add(c.id);
            }
            for(Bean cc : BeanList){
                if(c.isHit && c.ofnoIgnore(x,y)){
                    hitList.add(c.id);
                    c.isHit =false;
                }
            }
        }

    }

    private void updateHitState(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        for (Bean c : BeanList) {
            if (!c.isHit && c.of(x, y) ) {
                c.isHit =true;
                hitList.add(c.id);
            }
        }

    }

    public void setResultState(ResultState resultState){
        this.resultState = resultState;
    }

    public void chooseUpdateMethod(MotionEvent event){
        if (SettingPageActivity.setCell==44){
            if(SettingPageActivity.isIgnore){
                if(SettingPageActivity.isRepeat){
                    updateDownState(event);
                }else {
                    updateHitState(event);
                }
            }else {
                if (SettingPageActivity.isRepeat){
                    updateDownStateRepeatnoIgnore(event);
                }else {
                    updateDownStatenoIgnoreNoRepeat(event);
                }
            }
        }else if(SettingPageActivity.setCell==33){

            if(SettingPageActivity.isIgnore){
                if(SettingPageActivity.isRepeat){
                    updateDownState(event);
                }else {
                    updateHitState(event);
                }

            }else {
                if(SettingPageActivity.isRepeat){
                    updateDownStateRepeatnoIgnore(event);

                }else {
                    updateDownStatenoIgnoreNoRepeat(event);
                }
            }
        }
    }
}
