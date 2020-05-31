package com.example.planner.fragment;



import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.planner.Activities.LoginActivity;
import com.example.planner.R;
import com.google.firebase.auth.FirebaseAuth;

/*

settingFragment : 사용자 닉네임,프로필 사진 변경, 로그아웃, 성장나무 확인? , 기타
 */
public class SettingFragment extends Fragment {

    Button logoutBtn;

    public SettingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);


        logoutBtn = view.findViewById(R.id.logoutBtn);

        //로그아웃 클릭 이벤트
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent loginActivity = new Intent(getContext(), LoginActivity.class);
                startActivity(loginActivity);
                getActivity().finish();
            }
        });

        return view;
    }


}
