package com.example.r30_a.testlayout;

import android.content.Context;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 製作3d物件，首先要宣告用來儲存頂點座標和顏色的變數，接下來是定義物件會用到的頂點座標
 * 在OPENGL中，所有的變數都用float來儲存，以節省記憶體
 * 再來是指定每一個頂點的顏色，然後建立需要用到的轉換矩陣
 * 接著指定投影範圍最遠方的底色(clipping wall)
 * 再來是眼睛觀看的位置 & 方向
 * 利用程式碼的變數來控制物體旋轉的角度
 * 最後是程式碼的核心：vertexShader & fragmentShader
 */

public class sampleGL extends GLSurfaceView implements GLSurfaceView.Renderer{
    public sampleGL(Context context){super(context);}

    final static int bytePerfloat = 4,
                    sizeVertexLocation = 3,//每個頂點座標的資料量
                    sizeVertexColor = 4;//每個頂點顏色的資料量
    //OPENGL專用的BUFFER,用來儲存物件的頂點座標和顏色
    FloatBuffer LocationBuf, ColorBuf;

    //畫出3d物件的xyz座標，三個列是每一個頂點的xyz
    float[] Location3Dxyz = {-0.5f,-0.5f,0.0f,
                             0.5f,-0.5f,0.0f,
                             0.5f,0.5f,0.0f
                            };
    //3d物件的頂點顏色，顏色值的順序為rgba(alpha)
    float[] Color3Dxyz = {1.0f,1.0f,0.0f,1.0f,
                          1.0f,0.0f,1.0f,1.0f,
                          0.0f,1.0f,1.0f,1.0f
                           };
//－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
    //視線專用的矩陣
    float[] ViewMatrix = new float[16];

    //空間的座標短陣
    float[] ProjectionMatrix = new float[16];

    //3D物件用的座標矩陣
    float[] Model3DMatrix = new float[16];

    //儲存以上三種矩陣結合後的結果
    float[] CobineMatrix = new float[16];
//－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－

    //最遠處的底色
    float colorR=1.0f,colorG=1.0f,colorB=1.0f,colorA=1.0f;

    //眼睛的位置
    float eyeX=0.5f, eyeY=0.5f, eyeZ=1.5f;

    //眼睛看的平面位置，z值負數表前方
    float lookX=0.0f,lookY=0.0f,lookZ=-1.0f;

    //頭頂的方向，y軸正值表正上方
    float headUpX=0.0f, headUpY=1.0f,headUpZ=5.0f;

    //物體一開始的角度
    float rotateAngle = 0f;

    //資料傳給openGL需要用到的變數，用這些變數傳入資料
    int MatrixData, PositionData, ColorData;

//－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－

//繪製openGL，3D物體的vertex shader程式碼(著色器)

    final String vertexShader =
                   "uniform mat4 u_MVPMatrix;  \n" +//使用model,view,projection的matrix
                    "attribute vec4 a_Position; \n" +//設定頂點位置
                    "attribute vec4 a_Color; \n" + //設定頂點顏色
                   "varying vec4 v_Color; \n" +//傳給fragment shader用的

                    "void main()\n" + "{ " +
                           // "gl_Position = a_Potition;  \n" +//vertexshader主程式
                          "v_Color = a_Color;  \n" +//把設定的頂點顏色代給fragment shader
                           "gl_Position = u_MVPMatrix * a_Position;\n" +//用gl專門儲存頂點位置
                            "}";
//繪製OPENGL的3d物體的fragment shader程式碼
    final String fragmentShader = "precision mediump float;   \n" +//設定計算好的準確度
                                  "varying vec4 v_Color;     \n" +//接收vertexshader的顏色
                                  "void main()  {    \n" +//fragment shader主程式
                                    "gl_FragColor = v_Color; }    ";//用來儲存顏色

//－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
/*
setup方法是繪圖前的初始設定，這包括設定頂點(陣列)和頂點顏色(陣列)。且必須按照openGL的規定
使用BYTE來表示
另外還要載入vertexShader & fragmentShader，並建立openGL的GPU程式，它在傳送資料時用的屬性
 */
    public void setup(){
        //建立openGL的buffer
        LocationBuf = ByteBuffer.allocateDirect(Location3Dxyz.length * bytePerfloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        //建立openGL的buffer
        ColorBuf = ByteBuffer.allocateDirect(Color3Dxyz.length * bytePerfloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        //把3d物體的頂點座標 & 顏色，放到openGL的buffer
        LocationBuf.put(Location3Dxyz).position(0);
        ColorBuf.put(Color3Dxyz).position(0);

        //設定3d場景的background
        GLES20.glClearColor(colorR,colorG,colorB,colorA);
        android.opengl.Matrix.setLookAtM(ViewMatrix, 0, eyeX, eyeY, eyeZ,
                                                        lookX,lookY,lookZ,
                                                        headUpX,headUpY,headUpZ);

        //建立並載入vertexshader & fragmentshader
        int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER,vertexShader);
        int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER,fragmentShader);

        //建立並使用vertexshader & fragmentshader的openGL GPU程式
        int programHandle = GLES20.glCreateProgram();

            if(programHandle != 0){
                //連結vertexshader
                GLES20.glAttachShader(programHandle, vertexShaderHandle);
                //連結fragmentshader
                GLES20.glAttachShader(programHandle,fragmentShaderHandle);

                //連結vertexshader定義的屬性
                GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
                GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

                //載入程式
                GLES20.glLinkProgram(programHandle);

                //取得載入結果
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS,linkStatus, 0 );

                //如果失敗就刪除
                if(linkStatus[0]==0){
                    GLES20.glDeleteProgram(programHandle);
                    programHandle = 0;
                }

            }


