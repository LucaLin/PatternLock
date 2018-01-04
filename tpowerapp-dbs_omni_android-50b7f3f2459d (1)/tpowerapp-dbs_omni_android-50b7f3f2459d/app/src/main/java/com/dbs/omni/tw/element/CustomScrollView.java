package com.dbs.omni.tw.element;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by siang on 2017/4/21.
 */

public class CustomScrollView extends ScrollView {

//    private int maxHeight;
//    private final int defaultHeight = 200;

    private OnScrollToBottomListener mOnScrollToBottomListener;
    public interface OnScrollToBottomListener {
        void onScrollToBottom();
        void onNotScrollToBottom();
    }

    public void setScrollToBottomListener(OnScrollToBottomListener listener) {
        this.mOnScrollToBottomListener = listener;
    }


    public CustomScrollView(Context context) {
        super(context);
    }
//
    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        if (!isInEditMode()) {
//            init(context, attrs);
//        }
    }
//
//    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        if (!isInEditMode()) {
//            init(context, attrs);
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        if (!isInEditMode()) {
//            init(context, attrs);
//        }
//    }

//    private void init(Context context, AttributeSet attrs) {
//        if (attrs != null) {
//            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
//            //200 is a defualt value
//            maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight, defaultHeight);
//
//            styledAttrs.recycle();
//        }
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if(t + getHeight() >= getChildAt(0).getMeasuredHeight()){
            // ScrollView滑動到底部
            if(mOnScrollToBottomListener != null) {
                mOnScrollToBottomListener.onScrollToBottom();
            }

        } else {
            if(mOnScrollToBottomListener != null) {
                mOnScrollToBottomListener.onNotScrollToBottom(); }
        }
    }
}