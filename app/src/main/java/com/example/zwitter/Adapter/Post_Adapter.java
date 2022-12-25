package com.example.zwitter.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zwitter.CommentsActivity;
import com.example.zwitter.Models.Notification_model;
import com.example.zwitter.Models.Post_model;
import com.example.zwitter.Models.UserModel;
import com.example.zwitter.R;
import com.example.zwitter.databinding.DashboardRvSampleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.viewHolder>{

    ArrayList<Post_model> list;
    Context context;

    public Post_Adapter(ArrayList<Post_model> list, Context context) {
        this.list = list;
        this.context = context;
    }



    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv_sample , parent , false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Post_model model = list.get(position);

        Picasso.get()
                .load(model.getPostImage())
                .placeholder(R.drawable.placeholderimage)
                .into(holder.binding.postImage);

        holder.binding.likeCount.setText(model.getPostLiked() + "");
        holder.binding.commentsCount.setText(model.getCommentsCount() + "");

        String description = model.getPost_description();
        if (description.equals("")){
            holder.binding.postDescription.setVisibility(View.GONE);
        } else {
            holder.binding.postDescription.setText(model.getPost_description());
            holder.binding.postDescription.setVisibility(View.VISIBLE);
        }

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getPosted_by())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.ap4)
                                .into(holder.binding.profileImgPost);
                        holder.binding.userName.setText(user.getName());
                        holder.binding.about.setText(user.getProfession());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                        .child("Posts")
                        .child(model.getPost_id())
                        .child("likes")
                        .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.binding.like.setImageResource(R.drawable.heartdark);
                        } else {
                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Posts")
                                            .child(model.getPost_id())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts")
                                                            .child(model.getPost_id())
                                                            .child("postLiked")
                                                            .setValue(model.getPostLiked() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.like.setImageResource(R.drawable.heartdark);


                                                                    /// setting the Notification data of type like
                                                                    Notification_model notification_model = new Notification_model();
                                                                    notification_model.setNotification_by(FirebaseAuth.getInstance().getUid());
                                                                    notification_model.setNotification_at(new Date().getTime());
                                                                    notification_model.setPostID(model.getPost_id());
                                                                    notification_model.setPosted_by(model.getPosted_by());
                                                                    notification_model.setType("like");

                                                                    FirebaseDatabase.getInstance()
                                                                            .getReference()
                                                                            .child("notification")
                                                                            .child(model.getPosted_by())
                                                                            .push()
                                                                            .setValue(notification_model);

                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , CommentsActivity.class);
                intent.putExtra("postId" , model.getPost_id());
                intent.putExtra("postedBy" , model.getPosted_by());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        DashboardRvSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DashboardRvSampleBinding.bind(itemView);
        }
    }
}
