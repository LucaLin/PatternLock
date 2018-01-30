package com.example.r30_a.testlayout.VideoView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by R30-A on 2018/1/30.
 */

public class FullScreenVideoView extends VideoView{
    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0,widthMeasureSpec),getDefaultSize(0,heightMeasureSpec));
    }
}
