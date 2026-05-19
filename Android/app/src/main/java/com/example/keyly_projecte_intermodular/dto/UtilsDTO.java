package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.resources.Varis.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.dao.Hash;
import com.example.keyly_projecte_intermodular.resources.Varis;
import com.example.keyly_projecte_intermodular.dao.GeneradorContrasenya;
import com.example.keyly_projecte_intermodular.dao.Contrasenya;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class UtilsDTO {
    private static Retrofit retrofit = null;

    public static interface RequestUtils {
        // Generar contrasenya
        @POST("/api/utils/custom/password")
        Call<Contrasenya> generatePassword(@Body GeneradorContrasenya password);

        // Comprovar si la contrasenya és vulnerada o no
        @GET("/api/utils/pwned/password/{prefix}/{suffix}")
        Call<ArrayList<Hash>> comprovarContrasenya(@Path("prefix") String prefix, @Path("suffix") String suffix);
    }

    public static Retrofit obtenirJSONPassword() {

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
