package com.example.planner.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.planner.Adapters.PostAdapter;
import com.example.planner.Helpers.DividerItemDecoration;
import com.example.planner.Models.Post;
import com.example.planner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    /*
    등록된 카테고리별 게시물 갖고 온 뒤 보여주고 새로 게시물 작성해서 올려주는 액티비티
    게시물 상세보기
     */

    //파이어베이스 관련
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private static final int PReqCode = 2;
    private static final int REQUESCODE = 3;

    private Dialog popAddPost;
    private ImageView popupUserImage, popupPostImage, popupAddBtn;
    private ImageButton popupBackBtn, postBackBtn;
    private TextView popupTitle,popupDescription,categoryName;
    private ProgressBar popupProgressBar;

    //게시물 목록 관련
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    //선택한 카테고리
    private String category;
    private Uri pickedImgUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        category = getIntent().getExtras().getString("category");

        postBackBtn = findViewById(R.id.backBtn);
        categoryName = findViewById(R.id.categoryName);
        postRecyclerView = findViewById(R.id.postRecyclerView);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postRecyclerView.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), R.drawable.divider));

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("category" + category + "/Posts");


        categoryName.setText(getCategoryName(category));
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        iniPopup();
        setupPopupImageClick();

        postBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fb = findViewById(R.id.floatingActionButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.show();
            }
        });
    }


    private String getCategoryName(String a){

        switch (a){
            case "1":
                return "고등학생 게시판";
            case "2":
                return "대학생 게시판";
            case "3":
                return "직장인 게시판";
            case "4":
                return "공시생 게시판";
            case "5":
                return "기타 게시판";
            default:
                return "기타 게시판";
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //게시물 목록 갖고온뒤 보여주기
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for(DataSnapshot postsnap: dataSnapshot.getChildren()){
                    Post post = postsnap.getValue(Post.class);
                    postList.add(0,post);
                }
                postAdapter = new PostAdapter(getApplicationContext(),postList,category);
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //게시물 이미지 등록
    private void setupPopupImageClick() {

        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });

    }

    //갤러리 사용자 권한 확인
    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);

        }else{
            openGallery();
        }

    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){
            pickedImgUri = data.getData();
            popupPostImage.setImageURI(pickedImgUri);
        }

    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }


    //뒤로가기 눌렀을 때(게시물 작성 취소했을 때)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        popupPostImage.setImageResource(R.drawable.camera_add);
        popupTitle.setText("");
        popupDescription.setText("");

    }

    //게시물 작성 팝업 초기화
    protected void iniPopup(){

        popAddPost = new Dialog(this);
        View view  = getLayoutInflater().inflate(R.layout.popup_add_post, null);
        popAddPost.setContentView(view);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;
        //popAddPost.setCancelable(true);

        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_disciption);
        popupProgressBar = popAddPost.findViewById(R.id.popup_progressBar);
        popupBackBtn = popAddPost.findViewById(R.id.backBtn);

        popupBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.dismiss();
                popupPostImage.setImageResource(R.drawable.camera_add);
                popupTitle.setText("");
                popupDescription.setText("");
            }
        });


        if(currentUser.getPhotoUrl() != null){
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(popupUserImage);
        }else{
            Glide.with(this)
                    .load(R.drawable.success_emotion)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(popupUserImage);
        }


        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupProgressBar.setVisibility(View.VISIBLE);

                if(!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.toString().isEmpty()){


                    if(pickedImgUri != null){
                        //파이어베이스 저장
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                        final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String imageDownloadLink = uri.toString();

                                        if(currentUser.getPhotoUrl() != null){
                                            Post post = new Post(popupTitle.getText().toString(),
                                                    popupDescription.getText().toString(),
                                                    imageDownloadLink,
                                                    currentUser.getUid(),
                                                    currentUser.getPhotoUrl().toString());
                                            addPost(post);
                                        }else{
                                            Post post = new Post(popupTitle.getText().toString(),
                                                    popupDescription.getText().toString(),
                                                    imageDownloadLink,
                                                    currentUser.getUid(),null);
                                            addPost(post);
                                        }



                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessage(e.getMessage());
                                        popupProgressBar.setVisibility(View.INVISIBLE);
                                        popupAddBtn.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        });
                    }else{
                        //사진이 비어있으면
                        if(currentUser.getPhotoUrl() != null){
                            Post post = new Post(popupTitle.getText().toString(),
                                    popupDescription.getText().toString(),
                                    null,
                                    currentUser.getUid(),
                                    currentUser.getPhotoUrl().toString());
                            addPost(post);
                        }else{
                            Post post = new Post(popupTitle.getText().toString(),
                                    popupDescription.getText().toString(),
                                    null,
                                    currentUser.getUid(),null);
                            addPost(post);
                        }

                    }



                }else{
                    showMessage("Please Verify all fields and choose post img");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupProgressBar.setVisibility(View.INVISIBLE);
                }


            }
        });

    }



    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("category" + category + "/Posts").push();

        String key = myRef.getKey();
        post.setPostKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added successful");
                popupProgressBar.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();

                popupPostImage.setImageResource(R.drawable.camera_add);
                popupTitle.setText("");
                popupDescription.setText("");
            }
        });
    }
}
