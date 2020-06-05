package com.example.planner.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.planner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class RegisterActivity extends AppCompatActivity {

    /*
    RegisterActivity : 회원가입 액티비티

     */

    private ImageView imgUserPhoto;
    private Uri pickedImgUri;
    private EditText userName, userEmail,userPassword,userPassword2;
    private ProgressBar loadingProgress;
    private Button regBtn;

    private static int PICK_USER_PHOTO = 1;
    private static int PERMISSION_OPEN_GALLERY = 2;

    //파이어베이스 사용자 인증
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        //init view item
        userName = findViewById(R.id.regName);
        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regPassword2);
        regBtn = findViewById(R.id.regText);
        loadingProgress = findViewById(R.id.progressBar);
        imgUserPhoto = findViewById(R.id.loginPhoto);

        loadingProgress.setVisibility(View.INVISIBLE);

        //회원가입 확인 클릭 이벤트
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                //입력란으로부터 String 갖고오기
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()
                        || !password.equals(password2)){
                    //입력 한칸이라도 비어있으면

                    showMessage("Please Verify all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }else{
                    //모두 입력 되었으면
                    createUserAccount(name,email,password);
                }

            }
        });

        //프로필 이미지 추가 클릭 이벤트
        imgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22){
                    //갤러리 오픈 권한 요청
                    checkAndRequestForPermission();
                }else{
                    openGallery();
                }
            }
        });

    }


    //회원가입 파이어베이스에 회원 등록
    private void createUserAccount(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMessage("Account created");


                            //사진이 없다면
                            if(pickedImgUri != null){
                                updateUserInfo( name, pickedImgUri, mAuth.getCurrentUser());
                            }else {
                                updateUserInfoWithoutPhoto( name, mAuth.getCurrentUser());
                            }



                        }else{
                            //회원가입 등록 실패
                            showMessage("account creation failed"+ task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });

    }

    //사용자 이미지 DB에 저장
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            showMessage("Register complete");
                                            successRegisterAccount();
                                        }
                                    }
                                });
                    }
                });

            }
        });
    }

    private void updateUserInfoWithoutPhoto(final String name, final FirebaseUser currentUser) {

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            showMessage("Register complete");
                            successRegisterAccount();
                        }
                    }
                });

    }


    private void successRegisterAccount() {




        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
        LoginActivity LA = (LoginActivity)LoginActivity._Login_Activity;
        LA.finish();
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    //사진 등록을 위한 갤러리 열기
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_USER_PHOTO);
    }


    //갤러리 열기위한 사용자 권한 확인
    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_OPEN_GALLERY);

        }else{
            openGallery();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_USER_PHOTO && data != null){
            //사용자가 프로필 이미지 사진을 선택하였을 때
            pickedImgUri = data.getData();
            imgUserPhoto.setImageURI(pickedImgUri);
        }
    }

}