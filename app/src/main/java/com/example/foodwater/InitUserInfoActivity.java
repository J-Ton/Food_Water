package com.example.foodwater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.foodwater.utils.AppUtils;
import com.google.android.material.textfield.TextInputLayout;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class InitUserInfoActivity extends AppCompatActivity {

    private TextInputLayout weight, growth, age;
    private Spinner workTime;
    private EditText et_weight, et_growth, et_age;
    private String sex = "Mężczyzna";
    private float physicalActivity;
    private InputMethodManager imm;
    private ConstraintLayout parent;
    private SharedPreferences sharedPref;
    private RadioRealButtonGroup gender, weather;
    private boolean hot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_user_info);

        initValues();

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

        Button btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                parent = (ConstraintLayout) findViewById(R.id.init_user_info_parent_layout);
                imm.hideSoftInputFromWindow(parent.getWindowToken(), 0);
                et_weight = weight.getEditText();
                et_growth = growth.getEditText();
                et_age = age.getEditText();

                calculateActivity((int) workTime.getSelectedItemId());

                if (et_weight.getText().toString().isEmpty() || et_growth.getText().toString().isEmpty() || et_age.getText().toString().isEmpty() || sex.isEmpty()) {
                    Toast.makeText(InitUserInfoActivity.this, "Wszystkie pola muszą być wypęłnione i zaznaczona płeć!", Toast.LENGTH_SHORT).show();
                } else {

                    if (Integer.parseInt(et_weight.getText().toString()) > 9 && Integer.parseInt(et_weight.getText().toString()) < 251) {
                        if (Integer.parseInt(et_age.getText().toString()) > 0 && Integer.parseInt(et_age.getText().toString()) < 131) {
                            if (Integer.parseInt(et_growth.getText().toString()) > 49 && Integer.parseInt(et_growth.getText().toString()) < 291) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                double water,eat;
                                water = AppUtils.calculate(sex, Integer.parseInt(et_weight.getText().toString()), physicalActivity, Integer.parseInt(et_growth.getText().toString()),Integer.parseInt(et_age.getText().toString()),false, hot);
                                eat = AppUtils.calculate(sex, Integer.parseInt(et_weight.getText().toString()), physicalActivity, Integer.parseInt(et_growth.getText().toString()),Integer.parseInt(et_age.getText().toString()),true, hot);
                                editor.putInt(AppUtils.TOTAL_INTAKE_KEY_WATER, (int) water);
                                editor.putInt(AppUtils.TOTAL_INTAKE_KEY_EAT, (int) eat);
                                editor.putInt(AppUtils.WEIGHT_KEY, Integer.parseInt(et_weight.getText().toString()));
                                editor.putInt(AppUtils.HEIGHT_KEY, Integer.parseInt(et_growth.getText().toString()));
                                editor.putInt(AppUtils.AGE_KEY, Integer.parseInt(et_age.getText().toString()));
                                editor.putInt(AppUtils.WORK_TIME_KEY, (int) workTime.getSelectedItemId());
                                editor.putString(AppUtils.SEX_KEY, sex);
                                editor.putBoolean(AppUtils.WEATHER_KEY, hot);
                                editor.putBoolean(AppUtils.MY_VALUES_KEY, false);
                                editor.putBoolean(AppUtils.FIRST_RUN_KEY, false);
                                editor.apply();
                                //Snackbar.make(v, "Ja workaju!", Snackbar.LENGTH_SHORT).show();
                                startActivity(new Intent(InitUserInfoActivity.this, MainActivity.class));
                                InitUserInfoActivity.this.finish();
                            } else {
                                Toast.makeText(InitUserInfoActivity.this, "Podaj poprawny wzrost! (50-290)", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(InitUserInfoActivity.this, "Podaj poprawny wiek! (1-130)", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InitUserInfoActivity.this, "Podaj poprawną wagę! (10-250)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initValues() {
        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE);
        weight = (TextInputLayout) findViewById(R.id.etWeight);
        growth = findViewById(R.id.etGrowth);
        age = findViewById(R.id.etAge);
        workTime = findViewById(R.id.physicalActivity_init);
        gender = findViewById(R.id.radioGroup_gender);
        gender.setPosition(0);
        weather = findViewById(R.id.radioGroup_weather);
        weather.setPosition(0);
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
                physicalActivity = 1;
                break;
        }
    }
}