package com.example.planner.Alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.planner.Activities.HomeActivity;
import com.example.planner.Activities.LoginActivity;
import com.example.planner.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    int id = 0;
    int repeat = 0;
    String name = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        id = intent.getExtras().getInt("id");
        repeat = intent.getExtras().getInt("repeat");
        name = intent.getExtras().getString("name");
        System.out.println("리시버 id" + id + " 이름: " + name);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, LoginActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");


        //Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getPackageName() + "/" + R.raw.alert);
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alertsound);


        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
           // builder.setDefaults(Notification.DEFAULT_VIBRATE);

            String channelName ="알람 채널";
            String description = "정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0,500,1000});


            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            channel.setSound(soundUri, audioAttributes);





            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }



        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남


        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle(name + " 계획 시작")
                .setContentText("타이머를 시작해주세요")
                .setContentInfo("INFO")
                .setDefaults(Notification.DEFAULT_LIGHTS )
                .setContentIntent(pendingI)
                .setSound(soundUri)
                .setVibrate(new long[]{0, 500, 1000});


        if (notificationManager != null) {

            // 노티피케이션 동작시킴
            notificationManager.notify(id, builder.build());

//            Calendar nextNotifyTime = Calendar.getInstance();
//
//            // 내일 같은 시간으로 알람시간 결정
//            nextNotifyTime.add(Calendar.DATE, 1);

//            //  Preference에 설정한 값 저장
//            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
//            editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
//            editor.apply();

//            Date currentDateTime = nextNotifyTime.getTime();
//            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context.getApplicationContext(),name + "계획 타이머를 시작해주세요", Toast.LENGTH_SHORT).show();

            if(repeat > 0){
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                alarmIntent.putExtra("id", id);
                alarmIntent.putExtra("name",name);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if(repeat == 1){
                    //매일
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(24*60*60*1000),pendingIntent);
                    }else{
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(24*60*60*1000),pendingIntent);
                    }

                }else{
                    //2일때 매주
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(7*24*60*60*1000),pendingIntent);
                    }else{
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(7*24*60*60*1000),pendingIntent);
                    }
                }
            }


        }
    }



}
