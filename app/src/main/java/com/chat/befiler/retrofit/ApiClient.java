package com.chat.befiler.retrofit;

import android.content.Context;

import com.chat.befiler.commons.Common;
import com.chat.befiler.commons.TokenAuthenticator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {


//    public final static String URL = "http://175.107.196.226:8084/api";
    public final static String URL = "http://175.107.196.226:5003/api";

    private ApiClient instance;
    // Keep your services here, build them in buildRetrofit method later
    private WebService webService;
    private static Common commons;

    public ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
            commons = new Common();
        }
        return instance;
    }

    // Build retrofit once when creating a single instance
    public ApiClient(Context context) {
        // Implement a method to build your retrofit
        Retrofit(URL,context);
    }

    // Retrofit Class use Live Service
    public void Retrofit(String ip, Context context) {
        Retrofit retrofit ;
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient.Builder httpClientBuilder =
                new OkHttpClient.Builder().
                        readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .addInterceptor(new ApiInterceptor(context))
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .authenticator(new TokenAuthenticator(context));
        if(commons!=null){
            //commons.initSSL(httpClientBuilder,context,false);
        }

        // Live Server Https use
        retrofit = new Retrofit.Builder()
                .baseUrl(ip + "/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClientBuilder.build())
                .build();
        webService = retrofit.create(WebService.class);

    } // close the Retrofit Static Methord

    public WebService getWebService() {
        return webService;
    }

}
