package com.example.r30_a.testlayout.VideoView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by R30-A on 2018/1/30.
 */

public abstract class LazyLoadFragment extends Fragment {

    protected boolean isInit = false;
    protected boolean isLoad = false;
    protected final String TAG = "LazyLoadFragment";
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(setContentView(),container,false);
        isInit = true;
        //知始化加載數據
        loadData();
        return view;
    }
//視圖是否已經可見，可以的話就load data
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        loadData();
    }
    //是否可以加載數的方法
    //條件1。視圖已經初始化了
    //條件2。視圖可見
    protected  void loadData(){
        if(!isInit){
            return;
        }
        if(getUserVisibleHint()){
            lazyload();
            isLoad =true;
        }else {
            if(isLoad){
                stopLoad();
            }
        }

    }
//銷毀時fragment始始化，變成false
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
    }


//加載過的數據已經不可見，且需要切換其它頁面時停止加載用的方法
    protected abstract void stopLoad();
//視圖已經知始化並用戶可見時才去加載數
    protected abstract void lazyload();

    protected <T extends View> T findViewById(int id){
        return (T)getContentView().findViewById(id);
    }

//用來設置要顯示的view
    protected abstract int setContentView();


    protected View  getContentView(){
        return view;
    }

}
