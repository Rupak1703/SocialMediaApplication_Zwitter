package com.example.zwitter.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zwitter.Adapter.Notification_adapter;
import com.example.zwitter.Models.Notification_model;
import com.example.zwitter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Notification_notify extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Notification_model> list;
    FirebaseDatabase database;

    public Notification_notify() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifcation_notify, container, false);

        recyclerView = view.findViewById(R.id.notification_tab_rv);
        list = new ArrayList<>();



        Notification_adapter notification_adapter = new Notification_adapter(list , getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(notification_adapter);

        database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Notification_model notification_model = dataSnapshot.getValue(Notification_model.class);
                            notification_model.setNotificationID(dataSnapshot.getKey());
                            list.add(notification_model);
                        }

                        notification_adapter.notifyDataSetChanged();
                    }

                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;
    }
}