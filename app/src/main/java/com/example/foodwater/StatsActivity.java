package com.example.foodwater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.example.foodwater.helpers.SqliteHelper;
import com.example.foodwater.utils.AppUtils;
import com.example.foodwater.utils.ChartFormatterUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import me.itangqi.waveloadingview.WaveLoadingView;


public class StatsActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private SqliteHelper sqliteHelper;
    float[] percentList;
    int daysCount;

    ConstraintLayout mainLayout;
    WaveLoadingView LevelView;
    Spinner spinner;
    Date todayDate, dateCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Intent intent = getIntent();
        final boolean flagEat = intent.getBooleanExtra("key", false);

        sharedPref = this.getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE);
        sqliteHelper = new SqliteHelper((Context) this);
        mainLayout = findViewById(R.id.mainLayout);
        LevelView = findViewById(R.id.LevelView);
        spinner = findViewById(R.id.spinner);

        ImageView buttonBack = findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        setColors(flagEat);
        analizeStats(flagEat);
        setRemaining(flagEat);
        setTarget(flagEat);
        setAvgProgres();
        makeCraph(flagEat, 7);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch ((int)id){
                    case 0:
                        makeCraph(flagEat, 7);
                        break;
                    case 1:
                        makeCraph(flagEat, 14);
                        break;
                    case 2:
                        makeCraph(flagEat, 30);
                        break;
                    case 3:
                        makeCraph(flagEat, 90);
                        break;
                    case 4:
                        makeCraph(flagEat, 365);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void analizeStats(boolean flagEat) {
        Cursor cursor = sqliteHelper.getAllStats(flagEat);
        if (cursor.moveToFirst()) {
            daysCount = cursor.getCount();
            percentList = new float[daysCount];
            for (int i = 0; i < daysCount; ++i) {
                float percent = (float) cursor.getInt(2) / (float) cursor.getInt(3) * 100;
                percentList[i] = percent;
                cursor.moveToNext();
            }
        } else {
            Toast.makeText(this, (CharSequence) "Empty", Toast.LENGTH_LONG).show();
        }
    }

    private void setColors(boolean flagEat) {
        if (flagEat == false) {
            mainLayout.setBackgroundResource(R.drawable.ic_water_bg);
            LevelView.setBorderColor(ResourcesCompat.getColor(getResources(), R.color.Blue, null));
            LevelView.setWaveColor(ResourcesCompat.getColor(getResources(), R.color.BlueDark, null));
            LevelView.setCenterTitleStrokeColor(ResourcesCompat.getColor(getResources(), R.color.BlueDark, null));
        } else {
            LevelView.setBorderColor(ResourcesCompat.getColor(getResources(), R.color.Orange, null));
            LevelView.setWaveColor(ResourcesCompat.getColor(getResources(), R.color.OrangeDark, null));
            LevelView.setCenterTitleStrokeColor(ResourcesCompat.getColor(getResources(), R.color.OrangeDark, null));
            mainLayout.setBackgroundResource(R.drawable.ic_eat_bg);
        }
    }

    private void setAvgProgres() {
        float summ = 0;
        for (int i = 0; i < daysCount; ++i) {
            summ += percentList[i];

        }
        int percentage = (int) (summ / daysCount);
        WaveLoadingView WaveLoadingView = findViewById(R.id.LevelView);
        WaveLoadingView.setCenterTitle("" + percentage + '%');
        WaveLoadingView.setProgressValue((int) (percentage * 0.85));
    }

    private void setTarget(boolean flagEat) {
        TextView targetIntake = findViewById(R.id.targetIntake);
        if (flagEat)
            targetIntake.setText(sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY_EAT, 1) + " kkal");
        else
            targetIntake.setText(sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY_WATER, 1) + " ml");
    }

    private void setRemaining(boolean flagEat) {
        TextView remainingIntake = findViewById(R.id.remainingIntake);
        int remaining;
        String unit;
        if (flagEat) {
            remaining = sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY_EAT, 1) -
                    sqliteHelper.getIntake(AppUtils.getCurrentDate(), flagEat);
            unit = "kkal";
        } else {
            remaining = sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY_WATER, 1) -
                    sqliteHelper.getIntake(AppUtils.getCurrentDate(), flagEat);
            unit = "ml";
        }
        if (remaining > 0) {
            remainingIntake.setText(remaining + unit);
        } else {
            remainingIntake.setText("0" + unit);
        }
    }

    void makeCraph(boolean flagEat, int period) {
        ArrayList entries = new ArrayList();
        ArrayList dateArray = new ArrayList();
        Cursor cursor = sqliteHelper.getAllStats(flagEat);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); ++i) {
                dateArray.add(cursor.getString(1));
                if(calculateDiff(AppUtils.getCurrentDate(), cursor.getString(1)) <= period) {
                    float percent = (float) cursor.getInt(2) /
                            (float) cursor.getInt(3) * 100;
                    entries.add(new Entry((float) i, percent));
                }
                cursor.moveToNext();
            }
        } else {
            Toast.makeText(this,  "Empty", Toast.LENGTH_LONG).show();
        }

        if (!entries.isEmpty()) {
            LineChart chart = this.findViewById(R.id.chart);
            chart.getDescription().setEnabled(false);
            chart.animateY(1000, Easing.Linear);
            chart.getViewPortHandler().setMaximumScaleX(1.5F);
            chart.getXAxis().setDrawGridLines(false);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setGranularityEnabled(true);
            chart.getLegend().setEnabled(false);
            chart.fitScreen();
            chart.setAutoScaleMinMaxEnabled(true);
            chart.setScaleX(1.0F);
            chart.setPinchZoom(true);
            chart.setScaleXEnabled(true);
            chart.setScaleYEnabled(false);
            chart.getAxisLeft().setTextColor(Color.BLACK);
            chart.getXAxis().setTextColor(Color.BLACK);
            chart.getAxisLeft().setDrawAxisLine(false);
            chart.getXAxis().setDrawAxisLine(false);
            chart.setDrawMarkers(false);
            chart.getXAxis().setLabelCount(5);
            YAxis rightAxix = chart.getAxisRight();
            rightAxix.setDrawGridLines(false);
            rightAxix.setDrawZeroLine(false);
            rightAxix.setDrawAxisLine(false);
            rightAxix.setDrawLabels(false);

            LineDataSet dataSet = new LineDataSet(entries, "Label");
            dataSet.setDrawCircles(true);
            dataSet.setCircleColor(Color.BLACK);

            dataSet.setCircleRadius(4f);
            dataSet.setCircleHoleRadius(1f);
            dataSet.setLineWidth(2.5F);
            if (flagEat == false) {
                dataSet.setColor(ContextCompat.getColor(this, R.color.BlueDark));
            } else {
                dataSet.setColor(ContextCompat.getColor(this, R.color.OrangeDark));
            }
            dataSet.setDrawFilled(true);
            dataSet.setFillDrawable(this.getDrawable(R.drawable.graph_fill_gradiant));
            dataSet.setDrawValues(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData lineData = new LineData(dataSet);
            chart.getXAxis().setValueFormatter(new ChartFormatterUtils(dateArray));
            chart.setData(lineData);
            chart.invalidate();
        }
    }

    long calculateDiff(String today, String cursor){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            todayDate = sdf.parse(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            dateCursor = sdf.parse(cursor);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diffInMillies = Math.abs(todayDate.getTime() - dateCursor.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}