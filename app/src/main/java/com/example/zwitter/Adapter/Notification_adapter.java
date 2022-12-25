package com.example.zwitter.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zwitter.CommentsActivity;
import com.example.zwitter.Models.Notification_model;
import com.example.zwitter.Models.UserModel;
import com.example.zwitter.R;
import com.example.zwitter.databinding.NotificationRvSampleBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Notification_adapter extends RecyclerView.Adapter<Notification_adapter.viewHolder> {
    ArrayList<Notification_model> list;
    Context context;

    public Notification_adapter(ArrayList<Notification_model> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv_sample , parent , false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Notification_model model = list.get(position);

        String type = model.getType(); /// KIS TYPE KA NOTIFICATION HAI

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getNotification_by())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel model = snapshot.getValue(UserModel.class);

                        /// Pic internet se la rahe hai
                        Picasso.get().load(model.getProfile())
                                .placeholder(R.drawable.ap4)
                                .into(holder.binding.profileImgAtNotification);

                        if (type.equals("like")){
                            holder.binding.showNotification.setText(Html.fromHtml("<b>"+model.getName()+"</b>" + " liked your post."));
                        } else if (type.equals("comment")){
                            holder.binding.showNotification.setText(Html.fromHtml("<b>"+model.getName()+"</b>" + " commented on your post."));
                        } else {
                            holder.binding.showNotification.setText(Html.fromHtml("<b>"+model.getName()+"</b>" + " Started following you."));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // SETTING UP THE TIME
        String time = TimeAgo.using(model.getNotification_at());
        holder.binding.showCommentTime.setText(time);

        /// when click on the comment some action must be performed
        holder.binding.notifyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!model.getType().equals("follow")) {

                    FirebaseDatabase.getInstance().getReference()
                                    .child("notification")
                                            .child(model.getPosted_by())
                                                    .child(model.getNotificationID())
                                                            .child("check_open")
                                                                    .setValue(true);
                    holder.binding.notifyBox.setBackgroundColor(Color.parseColor("#DDD8D8"));

                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.putExtra("postId", model.getPostID());
                    intent.putExtra("postedBy", model.getPosted_by());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        if (!model.getType().equals("follow")) {
            Boolean checkOpen = model.isCheck_open();
            if (checkOpen) {
                holder.binding.notifyBox.setBackgroundColor(Color.parseColor("#DDD8D8"));
            } else {
                // EAT FIVE STAR DO NOTHING :)  ------->  * * * * *
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        NotificationRvSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationRvSampleBinding.bind(itemView);
        }
    }
}
