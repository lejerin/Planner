package com.example.planner.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.planner.Helpers.MyYAxisValueFormatter;
import com.example.planner.Helpers.SuccessStackedBarsMarkerView;
import com.example.planner.Helpers.SuccessYAxisValueFormatter;
import com.example.planner.Helpers.TimeMarkerView;
import com.example.planner.R;
import com.example.planner.Realm.Plans;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MonthStatFragment extends Fragment {

    private TextView selectMonth, focusTime, allTime, successPercent, allSet;
    private ImageButton beforeWeek, nextWeek;
    private ProgressBar progressBar;
    private LineChart TimeLineChart;
    private BarChart successChart;

    private Realm realm;
    private RealmResults<Plans> plans;

    private Date thisDate = new Date();

    private int[] colors = new int[]{Color.rgb(235,162,201)
            , Color.rgb(189, 189, 189)};

    public MonthStatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat_month, container, false);

        selectMonth = view.findViewById(R.id.selectMonthText);
        focusTime = view.findViewById(R.id.focusTime);
        allTime = view.findViewById(R.id.allTime);
        successPercent = view.findViewById(R.id.percentSuccess);
        allSet = view.findViewById(R.id.allSet);
        beforeWeek = view.findViewById(R.id.beforeWeek);
        nextWeek = view.findViewById(R.id.nextWeek);
        progressBar = view.findViewById(R.id.successProgressBar);
        TimeLineChart = view.findViewById(R.id.lineChart);
        successChart = view.findViewById(R.id.monthBarChart);

        realm = Realm.getDefaultInstance();

        setDBdata();


        beforeWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeekDay(-1);
                setDBdata();
            }
        });

        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeekDay(1);
                setDBdata();
            }
        });

        return view;
    }


    private void setDBdata(){

        //첫날 ~ 마지막날
        Date firstDay = getFirstDay();
        Date lastDay = getLastDay();

        SimpleDateFormat transFormat = new SimpleDateFormat("MM월");
        selectMonth.setText(transFormat.format(firstDay));


        plans = realm.where(Plans.class).greaterThanOrEqualTo("startTime", firstDay)
                .lessThanOrEqualTo("endTime", lastDay)
                .sort("startTime", Sort.ASCENDING).findAll();


        setProgressBar();
        setTimeBarChart(firstDay,lastDay);
        //setSuccessBarChart(success,fail);

    }



    private void setProgressBar(){
        int success = 0, fail = 0;

        for (int i=0; i<plans.size();i++){
            if(plans.get(i).getSuccess() == 1){
                success++;
            }else if(plans.get(i).getSuccess() == 2){
                fail++;
            }
        }





        int percent =  (int)( (double)success/ (double)(success+fail) * 100.0 );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(percent,true);
        }else{
            progressBar.setProgress(percent);
        }

        successPercent.setText("성공률: "+percent + "%");
        allSet.setText("총 세트: " + (success +fail));

    }

    private void setTimeBarChart(Date startDay, Date endDay){

        ArrayList<Entry> values = new ArrayList<>();

        //막대그래프
        ArrayList<BarEntry> data = new ArrayList<>();

        values.add(new Entry(0, 0.0f));

        Calendar day = Calendar.getInstance();

        float ft = 0.0f, af = 0.0f;
        int allCount = 0;


        //1~7일 데이터
        day.setTime(startDay);
        day.add(Calendar.SECOND,604799);
        RealmResults<Plans> thisday = plans.where().greaterThanOrEqualTo("startTime",startDay)
                .lessThan("endTime",day.getTime()).findAll();


        float thisTime = 0.0f;
        float thisFocus = 0.0f;
        float thisSuccess = 0.0f;
        float thisFail = 0.0f;

        for(int i=0;i<thisday.size();i++){
            if(thisday.get(i).getSuccess() != 0){

                thisTime += thisday.get(i).getDuration();
                thisFocus += thisday.get(i).getFocus();

                if(thisday.get(i).getSuccess() == 1){
                    thisSuccess += 1.0f;
                }else{
                    thisFail += 1.0f;
                }

            }

        }


        values.add(new Entry(1, thisTime));
        data.add(new BarEntry(0, new float[]{thisSuccess,thisFail}));
        ft+= thisTime;
        af += thisFocus;
        allCount += thisday.size();

        //8일~14일 데이터
        thisTime = 0.0f;
        thisFocus = 0.0f;
        thisSuccess = 0.0f;
        thisFail = 0.0f;

        startDay = day.getTime();

        day.add(Calendar.SECOND,604799);
        thisday = plans.where().greaterThanOrEqualTo("startTime",startDay)
                .lessThan("endTime",day.getTime()).findAll();


        for(int i=0;i<thisday.size();i++){
            if(thisday.get(i).getSuccess() != 0){

                thisTime += thisday.get(i).getDuration();
                thisFocus += thisday.get(i).getFocus();

                if(thisday.get(i).getSuccess() == 1){
                    thisSuccess += 1.0f;
                }else{
                    thisFail += 1.0f;
                }

            }

        }

        //집중시간만
        values.add(new Entry(2, thisTime));
        data.add(new BarEntry(1, new float[]{thisSuccess,thisFail}));
        ft+= thisTime;
        af += thisFocus;
        allCount += thisday.size();


        //15일~21일 데이터
        thisTime = 0.0f;
        thisFocus = 0.0f;
        thisSuccess = 0.0f;
        thisFail = 0.0f;

        startDay = day.getTime();

        day.add(Calendar.SECOND,604799);
        thisday = plans.where().greaterThanOrEqualTo("startTime",startDay)
                .lessThan("endTime",day.getTime()).findAll();


        for(int i=0;i<thisday.size();i++){
            if(thisday.get(i).getSuccess() != 0){

                thisTime += thisday.get(i).getDuration();
                thisFocus += thisday.get(i).getFocus();
                if(thisday.get(i).getSuccess() == 1){
                    thisSuccess += 1.0f;
                }else{
                    thisFail += 1.0f;
                }

            }

        }

        //집중시간만
        values.add(new Entry(3, thisTime));
        data.add(new BarEntry(2, new float[]{thisSuccess,thisFail}));
        ft+= thisTime;
        af += thisFocus;
        allCount += thisday.size();


        //22일~마지막날 데이터 (+29,30,31)
        thisTime = 0.0f;
        thisFocus = 0.0f;
        thisSuccess = 0.0f;
        thisFail = 0.0f;

        startDay = day.getTime();

        thisday = plans.where().greaterThanOrEqualTo("startTime",startDay)
                .lessThan("endTime",endDay).findAll();


        for(int i=0;i<thisday.size();i++){
            if(thisday.get(i).getSuccess() != 0){

                thisTime += thisday.get(i).getDuration();
                thisFocus += thisday.get(i).getFocus();

                if(thisday.get(i).getSuccess() == 1){
                    thisSuccess += 1.0f;
                }else{
                    thisFail += 1.0f;
                }
            }

        }

        //집중시간만
        values.add(new Entry(4, thisTime));
        data.add(new BarEntry(3, new float[]{thisSuccess,thisFail}));
        ft+= thisTime;
        af += thisFocus;
        allCount += thisday.size();

        //평균 집중력
        int mf = (int) (af/allCount);


        values.add(new Entry(5, 0.0f));

        focusTime.setText(getIntToTimeString((int)ft));
        allTime.setText(mf +"%");


        System.out.println("바데이터" + data);
        setSuccessChart(data);

        LineDataSet lineDataSet = new LineDataSet(values,"");


        lineDataSet.setDrawIcons(false);
        //lineDataSet.setColor(Color.TRANSPARENT);
        int lineFillColor = Color.rgb(127,199,44);

        lineDataSet.setCircleColors(Color.TRANSPARENT,lineFillColor,lineFillColor,lineFillColor,lineFillColor,Color.TRANSPARENT);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawVerticalHighlightIndicator(false);

//        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//
//        lineDataSet.setDrawFilled(true);
//        lineDataSet.setFillColor(getColor(mf));


        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        lineDataSet.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getContext(), getColor(mf));
            lineDataSet.setFillDrawable(drawable);
        }
        else {
            lineDataSet.setFillColor(lineFillColor);
        }


        lineDataSet.setColor(lineFillColor);
        LineData lineData = new LineData(lineDataSet);
        TimeLineChart.setData(lineData);



        String[] weekLabel = new String[]{"","1주차","2주차","3주차","4주차",""};
        TimeLineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekLabel));

        TimeLineChart.getAxisLeft().setValueFormatter(new MyYAxisValueFormatter());

        //TimeLineChart.getAxisLeft().setValueFormatter(new SuccessYAxisValueFormatter());

        XAxis x1 = TimeLineChart.getXAxis();
        YAxis yl1 = TimeLineChart.getAxisLeft();
        YAxis yr1 = TimeLineChart.getAxisRight();
        x1.setPosition(XAxis.XAxisPosition.BOTTOM);
        x1.setLabelCount(5);
        x1.setDrawAxisLine(false);
        x1.setDrawGridLines(false);
        x1.setDrawGridLinesBehindData(false);
        yr1.setDrawLabels(false);
        yr1.setDrawAxisLine(false);

