package com.example.r30_a.testlayout.explosion.explosionwidget;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import java.util.Random;

/**
 * Created by R30-A on 2017/12/5.
 */

public class Particle {
    //圓心坐標
    float cx;
    float cy;
    //半徑
    float radius;
    //顏色與透明度
    int color;
    float alpha;



    static Random random = new Random();
    Rect mBound;

    public static final int PART_WH=8;//小球的寬高

    public static Particle generateParticle(int color, Rect bound, Point point){
        int row = point.y;//高
        int column = point.x;//寬

        Particle particle = new Particle();
        particle.mBound = bound;
        particle.color = color;
        particle.alpha = 1f;

        particle.radius = PART_WH;
        particle.cx = bound.left + PART_WH *column;
        particle.cy = bound.top + PART_WH * row;

        return particle;
    }


    public void advance(float factor){
        cx = cx + factor * random.nextInt(mBound.width())* (random.nextFloat()-0.5f);
        cy = cy + factor * random.nextInt(mBound.height() / 2);

        radius = radius - factor * random.nextInt(2);

        alpha = (1f - factor)* (1+ random.nextFloat());
    }
}


