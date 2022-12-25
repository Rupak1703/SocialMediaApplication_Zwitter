
package com.example.zwitter.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.zwitter.Models.Post_model;
import com.example.zwitter.Models.UserModel;
import com.example.zwitter.R;

import com.example.zwitter.databinding.FragmentAddBinding;
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

import java.util.Date;

public class AddFragment extends Fragment {
    FragmentAddBinding binding;
    Uri uri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    /// dialog box while Loading
    ProgressDialog dialog;

    public AddFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater , container, false);

        /// creating a dialog box
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Post Uploading");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        database.getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel userModel = snapshot.getValue(UserModel.class);

                    // setting the image using picasso
                    Picasso.get()
                            .load(userModel.getProfile())
                            .placeholder(R.drawable.ap4)
                            .into(binding.profileImg);


                    binding.showUserName.setText(userModel.getName());
                    binding.showUserProfession.setText(userModel.getProfession());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /// changing the button when something gets written
        binding.postDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String desc = binding.postDescription.getText().toString();
                if (!desc.isEmpty()){
                    binding.postButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext() , R.drawable.follow_btn));
                    binding.postButton.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postButton.setEnabled(true);
                } else {
                    binding.postButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext() , R.drawable.following_btn));
                    binding.postButton.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , 10);
            }
        });

        /// saving the post and showing the post in the main Fragment
        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show(); /// when post button is clicked this dialog box should appear

                /// derive location where to store image
                final StorageReference storageReference = storage.getReference()
                        .child("Posts")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime() + ""); /// at what time the post is posted

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        /// FIRST WE STORE THE IMAGE IN "firebase storage" AND THEN WE STORE IT IN OUR "real time database"
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Post_model post = new Post_model();
                                post.setPostImage(uri.toString());
                                post.setPosted_by(FirebaseAuth.getInstance().getUid());
                                post.setPost_description(binding.postDescription.getText().toString());
                                post.setPosted_at(new Date().getTime());

                                /// now we have stored all the values in the model and we have to add this model in the database
                                database.getReference().child("Posts")
                                        .push()
                                        .setValue(post)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss(); /// dialog box showing ends here
                                                Toast.makeText(getContext(), "Posted successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

                    }
                });

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null){
            uri = data.getData();

            // setting up the image
            binding.imageShownInPost.setImageURI(uri);
            binding.imageShownInPost.setVisibility(View.VISIBLE);

            // enabling the button
            binding.postButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext() , R.drawable.follow_btn));
            binding.postButton.setTextColor(getContext().getResources().getColor(R.color.white));
            binding.postButton.setEnabled(true);
        }
    }
}