package com.example.planner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.planner.Helpers.FocusDialog;
import com.example.planner.Helpers.FocusTimerDialog;
import com.example.planner.R;
import com.example.planner.Realm.Plans;
import com.example.planner.TimerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class TimerActivity extends AppCompatActivity {

    private TimerView timer;
    private TextView hourTxt,minTxt,secTxt,timerTitle,timerStart,timerEnd;

    private int id;
    private int duration;

    private Realm realm;

    FocusDialog focusDialog;

    private Date startPlanTime;


    private CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

//                계획 정보 받기
//                기본키, 이름, 날짜, 시작시간, 종료시간, 총 소요시간
//                private id
//                private String title, timeText;
//                private Date startTime, endTime;
//                private int duration;

        realm = Realm.getDefaultInstance();

        id = getIntent().getExtras().getInt("id");
        String title = getIntent().getExtras().getString("title");
        String timeText = getIntent().getExtras().getString("timeText");
        Date startTime = (Date)getIntent().getSerializableExtra("startTime");
        Date endTime = (Date)getIntent().getSerializableExtra("endTime");
        duration = getIntent().getExtras().getInt("duration");


        //시작 시간 기록
        startPlanTime = new Date();

        timer = (TimerView) findViewById(R.id.timer);
        hourTxt = (TextView) findViewById(R.id.timer_hour);
        minTxt = (TextView) findViewById(R.id.timer_min);
        secTxt = (TextView) findViewById(R.id.timer_sec);
        timerTitle = (TextView) findViewById(R.id.timerTitle);
        timerStart = (TextView) findViewById(R.id.timerStart);
        timerEnd = (TextView) findViewById(R.id.timerEnd);
        Button giveupBtn = (Button) findViewById(R.id.giveupBtn);
        Button cpBtn = (Button) findViewById(R.id.cpBtn);

        timerTitle.setText(title);
        timerStart.setText(changeDateToStr(startTime));
        timerEnd.setText(changeDateToStr(endTime));

        initializeTimer(duration);



        //포기 버튼
        giveupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFocusDialog(false);
            }
        });

        //성공 버튼
        cpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFocusDialog(true);
            }
        });

    }



    private void initializeTimer(int duration) {
        timer.start(duration);
        countDown(duration);
//        new Timer().schedule(new TimerTask() {
//            @Override
//
//            public void run() {
//
//                System.out.println("end");
//
//            }
//        }, duration*1000);
    }

    public void countDown(final int duration) {

        long conversionTime = 0;

        // 변환시간
        conversionTime =  Long.valueOf(duration) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        countDownTimer = new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {
                // 시간단위
                long getHour = millisUntilFinished / (60 * 60 * 1000);
                String hour = String.valueOf(getHour);

                // 분단위
                long getMin = millisUntilFinished - (getHour*(60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // 몫

                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

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

                hourTxt.setText(hour);
                minTxt.setText(min);
                secTxt.setText(second);
            }

            // 제한시간 종료시
            public void onFinish() {

                finishTime();

            }
        }.start();

    }

    private String changeDateToStr(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("a hh:mm", Locale.ENGLISH);

        return dateFormat.format(date);
    }

    //시간 종료 되었을 때
    private void finishTime(){

        FocusTimerDialog focusTimerDialog = new FocusTimerDialog(TimerActivity.this);
        focusTimerDialog.setDialogListener(new FocusTimerDialog.TimerDialogListener() {
            @Override
            public void onPositiveClicked(Boolean isOk, final int focus) {
                if (isOk) {
                    System.out.println("저장");
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {


                            Plans plan = realm.where(Plans.class).equalTo("id", id).findFirst();
                            plan.setSuccess(1);
                            plan.setFocus(focus);
                            plan.setStartTime(startPlanTime);
                            plan.setEndTime(new Date());
                            finish();

                        }
                    });


                }
            }

            @Override
            public void onExtendClicked(Boolean isExtend) {
                System.out.println("연장");


            }
        });
        focusTimerDialog.showDialog();



    }

    private void showFocusDialog(final Boolean isSuccess){

        focusDialog = new FocusDialog(TimerActivity.this);
        focusDialog.setDialogListener(new FocusDialog.CustomDialogListener() {
            @Override
            public void onPositiveClicked(Boolean isOk, final int focus) {
                if(isOk){
                    System.out.println("저장");
                    final Plans plan = realm.where(Plans.class).equalTo("id", id).findFirst();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if(isSuccess){
                                //성공이면
                                plan.setSuccess(1);
                                plan.setFocus(focus);

                            }else{
                                //포기면
                                plan.setSuccess(2);
                                plan.setFocus(focus);
                            }
                            plan.setStartTime(startPlanTime);
                            plan.setEndTime(new Date());
                            finish();
                        }
                    });


                }
            }
        });
        focusDialog.showDialog();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
