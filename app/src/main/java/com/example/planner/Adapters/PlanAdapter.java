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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planner.R;
import com.example.planner.Realm.Plans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class PlanAdapter extends RealmRecyclerViewAdapter<Plans, PlanAdapter.MyViewHolder> {

    /*
    PlanFragment에서 계획 RecyclerView와 연결됨
    클릭 날짜의 계획 목록을 보여줌
     */
    public interface OnModifyClickListener {
        void onModifyClick(int pos) ;
    }
    private OnModifyClickListener mModifyListener = null ;
    public void setOnModifyClickListener(OnModifyClickListener listener) {
        this.mModifyListener = listener ;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int pos) ;
    }
    private OnDeleteClickListener mDeleteListener = null ;
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.mDeleteListener = listener ;
    }

    Context mContext;

    public PlanAdapter(Context context, OrderedRealmCollection<Plans> data) {
        super(data, true);

        setHasStableIds(true);
        mContext = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView planTitle,planTime;
        public ImageView emotionImg;
        public ImageButton moreBtn;

        MyViewHolder(View view) {
            super(view);


            planTitle = (TextView)view.findViewById(R.id.planTitle);
            planTime = (TextView)view.findViewById(R.id.planTime);
            emotionImg = (ImageView)view.findViewById(R.id.emotionImg);
            moreBtn = (ImageButton)view.findViewById(R.id.moreBtn);


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_calendar_item, parent, false);

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
        holder.planTime.setText(changeDateToStr(startTime) + " - "+ changeDateToStr(endTime));

        if(isSuccess == 1){
            holder.emotionImg.setImageResource(R.drawable.success_emotion);
        }else if(isSuccess == 2){
            holder.emotionImg.setImageResource(R.drawable.fail_emotion);
        }else{
            holder.emotionImg.setImageResource(R.drawable.notstart_emotion);
        }


        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.moreBtn);
                //inflating menu from xml resource
                popup.inflate(R.menu.plan_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.modify:
                                //handle menu1 click
                                int clicknum = position;
                                mModifyListener.onModifyClick(clicknum); ;
                                return true;
                            case R.id.delete:
                                //handle menu2 click
                                int clicnum = position;
                                mDeleteListener.onDeleteClick(clicnum); ;
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });


    }

    private String changeDateToStr(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("a hh:mm", Locale.ENGLISH);

        return dateFormat.format(date);
    }


}

