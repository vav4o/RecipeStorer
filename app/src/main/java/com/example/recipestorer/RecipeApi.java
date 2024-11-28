package com.example.recipestorer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RecipeApi {
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://themealdb.com/api/json/v1/1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
