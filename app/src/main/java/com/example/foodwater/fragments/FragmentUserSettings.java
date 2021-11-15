package com.example.foodwater.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.foodwater.MainActivity;
import com.example.foodwater.R;
import com.example.foodwater.helpers.SqliteHelper;
import com.example.foodwater.utils.AppUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;


public final class FragmentUserSettings extends BottomSheetDialogFragment {

    private SharedPreferences sharedPref;
    private final Context mCtx;
    private Float physicalActivity;
    private RadioRealButtonGroup gender, weather;
    private String sex, dateNow;
    private Button buttonOk;
    private Spinner spiner;
    private SqliteHelper sqliteHelper;
    private boolean myValues, flagEat, hot;
    private int item_id, currentWater, currentEat;
    private Switch custSwitch;
    private EditText et_weight, et_age, et_growth, et_cust_water, et_cust_aet;
    private TextInputLayout weight, age, growth, cust_water, cust_aet;

    public FragmentUserSettings(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        flagEat = args.getBoolean("flagEat", false);
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);
        ConstraintLayout mainUserLayout = view.findViewById(R.id.mainUserLayout);
        if (flagEat == false) {
            mainUserLayout.setBackgroundResource(R.drawable.blue_bg);
        } else {
            mainUserLayout.setBackgroundResource(R.drawable.orange_bg);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initValues(view);

        gender.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                switch (position) {
                    case 0:
                        sex = "Mężczyzna";
                        break;
                    case 1:
                        sex = "Kobieta";
                        break;
                    default:
                        break;
                }
            }
        });

        custSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cust_water.setEnabled(true);
                    cust_aet.setEnabled(true);
                    myValues = true;
                } else {
                    cust_water.setEnabled(false);
                    cust_aet.setEnabled(false);
                    myValues = false;
                }
            }
        });

        if (hot == false) {

            weather.setPosition(0);
        } else {
            weather.setPosition(1);
        }

        weather.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                switch (position) {
                    case 0:
                        hot = false;
                        break;
                    case 1:
                        hot = true;
                        break;
                    default:
                        break;
                }
            }
        });


        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                et_weight = weight.getEditText();
                et_age = age.getEditText();
                et_growth = growth.getEditText();
                item_id = spiner.getSelectedItemPosition();
                if (et_weight.getText().toString().isEmpty() || et_growth.getText().toString().isEmpty() || et_age.getText().toString().isEmpty() || sex.isEmpty()) {
                    Toast.makeText(mCtx, "Pola muszą być wypęłnione i zaznaczona płeć!", Toast.LENGTH_SHORT).show();
                } else {
                    if (Integer.parseInt(et_weight.getText().toString()) > 9 && Integer.parseInt(et_weight.getText().toString()) < 251) {
                        if (Integer.parseInt(et_age.getText().toString()) > 0 && Integer.parseInt(et_age.getText().toString()) < 131) {
                            if (Integer.parseInt(et_growth.getText().toString()) > 49 && Integer.parseInt(et_growth.getText().toString()) < 291) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                sqliteHelper = new SqliteHelper(mCtx);

                                calculateActivity((int) spiner.getSelectedItemId());
                                editor.putInt(AppUtils.WEIGHT_KEY, Integer.parseInt(et_weight.getText().toString()));
                                editor.putInt(AppUtils.HEIGHT_KEY, Integer.parseInt(et_growth.getText().toString()));
                                editor.putInt(AppUtils.AGE_KEY, Integer.parseInt(et_age.getText().toString()));
                                editor.putInt(AppUtils.WORK_TIME_KEY, (int) spiner.getSelectedItemId());
                                editor.putString(AppUtils.SEX_KEY, sex);
                                editor.putBoolean(AppUtils.WEATHER_KEY, hot);
                                editor.putBoolean(AppUtils.MY_VALUES_KEY, myValues);
                                if (custSwitch.isChecked()) {
                                    et_cust_aet = cust_aet.getEditText();
                                    et_cust_water = cust_water.getEditText();
                                    if (currentWater == Integer.parseInt(et_cust_water.getText().toString())) {

                                    } else {
                                        editor.putInt(AppUtils.TOTAL_INTAKE_KEY_WATER, Integer.parseInt(et_cust_water.getText().toString()));
                                        sqliteHelper.updateTotalIntake(dateNow, Integer.parseInt(et_cust_water.getText().toString()), false);
                                    }
                                    if (currentEat == Integer.parseInt(et_cust_aet.getText().toString())) {

                                    } else {
                                        editor.putInt(AppUtils.TOTAL_INTAKE_KEY_EAT, Integer.parseInt(et_cust_aet.getText().toString()));
                                        sqliteHelper.updateTotalIntake(dateNow, Integer.parseInt(et_cust_aet.getText().toString()), true);
                                    }
                                } else {
                                    float water = AppUtils.calculate(sex, Integer.parseInt(et_weight.getText().toString()), physicalActivity, Integer.parseInt(et_growth.getText().toString()), Integer.parseInt(et_age.getText().toString()), false, hot);
                                    float eat = AppUtils.calculate(sex, Integer.parseInt(et_weight.getText().toString()), physicalActivity, Integer.parseInt(et_growth.getText().toString()), Integer.parseInt(et_age.getText().toString()), true, hot);
                                    if (currentWater == (int) water) {

                                    } else {
                                        editor.putInt(AppUtils.TOTAL_INTAKE_KEY_WATER, (int) water);
                                        sqliteHelper.updateTotalIntake(dateNow, (int) water, false);
                                    }

                                    if (currentEat == (int) eat) {

                                    } else {
                                        editor.putInt(AppUtils.TOTAL_INTAKE_KEY_EAT, (int) eat);
                                        sqliteHelper.updateTotalIntake(dateNow, (int) eat, true);
                                    }
                                }
                                editor.apply();
                                FragmentUserSettings.this.dismiss();
                                MainActivity newValues = (MainActivity) FragmentUserSettings.this.getActivity();
                                newValues.updateValues(flagEat);
                            } else {
                                Toast.makeText(mCtx, "Podaj poprawny wzrost! (50-290)", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mCtx, "Podaj poprawny wiek! (1-130)", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mCtx, "Podaj poprawną wagę! (10-250)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void calculateActivity(int item) {
        switch (item) {
            case 0:
                physicalActivity = (float) 1.2;
                break;
            case 1:
                physicalActivity = (float) 1.38;
                break;
            case 2:
                physicalActivity = (float) 1.46;
                break;
            case 3:
                physicalActivity = (float) 1.5;
                break;
            case 4:
                physicalActivity = (float) 1.55;
                break;
            case 5:
                physicalActivity = (float) 1.6;
                break;
            case 6:
                physicalActivity = (float) 1.64;
                break;
            case 7:
                physicalActivity = (float) 1.73;
                break;
            case 8:
                physicalActivity = (float) 1.80;
                break;
            default:
                physicalActivity = (float) 1;
                break;
        }
    }

    private void initValues(View view) {
        dateNow = AppUtils.getCurrentDate();
        buttonOk = view.findViewById(R.id.buttonSettingsOK);
        sharedPref = mCtx.getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE);
        weight = view.findViewById(R.id.etWeight);
        age = view.findViewById(R.id.etAge);
        growth = view.findViewById(R.id.etGrowth2);
        gender = view.findViewById(R.id.radioGroup_settings);
        spiner = view.findViewById(R.id.physicalActivity_settings);
        custSwitch = view.findViewById(R.id.switchCustom);
        cust_water = view.findViewById(R.id.etCustom_water);
        cust_aet = view.findViewById(R.id.etCustom_eat);

        et_cust_aet = cust_aet.getEditText();
        et_cust_water = cust_water.getEditText();
        et_weight = weight.getEditText();
        et_age = age.getEditText();
        et_growth = growth.getEditText();
        item_id = this.sharedPref.getInt(AppUtils.WORK_TIME_KEY, 0);
        sex = this.sharedPref.getString(AppUtils.SEX_KEY, "Mężczyzna");
        hot = this.sharedPref.getBoolean(AppUtils.WEATHER_KEY, false);
        myValues = this.sharedPref.getBoolean(AppUtils.MY_VALUES_KEY, false);

        switch (sex) {
            case "Mężczyzna":
                gender.setPosition(0);
                break;
            case "Kobieta":
                gender.setPosition(1);
                break;
            default:
                break;
        }

        if (myValues) {
            custSwitch.setChecked(true);
            cust_water.setEnabled(true);
            cust_aet.setEnabled(true);
        } else {
            custSwitch.setChecked(false);
            cust_water.setEnabled(false);
            cust_aet.setEnabled(false);
        }

        currentWater = this.sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY_WATER, 0);
        currentEat = this.sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY_EAT, 0);
        StringBuilder sBilder1 = (new StringBuilder()).append("");
        et_weight.setText((CharSequence) sBilder1.append(this.sharedPref.getInt(AppUtils.WEIGHT_KEY, 0)).toString());
        StringBuilder sBilder2 = (new StringBuilder()).append("");
        et_age.setText((CharSequence) sBilder2.append(this.sharedPref.getInt(AppUtils.AGE_KEY, 0)).toString());
        StringBuilder sBilder3 = (new StringBuilder()).append("");
        et_growth.setText((CharSequence) sBilder3.append(this.sharedPref.getInt(AppUtils.HEIGHT_KEY, 0)).toString());
        StringBuilder sBilder4 = (new StringBuilder()).append("");
        et_cust_water.setText((CharSequence) sBilder4.append(this.sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY_WATER, 0)).toString());
        StringBuilder sBilder5 = (new StringBuilder()).append("");
        et_cust_aet.setText((CharSequence) sBilder5.append(this.sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY_EAT, 0)).toString());

        spiner.setSelection(item_id);
        weather = view.findViewById(R.id.radioGroup_weather);
    }

}