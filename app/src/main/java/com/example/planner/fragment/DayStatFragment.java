package com.example.planner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.planner.R;
import com.example.planner.Realm.Plans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;

public class DayStatFragment extends Fragment {

    private RealmResults<Plans> plansRealmResults;

    public DayStatFragment() {

    }

    List<Plans> plans = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat_day, container, false);


        makeDB();


        return view;
    }

    //가상 데이터
    private void makeDB(){
        Plans a = new Plans();
        a.setTitle("국어");
        a.setTimeText("월, 05월 25일 2020년");
        a.setYearMonth("2020-05");
        a.setStartTime(stringToDate("월, 05월 25일 2020년 10:00:00"));
        a.setEndTime(stringToDate("월, 05월 25일 2020년 11:30:00"));
        a.setDuration(90*60); //초 입력 1시간30분 * 60
        a.setSuccess(1);
        a.setFocus(80);
        plans.add(a);

        Plans b = new Plans();
        b.setTitle("수학");
        b.setTimeText("월, 05월 25일 2020년");
        b.setYearMonth("2020-05");
        b.setStartTime(stringToDate("월, 05월 25일 2020년 13:30:00"));
        b.setEndTime(stringToDate("월, 05월 25일 2020년 15:30:00"));
        b.setDuration(120*60); //초 입력 2시간 * 60
        b.setSuccess(2);
        b.setFocus(50);
        plans.add(b);


        System.out.println(plans.size());
        System.out.println(plans.get(1).getTitle());
    }

    //String -> Date
    private Date stringToDate(String date){
        SimpleDateFormat fm = new SimpleDateFormat("EE, MM월 dd일 yyyy년 HH:mm:ss");
        try {
            return  fm.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }
}
