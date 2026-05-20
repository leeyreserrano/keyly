package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.resources.Varis.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.resources.Varis;
import com.example.keyly_projecte_intermodular.dao.Rol;
import com.example.keyly_projecte_intermodular.request.RolRequest;
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

public class RolDTO {

    private static Retrofit retrofit = null;

    public static interface RequestRol {
        // Obté tots els rols
        @GET("/api/rol/all/admin")
        Call<ArrayList<Rol>> getAllRols();

        // Obté un rol per UUID
        @GET("/api/rol/get/admin/{uuid}")
        Call<Rol> getRol(@Path("uuid") String uuid);

        // Afegeix un rol nou
        @POST("/api/rol/add/admin")
        Call<Rol> afegirRol(@Body RolRequest rolRequest);

        // Actualitza un rol per UUID
        @PUT("/api/rol/update/admin/{uuid}")
        Call<Rol> actualitzarRol(@Path("uuid") String uuid, @Body RolRequest rolRequest);

        // Elimina un rol per UUID
        @DELETE("/api/rol/delete/admin/{uuid}")
        Call<Rol> eliminarRol(@Path("uuid") String uuid);
    }

    public static Retrofit obtenirJSONRol() {

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
