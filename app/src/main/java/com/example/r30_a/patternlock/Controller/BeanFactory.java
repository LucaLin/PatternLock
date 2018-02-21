package com.example.r30_a.patternlock.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca on 2018/2/14.
 */

public class BeanFactory {
    private int width;//球盤寬
    private int height;//球盤長
    private List<Bean> BeanList;//控制小球數量的list

    //傳入建構式時即設定球盤的大小並建立小球數量
    public BeanFactory(int width, int height){
        this.width = width;
        this.height = height;
        this.BeanList = new ArrayList<>();

    }
    //創建3*3的圖形
    private List<Bean> createBeanfor33(){
        //藉由切割球盤來控制球與球之間的間隔，view的1/8為單位
        final float pwidth = this.width / 8f;
        final float pHeight = this.height / 8f;
        final float diameter = pwidth *2;
        //建立小球陣列
        for (int i =0; i < 3; i++){//每列3個
            for(int j = 0; j< 3; j++){//每行3個
                this.BeanList.add(new Bean(
                        //這裡的3是控制每個球的距離
                        (i * 3 + j),
                        (j * 3 + 1)* pwidth,
                        (i * 3 + 1) * pHeight,
                        pwidth//小球的半徑等於view的1/8寬
                        ,diameter));
            }
        }
        return this.BeanList;
    }
    //創建4*4的圖形
    private List<Bean> createBeanfor44(){
        //藉由切割球盤來控制球與球之間的間隔，view的1/11為單位
        final float pwidth = this.width / 11f;
        final float pHeight = this.height / 11f;
        final float diameter = pwidth *2;

        //建立小球陣列
        for (int i =0; i < 4; i++){//每列4個
            for(int j = 0; j< 4; j++){//每行4個
                this.BeanList.add(new Bean(
                        //這裡的3是控制每個球的距離
                        (i * 4 + j),
                        (j * 3 + 1)* pwidth,
                        (i * 3 + 1) * pHeight,
                        pwidth//小球的半徑等於view的1/11寬
                        ,diameter));
            }
        }
        return this.BeanList;
    }

    public List<Bean> getCellBeanList(){return createBeanfor33();}
    public List<Bean> getCellBeanListfor44(){return createBeanfor44();}

}
