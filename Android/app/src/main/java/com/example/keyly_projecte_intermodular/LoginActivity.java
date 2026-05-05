package com.example.keyly_projecte_intermodular;

import android.util.Base64;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.keyly_projecte_intermodular.dao.LoginDto;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.rest_api.APIUsuari;
import com.example.keyly_projecte_intermodular.rest_api.ApiService;
import com.example.keyly_projecte_intermodular.rest_api.KdfResponse;
import com.example.keyly_projecte_intermodular.rest_api.PrivateKeyResponse;
import com.example.keyly_projecte_intermodular.rest_api.TokenResponse;
import com.example.keyly_projecte_intermodular.rest_api.UsuariResponse;
import com.example.keyly_projecte_intermodular.utils.Encrypt;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.*;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsuari, txtContrasenya;
    private Button btnLogin;
    private String json;
    private ArrayList<Usuari> usuaris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //cargarDatos();

        // Camp del correu d'usuari
        txtUsuari = new EditText(this);
        txtUsuari = findViewById(R.id.txtUsuari);

        // Camp de contrasenya d'usuari
        txtContrasenya = new EditText(this);
        txtContrasenya = findViewById(R.id.txtContrasenya);

        // Botó per iniciar sessió
        btnLogin = new Button(this);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            CallLoginService();
        });
    }

    private void CallLoginService() {
        String usuari = "user@domain.com";//txtUsuari.getText().toString();
        String password = "1234"; //txtContrasenya.getText().toString();

        ApiService service = APIUsuari.getUsuari().create(ApiService.class);
        Call<ResponseBody> srvLogin = service.getToken(new LoginDto(usuari, password));

        srvLogin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String ResponseJSON = response.body().string();
                        Log.d("DATA", ResponseJSON);

                        Gson gson = new Gson();
                        KdfResponse kdf = gson.fromJson(ResponseJSON, KdfResponse.class);
                        TokenResponse token = gson.fromJson(ResponseJSON, TokenResponse.class);
                        PrivateKeyResponse privateKeyResponse = gson.fromJson(ResponseJSON, PrivateKeyResponse.class);
                        UsuariResponse usuariResponse = gson.fromJson(ResponseJSON, UsuariResponse.class);

                        Usuari usuari = usuariResponse.getUsuari();

                        Log.d("USUARI", usuari.toString());

                        tokenNou = token.getToken();

                        Encrypt.clauDerivada = Encrypt.clauDerivada(password, kdf.getKdf());
                        Log.d("CLAU_DERIVADA", Encrypt.clauDerivada.toString());
                        Log.d("KDF_SALT", kdf.getKdf());
                        Log.d("CLAU_DERIVADA_B64", Base64.encodeToString(Encrypt.clauDerivada.getEncoded(), Base64.DEFAULT));

                        privateKeyEncrypt = privateKeyResponse.getPrivateKey();

                        String pkFormatejada = desencriptarPrivateKey();

                        byte[] privateKeyBytes = Encrypt.desencriptarContrasenya2(pkFormatejada, Encrypt.clauDerivada.getEncoded());
                        Log.d("PK_BYTES_LENGTH", String.valueOf(privateKeyBytes.length));
                        Log.d("PK_BYTES_B64", Base64.encodeToString(privateKeyBytes, Base64.DEFAULT));
                        Log.d("PK_BYTES_STRING", new String(privateKeyBytes, "UTF-8"));
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // o el algoritmo que uses
                        privateKeyDecrypt = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
                        Log.d("PRIVATE_KEY", privateKeyDecrypt.toString());

                        publicKey = getPublicKey(usuari.getPublicKey());
                        Log.d("PUBLIC_KEY", publicKey.toString());

                        Toast.makeText(LoginActivity.this, "Login correcto", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                    } catch (Exception e) {
                        Log.e("ERROR_1", e.getMessage());
                        Toast.makeText(LoginActivity.this, "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_2", "HTTP Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error conexión", Toast.LENGTH_SHORT).show();
                Log.e("ERROR", t.toString());
            }
        });
    }

    private PublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.decode(publicKey, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        return pubKey;
    }

    private String desencriptarPrivateKey() throws Exception {
        String[] parts = privateKeyEncrypt.split(":");
        // El IV viene como texto UTF-8, no como Base64
        //byte[] iv = parts[0].getBytes(StandardCharsets.UTF_8);   // 12 bytes directos
        byte[] iv = Base64.decode(parts[0], Base64.DEFAULT);
        byte[] cipherText = Base64.decode(parts[1], Base64.DEFAULT);

        // Combinar IV (12 bytes) + CipherText
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        return Base64.encodeToString(combined, Base64.NO_WRAP);
    }

}