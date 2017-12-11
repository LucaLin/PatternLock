package com.example.r30_a.testlayout.explosion.explosionwidget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.example.r30_a.testlayout.explosion.uitls.Utils;

import java.util.ArrayList;

/**
 * Created by R30-A on 2017/12/5.
 */
//畫粒子用的畫布
public class ExplosionField extends View{

    private static final String TAG = "ExplosionField";
    private static final Canvas canvas = new Canvas();
    private ArrayList<ExplosionAnimatior> explosionAnimatiors;
    private View.OnClickListener onClickListener;

    private Bitmap createBitmapFromView(View view){
        //建立跟螢幕一樣寬高的空白圖，後者參數為圖片質量
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        if(bitmap != null){
            synchronized (canvas){
                //透過畫布把空白的圖設定進去
                canvas.setBitmap(bitmap);
                view.draw(canvas);
                canvas.setBitmap(null);//清除引用
            }
        }
        //如果view是imageview的話，就直接獲取drawable的圖
        if(view instanceof ImageView){
            Drawable drawable = ((ImageView)view).getDrawable();
            if(drawable != null && drawable instanceof BitmapDrawable){
                return ((BitmapDrawable)drawable).getBitmap();
            }
        }

        return bitmap;
    }
    public ExplosionField(Context context) {
        super(context);
        init();
    }

    public ExplosionField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        explosionAnimatiors = new ArrayList<ExplosionAnimatior>();

        attachtoActivity((Activity)getContext());

    }
    //在原先的activity上加上一層透明的view，這樣在顯示粒子動畫時就不會被其它的view蓋到
    private void attachtoActivity(Activity activity){
        //取得最上層的薄膜
        ViewGroup rootview = (ViewGroup)activity.findViewById(Window.ID_ANDROID_CONTENT);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        rootview.addView(this,lp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(ExplosionAnimatior animatior : explosionAnimatiors){
            animatior.draw(canvas);
        }
        //畫粒子的地方，for each 一個一個畫
    }

    public void explode(final View view){
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);//取得view相對於整個螢幕的坐標
        rect.offset(0, -Utils.dp2px(25));

        final ExplosionAnimatior animatior = new ExplosionAnimatior(this,createBitmapFromView(view), rect);
        explosionAnimatiors.add(animatior);

        animatior.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.animate().alpha(1f).setDuration(150).start();

                explosionAnimatiors.remove(animation);
                animation = null;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.animate().alpha(0f).setDuration(150).start();
            }
        });
        animatior.start();
    }



    //調用此方法，有view想要爆炸效果的話就使用它
    public void addListener(View view){
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup)view;
            int count = viewGroup.getChildCount();
            for(int i = 0; i< count; i++){
                addListener(viewGroup.getChildAt(i));
            }
        }else {
            //只有一個view的時候，不屬於group
            view.setClickable(true);
            view.setOnClickListener(getOnClickListener());
        }
    }



    private OnClickListener getOnClickListener(){
        if(null == onClickListener){
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExplosionField.this.explode(v);
                }
            };
        }
        return onClickListener;
    }
}


