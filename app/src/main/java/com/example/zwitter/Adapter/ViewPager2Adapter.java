package com.example.zwitter.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.zwitter.Fragments.Notification_notify;
import com.example.zwitter.Fragments.requestFragment;

public class ViewPager2Adapter extends FragmentStateAdapter {


    public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new Notification_notify();
            case 1: return new requestFragment();

            default: return new Notification_notify();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
