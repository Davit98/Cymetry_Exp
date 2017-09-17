package com.davitmartirosyan.exp;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(new OkHttpClient.Builder().build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
    }

    public static Retrofit postClient(String baseUrl) {
        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

}
