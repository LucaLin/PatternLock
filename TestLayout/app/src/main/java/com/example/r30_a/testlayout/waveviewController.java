package com.example.r30_a.testlayout;

/**
 * Created by R30-A on 2017/12/4.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by R30-A on 2017/12/4.
 */

public class waveviewController extends View{

    private Path abovePath, belowWavePath;
    private Paint aboveWavePaint, belowWavePaint;

    private DrawFilter drawFilter;

    private float p;

    private OnWaveAnimationListener onWaveAnimationListener;

    public waveviewController(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        abovePath = new Path();
        belowWavePath = new Path();
        //要幾隻波浪就設定幾隻畫筆，每隻畫筆分別處理不同的波線
        aboveWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        aboveWavePaint.setAntiAlias(true);
        aboveWavePaint.setStyle(Paint.Style.FILL);
        aboveWavePaint.setColor(Color.BLUE);

        belowWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        belowWavePaint.setAntiAlias(true);
        belowWavePaint.setStyle(Paint.Style.FILL);
        belowWavePaint.setColor(Color.BLUE);
        belowWavePaint.setAlpha(80);

        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.setDrawFilter(drawFilter);

        abovePath.reset();
        belowWavePath.reset();

        p += 0.1f;//控制水流動的速度，正數為水波往左邊流，負數是右邊

        float y,y2;//兩條水波

        double w = 2 * Math.PI / getWidth();//控制波紋的次數

        abovePath.moveTo(getLeft(), getBottom());
        belowWavePath.moveTo(getLeft(), getBottom());

        for(float x =0; x<= getWidth(); x+=20){//波紋的係數
            /**
             *  y=A * sin(w * x + p)+k，波浪公式
             *  A—振幅越大，波形在y轴上最大与最小值的差值越大
             *  w—角速度， 控制正弦周期(单位角度内震动的次数)
             *  p—初相，反映在坐标系上则为图像的左右移动。这里通过不断改变p,达到波浪移动效果
             *  k—偏距，反映在坐标系上则为图像的上移或下移。
             */

            y = (float)(8 * Math.cos((w * x) + p) +8);
            y2 = (float)(8 * Math.sin((w * x) + p));

            //lineto，中間畫的位置
            abovePath.lineTo(x, y);
            belowWavePath.lineTo(x, y2);
            //設定listener讓物件呼叫時就啟動效果
            onWaveAnimationListener.OnWaveAnimation(y);

        }
        //moveto ~ line to，從哪裡畫到哪裡
        abovePath.lineTo(getRight(),getBottom());
        belowWavePath.lineTo(getRight(), getBottom());

        canvas.drawPath(abovePath,aboveWavePaint);
        canvas.drawPath(belowWavePath,belowWavePaint);

        postInvalidateDelayed(10);
    }
    //設定listener，為之後的view可以隨著波線移動
    public void setOnWaveAnimationListener(OnWaveAnimationListener l){
        this.onWaveAnimationListener = l;
    }


    public interface OnWaveAnimationListener{
        void OnWaveAnimation(float y);
    }

}

