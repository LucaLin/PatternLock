package com.example.r30_a.testlayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class huahuaActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new canvasTest(this));




    }
    class canvasTest extends View {
        Paint paint;
        Path path;
        int width, height;

        //從建構式去設定畫筆
        public canvasTest(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);

        }

        public canvasTest(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);



        }
        //設定畫布size與螢幕同樣size
       /* @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
            height = getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec);
            setMeasuredDimension(width,height);
        }*/

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);


            path = new Path();
            path.moveTo(100,100);
           // path.lineTo(0,200);
          //  path.lineTo(200,200);
            path.cubicTo(100,100,200,500,400,100);
            canvas.drawPath(path,paint);
            /*
            //畫弧線
            RectF rect = new RectF(0,0,100,100);
            canvas.drawArc(rect,0,90,true,paint);
            */
            //canvas.drawCircle(100,100,90,paint);
        }
    }

}
