package com.example.r30_a.testlayout.VideoView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by R30-A on 2018/1/30.
 */

public class ExtendedViewPager extends ViewPager{
    private boolean pagingEnabled = true;

    public ExtendedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        pagingEnabled = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!pagingEnabled)
            return false;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!pagingEnabled)
            return false;
        return super.onTouchEvent(ev);
    }

    public void setPagingEnabled(boolean enabled){
        pagingEnabled = enabled;
    }
}
