package com.example.planner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    private RecyclerView blogRecyclerView;
    private BlogAdapter blogAdapter;

    public BlogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);

        List<String> str = Arrays.asList("고등학생","대학생","직장인","공시생","기타");

        blogRecyclerView = view.findViewById(R.id.blogRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        blogRecyclerView.setLayoutManager(mLayoutManager);
        blogRecyclerView.addItemDecoration(new ItemDecoration(getActivity()));
        blogAdapter = new BlogAdapter(getContext(), str);
        blogRecyclerView.setAdapter(blogAdapter);


        return view;
    }



}
