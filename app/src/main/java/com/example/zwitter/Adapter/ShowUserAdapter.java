package com.example.zwitter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zwitter.Models.FollowModel;
import com.example.zwitter.Models.Notification_model;
import com.example.zwitter.Models.UserModel;
import com.example.zwitter.R;
import com.example.zwitter.databinding.ShowAllUserSamplervBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ShowUserAdapter extends RecyclerView.Adapter<ShowUserAdapter.viewHolder>{

    Context context;
    ArrayList<UserModel> list;

    public ShowUserAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_all_user_samplerv , parent , false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        UserModel user = list.get(position);
        Picasso.get().load(user.getProfile())
                .placeholder(R.drawable.ap4)
                .into(holder.binding.profileImg);

        holder.binding.showUserName.setText(user.getName());
        holder.binding.showUserProfession.setText(user.getProfession());

        FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(user.getUserID())
                        .child("followers")
                        .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() { /// this line says kya mai usko follow karta hu ya nhi
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context , R.drawable.following_btn));
                            holder.binding.followBtn.setText("Following");
                            holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.teal_700));
                            holder.binding.followBtn.setEnabled(false); /// means this btn will not respond when clicked
                        } else {

                            // follow button backend
                            holder.binding.followBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FollowModel followModel = new FollowModel();
                                    followModel.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                    followModel.setFollowedAt(new Date().getTime());

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserID())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(followModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(user.getUserID())
                                                            .child("followerCount")
                                                            .setValue(user.getFollowerCount() + 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context , R.drawable.following_btn));
                                                                    holder.binding.followBtn.setText("Following");
                                                                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.teal_700));
                                                                    holder.binding.followBtn.setEnabled(false); /// means this btn will not respond when clicked
                                                                    Toast.makeText(context , "You Followed = " +user.getName() , Toast.LENGTH_SHORT).show();

                                                                    /// DOING WORK RELATED TO THE NOTIFICATION
                                                                    Notification_model notification_model = new Notification_model();
                                                                    notification_model.setNotification_by(FirebaseAuth.getInstance().getUid());
                                                                    notification_model.setNotification_at(new Date().getTime());
                                                                    notification_model.setType("follow");

                                                                    /// now storing the model value in the database
                                                                    FirebaseDatabase.getInstance()
                                                                            .getReference()
                                                                            .child("notification")
                                                                            .child(user.getUserID())
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



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ShowAllUserSamplervBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ShowAllUserSamplervBinding.bind(itemView);
        }
    }
}
