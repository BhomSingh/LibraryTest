package com.chat.befiler.retrofit;

import android.content.Context;


import com.chat.befiler.commons.Common;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiInterceptor implements Interceptor {

    Context context;
    Common commons;

    public ApiInterceptor(Context context) {
        this.context= context;
        this.commons = new Common();
    }

    @Override
    public @NotNull
    Response intercept(@NotNull Chain chain) throws IOException {

         String token = commons.getToken(context);
         Request request = chain.request().newBuilder()
                        .addHeader("Authorization","Bearer "+token)
                        .addHeader("Content-Type","application/json; charset=UTF-8").build();
       return chain.proceed(request);

    }
}
