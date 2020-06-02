package com.example.planner.Helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class SuccessYAxisValueFormatter extends ValueFormatter
{

    private final DecimalFormat mFormat;

    private String beforeNum;

    public SuccessYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");

    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        int iV = (int)value;

        if(iV < 1){
            return "";
        }



        if(beforeNum != null && mFormat.format(iV).equals(beforeNum)){
            return "";
        }


        beforeNum = mFormat.format(iV);

        return mFormat.format(iV);
    }
}