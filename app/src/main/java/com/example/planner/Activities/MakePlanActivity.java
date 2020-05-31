package com.example.planner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.planner.R;
import com.example.planner.Realm.Plans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MakePlanActivity extends AppCompatActivity {

    /*
    계획 신규 DB에 추가하는 액티비티
    계획 시간 중첩되지 않도록 수정해야함
     */


    private EditText planTitle;
    private TextView makePlanDate,saveBtn;
    private Button startTimeBtn,endTimeBtn;
    private Spinner planSpinner;
    private String date;
    private Date startDate, endDate;
    private RealmResults<Plans> plansRealmResults;

    int pos = -1;

    int planId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_plan);



        //init item view
        planTitle = findViewById(R.id.makePlanTitle);
        makePlanDate = findViewById(R.id.makePlanDate);
        startTimeBtn = findViewById(R.id.startTimeBtn);
        endTimeBtn = findViewById(R.id.endTimeBtn);
        planSpinner = findViewById(R.id.planSpinner);
        saveBtn = findViewById(R.id.saveBtn);
        ImageButton makePlanbackBtn = findViewById(R.id.makePlanbackBtn);

        //PlanFragment에서 계획 추가하는 날짜 받아옴
        date = getIntent().getExtras().getString("date");
        makePlanDate.setText(date);


        Realm realm = Realm.getDefaultInstance();
        plansRealmResults = realm.where(Plans.class).equalTo("timeText",date)
                .sort("startTime", Sort.ASCENDING).findAll();


        pos = getIntent().getExtras().getInt("isNew");
        if(pos > -1){
            //수정일경우
            planId = plansRealmResults.get(pos).getId();
            planTitle.setText(plansRealmResults.get(pos).getTitle());
            startDate = plansRealmResults.get(pos).getStartTime();
            startTimeBtn.setText(changeDateToStr(startDate));
            endDate = plansRealmResults.get(pos).getEndTime();
            endTimeBtn.setText(changeDateToStr(endDate));
        }else{
            Number maxId = realm.where(Plans.class).max("id");
            planId = (maxId == null) ? 1 : maxId.intValue() + 1;
        }

        System.out.println("planid" + planId);

        //저장 클릭 이벤트
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(planTitle.getText().toString().equals("") || !checkTimeCorrect(startDate,endDate)){
                    showMessage("올바르게 입력해주세요");
                }else{
                    saveDB();
                    Intent intent = new Intent();
                    intent.putExtra("id", planId);
                    setResult(11, intent);
                    finish();
                }
            }
        });

       //뒤로가기 클릭 이벤트
        makePlanbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //시작 시간 클릭 이벤트
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MakePlanActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if(checkTimeDuple(stringToDate(selectedHour,selectedMinute))){
                            startDate = stringToDate(selectedHour,selectedMinute);
                            startTimeBtn.setText(changeDateToStr(startDate));
                        }else{
                            showMessage("기존 일정과 중복됩니다. 다시 선택해주세요");
                        }


                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        //종료 시간 클릭 이벤트
        endTimeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MakePlanActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if(checkTimeDuple(stringToDate(selectedHour,selectedMinute))){
                            endDate = stringToDate(selectedHour,selectedMinute);
                            endTimeBtn.setText(changeDateToStr(endDate));
                        }else{
                            showMessage("기존 일정과 중복됩니다. 다시 선택해주세요");
                        }

                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


    }

    //시작시간과 종료시간이 기존 다른 계획과 겹치는지 확인
    private boolean checkTimeDuple(Date check){

        for(int i=0;i<plansRealmResults.size();i++){
            if(plansRealmResults.get(i).getStartTime().getTime() <= check.getTime()
                    && plansRealmResults.get(i).getEndTime().getTime() >= check.getTime()){
                return false;
            }
        }


        return true;
    }

    //String -> Date
    private Date stringToDate(int hour, int min){
        String from = date  + " " + hour+":" + min + ":" + "00";
        SimpleDateFormat fm = new SimpleDateFormat("EE, MM월 dd일 yyyy년 HH:mm:ss");
        try {
            return  fm.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }

    //Date  -> String
    private String changeDateToStr(Date date) {

        java.text.DateFormat dateFormat = new SimpleDateFormat("a hh:mm", Locale.ENGLISH);

        return dateFormat.format(date);
    }

    //DB에 Plans 추가(
    private void saveDB() {

        Realm realm = Realm.getDefaultInstance();//데이터 넣기(insert)

        if(pos == -1){

        realm.executeTransaction(new Realm.Transaction() { @Override public void execute(Realm realm) {

            Number maxId = realm.where(Plans.class).max("id");
            // If there are no rows, currentId is null, so the next id must be 1
            // If currentId is not null, increment it by 1
            int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
            // User object created with the new Primary key

            Plans plan = realm.createObject(Plans.class,nextId);
            plan.setTitle(planTitle.getText().toString());
            plan.setTimeText(date);
            plan.setStartTime(startDate);
            plan.setEndTime(endDate);
            String monthNumber  = (String) DateFormat.format("yyyy-MM",   startDate);
            plan.setyearMonth(monthNumber);

            long diff =  endDate.getTime() - startDate.getTime();
            int sec = (int) (diff / 1000);
            plan.setDuration(sec);
        }
        } );

        }else{
            //수정


            realm.executeTransaction(new Realm.Transaction() { @Override public void execute(Realm realm) {

                plansRealmResults.get(pos).setTitle(planTitle.getText().toString());
                plansRealmResults.get(pos).setTimeText(date);
                plansRealmResults.get(pos).setStartTime(startDate);
                plansRealmResults.get(pos).setEndTime(endDate);
                String monthNumber  = (String) DateFormat.format("yyyy-MM",   startDate);
                plansRealmResults.get(pos).setyearMonth(monthNumber);

                long diff =  endDate.getTime() - startDate.getTime();
                int sec = (int) (diff / 1000);
                plansRealmResults.get(pos).setDuration(sec);
            }
            } );


        }
    }

    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    //종료시간이 시작시간 이후인지 확인
    private boolean checkTimeCorrect(Date start, Date end){
        if(start.getTime() < end.getTime()){
            return true;
        }

        return false;
    }
}