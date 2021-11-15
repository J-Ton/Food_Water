package com.example.foodwater.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;

public final class ChartFormatterUtils extends ValueFormatter {

    private final ArrayList dateArray;

    public ChartFormatterUtils(ArrayList dateArray) {
        super();
        this.dateArray = dateArray;
    }

    public String getAxisLabel(float value,  AxisBase axis) {
        String label;
        try{
            label = (String) dateArray.get((int)value);
        } catch (IndexOutOfBoundsException e ){
            label = "";
        }
        return label;
    }
}

