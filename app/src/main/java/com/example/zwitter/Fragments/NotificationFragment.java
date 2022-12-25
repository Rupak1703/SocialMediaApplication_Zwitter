package com.example.zwitter.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zwitter.Adapter.ViewPager2Adapter;
import com.example.zwitter.databinding.FragmentNotificationBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;



public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;


    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater , container , false);

        binding.viewPager2.setAdapter(new ViewPager2Adapter(requireActivity().getSupportFragmentManager(), getLifecycle()));
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tablayout , binding.viewPager2 , new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                binding.viewPager2.setCurrentItem(position);

                String title = null;
                if (position == 0){
                    title = "Notification";
                    tab.setText(title);
                }
                if (position == 1){
                    title = "Requests";
                    tab.setText(title);
                }

            }
        });
        tabLayoutMediator.attach();

        return binding.getRoot();
    }
}