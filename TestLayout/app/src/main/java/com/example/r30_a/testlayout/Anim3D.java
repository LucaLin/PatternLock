package com.example.r30_a.testlayout;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
3d的翻轉效果，可藉由更改x、y屬性來設定要怎麼轉，及轉動的細節
 */

public class Anim3D extends Animation {

    public float CenterX = 0;
    public float CenterY = 0;
    public float x;
    public float y;
    public float depthZ;
    public boolean reverse;
    public float scale = 1;
    public float Xdegrees,Ydegrees;


    public boolean turn;
    //public Anim3D(boolean turn){this.turn = turn;}
    public Anim3D(Context context, float x, float y, float depthZ, float Xdegrees, float Ydegrees, boolean reverse){

        this.x = x;
        this.y = y;
        this.Xdegrees = Xdegrees;
        this.Ydegrees = Ydegrees;
        this.depthZ = depthZ;
        this.reverse = reverse;
        //取得手機像素密度
        scale = context.getResources().getDisplayMetrics().density;

    }

    public void setCenter(float x, float y){
        CenterX = x;
        CenterY = y;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        /*
        3D翻轉的原理在於，使用CAMERA物件，在保持不動的物體上四處移動鏡頭
        在CAMERA物件呈現出來的畫面，就會有立體的感覺，等於是從各個角度來
        觀看這個VIEW。
        在旋轉、平移等方法，實際上就是在改變一個Matrix(矩陣)物件，操作
        完畢之後得到Matrix，然後加到這個view上面就可以了！
         */
        Matrix matrix = t.getMatrix();//這裡的t指要旋轉的view，代給可以給camera看的matrix物件
        Camera camera = new Camera();
        camera.save();//使camera先回復原狀態

        if (!reverse) {
            //正轉
            camera.rotateY(interpolatedTime * Xdegrees);//水平轉
            camera.rotateX(interpolatedTime * Ydegrees);//垂直轉
            camera.translate(x, y, depthZ);
        } else {
            //反轉
            camera.rotateY(interpolatedTime * Xdegrees);//水平反轉
            camera.rotateX(interpolatedTime * Ydegrees);//垂直反轉
            camera.translate(x, y, depthZ * (1.0f * interpolatedTime));
        }
        camera.getMatrix(matrix);//把設定好的鏡頭放到指定物件上
        camera.restore();//回復camera狀態
        //修正像素，避免失真
        float[] values = new float[9];
        matrix.getValues(values);
        values[6] = values[6] / scale;
        values[7] = values[7] / scale;
        matrix.setValues(values);
        //加上這兩行取得旋轉的中心點，如不設置，就會從(0，0)開始，變成翻頁的效果
        matrix.preTranslate(-CenterX, (float) (CenterY * -0.2));
        matrix.postTranslate(CenterX, (float) (CenterY * 0.2));

    }


}
