package com.example.vishal.msg_app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_tital  = remoteMessage.getNotification().getTitle();
        String notification_body = remoteMessage.getNotification().getBody();

        String from_id = remoteMessage.getData().get("from_id");

        String click_action = remoteMessage.getNotification().getClickAction();

        NotificationCompat.Builder mbuilder =
                new NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(notification_tital)
                        .setContentText(notification_body);


        Intent resultintent = new Intent(click_action);
        resultintent.putExtra("from_id",from_id);

        PendingIntent resultpendingintent = PendingIntent.getActivity(
                this,
                0,
                resultintent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mbuilder.setContentIntent(resultpendingintent);

        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotifymng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifymng.notify(mNotificationId,mbuilder.build());

    }
}
