package com.example.keyly_projecte_intermodular.config;

import android.util.Log;

import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;

import java.security.PrivateKey;
import java.security.PublicKey;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Data
@AllArgsConstructor
public class TokenForEver {

    public static String tokenFE = "eyJhbGciOiJIUzI1NiJ9.eyJyb2wiOiJBRE1JTiIsInN1YiI6IjZkYTlkNDkyLTI3OGMtMTFmMS1hMmE0LTcyZWFiYWUzNTI3MCIsImlhdCI6MTc3NDM2NTYzMSwiZXhwIjoxOTc3NDM2NTYzMX0.iGgCPYvGGgig2FAy8uFvarD6N1GvC5Bq21oR8Emheo8";
    // https://10.147.17.250:8081 (REAL)
    // 192.168.137.60 (local gerard hoy)
    public static final String BASE_URL = "https://10.147.17.250:8081";

    public static String tokenNou = "";
    public static String privateKeyEncrypt = "";
    public static PrivateKey privateKeyDecrypt;
    public static PublicKey publicKey;
    public static byte[] dataKey;

    public static Usuari usuariPropi;
    public static String clauMestra;
    public static String imatgePerfil = "";

    public static void getImage(Consumer<String> onResult) {
        // TODO obtenir imatge perfil
        Call<ResponseBody> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).getImage();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String raw = response.body().string();
                        // Netejar url
                        raw = raw.replaceAll("^\"|\"$", "");
                        imatgePerfil = raw;
                        Log.d("IMATGE_PERFIL", "Valor: " + imatgePerfil);
                        onResult.accept(imatgePerfil);
                    } catch (Exception e) {
                        Log.e("ERROR_RESPONSE_IMG", "Error llegint body: " + e.getMessage());
                    }
                } else {
                    Log.e("ERROR_RESPONSE_IMG", response.message());
                    try {
                        Log.e("ERROR_RESPONSE_IMG", "Body error: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("ERROR_RESPONSE_IMG", "No s'ha pogut llegir el errorBody");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR_FAILURE_IMG", t.getMessage());
            }
        });
    }

    public static void getImatgeUUID(Usuari usuari, Consumer<String> onResult) {
        Call<ResponseBody> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).getImageUUID(usuari.getUuid().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String raw = response.body().string();
                        // Netejar url
                        raw = raw.replaceAll("^\"|\"$", "");
                        imatgePerfil = raw;
                        Log.d("IMATGE_PERFIL", "Valor: " + imatgePerfil);
                        onResult.accept(imatgePerfil);
                    } catch (Exception e) {
                        Log.e("ERROR_RESPONSE_IMG", "Error llegint body: " + e.getMessage());
                    }
                } else {
                    Log.e("ERROR_RESPONSE_IMG_HTTP", "HTTP CODE: " + response.code());
                    Log.e("ERROR_RESPONSE_IMG", response.message());
                    try {
                        Log.e("ERROR_RESPONSE_IMG", "Body error: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("ERROR_RESPONSE_IMG", "No s'ha pogut llegir el errorBody");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR_FAILURE_IMG", t.getMessage());
            }
        });
    }
}
