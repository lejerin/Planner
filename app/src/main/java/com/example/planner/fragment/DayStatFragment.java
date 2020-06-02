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
import com.example.planner.Helpers.TimeStackedBarsMarkerView;
import com.example.planner.R;
import com.example.planner.Realm.Plans;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

public class DayStatFragment extends Fragment {

    private TextView selectday, focusTime, allTime, successPercent, allSet;
    private ImageButton beforeWeek, nextWeek;
    private ProgressBar progressBar;
    private PieChart dayPieChart;
    private BarChart successChart;

    private Realm realm;
    private RealmResults<Plans> plans;

    private Date thisDate = new Date();

    private int[] colors = new int[]{Color.rgb(253, 154, 188)
            , Color.rgb(171, 171, 171)};

    public DayStatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat_day, container, false);

        selectday = view.findViewById(R.id.selectMonthText);
        focusTime = view.findViewById(R.id.focusTime);
        allTime = view.findViewById(R.id.allTime);
        successPercent = view.findViewById(R.id.percentSuccess);
        allSet = view.findViewById(R.id.allSet);
        beforeWeek = view.findViewById(R.id.beforeWeek);
        nextWeek = view.findViewById(R.id.nextWeek);
        progressBar = view.findViewById(R.id.successProgressBar);
        dayPieChart = view.findViewById(R.id.dayPieChart);
        successChart = view.findViewById(R.id.monthBarChart);

        realm = Realm.getDefaultInstance();

      //  setDBdata();


        beforeWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeekDay(-1);
             //   setDBdata();
            }
        });

        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeekDay(1);
                //setDBdata();
            }
        });

        return view;
    }


