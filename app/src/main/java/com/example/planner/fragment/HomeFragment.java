package com.example.planner.fragment;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.planner.Activities.TimerActivity;
import com.example.planner.Helpers.Tree;
import com.example.planner.R;
import com.example.planner.Realm.Plans;
import com.example.planner.Realm.User;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/*
HomeFragment : 당일 원형 계획표, 타이머 시작, 성장나무


 */
public class HomeFragment extends Fragment {

    //원형 그래프(PieChart): MpAndroidChart 라이브러리 사용 https://github.com/PhilJay/MPAndroidChart
    //Canvas로 직접 그리는 방법 구현하다가 글씨 추가를 못해서 지움 : Helpers.PieChartView
    private PieChart pieChart;
    private RelativeLayout timerBtn;

    //Realm DB
    private Realm realm;
    private RealmResults<Plans> plansRealmResults;
    private RealmResults<Plans> plansRealmAmResults;
    private RealmResults<Plans> plansRealmPmResults;

    private ArrayList<Boolean> isPlanTimeList = new ArrayList<>();
    private ArrayList<Boolean> isPlanAmTimeList = new ArrayList<>();
    private ArrayList<Boolean> isPlanPmTimeList = new ArrayList<>();

    private ArrayList<String> holeString = new ArrayList<>();
    private ArrayList<String> holeAmString = new ArrayList<>();
    private ArrayList<String> holePmString = new ArrayList<>();

    private TextView holeTextView,timeStatus, timeTop, timeRight, timeBottom, timeLeft;
    private String todayNotYear= "";

    private TextView startPlanTitle;

    private ImageView tree;

    //test
    private PieChart amPie;
    private PieChart pmPie;
    private PieChart centerPie;

    boolean isAnimating = false;

    private Plans nowTimePlan, nextTimePlan;

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        realm = Realm.getDefaultInstance();

        //init view item
        pieChart = view.findViewById(R.id.piechart);
        holeTextView = view.findViewById(R.id.selectTime);
        amPie = view.findViewById(R.id.AM);
        pmPie = view.findViewById(R.id.PM);
        timeTop = view.findViewById(R.id.timeTop);
        timeRight = view.findViewById(R.id.timeRight);
        timeBottom = view.findViewById(R.id.timeBottom);
        timeLeft = view.findViewById(R.id.timeLeft);
        timeStatus = view.findViewById(R.id.timeStatus);
        timerBtn = view.findViewById(R.id.timerBtn);
        startPlanTitle = view.findViewById(R.id.startPlanTitle);
        tree = view.findViewById(R.id.tree);

        amPie.setTouchEnabled(false);
        pmPie.setTouchEnabled(false);
        centerPie = pieChart;

        //오늘 날짜 init
        SimpleDateFormat transFormat = new SimpleDateFormat("EE\nMM월 dd일");
        todayNotYear = transFormat.format(new Date());
        holeTextView.setText(todayNotYear);

        //원형 그래프 그리기
        makeTodayAllPlan();
        makeTodayAmPlan();
        makeTodayPmPlan();


        //현재 시간과 일치하는 일정 찾기
        nowTimePlan = findNowPlan();
        timerBtn.setVisibility(View.INVISIBLE);
        if(nowTimePlan != null ){
            if(nowTimePlan.getSuccess() == 0){
                //아직 시도안한 계획이면
                timerBtn.setVisibility(View.VISIBLE);
                startPlanTitle.setText(nowTimePlan.getTitle() + " 타이머를 시작해주세요");
            }
        }


        //사용자 보상 데이터 갖고오기
        User user = realm.where(User.class).findFirst();
        Tree treeData = new Tree();
        tree.setImageResource(treeData.getNowTree(user.getSuccess(), user.getTime()));



        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                타이머로 계획 정보 전달
//                기본키, 이름, 날짜, 시작시간, 종료시간, 총 소요시간
//                private id
//                private String title, timeText;
//                private Date startTime, endTime;
//                private int duration;