//        yr1.setDrawGridLines(false);
//        yl1.setDrawGridLines(false);
//
//        yl1.setDrawLabels(false);
//        yl1.setDrawAxisLine(false);

        //bardata1.setHighlightEnabled(false);

        // successBarChart.setExtraTopOffset(30);
        TimeLineChart.setMarker(new TimeMarkerView(getContext(),R.layout.custom_marker_view_layout));
        TimeLineChart.getAxisLeft().setAxisMinimum(0.0f);
        TimeLineChart.getDescription().setEnabled(false);
        TimeLineChart.getLegend().setEnabled(false);
        TimeLineChart.setScaleEnabled(false);
        TimeLineChart.setDoubleTapToZoomEnabled(false);


        TimeLineChart.animateXY(1000, 1000);
        TimeLineChart.invalidate();

    }

    private void setSuccessChart(ArrayList<BarEntry> data) {

        System.out.println("새 바데이터 " + data);

        BarDataSet barDataSet = new BarDataSet(data,"");
        barDataSet.setColors(colors);
        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);
        barData.setBarWidth(0.3f);
        successChart.setData(barData);


        String[] weekLabel = new String[]{"1주차","2주차","3주차","4주차"};
        successChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekLabel));

        successChart.getAxisLeft().setValueFormatter(new SuccessYAxisValueFormatter());

        XAxis x1 = successChart.getXAxis();
        YAxis yl1 = successChart.getAxisLeft();
        YAxis yr1 = successChart.getAxisRight();
        x1.setPosition(XAxis.XAxisPosition.BOTTOM);
        x1.setLabelCount(data.size());
        x1.setDrawAxisLine(false);
        x1.setDrawGridLines(false);
        x1.setDrawGridLinesBehindData(false);
        yr1.setDrawLabels(false);
        yr1.setDrawAxisLine(false);


