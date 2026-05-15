package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.resources.Varis.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.resources.Varis;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.request.SucursalRequest;
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

public class SucursalDTO {
    private static Retrofit retrofit = null;

    public static interface RequestSucursal {
        // Obtenir totes les sucursals
        @GET("/api/sucursal/all/admin")
        Call<ArrayList<Sucursal>> getAllSucursals();

        // Obtenir sucursal per UUID
        @GET("/api/sucursal/get/admin/{uuid}")
        Call<Sucursal> getSucursal(@Path("uuid") String uuid);

        // Crear una sucursal
        @POST("/api/sucursal/add/admin")
        Call<Sucursal> crearSucursal(@Body SucursalRequest sucursalRequest);

        // Actualitzar una sucursal
        @PUT("/api/sucursal/update/admin/{uuid}")
        Call<Sucursal> actualitzaSucursal(@Path("uuid") String uuid, @Body SucursalRequest sucursalRequest);

        // Eliminar sucursal per UUID
        @DELETE("/api/sucursal/delete/admin/{uuid}")
        Call<Sucursal> eliminarSucursal(@Path("uuid") String uuid);
    }

    public static Retrofit obtenirJSONSucursal() {

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
