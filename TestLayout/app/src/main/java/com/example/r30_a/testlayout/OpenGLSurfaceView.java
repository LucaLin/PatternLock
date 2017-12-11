package com.example.r30_a.testlayout;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by R30-A on 2017/11/3.
 */

public class OpenGLSurfaceView extends GLSurfaceView {

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    SampleGLRenderer renderer;

    public OpenGLSurfaceView(Context context){super(context);

    //建立openGL的ES2.0版本
    setEGLContextClientVersion(2);

    renderer= new SampleGLRenderer();
    //設定繪圖要用的renderer
    setRenderer(renderer);

    //只有在數據改變時才繪制view，可防止動作重覆
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();float y = e.getY();

        switch (e.getAction()){
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                if(y> getHeight() / 2){
                    dx = dx* -1;
                }

                if(x< getWidth() / 2){
                    dy = dy * -1;
                }


                renderer.setAngle(renderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));

                requestRender();


        }
        previousX = x;
        previousY = y;


        return true;
    }

}
