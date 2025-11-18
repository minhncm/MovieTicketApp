package com.example.ticketapp.;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.ticketapp.databinding.ActivityOnBoardingBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OnBoarding extends AppCompatActivity {
    private ActivityOnBoardingBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        binding = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setFirstTimeLaunch();
    }

    private void setFirstTimeLaunch() {
        binding.btnGetStarted.setOnClickListener(v -> {
            // Lưu đã xem onboarding
            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            startActivity(new Intent(this, Splash.class));
            finish();
        });
    }
}