package com.example.r30_a.testlayout.cellBean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.r30_a.testlayout.Config;
import com.example.r30_a.testlayout.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Luca on 2017/12/12.
 */
//開始畫出小球的畫盤
public class PatternLockView extends View{
    //線的屬性
    private int color;
    private int hitColor;
    private int errorColor;
    private int hitAgainColor;
    private int errorAgainColor;
    private int fillColor;
    private float lineWidth;
    int []  line1And2 = {0,1,4,5,8,9};
    int []  line1And2And3 = {1,2,3,5,6,7};
    int []  minusline1And2 = {2,3,6,7,11,15};
    int []  righttopNum = {4,5,6,8,9,10,12,13,14};
    int []  righttopNum2nd = {4,5,8,9,12,13};
    ArrayList<Integer> line12,line123,minusline12,ListrighttopNum,ListrighttopNum2nd;



    private ResultState resultState;
    CellBean drawBean;
    static CellBean B1,B2;
    CellBean B3;


    private List<CellBean> cellBeanList;
    private List<Integer> hitList, hitAgainList, OKlist;
    private List<Float> [][]processList;
    private int hitSize;

    private Paint paint;
    Set set, firstset,secondset ;
    float endX, endY;//小球連結的最後坐標位子
    public OnPatternChangeListener listener;

    public PatternLockView(Context context) {
        this(context,null);

    }
    public PatternLockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    public PatternLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs, defStyleAttr);
    }

    public void setOnPatternChangedListener(OnPatternChangeListener listener){
        this.listener = listener;
    }

    private void init(Context context, AttributeSet attr, int defStyleAttr){
        initattr(context,attr,defStyleAttr);
        initData();
    }
//設定各種狀態的顏色提示
    private void initattr(Context context, AttributeSet attr, int defStyleAttr){
        final TypedArray t = context.obtainStyledAttributes(attr, R.styleable.PatternLockerView,defStyleAttr,0);

        this.color = t.getColor(R.styleable.PatternLockerView_plv_color, Color.parseColor("#2196F3"));
        this.hitColor = t.getColor(R.styleable.PatternLockerView_plv_hitColor,Color.parseColor("#3F51B5"));
        this.errorColor = t.getColor(R.styleable.PatternLockerView_plv_errorColor,Color.parseColor("#F44336"));
        this.hitAgainColor = t.getColor(R.styleable.PatternLockerView_plv_hitAgainColor,Color.parseColor("#00ff00"));
        //this.errorAgainColor =t.getColor(R.styleable.PatternIndicatorView_piv_errorAgainColor,Color.parseColor("#F44336"));
        this.fillColor = t.getColor(R.styleable.PatternLockerView_plv_fillColor,Color.parseColor("#FAFAFA"));
        this.lineWidth = t.getDimension(R.styleable.PatternLockerView_plv_lineWidth,10);

        t.recycle();

        this.setColor(this.color);
        this.setHitColor(this.hitColor);
        this.setErrorColor(this.errorColor);
        this.setFillColor(this.fillColor);
        this.setLineWidth(this.lineWidth);

    }

    public void setColor(int color){
        this.color = color;
        postInvalidate();
    }
    public void setHitColor(int hitColor){
        this.hitColor = hitColor;
        postInvalidate();
    }
    public void setErrorColor(int errorColor){
        this.errorColor = errorColor;
        postInvalidate();
    }
    public void setFillColor(int fillColor){
        this.fillColor = fillColor;
        postInvalidate();
    }
    public void setLineWidth(float lineWidth){
        this.lineWidth = lineWidth;
        postInvalidate();
    }
//設置畫筆跟列表
    private void initData(){
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(8);

        this.hitList = new ArrayList<>();
        this.hitAgainList = new ArrayList<>();
        OKlist = new ArrayList<>();
        set = new LinkedHashSet();
        line12 = new ArrayList<>();
        line123 = new ArrayList<>();
        minusline12 = new ArrayList<>();
        ListrighttopNum = new ArrayList<>();
        ListrighttopNum2nd = new ArrayList<>();
        //set = new ArraySet();
        firstset = new TreeSet<Integer>();
        secondset = new TreeSet<Integer>();
        for(int i = 0; i< line1And2.length;i++){line12.add(line1And2[i]);}
        for(int i = 0; i< line1And2And3.length;i++){line123.add(line1And2And3[i]);}
        for(int i = 0; i< minusline1And2.length;i++){minusline12.add(minusline1And2[i]);}
        for(int i = 0; i< righttopNum.length;i++){ListrighttopNum.add(righttopNum[i]);}
        for(int i = 0; i< righttopNum2nd.length;i++){ListrighttopNum2nd.add(righttopNum2nd[i]);}
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //取得最低值
        int a = Math.min(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(a,a);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //第一次打開畫面時, 取得小球列表，根據建構式及setting頁面建立小球數
        if  ((this.cellBeanList == null) && CellSettingPageActivity.setCell == 2){
            this.cellBeanList = new CellBeanFactory(getWidth(),getHeight()).getCellBeanListfor44();

        }else if  ((this.cellBeanList == null) && CellSettingPageActivity.setCell == 1){
            this.cellBeanList = new CellBeanFactory(getWidth(),getHeight()).getCellBeanList();
        }
        //根據配置好的球數與球盤畫出圓圈
        if (!CellSettingPageActivity.ishide) {
            drawLine(canvas);

        }
        if(CellSettingPageActivity.isIgnore ){
            if(CellSettingPageActivity.isRepeat){
                drawCircles(canvas);
            }else {
                drawCircleNoIgnore(canvas);
            }
        }else {
            if(CellSettingPageActivity.isRepeat){
                drawCircles(canvas);
            }else {
                drawCircleNoIgnore(canvas);
            }
        }
    }
    private void drawLine(Canvas canvas){
        if(!this.hitList.isEmpty()){
        Path path = new Path();

        CellBean first = this.cellBeanList.get(this.hitList.get(0));
        path.moveTo(first.x, first.y);//從碰到的第一個球坐標開始
           // first.Draw= true;


        for(int i = 1; i< this.hitList.size(); i++){//不知道會碰到幾個，所以用for
            CellBean nextBean = this.cellBeanList.get(this.hitList.get(i));
            path.lineTo(nextBean.x, nextBean.y);

           // nextBean.Draw = true;
        }

        //設定線條結束的地方, 線條呈動態移動
        if((((this.endX != 0) || (this.endY !=0))/* && (endX - first.x <= first.radius*4)) && endX - first.x*/ )){
            path.lineTo(this.endX, this.endY);

        }
        this.paint.setColor(this.getColorByState(this.resultState));
        this.paint.setStyle(Paint.Style.STROKE);

        canvas.drawPath(path, this.paint);
        }
    }

private int getColorByState(ResultState state){
        return state == ResultState.OK ? this.hitColor : this.errorColor;
}
    private int getRepeatColorByState(ResultState state){
        return state == ResultState.OK ? this.hitAgainColor : this.errorColor;
    }

//畫出圓球的方法
    private void drawCircles(Canvas canvas) {
        this.paint.setStyle(Paint.Style.FILL);

        //根據球的數量一一畫出
        for (int i = 0; i < cellBeanList.size(); i++) {
            //將每個球依序傳給小球物件
            drawBean = cellBeanList.get(i);
            //外面的圈圈
            this.paint.setColor(this.color);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
            //上色
            this.paint.setColor(this.fillColor);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f-10 , paint);

            this.paint.setColor(this.color);
            canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
        }
        set = new TreeSet();

        //使用者畫線時開始動作
        if (hitList.size() != 0) {

            for (int j = 0; j < hitList.size() - 1; j++) {
                //使用set不可重複特性來判斷圓球的重複情形，使用draw & drawAgain兩布林變數來控制開關

                //強制加入碰到的第一顆球並上色
                if (j == 0) {
                    drawBean = cellBeanList.get(hitList.get(j));
                    drawBean.Draw = true;
                    set.add(hitList.get(j));
                    //處理碰到的下一個球，比較前後數字不一時 && 不是最後一顆球時
                }
                if(j ==1){
                    drawBean = cellBeanList.get(hitList.get(1));
                    drawBean.Draw = true;
                    set.add(hitList.get(1));
                }
                if (hitList.get(j) != hitList.get(j + 1) && hitList.size() - 1 != (j + 1) && j != 0) {

                    drawBean = cellBeanList.get(hitList.get(j + 1));
                    drawBean.Draw = true;
                    //檢查是否有重複的球跑進清單，有的話就要再畫一次，沒有的話就單純加入set內供下一次比較
                    if (set.contains(hitList.get(j + 1))) {
                        drawBean.DrawAgain = true;
                    } else {
                        set.add(hitList.get(j + 1));
                    }
                    //強制加入碰到的最後一顆球並上色
                } else if (hitList.size() - 1 == (j + 1)) {

                    drawBean = cellBeanList.get(hitList.get(j + 1));
                    drawBean.Draw = true;
                    // set.add(hitList.get(j+1));
                }

//-----------------------------------------------------------------------------
            //上色處理

            if (drawBean.Draw) {
                this.paint.setColor(this.getColorByState(this.resultState));
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
                //上色
                this.paint.setColor(this.fillColor);
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f - 10, paint);
                //中間的點

                this.paint.setColor(this.getColorByState(this.resultState));
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
            }

            if ((drawBean.Draw && drawBean.DrawAgain) && (CellSettingPageActivity.showrepeat)) {
                this.paint.setColor(this.getColorByState(this.resultState));
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
                //上色
                this.paint.setColor(this.fillColor);
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f - 10, paint);

                this.paint.setColor(this.getRepeatColorByState(this.resultState));
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
                }
            }
        }
}
        private void drawCircleNoIgnore(Canvas canvas){
            this.paint.setStyle(Paint.Style.FILL);

            //根據球的數量一一畫出
            for (int i = 0; i < cellBeanList.size(); i++) {
                //將每個球依序傳給小球物件
                drawBean = cellBeanList.get(i);
                //外面的圈圈
                this.paint.setColor(this.color);
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
                //上色
                this.paint.setColor(this.fillColor);
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f - 10, paint);

                this.paint.setColor(this.color);
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
            }

            for(int i = 0; i < hitList.size();i++){
                drawBean = cellBeanList.get(hitList.get(i));

                //  if (drawBean.Draw) {
                this.paint.setColor(this.getColorByState(this.resultState));
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f, paint);
                //上色
                this.paint.setColor(this.fillColor);
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius/1.3f - 10, paint);
                //中間的點

                this.paint.setColor(this.getColorByState(this.resultState));
                canvas.drawCircle(drawBean.x, drawBean.y, drawBean.radius / 7f, paint);
            }
        }

