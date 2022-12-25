package com.example.zwitter.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zwitter.Adapter.FollowersAdapter;
import com.example.zwitter.Adapter.FriendsAdapterForProfile_RV;
import com.example.zwitter.LoginActivity;
import com.example.zwitter.MainActivity;
import com.example.zwitter.Models.FollowModel;
import com.example.zwitter.Models.Friends_in_profile_model;
import com.example.zwitter.Models.UserModel;
import com.example.zwitter.R;
import com.example.zwitter.R.id;
import com.example.zwitter.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<FollowModel> list;
    FragmentProfileBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(getLayoutInflater() , container , false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(id.friend_rv);

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel user = snapshot.getValue(UserModel.class);

                    Picasso.get()
                            .load(user.getCoverPhoto()).placeholder(R.drawable.superfries)
                            .into(binding.settingImageShow);

                    Picasso.get()
                            .load(user.getProfile()).placeholder(R.drawable.ap4)
                            .into(binding.settingImgProfile);

                    binding.userNameSettings.setText(user.getName());
                    binding.aboutSettings.setText(user.getProfession());

                    binding.followersCount.setText(user.getFollowerCount() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        list = new ArrayList<>();


        FollowersAdapter followersAdapter = new FollowersAdapter(list , getContext());
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(getContext() , LinearLayoutManager.HORIZONTAL , false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(followersAdapter);

        database.getReference().child("Users")
                .child(auth.getUid())
                .child("followers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            FollowModel followModel = dataSnapshot.getValue(FollowModel.class);
                            list.add(followModel);
                        }
                        followersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        /// **********************************************************************************************************************
        binding.changeCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , 10);
            }
        });

        binding.blueTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , 11);
            }
        });

        binding.settingImageShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getContext() , LoginActivity.class);
                startActivity(intent);
            }
        });

        return view; /// note you have to add view here not binding.getRoot() this will not show your recycler view...
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10){
            if (data.getData() != null){
                // From Gallery we get Images of "Uri" type
                Uri uri = data.getData(); // now selected image is there in our URI
                binding.settingImageShow.setImageURI(uri);

                final StorageReference storageReference = storage.getReference().child("cover_photo")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "cover photo saved", Toast.LENGTH_SHORT).show();

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) { /// downloaded image will come to this uri
                                database.getReference().child("Users").child(auth.getUid()).child("CoverPhoto").setValue(uri.toString());
                            }
                        });
                    }
                });
            }

        } else {
            if (data.getData() != null){
                // From Gallery we get Images of "Uri" type
                Uri uri = data.getData(); // now selected image is there in our URI
                binding.settingImgProfile.setImageURI(uri);

                final StorageReference storageReference = storage.getReference().child("profile_image")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Profile photo saved", Toast.LENGTH_SHORT).show();

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) { /// downloaded image will come to this uri
                                database.getReference().child("Users").child(auth.getUid()).child("profile").setValue(uri.toString());
                            }
                        });
                    }
                });


            }

        }
    }
}