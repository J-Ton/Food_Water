package com.example.foodwater.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class AppUtils {

    public static final String USERS_SHARED_PREF = "user_pref";
    public static final int PRIVATE_MODE = 0;
    public static final String WEIGHT_KEY = "weight"; // waga użytkownika
    public static final String AGE_KEY = "age"; // jego wiek
    public static final String WORK_TIME_KEY = "worktime"; // aktywność fizyczna
    public static final String SEX_KEY = "sex"; // płeć
    public static final String HEIGHT_KEY = "growth"; // wzrost
    public static final String MY_VALUES_KEY = "myvalues"; // flaga dla własnych wartości wody i jedzenia
    public static final String FIRST_RUN_KEY = "firstrun"; // flaga pierwszego uruchomienia
    public static final String TOTAL_INTAKE_KEY_WATER = "totalintakewater"; // cel dla wody
    public static final String TOTAL_INTAKE_KEY_EAT = "totalintakeeat";  // cel dla jedzenia
    public static final String NOTIFICATION_STATUS_KEY = "notificationstatus"; // on/off powiadomień
    public static final String NOTIFICATION_FREQUENCY_KEY = "notificationfrequency"; // okres czasowy powiadomień
    public static final String NOTIFICATION_MSG_KEY = "notificationmsg";// tekst powiadomienia
    public static final String NOTIFICATION_TONE_URI_KEY = "notificationtone";// melodia powiadomienia
    public static final String WEATHER_KEY = "weather";// pogoda

    public static final float calculate(String sex, int weight, float workTime, int height, int wiek, boolean flag, boolean hot) {
        float val;
        if (flag) {// dla jedzenia
            if (sex == "Mężczyzna")
                val = (float) ((66.47 + (13.7 * weight) + (5 * height) - (6.76 * wiek)) * workTime);
            else
                val = (float) ((655.1 + (9.567 * weight) + (1.85 * height) - (4.68 * wiek)) * workTime);
        } else {// dla wody
            if (sex == "Mężczyzna")
                val = weight * 35;
            else
                val = weight * 31;
            if (hot)
                val = (float) (val * 1.3);
        }
        return val;
    }


    public static final String getCurrentDate() {
        SimpleDateFormat dateform = new SimpleDateFormat("dd-MM-yyyy");
        return dateform.format(Calendar.getInstance().getTime());
    }
}