            if(programHandle == 0 ){
                throw new RuntimeException("Error creating program");
            }


            //建立和openGL GPU程式傳送資料時要用的屬性，使用這些屬性來傳入資料
        MatrixData = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        PositionData = GLES20.glGetAttribLocation(programHandle, "a_Position");
        ColorData = GLES20.glGetAttribLocation(programHandle, "a_Color");

        //設定openGL並使用我們建立的GPU程式
        GLES20.glUseProgram(programHandle);
    }

/*
建立3d場景時會先執行的方法，可使用建立好的setup方法完成3d場景的建立
執行完後再呼叫下面的surfacechanged，傳入螢幕的寬高
再根據螢幕寬高設定好投影的參數，建立好投影的矩陣
 */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        setup();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        //設定視投影參數

        //設定最近和最遠的可視範圍和左右視角
        float nearest = .01f;
        float farest = 100f;
        float angle = 45f;
        //設定上下可視範圍，由左右視角和螢幕的寬高比來算
        float viewWidth = nearest * (float)Math.tan(Math.toRadians(angle)/2);
        float aspectRatio = (float)width / (float)height;
        float left = -viewWidth; float right = viewWidth;
        float bottom = -viewWidth / aspectRatio;
        float top = viewWidth / aspectRatio;

        //設定投影的矩陣,利用上下左右的視角來設定

        android.opengl.Matrix.frustumM(ProjectionMatrix, 0,
                                        left, right, bottom, top, nearest, farest);

    }
/*
此方法會自動執行，主要工作就是建立3D物件，
首先先清除畫面和Z-buffer
然後建立要使用的轉換矩陣，再把物體的頂點"座標" & "顏色"，利用傳送資料用的變數傳給「著色器」GLES20
最後執行繪圖
 */
    @Override
    public void onDrawFrame(GL10 gl) {
        //清除場景填上background，並清除z-buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BITS);

        //初始化3d物件的轉換矩陣
        android.opengl.Matrix.setIdentityM(Model3DMatrix,0);

        //3d物件沿指定的軸轉動
        android.opengl.Matrix.rotateM(Model3DMatrix, 0, rotateAngle, 0.0f, 0.0f, 1.0f);
        rotateAngle += 1f;//控制轉動的速度

        //將3d物件的頂點座標，利用openGL屬性傳給openGL程式
        LocationBuf.position(0);
        GLES20.glVertexAttribPointer(PositionData, sizeVertexLocation, GLES20.GL_FLOAT, false,3*bytePerfloat,LocationBuf);
        GLES20.glEnableVertexAttribArray(PositionData);

        //將3d物件的頂點顏色，利用openGL屬性傳給openGL程式
        ColorBuf.position(0);
        GLES20.glVertexAttribPointer(ColorData, sizeVertexColor, GLES20.GL_FLOAT, false, 4*bytePerfloat, ColorBuf);
        GLES20.glEnableVertexAttribArray(ColorData);

        //結合model, view, projection矩陣，得到matrix的mvp矩陣
        android.opengl.Matrix.multiplyMM(CobineMatrix, 0, ViewMatrix, 0, Model3DMatrix, 0);
        android.opengl.Matrix.multiplyMM(CobineMatrix, 0, ProjectionMatrix, 0, CobineMatrix, 0);

        //套用上面的mvp矩陣後開始繪圖
        GLES20.glUniformMatrix4fv(MatrixData, 1, false,CobineMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);


    }




    //載入vertexshader & fragmentshader的方法
    public int compileShader(int shaderType, String shaderSource){
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if(shaderHandle != 0 ){
            //載入並編譯shader
            GLES20.glShaderSource(shaderHandle,shaderSource);
            GLES20.glCompileShader(shaderHandle);

            //取得編譯結果
            int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle,GLES20.GL_COMPILE_STATUS,compileStatus,0);

            //如果編譯失敗就刪掉shader
            if(compileStatus[0]==0){
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle=0;
            }

        }
        if(shaderHandle == 0){
            throw new RuntimeException("Error creating shader");
        }

        return shaderHandle;
    }
}
