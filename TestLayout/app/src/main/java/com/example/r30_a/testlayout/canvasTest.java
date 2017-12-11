package com.example.r30_a.testlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by R30-A on 2017/12/8.
 */

public class canvasTest extends View{
    Paint paint;
    Path path;
    int width, height;


    public canvasTest(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(50);
        paint.setAntiAlias(true);

    }
//從建構式去設定畫筆
    public canvasTest(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);



    }
    //設定畫布size與螢幕同樣size
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.moveTo(100,100);//一開始下筆的位置
        path.lineTo(0,200);//從moveto到lineto這個位置
        path.lineTo(200,200);//第二條線
        path.close();//關閉缺口

        canvas.drawPath(path,paint);//開始用畫筆畫路徑
    }
}
