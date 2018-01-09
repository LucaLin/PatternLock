package tw.com.taishinbank.ewallet.handler;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;

import static android.util.Log.getStackTraceString;

/**
 * Created by Siang on 4/25/16.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "ExceptionHandler";
    private final Activity activity;

    public ExceptionHandler(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e(TAG, "crash log", throwable);

        EventAnalyticsUtil.addSpecialEvent(activity, new SpecialEvent(SpecialEvent.TYPE_CRASH, getStackTraceString(throwable)));

        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

}
