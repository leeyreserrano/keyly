package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.resources.Varis.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.resources.Varis;
import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Compartit;
import com.example.keyly_projecte_intermodular.request.CompartitItemRequest;
import com.example.keyly_projecte_intermodular.request.CompartitRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class CompartitDTO {

    private static Retrofit retrofit = null;

    public static interface RequestCompartit {

        // Obté tots els compartits
        @GET("/api/compartit/get/all")
        Call<ArrayList<Compartit>> getAllCompartit();

        // Obté tots els meus compartits
        @GET("/api/compartit/get/all/creats")
        Call<ArrayList<Compartit>> getAllCompartitCreats();

        // Crear compartits amb múltiples usuaris
        @POST("/api/compartit/add")
        Call<Void> compartir(@Body CompartitRequest compartitRequest);

        // Crea un ítem i el comparteix a múltiples usuaris
        @POST("/api/compartit/add/item")
        Call<CompartitRequest> compartirItem(@Body CompartitItemRequest compartitItemRequest);

        // Crea una carpeta i la comparteix a múltiples usuaris
        @POST("/api/compartit/add/carpeta")
        Call<Compartit> compartirCarpeta(@Body Carpeta carpetaRequest, @Body CompartitRequest compartitRequest);

        // Elimina un compartit per UUID
        @DELETE("/api/compartit/delete/{uuid}")
        Call<Void> eliminarCompartit(@Path("uuid") String uuid);

    }

    public static Retrofit obtenirJSONCompartit() {

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
                    .baseUrl(Varis.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }
}
