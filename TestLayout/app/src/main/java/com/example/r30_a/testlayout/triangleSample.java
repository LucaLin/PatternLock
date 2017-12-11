package com.example.r30_a.testlayout;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.example.r30_a.testlayout.SampleGLRenderer.loadShader;

/**
 * Created by R30-A on 2017/11/3.
 */

public class triangleSample {
    //用來儲存物件屬性的buffer
    public FloatBuffer vertexBuffer;

    public static int program;
    public int PositionHandle;
    public int ColorHandle;


    //頂點著色器(vertex shader)
    private final String vertexShaderCode =
           // "attribute vec4 vPosition;  "+
               //     "void main() { "+
                //    "  gl_Position = vPosition; }";
            //設加martix變量的著色器
            "uniform mat4 uMVPMatrix;"+
            "attribute vec4 vPosition;"+
            "void main(){"+
                    " gl_Position = MVPMatrix * vPosition; }";

    private int MVPMatrixHandle;
    //片段著色器(Fragment Shader)
    private final String fragmentShaderCode =
            "precision mediump float;   "+
                    " uniform vec4 vColor; "+
                    " void main(){ "+
                    " gl_FragColor = vColor; } ";




    //每個陣列的坐標數
    static final int CoordsPerVertex = 3;
    //建立坐標
    float triangleCoords[] = {0.0f,0.7f,0.0f,//從中心開始算第一個頂點的位置
                              -0.7f,0.0f,0.0f,
                              0.7f,0.0f,0.0f};

    //設定坐標的顏色，順序是rgba
    float color[] = {0.0f,1.0f,0.0f,1.0f};

    //建構式, 參數加入使接收組合後的變換矩陣，並應用到圖形上
    public triangleSample(){

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);
        //建立openGL Program
        program = GLES20.glCreateProgram();

        //增加著色器到program
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragmentShader);

        //GLES與program進行連接
        GLES20.glLinkProgram(program);

    //建立buffer來前置坐標與顏色屬性，建立物件時即可套用
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        //建立浮點緩沖區
        vertexBuffer = byteBuffer.asFloatBuffer();
        //把坐標加到floatbuffer
        vertexBuffer.put(triangleCoords);
        //設置buffer要從第一個坐標開始讀
        vertexBuffer.position(0);

        //將編譯的著色器加至openGL ES Program，進行連接，在建構式內做
        //將來呼叫圖形就會自動連接著色器
    }

    //建立一個draw方法來處理圖形的繪製，將著色器設定位置和顏色，然後執行函數
    private final int vertexCount = triangleCoords.length/CoordsPerVertex;
    private final int vertexStride = CoordsPerVertex *4;

    public void draw(float[] mvpMatrix){
        //先建立一個openGL的ES環境
        GLES20.glUseProgram(program);

        //取得頂點著色器的頂點位置給program
        PositionHandle = GLES20.glGetAttribLocation(program,"vPosition");

        //取得三角形物件的頂點屬性
        GLES20.glEnableVertexAttribArray(PositionHandle);

        //前置三角物件的互動資料
        GLES20.glVertexAttribPointer(PositionHandle, CoordsPerVertex,
                                    GLES20.GL_FLOAT, false,
                                    vertexStride, vertexBuffer);
//----------------------------------------------------------
        //取得著色器的顏色位置給program
       ColorHandle = GLES20.glGetUniformLocation(program,"vColor");
        MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        //設定三角形物件的顏色
       GLES20.glUniform4f(ColorHandle, 0.0f, 1.0f,0.0f,1.0f);
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glVertexAttribPointer(PositionHandle,3, GLES20.GL_FLOAT,false,0,vertexBuffer);
        //畫出三角物形件
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        //Disable vertex array
       GLES20.glDisableVertexAttribArray(PositionHandle);



    }



}
