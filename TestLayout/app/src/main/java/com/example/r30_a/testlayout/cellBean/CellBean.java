package com.example.r30_a.testlayout.cellBean;

/**
 * Created by R30-A on 2017/12/12.
 */
//小球的各項屬性設置地
public class CellBean {

    public int id;//小球的編號
    public float x;//小球的x與y坐標
    public float y;
    public float radius;//小球的半徑
    public float diameter;
    public float limitX;
    public boolean isHit;//是否被選中的控件
    public boolean DrawAgain = false;
    public boolean Draw = false;
    public int count;

    public CellBean( float x, float y, float radius){
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public CellBean(int id, float x, float y, float radius, float limitX, float diameter){
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.limitX = limitX;
        this.diameter = diameter;

    }

    public boolean of(float x, float y){
        final float dx = this.x - x;
        final float dy = this.y - y;
        //傳回的平方根是否小於半徑
        return Math.sqrt(dx * dx + dy * dy) <= this.radius/5;
    }
}
