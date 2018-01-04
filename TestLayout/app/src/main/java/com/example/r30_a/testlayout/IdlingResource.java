package com.example.r30_a.testlayout;

import com.bumptech.glide.request.ResourceCallback;

/**
 * Created by R30-A on 2017/12/28.
 */

public interface IdlingResource {
    //取得名稱
    public String getName();

    //當前的resource是否閒著
    public boolean isIdleNow();

    //註冊一個空閒狀態變換的resourceCallback
    public void registerIdleTransitionCallback(ResourceCallback callback);

    //通知espresso當前的idlingResource變成閒置狀態了
    public interface ResouceCallBack{
        //當前狀態變成空閒時，調用這招告訴espresso
        public void onTransitionToIdle();
    }


}
