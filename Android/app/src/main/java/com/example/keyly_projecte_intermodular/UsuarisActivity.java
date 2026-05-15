package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.resources.Varis.usuariPropi;
import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.adapters.UsuariAdpater;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarisActivity extends AppCompatActivity {

    private RecyclerView recyclerUsuaris;
    private LinearLayout layoutError;
    private UsuariAdpater usuariAdpater;
    private EditText etCercar;
    private ImageButton imgBtnLogOut, imgBtnBack, imgBtnAfegirUsuari;
    private ArrayList<Usuari> usuaris = new ArrayList<>(), totalUsuaris = new ArrayList<>(),
            usuarisSeleccionats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usuaris);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgBtnLogOut = findViewById(R.id.imgBtnLogOut);
        imgBtnLogOut.setOnClickListener(v -> {
            logOut(this);
        });

        recyclerUsuaris = findViewById(R.id.recyclerUsuaris);
        recyclerUsuaris.setLayoutManager(new LinearLayoutManager(this));

        carregarUsuaris();

        etCercar = findViewById(R.id.aCTVCercarUsuaris);
        etCercar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar usuaris
                String nomUsuari = s.toString();
                resultatsCerca(nomUsuari);
            }
        });

        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(v -> {
            finish();
        });

        imgBtnAfegirUsuari = findViewById(R.id.imgBtnAfegirUsuari);
        imgBtnAfegirUsuari.setOnClickListener(v -> {
            // TODO acceder como edición para añadir usuario
            Log.d("DEBUG", "Botó afegir usuari clicat"); // ← añade esto
            Intent intent = new Intent(this, PerfilActivity.class);
            intent.putExtra("usuariPropi", false);
            intent.putExtra("esCreant", true);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarUsuaris();
    }

    private void carregarUsuaris() {
        UsuariDTO.RequestUsuari requestUsuari = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class);
        requestUsuari.getAllUsuarisAdmin().enqueue(new Callback<ArrayList<Usuari>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuari>> call, Response<ArrayList<Usuari>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuaris.clear();
                    usuaris.addAll(response.body());
                    // Per no mostrar el usuari propi
                    for (Usuari usuari : usuaris) {
                        if (usuariPropi.getUuid().equals(usuari.getUuid())) {
                            usuaris.remove(usuari);
                            break;
                        }
                    }
                    actulitzarUsuaris(usuaris);
                    recyclerUsuaris.setVisibility(View.VISIBLE);
                } else {
                    // TODO mostrar error con layout
                    recyclerUsuaris.setVisibility(View.GONE);
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuari>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void resultatsCerca(String titolUsuari) {
        ArrayList<Usuari> llistaFiltradaUsuari = new ArrayList<>();

        for (Usuari usuari : usuaris) {
            // Comporovar si coincideix algún nom amb el nom d'algún usuari
            if (usuari.getNom().toLowerCase().contains(titolUsuari.toLowerCase())) {
                llistaFiltradaUsuari.add(usuari);
            }
        }

        actulitzarUsuaris(llistaFiltradaUsuari);
    }

    private void actulitzarUsuaris(ArrayList<Usuari> usuaris) {
        usuariAdpater = new UsuariAdpater(usuaris, usuari -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            // TODO acceder como admin view
            intent.putExtra("usuariPropi", false);
            intent.putExtra("esCreant", false);
            intent.putExtra("usuari", usuari);
            startActivity(intent);
        }, UsuarisActivity.this);
        recyclerUsuaris.setAdapter(usuariAdpater);
    }
}