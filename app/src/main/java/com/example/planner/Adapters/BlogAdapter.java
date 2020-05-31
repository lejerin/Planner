package com.example.planner.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.planner.Activities.PostActivity;
import com.example.planner.Activities.PostDetailActivity;
import com.example.planner.Models.Post;
import com.example.planner.R;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.MyViewHolder> {

    /*
    BlogAdapter : blogFragment에서 RecyclerView와 연결된 어댑터
    카테고리 별 이미지를 보여준다
    카테고리 클릭시 게시판을 보여주는 PostActivity로 넘어감

     */


    private Context mContext;
    private List<String> mData;


    public BlogAdapter(Context mContext, List<String> mData) {
        this.mContext = mContext;
        this.mData = mData;

    }

    @NonNull
    @Override
    public BlogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_blog_item, parent, false);

        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogAdapter.MyViewHolder holder, int position) {

        holder.category.setText(mData.get(position));

        System.out.println(mData.get(position));
        if(position == 0){
            holder.img_thumb.setImageResource(R.drawable.category1);
        }else if(position == 1){
            holder.img_thumb.setImageResource(R.drawable.category2);
        }
        else if(position == 2){
            holder.img_thumb.setImageResource(R.drawable.category3);
        }
        else if(position == 3){
            holder.img_thumb.setImageResource(R.drawable.category4);
        }else{
            holder.img_thumb.setImageResource(R.drawable.category5);
        }



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView category;
        ImageView img_thumb;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.category);
            img_thumb = itemView.findViewById(R.id.img_thumb);


            //PostActivity로 넘어감
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postActivity = new Intent(mContext, PostActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    int position = getAdapterPosition();
                    postActivity.putExtra("category", (position+1)+"");
                    System.out.println((position+1)+"");
                    mContext.startActivity(postActivity);
                }
            });
        }
    }
}
