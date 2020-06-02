package com.example.planner.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.planner.Alarm.AlarmReceiver;
import com.example.planner.Alarm.DeviceBootReceiver;
import com.example.planner.R;

import com.example.planner.fragment.BlogFragment;
import com.example.planner.fragment.HomeFragment;
import com.example.planner.fragment.StatisticsFragment;
import com.example.planner.fragment.PlanFragment;
import com.example.planner.fragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;


/*
HomeActivity : 5가지 Fragment 이동시키는 액티비티

homeFragment : 당일 원형 계획표, 타이머 시작, 성장나무
planFragment : 월간캘린더에서 일일 계획 DB에 추가, 수정, 삭제
statisticsFragment : DB에 저장된 사용자의 타이머 데이터를 이용하여 일, 주, 월로 시각화
blogFragment : 사용자 카테고리별로 게시판을 나눈 뒤 글 작성, 댓글 작성
settingFragment : 사용자 닉네임,프로필 사진 변경, 로그아웃, 성장나무 확인? , 기타
 */

public class HomeActivity extends AppCompatActivity {


    //private HomeFragment homeFragment = new HomeFragment();
    //private PlanFragment planFragment = new PlanFragment();
   // private StatisticsFragment statisticsFragment = new StatisticsFragment();
    private BlogFragment blogFragment = new BlogFragment();
    private SettingFragment settingFragment = new SettingFragment();

    private BottomNavigationView menuBawah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        menuBawah = findViewById(R.id.menu_bawah);
        setFragment(new HomeFragment());
        menuBawah.setSelectedItemId(R.id.menu_home);

        //하단 네비게이션바를 선택하면 Fragment 변경을 통해 화면 바꾸기
        menuBawah.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.isChecked()){
                    return true;
                }else{
                    switch (menuItem.getItemId()){
                        case R.id.menu_home:
                            setFragment(new HomeFragment());
                            return true;
                        case R.id.menu_plan:
                            setFragment(new PlanFragment());
                            return true;
                        case R.id.menu_note:
                            setFragment(new StatisticsFragment());
                            return true;
                        case R.id.menu_blog:
                            setFragment(blogFragment);
                            return true;
                        case R.id.menu_setting:
                            setFragment(settingFragment);
                            return true;

                        default:
                            setFragment(new HomeFragment());
                            return true;
                    }
                }
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame,fragment);
        ft.commit();
    }



    public void diaryNotification(Calendar calendar, int code, String name)
    {
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("id", code);
        alarmIntent.putExtra("name",name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, code, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {


            if (alarmManager != null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        //API 19 이상 API 23미만
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        //API 19미만
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                } else {
                    //API 23 이상

                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }

    public void removeNotification(int code){

        AlarmManager alarmManager = (AlarmManager)
                getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(),
                AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), code, myIntent, 0);

        alarmManager.cancel(pendingIntent);

    }



}
