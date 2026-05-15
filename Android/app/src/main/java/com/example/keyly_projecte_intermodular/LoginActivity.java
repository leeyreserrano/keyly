package com.example.keyly_projecte_intermodular;

import android.content.Context;
import android.util.Base64;

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

import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.utils.LoginService;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import static com.example.keyly_projecte_intermodular.resources.Varis.*;

public class LoginActivity extends AppCompatActivity {

    private Context context;
    private EditText txtUsuari, txtContrasenya;
    private Button btnLogin;
    private String json, nomUsuari = null, password = null;
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

        context = this;

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

        nomUsuari = txtUsuari.getText().toString();
        password = txtContrasenya.getText().toString();
        //        String usuari = "yami@gmail.com";
        //        String password = "1234";

        LoginService.login(LoginActivity.this, nomUsuari, password, true,
                new LoginService.LoginCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("INICIAR_APP", "Login correcte");
                        // Aquí ya es seguro que privateKeyDecrypt está actualizado
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(LoginActivity.this, "Error relogueig: " + error, Toast.LENGTH_SHORT).show();
                    }
                });

//        ApiService service = APIUsuari.getUsuari().create(ApiService.class);
//        Call<ResponseBody> srvLogin = service.getToken(new LoginDto(usuari, password));

//        srvLogin.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    try {
//                        String ResponseJSON = response.body().string();
//                        Log.d("DATA", ResponseJSON);
//
//                        Gson gson = new Gson();
//                        KdfResponse kdf = gson.fromJson(ResponseJSON, KdfResponse.class);
//                        TokenResponse token = gson.fromJson(ResponseJSON, TokenResponse.class);
//                        PrivateKeyResponse privateKeyResponse = gson.fromJson(ResponseJSON, PrivateKeyResponse.class);
//                        UsuariResponse usuariResponse = gson.fromJson(ResponseJSON, UsuariResponse.class);
//
//                        usuariPropi = usuariResponse.getUsuari();
//                        Log.d("USUARI", usuariPropi.toString());
//
//                        clauMestra = password;
//
//                        Log.d("TOKEN", token.getToken());
//                        tokenNou = token.getToken();
//
//                        Encrypt.clauDerivada = Encrypt.generarClauDerivada(password, kdf.getKdf());
//                        Log.d("CLAU_DERIVADA", Encrypt.clauDerivada.toString());
//                        Log.d("KDF_SALT", kdf.getKdf());
//                        Log.d("CLAU_DERIVADA_B64", Base64.encodeToString(Encrypt.clauDerivada.getEncoded(), Base64.DEFAULT));
//
//                        privateKeyEncrypt = privateKeyResponse.getPrivateKey();
//
//                        byte[] privateKeyBytes = Encrypt.desencriptarContrasenya2(privateKeyEncrypt, Encrypt.clauDerivada.getEncoded());
//                        Log.d("PK_BYTES_LENGTH", String.valueOf(privateKeyBytes.length));
//                        Log.d("PK_BYTES_B64", Base64.encodeToString(privateKeyBytes, Base64.DEFAULT));
//                        Log.d("PK_BYTES_STRING", new String(privateKeyBytes, "UTF-8"));
//                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//                        privateKeyDecrypt = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
//                        Log.d("PRIVATE_KEY", privateKeyDecrypt.toString());
//
//                        publicKey = getPublicKey(usuariPropi.getPublicKey());
//                        Log.d("PUBLIC_KEY", publicKey.toString());
//
//                        Toast.makeText(context, "Login correcto", Toast.LENGTH_SHORT).show();
//
//                        if (login) {
//                            Intent intent = new Intent(context, HomeActivity.class);
//                            startActivity(intent);
//                        }
//
//                    } catch (Exception e) {
//                        Log.e("ERROR_1", e.getMessage());
//                        Toast.makeText(context, "Error procesando respuesta", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
//                    Log.e("ERROR_2", "HTTP Code: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(context, "Error conexión", Toast.LENGTH_SHORT).show();
//                Log.e("ERROR", t.toString());
//            }
//        });
    }

    private PublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.decode(publicKey, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        return pubKey;
    }

    private byte[] desencriptarPrivateKey() throws Exception {
        String[] parts = privateKeyEncrypt.split(":");
        // El IV viene como texto UTF-8, no como Base64
        //byte[] iv = parts[0].getBytes(StandardCharsets.UTF_8);   // 12 bytes directos
        byte[] iv = Base64.decode(parts[0], Base64.DEFAULT);
        byte[] cipherText = Base64.decode(parts[1], Base64.DEFAULT);

        // Combinar IV (12 bytes) + CipherText
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        return combined;
    }

}