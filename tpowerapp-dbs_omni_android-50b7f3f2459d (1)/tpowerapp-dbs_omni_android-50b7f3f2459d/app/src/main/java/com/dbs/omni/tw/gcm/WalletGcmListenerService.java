/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dbs.omni.tw.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;


public class WalletGcmListenerService extends GcmListenerService {

    public static final String EXTRA_PUSH_MESSAGE = "extra_push_message";
    public static final String EXTRA_PUSH_URL = "extra_push_url";
    public static final String ACTION_RECEIVED_PUSH = "tw.com.taishinbank.ewallet.gcm.ACTION_RECEIVED_PUSH";
    private static final String TAG = "WalletGcmListener";
    private static int notificationId = 0;

    public enum MyPayPushType {

    }





    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Data: " + bundle2string(data));

        String message = data.getString("message");
//        //Server 來的推播，應要有url參數
        String url = data.getString("url");
        //TODO: 未來修改為格式需求

//        if (TextUtils.isEmpty(url)) {
//            Log.d(TAG, " No action from this push....");
//            return;
//        }
//
//        //看是不是定義的type
//        boolean hasInDefined = false;
//        String[] urlAction = url.split(":");
//        for (MyPayPushType type : MyPayPushType.values()) {
//            if (urlAction[0].equals(type.toString())) {
//                hasInDefined = true;
//                break;
//            }
//        }
//        if (hasInDefined == false) {
//            Log.d(TAG, " Action " + urlAction[0] + " is not in our definition list.");
//            return;
//        }

        //發送Android Notification -
        sendNotification(message, url);

        // 發送廣播（給儲值首頁）
        sendBroadcast(message, url);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String url) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.EXTRA_DO_ACION_BY_URL, url);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap bitmapLargeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmapLargeIcon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());
        notificationId++;
        // 如果notificationId > requestCode最大限制(16 bits)則歸0
        if((notificationId & 0xffff0000) != 0){
            notificationId = 0;
        }

    }

    private void sendBroadcast(String message, String url){
        Intent intent = new Intent(ACTION_RECEIVED_PUSH);
        intent.putExtra(EXTRA_PUSH_MESSAGE, message);
        intent.putExtra(EXTRA_PUSH_URL, url);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String bundle2string(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = "{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }";
        return string;
    }
}
