package com.example.planner.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.planner.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressWarnings("unused")
@SuppressLint("ViewConstructor")
public class TimeStackedBarsMarkerView extends MarkerView {

    private TextView tvContent;

    public TimeStackedBarsMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof BarEntry) {

            BarEntry be = (BarEntry) e;

            if(be.getYVals() != null) {

                // draw the stack value
                tvContent.setText(getTime(be.getYVals()[highlight.getStackIndex()]));

              //  tvContent.setText(Utils.formatNumber(, 0, true));
            } else {
                tvContent.setText(Utils.formatNumber(be.getY(), 0, true));
            }
        } else {

            tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }


    private String getTime(float v){
        int time = (int)v;

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