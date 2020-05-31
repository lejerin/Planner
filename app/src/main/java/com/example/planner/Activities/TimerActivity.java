package com.example.planner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planner.Helpers.FocusDialog;
import com.example.planner.Helpers.FocusFinishDialog;
import com.example.planner.Helpers.FocusTimerDialog;
import com.example.planner.R;
import com.example.planner.Realm.Plans;
import com.example.planner.TimerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class TimerActivity extends AppCompatActivity {

    private TimerView timer;
    private TextView hourTxt,minTxt,secTxt,timerTitle,timerStart,timerEnd, chance;

    private int id;
    private int duration;

    private Realm realm;

    FocusDialog focusDialog;

    private Date startPlanTime, nextPlanStartTime;

    private int extendChance = 0;
    String timeText;

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
        timeText = getIntent().getExtras().getString("timeText");
        Date startTime = (Date)getIntent().getSerializableExtra("startTime");
        Date endTime = (Date)getIntent().getSerializableExtra("endTime");
        duration = getIntent().getExtras().getInt("duration");
        nextPlanStartTime = (Date)getIntent().getSerializableExtra("next");


        //시작 시간 기록
        startPlanTime = new Date();

        timer = (TimerView) findViewById(R.id.timer);
        chance = (TextView) findViewById(R.id.chanceText);
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

        if(extendChance < 3) {

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
                    System.out.println("연장 시도");
                    //다음 계획 시간 여유있으면 10분 연장하기

                    if (nextPlanStartTime == null) {
                        //자정에 날짜 바뀌는지
                        if (getTodayLastTime().getTime() - new Date().getTime() > 10 * 60 * 1000) {
                            //자정까지 10분 여유있음
                            System.out.println("연장 가능");
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 내용
                                    initializeTimer(10*60);
                                    extendChance += 1;
                                    chance.setText(3 - extendChance + "");
                                }
                            }, 0);

                        } else {
                            //showMessage("곧 자정이므로 연장이 불가합니다");
                            showFocusFinishDialog();
                        }

                    } else {
                        //10분 여유있는지 체크
                        if (nextPlanStartTime.getTime() - new Date().getTime() > 10 * 60 * 1000) {
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 내용
                                    initializeTimer(10*60);
                                    extendChance += 1;
                                    chance.setText(3 - extendChance + "");
                                }
                            }, 0);
                        } else {
                            //showMessage("다음 일정으로 연장이 불가합니다");
                            showFocusFinishDialog();
                        }
                    }


                }
            });
            focusTimerDialog.showDialog();

        }else{
            System.out.println("연장 불가");
            showFocusFinishDialog();
        }

    }


    private void showFocusFinishDialog(){
        //마지막 연장까지 끝나서 최종 집중도 팝업
        System.out.println("마지막 팝업");

                FocusFinishDialog focusTimerDialog = new FocusFinishDialog(TimerActivity.this);
                focusTimerDialog.setSaveDialogListener(new FocusFinishDialog.SaveDialogListener() {
                    @Override
                    public void onPositiveClicked(Boolean isOk, final int focus) {
                        if (isOk) {
                            System.out.println("저장");
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 내용
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {


                                            Plans plan = realm.where(Plans.class).equalTo("id", id).findFirst();
                                            //포기
                                            plan.setSuccess(2);
                                            plan.setFocus(focus);
                                            plan.setStartTime(startPlanTime);
                                            plan.setEndTime(new Date());
                                            finish();

                                        }
                                    });
                                }
                            }, 0);



                        }
                    }

                    @Override
                    public void onSaveClicked(Boolean isExtend) {
                        //30초동안 입력이 없어서 자동 저장
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 내용
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {


                                        Plans plan = realm.where(Plans.class).equalTo("id", id).findFirst();
                                        //포기
                                        plan.setSuccess(2);
                                        //집중력 입력이 없으므로 임의 중간값?
                                        plan.setFocus(50);
                                        plan.setStartTime(startPlanTime);
                                        plan.setEndTime(new Date());
                                        finish();

                                    }
                                });
                            }
                        }, 0);


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

    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    //String -> Date
    private Date getTodayLastTime(){
        String from = timeText  + " 23:59:59";
        SimpleDateFormat fm = new SimpleDateFormat("EE, MM월 dd일 yyyy년 HH:mm:ss");
        try {
            return  fm.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }
}
