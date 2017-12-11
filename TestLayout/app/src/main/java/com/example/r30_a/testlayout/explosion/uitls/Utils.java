package com.example.r30_a.testlayout.explosion.uitls;

import android.content.res.Resources;

/**
 * Created by R30-A on 2017/12/5.
 */

public class Utils {
    //密度
    public static final float DENSITY =
            Resources.getSystem().getDisplayMetrics().density;

    public static int dp2px(int dp){return Math.round(dp * DENSITY);}
}
