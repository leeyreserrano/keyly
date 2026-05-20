package com.example.keyly_projecte_intermodular;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.gestions.GestionsIdiomes;
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
    private ImageButton imgBtnAjuda, imgBtnIdioma;
    private String json, nomUsuari = null, password = null;
    private ArrayList<Usuari> usuaris = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GestionsIdiomes.aplicarIdioma(newBase));
    }

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

        imgBtnAjuda = findViewById(R.id.imgBtnAjuda);
        imgBtnAjuda.setOnClickListener(v -> {
            String url = "https://10.147.17.250:8081/docs/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        imgBtnIdioma = findViewById(R.id.imgBtnIdioma);
        imgBtnIdioma.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_idiomes, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog
            RadioGroup rgIdioma = view.findViewById(R.id.rgIdioma);
            RadioButton rbCA = view.findViewById(R.id.rbCA); // Català
            RadioButton rbEN = view.findViewById(R.id.rbEN); // Anglès
            RadioButton rbES = view.findViewById(R.id.rbES); // Castellà/Espanyol
            Button btnTraduccio = view.findViewById(R.id.btnTraduccio);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            String idiomaActual = GestionsIdiomes.obtenirIdioma(this);
            if (idiomaActual.equals("ca")) {
                rbCA.setChecked(true);
            } else if (idiomaActual.equals("en")) {
                rbEN.setChecked(true);
            } else if (idiomaActual.equals("es")) {
                rbES.setChecked(true);
            }

            btnTraduccio.setOnClickListener(c -> {
                // TODO traducir
                if (rgIdioma.getCheckedRadioButtonId() == R.id.rbCA) {
                    GestionsIdiomes.canviarIdioma(this, "ca");
                    recreate();
                } else if (rgIdioma.getCheckedRadioButtonId() == R.id.rbEN) {
                    GestionsIdiomes.canviarIdioma(this, "en");
                    recreate();
                } else if (rgIdioma.getCheckedRadioButtonId() == R.id.rbES) {
                    GestionsIdiomes.canviarIdioma(this, "es");
                    recreate();
                }
                alertDialog.dismiss();
            });

            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
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
                        Toast.makeText(LoginActivity.this, getString(R.string.toastErrorRelogueig, error), Toast.LENGTH_SHORT).show();
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