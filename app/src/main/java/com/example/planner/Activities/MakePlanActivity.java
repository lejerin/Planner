package com.example.planner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.planner.Helpers.FocusDialog;
import com.example.planner.Helpers.MakePlanSearchDialog;
import com.example.planner.R;
import com.example.planner.Realm.Plans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MakePlanActivity extends AppCompatActivity {

    /*
    계획 신규 DB에 추가하는 액티비티
    계획 시간 중첩되지 않도록 수정해야함
     */


    private AutoCompleteTextView planTitle;
    private TextView makePlanDate,saveBtn,spinnerText;
    private Button startTimeBtn,endTimeBtn;
    private Spinner planSpinner;
    private String date;
    private Date startDate, endDate;
    private RealmResults<Plans> plansRealmResults,getAllPlans;
    Realm realm;
    int pos = -1;
    int planId = 1;
    int repeat = 0;

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
        spinnerText = findViewById(R.id.spinnerText);
        saveBtn = findViewById(R.id.saveBtn);
        ImageButton makePlanbackBtn = findViewById(R.id.makePlanbackBtn);

        //PlanFragment에서 계획 추가하는 날짜 받아옴
        date = getIntent().getExtras().getString("date");
        makePlanDate.setText(date);

        realm = Realm.getDefaultInstance();

        final List<String> getTitle = new ArrayList<String>();
        //자동완성 리스트 불러오기
        getAllPlans = realm.where(Plans.class).findAll();

        for(int i=0;i<getAllPlans.size();i++){
            boolean isduple = false;
            for(int j=0;j<getTitle.size();j++){
                if(getTitle.get(j).equals(getAllPlans.get(i).getTitle())){
                    isduple = true;
                }
            }
            if(!isduple){
                getTitle.add(getAllPlans.get(i).getTitle());
            }
        }

        System.out.println("이름 리스트"+getTitle);

        planTitle.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getTitle));

        planTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {

                System.out.println("클릭 리스트"+pos);
                String name = planTitle.getText().toString();
                showRecommendDialog(name);

            }
        });




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

            //반복설정 안보이게
            spinnerText.setVisibility(View.GONE);
            planSpinner.setVisibility(View.GONE);


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

                    if(pos > -1){
                        setResult(22, intent);
                    }else{
                        setResult(11, intent);
                    }

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




        //반복설정
        planSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repeat = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void showRecommendDialog(String name){

        System.out.println("클릭" + name);

        RealmResults<Plans> rp = getAllPlans.where().equalTo("title",name).findAll();
        //다이얼로그어댑터
        int success = 0;
        int successCount = 0;
        int fail = 0;
        int failCount = 0;
        int notStart = 0;
        int notStartCount = 0;

        for(int i=0;i<rp.size();i++){
            if(rp.get(i).getSuccess() == 1){
                success += (rp.get(i).getDuration()*(rp.get(i).getFocus()/(float)100));
                successCount++;
            }else if(rp.get(i).getSuccess() == 2){
                fail += (rp.get(i).getDuration()*(rp.get(i).getFocus()/(float)100));
                failCount++;
            }else{
                notStart += (rp.get(i).getDuration());
                notStartCount++;
            }
        }


        int recomendTime = 0;
        if(success != 0){
            recomendTime = success/successCount;
        }else{
            if(fail != 0){
                recomendTime = fail/failCount;
            }else{
                recomendTime = notStart/notStartCount;
            }
        }


        MakePlanSearchDialog makePlanSearchDialog = new MakePlanSearchDialog(MakePlanActivity.this,rp,recomendTime);
        makePlanSearchDialog.showDialog();


    }


    //시작시간과 종료시간이 기존 다른 계획과 겹치는지 확인
    private boolean checkTimeDuple(Date check){

        for(int i=0;i<plansRealmResults.size();i++){

            if(pos > -1){
                //수정이면 자신을 제외하고 겹치지 않으면 괜찮음
                if(planId != plansRealmResults.get(i).getId()){
                    if(plansRealmResults.get(i).getStartTime().getTime() <= check.getTime()
                            && plansRealmResults.get(i).getEndTime().getTime() >= check.getTime()){
                        return false;
                    }
                }


            }else{
                if(plansRealmResults.get(i).getStartTime().getTime() <= check.getTime()
                        && plansRealmResults.get(i).getEndTime().getTime() >= check.getTime()){
                    return false;
                }
            }


        }


        return true;
    }

