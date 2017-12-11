package util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;

import android.util.AttributeSet;
import android.view.View;

/**
 * Created by R30-A on 2017/12/4.
 */

public class waveViewUtil extends View{

    private Path abovePath, belowWavePath;
    private Paint aboveWavePaint, belowWavePaint;

    private DrawFilter drawFilter;

    private float p;

    private OnWaveAnimationListener onWaveAnimationListener;




    public waveViewUtil(Context context,  AttributeSet attrs) {
        super(context, attrs);

        abovePath = new Path();
        belowWavePath = new Path();

        aboveWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        aboveWavePaint.setAntiAlias(true);
        aboveWavePaint.setStyle(Paint.Style.FILL);
        aboveWavePaint.setColor(Color.WHITE);

        belowWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        belowWavePaint.setAntiAlias(true);
        belowWavePaint.setStyle(Paint.Style.FILL);
        belowWavePaint.setColor(Color.WHITE);
        belowWavePaint.setAlpha(80);

        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.setDrawFilter(drawFilter);

        abovePath.reset();
        belowWavePath.reset();

        p = 0.1f;

        float y,y2;

        double w = 2 * Math.PI / getWidth();

        abovePath.moveTo(getLeft(), getBottom());
        belowWavePath.moveTo(getLeft(), getBottom());

        for(float x =0; x<= getWidth(); x+=20){
            /**
             *  y=A * sin(w * x + p)+k
             *  A—振幅越大，波形在y轴上最大与最小值的差值越大
             *  w—角速度， 控制正弦周期(单位角度内震动的次数)
             *  p—初相，反映在坐标系上则为图像的左右移动。这里通过不断改变φ,达到波浪移动效果
             *  k—偏距，反映在坐标系上则为图像的上移或下移。
             */

            y = (float)(8 * Math.cos(w * x + p) +8);
            y2 = (float)(8 * Math.sin(w * x + p));

            abovePath.lineTo(x, y);
            belowWavePath.lineTo(x, y2);

            onWaveAnimationListener.OnWaveAnimation(y);
        }

        abovePath.lineTo(getRight(),getBottom());
        belowWavePath.lineTo(getRight(), getBottom());

        canvas.drawPath(abovePath,aboveWavePaint);
        canvas.drawPath(belowWavePath,belowWavePaint);

        postInvalidateDelayed(20);

    }

    public void setOnWaveAnimationListener(OnWaveAnimationListener l){
        this.onWaveAnimationListener = l;
    }


    public interface OnWaveAnimationListener{
        void OnWaveAnimation(float y);
    }

}

