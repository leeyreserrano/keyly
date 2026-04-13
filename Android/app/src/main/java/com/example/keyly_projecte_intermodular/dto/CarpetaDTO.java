package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.config.TokenForEver;
import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class CarpetaDTO {

    private static Retrofit retrofit = null;

    public static interface RequestCarpeta {
        // Obtenir tots les carpetes
        @GET("/api/carpeta/get/all")
        Call<ArrayList<Carpeta>> getAllCarpetes();
    }

    public static Retrofit obtenirJSONICarpeta() {

        Log.d("TOKEN", tokenNou);

        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + tokenNou)
                                .build();
                        return chain.proceed(newRequest);
                    })
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(TokenForEver.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }
}
