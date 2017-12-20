package com.example.r30_a.testlayout.cellBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by R30-A on 2017/12/12.
 */
//製作圓球的地方
public class CellBeanFactory {
    private int width;//球盤寬
    private int height;//球盤長
    private List<CellBean> cellBeanList;//控制小球數量的list

    //傳入建構式時即設定球盤的大小並建立小球數量
    public CellBeanFactory(int width, int height){
        this.width = width;
        this.height = height;
        this.cellBeanList = new ArrayList<>();
       // this.createBean();
    }
    private List<CellBean> createBean(){
        //藉由切割球盤來控制球與球之間的間隔，view的1/8為單位
        final float pwidth = this.width / 8f;
        final float pHeight = this.height / 8f;

        //建立小球陣列
        for (int i =0; i < 3; i++){//每列3個
            for(int j = 0; j< 3; j++){//每行3個
                this.cellBeanList.add(new CellBean(
                        //這裡的3是控制每個球的距離
                        (i * 3 + j),
                        (j * 3 + 1)* pwidth,
                        (i * 3 + 1) * pHeight,
                        pwidth//小球的半徑等於view的1/8寬
                ));
            }
        }
        return this.cellBeanList;
    }
    private List<CellBean> create44Bean(){
        //藉由切割球盤來控制球與球之間的間隔，view的1/11為單位
        final float pwidth = this.width / 11f;
        final float pHeight = this.height / 11f;

        //建立小球陣列
        for (int i =0; i < 4; i++){//每列4個
            for(int j = 0; j< 4; j++){//每行4個
                this.cellBeanList.add(new CellBean(
                        //這裡的3是控制每個球的距離
                        (i * 4 + j),
                        (j * 3 + 1)* pwidth,
                        (i * 3 + 1) * pHeight,
                        pwidth//小球的半徑等於view的1/11寬
                ));
            }
        }
        return this.cellBeanList;
    }
   // public List<CellBean> getCellBeanList(){return cellBeanList;}
    public List<CellBean> getCellBeanList(){return createBean();}
    public List<CellBean> getCellBeanListfor44(){return create44Bean();}

}
