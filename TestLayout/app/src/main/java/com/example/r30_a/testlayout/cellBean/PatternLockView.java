package com.example.r30_a.testlayout.cellBean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.r30_a.testlayout.Config;
import com.example.r30_a.testlayout.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by R30-A on 2017/12/12.
 */
//開始畫出小球的畫盤
public class PatternLockView extends View{
    //線的屬性
    private int color;
    private int hitColor;
    private int errorColor;
    private int fillColor;
    private float lineWidth;
    String listString;
    String []s;
    //線的屬性

    private ResultState resultState;

    CellBean Bean, AgainBean;

    private List<CellBean> cellBeanList, cellBeanAgainList;
    private List<Integer> hitList, hitAgainList;
    private String AgainList;
    private int hitSize, hitAgainSize;
    Canvas canvas;
    private Paint paint;

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
        this.fillColor = t.getColor(R.styleable.PatternLockerView_plv_fillColor,Color.parseColor("#FAFAFA"));
        this.lineWidth = t.getDimension(R.styleable.PatternLockerView_plv_lineWidth,1);

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
        this.paint.setStrokeWidth(this.lineWidth);

        this.hitList = new ArrayList<>();
        this.hitAgainList = new ArrayList<>();
        cellBeanAgainList = new ArrayList<>();


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
            this.cellBeanAgainList = new CellBeanFactory(getWidth(),getHeight()).getCellBeanListfor44();
        }else if  ((this.cellBeanList == null) && CellSettingPageActivity.setCell == 1){
            this.cellBeanList = new CellBeanFactory(getWidth(),getHeight()).getCellBeanList();
            this.cellBeanAgainList = new CellBeanFactory(getWidth(),getHeight()).getCellBeanList();
        }


        //根據配置好的球數與球盤畫出圓圈
        if (!CellSettingPageActivity.ishide) {
            drawLine(canvas);
        }
        drawCircles(canvas);
      //  drawsecondcircle(canvas);
    }

    private void drawLine(Canvas canvas){
        if(!this.hitList.isEmpty()){
        Path path = new Path();

        CellBean first = this.cellBeanList.get(this.hitList.get(0));
        path.moveTo(first.x, first.y);//從碰到的第一個球坐標開始
        for(int i = 1; i< this.hitList.size(); i++){//不知道會碰到幾個，所以用for
            if(first.isHit){
            CellBean nextBean = this.cellBeanList.get(this.hitList.get(i));
            path.lineTo(nextBean.x, nextBean.y);}


        }
        //設定線條結束的地方, 線條呈動態移動
        if(((this.endX != 0) || (this.endY !=0)) && (this.hitList.size() < 16)){
            path.lineTo(this.endX, this.endY);
        }
        this.paint.setColor(this.getColorByState(this.resultState));
        this.paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, this.paint);
    }
    }

private int getColorByState(ResultState state){
        return state == ResultState.OK?
                this.hitColor : this.errorColor;
}

