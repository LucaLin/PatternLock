package com.example.r30_a.testlayout.pilelayout;

import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;

/**
 * Created by R30-A on 2017/12/7.
 */

public class Config {

    @IntRange(from = 2)
    public int space = 60;
    public int maxStakCount = 3;
    public int initialStackCount = 0;
    @FloatRange(from = 0f,to = 1f)
    public float secondaryScale;
    @FloatRange(from = 0f, to = 1f)
    public float scaleRatio;
}
