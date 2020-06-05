package com.example.planner.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.planner.Helpers.MyYAxisValueFormatter;
import com.example.planner.Helpers.SuccessStackedBarsMarkerView;
import com.example.planner.Helpers.SuccessYAxisValueFormatter;
import com.example.planner.Helpers.TimeMarkerView;
import com.example.planner.R;
import com.example.planner.Realm.Plans;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class WeekStatFragment extends Fragment {

    private TextView startWeek, endWeek, focusTime, allTime, successPercent, allSet;
    private ImageButton beforeWeek, nextWeek;
    private ProgressBar progressBar;
    private BarChart timeBarChart,successBarChart;

    private Realm realm;
    private RealmResults<Plans> plans;

    private Date thisDate = new Date();



    public WeekStatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat_week, container, false);

        startWeek = view.findViewById(R.id.startWeek);
        endWeek = view.findViewById(R.id.endWeek);
        focusTime = view.findViewById(R.id.focusTime);
        allTime = view.findViewById(R.id.allTime);
        successPercent = view.findViewById(R.id.percentSuccess);
        allSet = view.findViewById(R.id.allSet);
        beforeWeek = view.findViewById(R.id.beforeWeek);
        nextWeek = view.findViewById(R.id.nextWeek);
        progressBar = view.findViewById(R.id.successProgressBar);
        timeBarChart = view.findViewById(R.id.lineChart);
        successBarChart = view.findViewById(R.id.successPieChart);

        realm = Realm.getDefaultInstance();

        setDBdata();


        beforeWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeekDay(-7);
                setDBdata();
            }
        });

        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeekDay(7);
                setDBdata();
            }
        });

        return view;
    }


    private void setDBdata(){

        //일요일부터 토요일까지 데이터를 갖고옴
        Date sunday = getSunDay();
        Date satday = getSatday();

        SimpleDateFormat transFormat = new SimpleDateFormat("MM월 dd일 EE요일");
        startWeek.setText(transFormat.format(sunday));
        endWeek.setText(transFormat.format(satday));

        plans = realm.where(Plans.class).greaterThanOrEqualTo("startTime", sunday)
                .lessThanOrEqualTo("endTime", satday)
                .sort("startTime", Sort.ASCENDING).findAll();


        setProgressBar();
        setTimeBarChart(sunday);
        setSuccessBarChart(sunday);
    }

    private void setProgressBar(){
        int success = 0, fail = 0;
        int focus = 0;

        for (int i=0; i<plans.size();i++){
            if(plans.get(i).getSuccess() == 1){
                success++;
            }else if(plans.get(i).getSuccess() == 2){
                fail++;
            }
            focus += plans.get(i).getFocus();
        }

        if(focus != 0){
            allTime.setText((int)focus/plans.size() + "%");
        }else{
            allTime.setText("0%");
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

    private void setTimeBarChart(Date startDay){

        ArrayList<BarEntry> data = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        Calendar day = Calendar.getInstance();

        float at = 0.0f;
        int af = 0;


        for(int i=0;i<7;i++){
            day.setTime(startDay);
            day.add(Calendar.DATE, i);

            RealmResults<Plans> thisday = realm.where(Plans.class).equalTo("timeText", changeDateToStr(day.getTime()))
                    .sort("startTime", Sort.ASCENDING).findAll();

            float thistime = 0.0f;
            float thisfocus = 0.0f;

            for(int j=0;j<thisday.size();j++){

                //미실행한 계획이 아닐경우에만
                if(thisday.get(j).getSuccess() != 0){

                    thistime += thisday.get(j).getDuration();
                    thisfocus += thisday.get(j).getFocus();
                  }
            }

            data.add(new BarEntry(i, new float[]{thistime}));
            System.out.println(thisfocus);
            int aa = (int) (thisfocus/thisday.size());
            colors.add(getBarColor(aa));
            at+= thistime;
            af += aa;
        }


        focusTime.setText(getIntToTimeString((int)at));


        BarDataSet barDataSet = new BarDataSet(data,"");
        barDataSet.setColors(colors);
        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);
        barData.setBarWidth(0.5f);
        timeBarChart.setData(barData);

        String[] weekLabel = new String[]{"일","월","화","수","목","금","토"};
        timeBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekLabel));

        timeBarChart.getAxisLeft().setValueFormatter(new MyYAxisValueFormatter());


        XAxis x1 = timeBarChart.getXAxis();
        YAxis yl1 = timeBarChart.getAxisLeft();
        YAxis yr1 = timeBarChart.getAxisRight();
        x1.setPosition(XAxis.XAxisPosition.BOTTOM);
        x1.setDrawAxisLine(false);
        x1.setDrawGridLines(false);
        x1.setDrawGridLinesBehindData(false);
        yr1.setDrawLabels(false);
        yr1.setDrawAxisLine(false);

        //bardata1.setHighlightEnabled(false);

        timeBarChart.setMarker(new TimeMarkerView(getContext(),R.layout.custom_marker_view_layout));


        timeBarChart.getAxisLeft().setAxisMinimum(0.0f);
        timeBarChart.getDescription().setEnabled(false);
        timeBarChart.getLegend().setEnabled(false);
        timeBarChart.setScaleEnabled(false);
        timeBarChart.setDoubleTapToZoomEnabled(false);
        timeBarChart.animateXY(1000, 1000);
        timeBarChart.invalidate();




    }

    private int getBarColor(int af) {

        System.out.println("평균 집중력" + af);
        //평균 집중력
        if(af >= 90){
            return Color.rgb(60,124,31);
        }
        if(af >= 70){
            return Color.rgb(88,147,56);
        }
        if(af >= 50){
            return Color.rgb(125,177,89);
        }
        if(af >= 30){
            return Color.rgb(167,210,127);
        }


        return Color.rgb(201,238,157);
    }

    private void setSuccessBarChart(Date startDay){

        ArrayList<BarEntry> data = new ArrayList<>();

        Calendar day = Calendar.getInstance();
        for(int i=0;i<7;i++){
            day.setTime(startDay);
            day.add(Calendar.DATE, i);

            RealmResults<Plans> thisday = realm.where(Plans.class).equalTo("timeText", changeDateToStr(day.getTime()))
                    .sort("startTime", Sort.ASCENDING).findAll();

            float thisSuccess = 0.0f;
            float thisFail = 0.0f;

            for(int j=0;j<thisday.size();j++){

                switch (thisday.get(j).getSuccess()){
                    case 0:
                        //미실행한 계획
                        break;
                    case 1:
                        thisSuccess++;
                        break;
                    case 2:
                        thisFail++;
                        break;
                    default:
                        break;
                }

            }
            data.add(new BarEntry(i, new float[]{thisSuccess,thisFail}));

        }


        BarDataSet barDataSet = new BarDataSet(data,"");
        int[] colors = new int[]{Color.rgb(253, 154, 188)
                , Color.rgb(171, 171, 171)};
        barDataSet.setColors(colors);
        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);
        barData.setBarWidth(0.5f);
        successBarChart.setData(barData);

        String[] weekLabel = new String[]{"일","월","화","수","목","금","토"};
        successBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekLabel));

        successBarChart.getAxisLeft().setValueFormatter(new SuccessYAxisValueFormatter());

        XAxis x1 = successBarChart.getXAxis();
        YAxis yl1 = successBarChart.getAxisLeft();
        YAxis yr1 = successBarChart.getAxisRight();
        x1.setPosition(XAxis.XAxisPosition.BOTTOM);
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
        successBarChart.setMarker(new SuccessStackedBarsMarkerView(getContext(),R.layout.custom_marker_view_layout));

        successBarChart.getAxisLeft().setAxisMinimum(0.0f);

        successBarChart.getDescription().setEnabled(false);
        successBarChart.getLegend().setEnabled(false);
        successBarChart.setScaleEnabled(false);
        successBarChart.setDoubleTapToZoomEnabled(false);


        successBarChart.animateXY(1000, 1000);
        successBarChart.invalidate();




    }

    //현재 날짜 토요일
    private Date getSatday(){

       Calendar cal = Calendar.getInstance();
       cal.setTime(thisDate);
       cal.add(Calendar.DATE, 7 - cal.get(Calendar.DAY_OF_WEEK));

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
    private Date getSunDay(){

        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDate);
//        cal.add(Calendar.DAY_OF_WEEK, selectWeek);
//        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, 1 - cal.get(Calendar.DAY_OF_WEEK));

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
        week.add(Calendar.DATE , amount);

        thisDate = week.getTime();

    }

}