//處理各種觸碰狀況
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
            return super.onTouchEvent(event);
        }
        boolean isHandle = false;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                handleActionDown(event);
                isHandle = true;
                break;
            case MotionEvent.ACTION_MOVE:

                handleActionMove(event);
                isHandle =true;
                break;
            case MotionEvent.ACTION_UP:

                handleActionUp(event);
                isHandle = true;
                break;
            default:break;
        }


        postInvalidate();
        return isHandle ? true : super.onTouchEvent(event);
    }

    //處理按下去時要處理的動作
    private void handleActionDown(MotionEvent event){

        //1. 先重新配置畫盤
        clearHitData();
        //2. 更新按到的球有哪些

        if (CellSettingPageActivity.setCell==2){
            if(CellSettingPageActivity.isIgnore){
                if(CellSettingPageActivity.isRepeat){
                    updateDownState(event);
                }else {
                    updateHitState(event);
                }

            }else {
                if (CellSettingPageActivity.isRepeat){

                }else {
                    updateNoIgnoreStatefor44(event);
                }
            }
        }else if(CellSettingPageActivity.setCell==1){

         if(CellSettingPageActivity.isIgnore){
            if(CellSettingPageActivity.isRepeat){
                updateDownState(event);
            }else {
                updateHitState(event);
            }

        }else {
             if(CellSettingPageActivity.isRepeat){
                 updateNoIgnoreRepeatState(event);
             }else {
                updateNoIgnoreState(event);
             }
        }
        }
        //3. 設定監聽器
        if(this.listener !=null){
            this.listener.onStart(this);
      //      setHitlist(hitList);
        }

    }
    //處理移動時要處理的動作
    private void handleActionMove(MotionEvent event){

        //1. 更新點擊的狀況
        if (CellSettingPageActivity.setCell==2){
            if(CellSettingPageActivity.isIgnore){
                if(CellSettingPageActivity.isRepeat){
                    updateDownState(event);
                }else {
                    updateHitState(event);
                }

            }else {
                if (CellSettingPageActivity.isRepeat){

                }else {
                    updateNoIgnoreStatefor44(event);
                }
            }
            if(CellSettingPageActivity.isRepeat){
                updateDownStatefor44(event);
            }else {
                updateHitState(event);
            }
        }else if(CellSettingPageActivity.setCell==1){

            if(CellSettingPageActivity.isIgnore){
                if(CellSettingPageActivity.isRepeat){
                updateDownState(event);
            }else {
                updateHitState(event);
            }

        }else {
                if(CellSettingPageActivity.isRepeat){
                updateNoIgnoreRepeatState(event);
            }else {
                updateNoIgnoreState(event);
            }
        }
        }
        //2. 更新移動到的坐標位置
        this.endX = event.getX();
        this.endY = event.getY();
        //3. 通知監聽, 監聽改變中的hitlist(點中的球)

        final int size = this.hitList.size();
        if((this.listener != null) && (this.hitSize != size)){
            this.hitSize = size;
       //     setHitlist(hitList);

            this.listener.onChange(this, this.hitList);

        }
    }
    //處理手指離開時的動作
    private void handleActionUp(MotionEvent event){
       // getX(event);
        //1. 更新坐標
        if (CellSettingPageActivity.setCell==2){
            if(CellSettingPageActivity.isIgnore){

            }
            if(CellSettingPageActivity.isRepeat){
                updateDownStatefor44(event);
            }else {
                updateHitState(event);
            }
        }else if(CellSettingPageActivity.setCell==1){
        if(CellSettingPageActivity.isIgnore){
            //updateDownState(event);
            //updateHitState(event);
        }else {
            if(CellSettingPageActivity.isRepeat){
                updateNoIgnoreRepeatState(event);
            }else {
                updateNoIgnoreState(event);
            }
        }
        }
        Log.d("234","324");
        this.endX = 0;
        this.endY = 0;

        //2. 通知監聽
        if(this.listener!= null){

            setHitlist(hitList);
           /* if(CellSettingPageActivity.isIgnore){
                hitAgainList.remove(0);
            }*/

            this.listener.onComplete(this,this.hitAgainList);


        }
        //3. 有必要的話開始計時器
        if(this.hitList.size() > 0){
            set.clear();
            starTimer();
        }

    }

    //重新整理成正確的list
    public List<Integer> setHitlist(List<Integer> list) {
        for(int i = 0; i< list.size()-1;i++) {//index限制讀到倒數第二個

        if(i==0 && !hitAgainList.contains(list.get(0))){

            hitAgainList.add(list.get(i));
        }

            //第一次比對數字是否相同，不相同著加入前項到新list
        if((i+1) <= list.size()){

           if (i !=0 && list.get(i) != list.get(i + 1)) {
                hitAgainList.add(list.get(i));

           }
        }//第二次強迫加入最後點到的球的編號
            if(i+1 == list.size()-1){
                hitAgainList.add(list.get(i+1));
            }
        }
        return hitAgainList;
    }

    //設定放開手指後開始計時圖形保留時間
    private void starTimer(){
        setEnabled(false);
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                setEnabled(true);
                clearHitState();
            }
        },700);//0.7秒後回復
    }

    public void clearHitState(){
        clearHitData();
        if(this.listener != null){
            this.listener.onClear(this);
        }
        postInvalidate();
    }

    private void clearHitData(){//清除所有list
        this.resultState  =ResultState.OK;
        for(int i =0; i< this.hitList.size(); i++){
            this.cellBeanList.get(hitList.get(i)).isHit = false;
            this.cellBeanList.get(hitList.get(i)).Draw = false;
            this.cellBeanList.get(hitList.get(i)).DrawAgain = false;
        }
        this.hitAgainList.clear();
        this.hitList.clear();//set = new ArraySet();
        this.hitSize = 0;
        //hitSet = new ArraySet();
    }


    private List updateDownState(MotionEvent event){
        final float x = event.getX();
        final float y = event.getY();

        for (CellBean c : this.cellBeanList) {
            if(!c.isHit && c.of(x,y)){
            switch (c.id){
                case 0 :{hitList.add(0);continue;}
                case 1 :{hitList.add(1);continue;}
                case 2 :{hitList.add(2);continue;}
                case 3 :{hitList.add(3);continue;}
                case 4 :{hitList.add(4);continue;}
                case 5 :{hitList.add(5);continue;}
                case 6 :{hitList.add(6);continue;}
                case 7 :{hitList.add(7);continue;}
                case 8 :{hitList.add(8);continue;}
            }
            }
        }
        return hitList;
    }

    private List updateDownStatefor44(MotionEvent event){
        final float x = event.getX();
        final float y = event.getY();

        for (CellBean c : this.cellBeanList) {
            if(!c.isHit && c.of(x,y)){
                switch (c.id){
                    case 0 :{hitList.add(0);continue;}
                    case 1 :{hitList.add(1);continue;}
                    case 2 :{hitList.add(2);continue;}
                    case 3 :{hitList.add(3);continue;}
                    case 4 :{hitList.add(4);continue;}
                    case 5 :{hitList.add(5);continue;}
                    case 6 :{hitList.add(6);continue;}
                    case 7 :{hitList.add(7);continue;}
                    case 8 :{hitList.add(8);continue;}
                    case 9 :{hitList.add(9);continue;}
                    case 10 :{hitList.add(10);continue;}
                    case 11 :{hitList.add(11);continue;}
                    case 12 :{hitList.add(12);continue;}
                    case 13 :{hitList.add(13);continue;}
                    case 14 :{hitList.add(14);continue;}
                    case 15 :{hitList.add(15);continue;}
                }
            }
        }
        return hitList;
    }

    private List updateMoveState(MotionEvent event){
        final float x = event.getX();
        final float y = event.getY();

        for (CellBean c : this.cellBeanList) {
            if(!c.isHit && c.of(x,y)) {
                switch (c.id) {
                    case 0: {hitList.add(0);continue;}
                    case 1: {hitList.add(1);continue;}
                    case 2: {hitList.add(2);continue;}
                    case 3: {hitList.add(3);continue;}
                    case 4: {hitList.add(4);continue;}
                    case 5: {hitList.add(5);continue;}
                    case 6: {hitList.add(6);continue;}
                    case 7: {hitList.add(7);continue;}
                    case 8: {hitList.add(8);continue;}
                }
            }
        }


        return hitList;
    }


    private List updateHitState(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        for (CellBean c : this.cellBeanList) {
            if (!c.isHit && c.of(x, y) ) {
                c.isHit =true;
                hitList.add(c.id);
               // hitSet.add(c.id);

            }
              /*  for (CellBean cc : this.cellBeanList) {
                    if ((c.isHit && c.of(x, y))) {
                        //要限制只能做一次

                       hitList.add(c.id);
                    }
                }//for each end*/
            }

            //set1 set2 set1加不進去的加給set2，最後的hitlist放set1+set2
          return hitList;
}


    private List updateNoIgnoreState(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        for (CellBean c : this.cellBeanList) {
                if (!c.isHit && c.of(x, y)) {
                    c.isHit = true;
                    this.hitList.add(c.id);
                }

            if (hitList.size() > 0) {

                B1 = cellBeanList.get(hitList.get(0));
                B1.isHit = true;

                B2 = null; B3 = null;
                //我拉出來的線長
                final double Myline = Math.sqrt(((x - B1.x) * (x - B1.x)) + ((y - B1.y) * (y - B1.y)));
                //表定的線長
                final double RuleLine = Math.sqrt(((B1.diameter *1.6) * (B1.diameter * 1.6)) + (B1.diameter * B1.diameter));
                //final double RuleLine = B1.diameter*1.5;
                if (Myline >  0) {//一開始劃就動作
                    //B3 = cellBeanList.get(B1.id+1);//取相鄰的那顆
                    /*●●○
                    * ○●○*/
                    if (getdx(event, B1) > 0 && Math.abs(getdx(event, B1)) > B1.radius * 1.6 && Math.abs(getdx(event, B1)) < B1.radius * 2 && hitList.size() >= 2) {
                        if (getdy(event, B1) < 0 && Math.abs(getdy(event, B1)) < B1.radius / 2) {
                            getB2(1, true);
                        }
                    }/*○●●
                       ○●○*/ else if (getdx(event, B1) < 0 && Math.abs(getdx(event, B1)) > B1.radius * 1.6 && Math.abs(getdx(event, B1)) < B1.radius * 2 && hitList.size() >= 2) {
                        if (getdy(event, B1) < 0 && Math.abs(getdy(event, B1)) < B1.radius / 2) {
                            getB2(-1, true);
                        }
                    /*●○○
                      ●●○*/
                    } else if (getdy(event, B1) > 0 && Math.abs(getdy(event, B1)) > B1.radius * 1.6 && Math.abs(getdy(event, B1)) < B1.radius * 2 && hitList.size() >= 2) {
                        if (getdx(event, B1) < 0 && Math.abs(getdx(event, B1)) < B1.radius / 2) {
                            getB2(3, true);
                        }
                    }
                    /*●●○
                    * ●○○*/
                    else if (getdy(event, B1) < 0 && Math.abs(getdy(event, B1)) > B1.radius * 1.6 && Math.abs(getdy(event, B1)) < B1.radius * 2 && hitList.size() >= 2) {
                        if (getdx(event, B1) < 0 && Math.abs(getdx(event, B1)) < B1.radius / 2) {
                            getB2(-3, true);
                        }
                        //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                    } else if (Myline >= RuleLine) {//如果拉出來的線跟A比，B比較長的話
                        //getdx>0代表往右劃
                        if (isRow(event, B1) && getdx(event, B1) > 0) {

                            //往右劃連接隔壁的那一個
                            //●●○
                            //先給中間的球id，後面來看是否平行再決定要不要加
                            if (B1.id != cellBeanList.size() - 1) {
                                B3 = cellBeanList.get(B1.id + 1);
                            }
                            if (is2ndLine(event, B1) && inRowArea(event,B1)) {
                                if (!hitList.contains(B3.id)) {
                                    getB2(1, true);
                                }
                                //連最右邊時先看是否跟b1平行，是的話，先加中間的再加後面的
                                //●●●
                            } else if (is3rdLine(event, B1) && inRowArea(event,B1)) {
                                getB2(2, true);
                                if (hitList.size() == 2) {
                                    hitList.add(1, B1.id + 1);
                                }
                            }
                            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            else if (is2ndLine(event, B1)) {
                                /*●○○
                                * ○●○*/
                                if (getdy(event, B1) > B1.radius * 3 && Math.abs(getdy(event, B1)) < B1.radius * 4.5) {
                                    if(B1.id < 6){
                                    getB2(4, true);
                                    }
                                /*●○○
                                  ○○○
                                * ○●○*/
                                } else if (getdy(event, B1) > B1.diameter * 3) {
                                    getB2(7, true);
                                }

                            } else if (is3rdLine(event, B1)) {
                                 /*●○○
                                 * ○○●*/
                                if (is3rdLine(event, B1) && getdy(event, B1) > B1.radius * 2.5 && getdy(event, B1) < B1.radius * 4) {
                                    if(B1.id < 5){
                                    getB2(5, true);
                                    }

                                /*●○○
                                  ○○○
                                * ○○●*/
                                } else if (is3rdLine(event, B1) && getdy(event, B1) < getHeight() - B1.radius && getdy(event, B1) > B1.radius * 5.5) {
                                    if(B1.id == 0){
                                    getB2(8, true);
                                    }
                                }

                            }
                            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            //getdx<0代表往左劃
                        } else if (isRow(event, B1) && getdx(event, B1) < 0) {
                            if (is2ndLine(event, B1) && inRowArea(event,B1)) {
                                getB2(-1, true);

                            } else if ((Math.abs(getdx(event, B1)) > B1.radius * 5.5 && Math.abs(getdx(event, B1)) < getWidth()) && Math.abs(getdy(event, B1)) < B1.radius / 2 && B3 == null) {
                                B3 = cellBeanList.get(B1.id - 2);
                                if (B2 != null && B2.y == B1.y) {
                                    getB2(-1, true);
                                    getB2(-2, true);
                                } else {
                                    getB2(-2, true);
                                    if (hitList.size() == 2)
                                        hitList.add(1, B1.id - 1);
                                }
                                //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            } else if (is2ndLine(event, B1) && getdx(event, B1) < 0) {
                                    /*○○●
                                    * ○●○*/
                                if (is2ndLine(event, B1) && getdy(event, B1) > B1.radius * 3 && getdy(event, B1) < B1.radius * 4.5) {
                                    getB2(2, true);
                                    /*○○●
                                      ○○○
                                    * ○●○*/
                                } else if (is2ndLine(event, B1) && getdy(event, B1) > B1.diameter * 3) {
                                    getB2(5, true);
                                }
                            } else if (is3rdLine(event, B1)) {
                                    /*○○●
                                    * ●○○*/
                                if (getdy(event, B1) > B1.radius * 2.5 && getdy(event, B1) < B1.radius * 4) {
                                    if(B1.id !=8){
                                    getB2(1, true);
                                    }
                                    /*○○●
                                      ○○○
                                    * ●○○*/
                                } else if (getdy(event, B1) > B1.radius * 5 && getdy(event, B1) < getHeight() - B1.radius) {
                                    if (B1.id < 3) {
                                    getB2(4, true);
                                    }
                                }
                            }
                        }
                        //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                        //往下劃
                        if (isLine(event, B1) && getdy(event, B1) > 0) {
                            if (B1.id < 6) {
                                B3 = cellBeanList.get(B1.id + 3);
                            }

                                /*●○○
                                * ●○○*/
                            if (is2ndRow(event, B1)) {
                                if (!hitList.contains(B3.id)) {
                                    hitList.add(B3.id);
                                }

                                /*●○○
                                * ●○○
                                * ●○○*///先看有沒有平行，有的話順序是先加中間的球
                            } else if (is3rdRow(event, B1)) {
                                if (B1.id < 3) {
                                    getB2(6, true);
                                }
                                if (hitList.size() == 2) {
                                    hitList.add(1, B1.id + 3);
                                }

                            }
                            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            //往上劃
                        } else if (isLine(event, B1) && getdx(event, B1) > 0) {
                            if (B1.id > 2) {
                                B3 = cellBeanList.get(B1.id - 3);
                            }
                            // B3 = cellBeanList.get(B1.id-3);//有問題
                                /*●○○
                                * ●○○*/
                            if (is2ndRow(event, B1)) {
                                if (B3 != null && !hitList.contains(B3.id)) {
                                    hitList.add(B3.id);
                                }
                                /*●○○
                                * ●○○
                                * ●○○*///先看有沒有平行，有的話順序是先加中間的球
                            } else if (is3rdRow(event, B1)&& B1.id > 5) {
                                getB2(-6, true);
                                if (hitList.size() == 2) {
                                    hitList.add(1, B1.id - 3);
                                }

                                //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                                //往斜上劃
                                /*○●○
                                * ●○○*/
                            } else if (is2ndLine(event, B1) && Math.abs(getdy(event, B1)) > B1.radius * 3 && Math.abs(getdy(event, B1)) < B1.radius * 5) {
                                if(B1.id > 1){
                                getB2(-2, true);
                                }

                                    /*○●○
                                    * ○●○
                                    * ●○○*/

                            } else if (is2ndLine(event, B1) && Math.abs(getdy(event, B1)) > B1.diameter * 3 && Math.abs(getdy(event, B1)) < getHeight()) {
                                if(B1.id > 5)
                                getB2(-5, true);
                            }
                            //往上斜對角
                                /*○○●
                                * ●○○*/
                            else if (is3rdLine(event, B1) && Math.abs(getdy(event, B1)) > B1.radius * 2.5 && Math.abs(getdy(event, B1)) < B1.radius * 4) {
                                if(B1.id > 2){
                                getB2(-1, true);
                                }//往上斜對角再往上
                                /*○○●
                                * ○○●
                                * ●○○*/
                                else if (is3rdLine(event, B1) && Math.abs(getdy(event, B1)) > B1.radius * 5.5 && Math.abs(getdy(event, B1)) < getHeight()) {
                                    if(B1.id > 3){
                                        getB2(-4, true);
                                    }
                                }

                            }
                            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            //右往斜上劃
                        } else if (isLine(event, B1) && getdx(event, B1) < 0 && getdy(event, B1) < 0) {
                                /*○●○
                                * ○○●*/
                            if (is2ndLine(event, B1) && Math.abs(getdy(event, B1)) > B1.radius * 3 && Math.abs(getdy(event, B1)) < B1.radius * 5) {
                                if(B1.id > 3){
                                getB2(-4, true);
                                }
                                //再往上
                                    /*○●○
                                      ○●○
                                    * ○○●*/
                            } else if (is2ndLine(event, B1) && Math.abs(getdy(event, B1)) > B1.diameter * 3 && Math.abs(getdy(event, B1)) < getHeight()) {
                                if(B1.id > 6){
                                getB2(-7, true);
                                }
                            }
                            //斜對角
                                /*●○○
                                * ○○●*/
                            else if (is3rdLine(event, B1) && Math.abs(getdy(event, B1)) > B1.radius * 2.5 && Math.abs(getdy(event, B1)) < B1.radius * 4) {
                                if(B1.id > 4){
                                getB2(-5, true);
                                }
                                //再往上
                                /*●○○
                                * ●○○
                                * ○○●*/
                            } else if (is3rdLine(event, B1) && Math.abs(getdy(event, B1)) > B1.radius * 5.5 && Math.abs(getdy(event, B1)) < getHeight()) {
                                if(B1.id > 7){
                                getB2(-8, true);
                                }
                            }
                        }
                    }//myline > ruleline
                }//myline>0 end
        }//hitsize>0 end
        }//for each end
        return hitList;
    }

    private List updateNoIgnoreStatefor44(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        for (CellBean c : this.cellBeanList) {
            if (!c.isHit && c.of(x, y)) {
                c.isHit = true;
                this.hitList.add(c.id);
            }

            if (hitList.size() > 0) {

                B1 = cellBeanList.get(hitList.get(0));
                B1.isHit = true;

                B2 = null; B3 = null;
                //我拉出來的線長
                final double Myline = Math.sqrt(((x - B1.x) * (x - B1.x)) + ((y - B1.y) * (y - B1.y)));
                //表定的線長
                final double RuleLine = Math.sqrt(((B1.diameter *1.6) * (B1.diameter * 1.6)) + (B1.diameter * B1.diameter));
                //final double RuleLine = B1.diameter*1.5;
                if (Myline >  0) {//一開始劃就動作
                    //B3 = cellBeanList.get(B1.id+1);//取相鄰的那顆
                    /*●●○
                    * ○●○*/
                    if (getdx(event, B1) > 0 && Math.abs(getdx(event, B1)) > B1.radius * 1.6 && Math.abs(getdx(event, B1)) < B1.radius * 2 && hitList.size() >= 2) {
                        if (getdy(event, B1) < 0 && Math.abs(getdy(event, B1)) < B1.radius / 2) {
                            getB2(1, true);
                        }
                    }/*○●●
                       ○●○*/ else if (getdx(event, B1) < 0 && Math.abs(getdx(event, B1)) > B1.radius * 1.6 && Math.abs(getdx(event, B1)) < B1.radius * 2 && hitList.size() >= 2) {
                        if (getdy(event, B1) < 0 && Math.abs(getdy(event, B1)) < B1.radius / 2) {
                            getB2(-1, true);
                        }
                    /*●○○
                      ●●○*/
                    } else if (getdy(event, B1) > 0 && Math.abs(getdy(event, B1)) > B1.radius * 1.6 && Math.abs(getdy(event, B1)) < B1.radius * 2 && hitList.size() >= 2) {
                        if (getdx(event, B1) < 0 && Math.abs(getdx(event, B1)) < B1.radius / 2) {
                            getB2(4, true);
                        }
                    }
                    /*●●○
                    * ●○○*/
                    else if (getdy(event, B1) < 0 && Math.abs(getdy(event, B1)) > B1.radius * 1.6 && Math.abs(getdy(event, B1)) < B1.radius * 2 && hitList.size() >= 2) {
                        if (getdx(event, B1) < 0 && Math.abs(getdx(event, B1)) < B1.radius / 2) {
                            getB2(-3, true);
                        }
                        //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                    } else if (Myline >= RuleLine) {//如果拉出來的線跟A比，B比較長的話
                        //getdx>0代表往右劃
                        if (isRow(event, B1) && getdx(event, B1) > 0) {

                            //往右劃連接隔壁的那一個
                            //●●○
                            //先給中間的球id，後面來看是否平行再決定要不要加
                            if (B1.id != cellBeanList.size() - 1) {
                                B3 = cellBeanList.get(B1.id + 1);
                            }
                            if (is2ndLine(event, B1) && inRowArea(event,B1)) {
                                if (!hitList.contains(B3.id)) {
                                    getB2(1, true);
                                }
                                //連最右邊時先看是否跟b1平行，是的話，先加中間的再加後面的
                                //●●●
                            } else if (is3rdLine(event, B1) && inRowArea(event,B1)) {
                                getB2(2, true);
                                if (hitList.size() == 2) {
                                    hitList.add(1, B1.id + 1);
                                }
                                /*●●●●*/
                            } else if(is4thLine(event,B1) && inRowArea(event,B1)){
                                getB2(3,true);
                                if(hitList.size() == 2){
                                    hitList.add(1,B1.id+1);
                                    hitList.add(2,B1.id+2);
                                }
                            }
                            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            else if (is2ndLine(event, B1)) {
                                /*●○○○
                                * ○●○○*/
                                if (getdy(event, B1) > B1.radius * 3 && Math.abs(getdy(event, B1)) < B1.radius * 4.5) {
                                    if(B1.id < 11 && (B1.id !=3 && B1.id !=7)){
                                        getB2(5, true);
                                    }
                                /*●○○○
                                  ○○○○
                                * ○●○*/
                                } else if (getdy(event, B1) > B1.radius * 5.5 && getdy(event,B1) < B1.radius* 7.5) {
                                    if(B1.id < 7 &&  (B1.id !=3 && B1.id !=7))
                                    getB2(9, true);
                                    /*●○
                                    * ○○
                                    * ○○
                                    * ○●*/
                                } else if(getdy(event,B1) > B1.radius*8 && getdy(event,B1) < getHeight() - B1.radius){
                                    if(B1.id <3){
                                        getB2(13,true);
                                    }
                                }

                            } else if (is3rdLine(event, B1)) {
                                 /*●○○○
                                 * ○○●○*/
                                if (getdy(event, B1) > B1.radius * 2.5 && getdy(event, B1) < B1.radius * 4) {
                                    if(line12.contains(B1.id)){
                                        getB2(6, true);
                                    }

                                /*●○○○
                                  ○○○○
                                * ○○●○*/
                                } else if (getdy(event, B1) > B1.radius * 5.5 && getdy(event, B1) < B1.radius*7) {
                                    if(B1.id == 0 || B1.id ==1 || B1.id == 4 || B1.id == 5){
                                        getB2(10, true);
                                    }
                                /*●○○○
                                  ○○○○
                                  ○○○○
                                * ○○●○*/
                                }else if(getdy(event,B1) > B1.radius*8 && getdy(event,B1) < getHeight() - B1.radius){
                                    if(B1.id <2){
                                        getB2(14,true);
                                    }

                                }

                            }else if(is4thLine(event,B1)){
                                    /*●○○○
                                    * ○○○●*/
                                if(getdy(event, B1) > B1.radius * 2.5 && getdy(event, B1) < B1.radius * 4){
                                    if(B1.id == 0 ||B1.id == 4 || B1.id == 8){
                                        getB2(7,true);
                                    }
                                    /*●○○○
                                    * ○○○○
                                    * ○○○●*/
                                }else if(getdy(event, B1) > B1.radius * 5.5 && getdy(event, B1) < B1.radius*7.5){
                                    if(B1.id == 0 || B1.id == 4){
                                        getB2(11,true);
                                    }
                                    /*●○○○
                                    * ○○○○
                                    * ○○○○
                                    * ○○○●*/
                                }else if(getdy(event,B1) > B1.radius*8 && getdy(event,B1) < getHeight() - B1.radius){
                                    if(B1.id ==0){
                                        getB2(15,true);
                                    }
                                }

                            }
                            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            //getdx<0代表往左劃
                        } else if (isRow(event, B1) && getdx(event, B1) < 0) {
                            if (is2ndLine(event, B1) && inRowArea(event,B1)) {
                                getB2(-1, true);

                            } else if (is3rdLine(event,B1) && inRowArea(event,B1) ) {
                              //  B3 = cellBeanList.get(B1.id - 2);
                                if (B2 != null && B2.y == B1.y) {
                                    getB2(-1, true);
                                    getB2(-2, true);
                                } else {
                                    getB2(-2, true);
                                    if (hitList.size() == 2)
                                        hitList.add(1, B1.id - 1);
                                }
                                //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            } else if(is4thLine(event,B1) && inRowArea(event,B1)){
                                getB2(-3,true);
                                if(hitList.size()==2){
                                    hitList.add(1,2);
                                    hitList.add(2,1);
                                }
                            }


                            else if (is2ndLine(event, B1) && getdx(event, B1) < 0) {
                                    /*○○●
                                    * ○●○*/
                                if (getdy(event, B1) > B1.radius * 3 && getdy(event, B1) < B1.radius * 4.5) {
                                    if(B1.id < 12 && (B1.id != 0 || B1.id != 4 || B1.id!= 8))
                                    getB2(3, true);
                                    /*○○●
                                      ○○○
                                    * ○●○*/
                                } else if (getdy(event, B1) > B1.radius * 5.5 && getdy(event,B1) < B1.radius* 7.5) {
                                    if(line123.contains(B1.id))
                                    getB2(7, true);
                                    /*○●
                                    * ○○
                                    * ○○
                                    * ●○*/
                                }else if (getdy(event,B1) > B1.radius*8 && getdy(event,B1) < getHeight() - B1.radius){
                                    if(B1.id < 4 && B1.id != 0){
                                        getB2(11,true);
                                    }
                                }


                            } else if (is3rdLine(event, B1)) {
                                    /*○○●
                                    * ●○○*/
                                if (getdy(event, B1) > B1.radius * 2.5 && getdy(event, B1) < B1.radius * 4) {
                                    if(minusline12.contains(B1.id)){
                                        getB2(2, true);
                                    }
                                    /*○○●
                                      ○○○
                                    * ●○○*/
                                } else if (getdy(event, B1) > B1.radius * 5 && getdy(event, B1) < B1.radius*7.5) {
                                    if (B1.id == 2 || B1.id == 3 || B1.id == 6 || B1.id ==7) {
                                        getB2(6, true);
                                    }
                                    /*○○○●
                                    * ○○○○
                                    * ○○○○
                                    * ○●○○*/
                                } else if(getdy(event,B1) > B1.radius*8 && getdy(event,B1) < getHeight() - B1.radius){
                                    if (B1.id == 2 || B1.id == 3){
                                        getB2(10,true);
                                    }

                                }
                            }else if(is4thLine(event,B1)){
                                /*○○○●
                                * ●○○○*/
                                if (getdy(event, B1) > B1.radius * 2.5 && getdy(event, B1) < B1.radius * 4){
                                        if(B1.id ==3 || B1.id == 7 || B1.id ==11){
                                            getB2(1,true);
                                        }
                                /*○○○●
                                  ○○○○
                                * ●○○○*/

                                }else if(getdy(event, B1) > B1.radius * 5 && getdy(event, B1) < B1.radius*7.5){
                                        if(B1.id ==3 || B1.id ==7){
                                            getB2(5,true);
                                        }
                                        /*○○○●
                                          ○○○○
                                          ○○○○
                                        * ●○○○*/
                                }else if (getdy(event,B1) > B1.radius*8 && getdy(event,B1) < getHeight() - B1.radius){
                                        if(B1.id == 3){
                                            getB2(9,true);
                                        }
                                 }
                            }
                        }
                        //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                        //往下劃
                        if (isLine(event, B1) && getdy(event, B1) > 0) {
                            if (B1.id < 12) {
                                B3 = cellBeanList.get(B1.id + 4);
                            }

                                /*●○○
                                * ●○○*/
                            if (is2ndRow(event, B1)) {
                                if (!hitList.contains(B3.id)) {
                                    hitList.add(B3.id);
                                }

                                /*●○○
                                * ●○○
                                * ●○○*///先看有沒有平行，有的話順序是先加中間的球
                            } else if (is3rdRow(event, B1)) {
                                if (B1.id < 8) {
                                    getB2(8, true);
                                }
                                if (hitList.size() == 2) {
                                    hitList.add(1, B1.id + 4);
                                }

                            }else if(is4thRow(event,B1)){
                                if(B1.id < 4){
                                    getB2(12,true);
                                }
                                if(hitList.size() == 2 ){
                                    hitList.add(1,B1.id+4);
                                    hitList.add(2,B1.id+8);
                                }
                            }
                            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            //往上劃
                        } else if (isLine(event, B1) && getdy(event,B1) < 0 && getdx(event,B1) > 0) {
                            if (B1.id > 3) {
                                B3 = cellBeanList.get(B1.id - 4);
                            }
                            // B3 = cellBeanList.get(B1.id-3);//有問題
                                /*●○○
                                * ●○○*/
                            if (is2ndRow(event, B1)) {
                                if (B3 != null && !hitList.contains(B3.id)) {
                                    hitList.add(B3.id);
                                }
                                /*●○○
                                * ●○○
                                * ●○○*///先看有沒有平行，有的話順序是先加中間的球
                            } else if (is3rdRow(event, B1) && B1.id > 7) {
                                getB2(-8, true);
                                if (hitList.size() == 2) {
                                    hitList.add(1, B1.id - 4);
                                }
                            } else if (is4thRow(event, B1) && B1.id > 11) {
                                getB2(-12, true);
                                if (hitList.size() == 2) {
                                    hitList.add(1, B1.id - 4);
                                    hitList.add(1, B1.id - 8);
                                }
                            }
                            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            //往斜上劃
                                /*○●○
                                * ●○○*/


                            else if (is2ndLine(event, B1)) {
                                /*○●○
                                * ●○○*/
                                if (Math.abs(getdy(event, B1)) > B1.radius * 3 && Math.abs(getdy(event, B1)) < B1.radius * 5) {
                                    if (ListrighttopNum.contains(B1.id)) {
                                        getB2(-3, true);
                                    }

                                    /*○●○
                                    * ○●○
                                    * ●○○*/

                                } else if (Math.abs(getdy(event, B1)) > B1.radius * 5.5 && Math.abs(getdy(event, B1)) < B1.radius * 7.5) {
                                    if (ListrighttopNum.contains(B1.id) && B1.id > 7) {
                                        getB2(-7, true);
                                    }
                                    /*○●
                                      ○○
                                      ○○
                                      ●○*/
                                } else if (Math.abs(getdy(event, B1)) > B1.radius * 8 && Math.abs(getdy(event, B1)) < getHeight() - B1.radius) {
                                    if (B1.id == 12 || B1.id == 13 || B1.id == 14) {
                                        getB2(-11, true);
                                    }
                                }


                                //往上斜對角
                                /*○○●
                                * ●○○*/
                            } else if (is3rdLine(event, B1) ){
                                if (Math.abs(getdy(event, B1)) > B1.radius * 2.5 && Math.abs(getdy(event, B1)) < B1.radius * 4) {
                                    if (ListrighttopNum2nd.contains(B1.id)) {
                                        getB2(-2, true);
                                    }//往上斜對角再往上
                                /*○○●
                                * ○○●
                                * ●○○*/
                                } else if ( Math.abs(getdy(event, B1)) > B1.radius * 5.5 && Math.abs(getdy(event, B1)) < B1.radius*7.5) {
                                        if (B1.id > 7 && ListrighttopNum2nd.contains(B1.id)) {
                                            getB2(-6, true);
                                        }
                                        /*○○●○
                                        * ○○○○
                                        * ○○○○
                                        * ●○○○*/
                                } else if(Math.abs(getdy(event, B1)) > B1.radius * 8 && Math.abs(getdy(event, B1)) <getHeight() - B1.radius){
                                        if(B1.id == 12 || B1.id == 13){
                                            getB2(-10,true);
                                        }
                                }


                                /*○○○●
                                * ●○○○*/
                            }else if(is4thLine(event,B1)){
                                if(Math.abs(getdy(event, B1)) > B1.radius * 2.5 && Math.abs(getdy(event, B1)) < B1.radius * 4){
                                    if(B1.id ==4 || B1.id == 8 || B1.id ==12){
                                        getB2(-1,true);
                                    }
                                    /*○○○●
                                    * ○○○○
                                    * ●○○○*/
                                }else if(Math.abs(getdy(event, B1)) > B1.radius * 5.5 && Math.abs(getdy(event, B1)) < B1.radius*7.5){
                                    if(B1.id == 8 || B1.id ==12){
                                        getB2(-5,true);
                                    }
                                    /*○○○●
                                    * ○○○○
                                    * ○○○○
                                    * ●○○○*/
                                }else if(Math.abs(getdy(event, B1)) > B1.radius * 8 && Math.abs(getdy(event, B1)) <getHeight() - B1.radius){
                                    if(B1.id ==12){
                                        getB2(-9,true);
                                    }
                                }
                            }
                            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
                            //右往斜上劃
                        }else if (isLine(event, B1) && getdx(event, B1) < 0 && getdy(event, B1) < 0) {
                                /*○●○
                                * ○○●*/
                                if (is2ndLine(event, B1)) {

                                    if (Math.abs(getdy(event, B1)) > B1.radius * 3 && Math.abs(getdy(event, B1)) < B1.radius * 5) {
                                        if (B1.id > 3 && B1.id % 4 != 0) {
                                            getB2(-5, true);
                                        }
                                        //再往上
                                    /*○●○
                                      ○●○
                                    * ○○●*/
                                    } else if (is2ndLine(event, B1) && Math.abs(getdy(event, B1)) > B1.radius* 5.5 && Math.abs(getdy(event, B1)) < B1.radius*7.5) {
                                        if (B1.id > 7 && B1.id % 4 != 0) {
                                            getB2(-9, true);
                                        }
                                    /*○●○
                                      ○●○
                                      ○●○
                                    * ○○●*/
                                    }else if(Math.abs(getdy(event, B1)) > B1.radius * 8 && Math.abs(getdy(event, B1)) <getHeight() - B1.radius){
                                        if(B1.id > 12){
                                            getB2(-13,true);
                                        }
                                    }
                                    //斜對角
                                /*●○○
                                * ○○●*/
                                }else if (is3rdLine(event, B1) ){
                                       if( Math.abs(getdy(event, B1)) > B1.radius * 2.5 && Math.abs(getdy(event, B1)) < B1.radius * 4) {
                                            if (B1.id > 3 && (B1.id % 4 > 1)) {
                                            getB2(-6, true);
                                        }
                                    //再往上
                                /*●○○
                                * ●○○
                                * ○○●*/
                                        }else if (Math.abs(getdy(event, B1)) > B1.radius * 5.5 && Math.abs(getdy(event, B1)) < B1.radius*7.5) {
                                    if (B1.id > 7 && (B1.id % 4 > 1)) {
                                        getB2(-10, true);
                                    }
                                    /*●○○
                                      ●○○
                                    * ●○○
                                    * ○○●*/
                                        }else if (Math.abs(getdy(event, B1)) > B1.radius * 8 && Math.abs(getdy(event, B1)) <getHeight() - B1.radius)
                                            if(B1.id > 13){
                                           getB2(-14,true);
                                            }
                            }else if(is4thLine(event,B1)){
                                    /*●○○○
                                    * ○○○●*/
                                    if(Math.abs(getdy(event, B1)) > B1.radius * 2.5 && Math.abs(getdy(event, B1)) < B1.radius * 4){
                                        if(B1.id>3 && (B1.id %4 == 3 )){
                                            getB2(-7,true);
                                        }
                                        /*●○○○
                                          ○○○○
                                        * ○○○●*/
                                    }else if (Math.abs(getdy(event, B1)) > B1.radius * 5.5 && Math.abs(getdy(event, B1)) < B1.radius*7.5){
                                        if(B1.id>7 && (B1.id %4 == 3 )){
                                            getB2(-11,true);
                                        }
                                        /*●○○○
                                          ○○○○
                                          ○○○○
                                        * ○○○●*/
                                    }else if (Math.abs(getdy(event, B1)) > B1.radius * 8 && Math.abs(getdy(event, B1)) <getHeight() - B1.radius){
                                        if(B1.id == 15){
                                            getB2(-15,true);
                                        }
                                    }
                                }
                            }

                    }//myline > ruleline
                }//myline>0 end
            }//hitsize>0 end
        }//for each end
        return hitList;
    }

    private List updateNoIgnoreRepeatState(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        for (CellBean c : this.cellBeanList) {
            if (!c.isHit && c.of(x, y) ) {
                switch (c.id){
                    case 0 :{hitList.add(0);break;}
                    case 1 :{hitList.add(1);break;}
                    case 2 :{hitList.add(2);break;}
                    case 3 :{hitList.add(3);break;}
                    case 4 :{hitList.add(4);break;}
                    case 5 :{hitList.add(5);break;}
                    case 6 :{hitList.add(6);break;}
                    case 7 :{hitList.add(7);break;}
                    case 8 :{hitList.add(8);break;}
                }
            }

            if (hitList.size() > 0) {

                B1 = cellBeanList.get(hitList.get(0));
                B1.isHit = true;
                B1.count =1;

                B2 = null;
                B3 = null;

                if (getMyLine(event, B1) > 0) {//一開始劃就動作

                    if (getMyLine(event, B1) > B1.radius) {

                        if (getdx(event, B1) > B1.radius && getdx(event, B1) > 0) {
                            toRight(event, B1);
                        } else if (getdx(event, B1) < 0) {
                            toLeft(event, B1);
                        }
                        if (getdy(event, B1) > 0 ) {
                            toDown(event, B1);
                        } else if (getdy(event,B1) < 0){
                            toUp(event, B1);
                        }

                    }
                }
            }//hitsize>0 end

        } return hitList;//for each end
    }