//    private boolean checkTimeAllDuple(Date check){
//
//        RealmResults<Plans> allResult = realm.where(Plans.class).greaterThan("startTime",startDate).findAll();
//
//
//        for(int i=0;i<plansRealmResults.size();i++) {
//
//
//
//        }
//
//
//        return true;
//    }

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
                plan.setRepeat(repeat);
                String monthNumber  = (String) DateFormat.format("yyyy-MM",   startDate);
                plan.setyearMonth(monthNumber);

                long diff =  endDate.getTime() - startDate.getTime();
                int sec = (int) (diff / 1000);
                plan.setDuration(sec);

            }
            } );

            //반복설정일시
            if(repeat == 1){
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.setTime(startDate);
                end.setTime(endDate);
                for(int i=0;i<6;i++){
                    start.add(Calendar.DATE, 1);
                    final Date repeatStrat = start.getTime();
                    end.add(Calendar.DATE, 1);
                    final Date repeatEnd = end.getTime();

                    realm.executeTransaction(new Realm.Transaction() { @Override public void execute(Realm realm) {

                        Number maxId = realm.where(Plans.class).max("id");
                        // If there are no rows, currentId is null, so the next id must be 1
                        // If currentId is not null, increment it by 1
                        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                        // User object created with the new Primary key

                        Plans plan = realm.createObject(Plans.class,nextId);
                        plan.setTitle(planTitle.getText().toString());
                        plan.setTimeText(changeDateToStrRepeat(repeatStrat));
                        plan.setStartTime(repeatStrat);
                        plan.setEndTime(repeatEnd);
                        plan.setRepeat(repeat);
                        String monthNumber  = (String) DateFormat.format("yyyy-MM",   repeatStrat);
                        plan.setyearMonth(monthNumber);

                        long diff =  repeatEnd.getTime() - repeatStrat.getTime();
                        int sec = (int) (diff / 1000);
                        plan.setDuration(sec);

                    }
                    } );
                }

            }else if(repeat == 2){
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.setTime(startDate);
                end.setTime(endDate);
                for(int i=0;i<4;i++){
                    start.add(Calendar.DATE, 7);
                    final Date repeatStrat = start.getTime();
                    end.add(Calendar.DATE, 7);
                    final Date repeatEnd = end.getTime();

                    realm.executeTransaction(new Realm.Transaction() { @Override public void execute(Realm realm) {

                        Number maxId = realm.where(Plans.class).max("id");
                        // If there are no rows, currentId is null, so the next id must be 1
                        // If currentId is not null, increment it by 1
                        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                        // User object created with the new Primary key

                        Plans plan = realm.createObject(Plans.class,nextId);
                        plan.setTitle(planTitle.getText().toString());
                        plan.setTimeText(changeDateToStrRepeat(repeatStrat));
                        plan.setStartTime(repeatStrat);
                        plan.setEndTime(repeatEnd);
                        plan.setRepeat(repeat);
                        String monthNumber  = (String) DateFormat.format("yyyy-MM",   repeatStrat);
                        plan.setyearMonth(monthNumber);

                        long diff =  repeatEnd.getTime() - repeatStrat.getTime();
                        int sec = (int) (diff / 1000);
                        plan.setDuration(sec);

                    }
                    } );
                }
            }


        }else{
            //수정
            //이미 완료한 작업은 수정못하도록 해야함


            realm.executeTransaction(new Realm.Transaction() { @Override public void execute(Realm realm) {

                plansRealmResults.get(pos).setTitle(planTitle.getText().toString());
                plansRealmResults.get(pos).setTimeText(date);
                plansRealmResults.get(pos).setStartTime(startDate);
                plansRealmResults.get(pos).setEndTime(endDate);
                String monthNumber  = (String) DateFormat.format("yyyy-MM",   startDate);
                plansRealmResults.get(pos).setyearMonth(monthNumber);
                plansRealmResults.get(pos).setRepeat(repeat);
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

    //날짜(Date) -> String
    private String changeDateToStrRepeat(Date date) {
        SimpleDateFormat transFormat = new SimpleDateFormat("EE, MM월 dd일 yyyy년");
        return transFormat.format(date);
    }
}
