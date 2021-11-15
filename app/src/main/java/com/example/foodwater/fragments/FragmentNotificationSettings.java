package com.example.foodwater.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.foodwater.R;
//import com.example.foodwater.helpers.AlarmHelper;
import com.example.foodwater.utils.AppUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;


public class FragmentNotificationSettings extends BottomSheetDialogFragment {

    private final Context context;
    private String currentToneUri;
    private int notificFrequency;
    private String notificMsg;
    private boolean notificStatus;
    private SharedPreferences sharedPref;
    View view1;

    public FragmentNotificationSettings(Context mCtx) {
        this.context = mCtx;
        this.notificMsg = "";
        this.currentToneUri = "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        boolean flagEat = args.getBoolean("flagEat", false);
        View view = inflater.inflate(R.layout.fragment_notification_settings, container, false);
        ConstraintLayout mainNotificationLayout = view.findViewById(R.id.mainNotificationLayout);
        if (flagEat == false) {
            mainNotificationLayout.setBackgroundResource(R.drawable.blue_bg);
        } else {
            mainNotificationLayout.setBackgroundResource(R.drawable.orange_bg);
        }
        view1 = view;
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref = context.getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE);


        EditText NotificationText = ((TextInputLayout) view.findViewById(R.id.etNotificationText)).getEditText();
        if (NotificationText != null) {
            NotificationText.setText(sharedPref.getString(
                    AppUtils.NOTIFICATION_MSG_KEY,
                    context.getResources().getString(R.string.pref_notification_message_value)
            ));
        }

        currentToneUri = sharedPref.getString(
                AppUtils.NOTIFICATION_TONE_URI_KEY,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
        );
        EditText Ringtone = ((TextInputLayout) view.findViewById(R.id.etRingtone)).getEditText();
        if (Ringtone != null) {
            Ringtone.setText(RingtoneManager.getRingtone(
                    context,
                    Uri.parse(this.currentToneUri)).getTitle(context)
            );
        }

        RadioRealButtonGroup ButtonNotificFrequency = view.findViewById(R.id.radioTime);
        ButtonNotificFrequency.setOnClickedButtonListener((new RadioRealButtonGroup.OnClickedButtonListener() {
            public final void onClickedButton(RadioRealButton button, int position) {
                switch (position) {
                    case 0:
                        notificFrequency = 30;
                        break;
                    case 1:
                        notificFrequency = 45;
                        break;
                    case 2:
                        notificFrequency = 60;
                        break;
                    default:
                        notificFrequency = 30;
                }
            }
        }));

        notificFrequency = sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30);
        switch (notificFrequency) {
            case 30:
                ButtonNotificFrequency.setPosition(0);
                break;
            case 45:
                ButtonNotificFrequency.setPosition(1);
                break;
            case 60:
                ButtonNotificFrequency.setPosition(2);
                break;
            default:
                notificFrequency = 30;
                ButtonNotificFrequency.setPosition(0);
        }

        notificStatus = sharedPref.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY, true);
        if (notificStatus) {
            ((Switch) view.findViewById(R.id.switchNotification)).setChecked(true);
        } else {
            ((Switch) view.findViewById(R.id.switchNotification)).setChecked(false);
        }

        Ringtone.setOnClickListener((new View.OnClickListener() {
            public final void onClick(View it) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Wybierz melodię:");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentToneUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 999);
            }
        }));

        Button ButtonNotificationOK = view.findViewById(R.id.buttonNotificationOK);
        ButtonNotificationOK.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View it) {
                notificStatus = ((Switch) view.findViewById(R.id.switchNotification)).isChecked();
                notificMsg = ((TextInputLayout) view.findViewById(R.id.etNotificationText)).getEditText().getText().toString();
                if (TextUtils.isEmpty(notificMsg)) {
                    Toast.makeText(context,
                            (CharSequence) "Wpisz treść powiadomień",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(AppUtils.NOTIFICATION_MSG_KEY, notificMsg);
                    editor.putInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, notificFrequency);
                    editor.putBoolean(AppUtils.NOTIFICATION_STATUS_KEY, notificStatus);
                    editor.apply();


//                    AlarmHelper alarmHelper = new AlarmHelper();
//                    if (notificStatus) {
//                        alarmHelper.cancelAlarm(context);
//                        alarmHelper.setAlarm(
//                                context,
//                                sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30)
//                        );
//                        Toast.makeText(
//                                context,
//                                "Powiadomienia co " + notificFrequency + " min",
//                                Toast.LENGTH_SHORT
//                        ).show();
//                    } else {
//                        alarmHelper.cancelAlarm(context);
//                        Toast.makeText(
//                                context,
//                                "Powiadomienia wyłączono",
//                                Toast.LENGTH_SHORT
//                        ).show();
//                    }
                    FragmentNotificationSettings.this.dismiss();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            currentToneUri = uri.toString();
            sharedPref.edit().putString(AppUtils.NOTIFICATION_TONE_URI_KEY, this.currentToneUri).apply();
            Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
            ((TextInputLayout) view1.findViewById(R.id.etRingtone)).getEditText().setText(ringtone.getTitle(context));
        }

    }

}