private double getMyLine(MotionEvent event,CellBean Bean){
        float x = event.getX();float y = event.getY();
    double Myline = Math.sqrt(((x - Bean.x) * (x - Bean.x)) + ((y - Bean.y) * (y - Bean.y)));
    return Myline;
}

private void toRight(MotionEvent event,CellBean Bean){
    if (isRow(event, Bean) /*&& getdx(event, Bean) > 0*/) {

        //往右劃連接隔壁的那一個
        //●●○
        //先給中間的球id，後面來看是否平行再決定要不要加
        if (Bean.id != cellBeanList.size() - 1) {
            B3 = cellBeanList.get(Bean.id);
        }

        if (is2ndLine(event, Bean) && inRowArea(event,Bean)) {
            getB2forRepeatNoIg(1,true);
            //連最右邊時先看是否跟b1平行，是的話，先加中間的再加後面的
            //●●●
        } else if (is3rdLine(event, Bean) && inRowArea(event,Bean)) {
            getB2forRepeatNoIg(2, true);

        }else if(getdx(event,Bean)< 0 && inRowArea(event,Bean)){
            getB2forRepeatNoIg(0,true);
        }
        //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        else if (is2ndLine(event, Bean)) {
                                /*●○○
                                * ○●○*/
            if (getdy(event, Bean) > Bean.radius * 3 && Math.abs(getdy(event, Bean)) <Bean.radius * 4.5) {
                if(Bean.id < 6){
                    getB2forRepeatNoIg(4, true);
                }
                                /*●○○
                                  ○○○
                                * ○●○*/
            } else if (getdy(event, Bean) > Bean.diameter * 3) {
                getB2forRepeatNoIg(7, true);
            }

        } else if (is3rdLine(event, Bean)) {
                                 /*●○○
                                 * ○○●*/
            if (is3rdLine(event, Bean) && getdy(event, Bean) > Bean.radius * 2.5 && getdy(event, Bean) < Bean.radius * 4) {
                if(Bean.id < 5){
                    getB2forRepeatNoIg(5, true);
                }

                                /*●○○
                                  ○○○
                                * ○○●*/
            } else if (is3rdLine(event, Bean) && getdy(event, Bean) < getHeight() - Bean.radius && getdy(event, Bean) > Bean.radius * 5.5) {
                if(Bean.id == 0){
                    getB2forRepeatNoIg(8, true);
                }
            }

        }
        //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        //getdx<0代表往左劃
    }


}

