package com.example.keyly_projecte_intermodular.dto;

import android.content.Context;
import android.util.Log;

import com.example.keyly_projecte_intermodular.config.TokenForEver;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.request.ItemRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.*;

public class ItemDTO {

    private static Retrofit retrofit = null;

    public static interface RequestItem {
        // Obtenir tots els items
        @GET("/api/item/get/all")
        Call<ArrayList<Item>> getAllItems();

        // Afegir un item
        // FUNCIONA
        @POST("/api/item/add")
        Call<Item> addItem2(@Body ItemRequest itemRequest);

        // Incrementar accés a un item
        @POST("/api/item/access/{uuid}")
        Call<Item> accessItem(@Path("uuid") String uuid);

        // Actualitzar un item per UUID
        @PUT("/api/item/update/{uuid}")
        Call<Item> updateItem(@Path("uuid") String uuid, @Body Item item);
        @PUT("/api/item/update/{uuid}")
        Call<Item> updateItem2(@Path("uuid") String uuid, @Body ItemRequest itemRequest);

        // Eliminar un item per UUID
        @DELETE("/api/item/delete/{uuid}")
        Call<Item> deleteItem(@Path("uuid") String uuid);
    }

    public static Retrofit obtenirJSONItem() {

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