//畫出圓球的方法
    private void drawCircles(Canvas canvas){
        this.paint.setStyle(Paint.Style.FILL);

        //根據球的數量一一畫出
        for(int i = 0; i < this.cellBeanList.size(); i ++){
            //將每個球依序傳給小球物件
            Bean = this.cellBeanList.get(i);
            //開始判斷是否有摸到球來畫對應的顏色
            if(Bean.isHit ){//如果有碰到的話
                //外面的圈圈
                this.paint.setColor(this.getColorByState(this.resultState));
                canvas.drawCircle(Bean.x, Bean.y, Bean.radius, paint);
                //上色
                this.paint.setColor(this.fillColor);
                canvas.drawCircle(Bean.x, Bean.y, Bean.radius - this.lineWidth, paint);
                //中間的點
                this.paint.setColor(this.getColorByState(this.resultState));
                canvas.drawCircle(Bean.x, Bean.y, Bean.radius/5f,paint);//5f

             //   drawsecondcircle(canvas);

            } else {//沒有碰到的話
                //外面的圈圈
                this.paint.setColor(this.color);
                canvas.drawCircle(Bean.x, Bean.y, Bean.radius, paint);
                //上色
                this.paint.setColor(this.fillColor);
                canvas.drawCircle(Bean.x, Bean.y, Bean.radius - lineWidth, paint);
            }


        }
        for(int j = 0; j<hitAgainList.size();j++){
            AgainBean = cellBeanAgainList.get(j);
            if(AgainBean.isHitAgain){
                this.paint.setColor(Color.RED);
                canvas.drawCircle(AgainBean.x, AgainBean.y, AgainBean.radius, paint);
            }
        }
        /*for(int j = 0; j< cellBeanAgainList.size(); j++){//處理重複畫到的球顏色
            AgainBean = cellBeanAgainList.get(j);
            if(AgainBean != null){
                this.paint.setColor(Color.RED);
                canvas.drawCircle(AgainBean.x, AgainBean.y, AgainBean.radius, paint);
            }
        }*/


        }

        public void drawsecondcircle(Canvas canvas) {
            this.paint.setStyle(Paint.Style.FILL);

            //根據球的數量一一畫出
            for (int i = 0; i < this.cellBeanAgainList.size(); i++) {
                //將每個球依序傳給小球物件
                AgainBean = cellBeanAgainList.get(i);//得到的重複號碼給指定的球

                if(Bean.isHit && AgainBean.isHitAgain){
                this.paint.setColor(Color.RED);
                canvas.drawCircle(AgainBean.x, AgainBean.y, AgainBean.radius, paint);
            }

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
        updateHitState(event);
        //3. 設定監聽器
        if(this.listener !=null){
            this.listener.onStart(this);
        }


    }
    //處理移動時要處理的動作
    private void handleActionMove(MotionEvent event){
        //1. 更新點擊的狀況
        updateHitState(event);
        //2. 更新移動到的坐標位置
        this.endX = event.getX();
        this.endY = event.getY();
        //3. 通知監聽, 監聽改變中的hitlist(點中的球)
        final int size = this.hitList.size();
        if((this.listener != null) && (this.hitSize != size)){
            this.hitSize = size;
            this.listener.onChange(this, this.hitList);
        }
    }
    //處理手指離開時的動作
    private void handleActionUp(MotionEvent event){
        //1. 更新坐標
        updateHitState(event);
        this.endX = 0;
        this.endY = 0;
        //2. 通知監聽
        if(this.listener!= null){
            this.listener.onComplete(this,this.hitList);
        }
        //3. 有必要的話開始計時器
        if(this.hitSize > 0){
            starTimer();
        }
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
        },1000);//1秒後回復
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
        }

        this.hitList.clear();cellBeanAgainList.clear();
        this.hitSize = 0;//this.hitAgainSize = 0;
    }

    private void updateHitState(MotionEvent event){
        final float x = event.getX();
        final float y = event.getY();
        //根據摸到的位置來增加選取到的小球數
        for(CellBean c : this.cellBeanList){
            //把碰到的球的位置放給list，不一定是第一個，就根據摸到哪個，給它對應的小球id
            if(!c.isHit && c.of(x,y)){//如何增加hitlist的長度
            c.isHit = true;//hitfirst, 再一個boolean ishitagain控制重複到的號碼
            this.hitList.add(c.id);


            }//球的編號加入到list內，


            for(CellBean cc : this.cellBeanAgainList){//加入有重複選到的球到list裡面
                if(c.isHit &&  c.of(x,y)) {
                    //cc.isHit=false;
                    this.hitList.add(c.id);
              // hitAgainList.add(cc.id);
               cc.isHitAgain = true;
             //       cc.isHitAgain = true;

              //      cc.isHit=true;

                   // listString += hitList.toString();//轉成一串字，做字串比對, 假設是01234512，要如何取出後面的12
                   // s = new String[listString.length()];
                   // Arrays.sort(s);//01122345
                    /*for (int i = 0; i < s.length; i++) {
                        if (s[i] == s[i + 1]) {
                            listString = s[i];//12
                            hitAgainList.add(Integer.parseInt(listString));//重複的編號放到againlist裡了
                            c.isHitAgain = true;
                            cellBeanAgainList.add(c);
                        }
                    }*/

                }
                    //把拿到的數字一一轉成球的ID

            }     // cellBeanAgainList.addcc);
                  //  cc.isHitAgain = true;
//------------------------------------------------
                    //處理重複的球，條件為如果有重複的id在hitlist裡，就加到另一條list…
                  //if(hitList.contains()){
                        ;}
    }

                 //   }
                    // c.isHitAgain =true;
                    // c.isHit =false;

                    // AgainList += c.id+"";
                    //c.isHitAgain = true;

           // }
            //for(int i=0,j=1;i<listString.length();i++,j++){}





    public void setResultState(ResultState resultState){
        this.resultState = resultState;
    }
}