private List toLeft(MotionEvent event,CellBean Bean){
    if (isRow(event, Bean) && getdx(event, Bean) < 0) {
        if (is2ndLine(event, Bean) && inRowArea(event,Bean)) {
            getB2forRepeatNoIg(-1, true);

        } else if (is3rdLine(event,Bean) && inRowArea(event,Bean) ) {
           // B3 = cellBeanList.get(Bean.id - 2);
            /*if (B2 != null && B2.y == B1.y) {
                getB2(-1, true);
                getB2(-2, true);
            } else {*/
            getB2forRepeatNoIg(-2, true);
               /* if (hitList.size() == 2)
                    hitList.add(1, Bean.id - 1);
            }*/
            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        } else if (is2ndLine(event, Bean) && getdx(event, Bean) < 0) {
                                    /*○○●
                                    * ○●○*/
            if (is2ndLine(event, Bean) && getdy(event, Bean) > Bean.radius * 3 && getdy(event, Bean) < Bean.radius * 4.5) {
                getB2forRepeatNoIg(2, true);
                                    /*○○●
                                      ○○○
                                    * ○●○*/
            } else if (is2ndLine(event, Bean) && getdy(event, Bean) > Bean.diameter * 3) {
                getB2forRepeatNoIg(5, true);
            }
        } else if (is3rdLine(event, Bean)) {
                                    /*○○●
                                    * ●○○*/
            if (getdy(event, Bean) >Bean.radius * 2.5 && getdy(event, Bean) < Bean.radius * 4) {
                if(Bean.id !=8){
                    getB2forRepeatNoIg(1, true);
                }
                                    /*○○●
                                      ○○○
                                    * ●○○*/
            } else if (getdy(event, Bean) > Bean.radius * 5 && getdy(event, Bean) < getHeight() - Bean.radius) {
                if (Bean.id < 3) {
                    getB2forRepeatNoIg(4, true);
                }
            }
        }
    }
    return hitList;
}