//    private void setDBdata(){
//
//        //아침 오후
//        Date sunday = getSunDay();
//        Date satday = getSatday();
//
//        SimpleDateFormat transFormat = new SimpleDateFormat("MM월 dd일 EE요일");
//        selectday.setText(transFormat.format(sunday));
//
//        plans = realm.where(Plans.class).greaterThanOrEqualTo("startTime", sunday)
//                .lessThanOrEqualTo("endTime", satday)
//                .sort("startTime", Sort.ASCENDING).findAll();
//
//
//        setProgressBar();
//        makeTodayAllPlan(sunday,satday);
//        setSuccessBarChart(sunday);
//    }

    private void setProgressBar(){
        int success = 0, fail = 0;

        for (int i=0; i<plans.size();i++){
            if(plans.get(i).getSuccess() == 1){
                success++;
            }else{
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

//    //DB에서 당일 계획 갖고온 뒤 전체 원형 그래프 그리기
//    private void makeTodayAllPlan(Date startDate, Date endDate) {
//
//        dayPieChart.setRotationEnabled(false);
//        dayPieChart.setUsePercentValues(true);
//        dayPieChart.getDescription().setEnabled(false);
//        // pieChart.setExtraOffsets(5,10,5,5);
//        dayPieChart.setDragDecelerationFrictionCoef(0.95f);
//        dayPieChart.setDrawHoleEnabled(true);
//        dayPieChart.setHoleColor(Color.WHITE);
//        dayPieChart.setTransparentCircleRadius(50f);
//
//        dayPieChart.setHoleRadius(40f);
//
//
//        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
//
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//
//
//        if(plans.size()  > 0){
//
//            long diffDay = (plans.get(0).getStartTime().getTime() - startDate.getTime()) / (60*1000);
//            yValues.add(new PieEntry((float)diffDay,""));
//            colors.add(Color.rgb(209, 209, 209));
//
//
//            if(plans.size() > 1){
//                //2개 이상일경우
//                for (int i=0;i<plans.size();i++){
//                    long diffDay3 = (plans.get(i).getEndTime().getTime() - plans.get(i).getStartTime().getTime()) / (60*1000);
//                    yValues.add(new PieEntry((float)diffDay3,plans.get(i).getTitle()));
//                    colors.add(getColor(i));
//                    isPlanTimeList.add(true);
//                    holeString.add(changeDateToStr(plansRealmResults.get(i).getStartTime()) +
//                            "\n-\n" + changeDateToStr(plansRealmResults.get(i).getEndTime()));
//
//
//                    if(plansRealmResults.size() != i+1){
//                        long diffDay4 = (plansRealmResults.get(i+1).getStartTime().getTime() - plansRealmResults.get(i).getEndTime().getTime()) / (60*1000);
//                        yValues.add(new PieEntry((float)diffDay4,""));
//                        colors.add(Color.rgb(209, 209, 209));
//                        isPlanTimeList.add(false);
//                        holeString.add("");
//                    }
//
//                }
//            }else{
//                //1개 일경우
//
//                long diffDay3 = (plansRealmResults.get(0).getEndTime().getTime() - plansRealmResults.get(0).getStartTime().getTime()) / (60*1000);
//                yValues.add(new PieEntry((float)diffDay3,plansRealmResults.get(0).getTitle()));
//                colors.add(getColor(0));
//                isPlanTimeList.add(true);
//                holeString.add(changeDateToStr(plansRealmResults.get(0).getStartTime()) +
//                        "\n-\n" + changeDateToStr(plansRealmResults.get(0).getEndTime()));
//
//            }
//
//            long diffDay2 = (endDate.getTime() - (plansRealmResults.get(plansRealmResults.size()-1).getEndTime().getTime())) / (60*1000);
//            yValues.add(new PieEntry((float)diffDay2,""));
//            colors.add(Color.rgb(209, 209, 209));
//            isPlanTimeList.add(false);
//            holeString.add("");
//        }
//
//
//        pieChart.animateY(1000, Easing.EaseInOutCubic); //애니메이션
//
//        PieDataSet dataSet = new PieDataSet(yValues,"plan");
//        //dataSet.setSliceSpace(3f);
//        //dataSet.setSelectionShift(5f);
//
//
//        dataSet.setColors(colors);
//
//        PieData data = new PieData((dataSet));
//        data.setDrawValues(false);
//        data.setHighlightEnabled(false);
////        data.setValueTextSize(10f);
////        data.setValueTextColor(Color.YELLOW);
//
//        pieChart.setData(data);
//        // pieChart.setTouchEnabled(false);
//        pieChart.getLegend().setEnabled(false);
//
//    }
//
//    private void setSuccessBarChart(Date startDay){
//
//        ArrayList<BarEntry> data = new ArrayList<>();
//
//        Calendar day = Calendar.getInstance();
//        for(int i=0;i<7;i++){
//            day.setTime(startDay);
//            day.add(Calendar.DATE, i);
//
//            RealmResults<Plans> thisday = realm.where(Plans.class).equalTo("timeText", changeDateToStr(day.getTime()))
//                    .sort("startTime", Sort.ASCENDING).findAll();
//
//            float thisSuccess = 0.0f;
//            float thisFail = 0.0f;
//
//            for(int j=0;j<thisday.size();j++){
//
//                switch (thisday.get(j).getSuccess()){
//                    case 0:
//                        //미실행한 계획
//                        break;
//                    case 1:
//                        thisSuccess++;
//                        break;
//                    case 2:
//                        thisFail++;
//                        break;
//                    default:
//                        break;
//                }
//
//            }
//            data.add(new BarEntry(i, new float[]{thisSuccess,thisFail}));
//
//        }
//
//
//        BarDataSet barDataSet = new BarDataSet(data,"");
//        barDataSet.setColors(colors);
//        BarData barData = new BarData(barDataSet);
//        barData.setDrawValues(false);
//        barData.setBarWidth(0.5f);
//        successBarChart.setData(barData);
//
//        String[] weekLabel = new String[]{"일","월","화","수","목","금","토"};
//        successBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekLabel));
//
//        successBarChart.getAxisLeft().setValueFormatter(new SuccessYAxisValueFormatter());
//
//        XAxis x1 = successBarChart.getXAxis();
//        YAxis yl1 = successBarChart.getAxisLeft();
//        YAxis yr1 = successBarChart.getAxisRight();
//        x1.setPosition(XAxis.XAxisPosition.BOTTOM);
//        x1.setDrawAxisLine(false);
//        x1.setDrawGridLines(false);
//        x1.setDrawGridLinesBehindData(false);
//        yr1.setDrawLabels(false);
//        yr1.setDrawAxisLine(false);
//
//
////        yr1.setDrawGridLines(false);
////        yl1.setDrawGridLines(false);
////
////        yl1.setDrawLabels(false);
////        yl1.setDrawAxisLine(false);
//
//        //bardata1.setHighlightEnabled(false);
//
//        // successBarChart.setExtraTopOffset(30);
//        successBarChart.setMarker(new SuccessStackedBarsMarkerView(getContext(),R.layout.custom_marker_view_layout));
//
//        successBarChart.getDescription().setEnabled(false);
//        successBarChart.getLegend().setEnabled(false);
//        successBarChart.setScaleEnabled(false);
//        successBarChart.setDoubleTapToZoomEnabled(false);
//
//
//        successBarChart.animateXY(1000, 1000);
//        successBarChart.invalidate();
//
//
//
//
//    }

    //현재 날짜 토요일
    private Date getSatday(){

        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDate);

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
