package com.example.foodwater.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import androidx.core.app.NotificationCompat;
import com.example.foodwater.MainActivity;
import com.example.foodwater.R;
import com.example.foodwater.utils.AppUtils;
import java.util.Calendar;
import java.util.Date;

public final class NotificationHelper {
    private NotificationManager notificationManager;
    private final String CHANNEL_ONE_ID;
    private final String CHANNEL_ONE_NAME;
    private final Context context;

    public NotificationHelper(Context ctx) {
        super();
        this.context = ctx;
        this.CHANNEL_ONE_ID = "com.example.zdrowe_zycie.CHANNELONE";
        this.CHANNEL_ONE_NAME = "Channel One";
    }

    private final void createChannels() {
        if (VERSION.SDK_INT >= 26) {
            SharedPreferences prefs = context.getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE);
            String MessageRingtone = prefs.getString(
                    AppUtils.NOTIFICATION_TONE_URI_KEY,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION
                    ).toString()
            );
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            if (!MessageRingtone.isEmpty()) {
                AudioAttributes audioAttributes = new Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel.setSound(Uri.parse(MessageRingtone), audioAttributes);
            }
            getManager().createNotificationChannel(channel);
        }
    }

    public final NotificationCompat.Builder getNotification(String title, String body, String RingtoneURI) {

        this.createChannels();
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(
                     BitmapFactory.decodeResource(
                           context.getResources(),
                           R.mipmap.ic_launcher
                     )
                )
                .setSmallIcon(R.drawable.water_drop)
                .setAutoCancel(true);
        notification.setShowWhen(true);
        notification.setSound(Uri.parse(RingtoneURI));
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                99,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        notification.setContentIntent(contentIntent);
        return notification;
    }

    private final boolean shallNotify() {
        SharedPreferences prefs = context.getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE);
        SqliteHelper sqliteHelper = new SqliteHelper(context);
        int percent = sqliteHelper.getIntake(AppUtils.getCurrentDate(),false) * 100 /
                prefs.getInt(AppUtils.TOTAL_INTAKE_KEY_WATER, 1);
        boolean doNotDisturbOff = true;
            Date now = Calendar.getInstance().getTime();
            Date start = new Date(2000,1,1,8,00);
            Date stop = new Date(2000,1,1,21,00);
            doNotDisturbOff = this.compareTimes(now, start) >= 0 && this.compareTimes(now, stop) <= 0;
        return doNotDisturbOff && percent < 100;
    }

    private final long compareTimes(Date currentTime, Date timeToRun) {
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentTime);

        Calendar runCal = Calendar.getInstance();
        runCal.setTime(timeToRun);

        runCal.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH));
        runCal.set(Calendar.MONTH, currentCal.get(Calendar.MONTH));
        runCal.set(Calendar.YEAR, currentCal.get(Calendar.YEAR));

        return currentCal.getTimeInMillis() - runCal.getTimeInMillis();
    }

    public final void notify(long id, NotificationCompat.Builder notification) {
        if (shallNotify()) {
            getManager().notify((int)id, notification.build());
        }
    }

    private final NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}

