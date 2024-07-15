package com.example.healthandfitness.ui;



import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.healthandfitness.CalorieFragment;
import com.example.healthandfitness.R;
import com.example.healthandfitness.TrackingFragment;
import com.example.healthandfitness.WorkoutFragment;
import com.example.healthandfitness.databinding.ActivityNavViewBinding;

public class NavViewActivity extends AppCompatActivity {

    ActivityNavViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new CalorieFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            //Using a switch case didn't work due to error "constant expression required", related to the referencing of the differing fragment ID's - resources (R.id...) are no longer declared final, to optimise build speeds, however this is a pre-requisite to be used in switch cases
            int itemId = item.getItemId();
            if (itemId == R.id.calorieFragment) {
                replaceFragment(new CalorieFragment());
            } else if (itemId == R.id.workoutFragment) {
                replaceFragment(new WorkoutFragment());
            } else if (itemId == R.id.trackingFragment) {
                replaceFragment(new TrackingFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.commit();
    }
}