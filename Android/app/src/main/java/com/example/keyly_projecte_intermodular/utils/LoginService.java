package com.example.keyly_projecte_intermodular.utils;

import static com.example.keyly_projecte_intermodular.resources.Varis.privateKeyEncrypt;
import static com.example.keyly_projecte_intermodular.resources.Varis.usuariPropi;
import static com.example.keyly_projecte_intermodular.resources.Varis.clauMestra;
import static com.example.keyly_projecte_intermodular.resources.Varis.tokenNou;
import static com.example.keyly_projecte_intermodular.resources.Varis.privateKeyDecrypt;
import static com.example.keyly_projecte_intermodular.resources.Varis.publicKey;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.keyly_projecte_intermodular.HomeActivity;
import com.example.keyly_projecte_intermodular.dao.LoginDto;
import com.example.keyly_projecte_intermodular.rest_api.APIUsuari;
import com.example.keyly_projecte_intermodular.rest_api.ApiService;
import com.example.keyly_projecte_intermodular.rest_api.KdfResponse;
import com.example.keyly_projecte_intermodular.rest_api.PrivateKeyResponse;
import com.example.keyly_projecte_intermodular.rest_api.TokenResponse;
import com.example.keyly_projecte_intermodular.rest_api.UsuariResponse;
import com.google.gson.Gson;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginService {

    public interface LoginCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void login(Context ctx, String usuariNou, String passwordNova, boolean navegarAHome,
                             LoginCallback callback) {

        String usuariLogin = usuariNou;
        String passwordLogin = passwordNova;

        ApiService service = APIUsuari.getUsuari().create(ApiService.class);
        Call<ResponseBody> srvLogin = service.getToken(new LoginDto(usuariLogin, passwordLogin));

        srvLogin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseJSON = response.body().string();
                        Gson gson = new Gson();
                        KdfResponse kdf = gson.fromJson(responseJSON, KdfResponse.class);
                        TokenResponse token = gson.fromJson(responseJSON, TokenResponse.class);
                        PrivateKeyResponse pkResponse = gson.fromJson(responseJSON, PrivateKeyResponse.class);
                        UsuariResponse usuariResponse = gson.fromJson(responseJSON, UsuariResponse.class);

                        usuariPropi = usuariResponse.getUsuari();
                        clauMestra = passwordLogin;
                        tokenNou = token.getToken();

                        Encrypt.clauDerivada = Encrypt.generarClauDerivada(passwordLogin, kdf.getKdf());
                        privateKeyEncrypt = pkResponse.getPrivateKey();

                        byte[] privateKeyBytes = Encrypt.desencriptarContrasenya2(privateKeyEncrypt, Encrypt.clauDerivada.getEncoded());
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        privateKeyDecrypt = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
                        publicKey = getPublicKey(ctx, usuariPropi.getPublicKey());

                        if (navegarAHome) {
                            Intent intent = new Intent(ctx, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(intent);
                        }

                        if (callback != null) callback.onSuccess();

                    } catch (Exception e) {
                        Log.e("LOGIN_ERROR", e.getMessage());
                        Toast.makeText(ctx, "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                        if (callback != null) callback.onFailure(e.getMessage());
                    }
                } else {
                    if (callback != null) callback.onFailure("Credencials incorrectes");
                    Toast.makeText(ctx, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (callback != null) callback.onFailure(t.getMessage());
                Toast.makeText(ctx, "Error conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static PublicKey getPublicKey(Context ctx, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.decode(publicKey, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
