package com.chat.befiler.activities;

import android.content.Intent;
import android.os.Bundle;

import com.chat.befiler.commons.Common;
import com.example.signalrtestandroid.databinding.ActivitySplashBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.signalrtestandroid.R;

public class SplashActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        launchExpenseModule();

    }

    private void launchExpenseModule() {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Common common = new Common();
            if (common.getIsLoggedIn(this)){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("conversationByUID","");
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("conversationByUID","");
                startActivity(intent);
                finish();
            }

        }).start();
    }

}