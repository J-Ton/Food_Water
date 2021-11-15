package com.example.foodwater.recievers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.foodwater.helpers.AlarmHelper;
import com.example.foodwater.utils.AppUtils;

public class BootReceiver extends BroadcastReceiver {
    private final AlarmHelper alarm = new AlarmHelper();

    @Override
    public void onReceive( Context context, Intent intent) {
        if (intent != null && intent.getAction() != null){
            if (intent.getAction() == "android.intent.action.BOOT_COMPLETED") {
                SharedPreferences prefs = context.getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE);
                int notificationFrequency = prefs.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 60);
                boolean notificationsNewMessage = prefs.getBoolean("notifications_new_message", true);
                this.alarm.cancelAlarm(context);
                if (notificationsNewMessage) {
                    this.alarm.setAlarm(context, (long)notificationFrequency);
                }
            }
        }
    }
}

