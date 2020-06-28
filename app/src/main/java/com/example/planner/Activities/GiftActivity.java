package com.example.planner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.planner.R;

public class GiftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);

        TextView pointText = findViewById(R.id.point);
        TextView successText = findViewById(R.id.userSuccessCount);
        TextView allTimeText = findViewById(R.id.userAllTime);

        int point = getIntent().getExtras().getInt("point");
        int success = getIntent().getExtras().getInt("success");
        int allTime = getIntent().getExtras().getInt("allTime");


        pointText.setText(point+"점");
        successText.setText(success+"");
        allTimeText.setText(allTime/3600 + "시간");

        ImageView gift1_1 = findViewById(R.id.gift1_1);
        ImageView gift1_2 = findViewById(R.id.gift1_2);
        ImageView gift1_3 = findViewById(R.id.gift1_3);
        ImageView gift1_4 = findViewById(R.id.gift1_4);
        ImageView gift1_5 = findViewById(R.id.gift1_5);
        ImageView gift1_6 = findViewById(R.id.gift1_6);
        ImageView gift1_7 = findViewById(R.id.gift1_7);
        ImageView gift1_8 = findViewById(R.id.gift1_8);

        ImageView gift2_1 = findViewById(R.id.gift2_1);
        ImageView gift2_2 = findViewById(R.id.gift2_2);
        ImageView gift2_3 = findViewById(R.id.gift2_3);
        ImageView gift2_4 = findViewById(R.id.gift2_4);
        ImageView gift2_5 = findViewById(R.id.gift2_5);
        ImageView gift2_6 = findViewById(R.id.gift2_6);

        //꽃2
        if(point < 10){
            gift1_2.setImageResource(R.drawable.question);
        }
        if(point < 20){
            gift1_3.setImageResource(R.drawable.question);
        }
        if(point < 30){
            gift1_4.setImageResource(R.drawable.question);
        }
        if(point < 40){
            gift1_5.setImageResource(R.drawable.question);
        }
        if(point < 50){
            gift1_6.setImageResource(R.drawable.question);
        }
        if(point < 60){
            gift1_7.setImageResource(R.drawable.question);
        }
        if(point < 70){
            gift1_8.setImageResource(R.drawable.question);
        }

        //꽃2
        if(point < 80){
            gift2_1.setImageResource(R.drawable.question);
        }
        if(point < 90){
            gift2_2.setImageResource(R.drawable.question);
        }
        if(point < 100){
            gift2_3.setImageResource(R.drawable.question);
        }
        if(point < 110){
            gift2_4.setImageResource(R.drawable.question);
        }
        if(point < 120){
            gift2_5.setImageResource(R.drawable.question);
        }
        if(point < 130){
            gift2_6.setImageResource(R.drawable.question);
        }


        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}