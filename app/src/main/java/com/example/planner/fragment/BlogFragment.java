package com.example.planner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planner.Activities.PostActivity;
import com.example.planner.Adapters.BlogAdapter;
import com.example.planner.Helpers.ItemDecoration;
import com.example.planner.R;

import java.util.Arrays;
import java.util.List;

/*

blogFragment : 사용자 카테고리별로 게시판을 나눔, 카테고리 클릭하면 각 카테고리의 PostActivity 로 넘어감
고등학생, 대학생, 직장인, 공시생, 기타
 */
public class BlogFragment extends Fragment {

//    private RecyclerView blogRecyclerView;
//    private BlogAdapter blogAdapter;

    private ConstraintLayout category1,category2,category3,category4,category5;

    public BlogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);

//        List<String> str = Arrays.asList("고등학생","대학생","직장인","공시생","기타");
//
//        blogRecyclerView = view.findViewById(R.id.blogRecyclerView);
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
//        blogRecyclerView.setLayoutManager(mLayoutManager);
//        blogRecyclerView.addItemDecoration(new ItemDecoration(getActivity()));
//        blogAdapter = new BlogAdapter(getContext(), str);
//        blogRecyclerView.setAdapter(blogAdapter);


        category1 = view.findViewById(R.id.category_1);
        category2 = view.findViewById(R.id.category_2);
        category3 = view.findViewById(R.id.category_3);
        category4 = view.findViewById(R.id.category_4);
        category5 = view.findViewById(R.id.category_5);

        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostCategory(1);
            }
        });
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostCategory(2);
            }
        });
        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostCategory(3);
            }
        });
        category4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostCategory(4);
            }
        });
        category5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostCategory(5);
            }
        });


        return view;
    }

    private void showPostCategory(int position){
        Intent postActivity = new Intent(getContext(),PostActivity.class);
        postActivity.putExtra("category", (position)+"");
        startActivity(postActivity);
    }



}
