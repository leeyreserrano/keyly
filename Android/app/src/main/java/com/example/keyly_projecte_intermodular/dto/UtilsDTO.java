package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.config.TokenForEver;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.utils.Password;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class UtilsDTO {
    private static Retrofit retrofit = null;

    public static interface RequestUtils {
        // Generar contrasenya
        @POST("/api/utils/custom/password")
        Call<Item> generatePassword(@Body Password password);
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
                    .baseUrl(TokenForEver.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }
}