//        yr1.setDrawGridLines(false);
//        yl1.setDrawGridLines(false);
//
//        yl1.setDrawLabels(false);
//        yl1.setDrawAxisLine(false);

        //bardata1.setHighlightEnabled(false);

        // successBarChart.setExtraTopOffset(30);
        successChart.setMarker(new SuccessStackedBarsMarkerView(getContext(),R.layout.custom_marker_view_layout));

        successChart.getDescription().setEnabled(false);
        successChart.getLegend().setEnabled(false);
        successChart.setScaleEnabled(false);
        successChart.setDoubleTapToZoomEnabled(false);
        successChart.getAxisLeft().setAxisMinimum(0.0f);

        successChart.animateXY(1000, 1000);
        successChart.invalidate();


    }
    private int getColor(int af) {

        System.out.println("평균 집중력" + af);
        //평균 집중력
        if(af >= 90){
            return R.drawable.gradiant5;
        }
        if(af >= 70){
            return R.drawable.gradiant4;
        }
        if(af >= 50){
            return R.drawable.gradiant3;
        }
        if(af >= 30){
            return R.drawable.gradiant2;
        }


        return R.drawable.gradiant1;
    }

    //현재 날짜 토요일
    private Date getLastDay(){

        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        String start = changeDateToStr(cal.getTime())  + " 23:59:59";
        SimpleDateFormat form = new SimpleDateFormat("EE, MM월 dd일 yyyy년 HH:mm:ss");
        Date startDate = new Date();
        try {
            startDate = form.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return startDate;

    }


    //날짜(Date) -> String
    private String changeDateToStr(Date date) {
        SimpleDateFormat transFormat = new SimpleDateFormat("EE, MM월 dd일 yyyy년");
        return transFormat.format(date);
    }

    //현재 날짜 일요일
    private Date getFirstDay(){

        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDate);
//        cal.add(Calendar.DAY_OF_WEEK, selectWeek);
//        cal.setFirstDayOfWeek(Calendar.MONDAY);

        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DAY_OF_MONTH));


        String start = changeDateToStr(cal.getTime())  + " 00:00:00";
        SimpleDateFormat form = new SimpleDateFormat("EE, MM월 dd일 yyyy년 HH:mm:ss");
        Date startDate = new Date();
        try {
            startDate = form.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return startDate;
    }


    private String getIntToTimeString(int duration){

        long getHour = duration / (60 * 60);
        String hour = String.valueOf(getHour);

        // 분단위
        long getMin = duration - (getHour*(60 * 60)) ;
        String min = String.valueOf(getMin / (60)); // 몫

        // 초단위
        String second = String.valueOf((getMin % (60))); // 나머지

        // 시간이 한자리면 0을 붙인다
        if (hour.length() == 1) {
            hour = "0" + hour;
        }

        // 분이 한자리면 0을 붙인다
        if (min.length() == 1) {
            min = "0" + min;
        }

        // 초가 한자리면 0을 붙인다
        if (second.length() == 1) {
            second = "0" + second;
        }

        return hour+"시간 " + min + "분";
    }

    private void getWeekDay(int amount){
        Calendar week = Calendar.getInstance();
        week.setTime(thisDate);
        week.add(Calendar.MONTH , amount);

        thisDate = week.getTime();

    }

}
