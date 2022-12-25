package com.example.zwitter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zwitter.Models.Story_model;
import com.example.zwitter.Models.UserModel;
import com.example.zwitter.Models.UserStories;
import com.example.zwitter.R;
import com.example.zwitter.databinding.StoryDesignRvBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class Story_Adapter extends RecyclerView.Adapter<Story_Adapter.viewHolder>{

    ArrayList<Story_model> list;
    Context context;

    public Story_Adapter(ArrayList<Story_model> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_design_rv , parent , false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Story_model story = list.get(position);

        if (story.getStories().size() > 0) { /// if its his first story then we will not perform any of this

            UserStories lastStory = story.getStories().get(story.getStories().size() - 1); /// last story :)
            Picasso.get().load(lastStory.getStory_image())
                    .into(holder.binding.story); /// reading the value from the database

            holder.binding.statusCircle.setPortionsCount(story.getStories().size());

            /// getting the data of the user
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(story.getStoryBy())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel user = snapshot.getValue(UserModel.class);

                            /// getting the value from the database
                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.ap4)
                                    .into(holder.binding.profileImgStory);

                            /// setting up the name of the user
                            holder.binding.name.setText(user.getName());


                            holder.binding.story.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (UserStories stories : story.getStories()) {
                                        myStories.add(new MyStory(
                                                stories.getStory_image()
                                        ));
                                    }

                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories) // Required
                                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                            .setTitleText(user.getName()) // Default is Hidden
                                            .setSubtitleText("") // Default is Hidden
                                            .setTitleLogoUrl(user.getProfile()) // Default is Hidden
                                            .setStoryClickListeners(new StoryClickListeners() {
                                                @Override
                                                public void onDescriptionClickListener(int position) {
                                                    //your action
                                                }

                                                @Override
                                                public void onTitleIconClickListener(int position) {
                                                    //your action
                                                }
                                            }) // Optional Listeners
                                            .build() // Must be called before calling show method
                                            .show();

                                }
                            });


                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        StoryDesignRvBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StoryDesignRvBinding.bind(itemView);
        }
    }
}
