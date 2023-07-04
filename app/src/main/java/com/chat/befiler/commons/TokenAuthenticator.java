package com.chat.befiler.commons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

import com.auth0.android.jwt.JWT;
import com.chat.befiler.activities.LoginActivity;
import com.chat.befiler.activities.MainActivity;
import com.google.gson.Gson;

public class TokenAuthenticator implements Authenticator {

    Context context;
    Common common;

    public TokenAuthenticator(Context context){
        this.context=context;
        common = new Common();
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {

        if(response.code() == 401)
        {

            common.saveUserId(context,"");
            common.isLoggedIn(context,false);
            common.saveToken(context,"");
            common.saveUserLoginData(context,"");
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("conversationByUID","");
            context.startActivity(intent);
        }

        return response.request().newBuilder()
                .header("Authorization", common.getToken(context))
                .build();
    }
}