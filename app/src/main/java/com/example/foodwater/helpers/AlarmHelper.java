package com.example.foodwater.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.example.foodwater.recievers.BootReceiver;
import com.example.foodwater.recievers.NotifierReceiver;
import java.util.concurrent.TimeUnit;

public final class AlarmHelper {
    private AlarmManager alarmManager;
    private final String ACTION_BD_NOTIFICATION = "com.example.zdrowe_zycie.NOTIFICATION";

    public final void setAlarm(Context context, long notificationFrequency) {
        long notificationFrequencyMs = TimeUnit.MINUTES.toMillis(notificationFrequency);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotifierReceiver.class);
        alarmIntent.setAction(this.ACTION_BD_NOTIFICATION);

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(),
                    notificationFrequencyMs,
                    pendingAlarmIntent
            );
        }
        /* Restart if rebooted */
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        context.getPackageManager().setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    public final void cancelAlarm(Context context) {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, NotifierReceiver.class);
        alarmIntent.setAction(this.ACTION_BD_NOTIFICATION);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(
                context,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        if (alarmManager != null) {
            alarmManager.cancel(pendingAlarmIntent);
        }
        /* Alarm won't start again if device is rebooted */
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    public final boolean checkAlarm(Context context) {
        Intent alarmIntent = new Intent(context, NotifierReceiver.class);
        alarmIntent.setAction(this.ACTION_BD_NOTIFICATION);
        return PendingIntent.getBroadcast(
                context,
                0,
                alarmIntent,
                PendingIntent.FLAG_NO_CREATE) != null;
    }
}
