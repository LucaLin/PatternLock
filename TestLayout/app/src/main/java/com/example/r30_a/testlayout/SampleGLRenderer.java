package com.example.r30_a.testlayout;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by R30-A on 2017/11/3.
 */

public class SampleGLRenderer implements GLSurfaceView.Renderer{

    triangleSample triangleSample;
    //Square square;

    private final float[] MVPMatrix = new float[16];
    //投影矩陣
    private final float[] ProjectionMatrix = new float[16];
    private final float[] ViewMatrix = new float[16];

    //控制旋轉
    private float[] RotationMatrix = new float[16];

    public float angle;

    public float getAngle(){return angle;}
    public void setAngle(float angle){this.angle = angle;}

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    GLES20.glClearColor(0.0f,0.0f,0.0f,0.0f);
    //設定背景邊框顏色

        triangleSample = new triangleSample();
        //square = new Square();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    GLES20.glViewport(0,0,width,height);

    float ratio = (float)width / height;

    //設定一個投影的變換矩陣
        Matrix.frustumM(ProjectionMatrix, 0, -ratio,ratio
                        , -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

    //重置背景顏色
   GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        float[] scratch = new float[16];
  // long time = SystemClock.uptimeMillis() % 4000L;
   //float angle = 0.090f * ((int)time);
  // Matrix.setRotateM(RotationMatrix, 0, angle, 0, 0, -1.0f);

   //設定相機的視角位置
       Matrix.setLookAtM(ViewMatrix, 0,0,0,
                        -3, 0f,0f,0f,0f,
                        1.0f, 0.0f);
        //計算視角的轉換
       Matrix.multiplyMM(MVPMatrix, 0, ProjectionMatrix,
                0, ViewMatrix, 0);
        //Matrix.multiplyMM(scratch, 0, MVPMatrix,
                //0, RotationMatrix, 0);


    triangleSample.draw(MVPMatrix);
    }


    //著色器使用前的編譯方法
    public static int loadShader(int type, String shaderCode){
        //vertexshader使用的type是GLES20.GL_VERTEX_SHADER
        //fragmentshader使用的是GLES20.GL_FRAGMENT_SHADER
        int shader = GLES20.glCreateShader(type);

        //加入來源code然後編譯
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
