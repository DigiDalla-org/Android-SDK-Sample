package com.oovoo.sdk.sample.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.oovoo.sdk.api.LogSdk;
import com.oovoo.sdk.oovoosdksampleshow.R;
import com.oovoo.sdk.sample.app.ooVooSdkSampleShowApp;
import com.oovoo.sdk.sample.ui.SampleActivity;

/**
 * Created by oovoo on 9/8/15.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String property = data.getString("property");
        String body     = data.getString("body");

        LogSdk.d(TAG, "GcmListenerService :: From     : " + from);
        LogSdk.d(TAG, "GcmListenerService :: full     : " + data.toString());
        LogSdk.d(TAG, "GcmListenerService :: property : " + property);
        LogSdk.d(TAG, "GcmListenerService :: body     : " + body);

        ooVooSdkSampleShowApp application = (ooVooSdkSampleShowApp) getApplication();

        Intent intent = new Intent(application.getContext(), SampleActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(application.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(application.getContext());

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(property != null ? property : from)
                .setContentTitle(property != null ? property : from)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) application.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

    }
}
