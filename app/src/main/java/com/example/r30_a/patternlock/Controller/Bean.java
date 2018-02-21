package com.example.r30_a.patternlock.Controller;

/**
 * Created by Luca on 2018/2/14.
 */

public class Bean {

    public int id;//小球的編號
    public float x;//小球的x與y坐標
    public float y;
    public float radius;//小球的半徑
    public float diameter;
    public boolean isHit;//是否被選中的控件
    public boolean Draw = false;
    public boolean DrawAgain = false;

    public Bean(int id, float x, float y, float radius, float diameter){
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.diameter = diameter;

    }

    public boolean of(float x, float y){
        final float dx = this.x - x;
        final float dy = this.y - y;
        //傳回的平方根是否小於半徑
        return Math.sqrt(dx * dx + dy * dy) <= this.radius/1.5;
    }
    public boolean ofnoIgnore(float x, float y){
        final float dx = this.x - x;
        final float dy = this.y - y;
        //傳回的平方根是否小於半徑
        return Math.sqrt(dx * dx + dy * dy) <= this.radius*1.5;
    }
}


