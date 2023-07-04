package com.chat.befiler.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.chat.befiler.commons.Common;
import com.chat.befiler.commons.Constants;
import com.chat.befiler.model.chat.PermissionDataModel;
import com.chat.befiler.model.login.LoginRequest;
import com.chat.befiler.model.login.LoginResponseData;
import com.chat.befiler.retrofit.ApiClient;
import com.chat.befiler.retrofit.WebResponse;
import com.example.signalrtestandroid.databinding.ActivityLogin2Binding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLogin2Binding binding;
    private Common common;
    private String conversationByUID = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent.getExtras().containsKey(Constants.CONVERSATION_BY_UID_KEY)){
            if(intent.getExtras().get(Constants.CONVERSATION_BY_UID_KEY)!=null){
            conversationByUID = intent.getStringExtra(Constants.CONVERSATION_BY_UID_KEY);
            }
        }
        binding = ActivityLogin2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        common = new Common();
        TextInputEditText usernameEditText = binding.edtEmail;
        TextInputEditText passwordEditText = binding.edtPassword;
        Button loginButton = binding.btnSubmit;

        loginButton.setOnClickListener(v -> {
            if (!usernameEditText.getText().toString().isEmpty() && !passwordEditText.getText().toString().isEmpty()) {
                loginApi(usernameEditText.getText().toString(),passwordEditText.getText().toString());
            } else {
                if (usernameEditText.getText().toString().isEmpty()) {

                }
                if (usernameEditText.getText().toString().isEmpty()) {

                }
            }
        });

    }

    public void loginApi(String email ,String passWord){

        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        if(getWindow()!=null){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        new ApiClient(LoginActivity.this).getWebService().loginUser(new LoginRequest(email,passWord,common.getDeviceToken(this))).enqueue(new Callback<WebResponse<LoginResponseData>>() {
            @Override
            public void onResponse(Call<WebResponse<LoginResponseData>> call, Response<WebResponse<LoginResponseData>> response) {
                if (response.body() != null){
                    if (response.body().isSuccess()){
                        JWT jwt = new JWT(response.body().getResult().token);
                        if (!jwt.getClaim("UserId").asString().isEmpty()){
                            common.saveUserId(LoginActivity.this,jwt.getClaim("UserId").asString());
                        }
                        if (!jwt.getClaim("IsSuperAdmin").asString().isEmpty()){
                            common.saveIsSuperAdmin(LoginActivity.this,jwt.getClaim("IsSuperAdmin").asString());
                        }
                        if (!jwt.getClaim("Permissions").asString().isEmpty()){
                            common.savePermission(LoginActivity.this,jwt.getClaim("Permissions").asString());
                        }
                        common.isLoggedIn(LoginActivity.this,true);
                        common.saveToken(LoginActivity.this,response.body().getResult().token);
                        common.saveUserLoginData(LoginActivity.this,new Gson().toJson(response.body().getResult()));
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra(Constants.CONVERSATION_BY_UID_KEY,conversationByUID);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if(getWindow()!=null){
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                    if (binding.progressBar != null) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WebResponse<LoginResponseData>> call, Throwable t) {
                if (binding.progressBar != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                if(getWindow()!=null){
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });

    }
}