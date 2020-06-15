package com.example.planner.Adapters;


import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.planner.Activities.PostDetailActivity;
import com.example.planner.Models.Post;
import com.example.planner.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    /*
    PostAcitivity의 게시글 RecylerView와 연결됨
    게시글 목록 보여줌
     */
    private Context mContext;
    private List<Post> mData;
    private String category;

    public PostAdapter(Context mContext, List<Post> mData, String category) {
        this.mContext = mContext;
        this.mData = mData;
        this.category = category;
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);


        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {

        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.tvDesc.setText(mData.get(position).getDescription());
        holder.tvTime.setText(formatTimeString((long)mData.get(position).getTimeStamp()));

        //  Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);


        String userImg = mData.get(position).getUserPhoto();
        if(userImg != null){
            Glide.with(mContext)
                    .load(userImg)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(holder.imgPostUser);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.success_emotion)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(holder.imgPostUser);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvDesc, tvTime;
        //ImageView imgPost;
        ImageView imgPostUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            tvDesc = itemView.findViewById(R.id.row_post_desc);
            tvTime = itemView.findViewById(R.id.row_post_time);

          //  imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostUser = itemView.findViewById(R.id.row_post_user);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage",mData.get(position).getPicture());
                    postDetailActivity.putExtra("description",mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());
                    postDetailActivity.putExtra("category",category);

                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp);
                    mContext.startActivity(postDetailActivity);
                }
            });
        }
    }


    private String formatTimeString(long time){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            calendar.setTimeInMillis(time);
            String a = DateFormat.format("yyyy-MM-dd HH:mm:ss",calendar).toString();
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date to = transFormat.parse(a);
                long regTime = to.getTime();
                long curTime = new Date().getTime() - 9*60*60*1000;

                long diffTime = (curTime - regTime) / 1000;
                String msg = null;
                if (diffTime < 60) {
                    msg = "방금 전";
                } else if ((diffTime /= 60) < 60) {
                    msg = diffTime + "분 전";
                } else if ((diffTime /= 60) < 24) {
                    msg = (diffTime) + "시간 전";
                } else if ((diffTime /= 24) < 30) {
                    msg = (diffTime) + "일 전";
                } else if ((diffTime /= 30) < 12) {
                    msg = (diffTime) + "달 전";
                } else {
                    msg = (diffTime) + "년 전";
                }
                return msg;

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }



        return "";
    }
}
