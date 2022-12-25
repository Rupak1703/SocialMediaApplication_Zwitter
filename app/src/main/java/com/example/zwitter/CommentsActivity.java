package com.example.zwitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.zwitter.Adapter.CommentsAdapter;
import com.example.zwitter.Models.CommentsModel;
import com.example.zwitter.Models.Notification_model;
import com.example.zwitter.Models.Post_model;
import com.example.zwitter.Models.UserModel;
import com.example.zwitter.databinding.ActivityCommentsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentsActivity extends AppCompatActivity {
    ActivityCommentsBinding binding;
    Intent intent;
    String postId;
    String postedBy;

    FirebaseDatabase database;
    FirebaseAuth auth;

    ArrayList<CommentsModel> list = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// for toolbar
        setSupportActionBar(binding.toolbar);
        CommentsActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        database.getReference()
                .child("Posts")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post_model post_model = snapshot.getValue(Post_model.class);

                        Picasso.get().load(post_model.getPostImage())
                                .placeholder(R.drawable.ap4).into(binding.postImage);

                        binding.description.setText(post_model.getPost_description());
                        binding.likeCount.setText(post_model.getPostLiked() + "");
                        binding.commentsCount.setText(post_model.getCommentsCount() + "");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        database.getReference()
                .child("Users")
                .child(postedBy)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);

                        Picasso.get().load(user.getProfile()).placeholder(R.drawable.ap4).into(binding.profileImg);
                        binding.userName.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommentsModel comment = new CommentsModel();
                comment.setCommentBody(binding.writeComment.getText().toString());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentedBy(FirebaseAuth.getInstance().getUid());

                database.getReference()
                        .child("Posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(comment)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                /// counting the comments
                                database.getReference()
                                        .child("Posts")
                                        .child(postId)
                                        .child("commentsCount")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentsCount = 0;
                                                if (snapshot.exists()) {
                                                    commentsCount = snapshot.getValue(Integer.class);
                                                }

                                                /// SETTING UP THE COUNT
                                                database.getReference()
                                                        .child("Posts")
                                                        .child(postId)
                                                        .child("commentsCount")
                                                        .setValue(commentsCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(CommentsActivity.this, "Commented", Toast.LENGTH_SHORT).show();
                                                                binding.writeComment.setText("");


                                                                /// NOTIFICATION WORK FOR THE **COMMENT** TO THE USER
                                                                Notification_model notification_model = new Notification_model();
                                                                notification_model.setNotification_by(FirebaseAuth.getInstance().getUid());
                                                                notification_model.setNotification_at(new Date().getTime());
                                                                notification_model.setPostID(postId);
                                                                notification_model.setPosted_by(postedBy);
                                                                notification_model.setType("comment");

                                                                FirebaseDatabase.getInstance()
                                                                        .getReference()
                                                                        .child("notification")
                                                                        .child(postedBy)
                                                                        .push()
                                                                        .setValue(notification_model);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        });
            }
        });


        CommentsAdapter adapter = new CommentsAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.commentRV.setLayoutManager(linearLayoutManager);
        binding.commentRV.setAdapter(adapter);

        database.getReference()
                .child("Posts")
                .child(postId)
                .child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CommentsModel commentsModel = dataSnapshot.getValue(CommentsModel.class);
                            list.add(commentsModel);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}