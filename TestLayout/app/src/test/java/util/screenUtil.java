package util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by R30-A on 2017/12/1.
 */

public class screenUtil {

    //取得屏幕的寬

    public static int getScreenWidth(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    //取得屏幕的高

    public static int getScreenHeight(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
