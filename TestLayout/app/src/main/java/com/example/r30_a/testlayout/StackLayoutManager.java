package com.example.r30_a.testlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by R30-A on 2017/12/7.
 * 可以滑動的圖片layout
 */

public class StackLayoutManager extends RecyclerView.LayoutManager{

    //每個圖片的間隔
    int space = 60;
    //offset unit, 一個子item的寬跟一個space的總和
    int unit;
    //
    int totalOffset;
    ObjectAnimator animator;
    private int animateValue;
    private int duration = 300;
    private RecyclerView.Recycler recycler;
    private int lastAnimateValue;
    private int maxStackCount = 4;//最多可堆疊的物品數量
    private int initialStackCount = 4;//一開始堆疊的層數
    private float secondaryScale = 0.8f;//沒有進入stack的item默認大小
    private float scaleRatio = 0.4f;
    private int initialOffset;
    private boolean initial;
    private int minVelociryX;
    //偵測速度
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private int pointerId;



    public StackLayoutManager(){}

    public StackLayoutManager(Config config){
        this.maxStackCount = config.maxStackCount;
        this.space = config.space;
        this.initialStackCount = config.initialStackCount;
        this.secondaryScale = config.secondaryScale;
        this.scaleRatio = config.scaleRatio;
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.recycler = recycler;
        //抽離並抓取附著著的view
        detachAndScrapAttachedViews(recycler);
        //取得子集的基準點，須假設每個item的尺寸一樣
        View anchorView = recycler.getViewForPosition(0);
        measureChild(anchorView, 0, 0);;
        unit = anchorView.getMeasuredWidth() + space;
        //因為會呼叫兩次此方法，所以要有一個offset緩衝，一個unit為單位
        initialOffset = initialStackCount * unit;
        minVelociryX = ViewConfiguration.get(anchorView.getContext()).getScaledMinimumFlingVelocity();
        fill(recycler, 0);

    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if(!initial){
            fill(recycler, initialOffset);
            initial =true;
        }
    }
    //擺放item的地方
    private int fill(RecyclerView.Recycler recycler, int dy){
        if(totalOffset + dy <0 || ((totalOffset + dy + 0f) / unit) > getItemCount() - 1)
            return 0;
//---------------------------------------------
        detachAndScrapAttachedViews(recycler);
        totalOffset += dy;//y軸的距離量
        int count = getChildCount();//子view的數量
        //先一一回收view
        for (int i=0 ; i < count; i++){
            View child = getChildAt(i);//一一取得所有view的位子
            if(child != null && shouldRecycle(child, dy))
                removeAndRecycleView(child, recycler);
        }

        int currentPos = totalOffset / unit;
        float n = (totalOffset + 0f) / unit;
        float x = n % 1f;
        int start = currentPos - maxStackCount >=0? currentPos - maxStackCount: 0;
        int end = currentPos + maxStackCount > getItemCount()? getItemCount(): currentPos + maxStackCount;

        //layout view一個個代入對應的參數
        for (int i = start; i < end;i++){
            //取得recycler的每一個view
            View view = recycler.getViewForPosition(i);

            float scale = scale(i);
            float alpha = alpha(i);


            addView(view);
            measureChild(view,0,0);

            int left = (int)(left(i) - (1- scale) * view.getMeasuredWidth() / 2);
            //設置每個view的margins， 調入取好的左邊界，右邊界為左邊界+view的寬
            layoutDecoratedWithMargins(view, left, 0, left + view.getMeasuredWidth(), view.getMeasuredHeight());
            //調入設定好的透明度，縮放方式為x軸跟y軸縮同樣的尺寸
            view.setAlpha(alpha);
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
        return dy;
    }
    //取得view的寬高
    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        detachAndScrapAttachedViews(recycler);
        //從基準點開始取view
        View anchorView = recycler.getViewForPosition(0);
        measureChild(anchorView, 0, 0);
        //取得view的高
        int height = anchorView.getMeasuredHeight();
        super.onMeasure(recycler, state, widthSpec, MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
    }

    //




    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);

