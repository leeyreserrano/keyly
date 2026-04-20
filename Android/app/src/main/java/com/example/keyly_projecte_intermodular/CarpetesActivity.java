package com.example.keyly_projecte_intermodular;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.resources.CarpetaAdapter;
import com.example.keyly_projecte_intermodular.resources.ItemAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarpetesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutError;
    private CarpetaAdapter carpetesAdapter;
    private EditText etCercar;
    private ImageView btnFiltres;
    private BottomNavigationView menu;
    private boolean filtrat = false;
    private ArrayList<Carpeta> carpetes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carpetes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        menu = findViewById(R.id.menu_app);
        menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_items_folders) {
                Intent intent = new Intent(this, ItemFolderSelectorActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_shared) {
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.recyclerCarpetes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CarpetaDTO.RequestCarpeta resquestCarpeta = CarpetaDTO.obtenirJSONICarpeta().create(CarpetaDTO.RequestCarpeta.class);
        resquestCarpeta.getAllCarpetes().enqueue(new Callback<ArrayList<Carpeta>>() {

            @Override
            public void onResponse(Call<ArrayList<Carpeta>> call, Response<ArrayList<Carpeta>> response) {
                carpetes.clear();
                carpetes.addAll(response.body());
                carpetesAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(RecyclerView.VISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<Carpeta>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
            }
        });

        actulitzarCarpetes(carpetes);

        etCercar = findViewById(R.id.et_search);
        etCercar.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar carpetes
                String nomCarpeta = s.toString();
                resultatsCerca(nomCarpeta);
            }
        });
    }

    private void resultatsCerca(String nomCarpeta) {
        ArrayList<Carpeta> llistaFiltradaCarpetes = new ArrayList<>();

        for (Carpeta carpeta : carpetes) {
            // Comporovar si coincideix algún nom amb la el títol del ítem
            if (carpeta.getNom().toLowerCase().contains(nomCarpeta.toLowerCase())) {
                llistaFiltradaCarpetes.add(carpeta);
            }
        }

        carpetesAdapter = new CarpetaAdapter(llistaFiltradaCarpetes, carpeta -> {
            Intent intent = new Intent(this, CarpetaActivity.class);
            intent.putExtra("nom", carpeta.getNom());
            intent.putExtra("items", new ArrayList<>(carpeta.getItems()));
            intent.putExtra("data_creacio", carpeta.getDataCreacio());
            Log.d("DATA_CREACIO", carpeta.getDataCreacio());
            startActivity(intent);
        });
        recyclerView.setAdapter(carpetesAdapter);
    }

    private void actulitzarCarpetes(ArrayList<Carpeta> carpetes) {
        carpetesAdapter = new CarpetaAdapter(carpetes, carpeta -> {
            Intent intent = new Intent(this, CarpetaActivity.class);
            intent.putExtra("nom", carpeta.getNom());
            intent.putExtra("items", new ArrayList<>(carpeta.getItems()));
            intent.putExtra("data_creacio", carpeta.getDataCreacio());
            Log.d("DATA_CREACIO", carpeta.getDataCreacio());
            startActivity(intent);
        });
        recyclerView.setAdapter(carpetesAdapter);
    }
}