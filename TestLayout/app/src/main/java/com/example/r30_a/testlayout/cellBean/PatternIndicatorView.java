package com.example.r30_a.testlayout.cellBean;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.r30_a.testlayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by R30-A on 2017/12/12.
 */

public class PatternIndicatorView extends View{

    private int color;
    private int hitColor;
    private int errorColor;
    private float lineWidth;

    private List<CellBean> cellBeanList;
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
        this.initData();
        this.initAttrs(context,attr,defStyleAttr);
    }

    public void initAttrs(Context context, AttributeSet attr, int defStyleAttr){
        final TypedArray t = context.obtainStyledAttributes(attr, R.styleable.PatternIndicatorView,defStyleAttr,0);

        this.color = t.getColor(R.styleable.PatternLockerView_plv_color,Color.parseColor("#2196F3"));
        this.hitColor = t.getColor(R.styleable.PatternLockerView_plv_hitColor,Color.parseColor("#3F51B5"));
        this.errorColor = t.getColor(R.styleable.PatternLockerView_plv_errorColor,Color.parseColor("#F44336"));
        this.lineWidth = t.getDimension(R.styleable.PatternLockerView_plv_lineWidth,1);

        t.recycle();

        this.setColor(this.color);
        this.setHitColor(this.hitColor);
        this.setErrorColor(this.errorColor);
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
    public void setLineWidth(float lineWidth){
        this.lineWidth = lineWidth;
        postInvalidate();
    }


    public void initData(){
        this.paint = new Paint();
        this.paint.setDither(true);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(this.lineWidth);

        this.hitList = new ArrayList<>();
    }

    //更新圖案
    public void updateState(List<Integer>hitList, ResultState resultState){
        //1. 重設為預設值
        if(!this.hitList.isEmpty()){
            for (int i : this.hitList){
                this.cellBeanList.get(i).isHit = false;
            }
            this.hitList.clear();
        }
        //2. 更新點擊的狀況
        if(hitList != null){
            this.hitList.addAll(hitList);
        }
        if(!this.hitList.isEmpty()){
            for (int i : this.hitList){
                this.cellBeanList.get(i).isHit = true;
            }
        }
        //3. 更新結果
        this.resultState = resultState;

        //4. 再畫一次
        postInvalidate();;

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
        if  ((this.cellBeanList == null) && CellSettingPageActivity.setCell == 2){
            this.cellBeanList = new CellBeanFactory(getWidth(),getHeight()).getCellBeanListfor44();
        }else if  ((this.cellBeanList == null) && CellSettingPageActivity.setCell == 1){
            this.cellBeanList = new CellBeanFactory(getWidth(),getHeight()).getCellBeanList();
        }

        //根據配置好的球數與球盤畫出圓圈
        drawLine(canvas);
        drawCircles(canvas);
    }

    /*private void drawLine(Canvas canvas){
        if(!this.hitList.isEmpty()){
            Path path = new Path();
            CellBean first = this.cellBeanList.get(hitList.get(0));
            path.moveTo(first.x, first.y);
            for(int i =1; i < hitList.size(); i++){
                CellBean nextbean = this.cellBeanList.get(hitList.get(i));
                path.lineTo(nextbean.x, nextbean.y);
            }
            this.paint.setColor(this.getColorByState(this.resultState));
            this.paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, this.paint);
        }
    }*/

    private void drawLine(Canvas canvas){
        if(!this.hitList.isEmpty()){
            Path path = new Path();
            CellBean first = this.cellBeanList.get(hitList.get(0));
            path.moveTo(first.x, first.y);

            for(int i =1; i < hitList.size(); i++){
                CellBean nextbean = this.cellBeanList.get(hitList.get(i));
                path.lineTo(nextbean.x, nextbean.y);
            }
            this.paint.setColor(this.getColorByState(this.resultState));
            this.paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, this.paint);
        }
    }


    private void drawCircles(Canvas canvas){
        //畫出九顆球
        for(int i = 0; i< this.cellBeanList.size(); i++){
            CellBean bean = this.cellBeanList.get(i);

            //判斷有沒有被選中
            if(bean.isHit){
                this.paint.setColor(this.getColorByState(this.resultState));
                this.paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(bean.x, bean.y, bean.radius - this.paint.getStrokeWidth() /2f, paint);
            }else {//其它沒被選中的
                this.paint.setColor(this.color);
                this.paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(bean.x, bean.y, bean.radius - this.paint.getStrokeWidth() /2f,paint);
            }
        }
        }

    private int getColorByState(ResultState state){
        return state == ResultState.OK?
                this.hitColor : this.errorColor;
    }

}


