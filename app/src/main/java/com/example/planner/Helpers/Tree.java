package com.example.planner.Helpers;

import android.content.Context;

import com.example.planner.R;

public class Tree {
    /*
    달성기준: 성공여부, 1시간 (중복 가능)
    한번 성공할 때마다 +1
    1시간마다 +1
    단계
    꽃1 : 10,20,30,40,50,60,70,80
    꽃2 : 90,100,110,120,130


     */

    public Tree() {

    }

    public int getNowTree(int successCount, int timeCount){

        timeCount = timeCount/3600;

        int point = successCount + timeCount;

        //꽃2
        if(point >= 130){
            return R.drawable.gift2_6;
        }
        if(point >= 120){
            return R.drawable.gift2_6;
        }
        if(point >= 110){
            return R.drawable.gift2_5;
        }

        if(point >= 100){
            return R.drawable.gift2_3;
        }

        if(point >= 90){
            return R.drawable.gift2_2;
        }

        if(point >= 80){
            return R.drawable.gift2_1;
        }

        //꽃1
        if(point >= 70){
            return R.drawable.gift1_8;
        }
        if(point >= 60){
            return R.drawable.gift1_7;
        }
        if(point >= 50){
            return R.drawable.gift1_6;
        }

        if(point >= 40){
            return R.drawable.gift1_5;
        }

        if(point >= 30){
            return R.drawable.gift1_4;
        }

        if(point >= 20){
            return R.drawable.gift1_3;
        }
        if(point >= 10){
            return R.drawable.gift1_2;
        }


        return R.drawable.gift1_1;
    }
}