        //檢查當手指移開時，item會自動到設定的位置而不是停下
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                velocityTracker.addMovement(event);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //手指觸碰時
                    if(animator != null && animator.isRunning())
                        animator.cancel();
                    pointerId = event.getPointerId(0);
                }

                if(event.getAction() == MotionEvent.ACTION_UP){
                    //手指離開時
                    velocityTracker.computeCurrentVelocity(1000,14000);
                    float xVelocity = VelocityTrackerCompat.getXVelocity(velocityTracker, pointerId);

                    int o = totalOffset % unit;
                    int scrollx;
                    //Math.abs取絕對值
                    if(Math.abs(xVelocity) < minVelociryX)
                        if (o != 0) {
                            if(o >= unit /2)
                                scrollx = unit -o;
                            else scrollx = -o;
                            int dur = (int)(Math.abs((scrollx + 0f) / unit) * duration);
                            brewAndStartAnimator(dur, scrollx);
                        }
                }
                return false;
            }
        });

        view.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                int o = totalOffset % unit;
                int s = unit -o;
                int scrollx;
                if(velocityX > 0){
                    scrollx = s;
                }else
                    scrollx = -o;
                int dur = computeSettleDuration(Math.abs(scrollx), Math.abs(velocityX));
                brewAndStartAnimator(dur, scrollx);
                return true;
            }
        });
    }
    int computeSettleDuration(int distance, float xvel){
        float sWeight = 0.5f * distance / unit;
        float velWeight = 0.5f * minVelociryX / xvel;

        return (int)((sWeight + velWeight) * duration);
    }


    private void brewAndStartAnimator(int dur, int finalx){
        animator = ObjectAnimator.ofInt(StackLayoutManager.this,"animateValue", 0,finalx);
        animator.setDuration(dur);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                lastAnimateValue = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lastAnimateValue = 0;
            }
        });
    }
    //取得左邊距
    public int left(int position){

        int left;
        //基準位置上顯示的是哪一個item
        int currentPos = totalOffset / unit;
        //此item離基準位置的百分比，+0.0f是為了呈現百分比
        float n = totalOffset + .0f / unit;
        float x = n - currentPos;

        if(position <= currentPos){

            if(position == currentPos){
                left = (int)(space * (maxStackCount - x));
            }else {
                left = (int)(space * (maxStackCount - x - (currentPos-position)));
            }
        }else {
            if(position == currentPos +2){
                float prevItemScale = scale(position-1);
                left = (int)(space * maxStackCount +position * unit - totalOffset - (1-prevItemScale)*(unit - space));
            }else {
            left = space * maxStackCount + position * unit - totalOffset;
            }
            left = left <=0? 0 : left;
        }
        return left;

    }

    //計算透明度，介於0~1之間
    public float alpha(int position){
        float alpha;
        int currentPos = totalOffset/ unit;
        float n = (totalOffset+ .0f) /unit;
        if(position > currentPos){//傳入的pos在item的左邊
            alpha = 1.0f;
        }else {//愈往左邊的透明度就愈高
            float o = 1 - (n - position) / maxStackCount;
            alpha = o;
        }
        return alpha <= 0.001f ? 0 : alpha;
    }

    //計算縮放
    public float scale(int position){
        float scale;
        int currentPos = this.totalOffset / unit;
        float n = (totalOffset + .0f) / unit;
        float x = n - currentPos;

        if(position >= currentPos) {
            if (position == currentPos)
                scale = 1 - scaleRatio * x / maxStackCount;
             else if (position == currentPos + 1) {

                scale = secondaryScale + (x > 0.5f ? 1 - secondaryScale : 2 * (1 - secondaryScale) * x);
            } else {
                scale = secondaryScale;
            }
        }else{
                if (position < currentPos - maxStackCount) {
                    scale = 0f;
                } else {
                    scale = 1f - scaleRatio * (n - currentPos + currentPos - position) / maxStackCount;
                }
            }
            return scale;

        }

    public boolean shouldRecycle(View view/*int position*/, int dy) {
        return view.getLeft() - dy < 0 || view.getRight() - dy > getWidth();
    }




    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT
        ,RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    //接收水平滾動的事件
    @Override
    public boolean canScrollHorizontally() {
        return true;
    }
    //用來接收滾動的距離，並以此距離做計算和調整
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return fill(recycler, dx);
    }






    public void setAnimateValue(int animateValue){
        this.animateValue = animateValue;
        int dy = this.animateValue - lastAnimateValue;
        fill(recycler, dy);
        lastAnimateValue = animateValue;
    }

    public int getAnimateValue(){return  animateValue;}


}
