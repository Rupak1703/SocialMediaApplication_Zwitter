package com.example.zwitter.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zwitter.Adapter.Post_Adapter;
import com.example.zwitter.Adapter.Story_Adapter;
import com.example.zwitter.Models.Post_model;
import com.example.zwitter.Models.Story_model;
import com.example.zwitter.Models.UserModel;
import com.example.zwitter.Models.UserStories;
import com.example.zwitter.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {
    RecyclerView story_rv , dashBoard_rv;
    ArrayList<Story_model> story_list;

    ArrayList<Post_model> post_models_list;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;

    RoundedImageView addStoryImage;
    ActivityResultLauncher<String> galleryLauncher;
    ProgressDialog dialog;


    public HomeFragment() {
        // Required empty public constructor
    } /// empty constructor

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(getContext());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        dashBoard_rv = view.findViewById(R.id.dashBoard_rv);
//        dashBoard_rv.showShimmerAdapter();

        /// STORY RECYCLER VIEW *********************************************

        story_rv = view.findViewById(R.id.storyRV);
        story_list = new ArrayList<>();

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading");
        dialog.setMessage("please wait...");
        dialog.setCancelable(false);

        CircleImageView prof_img = view.findViewById(R.id.profile_img);
        FirebaseDatabase.getInstance().getReference() /// SETTING UP THE PROFILE IMAGE
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.ap4)
                                .into(prof_img);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        /// setting the adapter ***********
        Story_Adapter adapter = new Story_Adapter(story_list , getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext() , LinearLayoutManager.HORIZONTAL , false);
        story_rv.setLayoutManager(linearLayoutManager);
        story_rv.setNestedScrollingEnabled(false);  // Here setNestedScrollingEnabled(false) disable scrolling for RecyclerView, so it doesn't intercept scrolling event from NestedScrollView.
        story_rv.setAdapter(adapter);


                    /// getting the data from the database
            database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        story_list.clear();
                        for (DataSnapshot storySnapshot : snapshot.getChildren()){
                            Story_model storyModel = new Story_model();
                            storyModel.setStoryBy(storySnapshot.getKey());
                            storyModel.setStoryAt(storySnapshot.child("postedAt_time").getValue(Long.class));

                            ArrayList<UserStories> stories = new ArrayList<>();
                            for (DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()){
                                UserStories userStories = snapshot1.getValue(UserStories.class);
                                stories.add(userStories);
                            }

                            storyModel.setStories(stories);
                            story_list.add(storyModel);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        //// POST RECYCLER VIEW **************************************
        post_models_list = new ArrayList<>();

        Post_Adapter post_adapter = new Post_Adapter(post_models_list , getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dashBoard_rv.setLayoutManager(layoutManager);
        dashBoard_rv.setAdapter(post_adapter);
        dashBoard_rv.setNestedScrollingEnabled(false);

        /// getting the value from the database --> for posts
        database.getReference()
                .child("Posts")
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post_models_list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post_model post_model = dataSnapshot.getValue(Post_model.class);
                    post_model.setPost_id(dataSnapshot.getKey());
                    post_models_list.add(post_model);
                }

//                dashBoard_rv.hideShimmerAdapter();
                post_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // WORK RELATED TO STORY IS HAPPENING HERE *****************************************************************************************

        addStoryImage = view.findViewById(R.id.addstory);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });

        /// as setActivityForResult() is deprecated so we will use this method (ActivityResultLauncher<String>)
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        /// setting up the image
//                        addStoryImage.setImageURI(result);


                        dialog.show();

                        /// storing the "result -> Uri from the gallery" in the firebase Storage
                        final StorageReference reference_path = storage.getReference()
                                .child("stories")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(new Date().getTime() + ""); /// at what time because user has upload its story as user can upload many stories

                        reference_path.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                /// now we have to store the image in our database as well *****************
                                reference_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                    public void onSuccess(Uri uri) {
                                        Story_model story = new Story_model();
                                        story.setStoryAt(new Date().getTime());

                                        database.getReference()
                                                .child("stories")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("postedAt_time")
                                                .setValue(story.getStoryAt())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        UserStories userStories = new UserStories(uri.toString() , story.getStoryAt());

                                                        database.getReference()
                                                                .child("stories")
                                                                .child(FirebaseAuth.getInstance().getUid())
                                                                .child("userStories")
                                                                .push()
                                                                .setValue(userStories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });

                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                });



        return view;
    }
}