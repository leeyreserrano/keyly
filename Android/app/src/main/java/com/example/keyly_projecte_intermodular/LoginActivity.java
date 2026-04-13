package com.example.keyly_projecte_intermodular;

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

import com.example.keyly_projecte_intermodular.config.TokenForEver;
import com.example.keyly_projecte_intermodular.dao.LoginDto;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.rest_api.APIUsuari;
import com.example.keyly_projecte_intermodular.rest_api.ApiService;
import com.example.keyly_projecte_intermodular.rest_api.TokenResponse;
import com.google.gson.Gson;

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
//            // Obtenir el correu i contrasenya introduïts
//            String correuUsuari = txtUsuari.getText().toString();
//            String contrasenyaUsuari = txtContrasenya.getText().toString();
//
//            // Comprovar si existeix l'usuari
//            Usuari usuariExistent = usuaris.stream()
//                    .filter(usuari -> usuari.getCorreu().equals(correuUsuari))
//                    .findFirst()
//                    .orElse(null);
//
//            if (usuariExistent != null) {
//                // Comprova si és correcte la contrasenya
//                if (usuariExistent.getContrasenya().equals(contrasenyaUsuari)) {
//                    // Iniciar sessió
//                    Intent intent = new Intent(this, HomeActivity.class);
//                    startActivity(intent);
//                } else {
//                    // Toast de contrasenya incorrecte
//                    Toast.makeText(this, "Contrasenya incorrecte.", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                // Toast de no existeix l'usuari
//                Toast.makeText(this, "Aquest usuari no existeix.", Toast.LENGTH_SHORT).show();
//            }



//            Intent intent = new Intent(this, HomeActivity.class);
//            startActivity(intent);
        });
    }

//    private void cargarDatos() {
//
//        ApiService service = APIUsuari.getUsuari().create(ApiService.class);
//
//        Call<ResponseBody> call = service.getData("eyJhbGciOiJIUzI1NiJ9.eyJyb2wiOiJBRE1JTiIsInN1YiI6IjZkYTlkNDkyLTI3OGMtMTFmMS1hMmE0LTcyZWFiYWUzNTI3MCIsImlhdCI6MTc3NDM2NTYzMSwiZXhwIjoxOTc3NDM2NTYzMX0.iGgCPYvGGgig2FAy8uFvarD6N1GvC5Bq21oR8Emheo8");
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    try {
//                        String data = response.body().string();
//                        Log.d("DATA", data);
//                    } catch (Exception e) {
//                        Log.e("ERROR", e.getMessage());
//                    }
//                } else {
//                    Log.e("ERROR", "Código: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("ERROR", t.getMessage());
//            }
//        });
//    }

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
                        TokenResponse token = gson.fromJson(ResponseJSON, TokenResponse.class);

                        tokenNou = token.getToken();

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

}