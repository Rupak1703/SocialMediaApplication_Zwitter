package com.example.zwitter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zwitter.Models.Friends_in_profile_model;
import com.example.zwitter.R;

import java.util.ArrayList;

public class FriendsAdapterForProfile_RV extends RecyclerView.Adapter<FriendsAdapterForProfile_RV.viewHolder> {
    ArrayList<Friends_in_profile_model> list;
    Context context;

    public FriendsAdapterForProfile_RV(ArrayList<Friends_in_profile_model> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_in_profile_rv , parent , false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Friends_in_profile_model model = list.get(position);
        holder.profile.setImageResource(model.getProfile());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView profile;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.friends_profile_for_rv);
        }
    }
}
