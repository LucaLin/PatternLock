package com.example.r30_a.patternlock.Controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.r30_a.patternlock.R;
import com.example.r30_a.patternlock.SettingPageActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca on 2018/2/14.
 */

public class PatternIndicatorView extends View {

    private int color;
    private int hitColor;
    private int errorColor;
    private float lineWidth;
    PatternLockView patternLockView;
    Context context;
    Bean Bean;

    private List<Bean> BeanList;
    private List<Integer> hitList;
    private Paint paint;
    private ResultState resultState;
    public PatternIndicatorView(Context context) {
        this(context,null);
    }

    public PatternIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PatternIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);

    }
    public void init(Context context, AttributeSet attr, int defStyleAttr){
        initData();
        initAttrs(context,attr,defStyleAttr);
    }

    public void initAttrs(Context context, AttributeSet attr, int defStyleAttr){
        final TypedArray t = context.obtainStyledAttributes(attr, R.styleable.PatternIndicatorView,defStyleAttr,0);

        color = t.getColor(R.styleable.PatternLockerView_plv_color, Color.parseColor("#2196F3"));
        hitColor = t.getColor(R.styleable.PatternLockerView_plv_hitColor,Color.parseColor("#3F51B5"));
        errorColor = t.getColor(R.styleable.PatternLockerView_plv_errorColor,Color.parseColor("#F44336"));
        lineWidth = t.getDimension(R.styleable.PatternLockerView_plv_lineWidth,1);

        t.recycle();

        setColor(color);
        setHitColor(hitColor);
        setErrorColor(errorColor);
        setLineWidth(lineWidth);
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
    public void setLineWidth(float lineWidth){
        this.lineWidth = lineWidth;
        postInvalidate();
    }


    public void initData(){
        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(this.lineWidth);
        hitList = new ArrayList<>();

    }

    //更新圖案
    public void updateState(List<Integer>hitList, ResultState resultState){
        //1. 重設為預設值
        if(!this.hitList.isEmpty()){
            for (int i : this.hitList){
                BeanList.get(i).isHit = false;
            }
            this.hitList.clear();

        }
        //2. 更新點擊的狀況
        if(hitList != null){
            this.hitList.addAll(hitList);
        }
        if(!this.hitList.isEmpty()){
            for (int i : this.hitList){
                BeanList.get(i).isHit = true;
            }
        }
        //3. 更新結果
        this.resultState = resultState;

        //4. 再畫一次
        postInvalidate();

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
        //第一次打開畫面時, 取得小球列表，根據建構式直接new出來的物件及前頁的設定判斷要出哪一種球盤
        if  (BeanList == null && SettingPageActivity.setCell == 44){
            BeanList = new BeanFactory(getWidth(),getHeight()).getCellBeanListfor44();
        }else if  ((BeanList == null) && SettingPageActivity.setCell == 33){
            BeanList = new BeanFactory(getWidth(),getHeight()).getCellBeanList();
        }
        //根據配置好的球數與球盤畫出圓圈
        drawLine(canvas);
        drawCircles(canvas);

    }


    private void drawLine(Canvas canvas){
        if(!hitList.isEmpty()){
            Path path = new Path();
            Bean first = BeanList.get(hitList.get(0));
            path.moveTo(first.x, first.y);

            for(int i =1; i < hitList.size(); i++){
                Bean nextbean = BeanList.get(hitList.get(i));
                path.lineTo(nextbean.x, nextbean.y);
            }
            paint.setColor(this.getColorByState(this.resultState));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, this.paint);
        }
    }


    private void drawCircles(Canvas canvas){
        //畫出九顆球
        for(int i = 0; i< BeanList.size(); i++){
            Bean bean = BeanList.get(i);

            //判斷有沒有被選中
            if(bean.isHit){
                paint.setColor(this.getColorByState(this.resultState));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(bean.x, bean.y, bean.radius - paint.getStrokeWidth() /2f, paint);
            }else {//其它沒被選中的
                this.paint.setColor(this.color);
                this.paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(bean.x, bean.y, bean.radius - paint.getStrokeWidth() /2f,paint);
            }
        }
    }

    private int getColorByState(ResultState state){
        return state == ResultState.OK?
                hitColor : errorColor;
    }
}