private List toDown(MotionEvent event,CellBean Bean){
    if (isLine(event, Bean) && getdy(event, Bean) > 0) {
      /*  if (Bean.id < 6) {
            B3 = cellBeanList.get(Bean.id + 3);
        }*/
                                /*●○○
                                * ●○○*/
        if (is2ndRow(event, Bean) ) {
            if(Bean.id < 6){
                getB2forRepeatNoIg(3,true);
            }
           /* if (!hitList.contains(B3.id)) {
                hitList.add(B3.id);
            }*/

                                /*●○○
                                * ●○○
                                * ●○○*///先看有沒有平行，有的話順序是先加中間的球
        } else if (is3rdRow(event, Bean)) {
            if (Bean.id < 3) {
                getB2forRepeatNoIg(6, true);
            }
           /* if (hitList.size() == 2) {
                hitList.add(1, Bean.id + 3);
            }*/

        }
        //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        //往上劃
    }
    return hitList;
}

private List toUp(MotionEvent event,CellBean Bean){
    if (isLine(event, Bean) && getdx(event, Bean) > 0) {
                                /*●○○
                                * ●○○*/
        if (is2ndRow(event, B1)) {

            if (Bean.id > 2){
                getB2forRepeatNoIg(-3,true);
            }
                                /*●○○
                                * ●○○
                                * ●○○*///先看有沒有平行，有的話順序是先加中間的球
        } else if (is3rdRow(event, Bean)&& Bean.id > 5) {
            getB2forRepeatNoIg(-6, true);

            //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
            //往斜上劃
                                /*○●○
                                * ●○○*/
        } else if (is2ndLine(event, Bean) && Math.abs(getdy(event, Bean)) > Bean.radius * 3 && Math.abs(getdy(event, Bean)) < Bean.radius * 4) {
            if(Bean.id > 1){
                getB2forRepeatNoIg(-2, true);
            }
                                    /*○●○
                                    * ○●○
                                    * ●○○*/
        } else if (is2ndLine(event, Bean) && Math.abs(getdy(event, Bean)) >Bean.diameter * 3 && Math.abs(getdy(event, Bean)) < getHeight()) {
            if(Bean.id > 5)
                getB2forRepeatNoIg(-5, true);
        }
        //往上斜對角
                                /*○○●
                                * ●○○*/
        else if (is3rdLine(event, Bean) && Math.abs(getdy(event, Bean)) > Bean.radius * 2.5 && Math.abs(getdy(event,Bean)) < Bean.radius * 4) {
            if(Bean.id > 2){
                getB2forRepeatNoIg(-1, true);
            }//往上斜對角再往上
                                /*○○●
                                * ○○●
                                * ●○○*/
        } else if (is3rdLine(event, Bean) && Math.abs(getdy(event, Bean)) > Bean.radius * 5 && Math.abs(getdy(event, Bean)) < getHeight()) {
                if(Bean.id >5){
                    getB2forRepeatNoIg(-4, true);
                }
        }
        //－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        //右往斜上劃
    } else if (isLine(event, Bean) && getdx(event, Bean) < 0 && getdy(event, Bean) < 0) {
                                /*○●○
                                * ○○●*/
        if (is2ndLine(event, Bean) && Math.abs(getdy(event, Bean)) > Bean.radius * 3.5 && Math.abs(getdy(event, Bean)) < Bean.radius * 4.5) {
            if(Bean.id > 3){
                getB2forRepeatNoIg(-4, true);
            }
            //再往上
                                    /*○●○
                                      ○●○
                                    * ○○●*/
        } else if (is2ndLine(event, Bean) && Math.abs(getdy(event, Bean)) > Bean.diameter * 3 && Math.abs(getdy(event, Bean)) < getHeight()) {
            if(Bean.id > 6){
                getB2forRepeatNoIg(-7, true);
            }
        }
        //斜對角
                                /*●○○
                                * ○○●*/
        else if (is3rdLine(event, Bean) && Math.abs(getdy(event, Bean)) > Bean.radius * 2.5 && Math.abs(getdy(event, Bean)) < Bean.radius * 3.5) {
            if(Bean.id > 4){
                getB2forRepeatNoIg(-5, true);
            }
            //再往上
                                /*●○○
                                * ●○○
                                * ○○●*/
        } else if (is3rdLine(event, Bean) && Math.abs(getdy(event, Bean)) > Bean.radius * 6 && Math.abs(getdy(event, Bean)) < getHeight()) {
            if(Bean.id > 7){
                getB2forRepeatNoIg(-8, true);
            }
        }
}
return hitList;
}


    private CellBean getB2(int count, boolean b) {
        B2 = cellBeanList.get(B1.id + count);

       if (!B2.isHit ) {
            hitList.add(B2.id);
           B2.isHit =b;
           //B2.count=2;
       }

        return  B2;
    }

    private CellBean getB2forRepeatNoIg(int count, boolean b) {
        B2 = cellBeanList.get(B1.id + count);

        if (!B2.isHit ) {
            hitList.add(B2.id);
            B2.count=2;
        }
        return  B2;
    }

    private boolean isRow(MotionEvent event, CellBean Bean){
        if(Bean != null){
        float x = event.getX();
        float dx = x-Bean.x;

        return  Math.abs(dx) < getWidth() ;
        } else
            return false;
    }

    private boolean isLine(MotionEvent event,CellBean Bean){
        if(Bean != null){
        float y = event.getY();
        float dy = y-Bean.y;

        return Math.abs(dy) < getHeight() ;
        }else
            return false;
    }

    private boolean inRowArea(MotionEvent event,CellBean Bean){
       return Math.abs(getdy(event, Bean)) < Bean.radius;
    }

    private boolean is2ndRow(MotionEvent event, CellBean Bean){
      return  Math.abs(getdy(event,Bean)) > Bean.radius*2.5 && Math.abs(getdy(event,Bean)) < Bean.radius*4.5 && Math.abs(getdx(event,Bean)) < Bean.radius*1.2;
    }
    private boolean is3rdRow(MotionEvent event,CellBean Bean){
      return Math.abs(getdy(event,Bean)) >Bean.radius*5  && Math.abs(getdy(event,Bean)) < Bean.radius*8 && Math.abs(getdx(event,Bean)) < Bean.radius*1.2;
    }
    private boolean is4thRow(MotionEvent event,CellBean Bean){
        return Math.abs(getdy(event,Bean)) >Bean.radius*8.5  && Math.abs(getdy(event,Bean)) < Bean.radius*10.5 && Math.abs(getdx(event,Bean)) < Bean.radius*1.2;
    }

    private boolean is2ndLine(MotionEvent event, CellBean Bean){
       return Math.abs(getdx(event,Bean)) > Bean.radius*2 && Math.abs(getdx(event,Bean)) < Bean.radius*4.5;
    }
    private boolean is3rdLine(MotionEvent event, CellBean Bean){
        return Math.abs(getdx(event,Bean)) > Bean.radius*5 && Math.abs(getdx(event,Bean)) < Bean.radius*7.5 ;
    }
    private boolean is4thLine(MotionEvent event, CellBean Bean){
        return Math.abs(getdx(event,Bean)) > Bean.radius*8 && Math.abs(getdx(event,Bean)) < Bean.radius*10.5 ;
    }

    private float getdx(MotionEvent event, CellBean Bean){
        float dx = event.getX() - Bean.x;
        return dx;
    }

    private float getdy(MotionEvent event, CellBean Bean){
        float dy = event.getY() - Bean.y;
        return dy;
    }


    public void setResultState(ResultState resultState){
        this.resultState = resultState;
    }
}