                if(nowTimePlan != null) {
                    Intent timerActivity = new Intent(getContext(), TimerActivity.class);
                    timerActivity.putExtra("id", nowTimePlan.getId());
                    timerActivity.putExtra("title", nowTimePlan.getTitle());
                    timerActivity.putExtra("timeText", nowTimePlan.getTimeText());
                    timerActivity.putExtra("startTime", nowTimePlan.getStartTime());
                    timerActivity.putExtra("endTime", nowTimePlan.getEndTime());
                    timerActivity.putExtra("duration", nowTimePlan.getDuration());
                    if(nextTimePlan != null){
                        timerActivity.putExtra("next", nextTimePlan.getStartTime());
                    }
                    startActivity(timerActivity);
                }
            }
        });


        amPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("am");
                if(isAnimating)
                    return;
                isAnimating=true;
                timeBackgroundInvisible();

                float x1, y1, x2, y2;
                x1 = getRelativeX(amPie);//Use v1.getX() if v1 & v2 have same parent
                y1 = getRelativeY(amPie);//Use v1.getY() if v1 & v2 have same parent
                x2 = getRelativeX(centerPie);//Use v2.getX() if v1 & v2 have same parent
                y2 = getRelativeY(centerPie);//Use v2.getY() if v1 & v2 have same parent

                float x_displacement = (x2-x1);
                float y_displacement = (y2-y1);


                amPie.animate().xBy(x_displacement).yBy(y_displacement);
                centerPie.animate().xBy(-x_displacement).yBy(-y_displacement);
                long anim_duration = amPie.animate().getDuration();

                new CountDownTimer(anim_duration + 10, anim_duration + 10) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        isAnimating=false;
                        centerPie.setTouchEnabled(false);
                        centerPie = amPie;
                        centerPie.setTouchEnabled(true);
                        timeBackgroundinit("am");
                    }
                }.start();

            }

            //returns x-pos relative to root layout
            private float getRelativeX(View myView) {
                if (myView.getParent() == myView.getRootView())
                    return myView.getX();
                else
                    return myView.getX() + getRelativeX((View) myView.getParent());
            }

            //returns y-pos relative to root layout
            private float getRelativeY(View myView) {
                if (myView.getParent() == myView.getRootView())
                    return myView.getY();
                else
                    return myView.getY() + getRelativeY((View) myView.getParent());
            }
        });

        pmPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAnimating)
                    return;
                isAnimating=true;
                timeBackgroundInvisible();

                float x1, y1, x2, y2;
                x1 = getRelativeX(pmPie);//Use v1.getX() if v1 & v2 have same parent
                y1 = getRelativeY(pmPie);//Use v1.getY() if v1 & v2 have same parent
                x2 = getRelativeX(centerPie);//Use v2.getX() if v1 & v2 have same parent
                y2 = getRelativeY(centerPie);//Use v2.getY() if v1 & v2 have same parent

                float x_displacement = (x2-x1);
                float y_displacement = (y2-y1);

                pmPie.animate().xBy(x_displacement).yBy(y_displacement);
                centerPie.animate().xBy(-x_displacement).yBy(-y_displacement);
                long anim_duration = pmPie.animate().getDuration();

                new CountDownTimer(anim_duration + 10, anim_duration + 10) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        isAnimating=false;
                        centerPie.setTouchEnabled(false);
                        centerPie = pmPie;
                        centerPie.setTouchEnabled(true);
                        timeBackgroundinit("pm");
                    }
                }.start();

            }

            //returns x-pos relative to root layout
            private float getRelativeX(View myView) {
                if (myView.getParent() == myView.getRootView())
                    return myView.getX();
                else
                    return myView.getX() + getRelativeX((View) myView.getParent());
            }

            //returns y-pos relative to root layout
            private float getRelativeY(View myView) {
                if (myView.getParent() == myView.getRootView())
                    return myView.getY();
                else
                    return myView.getY() + getRelativeY((View) myView.getParent());
            }
        });

        pieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAnimating)
                    return;
                isAnimating=true;
                timeBackgroundInvisible();

                float x1, y1, x2, y2;
                x1 = getRelativeX(pieChart);//Use v1.getX() if v1 & v2 have same parent
                y1 = getRelativeY(pieChart);//Use v1.getY() if v1 & v2 have same parent
                x2 = getRelativeX(centerPie);//Use v2.getX() if v1 & v2 have same parent
                y2 = getRelativeY(centerPie);//Use v2.getY() if v1 & v2 have same parent

                float x_displacement = (x2-x1);
                float y_displacement = (y2-y1);

                pieChart.animate().xBy(x_displacement).yBy(y_displacement);
                centerPie.animate().xBy(-x_displacement).yBy(-y_displacement);
                long anim_duration = pieChart.animate().getDuration();

                new CountDownTimer(anim_duration + 10, anim_duration + 10) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        isAnimating=false;
                        centerPie.setTouchEnabled(false);
                        centerPie = pieChart;
                        centerPie.setTouchEnabled(true);
                        timeBackgroundinit("all");
                    }
                }.start();

            }

            //returns x-pos relative to root layout
            private float getRelativeX(View myView) {
                if (myView.getParent() == myView.getRootView())
                    return myView.getX();
                else
                    return myView.getX() + getRelativeX((View) myView.getParent());
            }

            //returns y-pos relative to root layout
            private float getRelativeY(View myView) {
                if (myView.getParent() == myView.getRootView())
                    return myView.getY();
                else
                    return myView.getY() + getRelativeY((View) myView.getParent());
            }
        });

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                System.out.println(h);
                if(!isPlanTimeList.get((int)h.getX())){
                    pieChart.getData().setHighlightEnabled(false);
                    holeTextView.setText(todayNotYear);
                }else{
                    pieChart.getData().setHighlightEnabled(true);
                    holeTextView.setText(holeString.get((int)h.getX()));

                }


            }

            @Override
            public void onNothingSelected() {
                holeTextView.setText(todayNotYear);



            }
        });

        amPie.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                System.out.println(h);
                if(!isPlanAmTimeList.get((int)h.getX())){
                    amPie.getData().setHighlightEnabled(false);
                    holeTextView.setText(todayNotYear);
                }else{
                    amPie.getData().setHighlightEnabled(true);
                    holeTextView.setText(holeAmString.get((int)h.getX()));

                }


            }

            @Override
            public void onNothingSelected() {
                holeTextView.setText(todayNotYear);



            }
        });

        pmPie.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                System.out.println(h);
                if(!isPlanPmTimeList.get((int)h.getX())){
                    pmPie.getData().setHighlightEnabled(false);
                    holeTextView.setText(todayNotYear);
                }else{
                    pmPie.getData().setHighlightEnabled(true);
                    holeTextView.setText(holePmString.get((int)h.getX()));

                }


            }

            @Override
            public void onNothingSelected() {
                holeTextView.setText(todayNotYear);



            }
        });

        return view;
    }

    private Plans findNowPlan() {

        SimpleDateFormat transFormat = new SimpleDateFormat("EE, MM월 dd일 yyyy년");


        Plans nowPlan = realm.where(Plans.class).equalTo("timeText",transFormat.format(new Date()))
                .lessThanOrEqualTo("startTime",new Date())
                .greaterThanOrEqualTo("endTime",new Date()).findFirst();
//
//        Plans nowPlan = plansRealmResults.where()
//                .lessThanOrEqualTo("startTime",new Date())
//                .greaterThanOrEqualTo("endTime",new Date()).findFirst();

        if(nowPlan != null){
            nextTimePlan = plansRealmResults.where().greaterThan("startTime",nowPlan.getEndTime()).findFirst();

        }


        return nowPlan;
    }

    //DB에서 당일 계획 갖고온 뒤 전체 원형 그래프 그리기
    private void makeTodayAllPlan() {

        pieChart.setRotationEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
       // pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(50f);

        pieChart.setHoleRadius(40f);

        //DB에서 데이터 갖고오기
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();


        SimpleDateFormat transFormat = new SimpleDateFormat("EE, MM월 dd일 yyyy년");
        String today = transFormat.format(new Date());

        plansRealmResults = realm.where(Plans.class).equalTo("timeText",transFormat.format(new Date()))
                .sort("startTime", Sort.ASCENDING).findAll();


        String start = today  + " 00:00:00";
        String end = today +  " 23:59:59";
        SimpleDateFormat form = new SimpleDateFormat("EE, MM월 dd일 yyyy년 HH:mm:ss");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = form.parse(start);
            endDate = form.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<Integer> colors = new ArrayList<Integer>();


        if(plansRealmResults.size()  > 0){

            long diffDay = (plansRealmResults.get(0).getStartTime().getTime() - startDate.getTime()) / (60*1000);
            yValues.add(new PieEntry((float)diffDay,""));
            colors.add(Color.rgb(209, 209, 209));
            isPlanTimeList.add(false);
            holeString.add("");

            if(plansRealmResults.size() > 1){
                //2개 이상일경우
                for (int i=0;i<plansRealmResults.size();i++){
                    long diffDay3 = (plansRealmResults.get(i).getEndTime().getTime() - plansRealmResults.get(i).getStartTime().getTime()) / (60*1000);
                    yValues.add(new PieEntry((float)diffDay3,plansRealmResults.get(i).getTitle()));
                    colors.add(getColor(i));
                    isPlanTimeList.add(true);
                    holeString.add(changeDateToStr(plansRealmResults.get(i).getStartTime()) +
                            "\n-\n" + changeDateToStr(plansRealmResults.get(i).getEndTime()));


                    if(plansRealmResults.size() != i+1){
                        long diffDay4 = (plansRealmResults.get(i+1).getStartTime().getTime() - plansRealmResults.get(i).getEndTime().getTime()) / (60*1000);
                        yValues.add(new PieEntry((float)diffDay4,""));
                        colors.add(Color.rgb(209, 209, 209));
                        isPlanTimeList.add(false);
                        holeString.add("");
                    }

                }
            }else{
                //1개 일경우

                    long diffDay3 = (plansRealmResults.get(0).getEndTime().getTime() - plansRealmResults.get(0).getStartTime().getTime()) / (60*1000);
                    yValues.add(new PieEntry((float)diffDay3,plansRealmResults.get(0).getTitle()));
                    colors.add(getColor(0));
                    isPlanTimeList.add(true);
                    holeString.add(changeDateToStr(plansRealmResults.get(0).getStartTime()) +
                        "\n-\n" + changeDateToStr(plansRealmResults.get(0).getEndTime()));

            }

            long diffDay2 = (endDate.getTime() - (plansRealmResults.get(plansRealmResults.size()-1).getEndTime().getTime())) / (60*1000);
            yValues.add(new PieEntry((float)diffDay2,""));
            colors.add(Color.rgb(209, 209, 209));
            isPlanTimeList.add(false);
            holeString.add("");
        }


        pieChart.animateY(1000, Easing.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"plan");
        //dataSet.setSliceSpace(3f);
        //dataSet.setSelectionShift(5f);


        dataSet.setColors(colors);

        PieData data = new PieData((dataSet));
        data.setDrawValues(false);
        data.setHighlightEnabled(false);
//        data.setValueTextSize(10f);
//        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
       // pieChart.setTouchEnabled(false);
        pieChart.getLegend().setEnabled(false);

    }

    //DB에서 당일 계획 갖고온 뒤 오전 원형 그래프 그리기
    private void makeTodayAmPlan() {

        amPie.setRotationEnabled(false);
        amPie.setUsePercentValues(true);
        amPie.getDescription().setEnabled(false);
        // pieChart.setExtraOffsets(5,10,5,5);
        amPie.setDragDecelerationFrictionCoef(0.95f);
        amPie.setDrawHoleEnabled(true);
        amPie.setHoleColor(Color.WHITE);
        amPie.setTransparentCircleRadius(50f);

        amPie.setHoleRadius(40f);

        //DB에서 데이터 갖고오기
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();


        SimpleDateFormat transFormat = new SimpleDateFormat("EE, MM월 dd일 yyyy년");
        String today = transFormat.format(new Date());

        String start = today  + " 00:00:00";
        String end = today +  " 11:59:59";
        SimpleDateFormat form = new SimpleDateFormat("EE, MM월 dd일 yyyy년 HH:mm:ss");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = form.parse(start);
            endDate = form.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //오전 12시전
        plansRealmAmResults = realm.where(Plans.class).equalTo("timeText",transFormat.format(new Date()))
                .lessThanOrEqualTo("endTime",endDate)
                .sort("startTime", Sort.ASCENDING).findAll();

        //오전과 오후에 걸친 일정
        Plans more = realm.where(Plans.class).equalTo("timeText",transFormat.format(new Date()))
                .lessThanOrEqualTo("startTime",endDate)
                .greaterThan("endTime",endDate).findFirst();

        ArrayList<Integer> colors = new ArrayList<Integer>();

        if(plansRealmAmResults.size() == 0){
            if(more != null){

                long diffDay = (more.getStartTime().getTime() - startDate.getTime()) / (60*1000);
                yValues.add(new PieEntry((float)diffDay,""));
                colors.add(Color.rgb(209, 209, 209));
                isPlanAmTimeList.add(false);
                holeAmString.add("");


                long diffDay2 = (endDate.getTime() - more.getStartTime().getTime() )/ (60*1000);
                yValues.add(new PieEntry((float)diffDay2, more.getTitle()));
                colors.add(Color.rgb(132, 209, 123));
                isPlanAmTimeList.add(true);
                holeAmString.add(changeDateToStr(more.getStartTime()) +
                        "\n-\n" + changeDateToStr(more.getEndTime()));

            }
        }else{

            long diffDay = (plansRealmAmResults.get(0).getStartTime().getTime() - startDate.getTime()) / (60*1000);
            yValues.add(new PieEntry((float)diffDay,""));
            colors.add(Color.rgb(209, 209, 209));
            isPlanAmTimeList.add(false);
            holeAmString.add("");

            if(plansRealmAmResults.size() > 1){
                //2개 이상일경우
                for (int i=0;i<plansRealmAmResults.size();i++){
                    long diffDay3 = (plansRealmAmResults.get(i).getEndTime().getTime() - plansRealmAmResults.get(i).getStartTime().getTime()) / (60*1000);
                    yValues.add(new PieEntry((float)diffDay3,plansRealmAmResults.get(i).getTitle()));
                    colors.add(getColor(i));
                    isPlanAmTimeList.add(true);
                    holeAmString.add(changeDateToStr(plansRealmAmResults.get(i).getStartTime()) +
                            "\n-\n" + changeDateToStr(plansRealmAmResults.get(i).getEndTime()));


                    if(plansRealmAmResults.size() != i+1){
                        long diffDay4 = (plansRealmAmResults.get(i+1).getStartTime().getTime() - plansRealmAmResults.get(i).getEndTime().getTime()) / (60*1000);
                        yValues.add(new PieEntry((float)diffDay4,""));
                        colors.add(Color.rgb(209, 209, 209));
                        isPlanAmTimeList.add(false);
                        holeAmString.add("");
                    }

                }
            }else{
                //1개 일경우

                long diffDay3 = (plansRealmAmResults.get(0).getEndTime().getTime() - plansRealmAmResults.get(0).getStartTime().getTime()) / (60*1000);
                yValues.add(new PieEntry((float)diffDay3,plansRealmAmResults.get(0).getTitle()));
                colors.add(getColor(0));
                isPlanAmTimeList.add(true);
                holeAmString.add(changeDateToStr(plansRealmAmResults.get(0).getStartTime()) +
                        "\n-\n" + changeDateToStr(plansRealmAmResults.get(0).getEndTime()));

            }

            if(more == null){
                long diffDay2 = (endDate.getTime() - (plansRealmAmResults.get(plansRealmAmResults.size()-1).getEndTime().getTime())) / (60*1000);
                yValues.add(new PieEntry((float)diffDay2,""));
                colors.add(Color.rgb(209, 209, 209));
                isPlanAmTimeList.add(false);
                holeAmString.add("");
            }else{

                long diffDay3 = (more.getStartTime().getTime() - (plansRealmAmResults.get(plansRealmAmResults.size()-1).getEndTime().getTime())) / (60*1000);
                yValues.add(new PieEntry((float)diffDay3,""));
                colors.add(Color.rgb(209, 209, 209));
                isPlanAmTimeList.add(false);
                holeAmString.add("");

                long diffDay2 = (endDate.getTime() - more.getStartTime().getTime() )/ (60*1000);
                yValues.add(new PieEntry((float)diffDay2, more.getTitle()));
                colors.add(Color.rgb(132, 209, 123));
                isPlanAmTimeList.add(true);
                holeAmString.add(changeDateToStr(more.getStartTime()) +
                        "\n-\n" + changeDateToStr(more.getEndTime()));
            }
        }


        amPie.animateY(1000, Easing.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"plan");
        //dataSet.setSliceSpace(3f);
        //dataSet.setSelectionShift(5f);


        dataSet.setColors(colors);

        PieData data = new PieData((dataSet));
        data.setDrawValues(false);
        data.setHighlightEnabled(false);
//        data.setValueTextSize(10f);
//        data.setValueTextColor(Color.YELLOW);

        amPie.setData(data);
        // pieChart.setTouchEnabled(false);
        amPie.getLegend().setEnabled(false);

    }

    //DB에서 당일 계획 갖고온 뒤 오후 원형 그래프 그리기
    private void makeTodayPmPlan() {

        pmPie.setRotationEnabled(false);
        pmPie.setUsePercentValues(true);
        pmPie.getDescription().setEnabled(false);
        // pieChart.setExtraOffsets(5,10,5,5);
        pmPie.setDragDecelerationFrictionCoef(0.95f);
        pmPie.setDrawHoleEnabled(true);
        pmPie.setHoleColor(Color.WHITE);
        pmPie.setTransparentCircleRadius(50f);

        pmPie.setHoleRadius(40f);

        //DB에서 데이터 갖고오기
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();


        SimpleDateFormat transFormat = new SimpleDateFormat("EE, MM월 dd일 yyyy년");
        String today = transFormat.format(new Date());

        String start = today  + " 12:00:00";
        String end = today +  " 23:59:59";
        SimpleDateFormat form = new SimpleDateFormat("EE, MM월 dd일 yyyy년 HH:mm:ss");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = form.parse(start);
            endDate = form.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //오전 12시전
        plansRealmPmResults = realm.where(Plans.class).equalTo("timeText",transFormat.format(new Date()))
                .lessThanOrEqualTo("endTime",endDate)
                .greaterThanOrEqualTo("startTime",startDate)
                .sort("startTime", Sort.ASCENDING).findAll();

        //오전과 오후에 걸친 일정
        Plans more = realm.where(Plans.class).equalTo("timeText",transFormat.format(new Date()))
                .greaterThan("endTime",startDate)
                .lessThan("startTime",startDate).findFirst();

        ArrayList<Integer> colors = new ArrayList<Integer>();

        if(plansRealmPmResults.size() == 0){
            if(more != null){
                long diffDay = (more.getEndTime().getTime() - startDate.getTime()) / (60 * 1000);
                yValues.add(new PieEntry((float) diffDay, more.getTitle()));
                colors.add(Color.rgb(132, 209, 123));
                isPlanPmTimeList.add(true);
                holePmString.add(changeDateToStr(more.getStartTime()) +
                        "\n-\n" + changeDateToStr(more.getEndTime()));

                long diffDay1 = (endDate.getTime() - more.getEndTime().getTime()) / (60 * 1000);
                yValues.add(new PieEntry((float) diffDay1, ""));
                colors.add(Color.rgb(209, 209, 209));
                isPlanPmTimeList.add(false);
                holePmString.add("");

            }
        }else{

            if(more != null){
                long diffDay1 = (more.getEndTime().getTime() - startDate.getTime()) / (60 * 1000);
                yValues.add(new PieEntry((float) diffDay1, more.getTitle()));
                colors.add(Color.rgb(132, 209, 123));
                isPlanPmTimeList.add(true);
                holePmString.add(changeDateToStr(more.getStartTime()) +
                        "\n-\n" + changeDateToStr(more.getEndTime()));

                long diffDay = (plansRealmPmResults.get(0).getStartTime().getTime() - more.getEndTime().getTime()) / (60 * 1000);
                yValues.add(new PieEntry((float) diffDay, ""));
                colors.add(Color.rgb(209, 209, 209));
                isPlanPmTimeList.add(false);
                holePmString.add("");

            }else{
                long diffDay = (plansRealmPmResults.get(0).getStartTime().getTime() - startDate.getTime()) / (60 * 1000);
                yValues.add(new PieEntry((float) diffDay, ""));
                colors.add(Color.rgb(209, 209, 209));
                isPlanPmTimeList.add(false);
                holePmString.add("");
            }

            if(plansRealmPmResults.size() > 1){
                //2개 이상일경우
                for (int i=0;i<plansRealmPmResults.size();i++){
                    long diffDay3 = (plansRealmPmResults.get(i).getEndTime().getTime() - plansRealmPmResults.get(i).getStartTime().getTime()) / (60*1000);
                    yValues.add(new PieEntry((float)diffDay3,plansRealmPmResults.get(i).getTitle()));
                    colors.add(getColor(i));
                    isPlanPmTimeList.add(true);
                    holePmString.add(changeDateToStr(plansRealmPmResults.get(i).getStartTime()) +
                            "\n-\n" + changeDateToStr(plansRealmPmResults.get(i).getEndTime()));


                    if(plansRealmPmResults.size() != i+1){
                        long diffDay4 = (plansRealmPmResults.get(i+1).getStartTime().getTime() - plansRealmPmResults.get(i).getEndTime().getTime()) / (60*1000);
                        yValues.add(new PieEntry((float)diffDay4,""));
                        colors.add(Color.rgb(209, 209, 209));
                        isPlanPmTimeList.add(false);
                        holePmString.add("");
                    }

                }
            }else{
                //1개 일경우

                long diffDay3 = (plansRealmPmResults.get(0).getEndTime().getTime() - plansRealmPmResults.get(0).getStartTime().getTime()) / (60*1000);
                yValues.add(new PieEntry((float)diffDay3,plansRealmPmResults.get(0).getTitle()));
                colors.add(getColor(0));
                isPlanPmTimeList.add(true);
                holePmString.add(changeDateToStr(plansRealmPmResults.get(0).getStartTime()) +
                        "\n-\n" + changeDateToStr(plansRealmPmResults.get(0).getEndTime()));

            }


            long diffDay2 = (endDate.getTime() - (plansRealmPmResults.get(plansRealmPmResults.size()-1).getEndTime().getTime())) / (60*1000);
            yValues.add(new PieEntry((float)diffDay2,""));
            colors.add(Color.rgb(209, 209, 209));
            isPlanPmTimeList.add(false);
            holePmString.add("");


        }



        pmPie.animateY(1000, Easing.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"plan");
        //dataSet.setSliceSpace(3f);
        //dataSet.setSelectionShift(5f);


        dataSet.setColors(colors);

        PieData data = new PieData((dataSet));
        data.setDrawValues(false);
        data.setHighlightEnabled(false);
//        data.setValueTextSize(10f);
//        data.setValueTextColor(Color.YELLOW);

        pmPie.setData(data);
        // pieChart.setTouchEnabled(false);
        pmPie.getLegend().setEnabled(false);

    }

    private int getColor(int i){

        switch (i%5){
            case 0:
                return Color.rgb(40, 155, 246);
            case 1:
                return Color.rgb(148, 212, 212);
            case 2:
                return Color.rgb(136, 180, 187);
            case 3:
                return Color.rgb(118, 174, 175);
            case 4:
                return Color.rgb(42, 109, 130);
            default:
                return Color.rgb(207, 248, 246);
        }

    }

    private void timeBackgroundInvisible(){
        timeTop.setVisibility(View.INVISIBLE);
        timeRight.setVisibility(View.INVISIBLE);
        timeBottom.setVisibility(View.INVISIBLE);
        timeLeft.setVisibility(View.INVISIBLE);
        timeStatus.setVisibility(View.INVISIBLE);
        holeTextView.setVisibility(View.INVISIBLE);
    }

    private void timeBackgroundinit(String time){

        timeTop.setVisibility(View.VISIBLE);
        timeRight.setVisibility(View.VISIBLE);
        timeBottom.setVisibility(View.VISIBLE);
        timeLeft.setVisibility(View.VISIBLE);
        holeTextView.setText(todayNotYear);
        holeTextView.setVisibility(View.VISIBLE);


        switch (time){
            case "am":
                timeTop.setText("12");
                timeRight.setText("3");
                timeBottom.setText("6");
                timeLeft.setText("9");
                timeStatus.setText("AM");
                break;
            case "pm":
                timeTop.setText("12");
                timeRight.setText("3");
                timeBottom.setText("6");
                timeLeft.setText("9");
                timeStatus.setText("PM");
                break;
            default:
                timeTop.setText("12");
                timeRight.setText("6");
                timeBottom.setText("12");
                timeLeft.setText("6");
                timeStatus.setText("ALL");
                break;
        }

        timeStatus.setVisibility(View.VISIBLE);

    }


//    private void checkIsEndDateOver(){
//
//        for(int i=0;i<plansRealmResults.size();i++){
//            if(plansRealmResults.get(i).getSuccess() == 0){
//                //미완
//
//                if(plansRealmResults.get(i).getEndTime().getTime() - new Date().getTime() <0){
//                    //이미 시간 지난것
//                    final int finalI = i;
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//
//                            Plans plan = realm.where(Plans.class).equalTo("id", plansRealmResults.get(finalI).getId()).findFirst();
//                            //실패
//                            plan.setSuccess(2);
//                            plan.setFocus(50);
//                            plan.setDuration(getNewDuration(new Date()));
//                            plan.setStartTime(startPlanTime);
//                            plan.setEndTime(new Date());
//                            finish();
//
//                        }
//                    });
//                }
//
//            }
//
//        }
//
//    }



    private String changeDateToStr(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("a hh:mm", Locale.ENGLISH);

        return dateFormat.format(date);
    }

}
