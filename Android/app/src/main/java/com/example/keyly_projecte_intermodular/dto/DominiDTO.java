package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.resources.Varis.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.resources.Varis;
import com.example.keyly_projecte_intermodular.dao.Domini;
import com.example.keyly_projecte_intermodular.request.DominiRequest;
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

public class DominiDTO {

    private static Retrofit retrofit = null;

    public static interface RequestDomini {
        // Obtenir tots els dominis
        @GET("/api/domini/all/admin")
        Call<ArrayList<Domini>> getAllDominis();

        // Obtenir domini per UUID
        @GET("/api/domini/get/admin/{uuid}")
        Call<Domini> getDomini(@Path("uuid") String uuid);

        // Crear un domini
        @POST("/api/domini/add/admin")
        Call<Domini> crearDomini(@Body DominiRequest dominiRequest);

        // Actualitzar un domini per UUID
        @PUT("/api/domini/update/admin/{uuid}")
        Call<Domini> actualitzaDomini(@Path("uuid") String uuid, @Body DominiRequest dominiRequest);


        // Eliminar domini per UUID
        @DELETE("/api/domini/delete/admin/{uuid}")
        Call<Domini> eliminarDomini(@Path("uuid") String uuid);
    }

    public static Retrofit obtenirJSONDomini() {

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
