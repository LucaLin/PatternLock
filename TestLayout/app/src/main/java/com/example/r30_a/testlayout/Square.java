package com.example.r30_a.testlayout;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by R30-A on 2017/11/3.
 */

public class Square {

    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;

    static final int CoordsPerVertex = 3;
    //矩陣的坐標
    static float squareCoords[] = {
            -0,5f, 0.5f, 0.0f,//左上
            -0.5f, -0.5f, 0.0f,//左下
            0.5f, -0.5f, 0.0f,//右下
            0.5f, 0.5f, 0.0f};//右上
    //繪制坐標的順序，避免重
    public short drawOrder[] = {0,1,2,0,2,3};


    //建構式
    public Square(){
        //使用坐標實體化一個物件的形狀
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(squareCoords.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        //使用drawlist
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(drawOrder.length*2);
        byteBuffer.order(ByteOrder.nativeOrder());
        drawListBuffer = byteBuffer.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);


    }


    }





