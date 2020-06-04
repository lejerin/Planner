package com.example.planner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.planner.Adapters.CommentAdapter;
import com.example.planner.Models.Comment;
import com.example.planner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    /*
    글 상세보기
    글 제목, 등록 날짜, 작성자 이름, 이미지, 댓글 목록 등 불러오기
     */

    private ImageView imgPost, imgUserPost, imgCurrentUser;
    private TextView txtPostDesc, txtPostDateName, txtPostTitle;
    private EditText editTextComment;
    private ImageButton btnAddComment;
    private ImageButton backBtn;
    private String postKey;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    static String COMMENT_KEY = "Comment";
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);


        category = getIntent().getExtras().getString("category");

        backBtn = findViewById(R.id.backBtn);
        imgPost = findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detail_user);
        imgCurrentUser = findViewById(R.id.post_detail_current_user);

        txtPostTitle = findViewById(R.id.post_detail_title);
        txtPostDesc = findViewById(R.id.post_detail_desc);
        txtPostDateName = findViewById(R.id.post_detail_date_name);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment);

        commentRecyclerView = findViewById(R.id.rv_comment);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddComment.setVisibility(View.INVISIBLE);

                DatabaseReference commentReference = firebaseDatabase.getReference("category" + category + "/" + COMMENT_KEY).child(postKey).push();
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();


                if(firebaseUser.getPhotoUrl() != null){
                    String uimg = firebaseUser.getPhotoUrl().toString();
                    Comment comment = new Comment(comment_content, uid, uimg, uname);
                    commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessage("comment added");
                            editTextComment.setText("");
                            btnAddComment.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage("fail to add comment" + e.getMessage());
                        }
                    });


                }else{
                    Comment comment = new Comment(comment_content, uid, null, uname);
                    commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessage("comment added");
                            editTextComment.setText("");
                            btnAddComment.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage("fail to add comment" + e.getMessage());
                        }
                    });

                }


            }
        });

        String postImage = getIntent().getExtras().getString("postImage");
        if(postImage != null){
            Glide.with(this).load(postImage).into(imgPost);
        }else{
            imgPost.setVisibility(View.GONE);
        }



        String postTitle = getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String userpostImage = getIntent().getExtras().getString("userPhoto");

        if(userpostImage != null){
            Glide.with(this).load(userpostImage)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(imgUserPost);
        }else{
            Glide.with(this).load(R.drawable.success_emotion)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(imgUserPost);
        }



        String postDescription = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);


        if(firebaseUser.getPhotoUrl() != null){
            Glide.with(this).load(firebaseUser.getPhotoUrl())
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(imgCurrentUser);
        }else{
            Glide.with(this).load(R.drawable.success_emotion)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .into(imgCurrentUser);
        }


        postKey = getIntent().getExtras().getString("postKey");

        iniRvComment();

    }

    private void iniRvComment() {

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference("category" + category + "/" + COMMENT_KEY).child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList = new ArrayList<>();
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter = new CommentAdapter(getApplicationContext(),commentList);
                commentRecyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy년 MM월 dd일 HH:MM",calendar).toString();
        return date;
    }
}
