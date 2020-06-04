package com.example.planner.fragment;



import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.planner.Activities.LoginActivity;
import com.example.planner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*

settingFragment : 사용자 닉네임,프로필 사진 변경, 로그아웃, 성장나무 확인? , 기타
 */
public class SettingFragment extends Fragment {

    Button logoutBtn;
    ImageView userImg;
    TextView userNickName, userEmail;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public SettingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        userImg = view.findViewById(R.id.user_img);
        userNickName = view.findViewById(R.id.user_nickname);
        userEmail = view.findViewById(R.id.user_email);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        logoutBtn = view.findViewById(R.id.logoutBtn);




        setUserInfo();


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

    private void setUserInfo(){

        //프로필 사진
        if(currentUser.getPhotoUrl() != null){
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(userImg);
        }else{
            Glide.with(this)
                    .load(R.drawable.success_emotion)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(userImg);
        }
        userEmail.setText(currentUser.getEmail());
        userNickName.setText(currentUser.getDisplayName());


    }


}
