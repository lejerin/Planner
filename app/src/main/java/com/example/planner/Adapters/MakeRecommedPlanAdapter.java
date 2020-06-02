package com.example.planner.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.planner.R;
import com.example.planner.Realm.Plans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class MakeRecommedPlanAdapter extends RealmRecyclerViewAdapter<Plans, MakeRecommedPlanAdapter.MyViewHolder> {

    /*
    PlanFragment에서 계획 RecyclerView와 연결됨
    클릭 날짜의 계획 목록을 보여줌
     */

    Context mContext;

    public MakeRecommedPlanAdapter(Context context, OrderedRealmCollection<Plans> data) {
        super(data, true);

        setHasStableIds(true);
        mContext = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView planTitle,planTime;
        public ImageView emotionImg;


        MyViewHolder(View view) {
            super(view);

            planTitle = (TextView)view.findViewById(R.id.planTitle);
            planTime = (TextView)view.findViewById(R.id.planTime);
            emotionImg = (ImageView)view.findViewById(R.id.emotionImg);


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_makeplan_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        final Plans obj = (Plans) getItem(position);

        final String title = obj.getTitle();

        Date startTime = obj.getStartTime();
        Date endTime = obj.getEndTime();
        int isSuccess = obj.getSuccess();

        // View에 값 binding하기
        holder.planTitle.setText(title);
        holder.planTime.setText(getIntToTimeString(obj.getDuration()));


        if(isSuccess == 1){
            holder.emotionImg.setImageResource(R.drawable.success_emotion);
        }else if(isSuccess == 2){
            holder.emotionImg.setImageResource(R.drawable.fail_emotion);
        }else{
            holder.emotionImg.setImageResource(R.drawable.notstart_emotion);
        }



    }



    private String getIntToTimeString(int duration){

        long getHour = duration / (60 * 60);
        String hour = String.valueOf(getHour);

        // 분단위
        long getMin = duration - (getHour*(60 * 60)) ;
        String min = String.valueOf(getMin / (60)); // 몫

        // 초단위
        String second = String.valueOf((getMin % (60))); // 나머지

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

        return hour+"시간 " + min + "분";
    }

}

