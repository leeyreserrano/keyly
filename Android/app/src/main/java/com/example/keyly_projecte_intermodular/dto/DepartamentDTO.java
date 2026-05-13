package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.config.TokenForEver;
import com.example.keyly_projecte_intermodular.dao.Departament;
import com.example.keyly_projecte_intermodular.request.DepartamentRequest;
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

public class DepartamentDTO {

    private static Retrofit retrofit = null;

    public static interface RequestDepartament {
        // Obtenir tots els departaments
        @GET("/api/departament/all/admin")
        Call<ArrayList<Departament>> getAllDepartaments();

        // Crear un departament
        @POST("/api/departament/add/admin")
        Call<Departament> crearDepartament(@Body DepartamentRequest departamentRequest);

        // Actualitzar un departament
        @PUT("/api/departament/update/admin/{uuid}")
        Call<Departament> actualitzaDepartament(@Path("uuid") String uuid, @Body DepartamentRequest departamentRequest);

        // Eliminar departament per UUID
        @DELETE("/api/departament/delete/admin/{uuid}")
        Call<Departament> eliminarDepartament(@Path("uuid") String uuid);
    }

    public static Retrofit obtenirJSONDepartament() {

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
