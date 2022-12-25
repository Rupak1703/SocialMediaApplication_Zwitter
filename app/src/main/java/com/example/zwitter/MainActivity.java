package com.example.zwitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.zwitter.Fragments.AddFragment;
import com.example.zwitter.Fragments.HomeFragment;
import com.example.zwitter.Fragments.NotificationFragment;
import com.example.zwitter.Fragments.ProfileFragment;
import com.example.zwitter.Fragments.SearchFragment;
import com.example.zwitter.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());

        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);




        /// note this are working only when on double tapping item *****
        navigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.homeItem){
                    HomeFragment home= new HomeFragment();
                    replaceFragment(home);

                } else if (item.getItemId() == R.id.searchItem){

                    SearchFragment search = new SearchFragment();
                    replaceFragment(search);

                } else if (item.getItemId() == R.id.addItem){

                    AddFragment add = new AddFragment();
                    replaceFragment(add);

                } else if (item.getItemId() == R.id.notificationItem){

                    NotificationFragment notificationFragment = new NotificationFragment();
                    replaceFragment(notificationFragment);

                } else if (item.getItemId() == R.id.profileItem){

                    ProfileFragment profileFragment = new ProfileFragment();
                    replaceFragment(profileFragment);

                }

            }
        });
    }

    /// function to replace the frame layout with fragments

    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.frameLayout , fragment);
        transaction.commit();
    }

}