package com.example.planner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    /*
    LoginActivity : 앱 실행시 나타나는 로그인 액티비티, 파이어베이스 Auth 기능을 이용하여 사용자 인증 구현
    이메일, 비밀번호를 입력 -> HomeActivity 로 넘어감
    회원가입 클릭 -> RegisterActivity 로 넘어감
     */

    private EditText userEmail,userPassword;
    private TextView regText;
    private ProgressBar loadingProgress;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    public static Activity _Login_Activity;
    private Intent homeActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _Login_Activity = LoginActivity.this;
        homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        mAuth = FirebaseAuth.getInstance();

        //init view item
        userEmail = findViewById(R.id.loginMail);
        userPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        loadingProgress = findViewById(R.id.progressBar);
        regText = findViewById(R.id.regText);



        loadingProgress.setVisibility(View.INVISIBLE);

        //로그인 버튼 클릭 이벤트
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    //이메일, 비밀번호 입력칸이 둘 중 하나라도 비어있을 때
                    showMessage("Please Verify all fields");
                    loginBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }else{
                    //이메일, 비밀번호 입력칸이 모두 입력 됨.
                    //로그인 시도
                    signIn(email,password);
                    
                }
            }
        });

        //회원가입 클릭 이벤트
        regText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAccount();
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //로그인 성공
                    loadingProgress.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    successLogin();
                }else{
                    //로그인 실패
                    showMessage("account login failed"+ task.getException().getMessage());
                    loadingProgress.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //로그인 성공
    private void successLogin() {
        //홈 액티비티로 넘어감
        startActivity(homeActivity);
        finish();
    }

    //회원가입
    private void registerAccount() {
        Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(registerActivity);
    }

    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //로그인 기록이 있으면 자동로그인
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            showMessage("자동 로그인 성공");
            successLogin();
        }
    }
}
