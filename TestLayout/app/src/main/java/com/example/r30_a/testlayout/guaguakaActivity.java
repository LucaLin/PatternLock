package com.example.r30_a.testlayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class guaguakaActivity extends View {
    //layout在設置時可預設一張圖片來源，如要更改圖片或是隨機，可再經由物件設定

    private Paint outterPaint = new Paint();

    private Path path = new Path();

    private Canvas canvas;

    private Bitmap bitmap, backbitmap;


    private int lastX, lastY;


    public guaguakaActivity(Context context) {
        super(context);
    }

    public guaguakaActivity(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public guaguakaActivity(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        path = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        //初始化畫布
        bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(bitmap);
        //設定畫筆
        outterPaint.setColor(Color.RED);
        outterPaint.setAntiAlias(true);
        outterPaint.setDither(true);
        outterPaint.setStyle(Paint.Style.STROKE);
        outterPaint.setStrokeJoin(Paint.Join.ROUND);//圓角
        outterPaint.setStrokeCap(Paint.Cap.ROUND);//圓角
        outterPaint.setStrokeWidth(40);//畫筆的粗細
        //上層畫布的顏色
        canvas.drawColor(Color.parseColor("#c0c0c0"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPath();
        canvas.drawBitmap(bitmap,0,0,null);

    }

    private void drawPath(){
        //畫出的線是上面那一層的
        outterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawPath(path, outterPaint);
    }

    //偵測使用者的位置畫出顏色
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        //取得xy坐標
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (action){
            //觸碰的時候
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                path.moveTo(lastX, lastY);
                break;
            //移動的時候
            case MotionEvent.ACTION_MOVE:
                //取得絕對值
                int dx = Math.abs(x - lastX);
                int dy = Math.abs(y - lastY);

                if(dx > 3 || dy > 3)
                    path.lineTo(x,y);

                lastX = x;
                lastY = y;
                break;
        }
        //開始畫圖時的啟動動作
        invalidate();
        return true;
    }
}
