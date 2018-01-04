package com.dbs.omni.tw.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.MainActivity;

/**
 * Created by siang on 2017/5/4.
 */

public class TimeoutReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
//        String iconSmall = intent.getStringExtra("iconSmall");
        int requestCode = intent.getIntExtra("requestCode", 0);

//        int smallIcon = context.getResources().getIdentifier(iconSmall, "drawable", context.getPackageName());

        // 這邊表示點擊推播訊息後, 要返回 Unity, 所以必須是 UnityPlayerActivity.class
        Intent newIntent = new Intent(context, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, newIntent, PendingIntent.FLAG_CANCEL_CURRENT); // 取得PendingIntent


        // 建立推播內容
        Notification.Builder builder = new Notification.Builder(context.getApplicationContext());

        // setSmallIcon 尺寸建議 32 * 32
        builder.setSmallIcon(R.drawable.ic_logo);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel( true );
        Notification notification = builder.build();


        // 取得推播管理器, 執行推播
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestCode, notification);
    }
}