package com.example.planner.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import com.example.planner.R;
/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class TimeMarkerView extends MarkerView {

    private final TextView tvContent;

    public TimeMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(getTime(ce.getHigh()));
           // tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            tvContent.setText(getTime(e.getY()));
           // tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
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