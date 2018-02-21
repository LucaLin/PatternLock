package com.example.r30_a.patternlock.Controller;

import java.util.List;

/**
 * Created by Luca on 2018/2/14.
 */

public interface OnPatternChangeListener {
    //專門監聽patternlockview的監聽器

    //開始畫圖時調用start開始監聽
    void onStart(PatternLockView view);
    //圖案改變時(hitlist改變時)調用的方法
    void onChange(PatternLockView view, List<Integer> hitList);
    //圖案畫完之後的方法
    void onComplete(PatternLockView view, List<Integer> hitList);
    //圖案清除之的方法
    void onClear(PatternLockView view);






}
