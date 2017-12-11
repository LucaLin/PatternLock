package com.example.r30_a.testlayout.explosion.explosionwidget;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by R30-A on 2017/12/5.
 */
//編輯動畫的地方
public class ExplosionAnimatior extends ValueAnimator{

    public static final int DefaultDuration = 1500;
    private Particle[][] mParticles;
    private Paint paint;
    private View container;


    public ExplosionAnimatior(View view, Bitmap bitmap, Rect bound){

        paint = new Paint();
        container = view;


        //在一段時間內，讓valueanimator裡的值從0變化到1，
        //然後根據系統生成的隨機值改變particle的屬性值
        setFloatValues(0.0f,1.0f);
        setDuration(DefaultDuration);

        mParticles = generateParticles(bitmap, bound);
    }

    public Particle[][] generateParticles(Bitmap bitmap, Rect bound){

        //根據view的寬高，算出橫直粒子的個數，使用RECT物件來抓取view的尺寸
        int w = bound.width();
        int h = bound.height();

        int partW_count = w / Particle.PART_WH;//取橫向的個數
        int partH_Count = h / Particle.PART_WH;//取直向個數

        int bitmap_part_w = bitmap.getWidth() / partW_count;
        int bitmap_part_h = bitmap.getHeight() / partH_Count;

        Particle[][] particles = new Particle[partH_Count][partW_count];

        //取得xy坐標中bitmap的顏色值
        Point point = null;
        //使用for迴圈一一取得view的顏色，以及把每行每列的點分配給變數point
        for(int row = 0; row < partH_Count; row ++){//行

            for(int column = 0; column< partW_count; column++){//列

                //取得view變成當前粒子時所在位子的顏色
                int color = bitmap.getPixel(column * bitmap_part_w, row * bitmap_part_h);

                //取得點
                point = new Point(column,row);//列/行
                //
                particles[row][column] = Particle.generateParticle(color, bound, point);
            }


        }
        return particles;

    }
    public void draw(Canvas canvas){
        if(!isStarted()){return;}
        for (Particle[] particle : mParticles){
            for(Particle p : particle){
                p.advance((Float)getAnimatedValue());
                paint.setColor(p.color);

                paint.setAlpha((int)(Color.alpha(p.color) * p.alpha));
                canvas.drawCircle(p.cx ,p.cy , p.radius, paint);

            }

        }
        container.invalidate();
    }


    @Override
    public void start() {
        super.start();
        container.invalidate();
    }
}
