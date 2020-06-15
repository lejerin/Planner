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
import com.example.planner.Models.Comment;
import com.example.planner.Models.Post;
import com.example.planner.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    /*
    PostDetailActivity의 댓글 RecyclerView와 연결 됨
    댓글 목록 보여줌
     */
    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment_item, parent, false);


        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {

        //댓글의 이름과 내용 불러와서 적용시키기
        holder.txtCommentName.setText(mData.get(position).getUname());
        holder.txtCommentContent.setText(mData.get(position).getContent());

        String userImg = mData.get(position).getUimg();
        if(userImg != null){
            Glide.with(mContext)
                    .load(userImg)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(holder.imgCommentUser);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.success_emotion)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(holder.imgCommentUser);
        }


        holder.txtCommentDate.setText(timestampToString((long)mData.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtCommentName, txtCommentContent, txtCommentDate;
        ImageView imgCommentUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCommentName = itemView.findViewById(R.id.comment_user_name);
            txtCommentContent = itemView.findViewById(R.id.comment_content);
            txtCommentDate = itemView.findViewById(R.id.comment_date);
            imgCommentUser = itemView.findViewById(R.id.comment_user_img);


        }
    }

    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("MM/dd hh:mm",calendar).toString();
        return date;
    }

}
