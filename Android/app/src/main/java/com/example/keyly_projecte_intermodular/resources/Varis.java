package com.example.keyly_projecte_intermodular.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;

import java.io.ByteArrayOutputStream;
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
public class Varis {

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
    public static Drawable svgPerfilAltre = null;
    public static Bitmap imatgePerfilAltre = null;

    public static void getImage(Consumer<ResponseBody> onResult) {
        // TODO obtenir imatge perfil
        Call<ResponseBody> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).getImage();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    onResult.accept(response.body());
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

    public static void getImatgeUUID(Usuari usuari, Consumer<ResponseBody> onResult) {
        Call<ResponseBody> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).getImageUUID(usuari.getUuid().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        onResult.accept(response.body());
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

    public static String bitmapToBase64(Bitmap bitmap) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        // PNG o JPEG
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//
//        byte[] imageBytes = baos.toByteArray();
//
//        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        // REDUCIR TAMAÑO
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bitmap,
                800, // ancho máximo
                800, // alto máximo
                true
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // COMPRIMIR JPEG
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);

        byte[] imageBytes = baos.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    public static void pujarImatgeAPI(String base64, boolean esCreant, Context context) {
        Call<String> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).pujarImatge(base64);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (!esCreant) {
                        Toast.makeText(context, "Imatge de perfil actualitzada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "No s'ha pogut actualitzar l'imatge de perfil", Toast.LENGTH_SHORT).show();
                    //Log.e("ERROR_RESPONSE_IMG", response.message());
                    try {

                        Log.e("ERROR_RESPONSE_IMG",
                                "Code: " + response.code());

                        Log.e("ERROR_RESPONSE_IMG",
                                "Message: " + response.message());

                        if (response.errorBody() != null) {

                            Log.e("ERROR_RESPONSE_IMG",
                                    "Body: " + response.errorBody().string());
                        }

                    } catch (Exception e) {

                        Log.e("ERROR_RESPONSE_IMG",
                                "Error llegint errorBody",
                                e);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "No s'ha pogut actualitzar l'imatge de perfil", Toast.LENGTH_SHORT).show();
                Log.e("ERROR_FAILURE_IMG", t.getMessage());
            }
        });
    }
}
