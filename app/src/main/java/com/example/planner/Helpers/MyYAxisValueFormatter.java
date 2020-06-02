package com.example.planner.Helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class MyYAxisValueFormatter extends ValueFormatter
{

    private final DecimalFormat mFormat;


    public MyYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");

    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {


        int time = (int)value;

        if(time==0){
            return "0m";
        }

       int getHour = time/(60*60);
       int getMin = (time - (getHour*(60*60)));


        String min = String.valueOf(getMin / (60)); // 몫
        // 분이 한자리면 0을 붙인다
        if (min.length() == 1 && getHour != 0) {
            min = "0" + min;
        }

        String hour = String.valueOf(getHour);
        // 시간이 한자리면 0을 붙인다
        if(hour.equals("0")){
            return min+"m";

        }


    return hour +"h"+min+"m";
    }
}