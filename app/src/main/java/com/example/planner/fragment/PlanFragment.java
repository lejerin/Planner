package com.example.planner.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.planner.Activities.HomeActivity;
import com.example.planner.Activities.MakePlanActivity;
import com.example.planner.Adapters.PlanAdapter;
import com.example.planner.Helpers.DividerItemDecoration;
import com.example.planner.Helpers.EventDecorator;
import com.example.planner.Helpers.SaturdayDecorator;
import com.example.planner.Helpers.SundayDecorator;
import com.example.planner.Helpers.TodayDecorator;
import com.example.planner.R;
import com.example.planner.Realm.Plans;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class PlanFragment extends Fragment{

    /*
    PlanFragment : 월간캘린더에서 일일 계획 DB에 추가, 수정, 삭제

     */

    private static int MAKE_PLAN_ACTIVITY_CODE = 11;
    private static int MODIFY_PLAN_ACTIVITY_CODE = 22;

    // 월간캘린더 MaterialCalendarView 라이브러리 사용 https://github.com/prolificinteractive/material-calendarview
    private MaterialCalendarView calendarView;
    private TextView selectedDay;
    private RecyclerView planRecylerView;
    private String clickDate = null;
    private String thisYearMonth = null;

    List<CalendarDay> markCalendars = new ArrayList<>();

    private Realm realm;
    private RealmResults<Plans> plansRealmResults,thisYearMonthPlan;
    private PlanAdapter adapter;



    public PlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        //init view item
        calendarView = view.findViewById(R.id.calendarView);
        selectedDay = view.findViewById(R.id.selectedDay);
        planRecylerView = view.findViewById(R.id.planRecyclerView);
        ImageButton fb = view.findViewById(R.id.planAddBtn);

        realm = Realm.getDefaultInstance();


        //오늘 날짜 설정
        setSelectedDayTextView(new Date());
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM");
        thisYearMonth = transFormat.format(new Date());

        //당일 날짜 계획 갖고오기
        setUpRecyclerView();


        thisYearMonthPlan = realm.where(Plans.class).equalTo("yearMonth",thisYearMonth)
                .sort("startTime", Sort.ASCENDING).findAll();

        //캘린더에 마커 색깔 추가하기
        setUpMonthPlanColor();
        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new TodayDecorator(getActivity())
        );

        //캘린더 날짜 선택 이벤트
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                //하단 클릭 날짜 변경, DB에서 선택 날짜 계획을 갖고온 뒤 RecyclerView에 추가
                setSelectedDayTextView(date.getDate());
                plansRealmResults = realm.where(Plans.class).equalTo("timeText",clickDate)
                        .sort("startTime", Sort.ASCENDING).findAll();

                adapter = new PlanAdapter(getContext(),plansRealmResults);
                planRecylerView.setAdapter(adapter);

                setAdapterListener();

            }
        });


        //계획 추가 버튼 눌렀을 때
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent makePlanActivity = new Intent(getContext(), MakePlanActivity.class);
                makePlanActivity.putExtra("date",clickDate);
                makePlanActivity.putExtra("isNew",-1);
                startActivityForResult(makePlanActivity,MAKE_PLAN_ACTIVITY_CODE);


            }
        });




        //계획 수정, 삭제
        setAdapterListener();

        return view;
    }


    private void setAdapterListener(){
        //계획 수정, 삭제
        adapter.setOnModifyClickListener(new PlanAdapter.OnModifyClickListener() {
            @Override
            public void onModifyClick(int pos) {
                //수정 버튼 눌림
                System.out.println("위치" + pos);

                Intent makePlanActivity = new Intent(getContext(), MakePlanActivity.class);
                makePlanActivity.putExtra("date",clickDate);
                makePlanActivity.putExtra("isNew",pos);
                startActivityForResult(makePlanActivity,MODIFY_PLAN_ACTIVITY_CODE);



            }
        });

        adapter.setOnDeleteClickListener(new PlanAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(final int pos) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("일정 삭제").setMessage(plansRealmResults.get(pos).getTitle() + " 을 정말 삭제하시겠습니까?");

                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                int finalpos = pos;
                                int code = plansRealmResults.get(finalpos).getId();

                                realm.where(Plans.class).equalTo("id",plansRealmResults.get(finalpos).getId()).findFirst()
                                        .deleteFromRealm();
                                planRecylerView.getRecycledViewPool().clear();
                                adapter.notifyDataSetChanged();

                                //not working
                                setUpMonthPlanColor();

                                //알림 예약 삭제
                                ((HomeActivity)getActivity()).removeNotification(code);

                            }
                        });
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {


                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    //해당 달의 계획을 모두 갖고온 뒤 계획 존재하는 날짜는 마커 표시(현재는 분홍색)
    private void setUpMonthPlanColor() {

        String from = "";
        for(int i=0;i<thisYearMonthPlan.size();i++){
            //같은 날짜 중복인건 제외
            if(!from.equals(thisYearMonthPlan.get(i).getTimeText() +" 10:10:10")){
                from = thisYearMonthPlan.get(i).getTimeText() +" 10:10:10";
                SimpleDateFormat transFormat = new SimpleDateFormat("EE, MM월 dd일 yyyy년 HH:mm:ss");
                try {
                    Date to = transFormat.parse(from);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(to);
                    markCalendars.add(CalendarDay.from(cal));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }


        calendarView.addDecorator(new EventDecorator(markCalendars,getActivity()));

    }


    //날짜(Date) -> String
    private String changeDateToStr(Date date) {
        SimpleDateFormat transFormat = new SimpleDateFormat("EE, MM월 dd일 yyyy년");
        return transFormat.format(date);
    }

    //클릭 날짜 텍스트 변경
    private void setSelectedDayTextView(Date day){
        clickDate = changeDateToStr(day);
        selectedDay.setText(clickDate);

    }

    //리사이클러뷰 초기화(초기는 당일 날짜 계획)
    private void setUpRecyclerView() {

        plansRealmResults = realm.where(Plans.class).equalTo("timeText",clickDate)
                .sort("startTime", Sort.ASCENDING).findAll();
        adapter = new PlanAdapter(getContext(),plansRealmResults);

        planRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
        planRecylerView.setAdapter(adapter);
        planRecylerView.setHasFixedSize(true);
        planRecylerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.divider));

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //계획 추가가 성공적이면
        if(resultCode == 11){
            //마커 갱신
            setUpMonthPlanColor();


            //알람예약하기
            Plans plans = realm.where(Plans.class).equalTo("id", data.getExtras().getInt("id")).findFirst();

            Calendar cal = Calendar.getInstance();
            if(plans.getStartTime().getTime() - new Date().getTime() > 0){
                cal.setTime(plans.getStartTime());
                ((HomeActivity)getActivity()).diaryNotification(cal,plans.getId(),plans.getTitle());
            }


        }else if(resultCode == 22){
            setUpMonthPlanColor();


            //알람예약하기
            Plans plans = realm.where(Plans.class).equalTo("id", data.getExtras().getInt("id")).findFirst();

            Calendar cal = Calendar.getInstance();
            ((HomeActivity)getActivity()).removeNotification(plans.getId());

            if(plans.getStartTime().getTime() - new Date().getTime() > 0){
                cal.setTime(plans.getStartTime());
                ((HomeActivity)getActivity()).diaryNotification(cal,plans.getId(),plans.getTitle());
            }


        }

    }
}
