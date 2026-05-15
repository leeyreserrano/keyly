package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.resources.Varis.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.resources.Varis;
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
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class CarpetaDTO {

    private static Retrofit retrofit = null;

    public static interface RequestCarpeta {
        // Obtenir tots les carpetes
        @GET("/api/carpeta/get/all")
        Call<ArrayList<Carpeta>> getAllCarpetes();

        // Crear una carpeta
        @POST("/api/carpeta/add")
        Call<Carpeta> crearCarpeta(@Body Carpeta carpeta);

        // Afegir ítem existent a una carpeta
        @POST("/api/carpeta/add/{carpetaUuid}/item/existing/{itemUuid}")
        Call<Item> afegirItemCarpeta(@Path("carpetaUuid") String carpetaUuid, @Path("itemUuid") String itemUuid);

        // Editar una carpeta
        @PUT("/api/carpeta/update/{uuid}")
        Call<Carpeta> editarCarpeta(@Path("uuid") String uuid, @Body Carpeta carpeta);

        // Eliminar carpeta per UUID
        @DELETE("/api/carpeta/delete/{uuid}")
        Call<Void> eliminarCarpeta(@Path("uuid") String uuid);

        // Eliminar ítem d'una carpeta per UUID
        @DELETE("/api/carpeta/delete/{carpetaUuid}/item/{itemUuid}")
        Call<Void> eliminarItemCarpeta(@Path("carpetaUuid") String carpetaUuid, @Path("itemUuid") String itemUuid);
    }

    public static Retrofit obtenirJSONCarpeta() {

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
