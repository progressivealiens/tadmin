package com.techsalt.tadmin.firebase;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techsalt.tadmin.R;
import com.techsalt.tadmin.views.activity.OperationalAttendanceActivity;
import com.techsalt.tadmin.views.activity.ShowNotificationActivity;
import com.techsalt.tadmin.views.activity.SiteVisitHistory;
import com.techsalt.tadmin.views.activity.SplashActivity;

/**
 * Created by ankus on 29/05/2017.
 */

public class FireMsgService extends FirebaseMessagingService {

    public int NOTIFICATION_CHANNEL_ID = 1234;
    Intent intent;
    String title = "", subject = "", body = "",notificationType="",mobile="";
    Uri defaultSoundUri;
    PendingIntent pendingIntent;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("TadminFireBaseMessage", "" + remoteMessage.getData());

        if (remoteMessage.getData() != null) {
            receiveData(remoteMessage);
        }
    }

    private void receiveData(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("title");
            subject = remoteMessage.getData().get("subject");
            body = remoteMessage.getData().get("body");

            if (body.equalsIgnoreCase("")) {
                body = "Fetching latest data";
            }

            notificationType=remoteMessage.getData().get("notificationType");
            mobile=remoteMessage.getData().get("mobile");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                createNotificationAboveOreo(notificationType);
            else
                createNotificationBelowOreo(notificationType);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationAboveOreo(String notificationType) {

        try {
            if (notificationType.equalsIgnoreCase("show")) {
                defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.sos_tone);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
                r.play();
            }else{
                defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
                r.play();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (notificationType != null) {
            if (notificationType.equalsIgnoreCase("checkin")) {
                intent = new Intent(this, OperationalAttendanceActivity.class);
            }else if (notificationType.equalsIgnoreCase("site")) {
                intent = new Intent(this, SiteVisitHistory.class);
            }else if (notificationType.equalsIgnoreCase("show")) {
                intent = new Intent(this, ShowNotificationActivity.class);
                intent.putExtra("NotificationTitle", title);
                intent.putExtra("NotificationSubject", subject);
                intent.putExtra("NotificationMessage", body);
                intent.putExtra("NotificationMobile", mobile);
            }else{
                intent = new Intent(this, SplashActivity.class);
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_CHANNEL_ID, intent, PendingIntent.FLAG_ONE_SHOT);

        CharSequence name = "Tadmin";
        String description = "Notification";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel((String.valueOf(NOTIFICATION_CHANNEL_ID)), name, importance);
        channel.setDescription(description);
        NotificationManager notificationManage = getSystemService(NotificationManager.class);
        notificationManage.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(NOTIFICATION_CHANNEL_ID))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher_main_round)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(subject + "\n" + body))
                .setAutoCancel(true)
                .setLights(Color.BLUE, 500, 500)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_CHANNEL_ID, builder.build());
    }

    private void createNotificationBelowOreo(String notificationType) {

        try {
            if (notificationType.equalsIgnoreCase("show")) {
                defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.sos_tone);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
                r.play();
            }else{
                defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
                r.play();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (notificationType != null) {
            if (notificationType.equalsIgnoreCase("checkin")) {
                intent = new Intent(this, OperationalAttendanceActivity.class);
            }else if (notificationType.equalsIgnoreCase("site")) {
                intent = new Intent(this, SiteVisitHistory.class);
            }else if (notificationType.equalsIgnoreCase("show")) {
                intent = new Intent(this, ShowNotificationActivity.class);
                intent.putExtra("NotificationTitle", title);
                intent.putExtra("NotificationSubject", subject);
                intent.putExtra("NotificationMessage", body);
                intent.putExtra("NotificationMobile", mobile);
            }else{
                intent = new Intent(this, SplashActivity.class);
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_CHANNEL_ID, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(subject + "\n" + body))
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        mNotificationManager.notify(NOTIFICATION_CHANNEL_ID, mBuilder.build());
    }

}

