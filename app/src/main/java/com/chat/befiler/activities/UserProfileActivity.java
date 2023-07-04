package com.chat.befiler.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.chat.befiler.commons.Common;
import com.chat.befiler.commons.Utils;
import com.chat.befiler.model.chat.UserProfile;
import com.chat.befiler.retrofit.ApiClient;
import com.chat.befiler.retrofit.WebResponse;
import com.example.signalrtestandroid.R;
import com.example.signalrtestandroid.databinding.ActivityProfileUpdateBinding;
import com.example.signalrtestandroid.databinding.ActivitySplashBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    ActivityProfileUpdateBinding profileUpdateBinding;
    Common common;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileUpdateBinding = ActivityProfileUpdateBinding.inflate(getLayoutInflater());
        setContentView(profileUpdateBinding.getRoot());
        common = new Common();

        if (Utils.isNetworkAvailable(UserProfileActivity.this)) {
            getProfileData();
        } else {
            Utils.customeTost(getString(R.string.no_internet), UserProfileActivity.this, view);
        }

        profileUpdateBinding.imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getProfileData() {

        if (UserProfileActivity.this.getWindow() != null) {
            UserProfileActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        new ApiClient(UserProfileActivity.this).getWebService().getUserById("1").enqueue(new Callback<WebResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<WebResponse<UserProfile>> call, Response<WebResponse<UserProfile>> response) {
                if (response.body() != null) {
                    if (response.body().isSuccess()) {

                        profileUpdateBinding.txtUsername.setText(response.body().getResult().firstName+" "+response.body().getResult().lastName);
                        profileUpdateBinding.txtEmailAddress.setText(response.body().getResult().contactEmail);
                        profileUpdateBinding.txtMobileNo.setText(response.body().getResult().contactNo);

                    } else {
                        Toast.makeText(UserProfileActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Server Con,t Response", Toast.LENGTH_SHORT).show();
                }

                if (UserProfileActivity.this.getWindow() != null) {
                    UserProfileActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }

            @Override
            public void onFailure(Call<WebResponse<UserProfile>> call, Throwable t) {
                if (UserProfileActivity.this.getWindow() != null) {
                    UserProfileActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                if (t.getMessage().contains("Failed to connect")) {
                    Toast.makeText(UserProfileActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}