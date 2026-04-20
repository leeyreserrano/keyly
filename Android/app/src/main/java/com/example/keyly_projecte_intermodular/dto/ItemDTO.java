package com.example.keyly_projecte_intermodular.dto;

import android.content.Context;
import android.util.Log;

import com.example.keyly_projecte_intermodular.config.TokenForEver;
import com.example.keyly_projecte_intermodular.dao.Item;
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
        @POST("/api/item/add")
        Call<Item> addItem(@Body Item item);

        // Incrementar accés a un item
        @POST("/api/item/access/{uuid}")
        Call<Item> accessItem(@Path("uuid") String uuid);

        // Actualitzar un item per UUID
        @PUT("/api/item/update/{uuid}")
        Call<Item> updateItem(@Path("uuid") String uuid, @Body Item item);

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

    public static String carregarJSONItem(Context context, int nomArxiu) {

        String json = "[]";

        try {
            URL url = new URL("https://10.147.17.250:8081/api/items");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream isP = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(isP));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            json = sb.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.d("Error", ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", e.getMessage());
        }

        return json;

    }

    public static Item[] getItems(String json) {
        Gson gson = new Gson();
        Item[] itemsLlista = gson.fromJson(json, Item[].class);
        return itemsLlista;
    }
}
