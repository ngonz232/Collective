package com.example.collective.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.collective.R;
import com.example.collective.databinding.ActivityMainBinding;
import com.example.collective.fragments.CreateEvent;
import com.example.collective.fragments.HomeFeed;
import com.example.collective.fragments.MapsFragment;
import com.example.collective.fragments.ProfileFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {

    // Initializing binding
    private ActivityMainBinding binding;

    // Creating and inflating view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing FragmentManager
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Initializing variables for each fragment
        final Fragment fragment1 = new HomeFeed();
        final Fragment fragment2 = new CreateEvent();
        final Fragment fragment3 = new MapsFragment();
        final Fragment fragment4 = new ProfileFragment();

        // Binding and setting a listener on the bottom navigation bar for the tab select
        binding.bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {

            /* Switch case statement checking which tab is select and breaking to pass it to the
            fragment manager to inflate that fragment
             */
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {
                Fragment fragment;
                String title = tab1.getTitle();
                switch (title) {
                    case "Home":
                        fragment = fragment1;
                        break;
                    case "Publish":
                        fragment = fragment2;
                        break;
                    case "Map":
                        fragment = fragment3;
                        break;
                    case "Profile":
                        fragment = fragment4;
                        break;
                    default:
                        fragment = fragment1;
                        break;
                }
                // Beginning transaction to show fragment and setting transition animations
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.slide_out).replace(R.id.fragmentContainer, fragment).commit();

            }

            /* Empty required method for onTabReselected, but we are not taking actions on
            tab reselection
             */
            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {}

            });
            // Set default fragment
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment1).commit();
        }



    }
