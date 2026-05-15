package com.example.keyly_projecte_intermodular.dto;

import static com.example.keyly_projecte_intermodular.resources.Varis.tokenNou;

import android.util.Log;

import com.example.keyly_projecte_intermodular.resources.Varis;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.request.UsuariRequest;
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
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class UsuariDTO {

    private static Retrofit retrofit = null;

    public static interface RequestUsuari {

        /* ****************************** USUARI ****************************** */
        // Obté tots els usuaris de la mateixa sucursal del que fa la petició
        @GET("/api/usuari/all")
        Call<ArrayList<Usuari>> getAllUsuaris();

        // Obté tots els usuaris
        @GET("/api/usuari/all/admin")
        Call<ArrayList<Usuari>> getAllUsuarisAdmin();

        // Crea un usuari l'administrador
        @POST("/api/usuari/add/admin")
        Call<Usuari> crearUsuari(@Body UsuariRequest usuariRequest);

        // Actualitza l'usuari que fa la petició
        @PUT("/api/usuari/update")
        Call<Usuari> actualitzarUsuari(@Body UsuariRequest usuariRequest);

        // Actualitza un usuari per UUID
        @PUT("/api/usuari/update/admin/cap/{uuid}")
        Call<Usuari> actualitzarUsuari(@Path("uuid") String uuid, @Body UsuariRequest usuariRequest);

        // Elimina un usuari per UUID (admin)
        @DELETE("/api/usuari/delete/admin/cap/{uuid}")
        Call<Usuari> eliminarUsuari(@Path("uuid") String uuid);

        /* ****************************** IMATGE ****************************** */
        // Retorna la imatge de l'usuari (inicial del seu nom)
        @GET("/api/usuari/get/image")
        Call<ResponseBody> getImage();

        // Retorna la imatge d'un usuari especificat
        @GET("/api/usuari/get/image/{uuid}")
        Call<ResponseBody> getImageUUID(@Path("uuid") String uuid);

        // Puja una imatge de perfil l'usuari
        @POST("/api/usuari/upload/image")
        Call<String> pujarImatge(@Body String file);
    }

    public static Retrofit obtenirJSONUsuari() {

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
