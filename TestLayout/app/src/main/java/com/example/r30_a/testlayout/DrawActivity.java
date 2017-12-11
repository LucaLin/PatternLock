package com.example.r30_a.testlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

public class DrawActivity extends View  implements View.OnClickListener{

    private Bitmap bitmap = null;
    private Canvas canvas = null;

    private float startX;
    private float startY;

    private int startPointX;
    private int startPointY;

    private int EndPointX;
    private int EndPointY;

    private int MovePointX;
    private int MovePointY;

    private int Radius = 20;
    private int Height ;

    private Paint paint = null;
    private Paint helperpaint;
    private Path path;

    private int[] colors = {Color.BLACK, Color.BLUE, Color.DKGRAY, Color.GREEN, Color.RED};




    public DrawActivity(Context context) {
        super(context);

    }

    public DrawActivity(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

       /* bitmap = Bitmap.createBitmap(500,500,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.GRAY);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(10);*/
    }



    public DrawActivity(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        path = new Path();

        helperpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        helperpaint.setStyle(Paint.Style.FILL_AND_STROKE);
        helperpaint.setStrokeWidth(5);
        helperpaint.setColor(Color.BLUE);


        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.parseColor("#3f3b2d"));
        setOnClickListener(this);
    }

     @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        startPointX = w / 2;
        EndPointX = w / 2;
        EndPointY = h /2;
        startPointY = EndPointY *5/6;

        MovePointX = startPointX;
        MovePointY = startPointY;

        Height = EndPointY - startPointY;


    }
   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float stopX = event.getX();
                float stopY = event.getY();

                canvas.drawLine(startX,startY,stopX,stopY,paint);
                startX = event.getX();
                startY = event.getY();
                invalidate();
                break;
        }

        return true;
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
      /* Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawCircle(120,80,60,paint);*/


        paint = new Paint();
        paint.setColor(0xff00ffff);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        int i = 0;

        canvas.drawArc(new RectF(60,120,260,320),
                30, i, true, paint);
        if((i+=10) > 360){i=0;}
        invalidate();
        /*path.reset();
        path.moveTo(startPointX, startPointY);
        path.lineTo(EndPointX, EndPointY);
        path.close();
        canvas.drawPath(path,helperpaint);

        int Distance = EndPointY - MovePointY;
        if(Distance >= Radius){
            canvas.drawCircle(MovePointX, MovePointY, Radius, helperpaint);
        } else{
            float ratio = Distance / Radius;

            RectF oval = new RectF(
                    MovePointX - Radius -2,
                    MovePointY - Radius - ratio * Radius +5,
                    MovePointX + Radius +2,
                    MovePointY +Radius - ratio * Radius
            );
            canvas.drawOval(oval, helperpaint);

        }

        float downRatio = (MovePointY - startPointY) / (float)Height;

        if(downRatio > 0.3){
            RectF rectF = new RectF(
                    EndPointX - Radius * downRatio,
                    EndPointY +10,
                    EndPointX + Radius * downRatio,
                    EndPointY +15
            );
            canvas.drawOval(rectF, paint);
        }
       // if(bitmap != null){
        //    canvas.drawBitmap(bitmap,0,0,paint);
       // }

        /*Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawCircle(120,80,60,paint);


        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(20);
        canvas.drawText("test",245,80,paint);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawLine(245,145,500,145,paint);*/


    }
    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setWillNotDraw(false);
        //setOnClickListener(this);
    }*/








    @Override
    public void onClick(View v) {
        ValueAnimator startAnim = ValueAnimator.ofInt(startPointY, EndPointY);
        startAnim.setInterpolator(new AccelerateInterpolator(1.2f));;
        startAnim.setDuration(500);
        startAnim.setRepeatCount(ValueAnimator.INFINITE);
        startAnim.setRepeatCount(ValueAnimator.REVERSE);
        startAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                MovePointY = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        startAnim.start();
    }
}